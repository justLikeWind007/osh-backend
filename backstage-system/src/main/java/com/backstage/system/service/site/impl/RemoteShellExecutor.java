package com.backstage.system.service.site.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

/**
 * SSH remote shell executor
 * Uses JSch to connect to remote servers and execute shell commands
 */
public class RemoteShellExecutor {

    private static final Logger log = LoggerFactory.getLogger(RemoteShellExecutor.class);

    /** SSH login method */
    public enum AuthMethod {
        PASSWORD,
        PRIVATE_KEY
    }

    /**
     * Execute a command on a remote server via SSH (password auth, backward compatible).
     */
    public static String execute(String host, int port, String user,
                                 String password, String command,
                                 int timeoutSeconds) throws JSchException, IOException {
        return execute(host, port, user, AuthMethod.PASSWORD, password, null, command, timeoutSeconds);
    }

    /**
     * Execute a command on a remote server via SSH with configurable auth method.
     * @param authMethod   PASSWORD or PRIVATE_KEY
     * @param credential   password string (PASSWORD) or private key PEM text (PRIVATE_KEY)
     * @param passphrase   passphrase for encrypted private key (nullable)
     */
    public static String execute(String host, int port, String user,
                                 AuthMethod authMethod, String credential,
                                 String passphrase, String command,
                                 int timeoutSeconds) throws JSchException, IOException {

        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Host must not be empty");
        }
        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("User must not be empty");
        }
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command must not be empty");
        }

        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            session = jsch.getSession(user, host, port);

            if (authMethod == AuthMethod.PRIVATE_KEY) {
                if (credential == null || credential.isEmpty()) {
                    throw new IllegalArgumentException("Private key must not be empty when using PRIVATE_KEY auth");
                }
                byte[] passphraseBytes = (passphrase != null && !passphrase.isEmpty())
                        ? passphrase.getBytes(StandardCharsets.UTF_8) : null;
                jsch.addIdentity("demo-key", credential.getBytes(StandardCharsets.UTF_8), null, passphraseBytes);
            } else {
                session.setPassword(credential != null ? credential : "");
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.setConfig(config);

            session.connect(timeoutSeconds * 1000);

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect(timeoutSeconds * 1000);

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            while (!channel.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            int exitStatus = channel.getExitStatus();
            if (exitStatus != 0) {
                log.warn("Remote command exited with status {}: {}", exitStatus, command);
            }

            return output.toString().trim();

        } finally {
            JschUtils.disconnect(channel);
            JschUtils.disconnect(session);
        }
    }

    /**
     * Execute with default timeout (30 seconds)
     */
    public static String execute(String host, int port, String user,
                                 String password, String command)
            throws JSchException, IOException {
        return execute(host, port, user, password, command, 30);
    }

    /**
     * Execute a script on the remote server.
     * Writes the script content to a temp file, makes it executable,
     * runs it with bash, captures exit code and output, then cleans up.
     *
     * @param host           remote server hostname or IP
     * @param port           SSH port
     * @param user           SSH username
     * @param password       SSH password
     * @param scriptContent  the full script content (shell or with shebang for python)
     * @param timeoutSeconds timeout in seconds
     * @return ScriptResult with exit code and output
     */
    public static ScriptResult executeScript(String host, int port, String user,
                                             String password, String scriptContent,
                                             int timeoutSeconds) throws JSchException, IOException {
        String encoded = Base64.getEncoder().encodeToString(
                scriptContent.getBytes(StandardCharsets.UTF_8));
        String scriptPath = "/tmp/demo_script_" + System.currentTimeMillis() + ".sh";

        // Write script, make executable, run it, capture exit code, clean up
        String command = "echo " + shellEscape(encoded)
                         + " | base64 -d > " + scriptPath
                         + " && chmod +x " + scriptPath
                         + " && bash " + scriptPath + " 2>&1; RC=$?; rm -f " + scriptPath + "; exit $RC";

        String output = execute(host, port, user, password, command, timeoutSeconds);

        // The exit code from execute() is unreliable; we append "; echo EXIT:$?" approach
        // But simpler: re-execute with a wrapper
        return executeScriptWithExitCode(host, port, user, password, scriptContent, timeoutSeconds);
    }

    /**
     * Execute a script on the remote server with configurable auth method.
     */
    public static ScriptResult executeScript(String host, int port, String user,
                                             AuthMethod authMethod, String credential,
                                             String scriptContent, int timeoutSeconds)
            throws JSchException, IOException {
        return executeScriptWithAuth(host, port, user, authMethod, credential, scriptContent, timeoutSeconds);
    }

    private static ScriptResult executeScriptWithAuth(String host, int port, String user,
                                                      AuthMethod authMethod, String credential,
                                                      String scriptContent, int timeoutSeconds)
            throws JSchException, IOException {
        String encoded = Base64.getEncoder().encodeToString(
                scriptContent.getBytes(StandardCharsets.UTF_8));
        String scriptPath = "/tmp/demo_script_" + System.currentTimeMillis() + ".sh";

        String command = "echo " + shellEscape(encoded)
                         + " | base64 -d > " + scriptPath
                         + " && chmod +x " + scriptPath
                         + " && bash " + scriptPath + " 2>&1"
                         + "; echo ___EXIT___$?___"
                         + "; rm -f " + scriptPath;

        String rawOutput = execute(host, port, user, authMethod, credential, null, command, timeoutSeconds);

        int exitCode = 1;
        String output;
        String marker = "___EXIT___";
        int markerIdx = rawOutput.lastIndexOf(marker);
        if (markerIdx >= 0) {
            output = rawOutput.substring(0, markerIdx).trim();
            String afterMarker = rawOutput.substring(markerIdx + marker.length());
            int endMarkerIdx = afterMarker.indexOf("___");
            if (endMarkerIdx >= 0) {
                try {
                    exitCode = Integer.parseInt(afterMarker.substring(0, endMarkerIdx).trim());
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse exit code from: {}", afterMarker);
                }
            }
        } else {
            output = rawOutput;
        }

        return new ScriptResult(exitCode, output);
    }

    private static ScriptResult executeScriptWithExitCode(String host, int port, String user,
                                                          String password, String scriptContent,
                                                          int timeoutSeconds) throws JSchException, IOException {
        String encoded = Base64.getEncoder().encodeToString(
                scriptContent.getBytes(StandardCharsets.UTF_8));
        String scriptPath = "/tmp/demo_script_" + System.currentTimeMillis() + ".sh";

        String command = "echo " + shellEscape(encoded)
                         + " | base64 -d > " + scriptPath
                         + " && chmod +x " + scriptPath
                         + " && bash " + scriptPath + " 2>&1"
                         + "; echo ___EXIT___$?___"
                         + "; rm -f " + scriptPath;

        String rawOutput = execute(host, port, user, password, command, timeoutSeconds);

        // Parse exit code from the marker
        int exitCode = 1;
        String output;
        String marker = "___EXIT___";
        int markerIdx = rawOutput.lastIndexOf(marker);
        if (markerIdx >= 0) {
            output = rawOutput.substring(0, markerIdx).trim();
            String afterMarker = rawOutput.substring(markerIdx + marker.length());
            int endMarkerIdx = afterMarker.indexOf("___");
            if (endMarkerIdx >= 0) {
                try {
                    exitCode = Integer.parseInt(afterMarker.substring(0, endMarkerIdx).trim());
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse exit code from: {}", afterMarker);
                }
            }
        } else {
            output = rawOutput;
        }

        return new ScriptResult(exitCode, output);
    }

    /**
     * Execute a script in the background (nohup) and return immediately.
     * The script continues running after the SSH session disconnects.
     *
     * @return the PID of the background process, or empty string if failed
     */
    public static String executeScriptBackground(String host, int port, String user,
                                                 String password, String scriptContent)
            throws JSchException, IOException {
        return executeScriptBackground(host, port, user, AuthMethod.PASSWORD, password, scriptContent);
    }

    /**
     * Execute a script in the background with configurable auth method.
     */
    public static String executeScriptBackground(String host, int port, String user,
                                                 AuthMethod authMethod, String credential,
                                                 String scriptContent)
            throws JSchException, IOException {
        String encoded = Base64.getEncoder().encodeToString(
                scriptContent.getBytes(StandardCharsets.UTF_8));
        String scriptPath = "/tmp/demo_startup_" + System.currentTimeMillis() + ".sh";
        String logPath = scriptPath + ".log";

        // Use disown to detach from shell job table so SSH session close won't kill it.
        // < /dev/null prevents the nohup process from blocking on stdin.
        String command = "echo " + shellEscape(encoded)
                         + " | base64 -d > " + scriptPath
                         + " && chmod +x " + scriptPath
                         + " && nohup bash " + scriptPath + " < /dev/null > " + logPath + " 2>&1 & echo $!; disown";

        String output = execute(host, port, user, authMethod, credential, null, command, 30);
        return output.trim();
    }

    /**
     * Simple shell escaping for single-quoted strings.
     * Replaces single quotes with '\'' sequence.
     */
    private static String shellEscape(String value) {
        // Use single quotes, escape any embedded single quotes
        return "'" + value.replace("'", "'\\''") + "'";
    }
}

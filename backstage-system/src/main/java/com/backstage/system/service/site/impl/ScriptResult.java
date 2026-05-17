package com.backstage.system.service.site.impl;

/**
 * Result of a script execution including exit code
 */
public class ScriptResult {
    private final int exitCode;
    private final String output;

    public ScriptResult(int exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }

    public boolean isSuccess() {
        return exitCode == 0;
    }
}
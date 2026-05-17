package com.backstage.system.service.site.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class JschUtils {

    public static void disconnect(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public static void disconnect(ChannelExec channel) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
    }
}

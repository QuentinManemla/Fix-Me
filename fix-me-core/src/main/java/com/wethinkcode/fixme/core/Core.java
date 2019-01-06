package com.wethinkcode.fixme.core;

public class Core {
    public static String getServerType(String _server) {
        String port = _server.split(":")[1];
        port = port.substring(0, port.length() - 1);

        if (port.equals("5000"))
            return "Broker";
        else
            return "Market";
    }
}

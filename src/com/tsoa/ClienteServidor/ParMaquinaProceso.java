package com.tsoa.ClienteServidor;

public interface ParMaquinaProceso {
    int id = 0;
    String ip = "";

    static int getId() {
        return id;
    }

    static String getIp() {
        return ip;
    }
}

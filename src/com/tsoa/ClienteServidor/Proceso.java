package com.tsoa.ClienteServidor;

public class Proceso extends Thread {
    private int ID;
    private int PUERTO;
    private int status;
    private int delay = 1500;

    public Proceso(int id, int PUERTO, int status) {
        this.ID = id;
        this.PUERTO = PUERTO;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public int getPUERTO() {
        return PUERTO;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDelay() {
        return delay;
    }
}

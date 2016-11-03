package com.tsoa.ClienteServidor;

import com.tsoa.Visual.VentanaServidor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Proceso implements ActionListener, Runnable {
    private JTextField txtId;
    private JTextArea txtaEventos;
    private JButton btnCerrar;

    private double op1 = 0, op2 = 0, resultadoCODOP;

    byte[] mensaje = new  byte[1024];

    private JFrame ventanaPadre;

    public Servidor(JTextField txtId, JTextArea txtaEventos, JButton btnCerrar, int ID, int puerto, JFrame ventanaPadre, int status) {
        super(ID, puerto, status);
        this.txtId = txtId;
        this.txtId.setText(String.valueOf(this.getID()));
        this.txtaEventos = txtaEventos;
        this.btnCerrar = btnCerrar;
        this.btnCerrar.addActionListener(this);
        this.ventanaPadre = ventanaPadre;

        imprimirMensaje("Inicio de Proceso.");
    }

    private void imprimirMensaje(String string) {
        txtaEventos.append(string + "\n");
        txtaEventos.setCaretPosition(txtaEventos.getDocument().getLength());
    }

    @Override
    public void run() {
        int PUERTO_ACCESO = getPUERTO();

        ServerSocket servidor;
        Socket cliente;

        try
        {
            servidor = new ServerSocket(PUERTO_ACCESO);

            while (true) {
                cliente = servidor.accept();

                if(getStatus() == 1) {
                    MicroKernel.imprimirMensaje("Copiando el mensaje hacia el espacio del proceso.");
                    DataInputStream stream = new DataInputStream(cliente.getInputStream());

                    imprimirMensaje("Invocando Recieve().");
                    stream.read(mensaje);
                    setStatus(2);
                    sleep(getDelay());

                    imprimirMensaje("Procesando peticion recibida por el cliente: " + mensaje[0] + ".");

                    sleep(getDelay());

                    desempacar();

                    enviarRespuesta(empacarMensaje());

                    cliente.close();
                    setStatus(1);
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void desempacar() {
        byte CODOP = mensaje[4];

        String aux1 = "";
        String aux2 = "";

        boolean bandera = false;
        for(int i = 6; i < mensaje.length; i++) {
            if(mensaje[i] == 45){
                bandera = true;
                i++;
            }

            if (!bandera)
                aux1 += (char)mensaje[i];
            else
                aux2 += (char)mensaje[i];
        }

        op1 = Double.parseDouble(aux1);
        op2 = Double.parseDouble(aux2);

        imprimirMensaje("Datos de la operacion a realizar: \n" + identificarCODOP(CODOP) + "(" + op1 + ", " + op2 + ").");
        resultadoCODOP = realizarCODOP(CODOP, op1, op2);
    }

    private double realizarCODOP(byte COCOP, double operando1, double operando2) {
        double resultado = 0.0;

        switch (COCOP) {
            case 1:
                resultado = operando1 + operando2;
                break;
            case 2:
                resultado = operando1 - operando2;
                break;
            case 3:
                resultado = operando1 * operando2;
                break;
            case 4:
                resultado = operando1 / operando2;
                break;
            case 5:
                resultado = operando1 % operando2;
                break;
        }

        return resultado;
    }

    private String identificarCODOP(byte CODOP) {
        String operacion = "NULL";

        switch (CODOP) {
            case 1:
                operacion = "Suma";
                break;
            case 2:
                operacion = "Resta";
                break;
            case 3:
                operacion = "Multiplicacion";
                break;
            case 4:
                operacion = "Division";
                break;
            case 5:
                operacion = "Modulo";
                break;
        }

        return operacion;
    }

    private byte[] empacarMensaje() {
        String respuesta = String.valueOf(resultadoCODOP);

        byte[] paquete = new byte[1024];

        paquete[0] = (byte) this.getID();
        paquete[2] = mensaje[0];

        for (int i = 0; i < respuesta.length(); i++) {
            paquete[i + 4] = (byte) respuesta.charAt(i);
        }

        return paquete;
    }

    private void enviarRespuesta(byte respuesta[]) throws InterruptedException {
        sleep(getDelay());

        imprimirMensaje("Generando mensaje a ser enviado, llenando los campos necesarios.");

        sleep(getDelay());

        imprimirMensaje("Señalamiento al nucleo para envio del mensaje.");
        MicroKernel.send(respuesta[2], respuesta);
    }

    public void suspender() throws InterruptedException {
        ventanaPadre.setVisible(false);
        this.setStatus(0);
    }

    public void finalizar() {
        try {
            ventanaPadre.setVisible(false);
            ventanaPadre.dispose();
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void restaurar() {
        ventanaPadre.setVisible(true);
        this.setStatus(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCerrar) {
            try {
                MicroKernel.suspenderProceso(this.getID(), "servidor", this);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}

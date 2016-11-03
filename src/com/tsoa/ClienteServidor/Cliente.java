package com.tsoa.ClienteServidor;

import com.tsoa.Visual.VentanaCliente;
import com.tsoa.Visual.VentanaMicroNucleo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Cliente extends Proceso implements ActionListener, Runnable {
    private JButton btnSolicitar;
    private JComboBox comboBoxOpciones;
    private JTextArea txtaEventos;
    private JTextField txtOperando1;
    private JTextField txtOperando2;
    private JTextField txtId;

    private String op1, op2, operacion;

    private byte[] mensaje = new byte[1024];

    private JButton btnCerrar;
    private JFrame ventanaPadre;

    public Cliente(JButton btnSolicitar, JComboBox comboBoxOpciones, JTextArea txtaEventos, JTextField txtOperando1, JTextField txtOperando2, int id, int puerto, JTextField txtId, JButton btnCerrar, JFrame ventanaPadre, int status) {
        super(id, puerto, status);
        this.btnSolicitar = btnSolicitar;
        this.btnSolicitar.addActionListener(this);
        this.comboBoxOpciones = comboBoxOpciones;
        this.txtaEventos = txtaEventos;
        this.txtOperando1 = txtOperando1;
        this.txtOperando2 = txtOperando2;

        this.btnCerrar = btnCerrar;
        this.btnCerrar.addActionListener(this);

        this.ventanaPadre = ventanaPadre;

        this.txtId = txtId;
        txtId.setText(String.valueOf(this.getID()));

        imprimirMensaje("Inicio de Proceso.");
    }

    @Override
    public void run() {
        int PUERTO_ACCESO = getPUERTO();
        ServerSocket servidor;
        Socket cliente;

        byte[] respuestaServidor = new byte[1024];

        try {
            servidor = new ServerSocket(PUERTO_ACCESO);

            while (true) {
                cliente = servidor.accept();

                if(getStatus() == 1) {
                    MicroKernel.imprimirMensaje("Copiando el mensaje hacia el espacio del proceso.");
                    DataInputStream stream = new DataInputStream(cliente.getInputStream());

                    imprimirMensaje("Invocando Recieve().");
                    stream.read(respuestaServidor);

                    sleep(getDelay());

                    desempacarRespuesta(respuestaServidor);

                    cliente.close();

                    btnSolicitar.setEnabled(true);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void desempacarRespuesta(byte respuesta[]) throws InterruptedException {
        String resultado = "";

        for (int i = 4; i < respuesta.length; i++) {
            resultado += (char)respuesta[i];
        }

        sleep(getDelay());
        imprimirMensaje("Procesando respuesta recibida del\nServidor: " + respuesta[0] + ".");
        sleep(getDelay());
        imprimirMensaje("Resultado de la operacion solicitada: " + resultado + ".");
    }

    private void imprimirMensaje(String string) {
        txtaEventos.append(string + "\n");
        txtaEventos.setCaretPosition(txtaEventos.getDocument().getLength());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSolicitar) {
            if(txtOperando1.getText().length() == 0 || txtOperando2.getText().length() == 0 || comboBoxOpciones.getSelectedIndex() == 0)
                JOptionPane.showMessageDialog(null, "Llena los campos!");
            else {
                establecerCODOP();
                establecerOperandos();
                try {
                    enviarMensaje();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource() == btnCerrar) {
            try {
                MicroKernel.suspenderProceso(this.getID(), "cliente", this);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void establecerCODOP() {
        switch (comboBoxOpciones.getSelectedIndex()) {
            case 1:
                this.operacion = "1";
                break;
            case 2:
                this.operacion = "2";
                break;
            case 3:
                this.operacion = "3";
                break;
            case 4:
                this.operacion = "4";
                break;
            case 5:
                this.operacion = "5";
                break;
        }
    }

    private void establecerOperandos() {
        this.op1 = txtOperando1.getText();
        this.op2 = txtOperando2.getText();
    }

    private void enviarMensaje() throws InterruptedException {
        empaquetarMensaje();

        imprimirMensaje("Generando mensaje a ser enviado,\nllenando los campos necesarios.");
        imprimirMensaje("Señalamiento al nucleo para envio\ndel mensaje.");

        btnSolicitar.setEnabled(false);

        if(MicroKernel.obtenerProcesoDestino() != -1)
            MicroKernel.send(MicroKernel.obtenerProcesoDestino(), mensaje);
        else {
            btnSolicitar.setEnabled(true);
            imprimirMensaje("Servidores Ocupados o Inexistentes...");
        }
    }

    private void empaquetarMensaje() {
        mensaje[0] = (byte) this.getID();

        mensaje[2] = (byte)MicroKernel.obtenerProcesoDestino();

        mensaje[4] = (byte)comboBoxOpciones.getSelectedIndex();

        for (int i = 0; i < op1.length(); i++)
            mensaje[i + 6] = (byte)op1.charAt(i);

        mensaje[6 + op1.length()] = (byte)'-';

        for (int i = 0; i < op2.length(); i++)
            mensaje[i + 7 + op1.length()] = (byte)op2.charAt(i);
    }

    public void suspender(){
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
}

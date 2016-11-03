package com.tsoa.ClienteServidor;

import com.tsoa.Visual.VentanaCliente;
import com.tsoa.Visual.VentanaServidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class MicroKernel implements ActionListener, Runnable {
    private int contadorProcesos = 0;

    private static JTextField txtMaquinaDestino;
    private static JTextField txtProceso;

    private static JTextArea txtaEventos;

    private JButton btnCliente;
    private JButton btnServidor;

    private static Hashtable listaClientes;
    private static Hashtable listaServidores;

    private VentanaCliente ventanaCliente;
    private VentanaServidor ventanaServidor;

    private static JTable tablaAdministradora;
    private JButton btnEliminar;
    private JButton btnRestaurar;

    public MicroKernel(JTextArea txtaEventos, JButton btnCliente, JButton btnServidor, JButton btnEliminar, JTextField txtMaquinaDestino, JTextField txtProceso, JTable tablaAdministradora, JButton btnRestaurar) {
        MicroKernel.txtaEventos = txtaEventos;
        this.btnCliente = btnCliente;
        this.btnCliente.addActionListener(this);
        this.btnServidor = btnServidor;
        this.btnServidor.addActionListener(this);
        this.btnEliminar = btnEliminar;
        this.btnEliminar.addActionListener(this);
        this.btnRestaurar = btnRestaurar;
        this.btnRestaurar.addActionListener(this);

        MicroKernel.txtMaquinaDestino = txtMaquinaDestino;
        MicroKernel.txtProceso = txtProceso;
        this.txtProceso.setVisible(false);

        listaClientes = new Hashtable<>();
        listaServidores = new Hashtable<>();

        MicroKernel.tablaAdministradora = tablaAdministradora;
    }

    @Override
    public void run() {}

    public static void send(int procesoDestino, byte[] mensaje) throws InterruptedException {
        imprimirMensaje("Recibido mensaje proveniente de la red");
        Thread.sleep(1500);

        int PUERTO_OBJETIVO;

        String host = MicroKernel.obtenerDireccionIP();

        imprimirMensaje("Buscando proceso correspondiente al campo dest\ndel mensaje recibido.");
        imprimirMensaje("Buscando en listas locales el par (" + host + ", " + procesoDestino +  ").");
        Thread.sleep(1500);

        if (listaClientes.get(procesoDestino) != null)
            PUERTO_OBJETIVO = ((VentanaCliente)listaClientes.get(procesoDestino)).getPUERTO();
        else if (listaServidores.get(procesoDestino) != null) {
            imprimirMensaje("Enviando mensaje de búsqueda del servido.r");
            Thread.sleep(1500);
            imprimirMensaje("Recibido mensaje que contiene la ubicación\n(" + host + ", " + procesoDestino +  ") del servidor.");
            PUERTO_OBJETIVO = ((VentanaServidor) listaServidores.get(procesoDestino)).getPUERTO();
        }
        else {
            imprimirMensaje("Proceso destinatario no encontrado según campo dest del mensaje recibido.");
            PUERTO_OBJETIVO = -1;
        }

        imprimirMensaje("Completando campos de encabezado del mensaje\na ser enviado.");
        Thread.sleep(1500);
        imprimirMensaje("Enviando mensaje por la red.");

        try {
            Socket cliente = new Socket(host, PUERTO_OBJETIVO);
            DataOutputStream dataOutputStream = new DataOutputStream(cliente.getOutputStream());
            dataOutputStream.write(mensaje);
            cliente.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnCliente) {
            if(listaClientes.size() < 16) {

                ventanaCliente = new VentanaCliente(contadorProcesos, setPuertoEntrada());

                listaClientes.put(contadorProcesos, ventanaCliente);

                registrarProceso(contadorProcesos, "Cliente");
                imprimirMensaje("Inicio de proceso Cliente: " + contadorProcesos);
                contadorProcesos++;


            } else {
                JOptionPane.showMessageDialog(null, "Limite de Clientes");
            }
        }
        else if (e.getSource() == btnServidor) {
            if(listaServidores.size() < 16) {

                ventanaServidor = new VentanaServidor(contadorProcesos, setPuertoEntrada());

                listaServidores.put(contadorProcesos, ventanaServidor);

                registrarProceso(contadorProcesos, "Servidor");
                imprimirMensaje("Inicio de proceso Servidor: " + contadorProcesos);
                contadorProcesos++;

            } else {
                JOptionPane.showMessageDialog(null, "Limite de Servidores");
            }
        } else if (e.getSource() == btnEliminar) {
            eliminarProceso();
        } else if (e.getSource() == btnRestaurar) {
            restaurarProceso();
        }
    }

    private static String[] buscarProceso() {
        int indice = tablaAdministradora.getSelectedRow();

        DefaultTableModel model = (DefaultTableModel) tablaAdministradora.getModel();
        int id = (int)model.getValueAt(indice, 0);

        String tipo = "";

        Enumeration<Integer> enumeracionClientes = listaClientes.keys();
        Enumeration<Integer> enumeracionServidores = listaServidores.keys();

        while (enumeracionClientes.hasMoreElements()) {

            if(id == enumeracionClientes.nextElement()) {
                tipo = "cliente";
            }
        }

        while (enumeracionServidores.hasMoreElements()) {
            if(id == enumeracionServidores.nextElement()) {
                tipo = "servidor";
            }
        }

        String resultado[] = {String.valueOf(id), tipo, String.valueOf(indice)};

        return resultado;
    }

    private static void eliminarProceso() {
        String resultado[] = buscarProceso();
        int id = Integer.parseInt(resultado[0]);
        String tipo = resultado[1];
        int indice = Integer.parseInt(resultado[2]);

        switch (tipo) {
            case "cliente":
                Cliente clienteObjetivo = ((VentanaCliente)MicroKernel.listaClientes.get(id)).cliente;
                MicroKernel.listaClientes.remove(id);
                clienteObjetivo.finalizar();
                ((DefaultTableModel) tablaAdministradora.getModel()).removeRow(indice);
                tablaAdministradora.addNotify();
                MicroKernel.imprimirMensaje("Finalizacion del proceso cliente: " + id);
                break;

            case "servidor":
                Servidor servidorObjetivo = ((VentanaServidor)MicroKernel.listaServidores.get(id)).servidor;
                MicroKernel.listaServidores.remove(id);
                servidorObjetivo.finalizar();
                ((DefaultTableModel) tablaAdministradora.getModel()).removeRow(indice);
                tablaAdministradora.addNotify();
                MicroKernel.imprimirMensaje("Finalizacion del proceso servidor: " + id);
                break;
            }
    }

    public static void suspenderProceso(int id, String tipo, Proceso procesoObjetivo) throws InterruptedException {
        /*DefaultTableModel model = (DefaultTableModel) tablaAdministradora.getModel();

        for (int i = 0; i < tablaAdministradora.getRowCount(); i++) {
            if (id == (int)model.getValueAt(i, 0)) {
                model.removeRow(i);
            }
        }

        tablaAdministradora.addNotify();*/

        switch (tipo) {
            case "cliente":
                ((Cliente) procesoObjetivo).suspender();
                imprimirMensaje("Suspension del proceso cliente: " + id);
                break;

            case "servidor":
                ((Servidor) procesoObjetivo).suspender();
                imprimirMensaje("Suspension del proceso servidor: " + id);
                break;
        }

    }

    private static void restaurarProceso() {
        String resultado[] = buscarProceso();
        int id = Integer.parseInt(resultado[0]);
        String tipo = resultado[1];

        switch (tipo) {
            case "cliente":
                Cliente clienteObjetivo = ((VentanaCliente)MicroKernel.listaClientes.get(id)).cliente;
                clienteObjetivo.restaurar();
                MicroKernel.imprimirMensaje("Restauracion del proceso cliente: " + id);
                break;

            case "servidor":
                Servidor servidorObjetivo = ((VentanaServidor)MicroKernel.listaServidores.get(id)).servidor;
                servidorObjetivo.restaurar();
                MicroKernel.imprimirMensaje("Restauracion del proceso servidor: " + id);
                break;
            }
    }

    public void registrarProceso(int id, String tipo) {
        DefaultTableModel modelo = (DefaultTableModel) tablaAdministradora.getModel();

        Object[] fila = new Object[2];

        fila[0] = id;
        fila[1] = tipo;

        modelo.addRow(fila);
        tablaAdministradora.setModel(modelo);
    }

    public int setPuertoEntrada() {
        return 8080 + contadorProcesos;
    }

    public static void imprimirMensaje(String string) {
        txtaEventos.append(string + "\n");
        txtaEventos.setCaretPosition(txtaEventos.getDocument().getLength());
    }

    public static String obtenerDireccionIP() {
        if (txtMaquinaDestino.getText().length() != 0)
            return txtMaquinaDestino.getText();
        else
            return "127.0.0.1";
    }

    public static int obtenerProcesoDestino() {
        int id = -1;

        Enumeration<Integer> enumeracionServidores = listaServidores.keys();

        while (enumeracionServidores.hasMoreElements()) {
            id = enumeracionServidores.nextElement();

            Servidor servidorObjetivo = ((VentanaServidor)MicroKernel.listaServidores.get(id)).servidor;

            if (servidorObjetivo.getStatus() == 1)
                break;
            else
                id = -1;
        }

        return id;
    }
}

package com.tsoa.Visual;

import com.tsoa.ClienteServidor.Servidor;

import javax.swing.*;

public class VentanaServidor extends JFrame {
    private JLabel lblId;
    private JTextField txtId;

    private JTextArea txtaEventos;
    private JScrollPane scrollEventos;

    private JButton btnCerrar;

    public Servidor servidor;
    private int PUERTO;

    public VentanaServidor(int ID, int PUERTO) {
        super("Servidor");

        this.PUERTO = PUERTO;

        setLayout(null);

        lblId = new JLabel("ID:");
        lblId.setBounds(250, 10, 50, 30);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(280, 10, 50, 30);
        add(txtId);
        txtId.setEditable(false);

        txtaEventos = new JTextArea();
        txtaEventos.setBounds(10, 50, 575, 180);
        add(txtaEventos);
        txtaEventos.setEditable(false);

        scrollEventos = new JScrollPane(txtaEventos);
        scrollEventos.setBounds(10, 50, 575, 180);
        add(scrollEventos);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(250, 240, 80, 30);
        add(btnCerrar);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 320);
        setResizable(false);
        setVisible(true);

        servidor = new Servidor(txtId, txtaEventos, btnCerrar, ID, PUERTO, this, 1);
        Thread hiloServidor = new Thread(servidor);
        hiloServidor.start();
    }

    public int getPUERTO() {
        return PUERTO;
    }
}

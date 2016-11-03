package com.tsoa.Visual;

import com.tsoa.ClienteServidor.Cliente;

import javax.swing.*;

public class VentanaCliente extends JFrame {
    private JLabel lblId;
    private JTextField txtId;

    private JTextArea txtaEventos;
    private JScrollPane scrollEventos;

    private JLabel lblOperacion;
    private JComboBox comboBoxOpciones;
    private String[] opciones = {"Seleccione Op...", "Suma", "Resta", "Multiplicacion", "Division", "Modulo"};

    private JLabel lblOperando1;
    private JTextField txtOperando1;

    private JLabel lblOperando2;
    private JTextField txtOperando2;

    private JButton btnSolicitar;
    private JButton btnCerrar;

    public Cliente cliente;

    private int PUERTO;

    public VentanaCliente(int ID, int PUERTO) {
        super("Cliente");

        this.PUERTO = PUERTO;

        setLayout(null);

        lblId = new JLabel("ID:");
        lblId.setBounds(150, 10, 50, 30);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(180, 10, 50, 30);
        add(txtId);
        txtId.setEditable(false);

        txtaEventos = new JTextArea();
        txtaEventos.setBounds(10, 50, 350, 210);
        add(txtaEventos);
        txtaEventos.setEditable(false);

        scrollEventos = new JScrollPane(txtaEventos);
        scrollEventos.setBounds(10, 50, 350, 210);
        add(scrollEventos);

        lblOperacion = new JLabel("Operacion:");
        lblOperacion.setBounds(380, 70, 100, 30);
        add(lblOperacion);

        comboBoxOpciones = new JComboBox(opciones);
        comboBoxOpciones.setBounds(380, 100, 160, 30);
        add(comboBoxOpciones);

        lblOperando1 = new JLabel("Op1:");
        lblOperando1.setBounds(380, 140, 50, 30);
        add(lblOperando1);

        txtOperando1 = new JTextField();
        txtOperando1.setBounds(420, 140, 120, 30);
        add(txtOperando1);

        lblOperando2 = new JLabel("Op2:");
        lblOperando2.setBounds(380, 180, 50, 30);
        add(lblOperando2);

        txtOperando2 = new JTextField();
        txtOperando2.setBounds(420, 180, 120, 30);
        add(txtOperando2);

        btnSolicitar = new JButton("Solicitar");
        btnSolicitar.setBounds(380, 230, 80, 30);
        add(btnSolicitar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(460, 230, 80, 30);
        add(btnCerrar);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(560, 300);
        setResizable(false);
        setVisible(true);

        cliente = new Cliente(btnSolicitar, comboBoxOpciones, txtaEventos, txtOperando1, txtOperando2, ID, PUERTO, txtId, btnCerrar, this, 1);
        Thread hiloCliente = new Thread(cliente);
        hiloCliente.start();
    }

    public int getPUERTO() {
        return PUERTO;
    }
}

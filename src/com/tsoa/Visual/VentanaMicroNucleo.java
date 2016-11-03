package com.tsoa.Visual;

import com.tsoa.ClienteServidor.MicroKernel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaMicroNucleo extends JFrame {
    private JLabel lblMaquinaDestino;
    private JLabel lblProceso;

    private static JTextField txtMaquinaDestino;
    private static JTextField txtProceso;

    private JTextArea txtaEventos;
    private JScrollPane scrollEventos;

    private JButton btnCliente;
    private JButton btnServidor;

    private MicroKernel microKernel;

    private JTable tablaAdministradora;
    private JScrollPane scrollTabla;
    private JButton btnEliminar;
    private JButton btnRestaurar;

    public VentanaMicroNucleo() {
        super("Practica 2: Cliente-Servidor");

        setLayout(null);

        tablaAdministradora = new JTable(new DefaultTableModel(new Object[][]{},
                new String[] {
                        "ID", "Tipo de Proceso"
                }));
        tablaAdministradora.setBounds(420, 50, 300, 300);
        add(tablaAdministradora);

        scrollTabla = new JScrollPane(tablaAdministradora);
        scrollTabla.setBounds(500, 50, 300, 300);
        add(scrollTabla);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(670, 370, 80, 30);
        add(btnEliminar);

        btnRestaurar = new JButton("Restaurar");
        btnRestaurar.setBounds(560, 370, 80, 30);
        add(btnRestaurar);

        lblMaquinaDestino = new JLabel("Maquina Destino:");
        lblMaquinaDestino.setBounds(10, 10, 200, 30);
        add(lblMaquinaDestino);

        txtMaquinaDestino = new JTextField();
        txtMaquinaDestino.setBounds(120, 10, 200, 30);
        add(txtMaquinaDestino);

        lblProceso = new JLabel("Proceso:");
        lblProceso.setBounds(250, 10, 200, 30);
        add(lblProceso);
        lblProceso.setVisible(false);

        txtProceso = new JTextField();
        txtProceso.setBounds(320, 10, 50, 30);
        add(txtProceso);

        txtaEventos = new JTextArea();
        txtaEventos.setBounds(25, 50, 440, 300);
        add(txtaEventos);
        txtaEventos.setEditable(false);

        scrollEventos = new JScrollPane(txtaEventos);
        scrollEventos.setBounds(25, 50, 440, 300);
        add(scrollEventos);

        btnCliente = new JButton("Cliente");
        btnCliente.setBounds(150, 370, 80, 30);
        add(btnCliente);

        btnServidor = new JButton("Servidor");
        btnServidor.setBounds(250, 370, 80, 30);
        add(btnServidor);

        microKernel = new MicroKernel(txtaEventos, btnCliente, btnServidor, btnEliminar, txtMaquinaDestino, txtProceso, tablaAdministradora, btnRestaurar);
        Thread hiloMicroKernel = new Thread(microKernel);
        hiloMicroKernel.start();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(840, 440);
        setResizable(false);
        setVisible(true);
    }
}

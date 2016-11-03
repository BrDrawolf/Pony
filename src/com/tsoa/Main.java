package com.tsoa;

import com.tsoa.Visual.VentanaMicroNucleo;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new VentanaMicroNucleo();
    }
}

package com.projectASA.GUI;

import javax.swing.*;

public class StartSwing {
    private JFrame window;

    public StartSwing(){
        window = new JFrame("Persons DB");
        window.setSize(1000,600);
        window.add((new MainForm()));
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        //window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

//    public static void startBuildSwing() {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new StartSwing();
//            }
//        });
//    }
}

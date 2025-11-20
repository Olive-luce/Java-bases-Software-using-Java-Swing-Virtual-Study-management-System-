package com.mycompany.vsp2;

import com.mycompany.vsp2.ui.LoginPage;

import javax.swing.SwingUtilities;

public class VSP2 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}

package com.eventmgmt.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;


public class UIUtils {

   
    public static void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        if (button == null) {
            return;
        }
        button.setUI(new BasicButtonUI());
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize > 0 ? fontSize : 13));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

       
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
    }

    
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, int fontSize) {
        JButton button = new JButton(text);
        styleButton(button, bgColor, fgColor, fontSize);
        return button;
    }
}

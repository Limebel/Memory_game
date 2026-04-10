package org.example.view;

import javax.swing.*;
import java.awt.*;

public class ColorManager {
    public static void darkTheme(){
        try {
            // light theme (testing)
            //UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());

            Color base = new Color(82, 255, 197);
            Color highlight = new Color(255, 82, 140);
            Color light = new Color(128, 128, 128);
            Color dark = new Color(50, 50, 50);

            UIManager.put("Label.foreground", base); // all text
            UIManager.put("TextField.foreground", highlight); // text in textbox
            UIManager.put("TextField.background", dark); // textbox
            UIManager.put("TextField.caretForeground", highlight); // typing

            // buttons
            UIManager.put("Button.background", base);
            UIManager.put("Button.foreground", dark);
            UIManager.put("Button.hoverBackground", highlight);

            UIManager.put("Slider.thumbColor", base); // slider pointer
            UIManager.put("Slider.trackColor", light); // slider line
            UIManager.put("Slider.trackWidth", 10);

            UIManager.put("Component.focusColor", highlight);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

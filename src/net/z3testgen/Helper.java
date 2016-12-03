package net.z3testgen;

import javax.swing.*;
import java.text.SimpleDateFormat;

/**
 * Created by lequanghiep on 11/25/2016.
 */
public class Helper {
    JTextArea textArea;

    public Helper(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void setStatus(String content) {
        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        String current_time_str = time_formatter.format(System.currentTimeMillis());
        textArea.setText(textArea.getText() + current_time_str + ": " + content + "\n");
    }
}

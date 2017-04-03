package net.z3testgen;

import net.z3testgen.xeger.Xeger;
import org.apache.commons.lang3.RandomStringUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by lequanghiep on 11/25/2016.
 */
public class Helper {
    JTextArea textArea;

    public Helper() {
    }

    public Helper(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void setStatus(String content) {
        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        String current_time_str = time_formatter.format(System.currentTimeMillis());
        textArea.setText(textArea.getText() + current_time_str + ": " + content + "\n");
    }

    public static List<String> getParam(String dir) throws FileNotFoundException {
        List<String> params = new ArrayList<>();
        Scanner in = new Scanner(new File(dir));

        while (in.hasNext()) { // iterates each line in the file
            String line = in.nextLine().trim();
            if (line.contains("function")) {
                line = line.substring(line.indexOf('(') + 1, line.length() - 1);
                String[] listParam = line.split(",");
                for (String param : listParam) {
                    params.add(param);
                }
            }
        }

        in.close();
        return params;
    }

    public List<String> generateStringData(Map<String, StringCondition> varString) {
        List<String> tempData = new ArrayList<>();
        for (Map.Entry<String, StringCondition> entry : varString.entrySet()) {
            StringCondition value = entry.getValue();
            if (value.getRegex() != "") {
                Xeger generator = new Xeger(value.getRegex());
                tempData.add(generator.generate());
            } else {
                tempData.add(RandomStringUtils.random(value.getLength(), value.isContainLetter(), value.isContainDigit()));
            }
        }

        return tempData;
    }
}

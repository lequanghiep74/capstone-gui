package net.z3testgen;

import org.apache.commons.lang3.RandomStringUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static List<String> getParam(String dir, Map<String, StringCondition> map) throws FileNotFoundException {
        List<String> params = new ArrayList<>();
        Scanner in = new Scanner(new File(dir));

        while (in.hasNext()) { // iterates each line in the file
            String line = in.nextLine().trim();
            if (line.contains("function")) {
                line = line.substring(line.indexOf('(') + 1, line.length() - 1);
                String[] listParam = line.split(",");
                for (String param : listParam) {
                    param = param.trim();
                    String[] temp = param.split(" ");
                    params.add(param);
                }
            }
        }

        in.close();
        return params;
    }

    public List<String> generateStringData(Map<String, StringCondition> varString) {
        List<String> tempData = new ArrayList<>();
//        for (Map.Entry<String, StringCondition> entry : varString.entrySet()) {
//            StringCondition value = entry.getValue();
//            if (value.getRegex() != "") {
//                Xeger generator = new Xeger(value.getRegex());
//                tempData.add(generator.generate());
//            } else {
//                tempData.add(RandomStringUtils.random(value.getLength(), value.isContainLetter(), value.isContainDigit()));
//            }
//        }

        return tempData;
    }

    public String generateStringDataByCondition(StringCondition stringCondition, int length, String testcase) {
        String tempData = "";
        String contain = stringCondition.getMapParamsContain().get(stringCondition.getMapTestCaseUseContain().get(testcase));
        if (contain != null) {
            tempData = genStringWithContain(length, contain, stringCondition);
        } else {
            tempData = RandomStringUtils.random(length, stringCondition.isContainLetter(), stringCondition.isContainDigit());
        }
        return tempData;
    }

    private String genStringWithContain(int length, String contain, StringCondition stringCondition) {
        String s = "";
        if (contain.length() > length) {
            s = RandomStringUtils.random(length, stringCondition.isContainLetter(), stringCondition.isContainDigit());
        } else {
            int lengthAnother = length - contain.length() + 1;
            int index = new Random().nextInt(lengthAnother);
            s = RandomStringUtils.random(index + 1, stringCondition.isContainLetter(), stringCondition.isContainDigit())
                    + contain
                    + RandomStringUtils.random(lengthAnother - index, stringCondition.isContainLetter(), stringCondition.isContainDigit());
        }
        return s;
    }

    public Map<String, StringCondition> getMapStringParam(List<String> dsl) {
        Map<String, StringCondition> mapStringParams = new HashMap<>();

        for (String code : dsl) {
            if ((code.contains(".length") || code.contains(".contain") || code.contains(".onlyDigit")) && code.contains("define")) {
                String stringContain = "";
                String param = code.substring(code.indexOf("{") + 1, code.indexOf("."));

                if (code.contains(".contain")) {
                    stringContain = getStringContain(code);
                }

                if (mapStringParams.get(param) == null) {
                    StringCondition stringCondition = new StringCondition();
                    stringCondition.setStringContain(stringContain);
                    mapStringParams.put(param, stringCondition);
                } else {
                    StringCondition stringCondition = mapStringParams.get(param);
                    stringCondition.setStringContain(stringContain);
                }
            }
        }

        return mapStringParams;
    }

    public String getStringContain(String code) {
        return code.substring(code.indexOf(".contain(\"") + 10, code.lastIndexOf("\")"));
    }
}

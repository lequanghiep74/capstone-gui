package net.z3testgen;

import net.z3testgen.xeger.Xeger;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenString {
    public static void main(String[] args) throws IOException {
        genString();
    }

    public static void genString() throws IOException {
        ReadWriteFile readWriteFile = new ReadWriteFile();
        List<String> listDslCode = readWriteFile.readFile("input/string.agt");
        List<String> params = getParam("input/string.agt");
        Map<String, StringCondition> varString = new HashMap<>();

        FileWriter writer = new FileWriter("output/string.csv");

        for (int i = 0; i < params.size(); i++) {
            String[] components = params.get(i).trim().split(" ");
            varString.put(components[1], new StringCondition());
            writer.append(components[1]);
            if (i != params.size() - 1) {
                writer.append(',');
            }
        }
        writer.append("\n");

        for (int i = 0; i < listDslCode.size(); i++) {
            String code = listDslCode.get(i);
            String[] tempCode = code.split(" ");
            if (code.contains("str-regex")) {
                varString.get(tempCode[1]).setRegex(tempCode[2]);
            } else if (code.contains("str-def")) {
                if (code.contains("length")) {
                    String num = tempCode[tempCode.length - 1];
                    varString.get(tempCode[1]).setLength(Integer.parseInt(num.substring(0, num.length() - 1)));
                } else if (code.contains("contain-number")) {
                    varString.get(tempCode[1]).setContainDigit(code.contains("true"));
                } else if (code.contains("contain-letter")) {
                    varString.get(tempCode[1]).setContainLetter(code.contains("true"));
                }
            }
        }

        for (int i = 0; i < 100; i++) {
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
            writer.append(String.join(",", tempData));
            writer.append("\n");
        }
        writer.flush();
        writer.close();

        System.out.print("Done");
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

    public static class StringCondition {
        private boolean containDigit = true;
        private boolean containLetter = true;
        private int length = 10;
        private String regex = "";

        public StringCondition() {
        }

        public boolean isContainDigit() {
            return containDigit;
        }

        public void setContainDigit(boolean containDigit) {
            this.containDigit = containDigit;
        }

        public boolean isContainLetter() {
            return containLetter;
        }

        public void setContainLetter(boolean containLetter) {
            this.containLetter = containLetter;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }
}

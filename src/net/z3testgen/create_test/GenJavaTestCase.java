package net.z3testgen.create_test;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by lequanghiep on 5/8/2017.
 */
public class GenJavaTestCase {
    public ArrayList<String> getAllLine(String fileName) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        InputStream is = null;
        String line = null;
        try {
            is = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception ex) {

        } finally {
            if (is != null) {
                is.close();
            }
        }
        return list;
    }

    public void writeLine(String fileName, String line) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        fw.write(line);
        fw.write(System.getProperty("line.separator"));
        fw.close();
    }

    public void clearFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        fw.close();
    }

    public void doing(String inputFile, String outputFile) {
        GenJavaTestCase mf = new GenJavaTestCase();
        try {

            ArrayList<String> list = mf.getAllLine(inputFile);
            mf.clearFile(outputFile);
            String line = "import cucumber.api.java.en.Given\r\n" + "import cucumber.api.java.en.Then;\r\n"
                    + "import cucumber.api.java.en.When;\r\n";
            mf.writeLine(outputFile, line);
            String className;
            for (String s : list) {
                // Feature: triangle_cucumber
                if (s.contains("Feature:")) {
                    String temp = s.replaceFirst("Feature:", "public class");
                    String res = temp.split(" ")[0] + " " + temp.split(" ")[1];
                    String temp1 = temp.split(" ")[2];
                    String temp2 = String.valueOf(temp1.charAt(0)).toUpperCase();
                    for (int i = 1; i < temp1.length(); i++) {
                        if (temp1.charAt(i - 1) == '_') {
                            temp2 += String.valueOf(temp1.charAt(i)).toUpperCase();
                        } else {
                            temp2 += String.valueOf(temp1.charAt(i));
                        }
                    }
                    res += " " + temp2 + " {";
                    mf.writeLine(outputFile, res);
                }
                // Scenario Outline: Feature test triangle_cucumber
                if (s.contains("Scenario Outline:")) {
                    continue;
                }
                // Given I have three number is <a|int>, <b|int> and <c|String>

                if (!s.contains("Feature:")) {
                    // @Given("^I have three number is (\\d+), (\\d+) and
                    // ([^\"]*)$"
                    String xx = s.split(" ")[0];
                    String temp = s.replace(xx, "@" + xx + "(\"^");
                    String res = temp;
                    ArrayList<Name> arr = new ArrayList<>();
                    ArrayList<String> as = new ArrayList<>();
                    String name, method;
                    for (int i = 0; i < temp.length(); i++) {
                        if (temp.charAt(i) == '<') {
                            name = temp.charAt(i) + "";
                            i += 1;
                            while (temp.charAt(i) != '>') {
                                name += temp.charAt(i) + "";
                                i++;
                            }
                            name += temp.charAt(i) + "";
                            as.add(name);
                        }

                    }

                    for (String string : as) {
                        if (string.contains("int")) {
                            res = res.replace(string, "(\\\\d+)");
                        } else {
                            res = res.replace(string, "([^\\\"]*)$");
                        }
                        String tmp = "";
                        for (int i = 1; i < string.length() - 1; i++) {
                            tmp += string.charAt(i) + "";
                        }
                        tmp = tmp.trim();
                        tmp = tmp.replace("|", " ");
                        name = tmp.split(" ")[0];
                        method = tmp.split(" ")[1];
                        Name n = new Name();
                        n.setName(name);
                        n.setMethod(method);
                        arr.add(n);
                    }
                    res += "\")";


                    res += "\r\n";
                    res += "public void " + xx.toLowerCase() + "Function(";
                    for (int i = 0; i < arr.size(); i++) {
                        Name n = arr.get(i);
                        if (i != arr.size() - 1) {
                            res += n.getMethod() + " ";
                            res += n.getName() + ", ";
                        } else {
                            res += n.getMethod() + " ";
                            res += n.getName() + "";
                        }

                    }
                    res += "){\r\n\r\n";
                    res += "}";
                    mf.writeLine(outputFile, res);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Finish Thanks please go to " + outputFile + " review result");
    }


    public static class Name {
        private String name;
        private String method;

        public Name() {
            super();
        }

        public Name(String name, String method) {
            super();
            this.name = name;
            this.method = method;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    public void copy() throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream("C:\\\\Users\\\\PC\\\\Desktop\\\\cucumber_example\\\\src\\\\test\\\\resources\\\\features\\\\triangle\\\\triangle_cucumber.feature");
            out = new FileOutputStream("triangle_cucumber.feature");
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            System.out.println("Done.");

        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}

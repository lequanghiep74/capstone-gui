package net.z3testgen;

import java.util.ArrayList;
import java.util.List;

public class TranslateDsl {
    public List<String> translateMydsl(List<String> listDslCode) {
        List<String> listZ3Code = new ArrayList<>();
        for (int i = 0; i < listDslCode.size(); i++) {
            String code = listDslCode.get(i);
            code = code.trim();
            String codeZ3 = "";
            if (code.contains("enum")) {
                //loai bo tat ca ky tu ngoac
                code = code.replaceAll("[*{*}]", "");
                String[] parts = code.split(" ");
                codeZ3 = "declare-datatypes () ((" + parts[1];
                for (int j = 2; j < parts.length; j++) {
                    codeZ3 += " " + parts[j];
                }
                codeZ3 += "))";
                codeZ3 = "(" + codeZ3 + ")";
            } else if (code.contains("define")) {
                codeZ3 = "define-fun ";
                codeZ3 += code.split(" ")[1];
                codeZ3 += " ()Bool ";
                codeZ3 += "(" + code.substring(code.indexOf("{") + 1, code.indexOf("}")) + ")";
                codeZ3 = "(" + codeZ3 + ")";
            } else if (code.contains("run")) {
                codeZ3 = "check-sat";
                codeZ3 = "(" + codeZ3 + ")";
            } else if (code.contains("function")) {
                String[] params = code.substring(code.indexOf("(") + 1, code.indexOf(")")).split(",");
                for (int j = 0; j < params.length; j++) {
                    String[] com = params[j].trim().split(" ");
                    codeZ3 += "(declare-const " + com[1] + " "
                            + com[0] + ")\n";
                }
                codeZ3 += "(declare-fun x ()" + code.split(" ")[1] + ")";
            } else if (code.contains("testcase")) {
                codeZ3 = "(assert (= x" + "\n";
                listZ3Code.add(codeZ3);
                i++;
                code = listDslCode.get(i).trim();
                int open = 0;
                while (code.charAt(0) != '}') {
                    if (code.lastIndexOf("\"") != code.length() - 1) {
                        codeZ3 = "(if (";
                        code = code.substring(code.indexOf("\"") + 1);
                        int index = code.indexOf("\"");
                        codeZ3 += code.substring(index + 1).trim() + ")\n";
                        codeZ3 += code.substring(0, index) + "\n";

                    }
                    else {
                        codeZ3 = code.replaceAll("\"", "") + "\n";
                    }
                    listZ3Code.add(codeZ3);
                    open++;
                    code = listDslCode.get(++i).trim();
                }
                codeZ3 = "";
                for (int j = 1; j < open; j++) {
                    codeZ3 += ")";
                }
                codeZ3 += "))";
                listZ3Code.add(codeZ3 + "\n");
                codeZ3 = "";
            }
            else if (code.contains("precondition")) {
                codeZ3 = "assert ";
                codeZ3 += "(" + code.substring(code.indexOf("{") + 1, code.indexOf("}")) + ")";
                codeZ3 = "(" + codeZ3 + ")";
            }
            if (codeZ3.length() > 0) {
                listZ3Code.add(codeZ3 + "\n");
            }
        }
        return listZ3Code;
    }
}

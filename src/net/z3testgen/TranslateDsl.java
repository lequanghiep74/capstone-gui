package net.z3testgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranslateDsl {
    public List<String> translateMydsl(List<String> listDslCode, Map<String, StringCondition> mapStringParams) {
        Itp itp = new Itp();
        List<String> listZ3Code = new ArrayList<>();
        for (int i = 0; i < listDslCode.size(); i++) {
            String code = listDslCode.get(i).replace("&&", "and").replace("||", "or");
            code = code.trim();
            String codeZ3 = "";
            if (code.contains("enum")) {
                //loai bo tat ca ky tu ngoac
                code = code.replaceAll("[*{*}]", "");
                String[] parts = code.split(" ");
                String type = code.split(" ")[1];
                if (!type.contains("Bool") && !type.contains("Int") && !type.contains("Real")) {
                    codeZ3 = "declare-datatypes () ((" + parts[1];
                    for (int j = 2; j < parts.length; j++) {
                        codeZ3 += " " + parts[j];
                    }
                    codeZ3 += "))";
                    codeZ3 = "(" + codeZ3 + ")";
                }
                codeZ3 += "\n(declare-fun result ()" + code.split(" ")[1] + ")";
            } else if (code.contains("define")) {
                if (!code.contains(".contain(\"") && !code.contains(".onlyDigit") && !code.contains(".onlyLetter")) {
                    if (code.contains(".length")) {
                        code = code.replace(".length", "");
                    }
                    codeZ3 = "define-fun ";
                    codeZ3 += code.split(" ")[1];
                    codeZ3 += " ()Bool ";
                    codeZ3 += itp.infixToPrefixConvert(code.substring(code.indexOf("{") + 1, code.indexOf("}")));
                    codeZ3 = "(" + codeZ3 + ")";
                } else {
                    String type = code.contains(".contain") ? ".contain" :
                            (code.contains(".onlyDigit") ? ".onlyDigit" : "onlyLetter");
                    int beginIndex = code.indexOf("{");
                    int endIndex = code.indexOf(type);
                    String params = code.substring(beginIndex + 1, endIndex);
                    StringCondition stringCondition = mapStringParams.get(params);
                    if (code.contains(".contain")) {
                        String data = code.substring(code.indexOf("\"") + 1, code.lastIndexOf("\""));
                        stringCondition.getMapParamsContain().put(code.split(" ")[1], data);
                    } else if (code.contains(".onlyDigit")) {
                        stringCondition.setContainDigit(code.contains("true"));
                        stringCondition.setContainLetter(!code.contains("true"));
                    } else if (code.contains(".onlyLetter")) {
                        stringCondition.setContainLetter(code.contains("true"));
                        stringCondition.setContainDigit(!code.contains("true"));
                    }
                }
            } else if (code.contains("run")) {
                codeZ3 = "check-sat";
                codeZ3 = "(" + codeZ3 + ")";
            } else if (code.contains("function")) {
                String[] params = code.substring(code.indexOf("(") + 1, code.indexOf(")")).split(",");
                for (int j = 0; j < params.length; j++) {
                    String[] com = params[j].trim().split(" ");
                    if (!com[0].equals("String")) {
                        codeZ3 += "(declare-const " + com[1] + " " + com[0] + ")\n";
                    } else {
                        StringCondition stringCondition = mapStringParams.get(com[1]);
                        if (stringCondition.isUseLength()) {
                            codeZ3 += "(declare-const " + com[1] + " Int)\n";
                        }
                    }
                }
            } else if (code.contains("testcase")) {
                codeZ3 = "(assert (= result";
                listZ3Code.add(codeZ3.replace("%", "mod"));
                i++;
                code = listDslCode.get(i).replace("&&", "and").replace("||", "or").trim();
                int open = 0;
                while (code.charAt(0) != '}') {
                    code = removeUnusedParamInCode(code, mapStringParams);
                    //get list length condition which test case used
                    List<String> paramsLengthUsed = new ArrayList<>();
                    for (Map.Entry<String, StringCondition> entry : mapStringParams.entrySet()) {
                        if (code.contains(entry.getKey().trim())) {
                            paramsLengthUsed.add(entry.getKey().trim());
                        }
                    }
                    if (code.lastIndexOf("\"") != code.length() - 1) {
                        codeZ3 = "(if ";
                        code = code.substring(code.indexOf("\"") + 1);
                        int index = code.indexOf("\"");
                        String subCode = code.substring(index + 1).trim();
                        String tempCode = itp.infixToPrefixConvert(subCode);
                        codeZ3 += parseNotSymbol(tempCode) + "\n";
                        String nameTestCase = code.substring(0, index);
                        for (String nameParam : paramsLengthUsed) {
                            mapStringParams.get(nameParam).addListTestCaseUseLength(nameTestCase);
                        }
                        codeZ3 += nameTestCase;

                    } else {
                        codeZ3 = code.replaceAll("\"", "");
                    }
                    listZ3Code.add(codeZ3.replace("%", "mod"));
                    open++;
                    code = listDslCode.get(++i).replace("&&", "and").replace("||", "or").trim();
                }
                codeZ3 = "";
                for (int j = 1; j < open; j++) {
                    codeZ3 += ")";
                }
                codeZ3 += "))";
                listZ3Code.add(parseNotSymbol(codeZ3).replace("%", "mod"));
                codeZ3 = "";
            } else if (code.contains("precondition") || code.contains("example")) {
                code = code.replace(".length", "");
                codeZ3 = "assert ";
                codeZ3 += itp.infixToPrefixConvert(code.substring(code.indexOf("{") + 1, code.indexOf("}")));
                codeZ3 = "(" + codeZ3 + ")";
            }
            if (codeZ3.length() > 0) {
                listZ3Code.add(parseNotSymbol(codeZ3).replace("%", "mod"));
            }
        }
        return listZ3Code;
    }

    public String parseNotSymbol(String s) {
        s = s.trim();
        if (s.contains("!")) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '!') {
                    int begin = i;
                    while (s.charAt(i) != ' ' && i != s.length() - 1) {
                        i++;
                    }
                    if (i == s.length() - 1) {
                        s = s.replace(s.substring(begin), "(not " + s.substring(begin + 1) + ")");
                    } else {
                        s = s.replace(s.substring(begin, i), "(not " + s.substring(begin + 1, i) + ")");
                    }
                }
            }
        }
        return s;
    }

    public String removeUnusedParamInCode(String code, Map<String, StringCondition> mapStringParams) {
        for (Map.Entry<String, StringCondition> entry : mapStringParams.entrySet()) {
            if (!entry.getValue().getStringContain().equals("")) {
                for (Map.Entry<String, String> entryContain : entry.getValue().getMapParamsContain().entrySet()) {
                    int beginIndex = code.indexOf(entryContain.getKey());
                    int endIndex = code.indexOf(entryContain.getKey()) + entryContain.getKey().length();
                    int i = beginIndex - 1;
                    while (i >= 2) {
                        String text = code.substring(i - 3, i);
                        if (text.equals("and") || text.equals(" or")) {
                            i = i - 3;
                            break;
                        }
                        i--;
                    }
                    if (i > 0) {
                        beginIndex = i;
                        code = code.replace(code.substring(beginIndex, endIndex), "");
                    }
                }
            }
        }
        return code.trim();
    }
}

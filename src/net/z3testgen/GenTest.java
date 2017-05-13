package net.z3testgen;

import com.microsoft.z3.*;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenTest {
    private HashMap<String, String> listData;

    public void genTest(String dslFile, String outputFile, JTextArea textArea) throws Z3Exception, IOException {
        listData = new HashMap<>();
        Helper helper = new Helper(textArea);
        ReadWriteFile readWriteFile = new ReadWriteFile();
        TranslateDsl translateDsl = new TranslateDsl();
        List<String> listDslCode = readWriteFile.readFile(dslFile);
        Map<String, StringCondition> mapStringParams = helper.getMapStringParam(listDslCode);
        helper.setStatus("Begin convert dsl to z3.");
        List<String> listZ3Code = translateDsl.translateMydsl(listDslCode, mapStringParams);
        readWriteFile.writeFile(listZ3Code, "z3.smt2");
        helper.setStatus("Done convert.");

        helper.setStatus("Begin gen test.");
        Context ctx = new Context();

        Tactic smtTactic = ctx.mkTactic("smt");

        Params p = ctx.mkParams();

        Tactic using = ctx.usingParams(smtTactic, p);

        List<String> params = helper.getParam(dslFile, mapStringParams);
        Map<String, IntExpr> listParamInt = new HashMap<>();
        Map<String, BoolExpr> listParamBool = new HashMap<>();
        Map<String, RealExpr> listParamReal = new HashMap<>();

        //Get list params
        for (int i = 0; i < params.size(); i++) {
            try {
                String[] components = params.get(i).trim().split(" ");
                if (components[0].contains("Int")) {
                    listParamInt.put(components[1], ctx.mkIntConst(components[1]));
                } else if (components[0].contains("Real")) {
                    listParamReal.put(components[1], ctx.mkRealConst(components[1]));
                } else if (components[0].contains("Bool")) {
                    listParamBool.put(components[1], ctx.mkBoolConst(components[1]));
                } else if (components[0].contains("String")) {
                    listParamInt.put(components[1], ctx.mkIntConst(components[1]));
                }
            } catch (Z3Exception e) {
                e.printStackTrace();
            }
        }

        //Read and parse file SMT2
        BoolExpr expr = ctx.parseSMTLIB2File("z3.smt2", null, null, null, null);

        Solver s = ctx.mkSolver(using);    //invoke SMT solver
        s.setParameters(p);// set the parameter for random-seed
        Model m = null;

        Solver si = ctx.mkSolver(using);
        Solver sr = ctx.mkSolver(using);

        si.setParameters(p);
        sr.setParameters(p);

        // range of value
        Date before = new Date();
        long t_diff = ((new Date()).getTime() - before.getTime());// / 1000;
        helper.setStatus("SMT2 file read time: " + t_diff + " sec");
        FileWriter writer = new FileWriter(outputFile);
        //finding all satisfiable models
        s.add(expr);

        int i = 0;
        int indexResult = -1;
        int limit = getLimit(listDslCode);
        limit = limit == 0 ? 999999 : limit;
        while (s.check() == Status.SATISFIABLE && i <= limit) {
            p.add("random_seed", i);
            s.setParameters(p);

            m = s.getModel(); // get value and print out
            FuncDecl[] listDecl = m.getConstDecls();
            //write header
            if (i == 0) {
                String head = "";
                for (int j = 0; j < listDecl.length; j++) {
                    if (listDecl[j].getName().toString().equals("result")) {
                        indexResult = j;
                    }
                    head += listDecl[j].getName().toString();
                    writer.append(listDecl[j].getName().toString());
                    if (j != listDecl.length - 1) {
                        head += ",";
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }

            String lineData = "";
            String resultTemp = "";
            for (int j = 0; j < listDecl.length; j++) {
                if (listDecl[j].getName().toString().equals("result")) {
                    resultTemp = m.eval(m.getConstInterp(listDecl[j]), false).toString();
                }
            }

            for (int j = 0; j < listDecl.length; j++) {
                String stringData = null;
                StringCondition stringCondition = mapStringParams.get(listDecl[j].getName().toString());

                String result = m.eval(m.getConstInterp(listDecl[j]), false).toString();

                if (stringCondition != null) {
                    stringData = helper.generateStringDataByCondition(stringCondition, Integer.parseInt(result), resultTemp);
                }
                if (stringData != null) {
                    lineData += stringData;
                } else if (result.contains("/")) {
                    DecimalFormat df = new DecimalFormat("#.##########");
                    lineData += df.format(divide(result));
                } else {
                    lineData += result;
                }
                if (j != listDecl.length - 1) {
                    lineData += ",";
                }
            }
            lineData = lineData.trim();
            if (!isExistData(lineData)) {
                writer.append(lineData);
                writer.append('\n');
                listData.put(lineData, "");
            }

            // seek to "next" model, remove repeated value
            for (int j = 0; j < listDecl.length; j++) {
                IntExpr intEx = listParamInt.get(listDecl[j].getName().toString());
                if (intEx != null) {
                    s.add(
                            ctx.mkOr(
                                    ctx.mkEq(ctx.mkEq(intEx, m.eval(m.getConstInterp(listDecl[j]), false)), ctx.mkFalse())
                            )
                    );
                }
                RealExpr realEx = listParamReal.get(listDecl[j].getName().toString());
                if (realEx != null) {
                    s.add(
                            ctx.mkOr(
                                    ctx.mkEq(ctx.mkEq(realEx, m.eval(m.getConstInterp(listDecl[j]), false)), ctx.mkFalse())
                            )
                    );
                }
                BoolExpr boolEx = listParamBool.get(listDecl[j].getName().toString());
                if (boolEx != null) {
                    s.add(
                            ctx.mkOr(
                                    ctx.mkEq(ctx.mkEq(boolEx, m.eval(m.getConstInterp(listDecl[j]), false)), ctx.mkFalse())
                            )
                    );
                }
            }
            i++;
        }

        long t_diff2 = ((new Date()).getTime() - before.getTime());// / 1000;
        helper.setStatus("SMT2 file test took " + t_diff2 + " ms");
        writer.flush();
        writer.close();

        helper.setStatus("Success.");
    }

    private double divide(String math) {
        int index = math.indexOf("/");
        String numA = math.substring(0, index);
        String numB = math.substring(index + 1);
        return Double.parseDouble(numA) / Double.parseDouble(numB);
    }

    private boolean isExistData(String data) {
        return listData.get(data) != null;
    }

    private int getLimit(List<String> listDsl) {
        int limit = 0;
        for (String s : listDsl) {
            if (s.contains("limit ")) {
                limit = Integer.parseInt(s.trim().replace("limit ", ""));
            }
        }
        return limit;
    }
}
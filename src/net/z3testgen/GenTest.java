package net.z3testgen;

import com.microsoft.z3.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenTest {
    public static void main(String[] args) throws Z3Exception, IOException {
        String dslFile = "input/triangle.agt";
        String outputFile = "output/Triangle5.csv";
        ReadWriteFile readWriteFile = new ReadWriteFile();
        TranslateDsl translateDsl = new TranslateDsl();
        List<String> listDslCode = readWriteFile.readFile(dslFile);
        List<String> listZ3Code = translateDsl.translateMydsl(listDslCode);
        readWriteFile.writeFile(listZ3Code, "input/z3.smt2");
        System.out.println("Done Convert.");

        Context ctx = new Context();
        Tactic smtTactic = ctx.mkTactic("smt");

        Params p = ctx.mkParams();

        Tactic using = ctx.usingParams(smtTactic, p);
        //Read and parse file SMT2
        BoolExpr expr = ctx.parseSMTLIB2File("input/z3.smt2", null, null, null, null);
        List<String> params = getParam(dslFile);

        Solver s = ctx.mkSolver(using);    //invoke SMT solver
        s.setParameters(p);// set the parameter for random-seed
        Model m = null;

        Solver si = ctx.mkSolver(using);
        Solver sr = ctx.mkSolver(using);

        si.setParameters(p);
        sr.setParameters(p);

        Map<String, IntExpr> listParamInt = new HashMap<>();
        Map<String, BoolExpr> listParamBool = new HashMap<>();
        Map<String, RealExpr> listParamReal = new HashMap<>();
        for (int i = 0; i < params.size(); i++) {
            try {
                String[] components = params.get(i).trim().split(" ");
                if (components[0].contains("Int")) {
                    listParamInt.put(components[1], ctx.mkIntConst(components[1]));
                } else if (components[0].contains("Real")) {
                    listParamReal.put(components[1], ctx.mkRealConst(components[1]));
                } else if (components[0].contains("Bool")) {
                    listParamBool.put(components[1], ctx.mkBoolConst(components[1]));
                }
            } catch (Z3Exception e) {
                e.printStackTrace();
            }
        }

// range of value
        Date before = new Date();
        long t_diff = ((new Date()).getTime() - before.getTime());// / 1000;
        System.out.println("SMT2 file read time: " + t_diff + " sec");

        FileWriter writer = new FileWriter(outputFile);
        System.out.println("model for: Triangle Type");
        //finding all satisfiable models
        s.add(expr);

        int i = 0;
        while (s.check() == Status.SATISFIABLE && i != 100) {
            p.add("random_seed", i);
            s.setParameters(p);

            m = s.getModel(); // get value and print out
            FuncDecl[] listDecl = m.getConstDecls();
            //write header
            if (i == 0) {
                for (int j = 0; j < listDecl.length; j++) {
                    writer.append(listDecl[j].getName().toString());
                    if (j != listDecl.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }

            for (int j = 0; j < listDecl.length; j++) {
                writer.append("" + m.eval(m.getConstInterp(listDecl[j]), false));
                if (j != listDecl.length - 1) {
                    writer.append(",");
                }
            }
            writer.append('\n');

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
        System.out.println("SMT2 file test took " + t_diff2 + " ms");
        writer.flush();
        writer.close();

        System.out.println("Success!");
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
}
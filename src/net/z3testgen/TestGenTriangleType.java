package net.z3testgen;

import com.microsoft.z3.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class TestGenTriangleType {


    @SuppressWarnings("null")
    public static void main(String[] args) throws Z3Exception, IOException {
        Context ctx = new Context();
        Tactic smtTactic = ctx.mkTactic("smt");

        Params p = ctx.mkParams();

        Tactic using = ctx.usingParams(smtTactic, p);
        //Read and parse file SMT2
        BoolExpr expr = ctx.parseSMTLIB2File("input/Triangle.smt2", null, null, null, null);
        Solver s = ctx.mkSolver(using);    //invoke SMT solver
        s.setParameters(p);// set the parameter for random-seed
        Model m = null;

        Solver si = ctx.mkSolver(using);
        Solver sr = ctx.mkSolver(using);

        si.setParameters(p);
        sr.setParameters(p);


// Declare side a,b,c
        IntExpr a = ctx.mkIntConst("a");
        IntExpr b = ctx.mkIntConst("b");
        IntExpr c = ctx.mkIntConst("c");

// range of value
        Date before = new Date();
        long t_diff = ((new Date()).getTime() - before.getTime());// / 1000;
        System.out.println("SMT2 file read time: " + t_diff + " sec");

        FileWriter writer = new FileWriter("output/Triangle5.csv");
        System.out.println("model for: Triangle Type");
        writer.append("a");
        writer.append(',');
        writer.append("b");
        writer.append(',');
        writer.append("c");
        writer.append(',');
        writer.append("TType");
        writer.append('\n');
        //finding all satisfiable models
        s.add(expr);

        int i = 0;
        while (s.check() == Status.SATISFIABLE && i != 100) {
            i++;
/*
 * we can set parameter random_seed to generate random values in a range: a,b,c in (P,Q)
 */
            p.add("random_seed", i);
            s.setParameters(p);

            m = s.getModel(); // get value and print out

            //Check values and remark the properties of variable's value
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[1]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[0]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[2]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[3]), false));
            writer.append('\n');

            // seek to "next" model, remove repeated value
            s.add(
                    ctx.mkOr(
                            ctx.mkEq(ctx.mkEq(a, m.eval(m.getConstInterp(m.getConstDecls()[1]), false)), ctx.mkFalse()),
                            ctx.mkEq(ctx.mkEq(b, m.eval(m.getConstInterp(m.getConstDecls()[0]), false)), ctx.mkFalse()),
                            ctx.mkEq(ctx.mkEq(c, m.eval(m.getConstInterp(m.getConstDecls()[2]), false)), ctx.mkFalse())
                    )
            );
        }

        long t_diff2 = ((new Date()).getTime() - before.getTime());// / 1000;
        System.out.println("SMT2 file test took " + t_diff2 + " ms");
        writer.flush();
        writer.close();

        System.out.println("Success!");
    }

}
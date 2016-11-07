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
        int max = Integer.MAX_VALUE;
        //	 int max = 100;
        int min = 0;
        int lb = 1;

// LBound, UBound, UnBound, UpBound, NLBound, NUBound, MidVal ;declare boundary values
        IntExpr LBound = ctx.mkInt(lb);
        IntExpr UBound = ctx.mkInt(max);
        IntExpr NLBound = (IntExpr) ctx.mkAdd(LBound, ctx.mkInt(1));
        IntExpr NUBound = (IntExpr) ctx.mkSub(UBound, ctx.mkInt(1));
        IntExpr UnBound = (IntExpr) ctx.mkSub(LBound, ctx.mkInt(1));

        IntExpr MidVal = (IntExpr) ctx.mkDiv(ctx.mkAdd(UBound, LBound), ctx.mkInt(2));

        Date before = new Date();
        long t_diff = ((new Date()).getTime() - before.getTime());// / 1000;
        System.out.println("SMT2 file read time: " + t_diff + " sec");

//write solution to file .csv
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
        while (s.check() == Status.SATISFIABLE) {
            i++;
/*
 * we can set parameter random_seed to generate random values in a range: a,b,c in (P,Q)
 */
            p.add("random_seed", i);
            s.setParameters(p);

            m = s.getModel(); // get value and print out

            //Check values and remark the properties of variable's value
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[0]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[1]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[2]), false));
            writer.append(',');
            writer.append("" + m.eval(m.getConstInterp(m.getConstDecls()[3]), false));
            writer.append('\n');

            System.out.println(m.getConstDecls()[0].getName());
            System.out.println(m.getConstDecls()[1].getName());
            System.out.println(m.getConstDecls()[2].getName());
            System.out.println(m.getConstDecls()[3].getName());

            // seek to "next" model, remove repeated value
            s.add(
                    ctx.mkOr(
                            ctx.mkEq(ctx.mkEq(a, m.eval(m.getConstInterp(m.getConstDecls()[1]), false)), ctx.mkFalse()),
                            ctx.mkEq(ctx.mkEq(b, m.eval(m.getConstInterp(m.getConstDecls()[0]), false)), ctx.mkFalse()),
                            ctx.mkEq(ctx.mkEq(c, m.eval(m.getConstInterp(m.getConstDecls()[2]), false)), ctx.mkFalse())
                            // ctx.mkAnd(ctx.mkEq(ctx.mkEq(tong, ctx.mkAdd(a,b,c)), ctx.mkFalse()),ctx.mkEq(ctx.mkEq(f,m.eval(m.getConstInterp(m.getConstDecls()[3]), false) ),ctx.mkFalse()))
                    )
            );
/*
 *  add new constraint LBound <= a,b,c <=UBound
 *  using boundary values,and heuristic a >= LB <=> a = LB \/ a = LB+1 \/ a > LB
 *  generate worst case Test cases => generates 5^3 test cases
 */
            s.add(ctx.mkAnd(ctx.mkOr(ctx.mkEq(a, LBound), ctx.mkEq(a, NLBound), ctx.mkEq(a, NUBound), ctx.mkEq(a, UBound), ctx.mkEq(a, MidVal), ctx.mkEq(a, ctx.mkInt(20))),
                    ctx.mkOr(ctx.mkEq(b, LBound), ctx.mkEq(b, NLBound), ctx.mkEq(b, NUBound), ctx.mkEq(b, UBound), ctx.mkEq(b, MidVal), ctx.mkEq(b, ctx.mkInt(40))),
                    ctx.mkOr(ctx.mkEq(c, LBound), ctx.mkEq(c, NLBound), ctx.mkEq(c, NUBound), ctx.mkEq(c, UBound), ctx.mkEq(c, MidVal), ctx.mkEq(c, ctx.mkInt(60)))
            ));
/*
 * Mix with equivalence, heuristic:
 * each equivalence class, pick 1 representative (any value) for each variable
 * No.test cases = 6^3 (6 values for each variable)
 */
//			 s.add(ctx.mkAnd(ctx.mkOr(ctx.mkEq(a, LBound),ctx.mkEq(a, NLBound), ctx.mkEq(a, NUBound),ctx.mkEq(a,UBound),ctx.mkEq(a,MidVal),ctx.mkEq(a,ctx.mkInt(20))),
//					 ctx.mkOr(ctx.mkEq(b, LBound),ctx.mkEq(b, NLBound), ctx.mkEq(b, NUBound),ctx.mkEq(b,UBound),ctx.mkEq(b,MidVal),ctx.mkEq(b,ctx.mkInt(40))),
//					 ctx.mkOr(ctx.mkEq(c, LBound),ctx.mkEq(c, NLBound), ctx.mkEq(c, NUBound),ctx.mkEq(c,UBound),ctx.mkEq(c,MidVal),ctx.mkEq(c,ctx.mkInt(60)))
//					 ));

 /*
  * we can add more constraints to handle the output here.
  */

            //only EQUI
            //s.add(ctx.mkAnd(ctx.mkEq(a, b),ctx.mkEq(b, c) ));
             /*
              *
			  * ---- only ISO
			 s.add(ctx.mkOr(ctx.mkAnd(ctx.mkEq(a, b),ctx.mkNot(ctx.mkEq(b, c)) ),
					 ctx.mkAnd(ctx.mkEq(b, c),ctx.mkNot(ctx.mkEq(c, a)) ),
					 ctx.mkAnd(ctx.mkEq(a, c),ctx.mkNot(ctx.mkEq(b, c)) )
					 ));
					// */
        }

        long t_diff2 = ((new Date()).getTime() - before.getTime());// / 1000;
        System.out.println("SMT2 file test took " + t_diff2 + " ms");
        writer.flush();
        writer.close();

        System.out.println("Success!");
    }

}
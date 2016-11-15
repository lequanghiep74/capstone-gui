package net.z3testgen;

import java.util.Stack;
import java.util.StringTokenizer;

public class Itp {
    public static boolean isOperand(String s) {
        return !(s.equals("+") || s.equals("-") || s.equals("/") || s.equals("*") || s.equals("=") || s.equals(">")
                || s.equals("<") || s.equals("(") || s.equals(")") || s.equals("and") || s.equals("or")
                || s.equals("<=") || s.equals(">="));
    }

    public static String operationCombine(Stack<String> operatorStack, Stack<String> operandStack) {
        String operator = operatorStack.pop();
        String rightOperand = operandStack.pop();
        String leftOperand = operandStack.pop();
        String operand = "(" + operator + " " + leftOperand + " " + rightOperand + ")";
        return operand;
    }

    public static int rank(String s) {
        if (s.equals("and") || s.equals("or"))
            return 1;
        else if (s.equals("=") || s.equals(">") || s.equals("<") || s.equals("<=") || s.equals(">="))
            return 2;
        else if (s.equals("+") || s.equals("-"))
            return 3;
        else if (s.equals("/") || s.equals("*"))
            return 4;
        else
            return 0;
    }

    public static String infixToPrefixConvert(String infix) {
        Stack<String> operandStack = new Stack<String>();
        Stack<String> operatorStack = new Stack<String>();

        StringTokenizer tokenizer = new StringTokenizer(infix);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (isOperand(token)) {
                operandStack.push(token);
            } else if (token.equals("(") || operatorStack.isEmpty()
                    || rank(token) > rank(operatorStack.peek())) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.peek().equals("(")) {
                    operandStack.push(operationCombine(operatorStack, operandStack));
                }
                operatorStack.pop();
            } else if (rank(token) <= rank(operatorStack.peek())) {
                while (!operatorStack.isEmpty() && rank(token) <= rank(operatorStack.peek())) {
                    operandStack.push(operationCombine(operatorStack, operandStack));
                }
                operatorStack.push(token);
            }
        }
        while (!operatorStack.isEmpty()) {
            operandStack.push(operationCombine(operatorStack, operandStack));
        }
        return (operandStack.peek());
    }
}

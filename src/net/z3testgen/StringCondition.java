package net.z3testgen;

/**
 * Created by lequanghiep on 4/4/2017.
 */
public class StringCondition {
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

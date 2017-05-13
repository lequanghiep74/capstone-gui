package net.z3testgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lequanghiep on 4/4/2017.
 */
public class StringCondition {
    private boolean containDigit = true;
    private boolean containLetter = true;
    private String regex = "";
    private String stringContain = "";
    private int length;
    private boolean isUseLength = true;
    private Map<String, String> mapParamsContain = new HashMap<>();
    private List<String> listTestCaseUseLength = new ArrayList<>();
    private Map<String, String> mapTestCaseUseContain = new HashMap<>();

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

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getStringContain() {
        return stringContain;
    }

    public void setStringContain(String stringContain) {
        this.stringContain = stringContain;
    }

    public boolean isUseLength() {
        return isUseLength;
    }

    public void setUseLength(boolean useLength) {
        isUseLength = useLength;
    }

    public Map<String, String> getMapParamsContain() {
        return mapParamsContain;
    }

    public void setMapParamsContain(Map<String, String> mapParamsContain) {
        this.mapParamsContain = mapParamsContain;
    }

    public List<String> getListTestCaseUseLength() {
        return listTestCaseUseLength;
    }

    public void setListTestCaseUseLength(List<String> listTestCaseUseLength) {
        this.listTestCaseUseLength = listTestCaseUseLength;
    }

    public Map<String, String> getMapTestCaseUseContain() {
        return mapTestCaseUseContain;
    }

    public void setMapTestCaseUseContain(Map<String, String> mapTestCaseUseContain) {
        this.mapTestCaseUseContain = mapTestCaseUseContain;
    }

    public void addListTestCaseUseLength(String testcase) {
        this.listTestCaseUseLength.add(testcase);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}

package net.z3testgen.create_test;

import net.z3testgen.ReadWriteFile;

import java.util.List;

/**
 * Created by lequanghiep on 11/15/2016.
 */
public class CreateTestcaseBdd {
    public static void genTestCaseBdd(String uriDataTest, String uriBddFile, String outputFile) {
        ReadWriteFile readWriteFile = new ReadWriteFile();
        List<String> datas = readWriteFile.readFile(uriDataTest);
        List<String> bdd = readWriteFile.readFile(uriBddFile);
        bdd.add("\nExamples:");
        for (String line : datas) {
            bdd.add('|' + line.replace(",", "|") + "|");
        }
        readWriteFile.writeFile(bdd, outputFile);
    }

    public static void main (String[] args) {
        genTestCaseBdd("output/data.csv", "input/test.feature", "input/test1.feature");
    }
}

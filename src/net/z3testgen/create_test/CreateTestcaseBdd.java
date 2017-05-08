package net.z3testgen.create_test;

import net.z3testgen.ReadWriteFile;

import java.io.File;
import java.util.List;

/**
 * Created by lequanghiep on 11/15/2016.
 */
public class CreateTestcaseBdd {
    public static void genTestCaseBdd(String uriDataTest, String uriBddFile, String outputFile) {
        String dirTestcase = getOutputDir(uriBddFile);
        new File(dirTestcase).mkdirs();
        dirTestcase += "\\" + getFileName(new File(uriBddFile).getName()) + ".java";
        GenJavaTestCase genJavaTestCase = new GenJavaTestCase();
        genJavaTestCase.doing(uriBddFile, dirTestcase);

        ReadWriteFile readWriteFile = new ReadWriteFile();
        List<String> datas = readWriteFile.readFile(uriDataTest);
        List<String> bdd = readWriteFile.readFile(uriBddFile);
        bdd.add("\nExamples:");
        for (String line : datas) {
            bdd.add('|' + line.replace(",", "|") + "|");
        }
        readWriteFile.writeFile(bdd, outputFile);
    }

    private static String getFileName(String name) {
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name;
    }

    private static String getOutputDir(String inputDir) {
        File file = new File(inputDir);
        File temp = new File(file.getParent());
        while (!getLastFolder(temp.getPath()).equals("test")) {
            temp = new File(temp.getParent());
        }
        String dir = file.getPath().substring((temp.getPath() + "\\resources").length());
        dir = dir.substring(0, dir.lastIndexOf("\\"));
        return temp.getPath() + "\\java" + dir;
    }

    private static String getLastFolder(String dir) {
        return dir.substring(dir.lastIndexOf('\\') + 1);
    }
}

package net.z3testgen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenString {
    public void genString(String outputDir, List<String> listDslCode, List<String> params) throws IOException {
        Helper helper = new Helper();
        GenTest genTest = new GenTest();
        Map<String, StringCondition> varString = new HashMap<>();

        FileWriter writer = new FileWriter(outputDir);

        for (int i = 0; i < params.size(); i++) {
            String[] components = params.get(i).trim().split(" ");
            varString.put(components[1], new StringCondition());
            writer.append(components[1]);
            if (i != params.size() - 1) {
                writer.append(',');
            }
        }
        writer.append("\n");

        varString = genTest.getListConditionOfStringValue(listDslCode, varString);

        for (int i = 0; i < 100; i++) {
            List<String> tempData = helper.generateStringData(varString);
            writer.append(String.join(",", tempData));
            writer.append("\n");
        }
        writer.flush();
        writer.close();

        System.out.print("Done");
    }
}

package net.z3testgen;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadWriteFile {

    public List<String> readFile(String uri) {
        List<String> myDslCode = new ArrayList<>();
        try {
            Scanner input = new Scanner(System.in);
            File file = new File(uri);

            input = new Scanner(file);

            while (input.hasNextLine()) {
                String line = input.nextLine();
                myDslCode.add(line);
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return myDslCode;
        }
    }

    public void writeFile(List<String> codeZ3, String uri) {
        try {
            PrintWriter writer = new PrintWriter(uri, "UTF-8");
            for (int i = 0; i < codeZ3.size(); i++) {
                writer.println(codeZ3.get(i));
            }
            writer.close();
        } catch (Exception e) {
            // do something
        }
    }

    public void writeFile(String data, String uri) {
        try {
            PrintWriter writer = new PrintWriter(uri, "UTF-8");
            writer.println(data);
            writer.close();
        } catch (Exception e) {
            // do something
        }
    }
}

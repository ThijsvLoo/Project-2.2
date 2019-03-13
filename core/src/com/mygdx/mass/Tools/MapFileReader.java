package com.mygdx.mass.Tools;

import com.mygdx.mass.BoxObject.BoxObject;

import java.io.*;
import java.util.ArrayList;


public class MapFileReader {
    private static ArrayList<BoxObject> readFile(String filename) {
        ArrayList<String> boxStrings = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                boxStrings.add(line);
            }
            reader.close();

        }
        catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }
}

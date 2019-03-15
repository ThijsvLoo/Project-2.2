package com.mygdx.mass.Tools;

import com.mygdx.mass.World.Map;

import java.io.*;


public class MapFileReader implements Serializable{
    public static void saveToFile(Map map) {
        try {
            OutputStream outStream = new FileOutputStream("map1.ser");
            ObjectOutputStream fileObjectOut = new ObjectOutputStream(outStream);
            fileObjectOut.writeObject(map);
            fileObjectOut.close();
            outStream.close();
        } catch(IOException e){
            e.printStackTrace();
            e.getCause();
            e.getMessage();
            System.out.println("input IO error");
        }

    }
    public static Map readFromFile(String filename) throws IOException, ClassNotFoundException {
        Map map;
        try{
            InputStream inStream = new FileInputStream("map1.ser");
        ObjectInputStream fileObjectIn = new ObjectInputStream(inStream);
        map = (Map) fileObjectIn.readObject();
        fileObjectIn.close();
        inStream.close();
        } catch(IOException e){
        System.out.println("IO error");
        e.printStackTrace();
        return null;
        } catch(ClassNotFoundException e){
        System.out.println("Class not found");
        e.printStackTrace();
        return null;
        }
        return map;

//        ArrayList<String> boxStrings = new ArrayList<String>();
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(filename));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                boxStrings.add(line);
//            }
//            reader.close();
//
//        }
//        catch (Exception e) {
//            System.err.format("Exception occurred trying to read '%s'.", filename);
//            e.printStackTrace();
//            return null;
//        }
//        return null;
    }

}

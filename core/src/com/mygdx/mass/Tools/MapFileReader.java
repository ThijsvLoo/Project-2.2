package com.mygdx.mass.Tools;

import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;
import java.io.*;


public class MapFileReader implements Serializable{
    public static void saveMapToFile(Map map) {
        try {
        	File mapFile = new File("map1.ser");
            OutputStream outStream = new FileOutputStream(mapFile);
            ObjectOutputStream fileObjectOut = new ObjectOutputStream(outStream);
            MapData mapData = new MapData(map);
            fileObjectOut.writeObject(mapData);
            fileObjectOut.close();
            outStream.close();
        } catch(IOException e){
            e.printStackTrace();
            e.getCause();
            e.getMessage();
            System.out.println("input IO error");
        }

    }
    public static Map createMapFromFile(MASS mass){
        MapData mapData;
        try{
        	File mapFile = new File("map1.ser");
            InputStream inStream = new FileInputStream(mapFile);
			ObjectInputStream fileObjectIn = new ObjectInputStream(inStream);
			mapData = (MapData) fileObjectIn.readObject();
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
        return mapData.getMap(mass);
    }

}

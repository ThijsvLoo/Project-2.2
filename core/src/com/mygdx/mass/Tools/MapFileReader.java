package com.mygdx.mass.Tools;

import com.mygdx.mass.Data.MASS;
//import com.mygdx.mass.Scenes.FileChooserDemo;
import com.mygdx.mass.World.Map;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
import java.io.*;

public class MapFileReader extends JPanel implements Serializable{

    static String filePath = "blank.ser";
    static FileNameExtensionFilter serfilter = new FileNameExtensionFilter(
            "MASS Map Files (*.ser)", "ser");

    public static void saveMapToFile(Map map) {
        // File Chooser
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setFileFilter(serfilter);
        JFrame f = new JFrame();
        f.setVisible(true);
        f.toFront();
        f.setVisible(false);
        int res = fc.showSaveDialog(f);
        f.dispose();
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filePath = file.getPath();
            String extension = "";
            int i = filePath.lastIndexOf('.');
            if (i > 0) {
                extension = "." + filePath.substring(i+1);
            }
            System.out.println(extension);
            if (!extension.equals(".ser")){
                filePath = filePath + ".ser";
            }

        // Save Code
        try {
        	File mapFile = new File(filePath);
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
    }
    public static void loadMapFromFile(MASS mass){
        MapData mapData;
        // File Chooser
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                fc.setFileFilter(serfilter);
                JFrame f = new JFrame();
                f.setVisible(true);
                f.toFront();
                f.setVisible(false);
                int res = fc.showOpenDialog(f);
                f.dispose();
                if (res == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    filePath = file.getPath();
                }
//            }
//        }).start();
        // Loading Code
        try{
        	File mapFile = new File(filePath);
            InputStream inStream = new FileInputStream(mapFile);
			ObjectInputStream fileObjectIn = new ObjectInputStream(inStream);
			mapData = (MapData) fileObjectIn.readObject();
			fileObjectIn.close();
			inStream.close();
            mass.getMap().clearMap();
            mapData.loadMap(mass);
        } catch(IOException e){
			System.out.println("IO error");
			e.printStackTrace();
        } catch(ClassNotFoundException e){
			System.out.println("Class not found");
			e.printStackTrace();
        }
    }
}

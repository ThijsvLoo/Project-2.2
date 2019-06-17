package com.mygdx.mass.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Data.Properties;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MASS.TITLE;
		config.width = MASS.WINDOW_WIDTH;
		config.height = MASS.WINDOW_HEIGHT;
		config.samples = MASS.ANTI_ALIASING; //anti aliasing

//		THIS BUFFERED READER IS NO LONGER NEEDED
//
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new FileReader(
//					"config.properties"));
//			String line = reader.readLine();
//
//			ArrayList<Properties> settings = new ArrayList<Properties>();
//
//			settings.add(new Properties(line.split(": ")));
//
//			while (line != null) {
////				System.out.println(line);
//				settings.add(new Properties(line.split(": ")));
//				// read next line
//				line = reader.readLine();
//			}
//
//			for(int i = 0; i<settings.size(); i++){
//				if (settings.get(i).getName().equals("fs") && settings.get(i).getSetting().equals("true")) {
//					config.fullscreen = true;
//				}
//			}
//
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		config.addIcon("Textures/icon.png", Files.FileType.Internal);

		new LwjglApplication(new MASS(), config);
	}
}

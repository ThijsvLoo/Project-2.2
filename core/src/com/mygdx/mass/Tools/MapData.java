package com.mygdx.mass.Tools;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Agents.Agent;

import java.io.Serializable;
import java.util.ArrayList;

import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.World.Map;

public class MapData implements Serializable {

	public float width, heigth;

	public int[] agentType;
	public float[][] agentPosition;
	public int[] objectType;
	public Rectangle[] objectRectangle;

	public MapData(Map map){
		setMapData(map);
	}

	public void setAgents(ArrayList<Agent> agentArray) {
		agentType = new int[agentArray.size()];
		agentPosition = new float[agentArray.size()][3];
		for(int i = 0; i < agentArray.size(); i++){
			Agent tmpAgent = agentArray.get(i);
			agentType[i] = tmpAgent.getType();
			agentPosition[i][0] = tmpAgent.getBody().getPosition().x;
			agentPosition[i][1] = tmpAgent.getBody().getPosition().y;
			agentPosition[i][2] = tmpAgent.getBody().getAngle();
		}
	}

	public void setObjects(ArrayList<BoxObject> objectArray){
		objectType = new int[objectArray.size()];
		objectRectangle = new Rectangle[objectArray.size()];
		for(int i = 0; i < objectArray.size(); i++){
			BoxObject tmpObject = objectArray.get(i);
			objectType[i] = tmpObject.getType();
			objectRectangle[i] = tmpObject.getRectangle();
		}
	}

	public void setMapData(Map map){
		setAgents(map.getAgents());
		setObjects(map.getMapObjects());
		this.width = map.getWidth();
		this.heigth = map.getHeight();

	}

	public Map getMapData(){
		return null;//null is temporary
	}

	public float[][] getAgents() {
		return null;//temporary null
	}
}

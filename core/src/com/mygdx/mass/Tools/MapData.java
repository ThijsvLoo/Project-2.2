package com.mygdx.mass.Tools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import java.io.Serializable;
import java.util.ArrayList;
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.Agents.Agent.AgentType;
import com.mygdx.mass.BoxObject.BoxObject.ObjectType;

public class MapData implements Serializable {

	private float width, height;

	private AgentType[] agentType;
	private float[][] agentPosition;
	private ObjectType[] objectType;
	private Rectangle[] objectRectangle;
	//public enum AgentType {WALL, BUILDING, DOOR, WINDOW, SENTRY_TOWER, HIDING_AREA, TARGET_AREA, INTRUDER, GUARD};

	public MapData(Map map){
		setMapData(map);
	}

	private void setAgents(ArrayList<Agent> agentArray) {
		agentType = new AgentType[agentArray.size()];
		agentPosition = new float[agentArray.size()][3];
		for(int i = 0; i < agentArray.size(); i++){
			Agent tmpAgent = agentArray.get(i);
			agentType[i] = tmpAgent.getAgentType();
			agentPosition[i][0] = tmpAgent.getBody().getPosition().x;
			agentPosition[i][1] = tmpAgent.getBody().getPosition().y;
			agentPosition[i][2] = tmpAgent.getBody().getAngle();
		}
	}

	private void setObjects(ArrayList<BoxObject> objectArray){
		objectType = new ObjectType[objectArray.size()];
		objectRectangle = new Rectangle[objectArray.size()];
		for(int i = 0; i < objectArray.size(); i++){
			BoxObject tmpObject = objectArray.get(i);
			objectType[i] = tmpObject.getObjectType();
			objectRectangle[i] = tmpObject.getRectangle();
		}
	}

	public void setMapData(Map map){
		setAgents(map.getAgents());
		setObjects(map.getBoxObjects());
		this.width = map.getWidth();
		this.height = map.getHeight();

	}

	public Map getMap(MASS mass){
		Map map = new Map(mass, width, height);

		for(int i = 0; i < agentType.length; i++){
			switch(agentType[i]){
				case INTRUDER:
					map.addIntruder(new Vector2(agentPosition[i][0],agentPosition[i][1]));
					break;
				case GUARD:
					map.addGuard(new Vector2(agentPosition[i][0],agentPosition[i][1]));
			}
		}
		for(int i = 0; i < objectType.length; i++){
			switch(objectType[i]){
				case WALL:
					map.addWall(objectRectangle[i]);
					break;
				case BUILDING:
					map.addBuilding(objectRectangle[i]);
					break;
				case HIDING_AREA:
					map.addHidingArea(objectRectangle[i]);
					break;
				case SENTRY_TOWER:
					map.addSentryTower(objectRectangle[i]);
					break;
				case TARGET_AREA:
					map.addTargetArea(objectRectangle[i]);
					break;
				case DOOR:
					map.addDoor(objectRectangle[i]);
					break;
				case WINDOW:
					map.addWindow(objectRectangle[i]);
					break;
			}
		}
		return map;
	}
}

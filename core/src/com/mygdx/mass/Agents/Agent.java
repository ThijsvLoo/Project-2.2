package com.mygdx.mass.Agents;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Algorithms.Algorithm;
import com.mygdx.mass.Algorithms.Explore;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.MapToGraph.Dijkstra;
import com.mygdx.mass.MapToGraph.Edge;
import com.mygdx.mass.MapToGraph.Graph;
import com.mygdx.mass.MapToGraph.Vertex;
import com.mygdx.mass.Sensors.NoiseField;
import com.mygdx.mass.Sensors.VisualField;
import com.mygdx.mass.World.IndividualMap;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.World.WorldObject;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static com.mygdx.mass.Agents.Intruder.SPRINT_MAX_TURN_SPEED;

public abstract class Agent extends WorldObject implements java.io.Serializable{

    public static final float SIZE = 0.5f;
    public static final float DEFAULT_VIEW_ANGLE = 45.0f;
    public static final float DECREASE_VISION_FACTOR = 0.5f;
    public static final float DEFAULT_MAX_TURN_SPEED = 180.0f;
    public static final float BASE_SPEED = 1.4f;

    private float blindDuration;
    private float immobilityDuration;

    private boolean stealth;

    public Map map = new Map(mass);
    public IndividualMap individualMap;

    public enum AgentType {GUARD, INTRUDER};
    protected AgentType agentType;

    protected float moveSpeed;
    protected float turnSpeed;
    protected float maxTurnSpeed;
    protected int turnSide; //or Counter Clock

    protected float visualRange;
    protected float viewAngle;
    protected PointLight pointLight;
    protected ConeLight coneLight;

    protected ArrayList<WorldObject> objectsInSight;

    protected VisualField agentDetection;
    protected VisualField buildingDetection;
    protected VisualField TowerDetection;

    protected NoiseField noiseField;

    protected Vector2 destination;
    public LinkedBlockingQueue<Vector2> route;
    protected Vector2 direction;
    protected Vector2 velocity;

//    protected ArrayList<Object> collisions;

    protected Algorithm algorithm;

    public static int count = 0;

    public Agent(MASS mass, Vector2 position) {
        super(mass);
        individualMap = new IndividualMap(mass, Map.DEFAULT_WIDTH, Map.DEFAULT_HEIGHT);
        maxTurnSpeed = DEFAULT_MAX_TURN_SPEED;
        objectsInSight = new ArrayList<WorldObject>();
        route = new LinkedBlockingQueue<Vector2>();
        direction = new Vector2();
        velocity = new Vector2();
//        collisions = new ArrayList<Object>();
        count++;
    }

    //define the box2d body and put it into the box2d world
    public void define(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(SIZE/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        agentDetection = new VisualField(this, VisualField.VisualFieldType.AGENT);

        noiseField = new NoiseField(this);
    }

    public void update(float delta) {
//        algorithm.act();
        followRoute();
    }

    public void addWaypoint(Vector2 waypoint) {
        route.offer(waypoint);
    }

    //Need to recheck this part
    public void followRoute() {
        if (destination != null && atPosition(destination)) {
            if (!route.isEmpty()) {
                destination = route.poll();
            } else {
                destination = null;
                body.setLinearVelocity(0.0f,0.0f);

            }
        }
        if (destination == null && !route.isEmpty()) {
            destination = route.poll();
        }
        if (destination != null && !atPosition(destination)) {
            updateDirection();
            updateAngle();
            updateVelocity();
        }
    }

    public void goTo(Vector2 destination) {
        Vertex start = new Vertex(body.getPosition().x, body.getPosition().y);
        Vertex end = new Vertex(destination.x, destination.y);
        Graph graph = new Graph(new ArrayList<Vertex>(), new ArrayList<Edge>());
        graph.getPathVertices(start, end);
        Dijkstra dijkstra = new Dijkstra(graph);
        ArrayList<Vector2> bestPath = dijkstra.computePath();
        route.clear();
        for (Vector2 waypoint : bestPath) {
            addWaypoint(waypoint);
        }
    }

    public void updateDirection() {
        direction.x = destination.x - body.getPosition().x;
        direction.y = destination.y - body.getPosition().y;
    }

    //Turn the body to the destination direction
    public void updateAngle() {
        float angle;
        if (Math.PI - Math.abs(Math.PI - Math.abs(Math.atan2(direction.y, direction.x) - body.getAngle())) < 5*Math.PI/180) {
            turnSide = 0;
            angle = (float) Math.atan2(direction.y, direction.x);
        } else {
            turnSide = ((Math.atan2(direction.y, direction.x) + 2 * Math.PI - body.getAngle()) % (2 * Math.PI) < Math.PI) ? 1 : -1;
            angle = (float) ((body.getAngle() + turnSide * turnSpeed * Math.PI / 180 * Gdx.graphics.getDeltaTime()) % (2 * Math.PI));
        }
        body.setTransform(body.getWorldCenter(), angle);
    }

    public void updateVelocity() {
        if (turnSide == 0) {
            velocity.x = (float) Math.cos(body.getAngle())*moveSpeed;
            velocity.y = (float) Math.sin(body.getAngle())*moveSpeed;
            body.setLinearVelocity(velocity.x, velocity.y);
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    //point, coordinate, vector, position,
    public boolean atPosition(Vector2 vector2) {
        return body.getPosition().dst(vector2) < 0.25;
    }

    //get the unit vector with length 1
//    private Vector2 normalise(Vector2 vector2) {
//        float magnitude = magnitude(vector2);
//        return new Vector2(vector2.x*(1/magnitude), vector2.y*(1/magnitude));
//    }

    //magnitude is the length of a vector
//    private float magnitude(Vector2 vector2) {
//        return (float) Math.sqrt(Math.pow(vector2.x, 2) + Math.pow(vector2.y, 2));
//    }

//    public void addCollision(Object collision) {
//        collisions.add(collision);
//    }

//    public void removeCollision(Object collision) {
//        collisions.remove(collision);
//    }

    public AgentType getAgentType() { return agentType; }
    public float getMoveSpeed() { return moveSpeed; }
    public float getTurnSpeed() { return turnSpeed; }
    public float getVisualRange() { return visualRange; }
    public float getViewAngle() { return viewAngle; }
    public ArrayList<WorldObject> getObjectsInSight() { return objectsInSight; }
    public Vector2 getDestination() { return destination; }
    public LinkedBlockingQueue<Vector2> getRoute() { return route; }
    public Vector2 getDirection() { return direction; }
    public Vector2 getVelocity() { return velocity; }
    public IndividualMap getIndividualMap() { return individualMap; }
//    public ArrayList<Object> getCollisions() { return collisions; }

    public void setMoveSpeed(float moveSpeed) {
        if (moveSpeed >= 0.0f && moveSpeed <= Intruder.SPRINT_SPEED) {
            if (moveSpeed <= BASE_SPEED) {
                this.moveSpeed = moveSpeed;
                setMaxTurnSpeed(DEFAULT_MAX_TURN_SPEED);
            } else if (moveSpeed >= BASE_SPEED && this instanceof Intruder && ((Intruder) this).sprintDuration > 0.0f) {
                this.moveSpeed = moveSpeed;
                setMaxTurnSpeed(SPRINT_MAX_TURN_SPEED);
            }
            noiseField.update();
        } else {
            System.out.println("Move Speed Must Be Between 0.0 and 3.0 m/s");
        }
    }

    public void setTurnSpeed(float turnSpeed) {
        if (this.turnSpeed >= 0.0f && this.turnSpeed <= maxTurnSpeed) {
            this.turnSpeed = turnSpeed;
        } else if (this.turnSpeed > maxTurnSpeed) {
            this.turnSpeed = maxTurnSpeed;
        }
    }

    public void setMaxTurnSpeed(float maxTurnSpeed) {
        this.maxTurnSpeed = maxTurnSpeed;
        if (turnSpeed > this.maxTurnSpeed) {
            turnSpeed = this.maxTurnSpeed;
        }
    }
    public void setVisualRange(float visualRange) { this.visualRange = visualRange; }
    public void setViewAngle(float viewAngle) { this.viewAngle = viewAngle; }
    public void setObjectsInSight(ArrayList<WorldObject> objectsInSight) { this.objectsInSight = objectsInSight; }
    public void setDestination(Vector2 destination) { this.destination = destination; }
    public void setRoute(LinkedBlockingQueue<Vector2> route) { this.route = route; }
    public void setDirection(Vector2 direction) { this.direction = direction; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }
//    public void setCollisions(ArrayList<Object> collisions) { this.collisions = collisions; }

    public String toString() {
        return "AgentType = "+this.agentType +" count of Agents = "+ count;
    }

    public void shareMap(Agent agent){
        for (int i = 0; i < this.map.walls.size(); i++){
            if ( !(agent.map.walls.contains(this.map.walls.get(i)))) {
                ArrayList<Wall> walls = agent.map.getWalls();
                walls.add(this.map.walls.get(i));
                agent.map.setWalls(walls);
            }
        }
        for (int i = 0; i < this.map.buildings.size(); i++){
            if ( !(agent.map.buildings.contains(this.map.buildings.get(i)))) {
                ArrayList<Building> buildings = agent.map.getBuildings();
                buildings.add(this.map.buildings.get(i));
                agent.map.setBuildings(buildings);
            }
        }
        for (int i = 0; i < this.map.doors.size(); i++){
            if ( !(agent.map.doors.contains(this.map.doors.get(i)))) {
                ArrayList<Door> doors = agent.map.getDoors();
                doors.add(this.map.doors.get(i));
                agent.map.setDoors(doors);
            }
        }
        for (int i = 0; i < this.map.windows.size(); i++){
            if ( !(agent.map.windows.contains(this.map.windows.get(i)))) {
                ArrayList<Window> windows = agent.map.getWindows();
                windows.add(this.map.windows.get(i));
                agent.map.setWindows(windows);
            }
        }
        for (int i = 0; i < this.map.sentryTowers.size(); i++){
            if ( !(agent.map.sentryTowers.contains(this.map.sentryTowers.get(i)))) {
                ArrayList<SentryTower> sentryTowers = agent.map.getSentryTowers();
                sentryTowers.add(this.map.sentryTowers.get(i));
                agent.map.setSentryTowers(sentryTowers);
            }
        }
        for (int i = 0; i < this.map.hidingAreas.size(); i++){
            if ( !(agent.map.hidingAreas.contains(this.map.hidingAreas.get(i)))) {
                ArrayList<HidingArea> hidingAreas = agent.map.getHidingAreas();
                hidingAreas.add(this.map.hidingAreas.get(i));
                agent.map.setHidingAreas(hidingAreas);
            }
        }
        for (int i = 0; i < this.map.targetAreas.size(); i++){
            if ( !(agent.map.targetAreas.contains(this.map.targetAreas.get(i)))) {
                ArrayList<TargetArea> targetAreas = agent.map.getTargetAreas();
                targetAreas.add(this.map.targetAreas.get(i));
                agent.map.setTargetAreas(targetAreas);
            }
        }

        for (int i = 0; i < this.map.removedMarkers.size(); i++){
            if ( (agent.map.markers.contains(this.map.removedMarkers.get(i)))) {
                agent.map.removeMarker(this.map.removedMarkers.get(i));
            }
        }

        for (int i = 0; i < this.map.markers.size(); i++){
            if ( !(agent.map.markers.contains(this.map.markers.get(i)))) {
                ArrayList<Marker> markers = agent.map.getMarkers();
                markers.add(this.map.markers.get(i));
                agent.map.setMarkers(markers);
            }
        }


    }

}

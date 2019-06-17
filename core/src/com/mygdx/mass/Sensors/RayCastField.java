package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Scenes.MapSimulatorInfo;
import com.mygdx.mass.Screens.MapSimulatorScreen;
import com.mygdx.mass.World.WorldObject;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;
import java.util.HashSet;

public class RayCastField { // each agent has a RayCastField

    protected MASS mass;
    protected World world;

    private ArrayList<RayCast> rays = new ArrayList<RayCast>();

    public static final int MAX_RAYS = 10000;

    private int rayCount;
    private float viewingAngle, startRange, range;
    private double rotationRad, viewingAngleRad; // radian
    private Vector2 locationAgent;
    private short fieldScanTypeMask; // this defines the type of field to scan for(building, agent, etc.)
    private short fieldReturnTypeMask; // what objects should we return (e.g. only towers for tower ray field)
    private short fieldTransparentTypeMask; // these objects should still be detected but are transparent
    private HashSet<Object> collisionObjects; // holds list with all objects this ray field sees
    private String typeOfField;

    public RayCastField (MASS mass){
        this.mass = mass;
        this.world = mass.world;
        collisionObjects = new HashSet<Object>();

    }

    class RayCast { // each ray within a RayCastField
        private Vector2 startRay, endRay, drawRay;
        private ArrayList<RayCollision> rayCollisions;



        public RayCast(Vector2 start, Vector2 end) { // running one ray
            //System.out.println("closesCollision: "+closestCollision);
            RayCollision closestCollision = null;
            drawRay = new Vector2();
            startRay = start;
            endRay = end;
            rayCollisions = new ArrayList<RayCollision>();

            RayCastCallback callback = new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    // 'normal' = a point. To get line, start at 'point' to 'normal'
                    // 'fraction' = fraction of line length between 'point' and the collision between 0-1
                    //System.out.println("2Collision on ray at "+fraction*100+"% of the ray with fixture: "+fixture.getFilterData().categoryBits+" from body: "+fixture.getFilterData().maskBits+" @: "+point);
                    //if(fixture != null) {
                    MapSimulatorInfo.addRayCollisionCount();
                    short fixtureTypeObjectBit = fixture.getFilterData().categoryBits;
                    if ((fixtureTypeObjectBit & fieldScanTypeMask) == 0) { return 1; } // skip this collision as it's not in the type mask (e.g. collision is with an agent but we only want buildings)
                    rayCollisions.add(new RayCollision(fraction,fixture,new Vector2(point)));
                    //collision.set(point);
                    return 1;
                }
            };

            world.rayCast(callback,startRay,endRay); // callback is called multiple times until it's done. No call is also possible

            boolean isClosestCollisionTransparent = true;
            while (isClosestCollisionTransparent) { // check if the closest collision is transparent, if so, save it to objectsInSight but remove if from the collision array and run again until either there are no more collisions in the array or we find a collision which is not transparent
                if (rayCollisions.isEmpty()) {
                    drawRay = endRay; // nothing is detected, but we still want to draw
                    isClosestCollisionTransparent = false;
                } else {
                    // search for the closest collision
                    float fract = 1.1f;
                    for (RayCollision rayCollision : rayCollisions) {
                        if (rayCollision.getFraction() < fract) {
                            //closestCollision = new RayCollision(rayCollision.getFraction(), rayCollision.getFixture(), rayCollision.getPoint());
                            closestCollision = rayCollision;
                            fract = rayCollision.getFraction();
                        }
                    } // found the closest collision

                    if (closestCollision != null) { // do something with the closest collision
                        if ((closestCollision.fixture.getFilterData().categoryBits & fieldTransparentTypeMask) != 0) { // we found a transparent object
                            if ((closestCollision.fixture.getFilterData().categoryBits & fieldReturnTypeMask) != 0) {
                                collisionObjects.add(closestCollision.fixture.getUserData());
                            }
                            rayCollisions.remove(closestCollision);
                        }
                        else { // closest object is not transparent
                            drawRay = new Vector2(closestCollision.getPoint());
                            if ((closestCollision.fixture.getFilterData().categoryBits & fieldReturnTypeMask) != 0)
                                collisionObjects.add(closestCollision.fixture.getUserData());
                            isClosestCollisionTransparent = false;
                        }
                    } else {
                        System.out.println("ERROR IN CLOSEST COLLISION!");
                        drawRay = endRay;
                        isClosestCollisionTransparent = false;
                    }
                }
            }
            rayCollisions.clear();
        }



        class RayCollision { // each ray has 0 or more collisions
            // each collision has an object, this object has a fixture. The collision happened at a point along the fraction of a ray
            private float fraction;
            private Fixture fixture;
            private Vector2 point;
            //private WorldObject object;

            public RayCollision(float fraction, Fixture fixture, Vector2 point) {
                this.fraction = fraction;
                this.fixture = fixture;
                this.point = point;
                //this.object = object;
            }

            public float getFraction() {
                return fraction;
            }

            public Vector2 getPoint() {
                return point;
            }

            public Fixture getFixture() {
                return fixture;
            }

        }

        public Vector2 getStartRay() {
            return startRay;
        }

        public Vector2 getDrawRay() {
            return drawRay;
        }
    }

    public void createRays() {
        // calculation maximum allowable angle between rays in order to detect the smallest objects: agents
        if (viewingAngle > 360.0f) viewingAngle = 360.0f;
        viewingAngleRad = Math.toRadians(viewingAngle);
        double maxDegreeRad;
        double halfAgentSize = 0.5 * Agent.SIZE;
        maxDegreeRad = 2 * Math.asin(halfAgentSize/range);

        //          .|
        //         . |
        //   RAY  .  |
        //       .   | <- SIZE
        //      .   _|
        //     .  ,  |
        //    .  ,   | W
        //   .  ,    | A      LENGTH
        //  .  ,     | L
        // . ,    90*| L
        // ._________|
        //  .5 SIZE

        if(typeOfField.equalsIgnoreCase("GAP SENSOR")) { // logic to calculate maximum ray angle for gap sensor
            double completeAngleRad = Math.acos(halfAgentSize / range);
            double length = Math.tan(completeAngleRad) * halfAgentSize;
            double bottomAngleRad = Math.atan((length - Agent.SIZE) / halfAgentSize);
            maxDegreeRad = completeAngleRad - bottomAngleRad;
            //System.out.println(completeAngleRad+"|"+length+"|"+bottomAngleRad+"|"+maxDegreeRad);
        }
        rayCount = (int) Math.ceil(viewingAngleRad/maxDegreeRad);
        if (rayCount > MAX_RAYS) { System.out.println("Capping maximum rays for this field! "+this); rayCount = MAX_RAYS; }
        double fieldAnglePartitionRad = viewingAngleRad / rayCount;
        double startRotationRad = rotationRad - (viewingAngleRad / 2);
        double currentRotationRad = startRotationRad;
        rayCount++; // add last ray
        if (viewingAngle == 360.0f) rayCount = rayCount-1;  // remove last ray if it's 360 degrees

        for (int i = 0 ; i < rayCount ; i++) {
            rays.add(new RayCast(
                    getTarget(startRange,currentRotationRad),
                    getTarget(range,currentRotationRad)
            ));
            currentRotationRad += fieldAnglePartitionRad;
            MapSimulatorInfo.addRayCount();
        }


        // after all the rays are calculated, we can do some stuff



        //rays.add(new RayCast(getLocationAgent(),getTarget(getRadiusAgent(),getRange(),getRotation())));
        //rayCount++;
        //System.out.println("Number of rays: "+rayCount);
    }

    public Vector2[] beginPointRay() {
        Vector2[] beginPoints = new Vector2[rayCount];
        for (int i = 0 ; i < rayCount ; i++){
            beginPoints[i] = rays.get(i).getStartRay();
        }
        return beginPoints;
    }

    public Vector2[] endPointRay() {
        Vector2[] endPoints = new Vector2[rayCount];
        for (int i = 0 ; i < rayCount ; i++){
            endPoints[i] = rays.get(i).getDrawRay();
        }
        return endPoints;
    }

    public Vector2 getTarget(float range, double rotation) {
        float x = (float)Math.cos(rotation);
        float y = (float)Math.sin(rotation);
        x = x * range;
        y = y * range;

        Vector2 target = new Vector2(getLocationAgent());
        target.add(x,y);

        return target;
    }



    public void setViewingAngle(float viewingAngle) {
        this.viewingAngle = viewingAngle;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setLocationAgent(Vector2 locationAgent) {
        this.locationAgent = locationAgent;
    }

    public void setRotationRad(double rotation) {
        this.rotationRad = rotation;
    }

    public Vector2 getLocationAgent() {
        return locationAgent;
    }

    public void setFieldScanTypeMask(short fieldScanTypeMask) {
        this.fieldScanTypeMask = fieldScanTypeMask;
    }

    public void setFieldReturnTypeMask(short fieldReturnTypeMask) {
        this.fieldReturnTypeMask = fieldReturnTypeMask;
    }

    public void setStartRange(float startRange) {
        this.startRange = startRange;
    }

    public HashSet<Object> getCollisionObjects() {
        return collisionObjects;
    }

    public String getTypeOfField() {
        return typeOfField;
    }

    public void setTypeOfField(String typeOfField) {
        this.typeOfField = typeOfField;
    }

    public void setFieldTransparentTypeMask(short fieldTransparentTypeMask) {
        this.fieldTransparentTypeMask = fieldTransparentTypeMask;
    }
}

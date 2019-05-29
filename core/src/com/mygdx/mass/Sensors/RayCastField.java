package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.mass.World.WorldObject;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

public class RayCastField { // each agent has a RayCastField

    protected MASS mass;
    protected World world;

    private ArrayList<RayCast> rays = new ArrayList<RayCast>();

    public static final int MAX_RAYS = 50;

    private int rayCount;
    private float radiusAgent, viewingAngle, range;
    private double rotation; // radian
    private Vector2 locationAgent;

    public RayCastField (MASS mass){
        this.mass = mass;
        this.world = mass.world;

    }

    class RayCast { // each ray within a RayCastField
        private Vector2 startRay, endRay, drawRay;
        private ArrayList<RayCollision> rayCollisions;


        public RayCast(Vector2 start, Vector2 end) {
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
                    rayCollisions.add(new RayCollision(fraction,fixture,new Vector2(point)));
                    //collision.set(point);
                    return 1;
                }
            };

            world.rayCast(callback,startRay,endRay); // callback is called multiple times until it's done. No call is also possible
            if (rayCollisions.isEmpty()) {
                drawRay = endRay; // nothing is detected, but we still want to draw
            }
            else {
                // search for the closest collision
                float fract = 1.1f;
                int counFor = 0;
                int saveCount = 0;
                for(RayCollision rayCollision : rayCollisions) {
                    if (rayCollision.getFraction() < fract) {
                        closestCollision = new RayCollision(rayCollision.getFraction(),rayCollision.getFixture(),rayCollision.getPoint());
                        fract = rayCollision.getFraction();

                        //System.out.println(fract);
                        saveCount = counFor;
                    }
                    counFor++;
                }

                if (closestCollision != null) { // do something with the closest object
                    drawRay = new Vector2(closestCollision.getPoint()) ;
                    //System.out.println("Collision detected with fixture of BitMaskType: "+Integer.toString(closestCollision.getFixture().getFilterData().categoryBits,2));
                }
                else {
                    System.out.println("ERROR IN CLOSEST COLLISION!");
                    drawRay = endRay;
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
        // smarty pants distribution of rays, for now a single ray
        rayCount = MAX_RAYS;
        double angle = Math.toRadians(getViewingAngle());
        double anglePartition = angle / (rayCount-1);
        double rotation = getRotation();
        double startRotation = rotation - (angle / 2);
        double currentRotation = startRotation;

        for (int i = 0 ; i < rayCount ; i++) {
            rays.add(new RayCast(
                    getTarget(getRadiusAgent(),0.000001f,currentRotation),
                    getTarget(getRadiusAgent(),getRange(),currentRotation)
            ));
            currentRotation += anglePartition;
        }

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

    public Vector2 getTarget(float radius, float range, double rotation) {
        float x = (float)Math.cos(rotation);
        float y = (float)Math.sin(rotation);
        float multiplier = radius + range;
        x = x * multiplier;
        y = y * multiplier;

        Vector2 target = new Vector2(getLocationAgent());
        target.add(x,y);

        return target;
    }


    public void setRadiusAgent(float radiusAgent) {
        this.radiusAgent = radiusAgent;
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

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public float getRadiusAgent() {
        return radiusAgent;
    }

    public float getViewingAngle() {
        return viewingAngle;
    }

    public float getRange() {
        return range;
    }

    public double getRotation() {
        return rotation;
    }

    public Vector2 getLocationAgent() {
        return locationAgent;
    }

    public ArrayList<RayCast> getRays() {
        return rays;
    }
}

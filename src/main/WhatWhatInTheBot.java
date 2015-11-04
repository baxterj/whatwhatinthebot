package main;

import java.util.Map;

import java.util.HashMap;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

public class WhatWhatInTheBot extends AdvancedRobot {

    boolean initializeState;
    int direction = 1;
    String targetName;
    
    Map<String, Enemy> enemies = new HashMap<>();
    
    public void run() {
        setAdjustRadarForGunTurn(true);
        setTurnRadarLeft(Double.POSITIVE_INFINITY);
        if (initializeState) { // panic mode until we find a bot
            setAhead(1000);
            setTurnRight(90);
            waitFor(new TurnCompleteCondition(this));
        } else {
            // NADA
        }
        while (true) {
            doMove();
            doRadar();
            doGun();
            
        }
    }
    
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    public void reverseDirection() {
        this.direction = -this.direction;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        this.initializeState = false;
        if (e.getEnergy() <= 0) {
            this.targetName = null;
        }
        if (targetName == null) {
            this.targetName = e.getName();
        } else if (e.getName().equals(this.targetName)) {
            doMove(e);
            doGun(e);
        }
        
    }
    
    public void doMove() {
        Enemy e = getEnemy();
        setAhead(e.getDistance() * this.direction);
        setTurnRight(e.getBearing());
        
        
    }

    private Enemy getEnemy() {
        // TODO Auto-generated method stub
        return null;
    }

    public void doGun(ScannedRobotEvent e) {
        if (e.getDistance() < 1000 && e.getDistance() > 150) {
            fire((1000 - e.getDistance()) / Rules.MAX_BULLET_POWER);
        } else if (e.getDistance() >= 150) {
            fire(Rules.MAX_BULLET_POWER);
        }
    }
    
    
    public class Enemy {
        double bearing;
        double velocity;
        double energy;
        String name;
        double distance;

        public Enemy(ScannedRobotEvent e){
            bearing = e.getBearing();
            velocity = e.getVelocity();
            energy = e.getEnergy();
            name = e.getName();
            distance = e.getDistance();
        }
        
        public void update(ScannedRobotEvent e) {
            
        }

        public double getBearing() {
            return bearing;
        }

        public double getVelocity() {
            return velocity;
        }

        public double getEnergy() {
            return energy;
        }

        public String getName() {
            return name;
        }

        public double getDistance() {
            return distance;
        }
    }

}

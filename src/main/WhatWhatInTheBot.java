package main;

import java.awt.Graphics2D;

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
            execute();
        }
    }
    
    private void doRadar() {
        // TODO Auto-generated method stub
        
    }

    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    public void reverseDirection() {
        this.direction = -this.direction;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
//        this.initializeState = false;
//        if (e.getEnergy() <= 0) {
//            this.targetName = null;
//        }
//        if (targetName == null) {
//            this.targetName = e.getName();
//        } else if (e.getName().equals(this.targetName)) {
//            doMove();
//            doGun(e);
//        }
        if (e.getEnergy() <= 0) {
            enemies.remove(e.getName());
        } else {
            if(enemies.containsKey(e.getName())) {
                Enemy enemy = enemies.get(e.getName());
                enemy.update(e, this);
            }else {
                enemies.put(e.getName(), new Enemy(e));
            }
        }
    }
    
    public void doMove() {
        Enemy e = getEnemy();
        if(e != null) {
            setAhead(e.getDistance() * this.direction);
            setTurnRight(e.getBearing());
        }
        
        
    }

    private Enemy getEnemy() {
        return enemies.get(selectOptimumTarget()); 
    }


    public void doGun() {
//        if (e.getDistance() < 1000 && e.getDistance() > 150) {
//            fire((1000 - e.getDistance()) / Rules.MAX_BULLET_POWER);
//        } else if (e.getDistance() >= 150) {
//            fire(Rules.MAX_BULLET_POWER);
//        }
    }
    
    public void onPaint(Graphics2D g) {
        for(Enemy enemy : enemies.values()) {
            enemy.draw(g, getTime());
        }
    }
    
    public String selectOptimumTarget() {
        String bestTarget = null;
        double bestScore = 0;
        
        for (String name : enemies.keySet()) {
            Enemy enemy = enemies.get(name);
            if (enemy.getEnergy() > 0) { // only consider alive robots
                double score = enemy.getDistance() * enemy.getEnergy();// TODO scale energy and distance
                if (score < bestScore) {
                    bestScore = score;
                    bestTarget = name;
                }
            }
            
        }
        return bestTarget;
    }
    
    public class Enemy {
        double bearing;
        double velocity;
        double energy;
        final String name;
        double distance;
        
        double x;
        double y;

        public Enemy(ScannedRobotEvent e){
            bearing = e.getBearing();
            velocity = e.getVelocity();
            energy = e.getEnergy();
            name = e.getName();
            distance = e.getDistance();
        }
        
        public void draw(Graphics2D g, long time) {
            
            g.fillRect((int)x - 20, (int)y - 20, 40, 40);
            
        }

        public void update(ScannedRobotEvent e, AdvancedRobot whatWhatInTheBot) {
            this.bearing = e.getBearing();
            this.velocity = e.getVelocity();
            this.energy = e.getEnergy();
            this.distance = e.getDistance();
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
        
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
    }

}

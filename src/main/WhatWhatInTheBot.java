package main;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class WhatWhatInTheBot extends AdvancedRobot {

    private static final int MOVE_STRATEGY_THRESHOLD = 3;
    int direction = 1;
    String targetName;
    double moveAmount;
    
    Map<String, Enemy> enemies = new HashMap<>();
    
    public void run() {
        setAdjustRadarForGunTurn(true);
        setTurnRadarLeft(Double.POSITIVE_INFINITY);
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        while (true) {
            doMove();
            doGun();
            execute();
        }
    }

    public void onHitWall(HitWallEvent e) {
        if (enemies.size() > MOVE_STRATEGY_THRESHOLD) {
            this.direction = -this.direction;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
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
        if (enemies.size() > MOVE_STRATEGY_THRESHOLD) {
            ahead(moveAmount);
            turnRight(90);
        } else {
            Enemy e = getEnemy();
            if(e != null) {
                setAhead(e.getDistance() * this.direction);
                setTurnRight(e.getBearing());
            }
        }
    }

    private Enemy getEnemy() {
        return enemies.get(selectOptimumTarget());
    }


    public void doGun() {
//        Enemy e = getEnemy();
//        if (e != null) {
//            if (e.getDistance() < 1000 && e.getDistance() > 150) {
//                fire((1000 - e.getDistance()) / Rules.MAX_BULLET_POWER);
//            } else if (e.getDistance() >= 150) {
//                fire(Rules.MAX_BULLET_POWER);
//            }
//        }
        
    }
    
    public void onPaint(Graphics2D g) {
        for(Enemy enemy : enemies.values()) {
            enemy.draw(g, getTime());
        }
    }
    
    public String selectOptimumTarget() {
        String bestTarget = null;
        double bestScore = Double.MAX_VALUE;
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

package main;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

public class WhatWhatInTheBot extends AdvancedRobot {

    boolean initializeState;
    int direction = 1;
    String targetName;
    
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
    
    public void doMove(ScannedRobotEvent e) {
        setAhead(e.getDistance() * this.direction);
        setTurnRight(e.getBearing());
        
        
    }

    public void doGun(ScannedRobotEvent e) {
        if (e.getDistance() < 1000 && e.getDistance() > 150) {
            fire((1000 - e.getDistance()) / Rules.MAX_BULLET_POWER);
        } else if (e.getDistance() >= 150) {
            fire(Rules.MAX_BULLET_POWER);
        }
    }
}

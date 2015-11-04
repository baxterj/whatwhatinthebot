package main;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

public class WhatWhatInTheBot extends AdvancedRobot {

    boolean initializeState;
    
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

    public void onScannedRobot(ScannedRobotEvent e) {
        this.initializeState = false;
        
        setAhead(e.getDistance());
        setTurnRight(e.getBearing());
        
        if (e.getDistance() < 1000 && e.getDistance() > 150) {
            fire((1000 - e.getDistance()) / Rules.MAX_BULLET_POWER);
        } else if (e.getDistance() >= 150) {
            fire(Rules.MAX_BULLET_POWER);
        }
    }
}

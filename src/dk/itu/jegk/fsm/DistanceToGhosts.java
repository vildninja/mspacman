/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk.fsm;

import dk.itu.jegk.PacBehave;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.internal.Ghost;

/**
 *
 * @author Jannek
 */
public class DistanceToGhosts extends FSM.Edge {

    @Override
    public boolean Evaluate() {
        Game game = PacBehave.game;
        int pac = game.getPacmanCurrentNodeIndex();
        int shortest = 10000;
        Constants.GHOST closest = Constants.GHOST.BLINKY;
        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            int d = game.getShortestPathDistance(pac, game.getGhostCurrentNodeIndex(ghost));
            if (d < shortest) {
                shortest = d;
                closest = ghost;
            }
        }
        
        return shortest > value;
    }

    public DistanceToGhosts(double value, FSM.State state) {
        super(value, state);
    }
    
    
}

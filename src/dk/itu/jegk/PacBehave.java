/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import dk.itu.jegk.fsm.FSM;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class PacBehave extends Controller<Constants.MOVE> {

    public static Game game;
    
    private final FSM machine;

    public PacBehave() {
        machine = new FSM(null);
    }
    
    
    
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return Constants.MOVE.NEUTRAL;
    }
    
}

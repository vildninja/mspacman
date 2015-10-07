/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import dk.itu.jegk.fsm.FSM;
import dk.itu.jegk.mcts.MonteCarlo;
import java.util.Comparator;
import java.util.List;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class TestController extends Controller<Constants.MOVE>
{

    private MapAnalyzer analyzer = null;
    
    private final float[] factors;
    
    public TestController() {
        factors = new float[7];
        factors[0] = -0.1f; // steps
        factors[1] = 10f; // pills
        factors[2] = 1f; // exits
        factors[3] = 5f; // ghost #1
        factors[4] = 3f; // ghost #2
        factors[5] = 2f; // ghost #3
        factors[6] = 1f; // ghost #4
    }
    
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        
        
        MonteCarlo<ActionSimulator> mc = new MonteCarlo<>(ActionSimulator.GetRoot(game));
        
        int counter = 0;
        while (System.currentTimeMillis() < timeDue - 5)
        {
            counter++;
            
            ActionSimulator as = mc.FindBest();
            List<ActionSimulator> actions = as.BranchOut();
            
            for (ActionSimulator a : actions)
            {
                a.SimulateToJunction(game.getTotalTime() + 100);
            }
            
            mc.ExpandCurrent(actions);
        }
        
        ActionSimulator action = mc.GetAction();
        Constants.MOVE move = action != null ? action.firstMove : Constants.MOVE.NEUTRAL;
        
        System.out.println("Terminating " + counter + " move " + move);
        
        return move;
    }
}

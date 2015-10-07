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
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class TestController extends Controller<Constants.MOVE>
{

    private MapAnalyzer analyzer = null;
    
    private final double[] factors;
    
    public TestController() {
        factors = new double[7];
        factors[0] = -0.1; // steps
        factors[1] = 10; // pills
        factors[2] = 1; // exits
        factors[3] = 5; // ghost #1
        factors[4] = 3; // ghost #2
        factors[5] = 2; // ghost #3
        factors[6] = 1; // ghost #4
    }
    
    public static void main(String[] args) {
        Executor exe = new Executor();
        
        TestController controller = new TestController();
        
        exe.runExperiment(controller, new StarterGhosts(), 10);
    }
    
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        
        
        MonteCarlo<ActionSimulator> mc = new MonteCarlo<>(ActionSimulator.GetRoot(game), 0.01f);
        
        int startTime = game.getTotalTime();
        
        int counter = 0;
        while (System.currentTimeMillis() < timeDue - 3)
        {
            counter++;
            
            ActionSimulator as = mc.FindBest();
            List<ActionSimulator> actions = as.BranchOut();
            
            for (ActionSimulator a : actions)
            {
                a.SimulateToJunction(100, startTime);
            }
            
            mc.ExpandCurrent(actions);
        }
        
        ActionSimulator action = mc.GetAction();
        Constants.MOVE move = action != null ? action.firstMove : Constants.MOVE.NEUTRAL;
        
       System.out.println("Terminating " + counter + " move " + move);
        
        return move;
    }
}

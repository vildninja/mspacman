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
    private final Genotype genotype;

    public TestController(Genotype genotype) {
        this.genotype = genotype;
    }

    public TestController() {
        genotype = Genotype.CreateBest44();
    }
    
    
    
    public static void main(String[] args) {
        Executor exe = new Executor();
        
        TestController controller = new TestController();
        
        exe.runExperiment(controller, new StarterGhosts(), 10);
    }
    
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        
        
        MonteCarlo<ActionSimulator> mc = new MonteCarlo<>(ActionSimulator.GetRoot(game, genotype), 0.01 * genotype.geno[0]);
        
        int startTime = game.getTotalTime();
        
        int counter = 0;
        while (System.currentTimeMillis() < timeDue - 3)
        {
            counter++;
            
            ActionSimulator as = mc.FindBest();
            List<ActionSimulator> actions = as.BranchOut();
            
            for (ActionSimulator a : actions)
            {
                a.SimulateToJunction(Math.round((float)(100 * genotype.geno[1])), startTime);
            }
            
            mc.ExpandCurrent(actions);
        }
        
        ActionSimulator action = mc.GetAction();
        Constants.MOVE move = action != null ? action.firstMove : Constants.MOVE.NEUTRAL;
        
        //System.out.println("Terminating " + counter + " move " + move);
        
        return move;
    }
}

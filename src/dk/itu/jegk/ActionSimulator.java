/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.awt.Color;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 *
 * @author Jannek
 */
public class ActionSimulator implements CalculatedMove {

    private final Game game;
    public final Constants.MOVE firstMove;
    private Game junction;
    
    private int score = 0;
    private boolean isDead = false;
    private int distanceToGhosts = 0;
    private int survived = 0;
    private int timeWhithoutPills = 0;
    
    public ActionSimulator(Game game, Constants.MOVE move)
    {
        this.game = game;
        firstMove = move;
    }
    
    public static ActionSimulator GetRoot(Game game)
    {
        ActionSimulator as = new ActionSimulator(game, MOVE.NEUTRAL);
        as.junction = game;
        return as;
    }
    
    public List<ActionSimulator> BranchOut()
    {
        List<ActionSimulator> branches = new LinkedList<>();
        
        if (junction == null)
            return branches;
        
        for (Constants.MOVE move : junction.getCurrentMaze().graph[junction.getPacmanCurrentNodeIndex()].neighbourhood.keySet())
        {
            branches.add(new ActionSimulator(junction.copy(), move));
        }
        return branches;
    }
    
    public void SimulateToJunction(int rounds)
    {
        Controller<EnumMap<GHOST,MOVE>> ghosts = new StarterGhosts();
        Controller<MOVE> pacman = new StarterPacMan();
        
        boolean junctionFound = false;
        int lives = game.getPacmanNumberOfLivesRemaining();
        int lastIndex = game.getPacmanCurrentNodeIndex();
        
        int currentTimeWithoutPills = 0;
        
        MOVE move = firstMove;
        
        while (game.getTotalTime() < rounds || !junctionFound) {
            game.advanceGame(junctionFound ? pacman.getMove(game, 0) : move, ghosts.getMove(game, 0));
            
            if (game.getPacmanNumberOfLivesRemaining() < lives)
            {
                isDead = true;
                break;
            }
            lives = game.getPacmanNumberOfLivesRemaining();
            
            if (!junctionFound)
            {
                if (game.isJunction(game.getPacmanCurrentNodeIndex()))
                {
                    junction = game.copy();
                    junctionFound = true;
                    
                    distanceToGhosts = 400;
                    for (GHOST ghost : GHOST.values())
                    {
                        int g = game.getGhostCurrentNodeIndex(ghost);
                        int dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), g);
                        if (dist < distanceToGhosts)
                            distanceToGhosts = dist;
                    }
                }
                else
                {
                    // update move to follow corners
                    EnumMap<MOVE, MOVE[]> map = game.getCurrentMaze().graph[game.getPacmanCurrentNodeIndex()].allPossibleMoves;
                    
                    if (map.containsKey(move))
                    {
                        move = map.get(move)[0];
                    }
                    /**
                    else
                    {
                        GameView.addPoints(game, Color.yellow, game.getPacmanCurrentNodeIndex());
                        GameView.addPoints(game, Color.red, lastIndex);
                        String str = "";
                        for (Map.Entry<MOVE, MOVE[]> possible : map.entrySet()) {
                            MOVE key = possible.getKey();
                            MOVE[] value = possible.getValue();
                            str += " " + key;
                        }
                        System.out.println("Move " + move + " possible" + str + " " + lives + " " + game.getPacmanNumberOfLivesRemaining());
                        isDead = true;
                        break;
                    }
                    /**/
                    lastIndex = game.getPacmanCurrentNodeIndex();
                }
            }
            
            survived++;
            if (score == game.getScore())
            {
                currentTimeWithoutPills++;
                if (currentTimeWithoutPills > timeWhithoutPills)
                    timeWhithoutPills = currentTimeWithoutPills;
            }
            score = game.getScore();
        }
    }
    
    @Override
    public float getValue() {
        if (isDead)
            return score - 1000 + survived;
        return score + survived + distanceToGhosts;
    }
    
    
    
}

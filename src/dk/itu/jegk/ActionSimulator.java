/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class ActionSimulator implements CalculatedMove {

    private final Game game;
    public final Constants.MOVE firstMove;
    private Game junction;
    
    private int score = 0;
    private double scoreTimeDamped;
    private boolean isDead = false;
    private boolean nextLevel = false;
    private int distanceToGhosts = 0;
    private int survived = 0;
    private int timeWithoutPills = 0;
    
    private final Genotype geno;
    
    public ActionSimulator(Game game, Constants.MOVE move, Genotype geno)
    {
        this.game = game;
        this.geno = geno;
        firstMove = move;
    }
    
    public static ActionSimulator GetRoot(Game game, Genotype geno)
    {
        ActionSimulator as = new ActionSimulator(game, MOVE.NEUTRAL, geno);
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
            branches.add(new ActionSimulator(junction.copy(), move, geno));
        }
        return branches;
    }
    
    public void SimulateToJunction(int rounds, int startTime)
    {
        Controller<EnumMap<GHOST,MOVE>> ghosts = new StarterGhosts();
        Controller<MOVE> pacman = new StarterPacMan();
        
        boolean junctionFound = false;
        int lives = game.getPacmanNumberOfLivesRemaining();
        int lastIndex = game.getPacmanCurrentNodeIndex();
        int level = game.getCurrentLevel();
        
        int currentTimeWithoutPills = 0;
        
        MOVE move = firstMove;
        
        while (game.getTotalTime() < startTime + rounds) {
            game.advanceGame(junctionFound ? pacman.getMove(game, 0) : move, ghosts.getMove(game, 0));
            
            if (game.getPacmanNumberOfLivesRemaining() < lives)
            {
                isDead = true;
                break;
            }
            lives = game.getPacmanNumberOfLivesRemaining();
            
            if (game.getCurrentLevel() != level)
            {
                nextLevel = true;
                System.out.println("NEXT LEVEL!");
                break;
            }
            
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
                timeWithoutPills++;
            }
            scoreTimeDamped += (game.getScore() - score) * Math.pow(1 - survived/(double)rounds, 2);
            score = game.getScore();
        }
    }
    
    private static final int SCORE = 2;
    private static final int SCORE_DAMPED = 3;
    private static final int SURVIVED = 4;
    private static final int DEATH = 5;
    private static final int NEXT_LEVEL = 6;
    private static final int NEXT_LEVEL_SURVIVED = 7;
    
    @Override
    public double getValue() {
        
        if (junction == null && !nextLevel)
            return -10000;
        
        double result = 0;
        result += score;
        result += scoreTimeDamped;
        result += survived;
        //if (distanceToGhosts < 1)
            //result -= 100;
        if (isDead)
            result -= 1000;
        if (nextLevel)
            result += 10000 - survived * 10;
        //result -= timeWithoutPills;
        
        return result;
//        if (isDead)
//            return score - 1000 + survived;
//        return score + survived + distanceToGhosts;
    }
    
    
    
}

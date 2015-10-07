/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

/**
 *
 * @author Jannek
 */
public class MapAnalyzer {
    
    private Game game;
    private Maze maze;
    private final int[] visited;
    public final int[] steps;
    public final int[] pills;
    
    private final GhostMovement[] ghosts;
    private final Constants.GHOST[] gid;
    
    private int currentStep = 0;
    
    private static final boolean debug = true;
    
    private class GhostMovement
    {
        public int current;
        public Constants.MOVE move = Constants.MOVE.NEUTRAL;
        public int nextJunction;
        public int distance;
    }
    
    public class Move implements CalculatedMove
    {
        public final Node node;
        public final Constants.MOVE move;
        public final int step;
        public final int pill;
        public final int exits;
        public final int[] distances;
        
        public float value;
        public boolean evalueted = false;
        
        public Move(Node node, Constants.MOVE move)
        {
            this.node = node;
            this.move = move;
            step = steps[node.nodeIndex];
            pill = pills[node.nodeIndex];
            exits = node.numNeighbouringNodes;
            
            distances = new int[4];
            for (int i = 0; i < 4; i++)
            {
                distances[i] = game.getShortestPathDistance(node.nodeIndex,
                        game.getGhostCurrentNodeIndex(gid[i])) - step;
            }
            Arrays.sort(distances);
        }
        
        public Move()
        {
            node = maze.graph[game.getPacmanCurrentNodeIndex()];
            move = Constants.MOVE.NEUTRAL;
            
            step = 0;
            pill = 0;
            exits = 0;
            distances = new int[0];
            
            evalueted = true;
            value = 0;
        }
        
        public float Evaluate(float[] factors)
        {
            value = step * factors[0] +
            pill * factors[1] +
            exits * factors[2] +
            Math.max(distances[0], 10) * factors[3] +
            Math.max(distances[1], 10) * factors[4] +
            Math.max(distances[2], 10) * factors[5] +
            Math.max(distances[3], 10) * factors[6];
            evalueted = true;
            return value;
        }

        @Override
        public double getValue() {
            return value;
        }
        
        
    }
    
    
    public MapAnalyzer(Game game)
    {
        this.game = game;
        maze = game.getCurrentMaze();
        visited = new int[maze.graph.length];
        steps = new int[maze.graph.length];
        pills = new int[maze.graph.length];
        
        gid = Constants.GHOST.values();
        ghosts = new GhostMovement[gid.length];
        for (int i = 0; i < gid.length; i++) {
            ghosts[i] = new GhostMovement();
            ghosts[i].nextJunction = game.getGhostInitialNodeIndex();
            ghosts[i].distance = game.getGhostLairTime(gid[i]);
        }
    }
    
    public Move NextRound(Game game)
    {
        this.game = game;
        maze = game.getCurrentMaze();
        currentStep++;
        
        for (int i = 0; i < gid.length; i++) {
            ghosts[i].current = game.getGhostCurrentNodeIndex(gid[i]);
            /**
            // TODO calculate ghosts
            if (--ghosts[i].distance < 0)
            {
                ghosts[i].current = game.getGhostCurrentNodeIndex(gid[i]);
                Node node = maze.graph[ghosts[i].nextJunction];
                Constants.MOVE move = null;
                outer:
                for (Map.Entry<Constants.MOVE, int[]> entry : node.allNeighbouringNodes.entrySet()) {
                    int[] value = entry.getValue();
                    for (int j = 0; j < value.length; j++) {
                        if (value[j] == ghosts[i].current){
                            move = entry.getKey();
                            break outer;
                        }
                    }
                }
                
                if (move == null)
                {
                    ghosts[i].nextJunction = ghosts[i].current;
                    continue;
                }
                
                node = maze.graph[ghosts[i].current];
                while (node.numNeighbouringNodes == 2)
                {
                    // this might be error prone
                    node = maze.graph[node.allNeighbouringNodes.get(move)[0]];
                }
                ghosts[i].nextJunction = node.nodeIndex;
                ghosts[i].distance = game.getShortestPathDistance(ghosts[i].current, node.nodeIndex);
            }
            
            if (debug)
                GameView.addLines(game, Color.yellow, ghosts[i].nextJunction, ghosts[i].current);
            /**/
        }
        
        return new Move();
    }
    
    public ArrayList<Move> TraversePaths(Node node)
    {
        ArrayList<Move> list = new ArrayList<>();
        
        int start = node.nodeIndex;
        
        if (visited[start] != currentStep)
        {
            visited[start] = currentStep;
            steps[start] = 0;
            pills[start] = PointsForPill(node);
        }
        
        for (Map.Entry<Constants.MOVE, Integer> entry : maze.graph[start].neighbourhood.entrySet()) 
        {
            int value = entry.getValue();
            if (visited[value] == currentStep)
                continue;
            
            Constants.MOVE dir = entry.getKey();
            
            Node junction = FindNextJunction(maze.graph[value], dir, steps[start] + 1, pills[start]);
            if (junction == null)
                continue;
            Move move = new Move(junction, dir);
            
            if (debug)
                GameView.addLines(game, Color.yellow, start, move.node.nodeIndex);
            
            list.add(move);
        }
        
        return list;
    }
    
    private int PointsForPill(Node node)
    {
        if (node.pillIndex >= 0)
            return game.isPillStillAvailable(node.pillIndex) ? 1 : 0;
        if (node.powerPillIndex >= 0)
            return game.isPowerPillStillAvailable(node.powerPillIndex) ? 20 : 0;
        return 0;
    }
    
    private Node FindNextJunction(Node start, Constants.MOVE move, int step, int pill)
    {
        // not pretty, but simple way to avoid adding one junction multiple times
        if (visited[start.nodeIndex] == currentStep)
            return null;
        
        visited[start.nodeIndex] = currentStep;
        steps[start.nodeIndex] = step + 1;
        pills[start.nodeIndex] = pill + PointsForPill(start);
        
        if (start.numNeighbouringNodes != 2)
            return start;
        
        move = start.allPossibleMoves.get(move)[0];
        
        return FindNextJunction(maze.graph[start.neighbourhood.get(move)],
                move,
                steps[start.nodeIndex],
                pills[start.nodeIndex]);
    }
}

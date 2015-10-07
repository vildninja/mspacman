/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.pew.pacman;

import java.util.ArrayList;
import java.util.HashMap;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

/**
 *
 * @author Jannek
 */
public class BetterGraph
{
    public class Point {
        
    }
    
    public class Edge extends Point {
        private ArrayList<Node> nodes;
        
        public void addNode(Node node)
        {
            
        }
    }
    
    HashMap<Integer, Edge> edges;
    public BetterGraph(Maze maze) {
        
        edges = new HashMap<>();
        Edge edge = null;
        for (int i = 0; i < maze.graph.length; i++) {
            Node n = maze.graph[i];
            if (n.numNeighbouringNodes <= 2)
            {
                for (int ni : n.neighbourhood.values())
                    if (edges.containsKey(ni))
                        edge = edges.get(ni);
                if (edge == null)
                edge = new Edge();
                edge.addNode(n);
                edges.put(i, edge);
            }
        }
    }
    
}

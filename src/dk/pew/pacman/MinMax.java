/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.pew.pacman;

import java.util.ArrayList;
import pacman.game.Constants;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class MinMax {
    public ArrayList<MinMax> branches;
    public int value;
    public int pac;
    public int[] ghosts;
    
    public MinMax(int val, int p, int[] gs)
    {
        pac = p;
        ghosts = gs;
        value = val;
    }
    
    public void Compute(Game game, boolean myTurn)
    {
        branches = new ArrayList<>(myTurn ? 4 : 16);
        if (myTurn)
        {
            for (int n : game.getNeighbouringNodes(pac))
            {
                for (int g : ghosts)
                {
                    int v = game.getShortestPathDistance(n, g);
                    branches.add(new MinMax(v, n, ghosts));
                }
            }
        }
        else
        {
            for (int g : ghosts)
            {
                int localMin = 10000;
                for (int n : game.getNeighbouringNodes(g))
                {
                    int v = game.getShortestPathDistance(n, g);
                    if (v < localMin)
                    {
                        localMin = v;
                    }
                }
            }
        }
    }
}

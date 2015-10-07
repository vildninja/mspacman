/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk.mcts;

import dk.itu.jegk.CalculatedMove;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jannek
 */
public class MonteCarlo<E extends CalculatedMove>
{
    public class TreeNode
    {
        public final TreeNode parent;
        public List<TreeNode> children;
        
        private double value;
        
        public int tries = 1;
        
        public final E item;
        
        public TreeNode(E item, TreeNode parent)
        {
            this.item = item;
            this.parent = parent;
            value = item.getValue();
            children = null;
        }
        
        public void Backpropagate(double value)
        {
            tries++;
            
            if (value > this.value)
                this.value = value;
            
            if (parent != null)
                parent.Backpropagate(getValue(true));
        }
        
        public void Expand(List<E> items)
        {
            if (items.isEmpty())
            {
                Backpropagate(Double.NEGATIVE_INFINITY);
                return;
            }
            children = new ArrayList<>(items.size());
            for (E i : items)
            {
                TreeNode tn = new TreeNode(i, this);
                Backpropagate(tn.getValue());
                children.add(tn);
            }
        }
        
        public double getValue()
        {
            return getValue(false);
        }
        
        public double getValue(boolean ignorePolicy)
        {
            if (ignorePolicy)
                return value;
            return value + c * Math.sqrt(2*Math.log(parent.tries)/tries);
        }
        
        public TreeNode Select()
        {
            if (children == null || children.isEmpty())
                return this;
            
            TreeNode best = children.get(0);
            for (int i = 1; i < children.size(); i++)
            {
                if (children.get(i).getValue() > best.getValue())
                    best = children.get(i);
            }
            
            return best.Select();
        }
    }
    
    private final TreeNode root;
    private TreeNode current;
    private final double c;
    
    public MonteCarlo(E rootItem, double c)
    {
        this.c = c;
        root = new TreeNode(rootItem, null);
    }
    
    public double RootScore()
    {
        return root.getValue(true);
    }
    
    public E FindBest()
    {
        current = root.Select();
        
        return current.item;
    }
    
    public void ExpandCurrent(List<E> items)
    {
        current.Expand(items);
        current = null;
    }
    
    public E GetAction()
    {
        if (root.children == null || root.children.isEmpty())
            return null;
        
        TreeNode best = root.children.get(0);
        for (int i = 1; i < root.children.size(); i++)
        {
            if (root.children.get(i).getValue(true) > best.getValue(true))
                best = root.children.get(i);
        }

        return best.item;
    }
}

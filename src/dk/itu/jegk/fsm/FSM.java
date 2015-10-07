/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk.fsm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Jannek
 */
public class FSM
{
    public static abstract class State {
        public final ArrayList<Edge> edges = new ArrayList<>();
        public abstract void Enter();
        public void Exit() { }
    }
    
    public static abstract class Edge {
        public double value;
        public final State state;
        public abstract boolean Evaluate();

        public Edge(double value, State state) {
            this.value = value;
            this.state = state;
        }
    }
    
    private final ArrayList<Edge> edges;
    public State current;
    public final State first;

    public FSM(State first) {
        this.edges = new ArrayList<>();
        this.first = first;
        current = null;
        
        // traverse fsm, to find all edges
        Queue<State> states = new LinkedList<>();
        HashSet<State> visited = new HashSet<>();
        states.add(first);
        while (!states.isEmpty()) {
            State s = states.poll();
            if (!visited.add(s))
                continue;
            
            for (Edge e : s.edges) {
                edges.add(e);
                states.add(s);
            }
        }
    }
    
    public void Reset()
    {
        current = null;
    }
    
    public void Execute() {
        if (current == null) {
            current = first;
            current.Enter();
        }
        
        for (Edge edge : current.edges) {
            if (edge.Evaluate()) {
                current.Exit();
                current = edge.state;
                current.Enter();
                break;
            }
        }
    }
    
    public double[] GetValues() {
        double[] values = new double[edges.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = edges.get(i).value;
        }
        return values;
    }
    
    public void SetValues(double[] values) {
        for (int i = 0; i < values.length; i++) {
            edges.get(i).value = values[i];
        }
    }
    
    public void SetValue(int index, float value) {
        edges.get(index).value = value;
    }
}
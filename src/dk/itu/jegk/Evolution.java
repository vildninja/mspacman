/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import pacman.controllers.examples.StarterGhosts;
import static pacman.game.Constants.DELAY;
import pacman.game.Game;

/**
 *
 * @author Jannek
 */
public class Evolution {
    
    public static void main(String[] args) throws InterruptedException {
        new Evolution(30, 1000, 5, 4, 1, Genotype.CreateWithValue(8, 1));
    }
    
    public static final int MAX_TIME = 2000;
    
    public Evolution(int poolSize, int iterations, int evolve, int breed, int bottomMixIn, Genotype base) throws InterruptedException
    {
        if (poolSize % 2 == 1)
            poolSize++;
        
        List<Worker> workers = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            workers.add(new Worker(i > poolSize/2 ? Genotype.Evolve(base.geno, 0.9f) : Genotype.CreateBest44(), 4, MAX_TIME));
        }
        
        for (int iteration = 0; iteration < iterations; iteration++)
        {
            if (iteration > 0)
            {
                // mix stuff
                List<Worker> old = workers;
                Collections.sort(old, new Sorter());
                
                
                workers = new ArrayList<>(poolSize);
                
                for (int i = 0; i < 4; i++) {
                    workers.add(new Worker(old.get(i).geno, 4, MAX_TIME));
                }
                
                for (int i = 0; i < bottomMixIn; i++) {
                    old.add(0, old.remove(old.size() - 1));
                }
                
                for (int i = 2; i < poolSize / 2; i++) {
                    workers.add(new Worker(Genotype.Evolve(old.get(i%evolve).geno.geno, 0.1f), 4, MAX_TIME));
                    int a = i%breed;
                    int b = (int)(Math.random() * breed);
                    while (b == a && breed > 1)
                        b = (int)(Math.random() * breed);
                    workers.add(new Worker(Genotype.Breed(old.get(a).geno.geno, old.get(b).geno.geno), 4, MAX_TIME));
                }
            }
            
            for (int i = 0; i < poolSize; i++)
            {
                workers.get(i).start();
            }
            
            boolean isDone = false;
            while (!isDone)
            {
                Thread.sleep(2000);
                isDone = true;
                for (int i = 0; i < poolSize; i++) {
                    if (workers.get(i).isAlive())
                    {
                        isDone = false;
                        break;
                    }
                }
            }
            
            for (int i = 0; i < poolSize; i++)
            {
                System.out.println(iteration + " done: #" + i + " " + workers.get(i).score);
                workers.get(i).geno.SaveWithScore(workers.get(i).score, iteration + "_" + i + " " + ((int)workers.get(i).score) + ".txt");
            }
        }
    }
    
    public class Sorter implements Comparator<Worker>
    {

        @Override
        public int compare(Worker o1, Worker o2) {
            if (o1.score < o2.score)
                return 1;
            return -1;
        }
        
    }
    
    public class Worker extends Thread
    {
        public final Genotype geno;
        public final int runs;
        public final int maxTime;
        public double score;

        public Worker(Genotype geno, int runs, int maxTime) {
            this.geno = geno;
            this.runs = runs;
            this.maxTime = maxTime;
        }
        
        
        
        @Override
        public void run()
        {
            TestController controller = new TestController(geno);
            StarterGhosts ghost = new StarterGhosts();
            
            double avgScore = 0;
            Random rnd=new Random(0);
            Game game;

            for(int i=0;i<runs;i++)
            {
                    game=new Game(rnd.nextLong());

                    while(!game.gameOver() && game.getTotalTime() < maxTime)
                    {
                    game.advanceGame(controller.getMove(game.copy(),System.currentTimeMillis()+DELAY),
                                    ghost.getMove(game.copy(),System.currentTimeMillis()+DELAY));
                    }

                    avgScore += game.getScore();
                    //System.out.println(i+"\t"+game.getScore());
            }
            
            score = avgScore / runs;
        }
    }
}

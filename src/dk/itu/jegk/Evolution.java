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
        new Evolution(20, 1, 3, 3, 0, Genotype.CreateWithValue(8, 1));
    }
    
    public Evolution(int poolSize, int iterations, int evolve, int breed, int bottomMixIn, Genotype base) throws InterruptedException
    {
        if (poolSize % 2 == 1)
            poolSize++;
        
        List<Worker> workers = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            workers.add(new Worker(Genotype.Evolve(base.geno, 0.2f), 4, 1000));
        }
        
        for (int iteration = 0; iteration < iterations; iteration++)
        {
            if (iteration > 0)
            {
                // mix stuff
                List<Worker> old = workers;
                Collections.sort(old, new Sorter());
                
                workers = new ArrayList<>(poolSize);
                for (int i = 0; i < poolSize / 2; i++) {
                    workers.add(new Worker(Genotype.Evolve(old.get(i%evolve).geno.geno, 0.2f), 4, 1000));
                    int a = i%breed;
                    int b = i%(int)(Math.random() * 1000);
                    while (b == a && breed > 1)
                        b = i%(int)(Math.random() * 1000);
                    workers.add(new Worker(Genotype.Breed(old.get(a).geno.geno, old.get(b).geno.geno), 4, 1000));
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
                System.out.println("done: " + workers.get(i).score);
                workers.get(i).geno.SaveWithScore(workers.get(i).score, iteration + "_" + i + ".txt");
            }
        }
    }
    
    public class Sorter implements Comparator<Worker>
    {

        @Override
        public int compare(Worker o1, Worker o2) {
            if (o1.score > o2.score)
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
                    System.out.println(i+"\t"+game.getScore());
            }
            
            score = avgScore / runs;
        }
    }
}

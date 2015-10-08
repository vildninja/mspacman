/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.io.File;
import java.nio.file.Files;
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
        new Evolution(30, 1000, 5, 4, Genotype.CreateWithValue(8, 1));
    }
    
    public static final int MAX_TIME = 2000;
    
    public final List<Genotype> allGenotypes;
    
    public Evolution(int poolSize, int iterations, int evolve, int breed, Genotype base) throws InterruptedException
    {
        allGenotypes = new ArrayList<>();
        
        File dir = new File("Evolution");
        for (File f : dir.listFiles())
        {
            if (!f.getName().endsWith("txt"))
                continue;
            
            allGenotypes.add(Genotype.LoadFile(base.geno.length, f));
        }
        
        System.out.println("Loaded " + allGenotypes.size() + " from file!");
        
        while (allGenotypes.size() < Math.max(evolve, breed))
        {
            allGenotypes.add(Genotype.Evolve(base.geno, 0.9f));
        }
        
        if (poolSize % 2 == 1)
            poolSize++;
        
        List<Worker> workers = new ArrayList<>(poolSize);
        
        for (int iteration = 0; iteration < iterations; iteration++)
        {
            // mix stuff
            Collections.sort(allGenotypes, new Sorter(iteration, 0.92));
            workers.clear();

            for (int i = 0; i < 4; i++) {
                workers.add(new Worker(allGenotypes.get(i).Clone(), 4, MAX_TIME));
            }

            for (int i = 2; i < poolSize / 2; i++) {
                workers.add(new Worker(Genotype.Evolve(allGenotypes.get(i%evolve).geno, 0.1f), 4, MAX_TIME));
                int a = i%breed;
                int b = (int)(Math.random() * breed);
                while (b == a && breed > 1)
                    b = (int)(Math.random() * breed);
                workers.add(new Worker(Genotype.Breed(allGenotypes.get(a).geno, allGenotypes.get(b).geno), 4, MAX_TIME));
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
                Genotype g = workers.get(i).geno;
                System.out.println(iteration + " done: #" + i + " " + workers.get(i).score);
                g.SaveWithScore(workers.get(i).score, iteration + "_" + i + " " + ((int)workers.get(i).score) + ".txt");
                g.iteration = iteration + 1;
                allGenotypes.add(g);
            }
        }
    }
    
    public class Sorter implements Comparator<Genotype>
    {
        private final int iteration;
        private final double decay;

        public Sorter(int iteration, double decay) {
            this.iteration = iteration;
            this.decay = decay;
        }
        
        @Override
        public int compare(Genotype o1, Genotype o2) {
            if (o1.score * Math.pow(decay, iteration - o1.iteration) <
                    o2.score * Math.pow(decay, iteration - o2.iteration))
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

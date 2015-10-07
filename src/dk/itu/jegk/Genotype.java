/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;



/**
 *
 * @author Jannek
 */
public class Genotype
{
    public final double[] geno;
    public double score;
    
    private Genotype(int size)
    {
        geno = new double[size];
    }
    
    public static Genotype CreateWithValue(int size, double value)
    {
        Genotype child = new Genotype(size);
        for (int i = 0; i < size; i++) {
            child.geno[i] = value;
        }
        
        return child;
    }
    
    public static Genotype Evolve(double[] base, double change)
    {
        Genotype child = new Genotype(base.length);
        for (int i = 0; i < base.length; i++) {
            child.geno[i] = base[i] * (1 - change + Math.random() * change * 2);
        }
        
        return child;
    }
    
    public static Genotype Breed(double[] a, double[] b)
    {
        Genotype child = new Genotype(a.length);
        for (int i = 0; i < a.length; i++) {
            child.geno[i] = Math.random() > 0.5 ? a[i] : b[i];
        }
        
        return child;
    }
    
    public void SaveWithScore(double score, String name)
    {
        this.score = score;
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(name)), "UTF-8"));
            out.write(score + "\n");
            for (int i = 0; i < geno.length; i++) {
                out.write(geno[i] + "\n");
            }
            out.close();
        } catch (Exception ex) {
            System.out.println("Failed to write: ");
            System.out.println("score: " + score);
            for (int i = 0; i < geno.length; i++) {
                System.out.println(geno[i]);
            }
        }
    }
}

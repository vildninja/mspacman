/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.jegk;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Jannek
 */
public class Genotype
{
    public final double[] geno;
    public double score;
    public int iteration = 0;
    
    private Genotype(int size)
    {
        geno = new double[size];
    }
    
    public Genotype Clone()
    {
        Genotype child = new Genotype(geno.length);
        child.score = score;
        child.iteration = iteration;
        System.arraycopy(geno, 0, child.geno, 0, geno.length);
        return child;
    }
    
    public static Genotype CreateWithValue(int size, double value)
    {
        Genotype child = new Genotype(size);
        for (int i = 0; i < size; i++) {
            child.geno[i] = value;
        }
        
        return child;
    }
    
    public static Genotype CreateBestForTest()
    {
        Genotype child = new Genotype(8);
        int i = 0;
        child.geno[i++] = 0.1725553648254266;
        child.geno[i++] = 1.5374059332699304;
        child.geno[i++] = 1.7658946609910908;
        child.geno[i++] = 1.0464031140100842;
        child.geno[i++] = 1.290620322640275;
        child.geno[i++] = 0.1600018459906336;
        child.geno[i++] = 1.3218818865801538;
        child.geno[i++] = 0.5799284657161324;
        
        return child;
    }
    
    public static Genotype CreateBest44()
    {
        Genotype child = new Genotype(8);
        int i = 0;
        child.geno[i++] = 0.15481235467990292;
        child.geno[i++] = 1.7641276621834787;
        child.geno[i++] = 1.0909920449281676;
        child.geno[i++] = 1.4292946780740667;
        child.geno[i++] = 0.5569822943852913;
        child.geno[i++] = 0.14418420653356526;
        child.geno[i++] = 1.2839704185034961;
        child.geno[i++] = 0.6182691168034687;
        
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
    
    public static Genotype LoadFile(int size, String file)
    {
        try {
            Scanner scanner = new Scanner(new FileInputStream(file));
            
            Genotype child = new Genotype(size);
            child.score = scanner.nextDouble();
            
            for (int i = 0; i < size; i++) {
                child.geno[i] = scanner.nextDouble();
            }
            
            return child;
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + file + " - " + ex);
            return CreateWithValue(size, 1);
        }
    }
}

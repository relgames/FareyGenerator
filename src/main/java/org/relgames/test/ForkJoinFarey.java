package org.relgames.test;

import org.apache.commons.math3.fraction.Fraction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author opoleshuk
 */
public class ForkJoinFarey extends RecursiveTask<List<Fraction>>{
    private final Fraction left;
    private final Fraction right;

    private ForkJoinFarey(Fraction left, Fraction right) {
        this.left = left;
        this.right = right;
    }

    @Override
    protected List<Fraction> compute() {
        Fraction mediant = new Fraction(left.getNumerator()+right.getNumerator(), left.getDenominator()+right.getDenominator());
        if (mediant.getDenominator()> BASE) {
            return Collections.emptyList();
        }

        ForkJoinFarey leftList = new ForkJoinFarey(left, mediant);
        ForkJoinFarey rightList = new ForkJoinFarey(mediant, right);
        rightList.fork();

        List<Fraction> result = new LinkedList<Fraction>();
        result.addAll(leftList.compute());
        result.add(mediant);
        result.addAll(rightList.join());
        return result;
    }

    public static List<Fraction> getFareyList() {
        List<Fraction> result = new LinkedList<Fraction>();

        Fraction left = new Fraction(0, 1);
        Fraction right = new Fraction(1, 1);

        ForkJoinFarey task = new ForkJoinFarey(left, right);

        new ForkJoinPool().invoke(task);

        result.add(left);
        result.addAll(task.join());
        result.add(right);

        return result;
    }

    private final static int BASE = 500;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        List<Fraction> farey = ForkJoinFarey.getFareyList();

        int max = 0;
        for (Fraction f: farey) {
            if (f.getDenominator()>max) {
                max = f.getDenominator();
            }
        }

        System.out.printf("Total %d in  %dms", farey.size(), System.currentTimeMillis()-time);
    }

}

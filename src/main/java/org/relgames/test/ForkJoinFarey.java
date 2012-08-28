package org.relgames.test;

import org.apache.commons.math3.fraction.Fraction;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author opoleshuk
 */
public class ForkJoinFarey extends RecursiveAction {
    private final Node<Fraction> leftNode;

    private ForkJoinFarey(Node<Fraction> leftNode) {
        this.leftNode = leftNode;
    }


    @Override
    protected void compute() {
        Node<Fraction> rightNode = leftNode.next;

        Fraction mediant = new Fraction(leftNode.o.getNumerator()+rightNode.o.getNumerator(),
                                        leftNode.o.getDenominator()+rightNode.o.getDenominator());
        if (mediant.getDenominator()> BASE) {
            return;
        }

        Node<Fraction> mediantNode = new Node<>(mediant);
        leftNode.next = mediantNode;
        mediantNode.next = rightNode;

        ForkJoinFarey leftTask = new ForkJoinFarey(leftNode);
        ForkJoinFarey rightTask = new ForkJoinFarey(mediantNode);

        if (mediant.getDenominator()>BASE-500) {
            leftTask.compute();
            rightTask.compute();
        } else {
            rightTask.fork();
            leftTask.compute();
            rightTask.join();
        }


    }

    private static List<Fraction> getFareyList() {
        final Node<Fraction> leftNode = new Node<>(new Fraction(0, 1));
        leftNode.next = new Node<>(new Fraction(1, 1));

        ForkJoinFarey task = new ForkJoinFarey(leftNode);

        new ForkJoinPool().invoke(task);

        return ExecutorFarey.asList(leftNode);
    }

    private final static int BASE = 50000;

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

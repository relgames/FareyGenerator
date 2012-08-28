package org.relgames.test;

import org.apache.commons.math3.fraction.Fraction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author opoleshuk
 */
public class RecursiveFarey {
    private static List<Fraction> getFarey(Fraction left, Fraction right) {
        Fraction mediant = new Fraction(left.getNumerator()+right.getNumerator(), left.getDenominator()+right.getDenominator());
        if (mediant.getDenominator()>BASE) {
            return Collections.emptyList();
        }
        List<Fraction> result = new LinkedList<>();
        result.addAll(getFarey(left, mediant));
        result.add(mediant);
        result.addAll(getFarey(mediant, right));
        return result;
    }

    private static List<Fraction> getFarey() {
        List<Fraction> result = new LinkedList<>();

        Fraction left = new Fraction(0, 1);
        Fraction right = new Fraction(1, 1);

        result.add(left);
        result.addAll(getFarey(left, right));
        result.add(right);

        return result;
    }

    private final static int BASE = 500;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        List<Fraction> farey = RecursiveFarey.getFarey();

        int max = 0;
        for (Fraction f: farey) {
            if (f.getDenominator()>max) {
                max = f.getDenominator();
            }
        }

        System.out.printf("Total %d in  %dms", farey.size(), System.currentTimeMillis()-time);
    }
}

package com.setser.solveintegrate.utils;

import java.util.function.Function;

/**
 * Created by hp on 08.08.2016.
 */
public class MathUtils {

    public static double integrate(Function<Double, Double> f, double a, double b, double eps) {
        int n = 1000;
        double sum1 = 0, sum2 = 0;
        double step = (b-a)/n;
        for(int i = 0; i<n; ++i) {
            sum1 += (f.apply(a + (i + 1) * step) + f.apply(a + i * step))*step/2;
        }
        n *= 2;
        step = (b-a)/n;
        for(int i = 0; i<n; ++i) {
            sum2 += (f.apply(a + (i + 1) * step) + f.apply(a + i * step))*step/2;
        }
        while(Math.abs(sum2 - sum1) >= eps) {
            sum1 = sum2;
            sum2 = 0;
            n *= 2;
            step = (b-a)/n;
            for(int i = 0; i<n; ++i) {
                sum2 += (f.apply(a + (i + 1) * step) + f.apply(a + i * step))*step/2;
            }
        }
        return sum2;
    }

    public static double solve(Function<Double, Double> f, Function<Double, Double> df, double a, double b, double eps) {
        double xprev = a, xnext = b;
        while(Math.abs(xnext - xprev) >= eps) {
            xprev = xnext;
            xnext = xprev - f.apply(xprev)/df.apply(xprev);
        }
        return xnext;
    }

}

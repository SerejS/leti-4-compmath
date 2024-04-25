package org.relunluck.dynsys.stepper;

import org.relunluck.dynsys.functions.Function;

public interface Stepper<T> {
    public T makeStep(T x, double t, double h, Function<T> f);
}

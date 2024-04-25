package org.relunluck.dynsys.functions;

public interface Function<T> {
    public T calculate(double t, T x);
}

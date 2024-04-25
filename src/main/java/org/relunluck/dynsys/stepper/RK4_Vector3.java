package org.relunluck.dynsys.stepper;

import org.joml.Vector3d;
import org.relunluck.dynsys.functions.Function;

public class RK4_Vector3 implements Stepper<Vector3d> {
    @Override
    public Vector3d makeStep(Vector3d x, double t, double h, Function<Vector3d> f) {
        Vector3d k1 = f.calculate(t, x);
        Vector3d k2 = f.calculate(t + h * 0.5, new Vector3d(x).add(new Vector3d(k1).mul(0.5 * h)));
        Vector3d k3 = f.calculate(t + h * 0.5, new Vector3d(x).add(new Vector3d(k2).mul(0.5 * h)));
        Vector3d k4 = f.calculate(t + h, new Vector3d(x).add(new Vector3d(k3).mul(h)));
        return x.add((k1.mul(1. / 6).add(k2.mul(1. / 3)).add(k3.mul(1. / 3)).add(k4.mul(1. / 6)))).mul(h);
    }
}

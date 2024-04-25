package org.relunluck.dynsys.functions;

import org.joml.Vector3d;

public class VelocityFunction implements Function<Vector3d>{
    private Vector3d v, a;
    private double t0;
    public VelocityFunction(Vector3d v, Vector3d a, double t0){
        this.v = new Vector3d(v);
        this.a = new Vector3d(a);
        this.t0 = t0;
    }

    @Override
    public Vector3d calculate(double t, Vector3d x) {
        return v.add(a.mul(t - t0));
    }
}

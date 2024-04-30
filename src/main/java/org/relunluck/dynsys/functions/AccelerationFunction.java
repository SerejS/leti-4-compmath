package org.relunluck.dynsys.functions;

import org.joml.Vector3d;

public class AccelerationFunction implements Function<Vector3d>{

    private Vector3d a;
    public AccelerationFunction(Vector3d a){
        this.a = new Vector3d(a);
    }
    @Override
    public Vector3d calculate(double t, Vector3d v) {
        return a;
    }
}

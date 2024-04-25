package org.relunluck.dynsys.functions;

import org.joml.*;

public class WhirlFunction implements Function<Quaterniond> {
    private Vector3d w;
    public WhirlFunction(Vector3d w){
        this.w = new Vector3d(w);
    }
    @Override
    public Quaterniond calculate(double t, Quaterniond x) {
        Quaterniond res = new Quaterniond();
        return x.mul( w.x, w.y, w.z, 0, res).mul(new Quaterniond(0, 0, 0, 0.5));
    }
}

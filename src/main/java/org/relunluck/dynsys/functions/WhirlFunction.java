package org.relunluck.dynsys.functions;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class WhirlFunction implements Function<Quaterniond> {
    private Vector3d w;
    public WhirlFunction(Vector3d w){
        this.w = new Vector3d(w);
    }
    @Override
    public Quaterniond calculate(double t, Quaterniond x) {
        Quaterniond res = (new Quaterniond( w.x, w.y, w.z, 0)).mul(x);
        return res.mul(new Quaterniond(0, 0, 0, 0.5));
    }
}

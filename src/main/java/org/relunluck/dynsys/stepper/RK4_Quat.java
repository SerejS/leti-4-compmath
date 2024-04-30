package org.relunluck.dynsys.stepper;

import org.joml.*;
import org.relunluck.dynsys.functions.Function;

public class RK4_Quat implements Stepper<Quaterniond> {
    @Override
    public Quaterniond makeStep(Quaterniond x, double t, double h, Function<Quaterniond> f) {
        Quaterniond hq = new Quaterniond(0, 0, 0, h);
        Quaterniond one_half = new Quaterniond(0, 0, 0, 1. / 2);
        Quaterniond one_third = new Quaterniond(0, 0, 0, 1. / 3);
        Quaterniond one_sixth = new Quaterniond(0, 0, 0, 1. / 6);

        Quaterniond k1 = f.calculate(t, x);
        Quaterniond k2 = f.calculate(t + h * 0.5, new Quaterniond(x).add(new Quaterniond(k1).mul(hq.mul(one_half, new Quaterniond()))));
        Quaterniond k3 = f.calculate(t + h * 0.5, new Quaterniond(x).add(new Quaterniond(k2).mul(hq.mul(one_half, new Quaterniond()))));
        Quaterniond k4 = f.calculate(t + h, new Quaterniond(x).add(new Quaterniond(k3).mul(hq)));
        return x.add((k1.mul(one_sixth).add(k2.mul(one_third)).add(k3.mul(one_third)).add(k4.mul(one_sixth))).mul(hq));
    }
}

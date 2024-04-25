package org.relunluck.dynsys.state;

import org.ejml.simple.SimpleMatrix;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class DynamicState {
    double t;
    Quaterniond q;
    Vector3d x, v, a, w;
    SimpleMatrix I, I_inv;

    public DynamicState(DynamicState other) {
        this.t = other.t;
        this.q = new Quaterniond(other.q);
        this.x = new Vector3d(other.x);
        this.v = new Vector3d(other.v);
        this.a = new Vector3d(other.a);
        this.w = new Vector3d(other.w);
        this.I = new SimpleMatrix(other.I);
        this.I_inv = new SimpleMatrix(other.I_inv);
    }

    public static SimpleMatrix quatToMatrix(Quaterniond q) {
        SimpleMatrix res = new SimpleMatrix(3, 3);
        double sqw = q.w * q.w;
        double sqx = q.x * q.x;
        double sqy = q.y * q.y;
        double sqz = q.z * q.z;

        res.set(0, 0, (sqx - sqy - sqz + sqw));
        res.set(1, 1, (-sqx + sqy - sqz + sqw));
        res.set(2, 2, (-sqx - sqy + sqz + sqw));

        double tmp1 = q.x * q.y;
        double tmp2 = q.z * q.w;

        res.set(1, 0, 2.0 * (tmp1 + tmp2));
        res.set(0, 1, 2.0 * (tmp1 - tmp2));

        tmp1 = q.x * q.z;
        tmp2 = q.y * q.w;

        res.set(2, 0, 2.0 * (tmp1 - tmp2));
        res.set(0, 2, 2.0 * (tmp1 + tmp2));

        tmp1 = q.y * q.z;
        tmp2 = q.x * q.w;

        res.set(3, 0, 2.0 * (tmp1 + tmp2));
        res.set(0, 3, 2.0 * (tmp1 - tmp2));

        return res;
    }

    public DynamicState(ConstState cstate, Quaterniond q, Vector3d x, Vector3d v, Vector3d a, Vector3d w) {
        t = 0;
        this.q = new Quaterniond(q);
        this.x = new Vector3d(x);
        this.v = new Vector3d(v);
        this.a = new Vector3d(a);
        this.w = new Vector3d(w);
        SimpleMatrix R = quatToMatrix(q);
        SimpleMatrix R_inv = R.invert();
        this.I = R.mult(cstate.getI_body()).mult(R_inv);
        this.I_inv = R.mult(cstate.getI_body_inv()).mult(R_inv);
    }

    public void changeI(ConstState cstate){
        SimpleMatrix R = quatToMatrix(q);
        SimpleMatrix R_inv = R.transpose();
        this.I = R.mult(cstate.getI_body()).mult(R_inv);
        this.I_inv = R.mult(cstate.getI_body_inv()).mult(R_inv);
    }

    public double getT() {
        return t;
    }

    public Quaterniond getQ() {
        return q;
    }

    public SimpleMatrix getI() {
        return I;
    }

    public SimpleMatrix getI_inv() {
        return I_inv;
    }

    public Vector3d getA() {
        return a;
    }

    public Vector3d getV() {
        return v;
    }

    public Vector3d getW() {
        return w;
    }

    public Vector3d getX() {
        return x;
    }

}



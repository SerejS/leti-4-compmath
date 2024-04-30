package org.relunluck.dynsys;

import org.ejml.simple.SimpleMatrix;
import org.joml.*;
import org.relunluck.dynsys.functions.*;
import org.relunluck.dynsys.state.*;
import org.relunluck.dynsys.stepper.*;

import static org.relunluck.dynsys.state.ConstState.getPlaneNormal;
import static org.relunluck.dynsys.state.DynamicState.quatToMatrix;

public class SystemModeler {
    private SystemState state;
    private StateBuffer buffer;
    double time, step, t = 0;
    Stepper<Vector3d> vector3dStepper;

    Stepper<Quaterniond> quaterniondStepper;

    public SystemModeler(SystemState startState, Stepper<Vector3d> vector3dStepper, Stepper<Quaterniond> quaterniondStepper, double time, double step) {
        this.state = startState;
        buffer = new StateBuffer(this.state);
        this.time = time;
        this.vector3dStepper = vector3dStepper;
        this.quaterniondStepper = quaterniondStepper;
        this.step = step;
    }

    public void modeling() {
        while (t < time) {
            SystemState next = new SystemState(state);
            vector3dStepper.makeStep(next.getDynamicState().getX(), t, step,
                    new VelocityFunction(next.getDynamicState().getV(), next.getDynamicState().getA(), t));
            vector3dStepper.makeStep(next.getDynamicState().getV(), t, step,
                    new AccelerationFunction(next.getDynamicState().getA()));
            quaterniondStepper.makeStep(next.getDynamicState().getQ(), t, step,
                    new WhirlFunction(next.getDynamicState().getW()));
            next.getDynamicState().changeI(next.getConstState());
            buffer.add(next);
            state = next;
            next.getDynamicState().appendT(step);
            t += step;
            if (calcPointPosition(state.getConstState().getPseudo_plane(), state.getDynamicState().getX()) <= 0){
                System.out.println("Calced: " + calcPointPosition(state.getConstState().getPseudo_plane(), state.getDynamicState().getX()));
                System.out.println("Before: " + next.getDynamicState().getX());
                Vector3d v = getLowestVertex(state);
                if (v != null){
                    System.out.println(next.getDynamicState().getX());
                    System.out.println("Ded in side");
                    modelingContact(v);
                }
                System.out.println("After: " + next.getDynamicState().getW());
            }
        }
    }

    public static Vector3d getLowestVertex(SystemState state) {
        SimpleMatrix R = quatToMatrix(state.getDynamicState().getQ());
        R = R.mult(state.getConstState().getVertices());
        double mind = 0;
        Vector3d res = null;
        for(int i = 0; i < 8; i++) {
            Vector3d v = new Vector3d(R.get(0, i), R.get(1, i), R.get(2, i)).add(state.getDynamicState().getX());
            double d = calcPointPosition(state.getConstState().getPlane(), v);
            if (d < mind) {
                res = new Vector3d(v);
                mind = d;
            }
            if (d == mind && res != null) {
                res.add(v);
                res.mul(0.5);
            }
        }
        return res;
    }


    public static double calcPointPosition(Vector4d plane, Vector3d x){
        return plane.dot(x.x, x.y, x.z, 1);
    }

    public void modelingContact(Vector3d p) {
        SimpleMatrix I_inv = state.getDynamicState().getI_inv();
        Vector3d r = p.sub(state.getDynamicState().getX(), new Vector3d());
        Vector3d pdot = state.getDynamicState().getW().cross(r, new Vector3d()).add(state.getDynamicState().getV());
        Vector3d normal = getPlaneNormal(state.getConstState().getPlane());
        Vector3d tmp1 = r.cross(normal, new Vector3d());
        SimpleMatrix tmp2 = I_inv.mult(new SimpleMatrix(new double[][]{{tmp1.x}, {tmp1.y}, {tmp1.z}}));
        tmp1 = new Vector3d(tmp2.get(0,0), tmp2.get(1,0), tmp2.get(2, 0));
        tmp1.cross(r);
        double denum = 1./state.getConstState().getMass() + normal.dot(tmp1);
        double num = -(1. + state.getConstState().getPlane_coef()) * normal.dot(pdot);
        double j = num / denum;
        Vector3d J = normal.mul(j, new Vector3d());
        Vector3d dv = J.div(state.getConstState().getMass(), new Vector3d());
        state.getDynamicState().getV().add(dv);
        tmp1 = r.cross(J, tmp1);
        tmp2 = I_inv.mult(new SimpleMatrix(new double[][]{{tmp1.x}, {tmp1.y}, {tmp1.z}}));
        Vector3d dw = new Vector3d(tmp2.get(0,0), tmp2.get(1,0), tmp2.get(2, 0));
        state.getDynamicState().getW().add(dw);
    }

    public StateBuffer getBuffer() {
        return buffer;
    }
}

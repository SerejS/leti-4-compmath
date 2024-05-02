package org.relunluck.dynsys;

import org.ejml.simple.SimpleMatrix;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.relunluck.dynsys.functions.AccelerationFunction;
import org.relunluck.dynsys.functions.VelocityFunction;
import org.relunluck.dynsys.functions.WhirlFunction;
import org.relunluck.dynsys.state.StateBuffer;
import org.relunluck.dynsys.stepper.Stepper;

import java.util.ArrayList;
import java.util.List;

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
            t += step;
            next.getDynamicState().appendT(step);
            if (calcPointPosition(state.getConstState().getPseudo_plane(), state.getDynamicState().getX()) <= 0) {
                var v = getLowestVertex(state);
                if (v != null) modelingContact(v);
            }
        }
    }

    public static Vector3d getLowestVertex(SystemState state) {
        SimpleMatrix R = quatToMatrix(state.getDynamicState().getQ());
        R = R.mult(state.getConstState().getVertices());
        double mind = 0;
        List<Vector3d> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Vector3d v = new Vector3d(R.get(0, i), R.get(1, i), R.get(2, i)).add(state.getDynamicState().getX());
            Vector3d r = v.sub(state.getDynamicState().getX(), new Vector3d());
            Vector3d pdot = (state.getDynamicState().getW().cross(r, new Vector3d())).add(state.getDynamicState().getV());
            if (pdot.dot(getPlaneNormal(state.getConstState().getPlane())) > 0) continue;
            double d = calcPointPosition(state.getConstState().getPlane(), v);
            if (d < mind) {
                res.clear();
                res.add(new Vector3d(v));
                mind = d;
            } else if (d == mind) {
                res.add(new Vector3d(v));
            }
        }
        if (res.isEmpty()) return null;

        var resVec = new Vector3d(0);
        for (var v : res) {
            resVec.add(v, resVec);
        }
        resVec.div(res.size());

        return resVec;
    }


    public static double calcPointPosition(Vector4d plane, Vector3d x) {
        return plane.dot(x.x, x.y, x.z, 1);
    }

    public void modelingContact(Vector3d p) {
        SimpleMatrix I_inv = state.getDynamicState().getI_inv();                                        // I_inv
        Vector3d r = p.sub(state.getDynamicState().getX(), new Vector3d());                             // r - точка удара относительно центра
        Vector3d pdot = state.getDynamicState().getW().cross(r, new Vector3d()).add(state.getDynamicState().getV()); // ускорение точки касания
        Vector3d normal = getPlaneNormal(state.getConstState().getPlane());                             // n - нормаль плоскости касания
        Vector3d tmp1 = r.cross(normal, new Vector3d());                                                // r x n
        SimpleMatrix tmp2 = I_inv.mult(new SimpleMatrix(new double[][]{{tmp1.x}, {tmp1.y}, {tmp1.z}})); // I^-1 (r x n) в виде матрицы (3x1)
        tmp1 = new Vector3d(tmp2.get(0, 0), tmp2.get(1, 0), tmp2.get(2, 0));   // I^-1 (r x n) в виде вектора
        tmp1.cross(r);                                                                                  // (I^-1 (r x n)) x r
        double denum = 1. / state.getConstState().getMass() + normal.dot(tmp1);                           // знаменатель j = 1/M + <n, (I^-1 (r x n)) x r>
        double num = -(1. + state.getConstState().getPlane_coef()) * normal.dot(pdot);                  // числитель j = -(1 + eps) * <n, p^\dot>
        double j = num / denum;                                                                         // j
        Vector3d J = normal.mul(j, new Vector3d());                                                     // J = j*n
        Vector3d dv = J.div(state.getConstState().getMass(), new Vector3d());
        state.getDynamicState().getV().add(dv);                                                         // v = v0 + dv
        tmp1 = r.cross(J, tmp1);                                                                        // r x J
        tmp2 = I_inv.mult(new SimpleMatrix(new double[][]{{tmp1.x}, {tmp1.y}, {tmp1.z}}));              // dw = I^-1*(r x J) в виде матрицы (3x1)
        Vector3d dw = new Vector3d(tmp2.get(0, 0), tmp2.get(1, 0), tmp2.get(2, 0)); // dw = I^-1*(r x J) в виде вектора
        state.getDynamicState().getW().add(dw);
    }

    public StateBuffer getBuffer() {
        return buffer;
    }
}

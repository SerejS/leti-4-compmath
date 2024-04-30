package org.relunluck.dynsys.state;

import org.ejml.simple.SimpleMatrix;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.relunluck.engine.scene.Entity;
import org.ejml.*;

public class ConstState {
    private double mass, plane_coef;
    private SimpleMatrix I_body, I_body_inv;
    private SimpleMatrix vertices;
    private Vector4d plane, pseudo_plane;

    public ConstState(double mass, double eps, Entity cube, Vector4d plane) {
        this.mass = mass;
        this.plane_coef = eps;
        this.plane = new Vector4d(plane.normalize3());
        this.pseudo_plane = new Vector4d(this.plane);
        double scale = cube.getScale();
        pseudo_plane.w -= scale * Math.sqrt(3) / 2;
        I_body = new SimpleMatrix(new double[3][3]);
        I_body_inv = new SimpleMatrix(new double[3][3]);
        for (int i = 0; i < 3; i++) {
            I_body.set(i, i, scale * scale * 2 * mass / 12);
            I_body_inv.set(i,i, 1 / I_body.get(i, i));
        }
        this.vertices = new SimpleMatrix(3, 8);
        for (int i=0; i < 8; i++){
            vertices.set(0, i, i % 2 == 0 ? scale / 2: -scale / 2);
            vertices.set(1, i, (i / 2) % 2 == 0 ? scale / 2: -scale / 2);
            vertices.set(2, i, (i / 4) % 2 == 0 ? scale / 2: -scale / 2);
        }
    }

    public double getMass() {
        return mass;
    }

    public double getPlane_coef() {
        return plane_coef;
    }

    public SimpleMatrix getI_body() {
        return I_body;
    }

    public SimpleMatrix getI_body_inv() {
        return I_body_inv;
    }

    public Vector4d getPlane() {
        return plane;
    }

    public Vector4d getPseudo_plane() {
        return pseudo_plane;
    }

    public static Vector3d getPlaneNormal(Vector4d plane){
        return new Vector3d(plane.x, plane.y, plane.z);
    }

    public SimpleMatrix getVertices() {
        return vertices;
    }
}

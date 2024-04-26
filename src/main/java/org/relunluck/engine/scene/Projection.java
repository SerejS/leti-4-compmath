package org.relunluck.engine.scene;

import org.joml.Matrix4d;

public class Projection {

    private static final double FOV = Math.toRadians(60.0d);
    private static final double Z_FAR = 1000.d;
    private static final double Z_NEAR = 0.01d;

    private Matrix4d projMatrix;

    public Projection(int width, int height) {
        projMatrix = new Matrix4d();
        updateProjMatrix(width, height);
    }

    public Matrix4d getProjMatrix() {
        return projMatrix;
    }

    public void updateProjMatrix(int width, int height) {
        projMatrix.setPerspective(FOV, (double) width / height, Z_NEAR, Z_FAR);
    }
}

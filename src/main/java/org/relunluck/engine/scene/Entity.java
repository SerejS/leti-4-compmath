package org.relunluck.engine.scene;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class Entity {

    private final String id;
    private final String modelId;
    private Matrix4d modelMatrix;
    private Vector3d position;
    private Quaterniond rotation;
    private double scale;

    public Entity(String id, String modelId) {
        this.id = id;
        this.modelId = modelId;
        modelMatrix = new Matrix4d();
        position = new Vector3d();
        rotation = new Quaterniond();
        scale = 1;
    }

    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    public Matrix4d getModelMatrix() {
        return modelMatrix;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Quaterniond getRotation() {
        return rotation;
    }

    public double getScale() {
        return scale;
    }

    public final void setPosition(double x, double y, double z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(double x, double y, double z, double angle) {
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void updateModelMatrix() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
}
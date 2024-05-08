package org.relunluck.engine.entities;

import org.relunluck.engine.graph.Material;
import org.relunluck.engine.graph.Mesh;
import org.relunluck.engine.graph.Model;
import org.relunluck.engine.graph.Texture;
import org.relunluck.engine.scene.Entity;
import org.relunluck.engine.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class Parallelep {
    private final double a, b, c;
    Entity entity;


    public Parallelep(Scene scene, double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;

        double[] cubePositions = new double[]{
                // V0
                -b / 2, a / 2, c / 2,
                // V1
                -b / 2, -a / 2, c / 2,
                // V2
                b / 2, -a / 2, c / 2,
                // V3
                b / 2, a / 2, c / 2,
                // V4
                -b / 2, a / 2, -c / 2,
                // V5
                b / 2, a / 2, -c / 2,
                // V6
                -b / 2, -a / 2, -c / 2,
                // V7
                b / 2, -a / 2, -c / 2,

                // For text coords in top face
                // V8: V4 repeated
                -b / 2, a / 2, -c / 2,
                // V9: V5 repeated
                b / 2, a / 2, -c / 2,
                // V10: V0 repeated
                -b / 2, a / 2, c / 2,
                // V11: V3 repeated
                b / 2, a / 2, c / 2,

                // For text coords in right face
                // V12: V3 repeated
                b / 2, a / 2, c / 2,
                // V13: V2 repeated
                b / 2, -a / 2, c / 2,

                // For text coords in left face
                // V14: V0 repeated
                -b / 2, a / 2, c / 2,
                // V15: V1 repeated
                -b / 2, -a / 2, c / 2,

                // For text coords in bottom face
                // V16: V6 repeated
                -b / 2, -a / 2, -c / 2,
                // V17: V7 repeated
                b / 2, -a / 2, -c / 2,
                // V18: V1 repeated
                -b / 2, -a / 2, c / 2,
                // V19: V2 repeated
                b / 2, -a / 2, c / 2,
        };
        double[] cubeTexCoords = new double[]{ // Texture
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] cubeIndices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7
        };

        Texture cubeTex = scene.getTextureCache().createTexture("resources/models/cube/pyro.png");
        Material cubeMaterial = new Material();
        cubeMaterial.setTexturePath(cubeTex.getTexturePath());

        List<Material> cubeMatList = new ArrayList<>();
        cubeMatList.add(cubeMaterial);

        Mesh cubeMesh = new Mesh(cubePositions, cubeTexCoords, cubeIndices);
        cubeMaterial.getMeshList().add(cubeMesh);

        Model cubeModel = new Model("cube-model", cubeMatList);
        entity = new Entity("cube-entity", cubeModel.getId());

        scene.addModel(cubeModel);
        scene.addEntity(entity);
    }

    public double getHeight() {
        return a;
    }

    public double getWidth() {
        return b;
    }

    public double getLength() {
        return c;
    }

    public Entity getEntity() {
        return entity;
    }
}

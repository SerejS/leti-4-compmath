package org.relunluck.engine.entities;

import org.joml.Vector4d;
import org.relunluck.engine.graph.Material;
import org.relunluck.engine.graph.Mesh;
import org.relunluck.engine.graph.Model;
import org.relunluck.engine.scene.Entity;
import org.relunluck.engine.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class Flat {
    public Flat(Scene scene, Vector4d flatVector) {
        double left = -16f;
        double right = 16f;
        double[] flatPositions = new double[]{
                left, (-flatVector.x * left - flatVector.w) / flatVector.y, -32.0f, //upL
                left, (-flatVector.x * left - flatVector.w) / flatVector.y, 0.0f, //downL
                right, (-flatVector.x * right - flatVector.w) / flatVector.y, 0.0f, //downR
                right, (-flatVector.x * right - flatVector.w) / flatVector.y, -32.0f, //upR
        };
        double[] flatTex = new double[]{
                0.0f, 0.0f,
                0.0f, 1f,
                1f, 1f,
                1f, 0.0f,
        };
        int[] flatIndices = new int[]{0, 1, 3, 3, 1, 2};
        var flatTexture = scene.getTextureCache().createTexture("resources/models/flat/flat.png");
        var flatMaterial = new Material();
        flatMaterial.setTexturePath(flatTexture.getTexturePath());
        List<Material> flatMaterialList = new ArrayList<>();
        flatMaterialList.add(flatMaterial);
        var flatMesh = new Mesh(flatPositions, flatTex, flatIndices);
        flatMaterial.getMeshList().add(flatMesh);
        Model flatModel = new Model("flat-model", flatMaterialList);

        var entity = new Entity("flat-entity", flatModel.getId());
        scene.addModel(flatModel);
        scene.addEntity(entity);
        entity.updateModelMatrix();

    }
}

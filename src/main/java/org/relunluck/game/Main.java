package org.relunluck.game;

import org.joml.*;
import org.relunluck.dynsys.SystemModeler;
import org.relunluck.dynsys.SystemState;
import org.relunluck.dynsys.state.ConstState;
import org.relunluck.dynsys.state.DynamicState;
import org.relunluck.dynsys.state.StateBuffer;
import org.relunluck.dynsys.stepper.RK4_Quat;
import org.relunluck.dynsys.stepper.RK4_Vector3;
import org.relunluck.engine.*;
import org.relunluck.engine.graph.*;
import org.relunluck.engine.scene.*;

import java.lang.Math;
import java.util.*;

import static java.lang.Math.*;

public class Main implements IAppLogic {

    private Entity cubeEntity;
    private Entity flatEntity;
    private StateBuffer sb;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("chapter-07", new Window.WindowOptions(), main);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        var flatVector = new Vector4d(1.d, 3, 0.d, 3);

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

        flatEntity = new Entity("flat-entity", flatModel.getId());
        flatEntity.updateModelMatrix();

        scene.addModel(flatModel);

        scene.addEntity(flatEntity);


        double[] cubePositions = new double[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
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
        cubeEntity = new Entity("cube-entity", cubeModel.getId());

        scene.addModel(cubeModel);


        var cst = new ConstState(1d, 0.3d, cubeEntity, flatVector);
        var dst = new DynamicState(
                cst, new Quaterniond(),
                new Vector3d(-2, 1, -5),    // pos
                new Vector3d(0., -5, 0.),  // vel
                new Vector3d(0., -1, 0.),  // acc
                new Vector3d(0, 0, 0)                 // whirl
        );
        var stepQ = new RK4_Quat();
        var stepV = new RK4_Vector3();

        var st = new SystemState(cst, dst);

        var inv_step = 1000;
        var sm = new SystemModeler(st, stepV, stepQ, 180d, 1.d / inv_step);

        scene.addEntity(cubeEntity);

        sm.modeling();
        var tempBuffer = sm.getBuffer();
        this.sb = new StateBuffer(tempBuffer.getNext());
        for (int i = 0; !tempBuffer.IsEnd(); i++) {
            var ss = tempBuffer.getNext();
            if (i % inv_step != 0) continue;
            this.sb.add(ss);
        }
//        System.out.println("Peace");
    }


    // Возможность управления с клавиатуры
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        if (sb.IsEnd()) sb.restart();


        var s = sb.getNext();

        var pos = s.getDynamicState().getX();
        var rot = s.getDynamicState().getQ();
        cubeEntity.setPosition(pos.x, pos.y, pos.z);
        cubeEntity.setRotation(rot);
        cubeEntity.updateModelMatrix();

    }
}

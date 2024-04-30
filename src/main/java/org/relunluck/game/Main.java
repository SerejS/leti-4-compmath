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
        double[] positions = new double[]{
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
        double[] textCoords = new double[]{ // Texture
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
        int[] indices = new int[]{
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

        Texture texture = scene.getTextureCache().createTexture("resources/models/cube/pyro.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());

        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = new Mesh(positions, textCoords, indices);
        material.getMeshList().add(mesh);

        Model cubeModel = new Model("cube-model", materialList);
        scene.addModel(cubeModel);


        /*double[] positionsT = new double[]{
                -0.5f, 0.5f, -1.0f,
                -0.5f, -0.5f, -1.0f,
                0.5f, -0.5f, -1.0f,
                0.5f, 0.5f, -1.0f,
        };

        int[] indicesT = new int[]{
                0, 1, 3, 3, 1, 2,
        };

        Material materialT = new Material();
        Mesh meshT = new Mesh(positionsT, new double[]{}, indicesT);
        materialT.getMeshList().add(meshT);
        List<Material> materialListT = new ArrayList<>();
        materialListT.add(materialT);
//        materialList.add(materialT);
        Model modelT = new Model("quad", materialList);
        scene.addModel(modelT);
        var triangle = new Entity("quad-entity", modelT.getId());
        triangle.setPosition(0, 0, -5);
        triangle.updateModelMatrix();*/

        cubeEntity = new Entity("cube-entity", cubeModel.getId());


        var cst = new ConstState(1d, 0.9d, cubeEntity, new Vector4d(1.d, 3, 0.d, 4));
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

        var inv_step = 10;
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
        System.out.println("Peace");
    }


    // Возможность управления с клавиатуры
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        /*displInc.zero();

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displInc.y = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displInc.y = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            displInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            displInc.w = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            displInc.w = 1;
        }

        displInc.mul(diffTimeMillis / 1000.0f);

        Vector3d entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displInc.x + entityPos.x, displInc.y + entityPos.y, displInc.z + entityPos.z);
        cubeEntity.setScale(cubeEntity.getScale() + displInc.w);*/
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        if (sb.IsEnd()) {
            sb.restart();
            return;
        }

        var s = sb.getNext();

        var pos = s.getDynamicState().getX();
        var rot = s.getDynamicState().getQ();
        cubeEntity.setPosition(pos.x, pos.y, pos.z);
        cubeEntity.setRotation(rot);
        cubeEntity.updateModelMatrix();
    }
}

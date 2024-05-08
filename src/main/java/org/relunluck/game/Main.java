package org.relunluck.game;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.relunluck.dynsys.SystemModeler;
import org.relunluck.dynsys.SystemState;
import org.relunluck.dynsys.state.ConstState;
import org.relunluck.dynsys.state.DynamicState;
import org.relunluck.dynsys.state.StateBuffer;
import org.relunluck.dynsys.stepper.RK4_Quat;
import org.relunluck.dynsys.stepper.RK4_Vector3;
import org.relunluck.engine.Engine;
import org.relunluck.engine.IAppLogic;
import org.relunluck.engine.Window;
import org.relunluck.engine.entities.Flat;
import org.relunluck.engine.entities.Parallelep;
import org.relunluck.engine.graph.Render;
import org.relunluck.engine.scene.Entity;
import org.relunluck.engine.scene.Scene;

public class Main implements IAppLogic {

    private Entity cubeEntity;
    private StateBuffer sb;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("Fall Modeling", new Window.WindowOptions(), main);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        // Create entities (flat and parallelepiped)
        var flatVector = new Vector4d(0, 1, 0, 3);
        new Flat(scene, flatVector);

        cubeEntity = (new Parallelep(scene, 1, 1, 1)).getEntity();

        // Init system
        var cst = new ConstState(10d, 0.5, cubeEntity, flatVector);
        var dst = new DynamicState(
                cst, new Quaterniond(),
                new Vector3d(0.0, 1.0, -5),   // pos
                new Vector3d(0.0, -10, 0.0),  // vel
                new Vector3d(0.0, -10, 0.0),  // acc
                new Vector3d(0.0, 0.0, 0.0)   // whirl
        );
        var stepQ = new RK4_Quat();
        var stepV = new RK4_Vector3();

        var st = new SystemState(cst, dst);

        var inv_step = 100;
        var sm = new SystemModeler(st, stepV, stepQ, 500, 1.d / inv_step);

        sm.modeling();
        var tempBuffer = sm.getBuffer();
        this.sb = new StateBuffer(tempBuffer.getNext());
        for (int i = 0; !tempBuffer.IsEnd(); i++) {
            var ss = tempBuffer.getNext();
            if (i % (inv_step) != 0) continue;
            this.sb.add(ss);
        }
    }


    // Возможность управления с клавиатуры
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {}

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        if (sb.IsEnd()) sb.restart();

        var s = sb.getNext();
        var dState = s.getDynamicState();
        cubeEntity.setPosition(dState.getX());
        cubeEntity.setRotation(dState.getQ());

        cubeEntity.updateModelMatrix();
    }
}

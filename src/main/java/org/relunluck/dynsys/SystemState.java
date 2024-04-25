package org.relunluck.dynsys;

import org.relunluck.dynsys.state.ConstState;
import org.relunluck.dynsys.state.DynamicState;

public class SystemState {
    private ConstState constState;
    private DynamicState dynamicState;

    public SystemState(ConstState cstate, DynamicState dynState) {
        this.constState = cstate;
        this.dynamicState = dynState;
    }

    public SystemState(SystemState other) {
        this.constState = other.constState;
        this.dynamicState = new DynamicState(other.dynamicState);
    }

    public ConstState getConstState() {
        return constState;
    }

    public DynamicState getDynamicState() {
        return dynamicState;
    }
}

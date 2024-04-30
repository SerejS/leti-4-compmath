package org.relunluck.dynsys.state;

import org.relunluck.dynsys.SystemState;

import java.util.ArrayList;
import java.util.List;

public class StateBuffer {
    private List<SystemState> states;
    private int stateCursor;

    public StateBuffer(SystemState ss) {
        stateCursor = 0;
        states = new ArrayList<>();
        states.add(ss);
    }

    public void add(SystemState ss) {
        states.add(ss);
    }

    public void clear() {
        states.clear();
    }

    public boolean IsEnd() {
        return stateCursor == states.size();
    }

    public SystemState getNext() {
        return states.get(stateCursor++);
    }

    public SystemState peekNext() {
        return states.get(stateCursor);
    }

    public void restart() {
        stateCursor = 0;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append('{');
        for (var s : states) {
            var q = s.getDynamicState().q;
            out.append("x:\t").append(s.getDynamicState().x).append('\n');
            out.append("q:\t").append(s.getDynamicState().q)
                    .append("\t").append(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w).append('\n');
            out.append("w:\t").append(s.getDynamicState().w).append('\n');
            out.append("v:\t").append(s.getDynamicState().v).append('\n');
            out.append("a:\t").append(s.getDynamicState().a).append("\n\n");
        }
        out.append('}');
        return out.toString();
    }
}

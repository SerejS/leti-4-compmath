package org.relunluck.dynsys.state;

import org.relunluck.dynsys.SystemState;

import java.util.ArrayList;
import java.util.List;

public class StateBuffer {
    private List<SystemState> states;
    private int stateCursor;
    public StateBuffer(SystemState ss){
        stateCursor = 0;
        states = new ArrayList<>();
        states.add(ss);
    }
    public void add(SystemState ss){
        states.add(ss);
    }
    public void clear(){
        states.clear();
    }
    public boolean IsEnd(){
        return stateCursor == states.size();
    }
    public SystemState getNext(){
        return states.get(stateCursor++);
    }
    public void restart(){
        stateCursor = 0;
    }
}

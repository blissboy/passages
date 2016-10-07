package com.boyamihungry.passageways;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by patwheaton on 10/7/16.
 */
public interface StateEngine {


    /* thoughts on structure of control groups
    - have list of controls that are visible per state
    - likely have two states, could be more complex
    - have functions that are run depending on state
        - example, if state is "use oscillator, fields are set in different way, if state is "use slider", different way
    - possible implementations
        - Map<String (state),
              Map<String (functionType like "turn on fields", "show components", "do everything"),
                  Callable<String (@nullable stateChange)>>
     */
    Map<String, Map<String,Callable<Optional<List<StateChange>>>>> stateMap = new HashMap<>();
    Set<String> activeStates = new HashSet<>();


    /**
     * Processes changes to state
     * @param changes
     * @return an optional list of names of invoked states. Note that
     */
    Optional<List<StateChange>> processStateChange(List<StateChange> changes);

    Optional<List<StateChange>> processStateChange(StateChange change);

    /**
     *
     * @param stateName
     * @param activateMethod
     */
    void addState(String stateName, Map<String, Callable<String>> activateMethod);

    class StateChange {
        public final String stateName;
        public final State state;

        public StateChange(String name, State state) {
            this.state = state;
            this.stateName = name;
        }
    }

    enum State {
        ACTIVE,
        INACTIVE;
    }
}

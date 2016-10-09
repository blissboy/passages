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
    - the state functions which need to be run could be further broken down than jsut being Callable into classifications.
       - UI (meaning what is being presented as output)
       - render (meaning how calculations are made that likely affect UI)
       - control state (meaning  how external input is allowed / connected)
       - control interface (showing how control is currently afforded)


     */
    Map<String, Set<Callable<Optional<Set<StateChange>>>>> stateMap = new HashMap<>();
    Set<String> activeStates = new HashSet<>();


    /**
     * Processes changes to state
     * @param changes
     * @return an optional list of names of invoked states. Note that
     */
    Optional<Set<StateChange>> processStateChange(Set<StateChange> changes);

    Optional<Set<StateChange>> processStateChange(StateChange change);

    /**
     * Sets or replaces the definition of a state.
     * @param stateName
     * @param stateMethods a map of <code>Callable</code> keyed by a String
     */
    void setStateDefinition(String stateName, Set<Callable<Optional<Set<StateChange>>>> stateMethods);

    /**
     *
     */
    void addToStateDefinition(String stateName, Callable<Optional<Set<StateChange>>> methodToAdd);

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

    enum StateCategory {
/*
       - UI (meaning what is being presented as output)
       - render (meaning how calculations are made that likely affect UI)
       - control state (meaning  how external input is allowed / connected)
       - control interface (showing how control is currently afforded)

 */

        RENDER,
        CONTROL_STATE,
        CONTROL_INTERFACE,
        OUTPUT_CONTROL,
        UNCATEGORIZED
    }

}

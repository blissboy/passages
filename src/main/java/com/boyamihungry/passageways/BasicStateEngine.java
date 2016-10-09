package com.boyamihungry.passageways;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by patwheaton on 10/8/16.
 */
public class BasicStateEngine implements StateEngine {

    /**
     * Processes changes to state
     *
     * @param changes
     * @return an optional list of names of invoked states. Note that
     */
    @Override
    public Optional<Set<StateChange>> processStateChange(Set<StateChange> changes) {

        Set<StateChange> processedChanges = new HashSet<>();
        Set<StateChange> invokedChanges = new HashSet<>();
        Set<Callable<Optional<Set<StateChange>>>> currentSet;
        for ( StateChange change : changes ) {
            currentSet = stateMap.get(change.stateName);
            if ( null != currentSet) {
                for (Callable<Optional<Set<StateChange>>> callable : currentSet) {
                    try {
                        callable.call().ifPresent(invoked -> invokedChanges.addAll(invoked));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            processedChanges.add(change);
        }

        return invokedChanges.isEmpty() ? Optional.empty() : Optional.of(invokedChanges);

    }

    @Override
    public Optional<Set<StateChange>> processStateChange(StateChange change) {
        return null;
    }

    /**
     * Sets or replaces the definition of a state.
     *  @param stateName
     * @param stateMethods a map of <code>Callable</code> keyed by a String*/
    @Override
    public void setStateDefinition(String stateName, Set<Callable<Optional<Set<StateChange>>>> stateMethods) {

    }

    /**
     * @param stateName
     * @param methodToAdd
     */
    @Override
    public void addToStateDefinition(String stateName, Callable<Optional<Set<StateChange>>> methodToAdd) {

    }


}

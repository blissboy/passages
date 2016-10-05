package com.boyamihungry.passageways;

import controlP5.Controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by patwheaton on 9/24/16.
 */
public class ControlP5Helper {


    public static Controller addControlToGroup(String group, Controller c, Map<String, Set<Controller>> controlGroupMap) {
        if ( controlGroupMap.containsKey(group) ) {
            controlGroupMap.get(group).add(c);
            return c;
        } else {
            controlGroupMap.put(
                    group, new HashSet<Controller>()
            );
            return addControlToGroup(group,c,controlGroupMap);
        }
    }
}

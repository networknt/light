package com.networknt.light.server;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by hus5 on 2/10/2016.
 */
public class MapTest {
    public static void main(String args[]) {
        // create hash map
        HashMap newmap = new HashMap();

        // populate hash map
        newmap.put(1, "tutorials");
        newmap.put(2, "point");
        newmap.put(3, "is best");

        HashMap deepmap = new HashMap();
        deepmap.put(5, "five");
        deepmap.put(6, "six");

        newmap.put(4, deepmap);
        // create set view for the map
        Set set=newmap.entrySet();

        // check set values
        System.out.println("Set values: " + set);
    }

}

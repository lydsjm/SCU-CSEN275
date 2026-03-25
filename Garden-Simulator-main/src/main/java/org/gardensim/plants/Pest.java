package org.gardensim.plants;

import java.util.HashMap;
import java.util.Map;

public enum Pest {
    APHIDS("Aphid"), SCALE("Scale"), BEETLE("Beetle"),
    CATERPILLAR("Caterpillar"), SNAIL("Snail");

    private final String name;

    private static final Map<String, Pest> PestLookUp = new HashMap<>();

    static {
        for(Pest p: values()){
            PestLookUp.put(p.name().toLowerCase(), p);
            PestLookUp.put(p.getName().toLowerCase(), p);
        }
    }

    Pest(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public static Pest find(String input){
        return (input == null) ? null : PestLookUp.get(input.trim().toLowerCase());
    }
}
package org.gardensim.plants;

import java.util.Arrays;
import java.util.List;

public enum NightshadeType  implements PlantType{
    TOMATO("Tomato",Arrays.asList(75,90),2, Arrays.asList(Pest.APHIDS,Pest.SCALE),"/Assets/tomatoPlant.png"),
    CHILIPEPPER("ChiliPepper",Arrays.asList(75,95),2 , Arrays.asList(Pest.APHIDS,Pest.SCALE),"/Assets/chilipepper.png");

    private final String displayName;
    private final List<Integer> tempRange;
    private final int waterRequirements;
    private final List<Pest> pests;
    private final String imagePath;

    NightshadeType(String name, List<Integer> tempRange, int waterReq, List<Pest> pests, String path){
        this.displayName = name;
        this.tempRange = tempRange;
        this.waterRequirements = waterReq;
        this.pests = pests;
        this.imagePath = path;
    }

    public List<Integer> getTempRange() {return tempRange;}
    public int getWaterRequirements() {return waterRequirements;}
    public List<Pest> getPests() {return pests;}
    @Override public String getImagePath() {return imagePath;}
    @Override public String getName(){return displayName;}
}

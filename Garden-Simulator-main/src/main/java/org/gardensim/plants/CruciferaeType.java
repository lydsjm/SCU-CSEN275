package org.gardensim.plants;

import java.util.Arrays;
import java.util.List;

public enum CruciferaeType implements PlantType{
    KALE("Kale", Arrays.asList(70,85), 2, Arrays.asList(Pest.APHIDS,Pest.BEETLE),"/Assets/kale.png"),
    RADISH("Radish", Arrays.asList(65,85), 1, Arrays.asList(Pest.APHIDS,Pest.BEETLE),"Daikon_Radish_pedig2_2K_BaseColor.jpg;Daikon_Radish_pedig2_2K_Opacity.jpg"),
    CABBAGE("Cabbage", Arrays.asList(70,85), 3, Arrays.asList(Pest.APHIDS,Pest.BEETLE),"Water_Cabbage_sgynbbvf2_2K_BaseColor.jpg;Water_Cabbage_sgynbbvf2_2K_Opacity.jpg");

    private final String displayName;
    private final List<Integer> tempRange;
    private final int waterRequirements;
    private final List<Pest> pests;
    private final String imagePath;

    CruciferaeType(String name, List<Integer> tempRange, int waterReq, List<Pest> pests, String path){
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

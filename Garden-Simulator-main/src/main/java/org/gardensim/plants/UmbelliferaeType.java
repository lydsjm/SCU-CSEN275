package org.gardensim.plants;

import java.util.Arrays;
import java.util.List;

public enum UmbelliferaeType implements PlantType{
    CARROT("Carrot", Arrays.asList(70,85),1,Arrays.asList(Pest.BEETLE,Pest.SNAIL),"/Assets/carrot.png"),
    COWPARSLEY("CowParsley", Arrays.asList(65,85),1,Arrays.asList(Pest.BEETLE,Pest.SNAIL),"Cow_Parsley_sf2oegvi2_2K_BaseColor.jpg;Cow_Parsley_sf2oegvi2_2K_Opacity.jpg"),
    CORIANDER("Coriander", Arrays.asList(70,90),1,Arrays.asList(Pest.BEETLE,Pest.SNAIL),"Coriander_qfmhA2_2K_BaseColor.jpg;Coriander_qfmhA2_2K_Opacity.jpg");


    private final String displayName;
    private final List<Integer> tempRange;
    private final int waterRequirements;
    private final List<Pest> pests;
    private final String imagePath;

    UmbelliferaeType(String name, List<Integer> tempRange, int waterReq, List<Pest> pests, String path){
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

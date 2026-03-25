package org.gardensim.plants;

import javafx.scene.image.Image;
import org.gardensim.utils.ResourceManager;

import java.util.concurrent.ThreadLocalRandom;

public class Umbelliferae extends Plant{
    private final UmbelliferaeType type;

    public Umbelliferae(){this(getRandomNightshade());}

    public Umbelliferae(UmbelliferaeType type){
        super(type.getName(), type.getTempRange(), type.getWaterRequirements(), type.getPests());
        this.type = type;
    }

    private static UmbelliferaeType getRandomNightshade(){
        UmbelliferaeType[] types = UmbelliferaeType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(types.length);
        return types[randomIndex];
    }

    @Override public Image getImage(){return ResourceManager.getPlantImage(this.type);}
    @Override public Enum<?> getType() { return type;}
}

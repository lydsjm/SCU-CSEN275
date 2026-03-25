package org.gardensim.plants;

import javafx.scene.image.Image;
import org.gardensim.utils.ResourceManager;
import java.util.concurrent.ThreadLocalRandom;

public class Cruciferae extends Plant{
    private final CruciferaeType type;

    public Cruciferae(){this(getRandomCrucifer());}

    public Cruciferae(CruciferaeType type){
        super(type.getName(), type.getTempRange(), type.getWaterRequirements(), type.getPests());
        this.type = type;
    }

    private static CruciferaeType getRandomCrucifer(){
        CruciferaeType[] types = CruciferaeType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(types.length);
        return types[randomIndex];
    }

    @Override public Image getImage(){return ResourceManager.getPlantImage(this.type);}
    @Override public Enum<?> getType() { return type;}
}

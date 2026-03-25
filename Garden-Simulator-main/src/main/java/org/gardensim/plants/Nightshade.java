package org.gardensim.plants;

import javafx.scene.image.Image;
import org.gardensim.utils.ResourceManager;

import java.util.concurrent.ThreadLocalRandom;

public class Nightshade extends Plant{
    private final NightshadeType type;

    public Nightshade(){this(getRandomNightshade());}

    public Nightshade(NightshadeType type){
        super(type.getName(), type.getTempRange(), type.getWaterRequirements(), type.getPests());
        this.type = type;
    }

    private static NightshadeType getRandomNightshade(){
        NightshadeType[] types = NightshadeType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(types.length);
        return types[randomIndex];
    }

    @Override public Image getImage(){
        return ResourceManager.getPlantImage(this.type);
    }
    @Override public Enum<?> getType() { return type;}
}

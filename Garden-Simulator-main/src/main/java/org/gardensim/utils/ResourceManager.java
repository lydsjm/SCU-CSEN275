package org.gardensim.utils;


import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.gardensim.plants.PlantType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ResourceManager {
    private ResourceManager(){}

    private static final Map<String, Image> images = new HashMap<>();
    private static Image dampSoil;
    private static Image mossyPath;
    private static Image soilGrassy;
    private static final List<Image> leaves = new ArrayList<>();

    public static Image getPlantImage(PlantType type) {
        return images.computeIfAbsent(type.getName(), p -> {
            String path = type.getImagePath();
            if(path.contains(";")){
                String[] parts = type.getImagePath().split(";");
                Image base = new Image(ResourceManager.class.getResourceAsStream("/Assets/"+parts[0]));
                Image mask = new Image(ResourceManager.class.getResourceAsStream("/Assets/"+parts[1]));
                return makeTransparent(base,mask);
            }else{
                InputStream stream = ResourceManager.class.getResourceAsStream(path);
                if (stream == null) {
                    throw new RuntimeException("Missing image: " + path);
                }
                return new Image(stream);
            }
        });
    }

    public static void initializeLeaves(){
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmlup2_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmlup2_2K_Opacity.jpg"),100,100,true,true))
        );
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmB2_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmB2_2K_Opacity.jpg"),100,100,true,true))
        );
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgl52_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgl52_2K_Opacity.jpg"),100,100,true,true))
        );
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmH2_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmH2_2K_Opacity.jpg"),100,100,true,true))
        );
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmI2_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgmI2_2K_Opacity.jpg"),100,100,true,true))
        );
        leaves.add(makeTransparent(
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgl2ip2_2K_BaseColor.jpg"),100,100,true,true),
                new Image(ResourceManager.class.getResourceAsStream("/Assets/Leaves/Maple_Leaf_qkgl2ip2_2K_Opacity.jpg"),100,100,true,true))
        );
    }

    public static Image makeTransparent(Image colorImg, Image opacityImg){
        int width = (int)colorImg.getWidth();
        int height = (int)colorImg.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader colorReader = colorImg.getPixelReader();
        PixelReader opacityReader = opacityImg.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for(int y=0; y< height; y++){
            for(int x=0; x<width; x++){
                Color color = colorReader.getColor(x,y);
                Color mask = opacityReader.getColor(x,y);

                double alpha = mask.getBrightness();
                writer.setColor(x,y,new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha));
            }
        }
        return result;
    }

    public static Image getLeaf(){
        int rand = ThreadLocalRandom.current().nextInt(leaves.size());
        return leaves.get(rand);
    }

    public static Image getdampSoilImage(){
        if(dampSoil == null){
           dampSoil = new Image(ResourceManager.class.getResourceAsStream("/Assets/BaseTextures/dampSoil.jpg"));
        }
        return dampSoil;
    }

    public static Image getmossyPathImage(){
        if(mossyPath == null){
            mossyPath = new Image(ResourceManager.class.getResourceAsStream("/Assets/BaseTextures/mossyPath.jpg"));
        }
        return mossyPath;
    }

    public static Image getsoilGrassyImage(){
        if(soilGrassy == null){
            soilGrassy = new Image(ResourceManager.class.getResourceAsStream("/Assets/BaseTextures/soilBorder.jpg"));
        }
        return soilGrassy;
    }
}
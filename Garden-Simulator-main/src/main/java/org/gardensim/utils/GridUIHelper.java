package org.gardensim.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import org.gardensim.plants.Garden;

public class GridUIHelper {

    private GridUIHelper(){}

    public static ImageView createTile(Image img, int size){
        ImageView view = new ImageView(img);
        view.setFitHeight(size);
        view.setFitWidth(size);

        return view;
    }

    public static void prepareGridConstraints(GridPane mainGrid, int rows, int cols){
        mainGrid.getColumnConstraints().clear();
        mainGrid.getRowConstraints().clear();

        for(int i=0; i<cols; i++){
            mainGrid.getColumnConstraints().add(new ColumnConstraints(Garden.SOIL_SIZE));
        }

        for(int i=0; i<rows; i++){
            mainGrid.getRowConstraints().add(new RowConstraints(Garden.SOIL_SIZE));
        }
    }

    public static void updateScale(Scale scale, StackPane root, StackPane mainStack) {
        double windowWidth = root.getWidth();
        double windowHeight = root.getHeight();
        double finalScale = Math.min(windowWidth / 1920.0, windowHeight / 1080.0);

        scale.setX(finalScale);
        scale.setY(finalScale);

        double scaledWidth = 1920 * finalScale;
        double scaledHeight = 1080 * finalScale;

        mainStack.setTranslateX((windowWidth - scaledWidth) / 2);
        mainStack.setTranslateY((windowHeight - scaledHeight) / 2);
    }

}

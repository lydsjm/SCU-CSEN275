package org.gardensim.controllers;

//---------- Animations ----------
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;

//---------- Scene ----------
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

//---------- Geometry ----------
import javafx.geometry.Insets;
import javafx.geometry.Pos;

//---------- Java Utility ----------
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.util.Duration;

//---------- Package Imports ----------
import org.gardensim.utils.GridUIHelper;
import org.gardensim.utils.ResourceManager;
import org.gardensim.plants.*;

public class GardenController implements Initializable {

    //Garden where plants live
    Garden garden = new Garden();

    //2D array of the soil tiles in the game board
    private final ImageView[][] soilTiles = new ImageView[Garden.ROWS][Garden.COLS];

    //Status trackers for button functionality
    private boolean isRunning = false;
    private boolean startedOnce = false;

    //References to track current selected plant and dirt node
    private Plant selectedPlant = null;
    private Node selectedPlantView = null;

    private Timeline simulationTimeline;    //Timeline to implement in-game time
    private Label statuslabel;              //Top Status label for game status
    @FXML private Text dashboardTitle;      //Top Status Label FX Container

    @FXML private StackPane rootContainer;  //Base StackPane that encompasses all other Controls
    @FXML private StackPane mainStackPane;  //Second StackPane that encompasses the GridPane
    @FXML private GridPane mainGrid;        //Grid that holds the plants
    @FXML private Pane leafOverlay;         //Pane that holds the falling leaves

    //Dashboard Controls
    @FXML private VBox dashboard;
    @FXML private VBox dashboard1;

    //Events Dashboard Items
    @FXML private ComboBox spawnPests;
    @FXML private TextField rain;
    @FXML private TextField temp;

    //Control Dashboard Buttons
    @FXML private Button buttonConfirm;
    @FXML private Button buttonStartPause;
    @FXML private Button buttonStop;

    @FXML private TextField nightshades;
    @FXML private TextField nsTomato;
    @FXML private TextField nsChiliPepper;
    @FXML private TextField umbelliferae;
    @FXML private TextField umCarrot;
    @FXML private TextField umCowParsley;
    @FXML private TextField umCoriander;
    @FXML private TextField cruciferae;
    @FXML private TextField crKale;
    @FXML private TextField crRadish;
    @FXML private TextField crCabbage;

    //Farmer Image Walking around farm
    @FXML private ImageView farmer;
    private int farmerRow = 0;
    private int farmerCol = 0;
    private int walkDirection = 0;

    private List<TextField> plantFields;
    private final int simulationTimeMS = 1000;

    /**
     * This function will apply a text filter, only allowing integers, and adds Listeners to all TextFields
     * so the total value of all text fields can be tracked.
     */
    private void initializeTextFields(){
        UnaryOperator<TextFormatter.Change> filter = change ->
            change.getControlNewText().matches("\\d*") ? change : null;

        plantFields = Arrays.asList(nightshades, nsTomato, nsChiliPepper, umbelliferae, umCarrot, umCowParsley,
                umCoriander, cruciferae, crKale, crRadish, crCabbage, rain, temp);

        for(TextField field : plantFields){
            field.setTextFormatter(new TextFormatter<>(filter));
            field.textProperty().addListener((obs, oldVal, newVal) -> validateTotal());
        }
    }

    /**
     * This is a helper function that grabs the values for the TextFields, converts them to an int, and calculates the total.
     */
    private void validateTotal(){
        if(!startedOnce) {
            int total = getIntValue(nightshades) + getIntValue(nsTomato) + getIntValue(nsChiliPepper) +
                    getIntValue(umbelliferae) + getIntValue(umCarrot) + getIntValue(umCowParsley) + getIntValue(umCoriander) +
                    getIntValue(cruciferae) + getIntValue(crKale) + getIntValue(crRadish) + getIntValue(crCabbage);

            int max = (Garden.ROWS - 2) * (Garden.COLS - 2);

            if (total > max) {
                dashboardTitle.setFill(Color.RED);
                dashboardTitle.setText("Total: " + total + " (Max: " + max + "!)");
                buttonConfirm.setDisable(true);
            } else {
                dashboardTitle.setFill(Color.BLACK);
                dashboardTitle.setText("Control Dashboard");
                buttonConfirm.setDisable(false);
            }
        }
    }

    /**
     * This is a helper function that converts a string to an integer.
     * @param field A TextField Control from JavaFX
     * @return An integer
     */
    private int getIntValue(TextField field){
        String text = field.getText();
        if(text == null || text.isEmpty()){
            return 0;
        }
        return Integer.parseInt(text);
    }

    /**
     * This function wipes the current UI by replacing the UI with dirt and removing all plants from memory.
     */
    private void clearGarden(){
        garden.clearGarden();
        mainGrid.getChildren().clear();
        farmerRow = 0;
        farmerCol = 0;
        walkDirection = 0;
        GridUIHelper.prepareGridConstraints(mainGrid, Garden.ROWS, Garden.COLS);
        fillGardenPlot(Garden.ROWS, Garden.COLS);
    }

    /**
     * This function is called when the user clicks the confirm button.  It will initially clear
     * the garden to ensure everything is empty before it plants the number of each plant family
     * specified by the user.
     */
    @FXML private void onConfirmClick(){
        //Initially clear the garden and user selections
        deselectPlant();
        mainGrid.getChildren().clear();
        clearGarden();

        startedOnce = false;
        Random rand = new Random();

        //Enable the start and stop buttons
        buttonStartPause.setDisable(false);
        buttonStop.setDisable(false);

        //Randomly spawn the plants
        plantFamily(getIntValue(nightshades),Nightshade::new, rand);
        plantFamily(getIntValue(nsTomato), () -> new Nightshade(NightshadeType.TOMATO), rand);
        plantFamily(getIntValue(nsChiliPepper),() -> new Nightshade(NightshadeType.CHILIPEPPER), rand);
        plantFamily(getIntValue(umbelliferae),Umbelliferae::new, rand);
        plantFamily(getIntValue(umCarrot), () -> new Umbelliferae(UmbelliferaeType.CARROT), rand);
        plantFamily(getIntValue(umCowParsley),() -> new Umbelliferae(UmbelliferaeType.COWPARSLEY), rand);
        plantFamily(getIntValue(umCoriander),() -> new Umbelliferae(UmbelliferaeType.CORIANDER), rand);
        plantFamily(getIntValue(cruciferae),Cruciferae::new, rand);
        plantFamily(getIntValue(crKale), () -> new Cruciferae(CruciferaeType.KALE), rand);
        plantFamily(getIntValue(crRadish),() -> new Cruciferae(CruciferaeType.RADISH), rand);
        plantFamily(getIntValue(crCabbage),() -> new Cruciferae(CruciferaeType.CABBAGE), rand);
    }

    /**
     * This function can disable and clear text fields and buttons.
     * @param disabled Set to true to disable a field and false to enable it
     * @param clear Set to true to clear a field
     */
    private void setInputsDisabled(boolean disabled, boolean clear){
        plantFields.forEach(f -> {
            if(f != rain && f != temp){
                f.setDisable(disabled);
            }
            if(clear){
                f.clear();
            }
        });
    }

    /**
     * This function is called when the user clicks the start/pause button.  When the button is pressed
     * the TextFields will be disabled (until the game ends), the game simulation will start if it is null (this is only
     * true when the button is pressed for the first time), and the game will either be paused or resumed with a staus bar
     * changing accordingly.
     */
    @FXML private void onStartPauseClick(){
        //Disable the confirm button and text fields while the game is active
        buttonConfirm.setDisable(true);
        setInputsDisabled(true, false);
        startedOnce = true;

        //If the simulation has not started yet, start it.
        if(simulationTimeline == null){
            simulationTimeline = new Timeline(
                    new KeyFrame(Duration.millis(simulationTimeMS), event ->{
                        garden.advanceTime();

                        garden.updateAllPlants();
                        moveFarmerRandomly();

                        System.out.println("\n\nPlants ALIVE: " + garden.plantsAlive()+"\n\n");

                        if (garden.currentTickProperty().get() % 100 == 0) {
                            int variation = (int)(Math.random() * 21) - 10;
                            garden.setCurrentGreenHouseTemp(garden.getCurrentGreenHouseTemp() + variation);
                        }
                        if(garden.plantsAlive() == 0){
                            Platform.runLater(this::onStopClick);
                        }
                    })
            );
            simulationTimeline.setCycleCount(Animation.INDEFINITE);
        }
        //If the game is currently paused, start it, change the button name to pause, and edit the status bar
        if(!isRunning){
            simulationTimeline.play();
            buttonStartPause.setText("Pause");
            isRunning = true;
            statuslabel.textProperty().bind(createStatusBar("RUNNING"));
            statuslabel.getStyleClass().remove("paused");
            statuslabel.getStyleClass().add("running");

        //If the game is currently running, pause it, change the button name to start, and edit the status bar
        }else{
            simulationTimeline.pause();
            buttonStartPause.setText("Resume");
            isRunning = false;
            statuslabel.textProperty().bind(createStatusBar("PAUSED"));
            statuslabel.getStyleClass().add("paused");
            statuslabel.getStyleClass().remove("running");
        }
    }

    /**
     * This function is called when the user clicks the stop button.  This function will stop the game, reset the buttons,
     * TextFields, and it will prompt the user that the game is over.
     */
    @FXML private void onStopClick(){
        if(simulationTimeline != null){
            simulationTimeline.stop();
        }
        isRunning = false;
        buttonStartPause.setText("Start");

        //Reset the Status Bar
        statuslabel.textProperty().bind(createStatusBar("READY"));
        statuslabel.getStyleClass().remove("paused");
        statuslabel.getStyleClass().remove("running");

        //Enable the Buttons
        buttonConfirm.setDisable(false);
        buttonStartPause.setDisable(true);
        buttonStop.setDisable(true);

        //Enable and clear the textfields
        setInputsDisabled(false, true);

        //Reset garden stats
        garden.setCurrentGreenHouseTemp(80);
        temp.clear();
        rain.clear();
        garden.getWateringSystem().rain(garden, getIntValue(rain));
        garden.resetRainTimer();
        clearGarden();


        garden.log("--- End of Simulation Session ---");

        StringBuilder report = garden.getDeathReport();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(report.toString());
        alert.showAndWait();
    }

    @FXML private void onRainClick(){
        garden.getWateringSystem().rain(garden, getIntValue(rain));
        garden.resetRainTimer();
    }

    @FXML private void onInfestClick(){
        String selectedPest = (String) spawnPests.getValue();
        if(selectedPest == null){
            System.out.println("Warning: No Pest Selected!");
            return;
        }

        Pest p = Pest.find(selectedPest);
        if(p != null) {
            garden.addPest(p);
        }
    }

    @FXML private void onTempClick(){
        garden.setCurrentGreenHouseTemp(getIntValue(temp));
    }

    /**
     * This function takes care of randomly planting a family of plants on the grid.
     * It will pick randomly locations and check if they are available.  If they are,
     * it will generate a random plant within the family and then place it in that location.
     * @param count Number of plants to plant
     * @param plant The family of plants you want to plant.  Use: Nightshade::new
     * @param rand A generic random number generator
     */
    private void plantFamily(int count, Supplier<Plant> plant, Random rand){
        int placed = 0;
        while(placed < count){
            int r = 1 + rand.nextInt(Garden.ROWS - 2);
            int c = 1 + rand.nextInt(Garden.COLS - 2);

            if(garden.getPlantAt(r,c) == null){
                Plant newplant = plant.get();
                garden.setPlantAt(r, c, newplant);

                //Add hydration bar to each plant
                ProgressBar hydrationBar = new ProgressBar(1.0);
                hydrationBar.getStyleClass().add("hydration-bar");
                hydrationBar.progressProperty().bind(newplant.getWaterProgressBar());

                //Add health bar to each plant
                ProgressBar healthBar = new ProgressBar(1.0);
                healthBar.getStyleClass().add("health-bar");
                healthBar.progressProperty().bind(newplant.getHealthProgressBar());

                //Add image and both bars to a vbox
                ImageView plantView = GridUIHelper.createTile(newplant.getImage(),Garden.PLANT_SIZE);
                VBox plantContainer = new VBox(3);
                plantContainer.setAlignment(Pos.BOTTOM_CENTER);
                plantContainer.getChildren().addAll(healthBar, hydrationBar, plantView);

                //Ensure that each plant is actually clickable to be moveable
                movePlant(plantContainer, newplant);

                //Add a listener to the heath progress bar
                newplant.getaliveProperty().addListener((obs,wasAlive,isAlive) -> {
                    if(!isAlive){
                        garden.addDeath(newplant);
                        int row = GridPane.getRowIndex(plantContainer);
                        int col = GridPane.getColumnIndex(plantContainer);
                        garden.setPlantAt(row,col,null);
                        Platform.runLater(() -> mainGrid.getChildren().remove(plantContainer));
                    }
                });
                mainGrid.add(plantContainer,c,r);
                placed++;
            }
        }
    }

    /**
     * This helper funciton allows each plant, when planted, to be selectable by a mouse click and hence moveable.
     * This function must be called when each plant is created otherwise a user would be unable to interact with the plants.
     * @param view an ImageView
     * @param plant a Plant object
     */
    private void movePlant(Node view, Plant plant){
        view.setOnMouseClicked(e->{
            if(startedOnce) return;
            if(selectedPlantView == null){
                selectedPlantView = view;
                selectedPlant = plant;
                view.setEffect(new Glow(0.8));
                showPlacementGrid(true);
            }else if(selectedPlantView == view){
                deselectPlant();
            }
        });
    }

    /**
     * This function will move a plant to the new location which is determined by which grid square a user clicks on
     * after selecting the specific plant to be moved.
     * @param row automatically determined row index
     * @param col automatically determined column index
     */
    private void moveSelectedPlantTo(int row, int col){
        if(row == 0 || row == Garden.ROWS - 1 || col == 0 || col == Garden.COLS - 1){
            return;
        }
        int oldRow = GridPane.getRowIndex(selectedPlantView);
        int oldCol = GridPane.getColumnIndex(selectedPlantView);

        garden.setPlantAt(oldRow,oldCol,null);
        garden.setPlantAt(row,col,selectedPlant);

        ImageView targetSoil = soilTiles[row][col];
        targetSoil.setImage(ResourceManager.getdampSoilImage());
        targetSoil.setEffect(new Glow(0.2));
        targetSoil.toFront();

        GridPane.setConstraints(selectedPlantView,col,row);
        selectedPlantView.toFront();
        deselectPlant();
    }

    /**
     * This function will allow a user to deselect a plant.
     */
    private void deselectPlant(){
        if(selectedPlantView != null){
            selectedPlantView.setEffect(null);
            selectedPlantView = null;
            selectedPlant = null;
            showPlacementGrid(false);
        }
    }

    /**
     * This function sets an effect on the empty grid squares which allows the user to tell which grid squares are available
     * for a plant to be placed there.
     * @param show True: shows the grid, False: hides the grid
     */
    private void showPlacementGrid(boolean show) {
        for (int r = 1; r < Garden.ROWS - 1; r++) {
            for (int c = 1; c < Garden.COLS - 1; c++) {
                if (garden.getPlantAt(r, c) == null) {
                    soilTiles[r][c].setEffect(show ? new Glow(0.8) : new Glow(0.2));
                }
            }
        }
    }

    /**
     * This function will fill the center GridPane with dirt and soil images, the playable area.
     * @param rows The number of rows in the garden.  Automatically filled.
     * @param cols The number of columns in the garden.  Automatically filled.
     */
    private void fillGardenPlot(int rows, int cols){
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                //Underlying dirt image
                ImageView baseDirt = GridUIHelper.createTile(ResourceManager.getdampSoilImage(),Garden.SOIL_SIZE);
                mainGrid.add(baseDirt,c,r);

                //Image above the base layer
                final ImageView tile;

                //Border of Playable area
                if(r == 0 || r == rows - 1 || c == 0 || c == cols - 1){
                    tile = GridUIHelper.createTile(ResourceManager.getmossyPathImage(),Garden.SOIL_SIZE);
                //Playable area
                }else {
                    tile = GridUIHelper.createTile(ResourceManager.getdampSoilImage(), Garden.SOIL_SIZE);
                }

                //Allows each tile to be clickable, so plants can be moved by the user before the game starts
                tile.setOnMouseClicked(e -> {
                    if (selectedPlantView != null && !startedOnce) {
                        int targetCol = GridPane.getColumnIndex(tile);
                        int targetRow = GridPane.getRowIndex(tile);
                        if (garden.getPlantAt(targetRow, targetCol) == null) {
                            moveSelectedPlantTo(targetRow, targetCol);
                        }
                    }
                });

                //Add a glowing effect to every tile except the border tiles
                soilTiles[r][c] = tile;
                if(r > 0 && r < rows - 1 && c > 0 && c < cols - 1){
                   tile.setEffect(new Glow(0.2));
                }
                //Add the tile to the grid
                mainGrid.add(tile,c,r);
            }
        }
        //Add the farmer to the board
        Image farmerImg = new Image(getClass().getResourceAsStream("/Assets/BaseTextures/Farmer.png"));
        farmer = new ImageView(farmerImg);
        farmer.setFitHeight(Garden.SOIL_SIZE *2);
        farmer.setPreserveRatio(true);
        mainGrid.add(farmer, farmerRow, farmerCol);
        farmer.toFront();
    }

    /**
     * Function to have the farmer randomly move
     */
    private void moveFarmerRandomly(){
        switch(walkDirection){
            case 0 -> farmerCol++;
            case 1 -> farmerRow++;
            case 2 -> farmerCol--;
            case 3 -> farmerRow--;
        }
        if(farmerCol >= Garden.COLS-1 && farmerRow <= 0){
            walkDirection = 1;
        }else if(farmerRow >= Garden.ROWS-1 && farmerCol >= Garden.COLS - 1){
            walkDirection = 2;
        }else if(farmerCol <= 0 && farmerRow >= Garden.ROWS - 1){
            walkDirection = 3;
        }else if(farmerRow <= 0 && farmerCol <= 0){
            walkDirection = 0;
        }
        GridPane.setConstraints(farmer, farmerCol, farmerRow);
        farmer.toFront();
    }

    /**
     * Function will get a random leave image and the animate it falling down the screen and rotating.
     */
    private void spawnLeaf(){
        Image img = ResourceManager.getLeaf();
        ImageView leafView = new ImageView(img);

        leafView.setSmooth(true);

        double size = 20 + Math.random() * 40;
        leafView.setFitWidth(size);
        leafView.setPreserveRatio(true);

        double startX = Math.random() * leafOverlay.getWidth();
        leafView.setX(startX);
        leafView.setY(-50);

        leafOverlay.getChildren().add(leafView);

        TranslateTransition fall = new TranslateTransition(
                Duration.seconds(8 + Math.random() * 4), leafView);
        fall.setByY(leafOverlay.getHeight() + 150);
        fall.setInterpolator(Interpolator.LINEAR);

        TranslateTransition drift = new TranslateTransition(
                Duration.seconds(4 + Math.random() * 2), leafView);
        drift.setByX(-100 + Math.random() * 200);
        drift.setAutoReverse(true);
        drift.setCycleCount(Animation.INDEFINITE);
        drift.setInterpolator(Interpolator.EASE_BOTH);

        RotateTransition rotate = new RotateTransition(
                Duration.seconds(5 + Math.random() * 3), leafView);
        rotate.setByAngle(360 * (Math.random() > 0.5 ? 1 : -1));
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        ParallelTransition pt = new ParallelTransition(leafView, fall, drift, rotate);
        fall.setOnFinished(e -> {
            pt.stop();
            leafOverlay.getChildren().remove(leafView);
            leafView.setImage(null);
        });
        pt.play();
    }

    private String getPestString(){
        return garden.getcurrentPests().isEmpty() ? "None" :
                garden.getcurrentPests().stream().map(Pest::getName).collect(Collectors.joining(", "));
    }

    private StringBinding createStatusBar(String status){
        return Bindings.createStringBinding(() -> String.format(
                "STATUS: %s%nTemp: %.1f°F,\tLast Rain: %ds ago%nPests: %s",
                status, garden.getcurrentGreenHouseTemp1().get(),
                garden.ticksSinceLastRain().get(), getPestString()
            ),
            garden.getcurrentGreenHouseTemp1(), garden.getcurrentPests(), garden.ticksSinceLastRain()
        );
    }

    @FXML public void initialize (URL url, ResourceBundle rb) {
        //Initialize ComboBox

        spawnPests.getItems().clear();
        for(Pest p : Pest.values()){
            spawnPests.getItems().add(p.getName());
        }

        //Spawn Leaves
        ResourceManager.initializeLeaves();
        leafOverlay.setMouseTransparent(true);
        leafOverlay.setPickOnBounds(false);
        Timeline leafSpawner = new Timeline(new KeyFrame(Duration.seconds(2),e-> spawnLeaf()));
        leafSpawner.setCycleCount(Timeline.INDEFINITE);
        leafSpawner.play();

        //Load in CSS styling
        String css = GardenController.class.getResource("/style.css").toExternalForm();
        rootContainer.getStylesheets().add(css);

        //Restrict TextFields to only accept int and validate the total of all TextFields
        initializeTextFields();

        //Initially disable start and stop buttons until the user has clicked the button confirm
        buttonStartPause.setDisable(true);
        buttonStop.setDisable(true);

        //Status label at the top of the screen
        statuslabel = new Label();
        statuslabel.textProperty().bind(createStatusBar("READY"));
        statuslabel.getStyleClass().add("status-label");
        rootContainer.getChildren().add(statuslabel);
        StackPane.setAlignment(statuslabel,Pos.TOP_CENTER);
        StackPane.setMargin(statuslabel, new Insets(10,0,0,0));

        //Static Grass Background outside the playable area
        ImagePattern pattern = new ImagePattern(ResourceManager.getsoilGrassyImage(), 0, 0, 256, 256, false);
        rootContainer.setBackground(new Background(new BackgroundFill(pattern, null, null)));

        //User Dashboard on left side
        dashboard.getStyleClass().add("dashboard-panel");
        dashboard.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setMargin(dashboard, new Insets(10,0,0,10));
        StackPane.setAlignment(dashboard, Pos.TOP_LEFT);

        dashboard1.getStyleClass().add("dashboard-panel");
        dashboard1.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setMargin(dashboard1, new Insets(10,10,0,0));
        StackPane.setAlignment(dashboard1, Pos.TOP_RIGHT);

        //Fixes some scaling issues when the window is resized
        Scale scale = new Scale(1, 1, 0, 0);
        mainStackPane.getTransforms().add(scale);
        rootContainer.widthProperty().addListener((o, oldW, newW) -> GridUIHelper.updateScale(scale, rootContainer, mainStackPane));
        rootContainer.heightProperty().addListener((o, oldH, newH) -> GridUIHelper.updateScale(scale, rootContainer, mainStackPane));

        //Depth Effect for the playable area (Creates a shadow between the grass and mossy path
        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20.0);
        shadow.setChoke(0.3);
        shadow.setColor(Color.color(0,0,0,0.6));
        mainGrid.setEffect(shadow);
        mainGrid.setCache(true);
        mainGrid.setCacheHint(CacheHint.SPEED);

        /*
        Fix the size of the Grid to match the size of the soil.  Without this, GridPane will try to resize its columns
        and rows to fit the size of the content which can cause some scaling issues.
         */
        GridUIHelper.prepareGridConstraints(mainGrid, Garden.ROWS, Garden.COLS);

        //Fill the garden with soil and create the mossy path border
        fillGardenPlot(Garden.ROWS, Garden.COLS);
    }
}
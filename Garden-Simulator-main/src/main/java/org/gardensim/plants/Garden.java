package org.gardensim.plants;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.gardensim.systems.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Garden {
    public static final int ROWS = 20;
    public static final int COLS = 30;
    public static final int SOIL_SIZE = 50;
    public static final int PLANT_SIZE = 40;

    private int currentDay = 0;

    private final DoubleProperty currentGreenHouseTemp1;
    private double currentGreenHouseTemp;
    private final TemperatureSystem tempSystem;
    private final WateringSystem wateringSystem;
    private final PestControlSystem pestSystem;

    private final IntegerProperty currentTick = new SimpleIntegerProperty(0);
    private final IntegerProperty ticksSinceLastRain = new SimpleIntegerProperty(0);


    private final ObservableList<Pest> currentPests = FXCollections.observableArrayList();
    private final Map<Pest, Integer> pesticideInv = new HashMap<>();
    private final Map<String, Integer> deathToll = new HashMap<>();
    private double storedWaterInLiters;

    private PrintWriter output;
    private final Plant[][] gardenLogicMap = new Plant[ROWS][COLS];

    public Garden() {
        currentGreenHouseTemp = 80;
        currentGreenHouseTemp1 = new SimpleDoubleProperty(80);
        tempSystem = new TemperatureSystem();
        wateringSystem = new WateringSystem();
        pestSystem = new PestControlSystem();
        try {
            output = new PrintWriter(new BufferedWriter(new FileWriter("sim-logs.txt", true)), true);
        } catch (IOException e) {
            System.out.println("Could not open log file");
        }
        pesticideInv.put(Pest.APHIDS, 10);
        pesticideInv.put(Pest.SCALE, 10);
        pesticideInv.put(Pest.BEETLE, 10);
        pesticideInv.put(Pest.CATERPILLAR, 10);
        pesticideInv.put(Pest.SNAIL, 10);

        storedWaterInLiters = 100000;
    }


    public IntegerProperty currentTickProperty(){return currentTick;}
    public IntegerProperty ticksSinceLastRain(){return ticksSinceLastRain;}

    public void updateTick(){currentTick.set(currentTick.get() + 1);}
    public void updateRainTick(){ticksSinceLastRain.set(ticksSinceLastRain.get() + 1);}

    public void advanceTime(){
        updateTick();
        updateRainTick();
    }

    public void log(String message){
        if(output != null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String timestamp = dtf.format(LocalDateTime.now());

            int tickInDay = currentTick.get() % GardenSimulationAPI.TICKS_PER_DAY;
            output.println("[" + timestamp + "] Tick " + tickInDay + ": " + message);
            output.flush();
        }
    }

    public void resetRainTimer(){
        ticksSinceLastRain.set(0);
    }

    public void addDeath(Plant plant){
        deathToll.put(plant.getDisplayName(), deathToll.getOrDefault(plant.getDisplayName(),0)+1);
    }

    public int getDeathToll() {
        return deathToll.values().stream().mapToInt(Integer::intValue).sum();
    }

    public StringBuilder getDeathReport(){
        StringBuilder report = new StringBuilder("Simulation Results:\n");
        deathToll.forEach((type, count) ->{
            report.append(type).append(" Deaths: ").append(count).append("\n");
        });
        deathToll.clear();
        return report;
    }

    public List<Pest> getCurrentPests() {
        return currentPests;
    }

    // pass in pest and decrease that pest's pesticide by 1. return 1 if successful (meaning there is at least 1). else return 0
    public boolean usePesticide(Pest pest){
        if(pesticideInv.get(pest) > 0 ){
            pesticideInv.put(pest, pesticideInv.get(pest)-1);
            return true;
        }else{
            return false;
        }
    }

    public void removePest(Pest pest){
        if(currentPests.contains(pest)) {
            currentPests.remove(pest);
        }else{
            System.out.println("This pest is not currently in the garden");
        }
    }

    public void addPest(Pest pest){
        //Prevent concurrent modification of currentPests
        synchronized (this) {
            currentPests.add(pest);
        }
    }

    public ObservableList<Pest> getcurrentPests(){
        return currentPests;
    }

    public void updateAllSystems() {
        tempSystem.update(this);
        wateringSystem.update(this);
        pestSystem.update(this);
    }

    public void updateAllPlants(){
        synchronized (this) {
            updateAllSystems();
            updateTick();
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    Plant p = gardenLogicMap[r][c];
                    if (p != null) {
                        if (p.getTicksSinceWatered() % 20 == 0) {
                            p.setWaterLevel(p.getWaterLevel() - .05);
                        }
                        p.updateWaterProgressBar();
                        if(p.updateHealth(currentGreenHouseTemp, currentPests)){
                            logPlantDeath(p,r,c);
                        }
                        p.setTicksSinceWatered(p.getTicksSinceWatered() + 1);
                    }
                }
            }
        }
    }

    private void logPlantDeath(Plant p, int r, int c){
        String timeStamp = String.format("Day %d [Tick %d]", currentDay, currentTick.get());
        String deathMsg = String.format("[%s] DEATH: %s at [%d, %d] has perished.",timeStamp, p.getDisplayName(),r,c);
        this.log(deathMsg);
        System.out.println(deathMsg);
    }

    public int plantsAlive(){
        int count = 0;
        for(Plant[] row: gardenLogicMap){
            for(Plant p: row){
                if(p != null && p.getalive()) count++;
            }
        }
        return count;
    }

    public void clearGarden(){
        for(int r=0; r<ROWS; r++) {
            Arrays.fill(gardenLogicMap[r], null);
        }
    }

    public void printGarden(){
        for(int r=0; r<ROWS; r++){
            for(int c=0; c<COLS; c++){
                if(gardenLogicMap[r][c] != null)
                    System.out.print(gardenLogicMap[r][c].displayName + " ");
                System.out.print("-");
            }
            System.out.println();
        }
    }

    public Plant getPlantAt(int r, int c){
        return gardenLogicMap[r][c];
    }

    public void setPlantAt(int r, int c, Plant plant){
        gardenLogicMap[r][c] = plant;
    }

    public double getCurrentGreenHouseTemp() {
        return currentGreenHouseTemp;
    }

    public DoubleProperty getcurrentGreenHouseTemp1(){
        return currentGreenHouseTemp1;
    }

    public void setCurrentGreenHouseTemp(double currentGreenHouseTemp) {
        if(currentGreenHouseTemp >= 40 && currentGreenHouseTemp <= 120){
            log("[TEMPERATURE SYSTEM] Temperature changed to " + currentGreenHouseTemp + " from " + this.currentGreenHouseTemp);
            this.currentGreenHouseTemp = currentGreenHouseTemp;
            this.currentGreenHouseTemp1.set(currentGreenHouseTemp);
        }else{
            log("[TEMPERATURE SYSTEM] Input temperature " + currentGreenHouseTemp + " out of range (40 <= x <= 120).");
            System.out.println("[TEMPERATURE SYSTEM] Input tempewrature " + currentGreenHouseTemp + " out of range (40 <= x <= 120).");
        }
    }

    public WateringSystem getWateringSystem() {
        return wateringSystem;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public boolean isRainingToday() {
        return this.getCurrentDay() == this.getWateringSystem().getLastRainDay();
    }

    public double getStoredWaterInLiters() {
        return storedWaterInLiters;
    }

    public void setStoredWaterInLiters(double storedWaterInLiters) {
        this.storedWaterInLiters = storedWaterInLiters;
    }
}
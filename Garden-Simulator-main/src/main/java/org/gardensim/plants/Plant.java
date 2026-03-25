package org.gardensim.plants;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.util.List;

public abstract class Plant {
    protected String displayName;
    protected List<Integer> tempRange;
    protected int waterRequirements;

    protected List<Pest> pests;     //List of Vulnerable Pests
    private double waterLevel;
    private double healthLevel = 1.0;

    private final DoubleProperty waterProgressBar;
    private final DoubleProperty healthProgressBar = new SimpleDoubleProperty(1.0);
    private final BooleanProperty pestInfested = new SimpleBooleanProperty(false);
    private final BooleanProperty alive = new SimpleBooleanProperty(true);
    private int ticksSinceWatered;

    protected Plant(String name, List<Integer> tempRange, int waterReq, List<Pest> pests){
        this.displayName = name;
        this.waterLevel = waterReq;
        this.tempRange = tempRange;
        this.waterRequirements = waterReq;
        this.pests = pests;
        this.waterProgressBar = new SimpleDoubleProperty( (waterReq != 0) ? waterLevel/waterReq : 0.0);
        this.ticksSinceWatered = 0;
    }

    public boolean updateHealth(double greenHouseTemperature, List<Pest> currentPests){
        if(!getalive()) return false;

        int vulnerablePestCount = (currentPests == null) ? 0 : (int)currentPests.stream().filter(this.pests::contains).count();

        boolean outOfRange = false;
        if( greenHouseTemperature > tempRange.get(1) || greenHouseTemperature < tempRange.get(0)){
            outOfRange = true;
        }

        //double totalDamage = vulnerablePestCount * 0.01 + (outOfRange ? 1 : 0) * 0.02 + (waterLevel < 1 ? 1 : 0) * 0.01;
        double totalDamage = vulnerablePestCount * 0.0005 + (outOfRange ? 0.0001 : 0)  + (this.waterLevel < 1 ? 0.0004 : 0);

        this.healthLevel -= totalDamage;

        if(this.healthLevel <= 0){
            this.healthLevel = 0;
            this.setalive(false);
            return true;
        }

        // if no damage taken apply natural healing
        if(totalDamage == 0) {
            this.healthLevel += 0.002;
        }
        if(this.healthLevel > 1.0){
            this.healthLevel = 1.0;
        }
        // sync progress bar for UI
        this.setHealthProgressBar(healthLevel);

        return false;
    }

    public abstract Image getImage();
    public abstract Enum<?> getType();

    public String getDisplayName() {return displayName;}
    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public List<Integer> getTempRange() {return tempRange;}
    public void setTempRange(List<Integer> tempRange) {this.tempRange = tempRange;}

    public int getWaterRequirements() {return waterRequirements;}
    public void setWaterRequirements(int waterRequirements) {this.waterRequirements = waterRequirements;}

    public List<Pest> getPests() {return pests;}
    public void setPests(List<Pest> pests) {this.pests = pests;}

    public void setWaterLevel(double waterLevel){this.waterLevel=waterLevel;}
    public double getWaterLevel(){return waterLevel;}

    public double getHealthLevel(){return healthLevel;}
    public void setHealthLevel(double healthLevel){this.healthLevel=healthLevel;}

    public DoubleProperty getWaterProgressBar() {
        return waterProgressBar;
    }

    public void updateWaterProgressBar() {
        double progress = this.waterLevel / this.waterRequirements;
        if( progress < 0){
            progress = 0;
        }
        if(progress > 1.0){
            progress = 1.0;
        }
        waterProgressBar.set(progress);
    }

    public DoubleProperty getHealthProgressBar() {
        return healthProgressBar;
    }

    public void setHealthProgressBar(double value) {
        if(value < 0) {
            value = 0;
        }
        if(value > 1.0){
            value = 1.0;
        }
        healthProgressBar.set(value);
    }

    public void setpestInfested(boolean pestInfested){this.pestInfested.set(pestInfested);}
    public boolean getpestInfested(){return pestInfested.get();}
    public BooleanProperty getpestInfestedProperty(){return pestInfested;}

    public void setalive(boolean alive){this.alive.set(alive);}
    public boolean getalive(){return alive.get();}
    public BooleanProperty getaliveProperty(){return alive;}

    public int getTicksSinceWatered() {
        return this.ticksSinceWatered;
    }

    public void setTicksSinceWatered(int value) {
        ticksSinceWatered = value;
    }
}
package org.gardensim.systems;

import org.gardensim.plants.Garden;
import org.gardensim.plants.Plant;

import static org.gardensim.plants.Garden.ROWS;
import static org.gardensim.plants.Garden.COLS;

public class WateringSystem {

    private int lastRainDay = -1;

    // rain will update every plant by amount no matter what
    public void rain(Garden garden, int amount) {
        lastRainDay = garden.getCurrentDay();
        System.out.println("[WATERING SYSTEM] Rain detected. Watering all plants by " + amount + " units. Garden's stored water increased by " + amount * 100 + ".");
        garden.log("[WATERING SYSTEM] Rain detected. Watering all plants by " + amount + " units. Garden's stored water increased by " + amount * 100 + ".");
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant p = garden.getPlantAt(r, c);
                if (p != null) {
                    p.setWaterLevel(p.getWaterLevel() + amount);
                }
            }
        }
        garden.setStoredWaterInLiters(garden.getStoredWaterInLiters() + amount * 100);
    }

    public void update(Garden garden) {
        waterAllPlants(garden);
    }

    private int lastLogDay = -1;

    // smart sprinkler system that will water as much as needed
    public void waterAllPlants(Garden garden) {
        if(garden.getCurrentDay() == this.lastRainDay) {
            if(lastLogDay != garden.getCurrentDay()){
                garden.log("[WATERING SYSTEM] Raining today. If Plant has enough water, skipping watering.");
                System.out.println("[WATERING SYSTEM] Raining today. If Plant has enough water, skipping watering.");
                lastLogDay = garden.getCurrentDay();
            }
        }

        int numWateredPlants = 0;
        double totalWaterUsed = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant p = garden.getPlantAt(r, c);
                if (p != null && p.getalive()) {
                    // Only water if the plant is below 70% of its target
                    double threshold = p.getWaterRequirements() * 0.7;

                    if (p.getWaterLevel() < threshold) {
                        double amountNeeded = p.getWaterRequirements() - p.getWaterLevel();

                        // Check if the garden has enough stored water
                        if (garden.getStoredWaterInLiters() >= amountNeeded) {
                            p.setWaterLevel(p.getWaterRequirements());
                            garden.setStoredWaterInLiters(garden.getStoredWaterInLiters() - amountNeeded);
                            numWateredPlants++;
                            totalWaterUsed += amountNeeded;
                        }else{
                            garden.log("[WATERING SYSTEM] Insufficient water left in storage tanks to water the current plant.");
                            System.out.println("[WATERING SYSTEM] Insufficient water left in storage tanks to water the current plant.");
                        }
                    }
                }
            }
        }
        // Only log if the sprinklers actually HAD to do something
        if (numWateredPlants > 0) {
            String msg = String.format("[WATERING SYSTEM] Sprinklers active: Watered %d plants using %.2fL. Reservoir: %.1fL",
                    numWateredPlants, totalWaterUsed, garden.getStoredWaterInLiters());
            garden.log(msg);
            System.out.println(msg);
        }
    }

    public int getLastRainDay() {
        return lastRainDay;
    }
}

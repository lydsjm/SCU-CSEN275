package org.gardensim;

import java.util.Map;
import java.util.Random;

import org.gardensim.systems.GardenSimulationAPI;

public class GardenSimulator {
    private static final Random random = new Random();
    private static final String[] PEST_TYPES = {"Aphid", "Scale", "Beetle", "Caterpillar", "Snail"};

    public static void main(String[] args){
        GardenSimulationAPI api = new GardenSimulationAPI();
        api.setSimulationSpeed(1);

        api.initializeGarden();

        Map<String, Object> initialPlantDetails = api.getPlants();
        System.out.println("Plants Loaded: " + initialPlantDetails.get("plants"));


        random24HrTest(api);
        //custom24HrTest(api); //Needs to be filled in with api calls


        System.out.println("--- 24-HOUR STRESS TEST COMPLETE ---");
    }

    public static void random24HrTest(GardenSimulationAPI api){
        for (int day = 1; day <= 24; day++) {
            System.out.println("\n--- SIMULATOR: Starting Day " + day + " ---");
            int eventType = random.nextInt(4);
            api.getState();
            switch (eventType) {
                case 0: // Rain Event
                    int rainAmount = 10 + random.nextInt(41); // 10 to 50 units
                    System.out.println("Simulator Action: Triggering Rain (" + rainAmount + ")");
                    api.rain(rainAmount);
                    break;

                case 1: // Temperature Event
                    int newTemp = 40 + random.nextInt(81); // 40 to 120 degrees
                    System.out.println("Simulator Action: Setting Temperature to " + newTemp + "°F");
                    api.temperature(newTemp);
                    break;

                case 2: // Pest Outbreak
                    String pest = PEST_TYPES[random.nextInt(PEST_TYPES.length)];
                    System.out.println("Simulator Action: Spawning " + pest + " infestation!");
                    api.parasite(pest);
                    break;

                case 3: // Mixed Conditions (Tough day for the garden)
                    System.out.println("Simulator Action: Extreme Day (Rain + Pests)");
                    api.rain(15);
                    api.parasite(PEST_TYPES[random.nextInt(PEST_TYPES.length)]);
                    break;
            }
            api.sleepOneHour();
        }
    }

    public static void custom24HrTest(GardenSimulationAPI api){
        //Start of Day 1
        api.getState();
        api.temperature(40);
        api.rain(25);

        api.sleepOneHour();
        //End of Day 1

        //Start of Day 2
        api.getState();
        api.temperature(40);
        api.parasite("Aphid");

        api.sleepOneHour();
        //End of Day 2

        //Start of Day 3
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 3

        //Start of Day 4
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 4

        //Start of Day 5
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 5

        //Start of Day 6
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 6

        //Start of Day 7
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 7

        //Start of Day 8
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 8

        //Start of Day 9
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 9

        //Start of Day 10
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 10

        //Start of Day 11
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 11

        //Start of Day 12
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 12

        //Start of Day 13
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 13

        //Start of Day
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 14

        //Start of Day 15
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 15

        //Start of Day 16
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 16

        //Start of Day 17
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 17

        //Start of Day 18
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 18

        //Start of Day 19
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 19

        //Start of Day 20
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 20

        //Start of Day 21
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 21

        //Start of Day 22
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 22

        //Start of Day 23
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 23

        //Start of Day 24
        api.getState();
        api.rain(25);

        api.sleepOneHour();
        //End of Day 24
    }
}

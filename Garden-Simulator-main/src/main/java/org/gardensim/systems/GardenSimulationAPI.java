package org.gardensim.systems;

import org.gardensim.plants.Garden;
import org.gardensim.plants.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GardenSimulationAPI {
    private final Map<String, Supplier<Plant>> registry = new HashMap<>();
    private final Garden garden;
    private final Random rand = new Random();

    private final ScheduledExecutorService scheduler;
    private final AtomicInteger dayCounter = new AtomicInteger(0);
    private final AtomicInteger totalTicks = new AtomicInteger(0);

    public static final int TICKS_PER_DAY = 3600; //3600 seconds (ticks) per hour = 1 sim day
    private static final int TICK_INTERVAL_MS = 1000; //1 second will correlate to 1 minute in simulation
    private double timeScale = 1.0;

    public GardenSimulationAPI(){
        this.garden = new Garden();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        initializeRegistry();
    }

    private void initializeRegistry(){
        registry.put("KALE", () -> new Cruciferae(CruciferaeType.KALE));
        registry.put("RADISH", () -> new Cruciferae(CruciferaeType.RADISH));
        registry.put("CABBAGE", () -> new Cruciferae(CruciferaeType.CABBAGE));
        registry.put("TOMATO", () -> new Nightshade(NightshadeType.TOMATO));
        registry.put("CHILIPEPPER", () -> new Nightshade(NightshadeType.CHILIPEPPER));
        registry.put("CARROT", () -> new Umbelliferae(UmbelliferaeType.CARROT));
        registry.put("COWPARSLEY", () -> new Umbelliferae(UmbelliferaeType.COWPARSLEY));
        registry.put("CORIANDER", () -> new Umbelliferae(UmbelliferaeType.CORIANDER));
    }

    /**
     * Allows changing the timescale anywhere from 1x to 10x speeds.
     * @param scale A double value in the range 1.0 to 10.0
     */
    public void setSimulationSpeed(double scale){
        this.timeScale = Math.min(Math.max(1.0, scale), 10.0);
        System.out.println("[SYSTEM] Setting simulation speed to " + this.timeScale +"x");
        garden.log("[SYSTEM] Setting simulation speed to " + this.timeScale +"x");
    }


    private void verifyTotal(JSONArray plants){
        int sum = 0;
        int max = Garden.ROWS * Garden.COLS;
        for(int i=0; i < plants.length(); i++){
            JSONObject configPlant = plants.getJSONObject(i);
            sum += configPlant.getInt("amount");
        }
        if(sum > max){
            throw new RuntimeException("Too many plants were specified in the config.  A maximum of " + max + " can be used.");
        }
    }


    public void initializeGarden(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            garden.log("--- PROGRAM ENDED ABRUPTLY: JVM is shutting down.  Reason: User closed the program. ---");
            getState();
            System.out.println("Shutdown hook executed.  Log Saved.");
        }));

        initializeRegistry();
        try (InputStream is = getClass().getResourceAsStream("/config.json")){
            if(is == null) throw new FileNotFoundException("config.json not found");

            JSONObject root = new JSONObject(new JSONTokener(is));
            JSONArray plants = root.getJSONArray("plants");
            verifyTotal(plants);

            for(int i=0; i<plants.length(); i++){
                JSONObject configPlant = plants.getJSONObject(i);
                String plantName = configPlant.getString("name");
                int count = configPlant.getInt("amount");

                Supplier<Plant> plant = registry.get(plantName.toUpperCase());
                if(plant != null){
                    spawnPlants(plant, count);
                }
            }
            garden.log("--- SIMULATION STARTING ---");
            startInternalClock();
        }catch(Exception e){
            System.out.println("Error opening file: " + e);
        }
    }

    private void spawnPlants(Supplier<Plant> supplier, int count){
        int placed = 0;
        while(placed < count) {
            int r = rand.nextInt(Garden.ROWS);
            int c = rand.nextInt(Garden.COLS);

            if (garden.getPlantAt(r, c) == null) {
                garden.setPlantAt(r, c, supplier.get());
                placed++;
            }
        }
    }

    private void startInternalClock(){
        int dynamicInterval = (int) (TICK_INTERVAL_MS / timeScale);
        scheduler.scheduleAtFixedRate(this::tick, 0, dynamicInterval, TimeUnit.MILLISECONDS);
    }

    private void tick(){
        try{
            int totalTick = totalTicks.incrementAndGet();
            int currentDay = (totalTick / TICKS_PER_DAY) + 1;
            dayCounter.set(currentDay);

            garden.updateAllPlants();

            if(totalTick % TICKS_PER_DAY == 0){
                garden.setCurrentDay(currentDay);
                garden.setCurrentGreenHouseTemp(80);
                garden.log("--- DAY " + dayCounter + " ---");
            }

            if(garden.plantsAlive() == 0){
                garden.log("--- SIMULATION ENDED: All plants have died. ---");
                scheduler.shutdown();
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Exception during tick: " + e.getMessage());
        }

    }

    public Map<String, Object> getPlants(){
        List<String> names = new ArrayList<>();
        List<Integer> waterReqs = new ArrayList<>();
        List<List<Pest>> pests = new ArrayList<>();

        for(int r=0; r<Garden.ROWS; r++){
            for(int c=0; c<Garden.COLS; c++){
                Plant p = garden.getPlantAt(r,c);
                if(p != null && p.getalive()){
                    names.add(p.getDisplayName());
                    waterReqs.add(p.getWaterRequirements());
                    pests.add(p.getPests());
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("plants",names);
        result.put("waterRequirement", waterReqs);
        result.put("parasites", pests);

        return result;
    }

    public void rain(int amount){
        garden.getWateringSystem().rain(garden, amount);
    }

    public void temperature(int value){
        garden.setCurrentGreenHouseTemp(value);
    }

    public void parasite(String parasite){
        Pest temp = Pest.find(parasite);
        if(temp != null){
            garden.addPest(temp);
        }else{
            garden.log(String.format("[API PARASITE ERROR] The pest '%s' is not valid",parasite));
        }
    }

    public void getState(){
        int alive = garden.plantsAlive();
        int dead = garden.getDeathToll();
        boolean isRaining = garden.isRainingToday();
        garden.log(String.format("DAY: %d, RAINING: %s, PLANTS_ALIVE: %d, PLANTS_DEAD: %d", dayCounter.get(), isRaining ? "Yes" : "No", alive, dead));
    }

    public void sleepOneHour(){
        try{
            long sleepTime = (long) (3600000 / this.timeScale);
            Thread.sleep(sleepTime);
            TimeUnit.SECONDS.sleep(2);  //Padding to allow background thread time ot finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted!");
        }
    }
}
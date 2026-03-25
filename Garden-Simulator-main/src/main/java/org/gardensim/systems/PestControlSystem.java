package org.gardensim.systems;

import org.gardensim.plants.Garden;
import org.gardensim.plants.Pest;
import org.gardensim.plants.Plant;

import java.util.List;
import java.util.Random;

import static org.gardensim.plants.Garden.ROWS;
import static org.gardensim.plants.Garden.COLS;

public class PestControlSystem {

    // could be string array for multiple parasites and then modify that array after treatment

    public void update(Garden garden) {
        List<Pest> currentPests = garden.getCurrentPests();
        if (currentPests != null && !currentPests.isEmpty()) {
            Pest randomPest = currentPests.get(new Random().nextInt(currentPests.size()));
            deployTreatment(garden, randomPest);
        }
    }

    public void deployTreatment(Garden garden, Pest pest) {
        if(garden.usePesticide(pest)) {
            garden.log("[PEST CONTROL SYSTEM] Deploying organic pesticide. " + pest.getName() + " cleared.");
            System.out.println("[PEST CONTROL SYSTEM] Deploying organic pesticide. " + pest.getName() + " cleared.");

            garden.removePest(pest);
        }
        else {
            garden.log("[PEST CONTROL SYSTEM] No pesticides left for " + pest.getName());
            System.out.println("[PEST CONTROL SYSTEM] No pesticides left for " + pest.getName());
        }
    }
}
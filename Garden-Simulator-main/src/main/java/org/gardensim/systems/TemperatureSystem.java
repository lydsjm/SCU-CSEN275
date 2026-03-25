package org.gardensim.systems;

import org.gardensim.plants.Garden;

public class TemperatureSystem {

    private boolean cooler = false;
    private boolean heater = false;
    private final double targetTemp = 80.0;
    private final double tempChangeRate = 1.9; // Slowed down for better simulation feel
    private final int tempBuffer = 4;

    public void update(Garden garden) {
        double currentTemp = garden.getCurrentGreenHouseTemp();

        if (currentTemp > targetTemp + tempBuffer) {
            if (!cooler) {
                garden.log("[TEMP SYSTEM] Temperature high (" + String.format("%.1f", currentTemp) + "°F). Turning COOLER ON.");
                System.out.println("[TEMP SYSTEM] Temperature high (" + String.format("%.1f", currentTemp) + "°F). Turning COOLER ON.");
                cooler = true;
                heater = false;
            }
        } else if (currentTemp < targetTemp - tempBuffer) {
            if (!heater) {
                garden.log("[TEMP SYSTEM] Temperature low (" + String.format("%.1f", currentTemp) + "°F). Turning HEATER ON.");
                System.out.println("[TEMP SYSTEM] Temperature low (" + String.format("%.1f", currentTemp) + "°F). Turning HEATER ON.");
                heater = true;
                cooler = false;
            }
        } else if (currentTemp <= targetTemp + tempBuffer && currentTemp >= targetTemp - tempBuffer) {
            if (cooler || heater) {
                garden.log("[TEMP SYSTEM] Target reached (" + String.format("%.1f", currentTemp) + "°F). Systems OFF.");
                System.out.println("[TEMP SYSTEM] Target reached (" + String.format("%.1f", currentTemp) + "°F). Systems OFF.");
                cooler = false;
                heater = false;
            }
        }
        if (cooler) {
            garden.setCurrentGreenHouseTemp(currentTemp - tempChangeRate);

            currentTemp = garden.getCurrentGreenHouseTemp();
            garden.log("[TEMP SYSTEM] Cooler on. Decreased temperature by " + tempChangeRate + ". Current temperature: " + String.format("%.1f", currentTemp) + "°F");
            System.out.println("[TEMP SYSTEM] Cooler on. Decreased temperature by " + tempChangeRate + ". Current temperature: " + String.format("%.1f", currentTemp) + "°F");
        } else if (heater) {
            garden.setCurrentGreenHouseTemp(currentTemp + tempChangeRate);

            currentTemp = garden.getCurrentGreenHouseTemp();
            garden.log("[TEMP SYSTEM] Heater on. Increased temperature by " + tempChangeRate + ". Current temperature: " + String.format("%.1f", currentTemp) + "°F");
            System.out.println("[TEMP SYSTEM] Heater on. Increased temperature by " + tempChangeRate + ". Current temperature: " + String.format("%.1f", currentTemp) + "°F");
        }
    }
}

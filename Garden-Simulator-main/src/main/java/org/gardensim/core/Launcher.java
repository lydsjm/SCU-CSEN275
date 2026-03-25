package org.gardensim.core;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("prism.verbose", "true");
        System.setProperty("javafx.animation.fullspeed", "false");
        javafx.application.Application.launch(Application.class, args);
    }
}

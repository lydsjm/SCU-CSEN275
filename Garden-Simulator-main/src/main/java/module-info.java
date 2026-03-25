module org.gardensim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;

    //Location of Launcher
    exports org.gardensim.core;

    //Location of Controller
    exports org.gardensim.controllers;
    opens org.gardensim.controllers to javafx.fxml;

    //Lets fxml reference classes in Plants
    exports org.gardensim.plants;
    opens org.gardensim.plants to javafx.fxml;

    exports org.gardensim.systems;
    opens org.gardensim.systems to javafx.fxml;

    exports org.gardensim.utils;
    opens org.gardensim.utils to javafx.fxml;
}
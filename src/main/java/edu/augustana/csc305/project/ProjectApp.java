package edu.augustana.csc305.project;

import edu.augustana.csc305.project.controller.AppController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ProjectApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        AppController appController = new AppController(primaryStage);
        appController.startApp();
    }
}

package com.plter.blackboard;

import com.plter.blackboard.controllers.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;

public class BlackBoard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("黑板");
        primaryStage.setScene(MainViewController.createScene());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

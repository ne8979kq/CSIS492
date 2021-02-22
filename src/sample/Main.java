/*
* Author: Ronni Kurtzhals
* Date: 12/11/2020
* Description: This program allows the user to play the card game 99. The user can play the game, save their progress in
* the game, and view different scores in the database. The user creates an account or logs in to play their own games.
 */
package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Major Project 3");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

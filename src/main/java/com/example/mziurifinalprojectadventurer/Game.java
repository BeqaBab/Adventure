package com.example.mziurifinalprojectadventurer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Game extends Application {
    public static String Initiate() throws FileNotFoundException {
        File obj = new File("C:\\Users\\beqab\\Mziuri-Final-Project-adventurer\\src\\main\\java\\Password");
        Scanner myReader = new Scanner(obj);
        String password = "";
        while(myReader.hasNext()){
            password = myReader.nextLine();
        }
        myReader.close();

        return password;
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        String url = "jdbc:postgresql://localhost:5432/Adventure";
        String user = "postgres";
        String password = Initiate();
        Button authorisationButton = new Button("Start to play");
        LoginPage loginPage = new LoginPage(url, user, password, primaryStage);
        authorisationButton.setOnAction(e -> {
            loginPage.authorisation();
            primaryStage.setTitle("Authorisation page");
        });
        Label welcomeLabel = new Label("Welcome to Adventure");
        welcomeLabel.setId("welcomeLabel");

        VBox decorTop = new VBox();
        decorTop.getStyleClass().add("decorative-top");

        VBox decorBottom = new VBox();
        decorBottom.getStyleClass().add("decorative-bottom");

        VBox layout = new VBox(15);
        layout.getChildren().addAll(decorTop, welcomeLabel, authorisationButton, decorBottom);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getStyleClass().add("vbox");

        Scene scene = new Scene(layout, 700, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Welcome_screen_dark.css")).toExternalForm());

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/GameIcon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }

        primaryStage.setTitle("Welcome!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package com.example.mziurifinalprojectadventurer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
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
    public void start(Stage stage) throws IOException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/Adventure";
        String user = "postgres";
        String password = Initiate();
        Login_page loginPage = new Login_page();
        Adventurer currentAdventurer = loginPage.authorisation(url, user, password);
    }

    public static void main(String[] args) {
        launch();
    }
}
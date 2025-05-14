package com.example.mziurifinalprojectadventurer;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Login_page {
    public static long hash(String s) {
        final int p = 31;
        final int m = 100000009;
        long hashValue = 0;
        long p_pow = 1;
        for (int i = 0; i < s.length(); i++) {
            hashValue = (hashValue + (s.charAt(i) - 'a' + 1) * p_pow) % m;
            p_pow = (p_pow * p) % m;
        }
        return hashValue;
    }

    Label userLabel = new Label("Name:");
    Label classLabel = new Label("Class:");
    Label passwordLabel = new Label("Password:");

    Label messageLabel = new Label();

    TextField nameField = new TextField();
    PasswordField passwordField = new PasswordField();

    String[] classes = {"Warrior", "Mage", "Assassin"};
    ChoiceBox<String> classChoiceBox =  new ChoiceBox<String>(FXCollections.observableArrayList(classes));

    GridPane grid = new GridPane();


    Adventurer authorisation(String url, String user, String password) throws SQLException{
        Adventurer currentAdventurer = new Adventurer();
        grid.setVgap(10);
        grid.setHgap(10);
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = nameField.getText();
            String userPassword = passwordField.getText();
            String adventurerClass = classChoiceBox.getValue();
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                if(classChoiceBox.getValue().equals("Warrior")){
                    String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP, adventurer_attack, basic_potions, max_potions, adventurer_class, adventurer_password) VALUES(?, 0, 0, 80, 20, 10, 5, ?, ?);";
                    PreparedStatement stmt = connection.prepareStatement(insertQuery);
                    stmt.setString(1, username);
                    stmt.setString(2, adventurerClass);
                    stmt.setLong(3, hash(userPassword));
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        messageLabel.setText("User added successfully!");
                    } else messageLabel.setText("User couldn't be added.");
                }   else if (classChoiceBox.getValue().equals("Mage")){
                    String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP, adventurer_attack, basic_potions, max_potions, adventurer_class, adventurer_password) VALUES(?, 0, 0, 120, 15, 10, 5, ?, ?);";
                    PreparedStatement stmt = connection.prepareStatement(insertQuery);
                    stmt.setString(1, username);
                    stmt.setString(2, adventurerClass);
                    stmt.setLong(3, hash(userPassword));
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        messageLabel.setText("User added successfully!");
                    } else messageLabel.setText("User couldn't be added.");
                }   else {
                    String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP, adventurer_attack, basic_potions, max_potions, adventurer_class, adventurer_password) VALUES(?, 0, 0, 150, 10, 10, 5, ?, ?);";
                    PreparedStatement stmt = connection.prepareStatement(insertQuery);
                    stmt.setString(1, username);
                    stmt.setString(2, adventurerClass);
                    stmt.setLong(3, hash(userPassword));
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        messageLabel.setText("User added successfully!");
                    } else messageLabel.setText("User couldn't be added.");
                }

            } catch (SQLException ex) {
                throw new RuntimeException("Something's wrong");
            }
            currentAdventurer.setName(username);
            currentAdventurer.setAdventurer_class(adventurerClass);
        });

        grid.add(userLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(classLabel, 0, 1);
        grid.add(classChoiceBox, 1,1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1,2);

        //grid.add(loginButton, 1, 2);
        grid.add(registerButton, 1, 3);

        Scene scene = new Scene(grid, 300, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        return currentAdventurer;
    }}
package com.example.mziurifinalprojectadventurer;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;


public class LoginPage {
    String url, user, password;

    public LoginPage(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private void register(){
        Adventurer newAdventurer = new Adventurer();
        String username = nameField.getText();
        String userPassword = passwordField.getText();
        String adventurerClass = classChoiceBox.getValue();
        newAdventurer.setName(username);
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            switch(adventurerClass){
                case "Warrior":
                    newAdventurer.setMaxHp(80);
                    newAdventurer.setAttack(20);
                    newAdventurer.setAdventurer_class("Warrior");
                    break;
                case "Mage":
                    newAdventurer.setMaxHp(100);
                    newAdventurer.setAttack(15);
                    newAdventurer.setAdventurer_class("Mage");
                    break;
                case "Assassin":
                    newAdventurer.setMaxHp(150);
                    newAdventurer.setAttack(10);
                    newAdventurer.setAdventurer_class("Assassin");
                    break;
                default:
                    newAdventurer.setMaxHp(90);
                    newAdventurer.setAttack(5);
                    newAdventurer.setAdventurer_class("Basic");
                    break;
            }
            String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP, adventurer_attack, basic_potions, max_potions, adventurer_class, adventurer_password, max_health) VALUES(?, 1, 0, ?, ?, 10, 5, ?, ?, ?);";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setString(1, newAdventurer.getName());
            stmt.setInt(2, newAdventurer.getMaxHp());
            stmt.setInt(3, newAdventurer.getAttack());
            stmt.setString(4, newAdventurer.getAdventurer_class());
            stmt.setLong(5, hash(userPassword));
            stmt.setInt(6, newAdventurer.getMaxHp());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                messageLabel.setText("User added successfully!");
            } else messageLabel.setText("User couldn't be added.");
        } catch (SQLException ex) {
            throw new RuntimeException("Something's wrong");
        }   passwordField.clear();
    }

    private void login(){
        String username = nameField.getText();
        String userPassword = passwordField.getText();
        String adventurerClass = classChoiceBox.getValue();
        try{
            Connection connection = DriverManager.getConnection(url, user, password);
            boolean wasFound = false;
            String insertQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?;";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, adventurerClass);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                long password = rs.getLong("adventurer_password");
                if(password == hash(userPassword)){
                    messageLabel.setText("Logged in successfully!");
                    wasFound = true;
                }
            }   if(!wasFound)   messageLabel.setText("Wrong password or class.");
            passwordField.clear();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

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
    ChoiceBox<String> classChoiceBox =  new ChoiceBox<>(FXCollections.observableArrayList(classes));

    void authorisation(){

        VBox root = getVBox();
        root.getStyleClass().add("vbox");

        Scene scene = new Scene(root, 300, 500);
        Stage stage = new Stage();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Adventure_login.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    }

    @NotNull
    private VBox getVBox() {
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> register());

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> login());

        loginButton.setId("loginButton");
        registerButton.setId("registerButton");

        VBox root = new VBox(10, userLabel, nameField, classLabel, classChoiceBox, passwordLabel, passwordField, loginButton, registerButton, messageLabel);
        root.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        return root;
    }
}
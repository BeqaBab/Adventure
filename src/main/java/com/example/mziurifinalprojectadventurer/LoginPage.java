package com.example.mziurifinalprojectadventurer;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class LoginPage {
    String url, user, password;
    private Stage primaryStage;
    private VBox currentRoot;
    private Adventurer currentAdventurer = new Adventurer();

    public LoginPage(String url, String user, String password, Stage primaryStage) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.primaryStage = primaryStage;
    }

    private void register() {
        Adventurer newAdventurer = new Adventurer();
        String username = registrationNameField.getText();
        String userPassword = registrationPasswordField.getText();
        String adventurerClass;
        if(registrationClassChoiceBox.getValue() == null){
            registrationMessageLabel.setText("Please choose a class");
        }else{
            adventurerClass = registrationClassChoiceBox.getValue();
            newAdventurer.setName(username);
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                switch (adventurerClass) {
                    case "Warrior":
                        newAdventurer.setMaxHp(80);
                        newAdventurer.setAttack(20);
                        newAdventurer.setAdventurerClass("Warrior");
                        break;
                    case "Mage":
                        newAdventurer.setMaxHp(100);
                        newAdventurer.setAttack(15);
                        newAdventurer.setAdventurerClass("Mage");
                        break;
                    case "Assassin":
                        newAdventurer.setMaxHp(150);
                        newAdventurer.setAttack(10);
                        newAdventurer.setAdventurerClass("Assassin");
                        break;
                    default:
                        newAdventurer.setMaxHp(90);
                        newAdventurer.setAttack(5);
                        newAdventurer.setAdventurerClass("Basic");
                        break;

                }
                String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP, adventurer_attack, basic_potions, max_potions, adventurer_class, adventurer_password, max_health) VALUES(?, 1, 0, ?, ?, 10, 5, ?, ?, ?);";
                PreparedStatement stmt = connection.prepareStatement(insertQuery);
                stmt.setString(1, newAdventurer.getName());
                stmt.setInt(2, newAdventurer.getMaxHp());
                stmt.setInt(3, newAdventurer.getAttack());
                stmt.setString(4, newAdventurer.getAdventurerClass());
                stmt.setLong(5, hash(userPassword));
                stmt.setInt(6, newAdventurer.getMaxHp());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    registrationMessageLabel.setText("User added successfully!");
                } else registrationMessageLabel.setText("User couldn't be added.");
            } catch (SQLException ex) {
                registrationMessageLabel.setText("User couldn't be added, please choose a different name or try again later");
            }
            registrationPasswordField.clear();
        }
    }

    private void login() throws SQLException {
        String username = loginNameField.getText();
        String userPassword = loginPasswordField.getText();
        String adventurerClass = loginClassChoiceBox.getValue();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            boolean wasFound = false;
            String insertQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?;";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, adventurerClass);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long password = rs.getLong("adventurer_password");
                if (password == hash(userPassword)) {
                    loginMessageLabel.setText("Logged in successfully!");
                    wasFound = true;
                }
            }
            if (!wasFound) loginMessageLabel.setText("Wrong password or class.");
            else{
                insertQuery = "SELECT * FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?;";
                stmt = connection.prepareStatement(insertQuery);
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                rs = stmt.executeQuery();
                while(rs.next()){
                    currentAdventurer.setName(username);
                    currentAdventurer.setLevel(rs.getInt("adventurer_level"));
                    currentAdventurer.setExp(rs.getInt("adventurer_exp"));
                    currentAdventurer.setHp(rs.getInt("adventurer_hp"));
                    currentAdventurer.setAttack(rs.getInt("adventurer_attack"));
                    currentAdventurer.setBasicPotions(rs.getInt("basic_potions"));
                    currentAdventurer.setMaxPotions(rs.getInt("max_potions"));
                    currentAdventurer.setAdventurerClass(rs.getString("adventurer_class"));
                    currentAdventurer.setMaxHp(rs.getInt("max_health"));
                }
            }
            loginPasswordField.clear();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        Gameplay gameplay = new Gameplay(primaryStage, currentAdventurer, url, user, password);
        gameplay.Game();
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

    Label loginUserLabel = new Label("Name:");
    Label loginClassLabel = new Label("Class:");
    Label loginPasswordLabel = new Label("Password:");
    Label loginMessageLabel = new Label();

    Label registrationUserLabel = new Label("Name:");
    Label registrationClassLabel = new Label("Class:");
    Label registrationPasswordLabel = new Label("Password:");
    Label registrationMessageLabel = new Label();

    TextField loginNameField = new TextField();
    PasswordField loginPasswordField = new PasswordField();

    String[] classes = {"Warrior", "Mage", "Assassin"};
    ChoiceBox<String> loginClassChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(classes));
    ChoiceBox<String> registrationClassChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(classes));

    TextField registrationNameField = new TextField();
    PasswordField registrationPasswordField = new PasswordField();

    void authorisation() {
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> register());

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            try {
                login();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        Label registrationSuggestionLabel = new Label("Don't have an account?");
        Button switchToRegisterButton = new Button("Register here");

        Label loginSuggestionLabel = new Label("Already have an account?");
        Button switchToLoginButton = new Button("Login here");

        VBox loginRoot = new VBox(10);
        loginRoot.getStyleClass().add("vbox");
        loginRoot.getChildren().addAll(
                loginUserLabel, loginNameField,
                loginClassLabel, loginClassChoiceBox,
                loginPasswordLabel, loginPasswordField,
                loginMessageLabel, loginButton,
                registrationSuggestionLabel, switchToRegisterButton
        );
        loginRoot.setPadding(new Insets(20, 20, 20, 20));

        VBox registerRoot = new VBox(10);
        registerRoot.getStyleClass().add("vbox");
        registerRoot.getChildren().addAll(
                registrationUserLabel, registrationNameField,
                registrationClassLabel, registrationClassChoiceBox,
                registrationPasswordLabel, registrationPasswordField,
                registrationMessageLabel, registerButton,
                loginSuggestionLabel, switchToLoginButton
        );
        registerRoot.setPadding(new Insets(20, 20, 20, 20));

        switchToRegisterButton.setOnAction(e -> {
            primaryStage.getScene().setRoot(registerRoot);
            currentRoot = registerRoot;
        });

        switchToLoginButton.setOnAction(e -> {
            primaryStage.getScene().setRoot(loginRoot);
            currentRoot = loginRoot;
        });

        currentRoot = loginRoot;
        Scene mainScene = new Scene(currentRoot, 500, 400);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Adventure_login.css")).toExternalForm());
        primaryStage.setScene(mainScene);
    }
}
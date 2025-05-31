package com.example.mziurifinalprojectadventurer;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.Objects;

public class LoginPage {
    private final String url;
    private final String user;
    private final String password;
    private final Stage primaryStage;
    private VBox currentRoot;
    private Adventurer currentAdventurer = new Adventurer();

    public LoginPage(String url, String user, String password, Stage primaryStage) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.primaryStage = primaryStage;
    }

    private void register(@NotNull TextField nameField, @NotNull TextField passwordField, Label messageLabel, @NotNull ChoiceBox<String> classChoiceBox){
        String username = nameField.getText();
        String userPassword = passwordField.getText();
        String adventurerClass = classChoiceBox.getValue();

        if (adventurerClass == null) {
            messageLabel.setText("Please choose a class");
            return;
        }

        Adventurer newAdventurer = createNewAdventurer(username, adventurerClass);

        try (Connection connection = DriverManager.getConnection(url, user, password)){
            String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, " +
                    "adventurer_HP, adventurer_attack, adventurer_class, adventurer_password, max_health) " +
                    "VALUES(?, 1, 0, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setInt(2, newAdventurer.getMaxHp());
                stmt.setInt(3, newAdventurer.getAttack());
                stmt.setString(4, adventurerClass);
                stmt.setLong(5, hash(userPassword));
                stmt.setInt(6, newAdventurer.getMaxHp());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            newAdventurer.setId(id);

                            Weapon weapon = new Weapon();
                            weapon.setUrl(url);
                            weapon.setUser(user);
                            weapon.setPassword(password);

                            List<Weapon> startingWeapons = weapon.getWeaponsByLevel(1);
                            if (!startingWeapons.isEmpty()) {
                                String updateQuery = "UPDATE adventurer SET weapon_id = ? WHERE adventurer_id = ?";
                                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                    updateStmt.setInt(1, startingWeapons.getFirst().getId());
                                    updateStmt.setInt(2, id);
                                    updateStmt.executeUpdate();
                                }
                            }

                            messageLabel.setText("User added successfully!");
                        }
                    }
                } else {
                    messageLabel.setText("User couldn't be added.");
                }
            }
        } catch (SQLException ex) {
            messageLabel.setText("User couldn't be added. Please try a different name.");
        }
        passwordField.clear();
    }

    private Adventurer createNewAdventurer(String username, String adventurerClass){
        Adventurer adventurer = new Adventurer();
        adventurer.setName(username);
        adventurer.setAdventurerClass(adventurerClass);

        switch (adventurerClass) {
            case "Warrior":
                adventurer.setMaxHp(80);
                adventurer.setAttack(20);
                break;
            case "Mage":
                adventurer.setMaxHp(100);
                adventurer.setAttack(15);
                break;
            case "Assassin":
                adventurer.setMaxHp(150);
                adventurer.setAttack(10);
                break;
            default:
                adventurer.setMaxHp(90);
                adventurer.setAttack(5);
                break;
        }
        adventurer.setHp(adventurer.getMaxHp());
        return adventurer;
    }

    private void login(@NotNull TextField nameField, @NotNull TextField passwordField, @NotNull ChoiceBox<String> classChoiceBox, Label messageLabel) throws SQLException {
        String username = nameField.getText();
        String userPassword = passwordField.getText();
        String adventurerClass = classChoiceBox.getValue();
        Connection connection = DriverManager.getConnection(url, user, password);
        try {
            String checkQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        messageLabel.setText("Account doesn't exist");
                        passwordField.clear();
                        return;
                    }

                    long storedPassword = rs.getLong("adventurer_password");
                    if (userPassword.isBlank() || storedPassword == 0) {
                        messageLabel.setText("Please enter password");
                        passwordField.clear();
                        return;
                    }
                    if (storedPassword != hash(userPassword)) {
                        messageLabel.setText("Wrong password");
                        passwordField.clear();
                        return;
                    }
                }
            }

            String selectQuery = "SELECT * FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
            PreparedStatement stmt = connection.prepareStatement(selectQuery);
            try {
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        loadAdventurerData(rs);
                        Weapon weapon = new Weapon();
                        weapon.setUrl(url);
                        weapon.setUser(user);
                        weapon.setPassword(password);
                        if (currentAdventurer.getWeaponId() > 0) {
                            currentAdventurer.setCurrentWeapon(weapon.getWeaponById(currentAdventurer.getWeaponId()));
                        } else {
                            List<Weapon> startingWeapons = weapon.getWeaponsByLevel(1);
                            if (!startingWeapons.isEmpty()) {
                                currentAdventurer.setCurrentWeapon(startingWeapons.getFirst());
                                currentAdventurer.setWeaponId(startingWeapons.getFirst().getId());
                            }
                        }
                        Gameplay gameplay = new Gameplay(url, user, password, primaryStage, currentAdventurer);
                        gameplay.Game();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAdventurerData(ResultSet rs) throws SQLException {
        currentAdventurer.setId(rs.getInt("adventurer_id"));
        currentAdventurer.setName(rs.getString("adventurer_name"));
        currentAdventurer.setLevel(rs.getInt("adventurer_level"));
        currentAdventurer.setExp(rs.getInt("adventurer_exp"));
        currentAdventurer.setHp(rs.getInt("adventurer_hp"));
        currentAdventurer.setAttack(rs.getInt("adventurer_attack"));
        currentAdventurer.setBasicPotions(rs.getInt("basic_potions"));
        currentAdventurer.setMaxPotions(rs.getInt("max_potions"));
        currentAdventurer.setAdventurerClass(rs.getString("adventurer_class"));
        currentAdventurer.setMaxHp(rs.getInt("max_health"));
        currentAdventurer.setWeaponId(rs.getInt("weapon_id"));
    }

    public static long hash(@NotNull String s){
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

    void authorisation(){
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

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> handleRegisterAction(
                registrationNameField,
                registrationPasswordField,
                registrationMessageLabel,
                registrationClassChoiceBox
        ));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLoginAction(
                loginNameField,
                loginPasswordField,
                loginClassChoiceBox,
                loginMessageLabel
        ));

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

        switchToRegisterButton.setOnAction(e -> handleSwitchToRegister(
                registerRoot,
                loginMessageLabel,
                registrationClassChoiceBox,
                registrationPasswordField,
                registrationNameField
        ));

        switchToLoginButton.setOnAction(e -> handleSwitchToLogin(
                loginRoot,
                loginClassChoiceBox,
                loginNameField,
                loginPasswordField
        ));

        currentRoot = loginRoot;
        Scene mainScene = new Scene(currentRoot, 700, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Adventure_login.css")).toExternalForm());
        primaryStage.setScene(mainScene);
    }

    private void handleRegisterAction(TextField nameField, TextField passwordField, Label messageLabel, ChoiceBox<String> classChoiceBox){
        register(nameField, passwordField, messageLabel, classChoiceBox);
    }

    private void handleLoginAction(TextField nameField, TextField passwordField, ChoiceBox<String> classChoiceBox, Label messageLabel){
        try {
            login(nameField, passwordField, classChoiceBox, messageLabel);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleSwitchToRegister(VBox registerRoot, Label loginMessageLabel, ChoiceBox<String> classChoiceBox, PasswordField passwordField, TextField nameField){
        primaryStage.getScene().setRoot(registerRoot);
        loginMessageLabel.setText("");
        currentRoot = registerRoot;
        classChoiceBox.setValue(null);
        passwordField.clear();
        nameField.clear();
    }

    private void handleSwitchToLogin(VBox loginRoot, ChoiceBox<String> classChoiceBox, TextField nameField, PasswordField passwordField){
        primaryStage.getScene().setRoot(loginRoot);
        currentRoot = loginRoot;
        classChoiceBox.setValue(null);
        nameField.clear();
        passwordField.clear();
    }
}
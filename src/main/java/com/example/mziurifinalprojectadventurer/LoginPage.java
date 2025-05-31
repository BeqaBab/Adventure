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

    private TextField loginNameField;
    private PasswordField loginPasswordField;
    private ChoiceBox<String> loginClassChoiceBox;
    private Label loginMessageLabel;

    private TextField registrationNameField;
    private PasswordField registrationPasswordField;
    private ChoiceBox<String> registrationClassChoiceBox;
    private Label registrationMessageLabel;

    public LoginPage(String url, String user, String password, Stage primaryStage) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.primaryStage = primaryStage;
    }

    private void register(){
        String username = registrationNameField.getText();
        String userPassword = registrationPasswordField.getText();
        String adventurerClass = registrationClassChoiceBox.getValue();

        if (adventurerClass == null) {
            registrationMessageLabel.setText("Please choose a class");
            return;
        }

        Adventurer newAdventurer = createNewAdventurer(username, adventurerClass);

        try (Connection connection = DriverManager.getConnection(url, user, password)){
            String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP adventurer_class, adventurer_password, max_health) VALUES(?, 1, 0, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setInt(2, newAdventurer.getMaxHp());
                stmt.setString(3, adventurerClass);
                stmt.setLong(4, hash(userPassword));
                stmt.setInt(5, newAdventurer.getMaxHp());

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
                            registrationMessageLabel.setText("User added successfully!");
                        }
                    }
                } else {
                    registrationMessageLabel.setText("User couldn't be added.");
                }
            }
        } catch (SQLException ex) {
            registrationMessageLabel.setText("User couldn't be added. Please try a different name.");
        }
        registrationPasswordField.clear();
    }

    private Adventurer createNewAdventurer(String username, String adventurerClass){
        Adventurer adventurer = new Adventurer();
        adventurer.setName(username);
        adventurer.setAdventurerClass(adventurerClass);

        switch (adventurerClass) {
            case "Warrior":
                adventurer.setMaxHp(80);
                break;
            case "Mage":
                adventurer.setMaxHp(100);
                break;
            case "Assassin":
                adventurer.setMaxHp(150);
                break;
            default:
                adventurer.setMaxHp(90);
                break;
        }
        adventurer.setHp(adventurer.getMaxHp());
        return adventurer;
    }

    private void login() throws SQLException {
        String username = loginNameField.getText();
        String userPassword = loginPasswordField.getText();
        String adventurerClass = loginClassChoiceBox.getValue();
        Connection connection = DriverManager.getConnection(url, user, password);
        try {
            String checkQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        loginMessageLabel.setText("Account doesn't exist");
                        loginPasswordField.clear();
                        return;
                    }

                    long storedPassword = rs.getLong("adventurer_password");
                    if (userPassword.isBlank() || storedPassword == 0) {
                        loginMessageLabel.setText("Please enter password");
                        loginPasswordField.clear();
                        return;
                    }
                    if (storedPassword != hash(userPassword)) {
                        loginMessageLabel.setText("Wrong password");
                        loginPasswordField.clear();
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
                        currentAdventurer.setCurrentWeapon(weapon.getWeaponById(currentAdventurer.getWeaponId()));
                        currentAdventurer.getCurrentWeapon().setUrl(url);
                        currentAdventurer.getCurrentWeapon().setUser(user);
                        currentAdventurer.getCurrentWeapon().setPassword(password);
                        Gameplay gameplay = new Gameplay(url, user, password, primaryStage, currentAdventurer);
                        gameplay.Game();
                        currentAdventurer.checkAndUpgradeWeapon();
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

    private void initializeLoginComponents() {
        Label loginUserLabel = new Label("Name:");
        Label loginClassLabel = new Label("Class:");
        Label loginPasswordLabel = new Label("Password:");
        loginMessageLabel = new Label();

        loginNameField = new TextField();
        loginPasswordField = new PasswordField();

        String[] classes = {"Warrior", "Mage", "Assassin"};
        loginClassChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(classes));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLoginAction());

        Label registrationSuggestionLabel = new Label("Don't have an account?");
        Button switchToRegisterButton = new Button("Register here");
        switchToRegisterButton.setOnAction(e -> handleSwitchToRegister());

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

        currentRoot = loginRoot;
    }

    private void initializeRegistrationComponents() {
        Label registrationUserLabel = new Label("Name:");
        Label registrationClassLabel = new Label("Class:");
        Label registrationPasswordLabel = new Label("Password:");
        registrationMessageLabel = new Label();

        registrationNameField = new TextField();
        registrationPasswordField = new PasswordField();

        String[] classes = {"Warrior", "Mage", "Assassin"};
        registrationClassChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(classes));

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> handleRegisterAction());

        Label loginSuggestionLabel = new Label("Already have an account?");
        Button switchToLoginButton = new Button("Login here");
        switchToLoginButton.setOnAction(e -> handleSwitchToLogin());

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
    }

    void authorisation(){
        initializeLoginComponents();
        initializeRegistrationComponents();

        Scene mainScene = new Scene(currentRoot, 700, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Adventure_login.css")).toExternalForm());
        primaryStage.setScene(mainScene);
    }

    private void handleRegisterAction(){
        register();
    }

    private void handleLoginAction(){
        try {
            login();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleSwitchToRegister(){
        VBox registerRoot = createRegisterRoot();
        primaryStage.getScene().setRoot(registerRoot);
        loginMessageLabel.setText("");
        currentRoot = registerRoot;
        registrationClassChoiceBox.setValue(null);
        registrationPasswordField.clear();
        registrationNameField.clear();
    }

    private void handleSwitchToLogin(){
        VBox loginRoot = createLoginRoot();
        primaryStage.getScene().setRoot(loginRoot);
        currentRoot = loginRoot;
        loginClassChoiceBox.setValue(null);
        loginNameField.clear();
        loginPasswordField.clear();
    }

    private VBox createLoginRoot() {
        Label loginUserLabel = new Label("Name:");
        Label loginClassLabel = new Label("Class:");
        Label loginPasswordLabel = new Label("Password:");

        Label registrationSuggestionLabel = new Label("Don't have an account?");
        Button switchToRegisterButton = new Button("Register here");
        switchToRegisterButton.setOnAction(e -> handleSwitchToRegister());

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLoginAction());

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

        return loginRoot;
    }

    private VBox createRegisterRoot() {
        Label registrationUserLabel = new Label("Name:");
        Label registrationClassLabel = new Label("Class:");
        Label registrationPasswordLabel = new Label("Password:");

        Label loginSuggestionLabel = new Label("Already have an account?");
        Button switchToLoginButton = new Button("Login here");
        switchToLoginButton.setOnAction(e -> handleSwitchToLogin());

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> handleRegisterAction());

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

        return registerRoot;
    }
}
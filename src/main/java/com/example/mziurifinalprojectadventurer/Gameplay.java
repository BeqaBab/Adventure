package com.example.mziurifinalprojectadventurer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;
import java.util.Random;

public class Gameplay {
    private Stage primaryStage;
    private Adventurer currentAdventurer;
    private Random random = new Random();
    private Label damageLabel = new Label(), adventurerLabel = new Label(), currentMonsterShortLabel = new Label(), shameLabel = new Label(), giveUpLabel = new Label(), loseLabel = new Label(), winLabel = new Label(), infoLabel = new Label(),
            potionInfoLabel = new Label(), basicPotionLabel = new Label(), maxPotionLabel = new Label();
    private Button attackButton = new Button("⚔ Attack");
    private String url, user, password;

    public Gameplay(String url, String user, String password, Stage primaryStage, Adventurer currentAdventurer) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.primaryStage = primaryStage;
        this.currentAdventurer = currentAdventurer;
    }

    private void saveProgress(@NotNull Enemy currentEnemy) {
        try {
            int newExp = currentAdventurer.getExp() + currentEnemy.getDropExp();
            int levelsGained = 0;

            while (newExp >= 1000) {
                levelsGained++;
                newExp -= 1000;
            }

            if (levelsGained > 0) {
                currentAdventurer.setLevel(currentAdventurer.getLevel() + levelsGained);
                int hpIncrease = levelsGained * 10;
                int attackIncrease = levelsGained * 10;
                currentAdventurer.setHp(currentAdventurer.getHp() + hpIncrease);
                currentAdventurer.setMaxHp(currentAdventurer.getMaxHp() + hpIncrease);
                currentAdventurer.setAttack(currentAdventurer.getAttack() + attackIncrease);
            }

            currentAdventurer.setExp(newExp);

            currentAdventurer.setBasicPotions(currentAdventurer.getBasicPotions() + currentEnemy.getDropBasic());
            currentAdventurer.setMaxPotions(currentAdventurer.getMaxPotions() + currentEnemy.getDropMax());

            currentAdventurer.checkAndUpgradeWeapon();
            String updateQuery = "UPDATE adventurer SET adventurer_level = ?, adventurer_exp = ?, adventurer_HP = ?, adventurer_attack = ?, basic_potions = ?, max_potions = ?, max_health = ?, weapon_id = ? WHERE adventurer_id = ?";

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

                stmt.setInt(1, currentAdventurer.getLevel());
                stmt.setInt(2, currentAdventurer.getExp());
                stmt.setInt(3, currentAdventurer.getHp());
                stmt.setInt(4, currentAdventurer.getAttack());
                stmt.setInt(5, currentAdventurer.getBasicPotions());
                stmt.setInt(6, currentAdventurer.getMaxPotions());
                stmt.setInt(7, currentAdventurer.getMaxHp());
                stmt.setInt(8, currentAdventurer.getWeaponId());
                stmt.setInt(9, currentAdventurer.getId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Enemy chooseEnemy() throws SQLException {
        int randomId = random.nextInt(1, 10);
        Enemy currentEnemy = new Enemy();

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            String insertQuery = "SELECT * FROM monsters WHERE monster_id = ?;";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, randomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentEnemy = new Enemy();
                currentEnemy.setName(rs.getString("monster_name"));
                currentEnemy.setDamage(rs.getInt("monster_damage"));
                currentEnemy.setHp(rs.getInt("monster_hp"));
                currentEnemy.setDropBasic(rs.getInt("monster_drop_basic"));
                currentEnemy.setDropMax(rs.getInt("monster_drop_max"));
                currentEnemy.setDropExp(rs.getInt("monster_drop_exp"));
            } else {
                throw new SQLException("No monster found with ID: " + randomId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currentEnemy;
    }

    public void deleteProgress(){
        try{
            String insertQuery = "DELETE FROM adventurer WHERE adventurer_id = ?;";
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, currentAdventurer.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void returnToStartingPage() {
        try {
            Game gameApp = new Game();
            gameApp.start(primaryStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Game() throws SQLException {
        final Enemy[] currentEnemy = {chooseEnemy()};
        damageLabel.setText("Combat will begin when you attack!");
        damageLabel.setId("damageLabel");
        adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
        adventurerLabel.setId("adventurerLabel");
        currentMonsterShortLabel.setText(currentEnemy[0].getName() + " | HP: " + currentEnemy[0].getHp());
        currentMonsterShortLabel.setId("currentMonsterShortLabel");

        attackButton.setId("attackButton");

        Button showEnemyInfoButton = new Button("🔍 Enemy Info");
        showEnemyInfoButton.setId("showEnemyInfoButton");

        Button useAnItemButton = new Button("Use Item");
        useAnItemButton.setId("useAnItemButton");

        Button runButton = new Button("🏃 Flee");
        runButton.setId("runButton");

        VBox statusPanel = new VBox(10);
        statusPanel.getStyleClass().add("status-panel");
        statusPanel.getChildren().addAll(damageLabel, adventurerLabel, currentMonsterShortLabel);

        HBox primaryActions = new HBox(10);
        primaryActions.setAlignment(Pos.CENTER);
        primaryActions.getChildren().addAll(attackButton, useAnItemButton);

        HBox secondaryActions = new HBox(10);
        secondaryActions.setAlignment(Pos.CENTER);
        secondaryActions.getChildren().addAll(showEnemyInfoButton, runButton);

        VBox actionPanel = new VBox(8);
        actionPanel.getStyleClass().add("action-panel");
        actionPanel.getChildren().addAll(primaryActions, secondaryActions);

        VBox mainRoot = new VBox(20);
        mainRoot.getStyleClass().add("combat-container");
        mainRoot.getChildren().addAll(statusPanel, actionPanel);

        StackPane centerContainer = new StackPane();
        centerContainer.getChildren().add(mainRoot);
        centerContainer.setAlignment(Pos.CENTER);

        runButton.setOnAction(e -> handleRunButtonAction());
        attackButton.setOnAction(e -> handleAttackButtonAction(currentEnemy, centerContainer));
        showEnemyInfoButton.setOnAction(e -> handleShowEnemyInfoButtonAction(currentEnemy[0], centerContainer));
        useAnItemButton.setOnAction(e -> handleUseItemButtonAction(centerContainer));

        Scene mainScene = new Scene(centerContainer, 700, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Gameplay.css")).toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Adventure - Combat");
    }

    private void handleRunButtonAction() {
        shameLabel.setText("You have fled from battle...");
        shameLabel.setId("shameLabel");
        giveUpLabel.setText("There's no place for shame in this world");
        giveUpLabel.setId("giveUpLabel");
        Button returnToMenuButton = new Button("🏠 Return to Menu");
        returnToMenuButton.setId("returnToMenuButton");
        returnToMenuButton.setOnAction(actionEvent -> returnToStartingPage());

        Button seeRunInfoButton = getButton("flee");

        VBox shameLayout = new VBox(15);
        shameLayout.getStyleClass().add("combat-container");
        shameLayout.getChildren().addAll(shameLabel, giveUpLabel, seeRunInfoButton, returnToMenuButton);

        StackPane shameContainer = new StackPane();
        shameContainer.getChildren().add(shameLayout);
        shameContainer.setAlignment(Pos.CENTER);

        deleteProgress();

        primaryStage.getScene().setRoot(shameContainer);
    }

    private void handleAttackButtonAction(@NotNull Enemy[] currentEnemy, StackPane centerContainer) {
        Weapon weapon = currentAdventurer.getCurrentWeapon();
        int damage = weapon.calculateDamage();
        currentEnemy[0].setHp(currentEnemy[0].getHp() - damage);
        currentAdventurer.setHp(currentAdventurer.getHp() - currentEnemy[0].getDamage());

        if(currentAdventurer.getHp() <= 0){
            handleDefeat();
        } else if(currentEnemy[0].getHp() <= 0){
            handleVictory(currentEnemy, centerContainer);
        }

        int dealtDamage = weapon.calculateDamage();

        currentMonsterShortLabel.setText(currentEnemy[0].getName() + " | HP: " + currentEnemy[0].getHp());
        damageLabel.setText("⚔ " + weapon.getName() + " dealt: " + dealtDamage + (dealtDamage > weapon.getDamage() ? " CRITICAL!" : "") + "\n💥 You took: " + currentEnemy[0].getDamage() + " damage");
        adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + weapon.getName());
    }

    private void handleDefeat() {
        loseLabel.setText("Your journey ends here..." + "\nNo one gets a second chance");
        loseLabel.setId("loseLabel");

        Button seeRunInfoButton = getButton("lose");

        Button returnToMenuButton = new Button("🏠 Return to Menu");
        returnToMenuButton.setId("returnToMenuButton");
        returnToMenuButton.setOnAction(actionEvent -> returnToStartingPage());

        VBox loseLayout = new VBox(15);
        loseLayout.getStyleClass().add("combat-container");
        loseLayout.getChildren().addAll(loseLabel, seeRunInfoButton, returnToMenuButton);

        StackPane loseContainer = new StackPane();
        loseContainer.getChildren().add(loseLayout);
        loseContainer.setAlignment(Pos.CENTER);
        deleteProgress();

        primaryStage.getScene().setRoot(loseContainer);
    }

    private void handleVictory(@NotNull Enemy[] currentEnemy, StackPane centerContainer) {
        winLabel.setText("Victory! You defeated " + currentEnemy[0].getName() + "\n Your progress has been saved!");
        winLabel.setId("winLabel");
        saveProgress(currentEnemy[0]);

        try {
            currentEnemy[0] = chooseEnemy();
            adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            currentMonsterShortLabel.setText(currentEnemy[0].getName() + " | HP: " + currentEnemy[0].getHp());
            damageLabel.setText("Combat will begin when you attack!");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        Button continueButton = new Button("Continue Adventure →");
        continueButton.setId("continueButton");
        continueButton.setOnAction(actionEvent -> {
            primaryStage.getScene().setRoot(centerContainer);
        });

        VBox winLayout = new VBox(15);
        winLayout.getStyleClass().add("combat-container");
        winLayout.getChildren().addAll(winLabel, continueButton);

        StackPane winContainer = new StackPane();
        winContainer.getChildren().add(winLayout);
        winContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(winContainer);
    }

    private void handleShowEnemyInfoButtonAction(@NotNull Enemy enemy, StackPane centerContainer) {
        infoLabel.setText(enemy.toString());
        infoLabel.setId("infoLabel");

        Button backButton = new Button("← Back to Combat");
        backButton.setId("backButton");
        backButton.setOnAction(action -> primaryStage.getScene().setRoot(centerContainer));

        VBox infoLayout = new VBox(15);
        infoLayout.getStyleClass().add("combat-container");
        infoLayout.getChildren().addAll(infoLabel, backButton);

        StackPane infoContainer = new StackPane();
        infoContainer.getChildren().add(infoLayout);
        infoContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(infoContainer);
    }

    private void handleUseItemButtonAction(StackPane centerContainer) {
        potionInfoLabel.setText("Remaining potions:");
        basicPotionLabel.setText("Basic Potions: " + currentAdventurer.getBasicPotions());
        maxPotionLabel.setText("Max Potions: " + currentAdventurer.getMaxPotions());
        Button useBasicPotionButton = new Button("Use Basic Potion");
        Button useMaxPotionButton = new Button("Use Max Potion");
        Button backButton = new Button("← Back to Combat");
        backButton.setId("backButton");

        VBox itemLayout = new VBox(15);
        itemLayout.setAlignment(Pos.CENTER);
        itemLayout.setAlignment(Pos.CENTER);
        itemLayout.getChildren().addAll(potionInfoLabel, basicPotionLabel, maxPotionLabel, useBasicPotionButton, useMaxPotionButton, backButton);
        StackPane potionsContainer = new StackPane();
        potionsContainer.getChildren().addAll(itemLayout);
        potionsContainer.setAlignment(Pos.CENTER);
        primaryStage.getScene().setRoot(potionsContainer);

        useBasicPotionButton.setOnAction(action -> {
            if(currentAdventurer.getBasicPotions() > 0 && currentAdventurer.getHp() < currentAdventurer.getMaxHp()){
                currentAdventurer.setBasicPotions(currentAdventurer.getBasicPotions() - 1 );
                basicPotionLabel.setText("Basic Potions: " + currentAdventurer.getBasicPotions());
                int newAdventurerHealth = currentAdventurer.getHp() + 20;
                if(newAdventurerHealth > currentAdventurer.getMaxHp())  newAdventurerHealth = currentAdventurer.getMaxHp();
                currentAdventurer.setHp(newAdventurerHealth);
                adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            } else infoLabel.setText("You don't have any basic potions left.");
        });

        useMaxPotionButton.setOnAction(action -> {
            if(currentAdventurer.getMaxPotions() > 0 && currentAdventurer.getHp() < currentAdventurer.getMaxHp()){
                currentAdventurer.setMaxPotions(currentAdventurer.getMaxPotions() - 1);
                maxPotionLabel.setText("Max Potions: " + currentAdventurer.getMaxPotions());
                int newAdventurerHealth = currentAdventurer.getHp() + 40;
                if(newAdventurerHealth > currentAdventurer.getMaxHp())  newAdventurerHealth = currentAdventurer.getMaxHp();
                currentAdventurer.setHp(newAdventurerHealth);
                adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            } else infoLabel.setText("You don't have any max potions left.");
        });

        backButton.setOnAction(action -> primaryStage.getScene().setRoot(centerContainer));
        infoLabel.setId("infoLabel");
    }

    @NotNull
    private Button getButton(String context) {
        Button seeRunInfoButton = new Button("See your run info");
        seeRunInfoButton.setOnAction(actionEvent -> {
            Label runInfoLabel = new Label(currentAdventurer.toString());
            runInfoLabel.setId("infoLabel");
            Button backButton = new Button("← Back");
            backButton.setId("backButton");
            backButton.setOnAction(e -> {
                if (context.equals("flee")) {
                    Label shameLabel = new Label("You have fled from battle...");
                    shameLabel.setId("shameLabel");
                    Label giveUpLabel = new Label("There's no place for shame in this world");
                    giveUpLabel.setId("giveUpLabel");
                    Button returnToMenuButton = new Button("🏠 Return to Menu");
                    returnToMenuButton.setId("returnToMenuButton");
                    returnToMenuButton.setOnAction(action -> returnToStartingPage());

                    Button seeRunInfoButtonAgain = getButton("flee");

                    VBox shameLayout = new VBox(15);
                    shameLayout.getStyleClass().add("combat-container");
                    shameLayout.getChildren().addAll(shameLabel, giveUpLabel, seeRunInfoButtonAgain, returnToMenuButton);

                    StackPane shameContainer = new StackPane();
                    shameContainer.getChildren().add(shameLayout);
                    shameContainer.setAlignment(Pos.CENTER);

                    primaryStage.getScene().setRoot(shameContainer);
                } else {
                    Label loseLabel = new Label("Your journey ends here..." + "\nNo one gets a second chance");
                    loseLabel.setId("loseLabel");
                    Button seeRunInfoButtonAgain = getButton("lose");

                    Button returnToMenuButton = new Button("🏠 Return to Menu");
                    returnToMenuButton.setId("returnToMenuButton");
                    returnToMenuButton.setOnAction(action -> returnToStartingPage());

                    VBox loseLayout = new VBox(15);
                    loseLayout.getStyleClass().add("combat-container");
                    loseLayout.getChildren().addAll(loseLabel, seeRunInfoButtonAgain, returnToMenuButton);

                    StackPane loseContainer = new StackPane();
                    loseContainer.getChildren().add(loseLayout);
                    loseContainer.setAlignment(Pos.CENTER);

                    primaryStage.getScene().setRoot(loseContainer);
                }
            });

            VBox runInfoLayout = new VBox(15);
            runInfoLayout.getStyleClass().add("combat-container");
            runInfoLayout.getChildren().addAll(runInfoLabel, backButton);

            StackPane runInfoContainer = new StackPane();
            runInfoContainer.getChildren().add(runInfoLayout);
            runInfoContainer.setAlignment(Pos.CENTER);

            primaryStage.getScene().setRoot(runInfoContainer);
        });
        return seeRunInfoButton;
    }
}
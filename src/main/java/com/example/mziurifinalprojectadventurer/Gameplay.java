package com.example.mziurifinalprojectadventurer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Gameplay {
    private Stage primaryStage;
    private Adventurer currentAdventurer;
    private Label damageLabel = new Label();
    private Label adventurerLabel = new Label();
    private Label currentMonsterShortLabel = new Label();
    private Label shameLabel = new Label();
    private Label giveUpLabel = new Label();
    private Label loseLabel = new Label();
    private Label winLabel = new Label();
    private Label infoLabel = new Label();
    private Label potionInfoLabel = new Label();
    private Label basicPotionLabel = new Label();
    private Label maxPotionLabel = new Label();
    private final Button playerInfoButton = new Button("🔍 Player Info");
    private final Button attackButton = new Button("⚔ Attack");
    private final Button showEnemyInfoButton = new Button("🔍 Enemy Info");
    private final Button useAnItemButton = new Button("Use Item");
    private final Button runButton = new Button("🏃 Flee");
    private final BaseConnection baseConnection;

    public Gameplay(BaseConnection baseConnection, Stage primaryStage, Adventurer currentAdventurer){
        this.baseConnection = baseConnection;
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
                currentAdventurer.setLevel(regulatedAdd(currentAdventurer.getLevel(), levelsGained));
                int hpIncrease = levelsGained * 10;
                currentAdventurer.setHp(regulatedAdd(currentAdventurer.getHp(), hpIncrease));
                currentAdventurer.setMaxHp(regulatedAdd(currentAdventurer.getMaxHp(), hpIncrease));
            }

            currentAdventurer.setExp(newExp);

            currentAdventurer.setBasicPotions(regulatedAdd(currentAdventurer.getBasicPotions(), currentEnemy.getDropBasic()));
            currentAdventurer.setMaxPotions(regulatedAdd(currentAdventurer.getMaxPotions(), currentEnemy.getDropMax()));

            currentAdventurer.checkAndUpgradeWeapon(baseConnection);
            adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            baseConnection.saveProgressToDataBase(currentAdventurer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpLabels(Enemy enemy){
        damageLabel.setText("Combat will begin when you attack!");
        damageLabel.setId("damageLabel");
        adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
        adventurerLabel.setId("adventurerLabel");
        currentMonsterShortLabel.setText(enemy.getName() + " | HP: " + enemy.getHp());
        currentMonsterShortLabel.setId("currentMonsterShortLabel");
    }

    private void setUpButtons(){
        attackButton.setId("attackButton");
        showEnemyInfoButton.setId("showEnemyInfoButton");
        useAnItemButton.setId("useAnItemButton");
        runButton.setId("runButton");
    }

    private void returnToStartingPage() {
        try {
            Game gameApp = new Game();
            gameApp.start(primaryStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Game(){
        final Enemy[] currentEnemy = {baseConnection.chooseEnemy()};
        setUpLabels(currentEnemy[0]);
        setUpButtons();

        Scene mainScene = createMainScene(currentEnemy);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Adventure - Combat");
    }

    private Scene createMainScene(Enemy[] currentEnemy) {
        StackPane centerContainer = createMainContainer();
        setupButtonActions(currentEnemy, centerContainer);

        Scene mainScene = new Scene(centerContainer, 700, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Gameplay.css")).toExternalForm());
        return mainScene;
    }

    private StackPane createMainContainer() {
        VBox statusPanel = createStatusPanel();
        VBox actionPanel = createActionPanel();

        VBox mainRoot = new VBox(20);
        mainRoot.getStyleClass().add("combat-container");
        mainRoot.getChildren().addAll(statusPanel, actionPanel);

        StackPane centerContainer = new StackPane();
        centerContainer.getChildren().add(mainRoot);
        centerContainer.setAlignment(Pos.CENTER);
        return centerContainer;
    }

    private VBox createStatusPanel() {
        VBox statusPanel = new VBox(10);
        statusPanel.getStyleClass().add("status-panel");
        statusPanel.getChildren().addAll(damageLabel, adventurerLabel, currentMonsterShortLabel);
        return statusPanel;
    }

    private VBox createActionPanel() {
        HBox primaryActions = createPrimaryActions();
        HBox secondaryActions = createSecondaryActions();

        VBox actionPanel = new VBox(8);
        actionPanel.getStyleClass().add("action-panel");
        actionPanel.getChildren().addAll(primaryActions, secondaryActions);
        return actionPanel;
    }

    private HBox createPrimaryActions() {
        HBox primaryActions = new HBox(10);
        primaryActions.setAlignment(Pos.CENTER);
        primaryActions.getChildren().addAll(attackButton, useAnItemButton);
        return primaryActions;
    }

    private HBox createSecondaryActions() {
        HBox secondaryActions = new HBox(10);
        secondaryActions.setAlignment(Pos.CENTER);
        secondaryActions.getChildren().addAll(showEnemyInfoButton, playerInfoButton, runButton);
        return secondaryActions;
    }

    private void setupButtonActions(Enemy[] currentEnemy, StackPane centerContainer) {
        playerInfoButton.setOnAction(_ -> handleShowPlayerInfoButton(centerContainer));
        runButton.setOnAction(_ -> handleRunButtonAction());
        attackButton.setOnAction(_ -> handleAttackButtonAction(currentEnemy, centerContainer));
        showEnemyInfoButton.setOnAction(_ -> handleShowEnemyInfoButtonAction(currentEnemy[0], centerContainer));
        useAnItemButton.setOnAction(_ -> handleUseItemButtonAction(centerContainer));
    }

    private void handleRunButtonAction() {
        shameLabel.setText("You have fled from battle...");
        shameLabel.setId("shameLabel");
        giveUpLabel.setText("There's no place for shame in this world");
        giveUpLabel.setId("giveUpLabel");
        Button returnToMenuButton = new Button("🏠 Return to Menu");
        returnToMenuButton.setId("returnToMenuButton");
        returnToMenuButton.setOnAction(_ -> returnToStartingPage());

        Button seeRunInfoButton = getButton("flee");

        VBox shameLayout = new VBox(15);
        shameLayout.getStyleClass().add("combat-container");
        shameLayout.getChildren().addAll(shameLabel, giveUpLabel, seeRunInfoButton, returnToMenuButton);

        StackPane shameContainer = new StackPane();
        shameContainer.getChildren().add(shameLayout);
        shameContainer.setAlignment(Pos.CENTER);

        baseConnection.deleteProgress(currentAdventurer);

        primaryStage.getScene().setRoot(shameContainer);
    }

    private void handleAttackButtonAction(@NotNull Enemy[] currentEnemy, StackPane centerContainer) {
        Weapon weapon = currentAdventurer.getCurrentWeapon();
        int damage = currentAdventurer.calculateDamage();
        currentEnemy[0].setHp(currentEnemy[0].getHp() - damage);
        currentAdventurer.setHp(currentAdventurer.getHp() - currentEnemy[0].getDamage());

        currentMonsterShortLabel.setText(currentEnemy[0].getName() + " | HP: " + currentEnemy[0].getHp());
        damageLabel.setText("⚔ " + weapon.getName() + " dealt: " + damage + (damage > weapon.getDamage() ? " CRITICAL!" : "") + "\n💥 You took: " + currentEnemy[0].getDamage() + " damage");
        adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + weapon.getName());

        if(currentAdventurer.getHp() <= 0){
            handleDefeat();
        } else if(currentEnemy[0].getHp() <= 0){
            handleVictory(currentEnemy, centerContainer);
            damageLabel.setText("Combat will begin when you attack!");
        }
    }

    private void handleDefeat() {
        loseLabel.setText("Your journey ends here..." + "\nNo one gets a second chance");
        loseLabel.setId("loseLabel");

        Button seeRunInfoButton = getButton("lose");

        Button returnToMenuButton = new Button("🏠 Return to Menu");
        returnToMenuButton.setId("returnToMenuButton");
        returnToMenuButton.setOnAction(_ -> returnToStartingPage());

        VBox loseLayout = new VBox(15);
        loseLayout.getStyleClass().add("combat-container");
        loseLayout.getChildren().addAll(loseLabel, seeRunInfoButton, returnToMenuButton);

        StackPane loseContainer = new StackPane();
        loseContainer.getChildren().add(loseLayout);
        loseContainer.setAlignment(Pos.CENTER);
        baseConnection.deleteProgress(currentAdventurer);

        primaryStage.getScene().setRoot(loseContainer);
    }

    private void handleVictory(@NotNull Enemy[] currentEnemy, StackPane centerContainer) {
        winLabel.setText("Victory! You defeated " + currentEnemy[0].getName() + "\n Your progress has been saved!");
        winLabel.setId("winLabel");
        currentAdventurer.defeatMonster();
        saveProgress(currentEnemy[0]);

        currentEnemy[0] = baseConnection.chooseEnemy();
        adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
        currentMonsterShortLabel.setText(currentEnemy[0].getName() + " | HP: " + currentEnemy[0].getHp());
        damageLabel.setText("Combat will begin when you attack!");

        Button continueButton = new Button("Continue Adventure →");
        continueButton.setId("continueButton");
        continueButton.setOnAction(_ -> primaryStage.getScene().setRoot(centerContainer));

        VBox winLayout = new VBox(15);
        winLayout.getStyleClass().add("combat-container");
        winLayout.getChildren().addAll(winLabel, continueButton);

        StackPane winContainer = new StackPane();
        winContainer.getChildren().add(winLayout);
        winContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(winContainer);
    }

    private void handleShowPlayerInfoButton(StackPane centerContainer) {
        infoLabel.setText(currentAdventurer.toString());
        infoLabel.setId("infoLabel");

        Button backButton = new Button("← Back to Combat");
        backButton.setId("backButton");
        backButton.setOnAction(_ -> primaryStage.getScene().setRoot(centerContainer));

        VBox infoLayout = new VBox(15);
        infoLayout.getStyleClass().add("combat-container");
        infoLayout.getChildren().addAll(infoLabel, backButton);

        StackPane infoContainer = new StackPane();
        infoContainer.getChildren().add(infoLayout);
        infoContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(infoContainer);
    }

    private void handleShowEnemyInfoButtonAction(@NotNull Enemy enemy, StackPane centerContainer) {
        infoLabel.setText(enemy.toString());
        infoLabel.setId("infoLabel");

        Button backButton = new Button("← Back to Combat");
        backButton.setId("backButton");
        backButton.setOnAction(_ -> primaryStage.getScene().setRoot(centerContainer));

        VBox infoLayout = new VBox(15);
        infoLayout.getStyleClass().add("combat-container");
        infoLayout.getChildren().addAll(infoLabel, backButton);

        StackPane infoContainer = new StackPane();
        infoContainer.getChildren().add(infoLayout);
        infoContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(infoContainer);
    }

    private void handleUseItemButtonAction(StackPane centerContainer) {
        initializePotionLabels();
        Button useBasicPotionButton = createBasicPotionButton();
        Button useMaxPotionButton = createMaxPotionButton();
        Button backButton = createBackButton(centerContainer);

        setupPotionUI(useBasicPotionButton, useMaxPotionButton, backButton);
    }

    private void initializePotionLabels() {
        potionInfoLabel.setText("Remaining potions:");
        potionInfoLabel.setId("infoLabel");

        basicPotionLabel.setText("Basic Potions: " + currentAdventurer.getBasicPotions());
        basicPotionLabel.setId("infoLabel");

        maxPotionLabel.setText("Max Potions: " + currentAdventurer.getMaxPotions());
        maxPotionLabel.setId("infoLabel");
    }

    private Button createBasicPotionButton() {
        Button button = new Button("Use Basic Potion");
        button.setOnAction(_ -> {
            if (currentAdventurer.getBasicPotions() > 0 && currentAdventurer.getHp() < currentAdventurer.getMaxHp()) {
                currentAdventurer.setBasicPotions(currentAdventurer.getBasicPotions() - 1);
                basicPotionLabel.setText("Basic Potions: " + currentAdventurer.getBasicPotions());
                int newHealth = Math.min(currentAdventurer.getHp() + 20, currentAdventurer.getMaxHp());
                currentAdventurer.setHp(newHealth);
                adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            } else {
                infoLabel.setText("You don't have any basic potions left.");
            }
        });
        return button;
    }

    private Button createMaxPotionButton() {
        Button button = new Button("Use Max Potion");
        button.setOnAction(_ -> {
            if (currentAdventurer.getMaxPotions() > 0 && currentAdventurer.getHp() < currentAdventurer.getMaxHp()) {
                currentAdventurer.setMaxPotions(currentAdventurer.getMaxPotions() - 1);
                maxPotionLabel.setText("Max Potions: " + currentAdventurer.getMaxPotions());
                int newHealth = Math.min(currentAdventurer.getHp() + 40, currentAdventurer.getMaxHp());
                currentAdventurer.setHp(newHealth);
                adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " | Weapon: " + currentAdventurer.getCurrentWeapon().getName());
            } else {
                infoLabel.setText("You don't have any max potions left.");
            }
        });
        return button;
    }

    private Button createBackButton(StackPane centerContainer) {
        Button button = new Button("← Back to Combat");
        button.setId("backButton");
        button.setOnAction(_ -> primaryStage.getScene().setRoot(centerContainer));
        return button;
    }

    private void setupPotionUI(Button useBasicPotionButton, Button useMaxPotionButton, Button backButton) {
        VBox itemLayout = new VBox(15);
        itemLayout.getStyleClass().add("combat-container");
        itemLayout.setAlignment(Pos.CENTER);
        itemLayout.getChildren().addAll(
                potionInfoLabel,
                basicPotionLabel,
                maxPotionLabel,
                useBasicPotionButton,
                useMaxPotionButton,
                backButton
        );

        StackPane potionsContainer = new StackPane();
        potionsContainer.getChildren().add(itemLayout);
        potionsContainer.setAlignment(Pos.CENTER);
        primaryStage.getScene().setRoot(potionsContainer);
    }

    @NotNull
    private Button getButton(String context) {
        Button seeRunInfoButton = new Button("See your run info");
        seeRunInfoButton.setOnAction(_ -> showRunInfoScreen(context));
        return seeRunInfoButton;
    }

    private void showRunInfoScreen(String context) {
        Label runInfoLabel = new Label(currentAdventurer.toString());
        runInfoLabel.setId("infoLabel");

        Button backButton = new Button("← Back");
        backButton.setId("backButton");
        backButton.setOnAction(_ -> handleBackAction(context));

        VBox runInfoLayout = new VBox(15);
        runInfoLayout.getStyleClass().add("combat-container");
        runInfoLayout.getChildren().addAll(runInfoLabel, backButton);

        StackPane runInfoContainer = new StackPane();
        runInfoContainer.getChildren().add(runInfoLayout);
        runInfoContainer.setAlignment(Pos.CENTER);

        primaryStage.getScene().setRoot(runInfoContainer);
    }

    private void handleBackAction(String context) {
        if (context.equals("flee")) {
            showFleeScreen();
        } else {
            showLoseScreen();
        }
    }

    private void showFleeScreen() {
        Label shameLabel = new Label("You have fled from battle...");
        shameLabel.setId("shameLabel");

        Label giveUpLabel = new Label("There's no place for shame in this world");
        giveUpLabel.setId("giveUpLabel");

        Button returnToMenuButton = createReturnToMenuButton();
        Button seeRunInfoButtonAgain = getButton("flee");

        VBox shameLayout = createResultLayout(shameLabel, giveUpLabel, seeRunInfoButtonAgain, returnToMenuButton);
        StackPane shameContainer = createContainer(shameLayout);

        primaryStage.getScene().setRoot(shameContainer);
    }

    private void showLoseScreen() {
        Label loseLabel = new Label("Your journey ends here..." + "\nNo one gets a second chance");
        loseLabel.setId("loseLabel");

        Button seeRunInfoButtonAgain = getButton("lose");
        Button returnToMenuButton = createReturnToMenuButton();

        VBox loseLayout = createResultLayout(loseLabel, seeRunInfoButtonAgain, returnToMenuButton);
        StackPane loseContainer = createContainer(loseLayout);

        primaryStage.getScene().setRoot(loseContainer);
    }

    private Button createReturnToMenuButton() {
        Button returnToMenuButton = new Button("🏠 Return to Menu");
        returnToMenuButton.setId("returnToMenuButton");
        returnToMenuButton.setOnAction(_ -> returnToStartingPage());
        return returnToMenuButton;
    }

    private VBox createResultLayout(Node... children) {
        VBox layout = new VBox(15);
        layout.getStyleClass().add("combat-container");
        layout.getChildren().addAll(children);
        return layout;
    }

    private StackPane createContainer(VBox layout) {
        StackPane container = new StackPane();
        container.getChildren().add(layout);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private static int regulatedAdd(int value, int increment) {
        return Math.min(value + increment, 100000);
    }
}
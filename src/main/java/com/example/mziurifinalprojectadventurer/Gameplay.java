package com.example.mziurifinalprojectadventurer;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.Random;

public class Gameplay {
    String url, user, password;
    private Stage primaryStage;
    private Adventurer currentAdventurer;
    private VBox currentRoot;
    Random random = new Random();

    public Gameplay(Stage primaryStage, Adventurer currentAdventurer, String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.primaryStage = primaryStage;
        this.currentAdventurer = currentAdventurer;
    }

    public Enemy chooseEnemy() throws SQLException {
        int randomId = random.nextInt(1, 10);
        Enemy currentEnemy = new Enemy();
        Connection connection = DriverManager.getConnection(url, user, password);
        String insertQuery = "SELECT * FROM monsters WHERE monster_id = ?;";
        PreparedStatement stmt = connection.prepareStatement(insertQuery);
        stmt.setInt(1, randomId);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            currentEnemy.setName(rs.getString("monster_name"));
            currentEnemy.setDamage(rs.getInt("monster_damage"));
            currentEnemy.setHp(rs.getInt("monster_hp"));
            currentEnemy.setDropBasic(rs.getInt("monster_drop_basic"));
            currentEnemy.setDropMax(rs.getInt("monster_drop_max"));
            currentEnemy.setDropExp(rs.getInt("monster_drop_exp"));
        }
        return currentEnemy;
    }

    public void Game() throws SQLException {
            Enemy currentEnemy = chooseEnemy();
            Label adventurerLabel = new Label("HP: " + currentAdventurer.getHp() + " Damage: " + currentAdventurer.getAttack());
            Label currentMonsterShortLabel = new Label("Monster: " + currentEnemy.getName() + " HP: " + currentEnemy.getHp());
            Button attackButton = new Button("Attack");
            Button showEnemyInfoButton = new Button("Enemy stats");
            Button useAnItemButton = new Button("Use An Item");
            Button run = new Button("Run");
            VBox mainRoot = new VBox(10, adventurerLabel, currentMonsterShortLabel, attackButton, showEnemyInfoButton, useAnItemButton, run);
            run.setOnAction(e -> {
                Label shameLabel = new Label("You have run away in shame");
                Label giveUpLabel = new Label("There's no point in trying to continue the journey");
                VBox shameLayout = new VBox(10, shameLabel, giveUpLabel);
                primaryStage.getScene().setRoot(shameLayout);
            });
            attackButton.setOnAction(e -> {
                double criticalHitNumber = random.nextDouble(0, 1);
                int damage = currentAdventurer.getAttack();
                if(criticalHitNumber >= 0.9){
                    damage *= 2;
                }
                currentEnemy.setHp(currentEnemy.getHp() - damage);
                currentAdventurer.setHp(currentAdventurer.getHp() - currentEnemy.getDamage());
                currentMonsterShortLabel.setText("Monster: " + currentEnemy.getName() + " HP: " + currentEnemy.getHp());
                adventurerLabel.setText("HP: " + currentAdventurer.getHp() + " Damage: " + currentAdventurer.getAttack());
            });
            showEnemyInfoButton.setOnAction(e -> {
                Label InfoLabel = new Label(
                        "Name: " + currentEnemy.getName() + "\n"
                        + "Damage: " + currentEnemy.getDamage() + "\n"
                        + "HP: " + currentEnemy.getHp() + "\n"
                        + "Basic potions drop: " + currentEnemy.getDropBasic() + "\n"
                        + "Max potions drop: " + currentEnemy.getDropMax() + "\n"
                        + "Drop exp: " + currentEnemy.getDropMax() + "\n"
                );
                Button backButton = new Button("<- Back");
                backButton.setOnAction(action -> {
                    currentRoot = mainRoot;
                    primaryStage.getScene().setRoot(mainRoot);
                });
                VBox infoLayout = new VBox(10, InfoLabel, backButton);
                primaryStage.getScene().setRoot(infoLayout);
            });

            Scene mainScene = new Scene(mainRoot,500, 400);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Adventure");
    }
}

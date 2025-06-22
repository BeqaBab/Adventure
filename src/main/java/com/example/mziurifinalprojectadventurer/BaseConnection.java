package com.example.mziurifinalprojectadventurer;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseConnection {
    private final Connection connection;

    public BaseConnection(String url, String user,String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public void newAccount(String username, String adventurerClass, Adventurer newAdventurer, long password, Label registrationMessageLabel) {
        try{
            String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_HP, adventurer_class, adventurer_password, max_health, basic_potions, max_potions, crit_chance, adventurer_exp, adventurer_level, defeated_monsters) VALUES(?, ?, ?, ?, ?, ?, ?, ?, 0, 1, 0)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setInt(2, newAdventurer.getMaxHp());
                stmt.setString(3, adventurerClass);
                stmt.setLong(4, password);
                stmt.setInt(5, newAdventurer.getMaxHp());
                stmt.setInt(6, newAdventurer.getBasicPotions());
                stmt.setInt(7, newAdventurer.getMaxPotions());
                stmt.setDouble(8, newAdventurer.getCritChance());

                if (stmt.executeUpdate() > 0) {
                    handleSuccessfulRegistration(connection, stmt, newAdventurer, registrationMessageLabel);
                } else {
                    registrationMessageLabel.setText("User couldn't be added.");
                }
            }
        } catch (SQLException ex) {
            registrationMessageLabel.setText("User couldn't be added. Please try a different name.");
        }
    }

    private void handleSuccessfulRegistration(Connection connection, PreparedStatement stmt, Adventurer newAdventurer, Label registrationMessageLabel) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                int id = rs.getInt(1);
                newAdventurer.setId(id);
                assignStartingWeapon(connection, id, newAdventurer.getAdventurerClass());
                registrationMessageLabel.setText("User added successfully!");
            }
        }
    }

    private void assignStartingWeapon(Connection connection, int adventurerId, String adventurerClass) throws SQLException {
        List<Weapon> startingWeapons = getWeaponsByLevel(1, adventurerClass);
        if (!startingWeapons.isEmpty()) {
            String updateQuery = "UPDATE adventurer SET weapon_id = ? WHERE adventurer_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, startingWeapons.getFirst().getId());
                updateStmt.setInt(2, adventurerId);
                updateStmt.executeUpdate();
            }
        }
    }

    public Adventurer loginToExistingAccount(String username, String adventurerClass, long password, Label loginMessageLabel, String userPassword, TextField loginPasswordField) {
        try {
            if (!validateLoginCredentials(username, adventurerClass, password, loginMessageLabel, userPassword, loginPasswordField)) {
                return null;
            }
            return loadAdventurer(username, adventurerClass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateLoginCredentials(String username, String adventurerClass, long password, Label loginMessageLabel, String userPassword, TextField loginPasswordField) throws SQLException {
        String checkQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, adventurerClass);
            try (ResultSet rs = stmt.executeQuery()) {
                return processLoginValidation(rs, password, loginMessageLabel, userPassword, loginPasswordField);
            }
        }
    }

    private boolean processLoginValidation(ResultSet rs, long password, Label loginMessageLabel, String userPassword, TextField loginPasswordField) throws SQLException {
        if (!rs.next()) {
            loginMessageLabel.setText("Account with this name and class doesn't exist");
            loginPasswordField.clear();
            return false;
        }
        long storedPassword = rs.getLong("adventurer_password");
        if (userPassword.isBlank() || storedPassword == 0) {
            loginMessageLabel.setText("Please enter password");
            loginPasswordField.clear();
            return false;
        }
        if (storedPassword != password) {
            loginMessageLabel.setText("Wrong password");
            loginPasswordField.clear();
            return false;
        }
        return true;
    }

    private Adventurer loadAdventurer(String username, String adventurerClass) throws SQLException {
        String selectQuery = "SELECT * FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, adventurerClass);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? loadAdventurerData(rs) : null;
            }
        }
    }

    private Adventurer loadAdventurerData(ResultSet rs) throws SQLException {
        Adventurer currentAdventurer = new Adventurer();
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
        currentAdventurer.setDefeatedMonsters(rs.getInt("defeated_monsters"));
        currentAdventurer.setCritChance(rs.getDouble("crit_chance"));
        return currentAdventurer;
    }

    public Weapon getWeaponById(int weaponId) throws SQLException {
        String query = "SELECT * FROM weapons WHERE weapon_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, weaponId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Weapon weapon = new Weapon();
                    weapon.setId(rs.getInt("weapon_id"));
                    weapon.setName(rs.getString("weapon_name"));
                    weapon.setDamage(rs.getInt("weapon_damage"));
                    weapon.setLevelRequirement(rs.getInt("weapon_level_requirement"));
                    return weapon;
                }
            }
        }
        return null;
    }

    public List<Weapon> getWeaponsByLevel(int level, String adventurerClass) throws SQLException {
        String query = "SELECT * FROM weapons WHERE weapon_level_requirement <= ? AND weapon_class = ? ORDER BY weapon_damage";
        List<Weapon> weapons = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, level);
            stmt.setString(2, adventurerClass);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    weapons.add(createWeaponFromResultSet(rs));
                }
            }
        }
        return weapons;
    }

    private Weapon createWeaponFromResultSet(ResultSet rs) throws SQLException {
        Weapon weapon = new Weapon();
        weapon.setId(rs.getInt("weapon_id"));
        weapon.setName(rs.getString("weapon_name"));
        weapon.setDamage(rs.getInt("weapon_damage"));
        weapon.setLevelRequirement(rs.getInt("weapon_level_requirement"));
        return weapon;
    }

    public void saveProgressToDataBase(Adventurer currentAdventurer){
        String updateQuery = "UPDATE adventurer SET adventurer_level = ?, adventurer_exp = ?, adventurer_HP = ?, basic_potions = ?, max_potions = ?, max_health = ?, weapon_id = ?, defeated_monsters = ? WHERE adventurer_id = ?";
        try{
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setInt(1, currentAdventurer.getLevel());
            stmt.setInt(2, currentAdventurer.getExp());
            stmt.setInt(3, currentAdventurer.getHp());
            stmt.setInt(4, currentAdventurer.getBasicPotions());
            stmt.setInt(5, currentAdventurer.getMaxPotions());
            stmt.setInt(6, currentAdventurer.getMaxHp());
            stmt.setInt(7, currentAdventurer.getWeaponId());
            stmt.setInt(8, currentAdventurer.getDefeatedMonsters());
            stmt.setInt(9, currentAdventurer.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Enemy chooseEnemy(){
        Random random = new Random();
        int randomId = random.nextInt(1, 10);
        Enemy currentEnemy;
        try {
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

    public void deleteProgress(Adventurer currentAdventurer){
        try{
            String insertQuery = "DELETE FROM adventurer WHERE adventurer_id = ?;";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, currentAdventurer.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
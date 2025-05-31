package com.example.mziurifinalprojectadventurer;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public BaseConnection(String password, String user, String url) {
        this.password = password;
        this.user = user;
        this.url = url;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void newAccount(String username, String adventurerClass, Adventurer newAdventurer, long password, Label registrationMessageLabel){
        try{
            String insertQuery = "INSERT INTO adventurer(adventurer_name, adventurer_level, adventurer_exp, adventurer_HP adventurer_class, adventurer_password, max_health) VALUES(?, 1, 0, ?, ?, ?, ?)";
            try (PreparedStatement stmt = getConnection().prepareStatement(insertQuery)) {
                stmt.setString(1, username);
                stmt.setInt(2, newAdventurer.getMaxHp());
                stmt.setString(3, adventurerClass);
                stmt.setLong(4, password);
                stmt.setInt(5, newAdventurer.getMaxHp());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            newAdventurer.setId(id);
                            List<Weapon> startingWeapons = getWeaponsByLevel(1);
                            if (!startingWeapons.isEmpty()) {
                                String updateQuery = "UPDATE adventurer SET weapon_id = ? WHERE adventurer_id = ?";
                                try (PreparedStatement updateStmt = getConnection().prepareStatement(updateQuery)) {
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
    }

    public Adventurer loginToExistingAccount(String username, String adventurerClass, long password, Label loginMessageLabel, String userPassword, TextField loginPasswordField){
        Adventurer currentAdventurer = new Adventurer();
        try {
            String checkQuery = "SELECT adventurer_password FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
            try (PreparedStatement stmt = getConnection().prepareStatement(checkQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        loginMessageLabel.setText("Account doesn't exist");
                        loginPasswordField.clear();
                        return null;
                    }
                    long storedPassword = rs.getLong("adventurer_password");
                    if (userPassword.isBlank() || storedPassword == 0) {
                        loginMessageLabel.setText("Please enter password");
                        loginPasswordField.clear();
                        return null;
                    }
                    if (storedPassword != password) {
                        loginMessageLabel.setText("Wrong password");
                        loginPasswordField.clear();
                        return null;
                    }
                }
            }

            String selectQuery = "SELECT * FROM adventurer WHERE adventurer_name = ? AND adventurer_class = ?";
            PreparedStatement stmt = getConnection().prepareStatement(selectQuery);
            try {
                stmt.setString(1, username);
                stmt.setString(2, adventurerClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentAdventurer = loadAdventurerData(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currentAdventurer;
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
        return currentAdventurer;
    }

    public Weapon getWeaponById(int weaponId) throws SQLException {
        String query = "SELECT * FROM weapons WHERE weapon_id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        try{
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Weapon> getWeaponsByLevel(int level) throws SQLException {
        String query = "SELECT * FROM weapons WHERE weapon_level_requirement <= ? ORDER BY weapon_damage";
        List<Weapon> weapons = new ArrayList<>();
        PreparedStatement stmt = getConnection().prepareStatement(query);
        try{
            stmt.setInt(1, level);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Weapon weapon = new Weapon();
                    weapon.setId(rs.getInt("weapon_id"));
                    weapon.setName(rs.getString("weapon_name"));
                    weapon.setDamage(rs.getInt("weapon_damage"));
                    weapon.setLevelRequirement(rs.getInt("weapon_level_requirement"));
                    weapons.add(weapon);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return weapons;
    }
}

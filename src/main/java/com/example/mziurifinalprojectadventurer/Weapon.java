package com.example.mziurifinalprojectadventurer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Weapon {
    private int id;
    private String name;
    private int damage;
    private int levelRequirement;
    private String url;
    private String user;
    private String password;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getLevelRequirement() { return levelRequirement; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setLevelRequirement(int levelRequirement) { this.levelRequirement = levelRequirement; }
    public void setUrl(String url) { this.url = url; }
    public void setUser(String user) { this.user = user; }
    public void setPassword(String password) { this.password = password; }

    public Weapon getWeaponById(int weaponId) throws SQLException {
        String query = "SELECT * FROM weapons WHERE weapon_id = ?";
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt = connection.prepareStatement(query);
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
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt = connection.prepareStatement(query);
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

    public int calculateDamage() {
        Random random = new Random();
        double critChance = 0.2;
        if (random.nextDouble() < critChance) {
            return (int)(damage * 1.5); // 50% damage increase on crit
        }
        return damage;
    }

    @Override
    public String toString() {
        return name + " \n(Damage: " + damage + ", \nLevel Requirement: " + levelRequirement + ")";
    }
}
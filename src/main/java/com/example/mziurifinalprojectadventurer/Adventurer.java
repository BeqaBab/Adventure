package com.example.mziurifinalprojectadventurer;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Adventurer {
    private String name;
    private String adventurerClass;
    private int level;
    private int exp;
    private int hp;
    private int maxHp;
    private int id;
    private int basicPotions;
    private int maxPotions;
    private int weaponId;
    private int defeatedMonsters;
    private Weapon currentWeapon;
    private double critChance;

    public Adventurer() {
        this.level = 1;
        this.exp = 0;
        this.basicPotions = 10;
        this.maxPotions = 5;
        this.defeatedMonsters = 0;
        this.currentWeapon = new Weapon();
    }

    public double getCritChance() {
        return critChance;
    }
    public int getWeaponId() { return weaponId; }
    public void setWeaponId(int weaponId) { this.weaponId = weaponId; }
    public int getMaxHp() { return maxHp; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getHp() { return hp; }
    public int getBasicPotions() { return basicPotions; }
    public int getMaxPotions() { return maxPotions; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public int getDefeatedMonsters() {
        return defeatedMonsters;
    }
    public String getAdventurerClass() {
        return adventurerClass;
    }

    public void setCritChance(double critChance) {
        this.critChance = critChance;
    }
    public void defeatMonster(){ if(this.defeatedMonsters < 100000) this.defeatedMonsters++;}
    public void setName(String name) { this.name = name; }
    public void setAdventurerClass(String adventurerClass) { this.adventurerClass = adventurerClass; }
    public void setLevel(int level) { this.level = level; }
    public void setExp(int exp) { this.exp = exp; }
    public void setHp(int hp) { this.hp = hp; }
    public void setBasicPotions(int basicPotions) { this.basicPotions = basicPotions; }
    public void setMaxPotions(int maxPotions) { this.maxPotions = maxPotions; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setDefeatedMonsters(int defeatedMonsters) {
        this.defeatedMonsters = defeatedMonsters;
    }

    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
        if (weapon != null) {
            this.weaponId = weapon.getId();
        }
    }

    public int calculateDamage() {
        Random random = new Random();
        if (random.nextDouble() < critChance) {
            return (int)(getCurrentWeapon().getDamage() * 1.5);
        }
        return getCurrentWeapon().getDamage();
    }

    public void checkAndUpgradeWeapon(BaseConnection baseConnection) throws SQLException {
        List<Weapon> availableWeapons = baseConnection.getWeaponsByLevel(this.level, adventurerClass);
        Weapon bestWeapon = availableWeapons.stream()
                .max(Comparator.comparingInt(Weapon::getDamage))
                .orElse(currentWeapon);
        if (bestWeapon.getDamage() > currentWeapon.getDamage()) {
            setCurrentWeapon(bestWeapon);
        }
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "Class: " + adventurerClass + "\n" +
                "Level: " + level + "\n" +
                "Exp: " + exp + "\n" +
                "Potions: " + basicPotions + " basic, " + maxPotions + " max\n" +
                "Weapon: " + (currentWeapon != null ? currentWeapon.toString() : "None") + "\n" +
                "Defeated Monsters: " + defeatedMonsters;
    }
}
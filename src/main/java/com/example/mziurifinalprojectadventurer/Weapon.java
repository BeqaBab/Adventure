package com.example.mziurifinalprojectadventurer;

public class Weapon {
    private int id;
    private String name;
    private int damage;
    private int levelRequirement;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDamage() { return damage; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setLevelRequirement(int levelRequirement) { this.levelRequirement = levelRequirement; }

    @Override
    public String toString() {
        return name + " \n(Damage: " + damage + ", \nLevel Requirement: " + levelRequirement + ")";
    }
}
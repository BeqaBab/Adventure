package com.example.mziurifinalprojectadventurer;

public class Adventurer {
    private String name, adventurerClass;
    private int level, exp, hp, attack, maxHp;
    private int basicPotions, maxPotions;

    public Adventurer() {
        this.level = 1;
        this.exp = 0;
        this.basicPotions = 10;
        this.maxPotions = 5;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public String getName() {
        return name;
    }

    public String getAdventurerClass() {
        return adventurerClass;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getBasicPotions() {
        return basicPotions;
    }

    public int getMaxPotions() {
        return maxPotions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdventurerClass(String adventurer_class) {
        this.adventurerClass = adventurer_class;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setBasicPotions(int basicPotions) {
        this.basicPotions = basicPotions;
    }

    public void setMaxPotions(int maxPotions) {
        this.maxPotions = maxPotions;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
}

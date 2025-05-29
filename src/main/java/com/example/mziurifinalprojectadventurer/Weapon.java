package com.example.mziurifinalprojectadventurer;

import java.util.Random;

public abstract class Weapon {
    protected String name;
    protected int baseDamage;
    protected int levelRequirement;

    public Weapon(String name, int baseDamage, int levelRequirement) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.levelRequirement = levelRequirement;
    }

    public String getName() {
        return name;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public abstract int calculateDamage(Random random);

    @Override
    public String toString() {
        return name;
    }
}


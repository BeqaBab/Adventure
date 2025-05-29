package com.example.mziurifinalprojectadventurer;

import java.util.Random;

public class WoodenSword extends Weapon {
    public WoodenSword() {
        super("🗡Wooden Sword", 15, 1);
    }

    @Override
    public int calculateDamage(Random random) {
        return random.nextInt(100) >= 90 ? baseDamage * 2 : baseDamage;
    }
}

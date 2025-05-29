package com.example.mziurifinalprojectadventurer;

import java.util.Random;

public class MagicStaff extends Weapon {
    public MagicStaff() {
        super("Magic Staff", 30, 10);
    }

    @Override
    public int calculateDamage(Random random) {
        return random.nextInt(100) >= 85 ? baseDamage * 2 : baseDamage;
    }
}

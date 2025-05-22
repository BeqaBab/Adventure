package com.example.mziurifinalprojectadventurer;

public class Enemy {
    private String name;
    private int damage;
    int hp;
    private int dropBasic;
    private int dropMax;
    private int dropExp;

    public int getDropExp() {
        return dropExp;
    }

    public void setDropExp(int dropExp) {
        this.dropExp = dropExp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDropBasic(int dropBasic) {
        this.dropBasic = dropBasic;
    }

    public void setDropMax(int dropMax) {
        this.dropMax = dropMax;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getHp() {
        return hp;
    }

    public int getDropBasic() {
        return dropBasic;
    }

    public int getDropMax() {
        return dropMax;
    }
}

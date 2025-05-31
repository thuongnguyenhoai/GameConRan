package com.example.ransanmoi;

public class SnakeSkin {
    private int headDrawable;
    private int bodyDrawable;
    private int tailDrawable;
    private String name;

    public SnakeSkin(int headDrawable, int bodyDrawable, int tailDrawable, String name) {
        this.headDrawable = headDrawable;
        this.bodyDrawable = bodyDrawable;
        this.tailDrawable = tailDrawable;
        this.name = name;
    }

    public int getHeadDrawable() {
        return headDrawable;
    }

    public int getBodyDrawable() {
        return bodyDrawable;
    }

    public int getTailDrawable() {
        return tailDrawable;
    }

    public String getName() {
        return name;
    }
} 
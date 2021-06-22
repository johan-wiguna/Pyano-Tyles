package com.example.tb2;

public class Tile {
    private float height,width;
    private float x,y;
    private boolean touched;

    public Tile(float x,float y, float height, float width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.touched = false;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }
}

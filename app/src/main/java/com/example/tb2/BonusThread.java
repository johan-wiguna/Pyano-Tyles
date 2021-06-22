package com.example.tb2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BonusThread implements Runnable {
    protected Thread thread;
    protected UIThreadedWrapper uiThreadedWrapper;
    protected Random rnd;
    protected float dy = 6;
    protected float hCanvas, wCanvas;
    protected ArrayList<Tile> arrTile;
    protected boolean onPause = false;
    protected boolean endGame = false;
    protected BonusPresenter presenter;
    protected int sleepTime = 5;

    public BonusThread(BonusPresenter presenter, UIThreadedWrapper uiThreadedWrapper, float hCanvas, float wCanvas) {
        this.presenter = presenter;
        this.uiThreadedWrapper = uiThreadedWrapper;
        this.thread = new Thread(this);
        this.rnd = new Random();
        this.hCanvas = hCanvas;
        this.wCanvas = wCanvas;
        this.arrTile = new ArrayList<>();
    }

    public ArrayList<Tile> getArrTile() {
        return arrTile;
    }

    public void startThread() {
        this.thread.start();
    }

    public boolean isOnPause() {
        return onPause;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public void setOnPause(boolean onPause) {
        this.onPause = onPause;
    }

    //lebar setiap tile = layar / 2;
    public float randomX(float wCanvas) {
        float lebar = wCanvas / 2;
        float[] pos = new float[2];
        pos[0] = lebar / 2;
        pos[1] = lebar + lebar / 2;
        int i = rnd.nextInt(pos.length);
        return pos[i];
    }

    @Override
    public void run() {
        //draw countdown before start
        for (int i = 3; i >= 0; i--) {
            if (i == 0) {
                this.presenter.drawCountdown("Start!");
            } else {
                this.presenter.drawCountdown(Integer.toString(i));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Tile tile = new Tile(this.randomX(wCanvas), -this.hCanvas / 8, this.hCanvas / 4, this.wCanvas / 2);
        this.arrTile.add(tile);

        while (!isEndGame()) {
            if (!isOnPause()) {
                //move tile
                for (Tile t : arrTile) {
                    t.setY(t.getY() + dy);
                }

                //draw tile
                uiThreadedWrapper.setMsgDrawTilesBonus((List<Tile>) arrTile.clone());

                //remove tile
                if (arrTile.get(0).getY() >= this.hCanvas + this.hCanvas / 8) {
                    if (arrTile.get(0).isTouched()) {
                        this.arrTile.remove(0);
                    } else {
                        uiThreadedWrapper.setMsgEndBonus();
                    }
                }

                //spawn tile
                if (tile.getY() >= tile.getHeight() / 2) {
                    tile = new Tile(this.randomX(wCanvas), -this.hCanvas / 8, this.hCanvas / 4, this.wCanvas / 2);
                    this.arrTile.add(tile);
                    Log.d("ARRSIZE", Integer.toString(this.arrTile.size()));
                }

            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

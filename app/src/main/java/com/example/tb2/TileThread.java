package com.example.tb2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileThread implements Runnable{
    protected Thread thread;
    protected UIThreadedWrapper uiThreadedWrapper;
    protected Random rnd;
    protected float dy = 21;
    protected float hCanvas,wCanvas;
    protected ArrayList<Tile> arrTile;
    protected boolean onPause = false;
    protected boolean endGame = false;
    protected int diff;
    protected MainPresenter presenter;
    protected int sleepTime = 0;

    public TileThread(MainPresenter presenter,UIThreadedWrapper uiThreadedWrapper, float hCanvas, float wCanvas) {
        this.presenter = presenter;
        this.uiThreadedWrapper = uiThreadedWrapper;
        this.thread = new Thread(this);
        this.rnd = new Random();
        this.hCanvas = hCanvas;
        this.wCanvas = wCanvas;
        this.arrTile = new ArrayList<>();
    }

    public void resetArrayList(){
        this.arrTile = new ArrayList<>();
    }

    public void setDiff(int diff) {
        this.diff = diff;
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

    //lebar setiap tile = layar / 4;
    public float randomX(float wCanvas){
        float lebar = wCanvas / 4;
        float[] pos = new float[4];
        pos[0] = lebar/2;
        pos[1] = lebar + lebar/2;
        pos[2] = (lebar * 2) + lebar/2;
        pos[3] = (lebar * 3) + lebar/2;
        int i = rnd.nextInt(pos.length);
        return pos[i];
    }

    @Override
    public void run() {
        //draw countdown before start
        for(int i = 3; i>=0; i--){
            if(i == 0){
                this.presenter.drawCountdown("Start!");
            }else{
                this.presenter.drawCountdown(Integer.toString(i));
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        Tile tile = new Tile(this.randomX(wCanvas),-this.hCanvas/8,this.hCanvas/4,this.wCanvas/4);
        this.arrTile.add(tile);

        if(diff == 0){
            sleepTime = 19;
        }else if(diff == 1){
            sleepTime = 14;
        }else if(diff == 2){
            sleepTime = 11;
        }

        while(!isEndGame()){
            if(!isOnPause()){
                //move tile
                for(Tile t : arrTile){
                    t.setY(t.getY()+dy);
                }

                //draw tile
                uiThreadedWrapper.setMsgDrawTiles((List<Tile>) arrTile.clone());

                //remove tile
                if(arrTile.get(0).getY() >= this.hCanvas + this.hCanvas/8){
                    if(arrTile.get(0).isTouched()){
                        this.arrTile.remove(0);
                    }else{
                        uiThreadedWrapper.setMsgEndGame();
                    }
                }

                //spawn tile
                if(tile.getY() >= tile.getHeight()/2){
                    tile = new Tile(this.randomX(wCanvas),-this.hCanvas/8,this.hCanvas/4,this.wCanvas/4);
                    this.arrTile.add(tile);
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

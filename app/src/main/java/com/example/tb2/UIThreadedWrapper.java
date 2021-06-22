package com.example.tb2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

public class UIThreadedWrapper extends Handler {
    protected final static int MSG_DRAW_TILES = 101;
    protected final static int MSG_END_GAME = 202;
    protected final static int MSG_DRAW_TILES_BONUS = 303;
    protected final static int MSG_END_BONUS = 404;
    protected MainActivity mainActivity;

    public UIThreadedWrapper(MainActivity mainActivity, Looper looper) {
        super(looper);
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg){
        if(msg.what == this.MSG_DRAW_TILES){
            this.mainActivity.presenter.drawTiles((List<Tile>) msg.obj);
        }else if(msg.what == this.MSG_END_GAME){
            this.mainActivity.presenter.endGame();
        }else if(msg.what == this.MSG_DRAW_TILES_BONUS){
            this.mainActivity.bonusPresenter.drawTiles((List<Tile>) msg.obj);
        }else if(msg.what == this.MSG_END_BONUS){
            this.mainActivity.bonusPresenter.endBonus();
        }
    }

    public void setMsgDrawTiles(List<Tile> t){
        Message msg = new Message();
        msg.what = MSG_DRAW_TILES;
        msg.obj = t;
        this.sendMessage(msg);
    }

    public void setMsgDrawTilesBonus(List<Tile> t){
        Message msg = new Message();
        msg.what = MSG_DRAW_TILES_BONUS;
        msg.obj = t;
        this.sendMessage(msg);
    }

    public void setMsgEndGame(){
        Message msg = new Message();
        msg.what = MSG_END_GAME;
        this.sendMessage(msg);
    }

    public void setMsgEndBonus(){
        Message msg = new Message();
        msg.what = MSG_END_BONUS;
        this.sendMessage(msg);
    }
}

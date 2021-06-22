package com.example.tb2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class BonusPresenter{
    protected MainActivity activity;
    protected IMainActivity ui;
    protected Bitmap mBitmap;
    protected ImageView ivGame;
    protected Canvas canvas;
    protected BonusThread threadBonus;
    protected ArrayList<Tile> tiles;
    protected int currScore = 0;
    protected int highScore = 0;
    protected boolean endFirstTime = true;
    protected float azimuth = 0f;
    protected boolean checkable = true;

    public BonusPresenter(MainActivity mainActivity, IMainActivity ui) {
        this.activity = mainActivity;
        this.ui = ui;
        this.tiles = new ArrayList<>();
    }

    public void setIvGame(ImageView ivGame) {
        this.ivGame = ivGame;
    }

    public int getCurrScore() {
        return currScore;
    }

    public void setCurrScore(int currScore) {
        this.currScore = currScore;
    }

    public void setNilaix(float nilaix) {
        this.azimuth = nilaix;
        Log.d("debug","NILAI X" + nilaix);
    }

//    public void increaseTileSpeed(){
//        this.threadBonus.sleepTime--;
//    }

    public void drawCountdown(String text){
        this.canvas.drawColor(this.activity.getResources().getColor(R.color.white));
        Paint paint = new Paint();
        int mColorTest = ResourcesCompat.getColor(activity.getResources(), R.color.black, null);
        paint.setColor(mColorTest);
        paint.setTextAlign(Paint.Align.LEFT);

        Rect r = new Rect();
        this.canvas.getClipBounds(r);

        int cHeight = r.height();
        int cWidth = r.width();

        paint.setTextSize(100);
        paint.getTextBounds(text, 0, text.length(), r);

        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        this.canvas.drawText(text, x, y, paint);
        this.ivGame.invalidate();
    }

    public void startBonus(){
        //Create bitmap
        this.mBitmap = Bitmap.createBitmap(this.ivGame.getWidth(), this.ivGame.getHeight(), Bitmap.Config.ARGB_8888);

        //Associate
        this.ivGame.setImageBitmap(mBitmap);

        //Create canvas
        this.canvas = new Canvas(mBitmap);

        //Draw canvas
        int mColorBackground = ResourcesCompat.getColor(activity.getResources(), R.color.white, null);
        this.canvas.drawColor(mColorBackground);

        this.threadBonus = new BonusThread(this,activity.objUIWrapper, this.ivGame.getHeight(), this.ivGame.getWidth());
        this.threadBonus.startThread();
    }

    public void resetScoreTv(){
        this.activity.resetBonusScore();
    }

    public void pauseGame(){
        this.threadBonus.setOnPause(true);
    }

    public void resumeGame() { this.threadBonus.setOnPause(false);}

    public void endBonus(){
        if(endFirstTime){
            endFirstTime = false;
            this.threadBonus.setEndGame(true);
            Dialog gameOverDialog = new Dialog(this.activity);
            gameOverDialog.setContentView(R.layout.dialog_game_over);
            gameOverDialog.setCanceledOnTouchOutside(false);
            gameOverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            gameOverDialog.show();

            TextView scoreTv = gameOverDialog.findViewById(R.id.tv_game_over_score);
            scoreTv.setText(Integer.toString(this.currScore));

            TextView newHighScoreTv = gameOverDialog.findViewById(R.id.tv_new_high_score);
            TextView currentHighScoreTv = gameOverDialog.findViewById(R.id.tv_current_high_score);
            currentHighScoreTv.setText(Integer.toString(this.highScore));
            if(this.currScore > this.highScore){
                this.highScore= this.currScore;
                newHighScoreTv.setVisibility(View.VISIBLE);
                currentHighScoreTv.setText(Integer.toString(currScore));
                newHighScoreTv.invalidate();
            }

            Button retryBtn = gameOverDialog.findViewById(R.id.btn_game_over_retry);
            retryBtn.setOnClickListener(v -> {
                gameOverDialog.dismiss();
                this.currScore = 0;
                resetScoreTv();
                startBonus();
            });

            Button menuBtn = gameOverDialog.findViewById(R.id.btn_game_over_menu);
            menuBtn.setOnClickListener(v -> {
                gameOverDialog.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            });
        }

    }

    public void drawTiles(List<Tile> t){
        Paint paint = new Paint();
        int mColorTest = ResourcesCompat.getColor(activity.getResources(), R.color.black, null);
        paint.setColor(mColorTest);

        Paint paint2 = new Paint();
        int mColorTest2 = ResourcesCompat.getColor(activity.getResources(), R.color.dark_grey, null);
        paint2.setColor(mColorTest2);

        this.canvas.drawColor(this.activity.getResources().getColor(R.color.white));

        this.setTiles();

        for(Tile tile: t){
            if(!tile.isTouched()){
                this.canvas.drawRect(tile.getX() - tile.getWidth()/2,tile.getY() - tile.getHeight()/2,tile.getX() + tile.getWidth()/2,tile.getY() + tile.getHeight()/2,paint);
            }else if(tile.isTouched()){
                this.canvas.drawRect(tile.getX() - tile.getWidth()/2,tile.getY() - tile.getHeight()/2,tile.getX() + tile.getWidth()/2,tile.getY() + tile.getHeight()/2,paint2);
            }
        }

        this.ivGame.invalidate();
    }

    public void setTiles(){
        this.tiles = this.threadBonus.getArrTile();
    }

    public void addScore(TextView tv){
        this.currScore++;
        tv.setText(Integer.toString(currScore));
    }

    public void checkMove(TextView score){
        if(this.azimuth < 2f && this.azimuth > -2f){
            this.checkable = true;
        }

        if(checkable){
            for(Tile tile: tiles){
                if(!tile.isTouched()){
                    if(tile.getX() > this.ivGame.getWidth()/2){
                        if(this.azimuth < -2f){
                            tile.setTouched(true);
                            this.addScore(score);
                            this.checkable = false;
                        }
                    }else if (tile.getX() < this.ivGame.getWidth()/2){
                        if(this.azimuth > 2f){
                            tile.setTouched(true);
                            this.addScore(score);
                            this.checkable = false;
                        }
                    }
                }
            }
        }
    }

    public interface IMainActivity{

    }
}

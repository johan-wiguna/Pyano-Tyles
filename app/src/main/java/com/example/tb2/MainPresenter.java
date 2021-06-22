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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements View.OnTouchListener{
    protected MainActivity activity;
    protected IMainActivity ui;
    protected Bitmap mBitmap;
    protected ImageView ivGame;
    protected Canvas canvas;
    protected int difficulty;
    protected TileThread thread;
    protected int song;
    protected ArrayList<Tile> tiles;
    protected int currScore = 0;
    protected int[] highScore;

    public MainPresenter(MainActivity mainActivity, IMainActivity ui) {
        this.activity = mainActivity;
        this.ui = ui;
        this.tiles = new ArrayList<>();
        this.highScore = new int[3];
        for (int i=0; i<highScore.length; i++){
            this.highScore[i] = 0;
        }
    }

    public void setIvGame(ImageView ivGame) {
        this.ivGame = ivGame;
    }

    public void setSong(int song) {
        this.song = song;
    }

    public int getSong() {
        return song;
    }

    public int getDifficulty(){
        return this.difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getCurrScore() {
        return currScore;
    }

    public void setCurrScore(int currScore) {
        this.currScore = currScore;
    }


    public void increaseTileSpeed(){
        this.thread.sleepTime--;
    }

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

    public void startGame(){
        //Create bitmap
        this.mBitmap = Bitmap.createBitmap(this.ivGame.getWidth(), this.ivGame.getHeight(), Bitmap.Config.ARGB_8888);

        //Associate
        this.ivGame.setImageBitmap(mBitmap);

        //Create canvas
        this.canvas = new Canvas(mBitmap);

        //Draw canvas
        int mColorBackground = ResourcesCompat.getColor(activity.getResources(), R.color.white, null);
        this.canvas.drawColor(mColorBackground);

        this.ivGame.setOnTouchListener(this);

        this.thread = new TileThread(this,activity.objUIWrapper, this.ivGame.getHeight(), this.ivGame.getWidth());
        if(this.difficulty == 0){
            this.thread.setDiff(0);
        }else if(this.difficulty == 1){
            this.thread.setDiff(1);
        }else if(this.difficulty == 2){
            this.thread.setDiff(2);
        }
        this.thread.startThread();
    }

    public void resetScoreTv(){
        this.activity.resetGameplayScore();
    }

    public void pauseGame(){
        this.thread.setOnPause(true);
    }

    public void resumeGame() { this.thread.setOnPause(false);}

    public void endGame(){
        this.thread.setEndGame(true);
        Dialog gameOverDialog = new Dialog(this.activity);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        gameOverDialog.setCanceledOnTouchOutside(false);
        gameOverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameOverDialog.show();

        TextView scoreTv = gameOverDialog.findViewById(R.id.tv_game_over_score);
        scoreTv.setText(Integer.toString(this.currScore));

        TextView newHighScoreTv = gameOverDialog.findViewById(R.id.tv_new_high_score);
        TextView currentHighScoreTv = gameOverDialog.findViewById(R.id.tv_current_high_score);
        currentHighScoreTv.setText(Integer.toString(this.highScore[this.difficulty]));
        if(this.currScore > this.highScore[this.difficulty]){
            this.highScore[this.difficulty] = this.currScore;
            newHighScoreTv.setVisibility(View.VISIBLE);
            currentHighScoreTv.setText(Integer.toString(currScore));
            newHighScoreTv.invalidate();
        }

        Button retryBtn = gameOverDialog.findViewById(R.id.btn_game_over_retry);
        retryBtn.setOnClickListener(v -> {
            gameOverDialog.dismiss();
            this.resetScoreTv();
            this.thread.resetArrayList();
            this.startGame();
        });

        Button menuBtn = gameOverDialog.findViewById(R.id.btn_game_over_menu);
        menuBtn.setOnClickListener(v -> {
            gameOverDialog.dismiss();
            Intent intent = new Intent(activity, MainActivity.class);
            this.activity.startActivity(intent);
        });
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
        this.tiles = this.thread.getArrTile();
    }

    public void addScore(TextView tv){
        currScore++;
        tv.setText(Integer.toString(currScore));
        if(this.currScore % 25 == 0 && this.currScore <= 100){
            increaseTileSpeed();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float xT = event.getX();
            float yT = event.getY();

            float batasKiri, batasAtas, batasKanan, batasBawah;
            boolean touched = false;
            for(Tile tile: tiles){
                if(!tile.isTouched()) {
                    batasKiri = tile.getX() - tile.getWidth() / 2;
                    batasKanan = tile.getX() + tile.getWidth() / 2;
                    batasAtas = tile.getY() - tile.getHeight() / 2;
                    batasBawah = tile.getY() + tile.getHeight() / 2;
                    if (xT >= batasKiri && xT <= batasKanan && yT >= batasAtas && yT <= batasBawah) {
                        tile.setTouched(true);
                        addScore(v.getRootView().findViewById(R.id.tv_gameplay_score));
                        touched = true;
                    }
                    break;
                }
            }
            if(!touched){
                boolean touched2 = false;
                for(Tile tile: tiles){
                    batasKiri = tile.getX() - tile.getWidth() / 2;
                    batasKanan = tile.getX() + tile.getWidth() / 2;
                    batasAtas = tile.getY() - tile.getHeight() / 2;
                    batasBawah = tile.getY() + tile.getHeight() / 2;
                    if (xT >= batasKiri && xT <= batasKanan && yT >= batasAtas && yT <= batasBawah) {
                        touched2 = true;
                        break;
                    }
                }

                if(!touched2){
                    this.endGame();
                }
            }
        }
        return true;
    }

    public interface IMainActivity{

    }
}

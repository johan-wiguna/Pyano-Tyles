package com.example.tb2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FragmentListener, MainPresenter.IMainActivity, BonusPresenter.IMainActivity {
    public MainPresenter presenter;
    public BonusPresenter bonusPresenter;
    public MediaPlayer sound;
    private FragmentManager fragmentManager;
    private Fragment fragmentMenu, fragmentDifficulty, fragmentGameplay, fragmentSettings, fragmentBonus;
    public UIThreadedWrapper objUIWrapper;
    private final String LANGUAGE_PREF = "language";
    private final String HIGH_SCORE_PREF = "highscore";
    private final String BG_MUSIC_PREF = "bg_music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        //DECLARE
        this.objUIWrapper = new UIThreadedWrapper(this, Looper.getMainLooper());

        //PRESENTER
        this.presenter = new MainPresenter(this, this);
        this.bonusPresenter = new BonusPresenter(this,this);

        //FRAGMENTS
        this.fragmentMenu = new MenuFragment(this.presenter);
        this.fragmentDifficulty = new DifficultyFragment(this.presenter);
        this.fragmentGameplay = new GameplayFragment(this.presenter);
        this.fragmentSettings = new SettingsFragment(this.presenter);
        this.fragmentBonus = new BonusFragment(this.bonusPresenter);

        this.fragmentManager = this.getSupportFragmentManager();

        //PLAY BACKGROUND MUSIC
        this.playBackgroundMusic();

        this.changePage(1);
    }

    public void resetGameplayScore(){
        this.presenter.currScore = 0;
        View v = this.fragmentGameplay.getView();
        TextView scoreTv = v.findViewById(R.id.tv_gameplay_score);
        scoreTv.setText("0");
    }

    public void resetBonusScore(){
        this.presenter.currScore = 0;
        View v = this.fragmentBonus.getView();
        TextView scoreTv = v.findViewById(R.id.tv_bonus_score);
        scoreTv.setText("0");
    }

    public void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE).edit();
        editor.putString("my_lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences(LANGUAGE_PREF, Activity.MODE_PRIVATE);
        String language = pref.getString("my_lang", "");
        setLocale(language);
    }

    public void playBackgroundMusic(){
        SharedPreferences sharedPreferences1 = getSharedPreferences(BG_MUSIC_PREF, MODE_PRIVATE);

        String bgMusic = sharedPreferences1.getString("my_music", "0");
        if(bgMusic.equals("0")){
            this.sound = MediaPlayer.create(this, R.raw.gymnopedie1);
            this.sound.setLooping(true);
            this.sound.start();

            this.presenter.setSong(0);
        }else if(bgMusic.equals("1")){
            this.sound = MediaPlayer.create(this, R.raw.sonata12);
            this.sound.setLooping(true);
            this.sound.start();

            this.presenter.setSong(1);
        }else if(bgMusic.equals("2")){
            this.sound = MediaPlayer.create(this, R.raw.arabesque1);
            this.sound.setLooping(true);
            this.sound.start();

            this.presenter.setSong(2);
        }
    }

    public void playG(){
        this.sound.stop();
        this.sound = MediaPlayer.create(this,R.raw.gymnopedie1);
        this.sound.setLooping(true);
        this.sound.start();

        this.presenter.setSong(0);
    }

    public void playS(){
        this.sound.stop();
        this.sound = MediaPlayer.create(this,R.raw.sonata12);
        this.sound.setLooping(true);
        this.sound.start();

        this.presenter.setSong(1);
    }

    public void playA(){
        this.sound.stop();
        this.sound = MediaPlayer.create(this,R.raw.arabesque1);
        this.sound.setLooping(true);
        this.sound.start();

        this.presenter.setSong(2);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.sound.pause();

        //HIGH SCORE SHARED PREFERENCES
        SharedPreferences sharedPreferences = getSharedPreferences(HIGH_SCORE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < this.presenter.highScore.length; i++){
            int temp = this.presenter.highScore[i];
            editor.putString(Integer.toString(i), Integer.toString(temp));
        }

        int bonusHighScore = this.bonusPresenter.highScore;
        editor.putString("bonus", Integer.toString(bonusHighScore));

        editor.commit();

        //BACKGROUND MUSIC SHARED PREFERENCES
        SharedPreferences sharedPreferences1 = getSharedPreferences(BG_MUSIC_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();

        editor1.putString("my_music", Integer.toString(this.presenter.song));
        editor1.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.sound.start();

        SharedPreferences sharedPreferences = getSharedPreferences(HIGH_SCORE_PREF, MODE_PRIVATE);
        for (int i = 0; i < this.presenter.highScore.length; i++){
            String data = sharedPreferences.getString(Integer.toString(i), "0");
            this.presenter.highScore[i] = Integer.parseInt(data);
        }

        String data = sharedPreferences.getString("bonus", "0");
        this.bonusPresenter.highScore = Integer.parseInt(data);
    }

    @Override
    public void changePage(int page) {
        FragmentTransaction ft = this.fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
        );

        if(page == 1) { //MENU
            if(this.fragmentMenu.isAdded()){
                ft.show(this.fragmentMenu);
            }else{
                ft.add(R.id.fragment_container, this.fragmentMenu).addToBackStack(null);
            }

            if(this.fragmentDifficulty.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentGameplay.isAdded()) ft.hide(this.fragmentGameplay);
            else if(this.fragmentSettings.isAdded()) ft.hide(this.fragmentSettings);
            else if(this.fragmentBonus.isAdded()) ft.hide(this.fragmentBonus);

        }else if(page == 2){ //DIFFICULTY
            if(this.fragmentDifficulty.isAdded()){
                ft.show(this.fragmentDifficulty);
            }else{
                ft.add(R.id.fragment_container, this.fragmentDifficulty).addToBackStack(null);
            }

            if(this.fragmentMenu.isAdded()) ft.hide(this.fragmentMenu);
            else if(this.fragmentGameplay.isAdded()) ft.hide(this.fragmentGameplay);
            else if(this.fragmentSettings.isAdded()) ft.hide(this.fragmentSettings);
            else if(this.fragmentBonus.isAdded()) ft.hide(this.fragmentBonus);

        }else if(page == 3){ //GAMEPLAY
            if(this.fragmentGameplay.isAdded()){
                ft.show(this.fragmentGameplay);
            }else{
                ft.add(R.id.fragment_container, this.fragmentGameplay).addToBackStack(null);
            }

            if(this.fragmentMenu.isAdded()) ft.hide(this.fragmentMenu);
            else if(this.fragmentDifficulty.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentSettings.isAdded()) ft.hide(this.fragmentSettings);
            else if(this.fragmentBonus.isAdded()) ft.hide(this.fragmentBonus);

        }else if(page == 4){ //SETTINGS
            if(this.fragmentSettings.isAdded()){
                ft.show(this.fragmentSettings);
            }else{
                ft.add(R.id.fragment_container, this.fragmentSettings).addToBackStack(null);
            }

            if(this.fragmentMenu.isAdded()) ft.hide(this.fragmentMenu);
            else if(this.fragmentDifficulty.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentGameplay.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentBonus.isAdded()) ft.hide(this.fragmentBonus);

        }else if(page == 5){ //BONUS
            if(this.fragmentBonus.isAdded()){
                ft.show(this.fragmentBonus);
            }else{
                ft.add(R.id.fragment_container,this.fragmentBonus).addToBackStack(null);
            }

            if(this.fragmentMenu.isAdded()) ft.hide(this.fragmentMenu);
            else if(this.fragmentDifficulty.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentGameplay.isAdded()) ft.hide(this.fragmentDifficulty);
            else if(this.fragmentSettings.isAdded()) ft.hide(this.fragmentSettings);
        }

        ft.commit();
    }

    @Override
    public void closeApplication() {
        this.moveTaskToBack(true);
        this.finish();
    }
}
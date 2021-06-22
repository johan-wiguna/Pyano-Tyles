package com.example.tb2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.time.Duration;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private MainPresenter presenter;
    private FragmentListener listener;
    private ImageView ivBack;
    private CardView cvReset, cvSound, cvBgMusic, cvLanguage;
    private TextView tvLanguage;
    private Switch switchSound;

    public SettingsFragment(MainPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        this.ivBack = view.findViewById(R.id.iv_settings_back);
        this.cvReset = view.findViewById(R.id.cv_set_reset);
        this.cvSound = view.findViewById(R.id.cv_set_sound);
        this.cvBgMusic = view.findViewById(R.id.cv_set_bg_music);
        this.cvLanguage = view.findViewById(R.id.cv_set_language);
        this.tvLanguage = view.findViewById(R.id.tv_change_language);
        this.switchSound = view.findViewById(R.id.switch_sound);

        this.ivBack.setOnClickListener(this);
        this.cvReset.setOnClickListener(this);
        this.cvSound.setOnClickListener(this);
        this.cvBgMusic.setOnClickListener(this);
        this.cvLanguage.setOnClickListener(this);
        this.switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                presenter.activity.sound.start();
            }else{
                presenter.activity.sound.pause();
            }
        });

        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof FragmentListener){
            this.listener = (FragmentListener) context;
        }else{
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onClick(View v) {
        if(v == this.cvReset){
            for (int i = 0; i < this.presenter.highScore.length; i++){
                this.presenter.highScore[i] = 0;
            }
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this.getContext(), "High score has been reset", Toast.LENGTH_SHORT).show();
        }else if(v == this.cvSound){
            if(this.switchSound.isChecked()){
                this.switchSound.setChecked(false);
                this.presenter.activity.sound.pause();
            }else{
                this.switchSound.setChecked(true);
                this.presenter.activity.sound.start();
            }
        }else if(v == this.cvBgMusic){
            final String[] bgMusicOptions = {"E. Satie - Gymnopédie No. 1", "W. A. Mozart - Sonata No. 12 (2nd mvt.)", "C. Debussy - Arabesque No. 1"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this.presenter.activity);
            mBuilder.setTitle(R.string.choose_bg_music);

            int checkedItem;

            if(this.presenter.getSong() == 0){
                checkedItem = 0;
            }else if(this.presenter.getSong() == 1){
                checkedItem = 1;
            }else{
                checkedItem = 2;
            }

            mBuilder.setSingleChoiceItems(bgMusicOptions, checkedItem, (dialog, which) -> {
                if(which == 0){
                    this.presenter.activity.playG();
                    Toast.makeText(getContext(), "Playing: Gymnopedie No. 1", Toast.LENGTH_SHORT).show();
                }
                if (which == 1){
                    this.presenter.activity.playS();
                    Toast.makeText(getContext(), "Playing: Sonata No. 12 (2nd mvt.)", Toast.LENGTH_SHORT).show();
                }
                if (which == 2){
                    this.presenter.activity.playA();
                    Toast.makeText(getContext(), "Playing: Arabesque No. 1", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }else if(v == this.cvLanguage){
            final String[] languageOptions = {"English", "Bahasa Indonesia"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this.presenter.activity);
            mBuilder.setTitle(R.string.choose_language);

            int checkedItem;
            if(this.tvLanguage.getText().toString().equalsIgnoreCase("Change language")){
                checkedItem = 0;
            }else{
                checkedItem = 1;
            }

            mBuilder.setSingleChoiceItems(languageOptions, checkedItem, (dialog, which) -> {
                if(which == 0){
                    presenter.activity.setLocale("en");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getContext(), "Language has been changed", Toast.LENGTH_SHORT).show();
                }
                if (which == 1){
                    presenter.activity.setLocale("in");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getContext(), "Bahasa telah diubah", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }else if(v == this.ivBack){
            this.listener.changePage(1);
        }
    }
}

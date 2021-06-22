package com.example.tb2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class GameplayFragment extends Fragment implements View.OnClickListener {
    private MainPresenter presenter;
    private FragmentListener listener;
    private TextView scoreTv;
    private ImageView pauseIv;
    public ImageView ivGame;
    private Button resumeBtn, restartBtn, menuBtn;
    private Dialog pauseDialog;

    public GameplayFragment(MainPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("GF", "created");
        View view = inflater.inflate(R.layout.fragment_gameplay,container,false);

        this.scoreTv = view.findViewById(R.id.tv_gameplay_score);
        this.pauseIv = view.findViewById(R.id.iv_pause);
        this.menuBtn = view.findViewById(R.id.btn_pause_menu);
        this.ivGame = view.findViewById(R.id.iv_game);

        this.presenter.setIvGame(this.ivGame);

        this.ivGame.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            this.presenter.startGame();
        });

        this.pauseDialog = new Dialog(getActivity());
        this.pauseDialog.setContentView(R.layout.dialog_pause);

        this.pauseIv.setOnClickListener(this);

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
        if(v == this.pauseIv){
            showPauseModal();
            this.presenter.pauseGame();
        }
    }

    public void showPauseModal(){
        this.resumeBtn = this.pauseDialog.findViewById(R.id.btn_pause_resume);
        this.resumeBtn.setOnClickListener(v -> {
            pauseDialog.dismiss();
            presenter.resumeGame();
        });

        this.restartBtn = this.pauseDialog.findViewById(R.id.btn_pause_restart);
        this.restartBtn.setOnClickListener(v -> {
            pauseDialog.dismiss();
            this.presenter.resetScoreTv();
            this.presenter.startGame();
        });

        this.menuBtn = this.pauseDialog.findViewById(R.id.btn_pause_menu);
        this.menuBtn.setOnClickListener(v -> {
            backToMenu();
        });

        pauseDialog.setCanceledOnTouchOutside(false);

        pauseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pauseDialog.show();
    }

    public void backToMenu(){
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
    }
}

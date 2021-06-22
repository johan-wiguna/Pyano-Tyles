package com.example.tb2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BonusFragment extends Fragment implements View.OnClickListener, SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor accelerometer;
//    private Sensor magnetometer;
    private float[] accelerometerReading, magnetometerReading;
    private BonusPresenter presenter;
    private FragmentListener listener;
    private TextView scoreTv;
    private ImageView pauseIv;
    public ImageView ivGame;
    private Button resumeBtn, restartBtn, menuBtn;
    private Dialog pauseDialog;

    public BonusFragment(BonusPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bonus_gameplay,container,false);

        this.scoreTv = view.findViewById(R.id.tv_bonus_score);
        this.pauseIv = view.findViewById(R.id.iv_pause_bonus);
        this.menuBtn = view.findViewById(R.id.btn_pause_menu);
        this.ivGame = view.findViewById(R.id.iv_game_bonus);

        this.presenter.setIvGame(this.ivGame);

        this.ivGame.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            this.presenter.startBonus();
        });

        this.pauseDialog = new Dialog(getActivity());
        this.pauseDialog.setContentView(R.layout.dialog_pause);

        this.pauseIv.setOnClickListener(this);

        mSensorManager = (SensorManager) this.presenter.activity.getSystemService(Context.SENSOR_SERVICE);

        this.accelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        this.accelerometerReading = new float[3];

        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setTitle(R.string.instruction);
        alert.setMessage(R.string.bonus_instruction);
        alert.setPositiveButton("Ok", null);
        alert.show();

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
    public void onResume() {
        super.onResume();
        if (this.accelerometer != null) {
            mSensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
//        if (this.magnetometer != null) {
//            mSensorManager.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_GAME);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
            scoreTv.setText("0");
            this.presenter.currScore = 0;
            this.presenter.startBonus();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                this.accelerometerReading = event.values.clone();
                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                this.magnetometerReading = event.values.clone();
//                break;
        }

//        final float[] rotationMatrix = new float[9];
//        mSensorManager.getRotationMatrix(rotationMatrix,null,this.accelerometerReading,this.magnetometerReading);
//
//        final float[] orientationAngles = new float[3];
//        mSensorManager.getOrientation(rotationMatrix,orientationAngles);

//        float azimuth = orientationAngles[0];
        float azimuth = accelerometerReading[0];

        this.presenter.setNilaix(azimuth);

        this.presenter.checkMove(scoreTv);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

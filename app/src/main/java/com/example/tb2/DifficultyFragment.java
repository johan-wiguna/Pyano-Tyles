package com.example.tb2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class DifficultyFragment extends Fragment implements View.OnClickListener {
    private MainPresenter presenter;
    private FragmentListener listener;
    private ImageView ivBack;
    private CardView cvEasy, cvMedium, cvHard;
    private TextView tvEasyHighScore, tvMediumHighScore, tvHardHighScore, tvBonus;

    public DifficultyFragment(MainPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_difficulty,container,false);

        this.ivBack = view.findViewById(R.id.iv_diff_back);
        this.cvEasy = view.findViewById(R.id.cv_easy);
        this.cvMedium = view.findViewById(R.id.cv_medium);
        this.cvHard = view.findViewById(R.id.cv_hard);
        this.tvEasyHighScore = view.findViewById(R.id.tv_easy_highscore);
        this.tvMediumHighScore = view.findViewById(R.id.tv_medium_highscore);
        this.tvHardHighScore = view.findViewById(R.id.tv_hard_highscore);
        this.tvBonus = view.findViewById(R.id.tv_bonus);

        //SET CURRENT HIGH SCORE FOR EACH DIFFICULTY
        this.tvEasyHighScore.setText(Integer.toString(this.presenter.highScore[0]));
        this.tvMediumHighScore.setText(Integer.toString(this.presenter.highScore[1]));
        this.tvHardHighScore.setText(Integer.toString(this.presenter.highScore[2]));

        this.ivBack.setOnClickListener(this);
        this.cvEasy.setOnClickListener(this);
        this.cvMedium.setOnClickListener(this);
        this.cvHard.setOnClickListener(this);
        this.tvBonus.setOnClickListener(this);

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
        if(v == this.ivBack){
            this.listener.changePage(1);
        }else if(v == this.cvEasy){
            this.presenter.setDifficulty(0);
            this.listener.changePage(3);
        }else if(v == this.cvMedium){
            this.presenter.setDifficulty(1);
            this.listener.changePage(3);
        }else if(v == this.cvHard){
            this.presenter.setDifficulty(2);
            this.listener.changePage(3);
        }else{
            this.listener.changePage(5);
        }
    }
}

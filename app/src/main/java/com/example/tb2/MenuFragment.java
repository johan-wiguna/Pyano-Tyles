package com.example.tb2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private MainPresenter presenter;
    private FragmentListener listener;
    private Button btnPlay, btnExit;
    private ImageView ivSettings;

    public MenuFragment(){

    }

    public MenuFragment(MainPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,container,false);

        this.btnPlay = view.findViewById(R.id.btn_play);
        this.btnExit = view.findViewById(R.id.btn_exit);
        this.ivSettings = view.findViewById(R.id.iv_settings);

        this.btnPlay.setOnClickListener(this);
        this.btnExit.setOnClickListener(this);
        this.ivSettings.setOnClickListener(this);

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
        if(v == this.btnPlay){
            this.listener.changePage(2);
        }else if(v == this.btnExit){
            this.getActivity().finishAffinity();
            this.listener.closeApplication();
        }else if(v == this.ivSettings){
            this.listener.changePage(4);
        }
    }

}

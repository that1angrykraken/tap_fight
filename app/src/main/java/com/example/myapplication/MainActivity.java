package com.example.myapplication;

import android.app.Dialog;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.DialogInstructionBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    static int scrWidth = 0, scrHeight = 0, tapCounter1 = 0, tapCounter2 = 0;
    RelativeLayout.LayoutParams lp;
    ActivityMainBinding binding;
    static boolean gameStart = false, won = false, preparingPhase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrWidth = dm.widthPixels;
        scrHeight = dm.heightPixels;
        lp = (RelativeLayout.LayoutParams) binding.layoutBlock2.getLayoutParams();
        lp.height = scrHeight/2;
        binding.layoutBlock2.setLayoutParams(lp);

        binding.relativeLayout1.setOnClickListener(this);
        binding.layoutBlock2.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        DialogInstructionBinding db = DialogInstructionBinding.inflate(LayoutInflater.from(this), null, false);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(db.getRoot());
        db.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    void startPreparingPhase(){
        binding.txtBanner.setText("Tap to Start");
        lp.height = scrHeight/2;
        binding.layoutBlock2.setLayoutParams(lp);
        tapCounter1 = tapCounter2 = 0;
        won = false;
        gameStart = false;
        preparingPhase = false;
    }

    void showWinner(){
        binding.txtBanner.setVisibility(View.VISIBLE);
        String winner = tapCounter1 > tapCounter2 ? "P1 WIN" : "P2 WIN";
        binding.txtBanner.setText(winner + "\n" + "Tap to continue..");
        preparingPhase = true;
    }

    @Override
    public void onClick(View view) {
        int increment = 25;
//        binding.layoutBlock2.bringToFront();
        if(preparingPhase){
            startPreparingPhase();
            return;
        }

        if(!gameStart){
            gameStart = true;
            binding.txtBanner.setVisibility(View.GONE);
            onClick(view);
            return;
        }

        int currentHeight = binding.layoutBlock2.getHeight();
        if(view.getTag().equals("P1")){
            currentHeight = Math.max(currentHeight - increment, 0);
            tapCounter1++;
            if(currentHeight == 0) won = true;
        }
        else{
            currentHeight = Math.min(currentHeight + increment, scrHeight);
            tapCounter2++;
            if(currentHeight == scrHeight) won = true;
        }
        lp.height = currentHeight;
        binding.layoutBlock2.setLayoutParams(lp);

        if(won){
            showWinner();
        }
    }
}
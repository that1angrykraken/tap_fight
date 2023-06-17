package seaMonster.kraken.chocolate1;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import seaMonster.kraken.chocolate1.databinding.ActivityMainBinding;
import seaMonster.kraken.chocolate1.databinding.DialogInstructionBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    RelativeLayout.LayoutParams lp;
    ActivityMainBinding binding;
    SharedPreferences sp;

    static boolean gameStart = false, won = false, preparingPhase = false;
    static int scrWidth = 0, scrHeight = 0, tapCounter1 = 0, tapCounter2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // initialize block height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrWidth = dm.widthPixels;
        scrHeight = dm.heightPixels;
        lp = (RelativeLayout.LayoutParams) binding.layoutBlock2.getLayoutParams();
        lp.height = scrHeight/2;
        binding.layoutBlock2.setLayoutParams(lp);
        // set listener
        binding.relativeLayout1.setOnClickListener(this);
        binding.layoutBlock2.setOnClickListener(this);
        // get shared preferences and show the dialog
        sp = getPreferences(Context.MODE_PRIVATE);
        showInstruction();
    }

    void showInstruction(){
        boolean dontShowDialog = sp.getBoolean("DONT_SHOW_INSTRUCTION_DIALOG", false);
        if(dontShowDialog) return;
        DialogInstructionBinding db = DialogInstructionBinding.inflate(LayoutInflater.from(this), null, false);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(db.getRoot());
        db.btnClose.setOnClickListener(v -> dialog.dismiss());
        db.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("DONT_SHOW_INSTRUCTION_DIALOG", b);
            editor.apply();
        });
        dialog.show();
    }

    void startPreparingPhase(){
        binding.txtBanner.setText(getString(R.string.tap_to_start));
        lp.height = scrHeight/2;
        binding.layoutBlock2.setLayoutParams(lp);
        tapCounter1 = tapCounter2 = 0;
        won = false;
        gameStart = false;
        preparingPhase = false;
    }

    void showWinner(){
        binding.txtBanner.setVisibility(View.VISIBLE);
        String winner = tapCounter1 > tapCounter2 ? "P1" : "P2";
        binding.txtBanner.setText(winner + getString(R.string.winner_counter) + Math.max(tapCounter1, tapCounter2));
    }

    void increase(Object obj){
        int increment = 25;
        int currentHeight = binding.layoutBlock2.getHeight();
        if(obj.equals("P1")){
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
    }

    @Override
    public void onClick(View view) {
        int interval = 3000;

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

        increase(view.getTag());

        if(won){
            showWinner();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                preparingPhase = true;
                binding.txtBanner.setText(R.string.tap_to_continue);
            }, interval);
        }
    }
}
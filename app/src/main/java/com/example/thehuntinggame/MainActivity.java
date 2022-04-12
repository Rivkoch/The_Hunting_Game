package com.example.thehuntinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private final int DELAY = 1000;

    private final int ROWS = 5;
    private final int COLS = 3;
    private final int NUM_OF_LIVES = 3;

    private final int BONUS = 10;
    private final int FINE = 2;
    private Timer timer;
    private Game game;

    private ImageView[][] hunterMatrix;
    private ImageView[][] victimMatrix;
    private MaterialButton[] arrows;
    private ImageView[] livesView;

    private long[] vibratePattern = {100, 300, 100, 300, 100};
    private int[] vibrateStrength = {1, 10, 100, 10, 1};

    private TextView game_LBL_score;

    private int seconds, saveLastScore;
    private int startRow = 0, startCol =1;
    private int hunterRow, hunterCol, victimRow, victimCol;
    private int direction = -1;
    private int cuurentLives;

    private Toast toast;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initGame();
        logic();
    }

    private void initGame() {
        cuurentLives = NUM_OF_LIVES;
        seconds =0;
        game_LBL_score.setText(String.valueOf(seconds));
        placeAtStart();

    }

    private void placeAtStart() {
        hunterRow = startRow;
        victimRow = startRow + 4;
        hunterCol = victimCol = startCol;
        hunterMatrix[startRow][startCol].setVisibility(View.VISIBLE);
        victimMatrix[startRow + 4][startCol].setVisibility(View.VISIBLE);
    }

    private void logic(){
        buttonPressed();
        ifHunted();
        tick();
        moveCat();
    }

    private void buttonPressed() {
        for (int i = 0; i < arrows.length; i++){
            int pressed = i;
            arrows[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    directItem(pressed);
                }
            });
        }
    }

    private void ifHunted() {
        if(hunterRow == victimRow && hunterCol == victimCol){
            direction = -1;
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            victimMatrix[victimRow][victimCol].setVisibility(View.GONE);

            vibrateMultipleTimes();
            setCurrentLives();
            placeAtStart();
            if(cuurentLives > 0) {
                Toast.makeText(this, "Oops, the hunter catch you", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void vibrateMultipleTimes() {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibratePattern, vibrateStrength, -1);
            vib.vibrate(vibrationEffect);
        } else {
            vib.vibrate(vibratePattern, -1);
        }
    }

    private void setCurrentLives() {
        cuurentLives--;
        livesView[cuurentLives].setVisibility(View.INVISIBLE);
        if(cuurentLives == 0){
            saveLastScore = seconds;
            gameOver();
        }
    }

    private void gameOver() {
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(MainActivity.this,"No more attempts.\nYOU GOT CAUGHT!!!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void directItem(int move) {
        direction = move;
        switch (move) {
            case 0:
                mouseUp();
                ifHunted();
                break;

            case 1:
                mouseDown();
                ifHunted();
                break;
            case 2:
                mouseRight();
                ifHunted();
                break;
            case 3:
                mouseLeft();
                ifHunted();
                break;
        }
    }

    private void mouseLeft() {
        if(victimCol > 0) {
            victimMatrix[victimRow][victimCol].setVisibility(View.GONE);
            victimMatrix[victimRow][--victimCol].setVisibility(View.VISIBLE);
        }
    }

    private void mouseRight() {
        if(victimCol < COLS - 1){
            victimMatrix[victimRow][victimCol].setVisibility(View.GONE);
            victimMatrix[victimRow][++victimCol].setVisibility(View.VISIBLE);
        }
    }

    private void mouseDown() {
        if (victimRow < ROWS - 1) {
            victimMatrix[victimRow][victimCol].setVisibility(View.GONE);
            victimMatrix[++victimRow][victimCol].setVisibility(View.VISIBLE);
        }
    }

    private void mouseUp() {
        if (victimRow > 0) {
            victimMatrix[victimRow][victimCol].setVisibility(View.GONE);
            victimMatrix[--victimRow][victimCol].setVisibility(View.VISIBLE);
        }
    }

    private void moveCat() {
        int rand = (int)(Math.random() * ((arrows.length-1) - 0 +1)) +1;
        switch (rand) {
            case 0:
                catUp();
                ifHunted();
                break;
            case 1:
                catDown();
                ifHunted();
                break;
            case 2:
                catRight();
                ifHunted();
                break;
            case 3:
                catLeft();
                ifHunted();
                break;
        }
    }

    private void catLeft() {
        if(hunterCol > 0){
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            hunterMatrix[hunterRow][--hunterCol].setVisibility(View.VISIBLE);
        }
    }

    private void catRight() {
        if(hunterCol < COLS-1){
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            hunterMatrix[hunterRow][++hunterCol].setVisibility(View.VISIBLE);
        }
    }

    private void catDown() {
        if (hunterRow < ROWS - 1) {
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            hunterMatrix[++hunterRow][hunterCol].setVisibility(View.VISIBLE);
        }

        if(hunterRow == ROWS){
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            hunterMatrix[0][hunterCol].setVisibility(View.VISIBLE);
            hunterRow = 0;
        }
    }

    private void catUp() {
        if (hunterRow > 0) {
            hunterMatrix[hunterRow][hunterCol].setVisibility(View.GONE);
            hunterMatrix[--hunterRow][hunterCol].setVisibility(View.VISIBLE);
        }
    }

    private void findViews() {
        game_LBL_score = findViewById(R.id.game_LBL_score);

        arrows = new MaterialButton[]{
                findViewById(R.id.game_BTN_moveUp),
                findViewById(R.id.game_BTN_moveDown),
                findViewById(R.id.game_BTN_moveRight),
                findViewById(R.id.game_BTN_moveLeft)
        };
        livesView = new ImageView[]{
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3),
        };
        hunterMatrix = new ImageView[][]{
                {findViewById(R.id.game_IMG_hunter00), findViewById(R.id.game_IMG_hunter01), findViewById(R.id.game_IMG_hunter02)},
                {findViewById(R.id.game_IMG_hunter10), findViewById(R.id.game_IMG_hunter11), findViewById(R.id.game_IMG_hunter12)},
                {findViewById(R.id.game_IMG_hunter20), findViewById(R.id.game_IMG_hunter21), findViewById(R.id.game_IMG_hunter22)},
                {findViewById(R.id.game_IMG_hunter30), findViewById(R.id.game_IMG_hunter31), findViewById(R.id.game_IMG_hunter32)},
                {findViewById(R.id.game_IMG_hunter40), findViewById(R.id.game_IMG_hunter41), findViewById(R.id.game_IMG_hunter42)}
        };
        victimMatrix = new ImageView[][]{
                {findViewById(R.id.game_IMG_victim00), findViewById(R.id.game_IMG_victim01), findViewById(R.id.game_IMG_victim02)},
                {findViewById(R.id.game_IMG_victim10), findViewById(R.id.game_IMG_victim11), findViewById(R.id.game_IMG_victim12)},
                {findViewById(R.id.game_IMG_victim20), findViewById(R.id.game_IMG_victim21), findViewById(R.id.game_IMG_victim22)},
                {findViewById(R.id.game_IMG_victim30), findViewById(R.id.game_IMG_victim31), findViewById(R.id.game_IMG_victim32)},
                {findViewById(R.id.game_IMG_victim40), findViewById(R.id.game_IMG_victim41), findViewById(R.id.game_IMG_victim42)}
        };
    }
    
    private void tick() {
        handler = new Handler();
        handler.postDelayed(() -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ++seconds;
                    game_LBL_score.setText("" + seconds);

                    if (direction != -1) {
                        directItem(direction);
                    }
                    logic();
                }
            });

        }, DELAY);
    }


}


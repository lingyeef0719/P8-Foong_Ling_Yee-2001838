package my.edu.utar.individualpracassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Level2Activity extends AppCompatActivity {

        private TextView scoreView, timerView;
        private List<TextView> textViewList;
        private int score = 0, level = 1, numOfViews = 9;
        private CountDownTimer countDownTimer;
        private TextView highlightedView;
        private boolean complete_level = false;
        private Button exitButton, nextLevelButton, startButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_level2);
            scoreView = findViewById(R.id.scoreTextView);
            timerView = findViewById(R.id.timerTextView);
            exitButton = findViewById(R.id.exitButton);
            nextLevelButton = findViewById(R.id.nextLevelButton);
            startButton = findViewById(R.id.startButton);

            textViewList = new ArrayList<>();
            textViewList.add(findViewById(R.id.view1));
            textViewList.add(findViewById(R.id.view2));
            textViewList.add(findViewById(R.id.view3));
            textViewList.add(findViewById(R.id.view4));
            textViewList.add(findViewById(R.id.view5));
            textViewList.add(findViewById(R.id.view6));
            textViewList.add(findViewById(R.id.view7));
            textViewList.add(findViewById(R.id.view8));
            textViewList.add(findViewById(R.id.view9));

            score = getIntent().getIntExtra("score", 0);

            TextView scoreView = findViewById(R.id.scoreTextView);
            scoreView.setText("Score: " + score);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            for (int i = 0; i < numOfViews; i++){
                textViewList.get(i).setHeight(width / 3);
            }

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGame();
                }
            });

        }

        private void startGame() {
            scoreView.setText("Score: " + score);
            timerView.setText("Time Left: " +  5 + "s");
            exitButton.setClickable(false);
            nextLevelButton.setClickable(false);

            countDownTimer = new CountDownTimer( 5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    timerView.setText("Time Left: " + millisUntilFinished / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    cancel();

                    if (highlightedView != null) {
                        highlightedView.setBackgroundColor(Color.TRANSPARENT);
                        highlightedView = null;
                    }

                    for (TextView textView : textViewList) {
                        textView.setClickable(false);
                        textView.setOnClickListener(null);
                    }

                    exitButton.setClickable(true);
                    nextLevelButton.setClickable(true);

                    nextLevelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Level2Activity.this, Level3Activity.class);
                            intent.putExtra("score", score);
                            startActivity(intent);
                        }
                    });

                    exitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Level2Activity.this, ScoreboardActivity.class);
                            intent.putExtra("score", score);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }.start();

            for (TextView textView : textViewList) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == highlightedView) {
                            score++;
                            scoreView.setText("Score: " + score);

                            v.setBackgroundColor(Color.CYAN);
                            highlightedView = null;
                            textViewList.remove(v);

                            if (textViewList.isEmpty()) {
                                complete_level = true;
                                level++;
                            } else {
                                highlightNextView();
                            }
                        } else {
                            Toast.makeText(Level2Activity.this, "Wrong choice!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            highlightNextView();
        }

        private void highlightNextView() {
            if (textViewList.isEmpty()) {
                textViewList = new ArrayList<>();
                textViewList.add(findViewById(R.id.view1));
                textViewList.add(findViewById(R.id.view2));
                textViewList.add(findViewById(R.id.view3));
                textViewList.add(findViewById(R.id.view4));
                textViewList.add(findViewById(R.id.view5));
                textViewList.add(findViewById(R.id.view6));
                textViewList.add(findViewById(R.id.view7));
                textViewList.add(findViewById(R.id.view8));
                textViewList.add(findViewById(R.id.view9));
            }

            int randomIndex = (int) (Math.random() * textViewList.size());
            highlightedView = textViewList.get(randomIndex);
            highlightedView.setBackgroundColor(Color.YELLOW);

            textViewList.remove(randomIndex);
        }
    }


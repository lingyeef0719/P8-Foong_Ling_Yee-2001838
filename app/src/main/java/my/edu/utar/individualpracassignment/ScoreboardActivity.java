package my.edu.utar.individualpracassignment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreboardActivity extends AppCompatActivity {

    private ListView mListView;
    private SharedPreferences mPrefs;
    private static final String TAG = "ScoreboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        mListView = findViewById(R.id.list_view);

        mPrefs = getSharedPreferences("my_prefs", MODE_PRIVATE);

        // check the score can enter the top 25 scores or not
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);

        if (isTopScore(score)) {
            showNameInputDialog(score);
        }

        showScoreboard();
    }

    private boolean isTopScore(int score) {
        // obtain the previous top 25 rank
        Set<String> scores = mPrefs.getStringSet("scores", new HashSet<String>());

        if (scores.size() < 25 || score > getMinScore(scores)) {
            return true;
        }

        return false;
    }

    private int getMinScore(Set<String> scores) {
        List<Integer> topScores = new ArrayList<>();
        for (String scoreString : scores) {
            int scoreValue = Integer.parseInt(scoreString.split(",")[1]);
            topScores.add(scoreValue);
        }

        Collections.sort(topScores);
        int minScore = topScores.get(0);

        return minScore;
    }

    private void showNameInputDialog(final int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulation!!You are eligible to enter the top 25 score rank");
        builder.setMessage("Please enter your name: ");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                // record the name and score into the rank
                addScore(name, score);
                showScoreboard();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addScore(String name, int score) {
        ScoreDbHelper dbHelper = new ScoreDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoreDbHelper.COLUMN_NAME, name);
        values.put(ScoreDbHelper.COLUMN_SCORE, score);

        long newRowId = db.insert(ScoreDbHelper.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(TAG, "Failed to insert row into " + ScoreDbHelper.TABLE_NAME);
        } else {
            Log.d(TAG, "New row inserted into " + ScoreDbHelper.TABLE_NAME + " with ID " + newRowId);
        }
    }

    private void showScoreboard() {
        ScoreDbHelper dbHelper = new ScoreDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                ScoreDbHelper.COLUMN_ID,
                ScoreDbHelper.COLUMN_NAME,
                ScoreDbHelper.COLUMN_SCORE
        };
        Cursor cursor = db.query(
                ScoreDbHelper.TABLE_NAME,projection, null, null, null, null, ScoreDbHelper.COLUMN_SCORE + " DESC");
        List<String> sortedScores = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COLUMN_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COLUMN_SCORE));
            String scoreString = name + "," + score;
            sortedScores.add(scoreString);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortedScores);
        mListView.setAdapter(adapter);
    }
}
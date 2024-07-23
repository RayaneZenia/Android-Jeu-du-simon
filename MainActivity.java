package com.example.simonprojet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView top;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // Initialisation du TextView pour afficher les meilleurs scores
        top = findViewById(R.id.tvBestScores);

        // Initialisation de SharedPreferences
        sharedPreferences = getSharedPreferences("scores", Context.MODE_PRIVATE);
        boolean scoresInitialized = sharedPreferences.getBoolean("scores_initialized", false);
        if (!scoresInitialized) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("top_score1", 0);
            editor.putInt("top_score2", 0);
            editor.putInt("top_score3", 0);

            editor.putBoolean("scores_initialized", true);
            editor.apply();
        }
        mettreAJourScores();

    }

    public void Quitter(View v) {
        finish();
    }

    public void VersA2(View v) {
        Intent intent = new Intent(this, GameActivity.class);// Passer le nom saisi à l'activité Simon
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        // Récupérer les meilleurs scores à partir de SharedPreferences
                        int bestScore1 = sharedPreferences.getInt("top_score1", 0);
                        int bestScore2 = sharedPreferences.getInt("top_score2", 0);
                        int bestScore3 = sharedPreferences.getInt("top_score3", 0);

                        Intent intent = result.getData();
                        if (intent != null) {
                            Bundle data = intent.getExtras();
                            if (data != null) {
                                int score = data.getInt("score");
                                String userName = data.getString("user_name");


                                afficherMeilleursScores(score, bestScore1, bestScore2, bestScore3, userName);

                                // Afficher les scores mis à jour
                                mettreAJourScores();
                            }
                        }
                    }
                }
            });


    private void mettreAJourScores() {

        int topScore1 = sharedPreferences.getInt("top_score1", 0);
        int topScore2 = sharedPreferences.getInt("top_score2", 0);
        int topScore3 = sharedPreferences.getInt("top_score3", 0);


        String nameTopScore1 = sharedPreferences.getString("name_score1", "");
        String nameTopScore2 = sharedPreferences.getString("name_score2", "");
        String nameTopScore3 = sharedPreferences.getString("name_score3", "");

        // Afficher les meilleurs scores dans le TextView avec les noms des joueurs
        String topScoresText = "Meilleurs scores :\n" +
                "1. " + nameTopScore1 + " : " + topScore1 + "\n" +
                "2. " + nameTopScore2 + " : " + topScore2 + "\n" +
                "3. " + nameTopScore3 + " : " + topScore3;
        top.setText(topScoresText);
    }


    private void afficherMeilleursScores(int score, int bestScore1, int bestScore2, int bestScore3, String userName) {
        if (score > bestScore1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("top_score3", bestScore2);
            editor.putInt("top_score2", bestScore1);
            editor.putInt("top_score1", score);
            editor.putString("name_score3", sharedPreferences.getString("name_score2", ""));
            editor.putString("name_score2", sharedPreferences.getString("name_score1", ""));
            editor.putString("name_score1", userName);
            editor.apply();
        } else if (score > bestScore2) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("top_score3", bestScore2);
            editor.putInt("top_score2", score);
            editor.putString("name_score3", sharedPreferences.getString("name_score2", ""));
            editor.putString("name_score2", userName);
            editor.apply();
        } else if (score > bestScore3) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("top_score3", score);
            editor.putString("name_score3", userName);
            editor.apply();
        }
    }


}
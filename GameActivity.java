package com.example.simonprojet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer sound1;
    private MediaPlayer sound2;
    private MediaPlayer sound3;
    private MediaPlayer sound4;
    private MediaPlayer soundPerdu;
    private ImageView imageViewColor1;
    private ImageView imageViewColor2;
    private ImageView imageViewColor3;
    private ImageView imageViewColor4;
    private TextView textViewScore;
    private SharedPreferences sharedPreferences;

    private List<Integer> sequence;
    private int indiceJoueur;
    private int score;

    private final Random random = new Random();
    private final Handler handler = new Handler();
    private final long delaiEntreBoutons = 1000; // Temps en millisecondes entre chaque bouton de la séquence
    private final long delaiEntreSequences = 1000; // Temps en millisecondes entre chaque séquence de jeu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = getSharedPreferences("scores", MODE_PRIVATE);

        imageViewColor1 = findViewById(R.id.ivColor1);
        imageViewColor2 = findViewById(R.id.ivColor2);
        imageViewColor3 = findViewById(R.id.ivColor3);
        imageViewColor4 = findViewById(R.id.ivColor4);
        textViewScore = findViewById(R.id.tvScore2);

        imageViewColor1.setOnClickListener(this);
        imageViewColor2.setOnClickListener(this);
        imageViewColor3.setOnClickListener(this);
        imageViewColor4.setOnClickListener(this);

        sound1 = MediaPlayer.create(this, R.raw.son1);
        sound2 = MediaPlayer.create(this, R.raw.son2);
        sound3 = MediaPlayer.create(this, R.raw.son3);
        sound4 = MediaPlayer.create(this, R.raw.son4);
        soundPerdu = MediaPlayer.create(this, R.raw.error);

        // Chargez l'animation d'allumage des boutons
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_light_up);

        // Appliquez l'animation à chaque bouton
        imageViewColor1.startAnimation(buttonAnimation);
        imageViewColor2.startAnimation(buttonAnimation);
        imageViewColor3.startAnimation(buttonAnimation);
        imageViewColor4.startAnimation(buttonAnimation);

        score = 0;


        jouer();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivColor1) {
            gererClickCouleurs(1);
        } else if (v.getId() == R.id.ivColor2) {
            gererClickCouleurs(2);
        } else if (v.getId() == R.id.ivColor3) {
            gererClickCouleurs(3);
        } else if (v.getId() == R.id.ivColor4) {
            gererClickCouleurs(4);
        }
    }

    private void jouer() {
        sequence = new ArrayList<>();
        indiceJoueur = 0;
        ajouterAsequence();
        jouerSequence();
    }

    private void ajouterAsequence() {
        int nextButton = random.nextInt(4) + 1;
        sequence.add(nextButton);
    }

    private void jouerSequence() {
        indiceJoueur = 0;
        modifieScore();

        final int lastIndex = sequence.size() - 1; // Déclaration de la variable finale pour le dernier index

        // Affichage de chaque bouton de la séquence avec un délai entre chacun
        for (int i = 0; i < sequence.size(); i++) {
            final int buttonIndex = sequence.get(i);
            final int currentIndex = i;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    desactiverInputJoueur();
                    jouerSonBouton(buttonIndex);
                    surlignerBouton(buttonIndex);

                    // Si c'est le dernier bouton de la séquence, on attend un délai avant de permettre au joueur de jouer
                    if (currentIndex == lastIndex) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Permettre au joueur de jouer après le délai
                                activerInputJoueur();
                            }
                        }, delaiEntreSequences);
                    }
                }
            }, i * delaiEntreBoutons);
        }
    }

    private void activerInputJoueur() {
        // Activer l'interaction avec les boutons pour permettre au joueur de jouer
        imageViewColor1.setEnabled(true);
        imageViewColor2.setEnabled(true);
        imageViewColor3.setEnabled(true);
        imageViewColor4.setEnabled(true);
    }

    private void desactiverInputJoueur() {
        // Désactiver l'interaction avec les boutons pendant la lecture de la séquence
        imageViewColor1.setEnabled(false);
        imageViewColor2.setEnabled(false);
        imageViewColor3.setEnabled(false);
        imageViewColor4.setEnabled(false);
    }

    private void surlignerBouton(int buttonIndex) {
        ImageView button = getButtonByIndex(buttonIndex);
        if (button != null) {
            Animation highlightAnimation = AnimationUtils.loadAnimation(this, R.anim.highlight_animation);
            button.startAnimation(highlightAnimation);
        }
    }

    private void jouerSonBouton(int buttonIndex) {
        switch (buttonIndex) {
            case 1:
                sound1.start();
                break;
            case 2:
                sound2.start();
                break;
            case 3:
                sound3.start();
                break;
            case 4:
                sound4.start();
                break;
            default:
                break;
        }
    }

    private void gererClickCouleurs(int colorIndex) {

        // Jouez le son correspondant au bouton cliqué
        jouerSonBouton(colorIndex);


        if (colorIndex == sequence.get(indiceJoueur)) {
            indiceJoueur++;
            surlignerBouton(colorIndex);
            if (indiceJoueur == sequence.size()) {
                // Si le joueur a cliqué sur tous les boutons dans la séquence, augmenter le score, mettre à jour l'affichage du score et ajouter une nouvelle séquence
                score++;
                modifieScore();
                ajouterAsequence();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jouerSequence(); // Démarrer la nouvelle séquence après un délai
                    }
                }, delaiEntreSequences); // Délai avant de démarrer la nouvelle séquence
            }
        } else {
            soundPerdu.start();
            finJeu();
        }
    }

    private void modifieScore() {
        textViewScore.setText("Score: " + score);
    }

    private void finJeu() {
        afficherBoiteDialogueScore();
    }

    private void reinistialiserJeu() {
        score = 0;
        modifieScore();
    }

    private ImageView getButtonByIndex(int index) {
        switch (index) {
            case 1:
                return imageViewColor1;
            case 2:
                return imageViewColor2;
            case 3:
                return imageViewColor3;
            case 4:
                return imageViewColor4;
            default:
                return null;
        }
    }

    private void afficherBoiteDialogueScore() {
        int bestScore1 = sharedPreferences.getInt("top_score1", 0);
        int bestScore2 = sharedPreferences.getInt("top_score2", 0);
        int bestScore3 = sharedPreferences.getInt("top_score3", 0);

        if (score > bestScore1 || score > bestScore2 || score > bestScore3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Felecitation!");
            builder.setMessage("Felecitation!T as battu l'un des meilleurs scores.");

            final EditText input = new EditText(this);
            input.setHint("Enter your name: ");
            builder.setView(input);
            builder.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Récupérer le nom de l'utilisateur
                    String userName = input.getText().toString();

                    // Terminer l'activité en renvoyant le score et le nom à l'activité principale
                    Intent intent = new Intent();
                    intent.putExtra("score", score);
                    intent.putExtra("user_name", userName);

                    // Ajouter les trois meilleurs scores à l'intent
                    intent.putExtra("best_score1", bestScore1);
                    intent.putExtra("best_score2", bestScore2);
                    intent.putExtra("best_score3", bestScore3);

                    setResult(RESULT_OK, intent);
                    reinistialiserJeu();
                    jouer();
                }
            });
            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Récupérer le nom de l'utilisateur
                    String userName = input.getText().toString();

                    // Terminer l'activité en renvoyant le score et le nom à l'activité principale
                    Intent intent = new Intent();
                    intent.putExtra("score", score);
                    intent.putExtra("user_name", userName);

                    // Ajouter les trois meilleurs scores à l'intent
                    intent.putExtra("best_score1", bestScore1);
                    intent.putExtra("best_score2", bestScore2);
                    intent.putExtra("best_score3", bestScore3);

                    setResult(RESULT_OK, intent);
                    reinistialiserJeu();
                    finish();
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oupss!");
            builder.setMessage("Tu n'as battu aucun score malheureusement :(.");
            builder.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reinistialiserJeu();
                    jouer();
                }
            });
            builder.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reinistialiserJeu();
                    finish();
                }
            });

            // Créer et afficher la boîte de dialogue
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

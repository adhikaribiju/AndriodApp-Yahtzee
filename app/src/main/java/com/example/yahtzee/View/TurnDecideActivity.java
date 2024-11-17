package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.R;

import java.util.Random;

public class TurnDecideActivity extends AppCompatActivity {
    private ImageView computerDice;
    private ImageView humanDice;
    private TextView winnerTextView;
    private Button rollDiceButton;
    private Button startTournamentButton;
    private Random random;

    // Array of drawable resource IDs for the dice images
    private final int[] diceImages = {
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide_turn);

        // Initialize the views
        computerDice = findViewById(R.id.computerDice);
        humanDice = findViewById(R.id.humanDice);
        winnerTextView = findViewById(R.id.winnerTextView);
        rollDiceButton = findViewById(R.id.rollDiceButton);
        startTournamentButton = findViewById(R.id.startTournamentButton);

        // Initialize the Random object
        random = new Random();

        // Set the button click listener
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollDices();
            }
        });

        // Set the Start Tournament button click listener
        startTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayRoundActivity();
            }
        });
    }

    private void rollDices() {
        // Generate random rolls for both computer and human (1 to 6)
        int computerRoll = random.nextInt(6) + 1; // Random number from 1 to 6
        int humanRoll = random.nextInt(6) + 1;    // Random number from 1 to 6

        // Update computer's dice ImageView based on the roll
        computerDice.setImageResource(diceImages[computerRoll - 1]);

        // Update human's dice ImageView based on the roll
        humanDice.setImageResource(diceImages[humanRoll - 1]);

        // Determine the winner or if it's a draw
        if (humanRoll > computerRoll) {
            // Human wins
            displayWinner("Human");
        } else if (computerRoll > humanRoll) {
            // Computer wins
            displayWinner("Computer");
        } else {
            // It's a draw, ask to reroll
            Toast.makeText(this, "It's a draw! Please roll again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayWinner(String winner) {
        // Show the winner in the TextView
        winnerTextView.setText(winner + " won the toss!");

        // Hide the "Roll Dice" button
        rollDiceButton.setVisibility(View.GONE);

        // Make the "Start the Tournament" button visible
        startTournamentButton.setVisibility(View.VISIBLE);

        // Save the winner to pass to the next activity
        startTournamentButton.setTag(winner); // Store the winner name in the button tag
    }

    private void startPlayRoundActivity() {
        // Retrieve the winner name from the button tag
        String winner = (String) startTournamentButton.getTag();

        // Start the PlayRoundActivity with the winner's name
        Intent intent = new Intent(TurnDecideActivity.this, PlayRoundActivity.class);
        intent.putExtra("winner_name", "Human");
        startActivity(intent);
    }
}

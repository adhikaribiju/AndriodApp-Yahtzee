package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.R;

import java.util.Random;

public class TurnDecideActivity extends AppCompatActivity {
    private ImageView computerDice;
    private ImageView humanDice;
    private TextView winnerTextView;
    private Button rollDiceButton;
    private Button startTournamentButton;
    private Button manSetButton;
    private Random random;

    private TextView drawText;

    // Array of drawable resource IDs for the dice images
    private final int[] diceImages = {
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    /**
     * Will run after the TurnDecideActivity is called and will initialize the activity, including setting the content view, creating the buttons,
     * and setting the OnClickListener for the buttons.
     * @param savedInstanceState The saved instance state of the activity.
     */
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
        manSetButton = findViewById(R.id.manSetButton);
        drawText = findViewById(R.id.drawText);

        drawText.setVisibility(View.INVISIBLE);

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



        // Set the Start Tournament button click listener
        manSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualRoll();
            }
        });
    }

    /**
     * Rolls the dices for both computer and human, and determines the winner.
     */
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
            drawText.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "It's a draw! Please roll again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Allows the user to manually set the dice values for both computer and human, and determines the winner.
     */
    private void manualRoll() {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the Dice Values");

        // Create a LinearLayout to hold all UI elements
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Arrays to store selected values
        final int[] humanScore = {0};
        final int[] computerScore = {0};

        // Create UI for Human
        TextView humanLabel = new TextView(this);
        humanLabel.setText("Human:");
        layout.addView(humanLabel);

        RadioGroup humanGroup = new RadioGroup(this);
        humanGroup.setOrientation(RadioGroup.HORIZONTAL);
        for (int i = 1; i <= 6; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(String.valueOf(i));
            radioButton.setTag(i); // Use tag to store the value
            humanGroup.addView(radioButton);
        }
        layout.addView(humanGroup);

        // Create UI for Computer
        TextView computerLabel = new TextView(this);
        computerLabel.setText("Computer:");
        layout.addView(computerLabel);

        RadioGroup computerGroup = new RadioGroup(this);
        computerGroup.setOrientation(RadioGroup.HORIZONTAL);
        for (int i = 1; i <= 6; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(String.valueOf(i));
            radioButton.setTag(i); // Use tag to store the value
            computerGroup.addView(radioButton);
        }
        layout.addView(computerGroup);

        // Add the layout to the dialog
        builder.setView(layout);

        // Set dialog buttons
        builder.setPositiveButton("Set", (dialog, which) -> {
            // Get selected value for Human
            int selectedHumanId = humanGroup.getCheckedRadioButtonId();
            if (selectedHumanId != -1) {
                RadioButton selectedHumanButton = humanGroup.findViewById(selectedHumanId);
                humanScore[0] = (int) selectedHumanButton.getTag();
            }

            // Get selected value for Computer
            int selectedComputerId = computerGroup.getCheckedRadioButtonId();
            if (selectedComputerId != -1) {
                RadioButton selectedComputerButton = computerGroup.findViewById(selectedComputerId);
                computerScore[0] = (int) selectedComputerButton.getTag();
            }

            computerDice.setImageResource(diceImages[computerScore[0] - 1]);

            // Update human's dice ImageView based on the roll
            humanDice.setImageResource(diceImages[humanScore[0]  - 1]);

            // Determine the winner or if it's a draw
            if (humanScore[0] > computerScore[0]) {
                // Human wins
                displayWinner("Human");
            } else if (computerScore[0] > humanScore[0]) {
                // Computer wins
                displayWinner("Computer");
            } else {
                // It's a draw, ask to reroll
                //Toast.makeText(this, "It's a draw! Please roll again.", Toast.LENGTH_SHORT).show();
                drawText.setVisibility(View.VISIBLE);
            }


        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();


    }


    /**
     * Displays the winner of the dice roll.
     * @param winner The name of the winner.
     */
    private void displayWinner(String winner) {

        drawText.setVisibility(View.INVISIBLE);
        manSetButton.setVisibility(View.GONE);

        // Show the winner in the TextView
        winnerTextView.setText(winner + " won the toss!");

        // Hide the "Roll Dice" button
        rollDiceButton.setVisibility(View.GONE);

        // Make the "Start the Tournament" button visible
        startTournamentButton.setVisibility(View.VISIBLE);

        // Save the winner to pass to the next activity
        startTournamentButton.setTag(winner); // Store the winner name in the button tag
    }

    /**
     * Starts the PlayRoundActivity with the winner's name.
     */
    private void startPlayRoundActivity() {
        // Retrieve the winner name from the button tag
        String winner = (String) startTournamentButton.getTag();

        // Start the PlayRoundActivity with the winner's name
        Intent intent = new Intent(TurnDecideActivity.this, PlayRoundActivity.class);
        intent.putExtra("winner_name", winner);
        startActivity(intent);
    }
}

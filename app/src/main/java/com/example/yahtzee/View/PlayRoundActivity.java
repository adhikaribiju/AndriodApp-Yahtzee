package com.example.yahtzee.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.Model.Combinations;
import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.R;

import java.util.ArrayList;
import java.util.Random;



public class PlayRoundActivity extends AppCompatActivity {

    public Combinations combinations;

    private Button rollButton;
    private Button manRoll;
    private Random random = new Random(); // Initialize Random

    private ImageView[] diceViews;
    private int rollsCount = 0;

    private static final int HUMAN = 1;     // ID for the human player
    private static final int COMPUTER = 2;  // ID for the computer player

    // Array of drawable resource IDs for the dice images
    private final int[] diceImages = {
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    private ArrayList<Integer> Currentdice = new ArrayList<>(5); // Initialize dice array

    private ArrayList<Integer> availableCombinations;
    private ArrayList<Integer> unfilledCategories;

    private boolean[] isSelected = new boolean[5]; // Track selected dice
    private boolean[] isKept = new boolean[5]; // Track dice kept at 2nd roll
    private ArrayList<Integer> selectedDiceInd = new ArrayList<>(); // Store selected dice index
    private ArrayList<Integer> selectedDice = new ArrayList<>(); // Store selected dice index

    public interface DiceEntryCallback {
        void onDiceEntered(ArrayList<Integer> diceValues);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_round);



        rollButton = findViewById(R.id.rollButton);
        manRoll = findViewById(R.id.Manuallroll);


        diceViews = new ImageView[] {
                findViewById(R.id.dice1),
                findViewById(R.id.dice2),
                findViewById(R.id.dice3),
                findViewById(R.id.dice4),
                findViewById(R.id.dice5)
        };

        // Retrieve the winner's name from the intent
        String winnerName = getIntent().getStringExtra("winner_name");

        // Initialize a TextView to display the winner's name
        TextView winnerTextView = findViewById(R.id.playerTurnText);

        // Display a message indicating which player won the toss
        winnerTextView.setText(winnerName + "'s Turn");

        assert winnerName != null;
        if (winnerName.equals("Human")) {
            MainActivity.tournament.startTournament(1);
        }
        else{
            MainActivity.tournament.startTournament(2);
        }



        for (int i = 0; i < diceViews.length; i++) {
            final int index = i; // Capture the index for use in the listener
            diceViews[i].setOnClickListener(v -> {
                if (rollsCount == 2 && isKept[index]) {
                    // If it's the 3rd roll, kept dice cannot be unselected
                    Toast.makeText(PlayRoundActivity.this, "This dice is kept and cannot be unselected.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rollsCount == 0) return;

                // Toggle selection state
                isSelected[index] = !isSelected[index];

                // Update the border based on selection state
                if (isSelected[index]) {
                    diceViews[index].setBackgroundResource(R.drawable.blue_border); // Highlight selected
                } else {
                    diceViews[index].setBackgroundResource(R.drawable.default_border); // Remove highlight
                }
            });
        }




        // Set the button click listener
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear previously selected dice values
                //selectedDiceInd.clear();

                // Collect values of selected dice
                for (int i = 0; i < diceViews.length; i++) {
                    if (isSelected[i]) {
                        selectedDiceInd.add(i); // Add the dice index (0-5)

                    }
                }

                // selectedDice is the kept dice

                //rollsCount++;


                // select vayesi unselect mildaina
                if (rollsCount < 3) {
                    rollsCount++;
                    updateRollsLeftText(rollsCount);
                    if (rollsCount == 2) {
                        // Second roll: Mark currently selected dice as kept
                        for (int i = 0; i < isSelected.length; i++) {
                            if (isSelected[i]) {
                                isKept[i] = true; // Mark dice as kept at 2nd roll
                            }
                        }
                    }
                }



                rollDices(generateDice(selectedDiceInd)); // Important ** DO NOT MESS
                // need to keep track of the number of dice rolls
                //
                // Final Roll
                if (rollsCount >2){
                    rollsCount = 0; //reset
                    updateRollsLeftText(rollsCount);
                    // After 3rd roll, reset all dice borders
                    initDiceBorders();


                    // if there is available categories to score, must score [show a toast saying you must select a category to score]
                    // else, next player plays

                    if (!combinations.availableToScoreCategories().isEmpty()) {
                        // There are available categories to score
                        rollButton.setEnabled(false);
                        updateRollsLeftText(4);

                        Toast.makeText(PlayRoundActivity.this, "You must select a category to score now.", Toast.LENGTH_SHORT).show();
                    } else {
                        // No available categories, next player's turn
                        playNextTurn();
                    }

                    // Clear previously selected dice values
                    selectedDiceInd.clear();
                    isSelected = new boolean[5];

                    //playNextTurn();
                }

            }


        });

        // Set the button click listener
        manRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rollsCount++;
                updateRollsLeftText(rollsCount);
                askDiceEntry(PlayRoundActivity.this, diceValues -> {
                    // Now `diceValues` contains the validated user input
                    rollDices(diceValues);  // Pass the values to rollDices
                    // Keep track of the number of dice rolls here if needed
                });

                if (rollsCount >3){
                    rollsCount = 0; //reset
                    playNextTurn();
                }

                //rollDices(askDiceEntry(PlayRoundActivity.this));
                // need to keep track of the number of dice rolls
                //
            }
        });
    }
    public void askDiceEntry(Context context, DiceEntryCallback callback) {
        ArrayList<Integer> diceValues = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Dice Values (1-6)");

        // Create an input field for each dice entry
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<EditText> inputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Dice " + (i + 1));
            layout.addView(input);
            inputs.add(input);
        }

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Validate and collect dice values
            for (EditText input : inputs) {
                String text = input.getText().toString().trim();
                if (!text.isEmpty()) {
                    int value = Integer.parseInt(text);
                    if (value >= 1 && value <= 6) {
                        diceValues.add(value);
                    } else {
                        Toast.makeText(context, "Please enter values between 1 and 6.", Toast.LENGTH_SHORT).show();
                        diceValues.clear();
                        break;
                    }
                }
            }

            // Trigger the callback with the collected dice values
            callback.onDiceEntered(diceValues);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }


    private ArrayList<Integer> generateDice(ArrayList<Integer> keptDicesInd){
        // Generate 5 random dice rolls



        ArrayList<Integer> newDice = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            if (keptDicesInd.contains(i)) {
                // Keep the existing value for dice at index `i`
                newDice.add(Currentdice.get(i));
            } else {
                // Generate a new random value for dice at index `i`
                newDice.add(random.nextInt(6) + 1); // Random number from 1 to 6
            }
        }

        Currentdice = new ArrayList<>(newDice);

        return newDice;

    }


    private void rollDices( ArrayList<Integer> dice) {

        int numRolls = 1; // to track the number of rolls

        initBoard();

        // Generate 5 random dice rolls
//        dice.clear(); // reset
//
//        for(int i = 0; i< 5; i++) {
//            dice.add(random.nextInt(6) + 1); // Random number from 1 to 6
//        }

        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setImageResource(diceImages[dice.get(i) - 1]);
        }

        combinations = new Combinations(dice);

        availableCombinations = combinations.availableToScoreCategories();



// Convert ArrayList to a single String
        StringBuilder combinationsText = new StringBuilder();
        for (Integer combination : availableCombinations) {
            combinationsText.append(combination+1).append("\n");
        }

        // check garna ko lagi alert box ho hai
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Available Combinations")
//                .setMessage(combinationsText.toString())
//                .setPositiveButton("OK", null) // Dismiss button
//                .show();
//

        // Loop through each available combination and apply the border to corresponding TextViews
        for (Integer categoryNumber : availableCombinations) {

            String textViewId = "category" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(textViewId, "id", getPackageName());

            TextView categoryTextView = findViewById(resID);
            if (categoryTextView != null) {
                // Apply the highlight border to the TextView
                categoryTextView.setBackgroundResource(R.drawable.highlight_border);
            }
        }


        // Make only the specified buttons in availableCategories visible
        for (Integer categoryNumber : availableCombinations) {
            String buttonId = "scoreButton" + (categoryNumber+1);
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            if (scoreButton != null) {
                scoreButton.setVisibility(View.VISIBLE); // Make specified buttons visible

            }
        }

        // Loop through each score button to set up an OnClickListener
        for (int i = 1; i <= 12; i++) {
            String buttonId = "scoreButton" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            final int categoryNumber = i-1; // Store the button number for use in the listener

            if (scoreButton != null) {
                scoreButton.setOnClickListener(v -> {
                    // Get the score for the clicked category
                    rollButton.setEnabled(true);
                    int score = combinations.getScore(categoryNumber);


                    String textId = "score" + (categoryNumber + 1 );
                    int resID2 = getResources().getIdentifier(textId, "id", getPackageName());
                    TextView textScore = findViewById(resID2);

                    String playerText = "player" + (categoryNumber + 1 );
                    int resID3 = getResources().getIdentifier(playerText, "id", getPackageName());
                    TextView playerScoredText = findViewById(resID3);

                    String roundText = "r" + (categoryNumber + 1 );
                    int resID4 = getResources().getIdentifier(roundText, "id", getPackageName());
                    TextView roundNoText = findViewById(resID4);


                    if (textScore != null) {
                        // Set the textScore to display the score
                        textScore.setText(String.valueOf(score));

                        playerScoredText.setText(String.valueOf(Tournament.currentPlayerId));

                        roundNoText.setText(String.valueOf(MainActivity.tournament.round.getRoundNo()));

                        // update the scorecard.....how are you gonna do this? idk

                        MainActivity.tournament.round.playTurn(Tournament.currentPlayerId, dice,  categoryNumber + 1);


                        rollsCount =0; //reset

                        TextView rollLeftText = findViewById(R.id.rollLeftText);
                        rollLeftText.setText("No Rolls Left");

                        handleGameEnd(); // check if scorecard filled

                        playNextTurn();
                    }

                });
            }
        }



        // find out the available categories :: maybe need to pass scorecard object for this, not sure but take a look into this
        // [skip this for now] based on the available categories, check the scorecard if is it scoreable
        // for all the available categories, highlight using a color on the board
        // if user clicks on any available categories, score it -> may need to pass the player_id


        // Update computer's dice ImageView based on the roll
       // computerDice.setImageResource(diceImages[computerRoll - 1]);

        // Update human's dice ImageView based on the roll
        //humanDice.setImageResource(diceImages[humanRoll - 1]);

        // Determine the winner or if it's a draw
       // if (humanRoll > computerRoll) {
            // Human wins
        //    displayWinner("Human");
        //} else if (computerRoll > humanRoll) {
            // Computer wins
          //  displayWinner("Computer");
        //} else {
            // It's a draw, ask to reroll
          //  Toast.makeText(this, "It's a draw! Please roll again.", Toast.LENGTH_SHORT).show();
        //}
    }

    public void playNextTurn(){
        initBoard();
        // find out who plays next, accordingly update the round number and turn's field
        MainActivity.tournament.round.playRoundt(Tournament.currentPlayerId);
        Tournament.currentPlayerId = (MainActivity.tournament.round.findNextPlayer(Tournament.currentPlayerId));

        // by this point, i should have new round num or new player: update those
        updateUIPlayerRoundNo();

       // updateRollsLeftText(4);
        initDiceBorders();


    }

    public void updateUIPlayerRoundNo(){

        TextView winnerTextView = findViewById(R.id.playerTurnText);

        String player = (Tournament.currentPlayerId == 1) ? "Human" : "Computer";

        // Display a message indicating which player won the toss
        winnerTextView.setText(player + "'s Turn");


        TextView RoundNo = findViewById(R.id.roundNumberText);

        // Display a message indicating which player won the toss
        RoundNo.setText("Round No: " + MainActivity.tournament.round.getRoundNo());
    }


    public void initBoard(){
        // Unhighlight
        for (int i = 1; i <= 12; i++) { // Replace 12 with the actual number of categories
            String textViewId = "category" + i;
            int resID = getResources().getIdentifier(textViewId, "id", getPackageName());

            TextView categoryTextView = findViewById(resID);
            if (categoryTextView != null) {
                // Reset the background to the default background or transparent
                categoryTextView.setBackgroundResource(R.drawable.default_background); // Or use 0 for transparent
            }
        }

        // invisible
        for (int i = 1; i <= 12; i++) { // Loop for all 12 buttons
            String buttonId = "scoreButton" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            if (scoreButton != null) {
                scoreButton.setVisibility(View.INVISIBLE); // Set all buttons to invisible initially
            }
        }

    }


    public void handleGameEnd() {
        if (combinations.isScorecardFull()) {
            // Determine the winner (assuming you have a way to get scores for Human and Computer)
            int humanScore = combinations.getTotal(HUMAN);
            int computerScore = combinations.getTotal(COMPUTER);
            String winnerMessage;

            if (humanScore > computerScore) {
                winnerMessage = "\nCONGRATS!\n\nYou Won\n\nYour Score: " + humanScore + "\nComputer Score: " + computerScore;
            } else if (computerScore > humanScore) {
                winnerMessage = "Winner: Computer\n\nYour Score: " + humanScore + "\nComputer Score: " + computerScore;
            } else {
                winnerMessage = "It's a Tie!\n\nYour Score: " + humanScore + "\nComputer Score: " + computerScore;
            }

            // Show a dialog with the winner information
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(winnerMessage)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Redirect to MainActivity when OK is pressed
                        combinations.resetScorecard();
                        Intent intent = new Intent(PlayRoundActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // End PlayRoundActivity
                    })
                    .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                    .show();
        }
    }

    private void initDiceBorders() {
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setBackgroundResource(R.drawable.default_border); // Reset border to default
            isSelected[i] = false; // Reset selected state
            isKept[i] = false; // Reset kept state
        }
        Toast.makeText(this, "Dice selection has been reset.", Toast.LENGTH_SHORT).show();
        updateRollsLeftText(rollsCount);
    }


    private void updateRollsLeftText(int rollsCount) {
        // Reference to the TextView
        TextView rollLeftText = findViewById(R.id.rollLeftText);

        // Update text based on rollsCount
        switch (rollsCount) {
            case 0:
                rollLeftText.setText("3 Rolls Left");
                break;
            case 1:
                rollLeftText.setText("2 Rolls Left");
                break;
            case 2:
                rollLeftText.setText("1 Roll Left");
                break;
            default:
                rollLeftText.setText("No Rolls Left");
                break;
        }
    }
}

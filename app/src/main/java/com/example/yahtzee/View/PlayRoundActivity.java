package com.example.yahtzee.View;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yahtzee.Model.Combinations;
import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Round;
import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.R;
import com.google.android.material.button.MaterialButton;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;



public class PlayRoundActivity extends AppCompatActivity {

    public Combinations combinations;
    private Button rollButton;

    private ImageView[] diceViews;
    private int rollsCount = 0;
    private static final int HUMAN = 1;     // ID for the human player
    private static final int COMPUTER = 2;  // ID for the computer player
    private boolean manualChoice = false;
    private final Random random = new Random();
    private final Logger logger = Logger.getInstance();


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


    private boolean[] isSelected = new boolean[5]; // Track selected dice
    private final boolean[] isKept = new boolean[5]; // Track dice kept at 2nd roll
    private final ArrayList<Integer> selectedDiceInd = new ArrayList<>(); // Store selected dice index
    private int compRollCount = 0;

    MaterialButton manualRollButton;

    ArrayList<Integer> KeptDiceInd = new ArrayList<>();


    /**
     * Callback interface for manual dice input
     */
    public interface ManualDiceCallback {
        void onDiceValuesSet(ArrayList<Integer> diceValues);
    }


    /** 
     * This method is called when the activity is first created. It initializes the activity, including setting the content view,
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_round);

        ImageView helpButton = findViewById(R.id.helpButton);



        combinations = new Combinations();


        rollButton = findViewById(R.id.rollButton);


        diceViews = new ImageView[]{
                findViewById(R.id.dice1),
                findViewById(R.id.dice2),
                findViewById(R.id.dice3),
                findViewById(R.id.dice4),
                findViewById(R.id.dice5)
        };


        String gameType = getIntent().getStringExtra("gameType");

        manualRollButton = findViewById(R.id.Manuallroll);
        manualRollButton.setOnClickListener(v -> {
            // Get the current background tint
            ColorStateList backgroundTint = manualRollButton.getBackgroundTintList();

            if (backgroundTint != null && backgroundTint.getDefaultColor() == Color.RED) {
                // Switch to white background
                manualChoice = false;
                manualRollButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE)); // White background
                manualRollButton.setStrokeColor(ColorStateList.valueOf(Color.RED)); // Red border
                manualRollButton.setTextColor(ColorStateList.valueOf(Color.RED)); // Red text
            } else {
                // Switch to red background
                manualChoice = true;
                manualRollButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED)); // Red background
                manualRollButton.setStrokeColor(ColorStateList.valueOf(Color.WHITE)); // White border
                manualRollButton.setTextColor(ColorStateList.valueOf(Color.WHITE)); // White text
            }
        });

        if (Objects.equals(gameType, "Load")) {

            String selectedFile = getIntent().getStringExtra("selectedFile");
            logger.log("Game loaded from file: " + selectedFile);

            helpButton.setVisibility(View.INVISIBLE);

            loadFileAndRead(selectedFile);

            updateUIPlayerRoundNo();
            loadedScorecard();


            // find out the player with the lowest score since they will start the game
            // set the scorecard accordingly
            // initialize the scorecard layout

            //MainActivity.tournament.loadTournament(selectedFile);


        } else { // start a new game
            // Retrieve the winner's name from the intent

            logger.log("New Game started.");


            String winnerName = getIntent().getStringExtra("winner_name");
            logger.log(winnerName+" won the toss and is starting the round.");



            // Initialize a TextView to display the winner's name
            TextView winnerTextView = findViewById(R.id.playerTurnText);

            // Display a message indicating which player won the toss
            winnerTextView.setText(format("%s's Turn", winnerName));

            assert winnerName != null;
            if (winnerName.equals("Human")) {
                MainActivity.tournament.startTournament(1);
                helpButton.setVisibility(View.INVISIBLE);
            } else {
                MainActivity.tournament.startTournament(2);
                helpButton.setVisibility(View.INVISIBLE);
            }

        }


        for (int i = 0; i < diceViews.length; i++) {
            final int index = i; // Capture the index for use in the listener
            diceViews[i].setOnClickListener(v -> {
                if (rollsCount == 2 && isKept[index]) {
                    // If it's the 3rd roll, kept dice cannot be unselected
                    //****Toast.makeText(PlayRoundActivity.this, "This dice is kept and cannot be unselected.", Toast.LENGTH_SHORT).show();
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

        handleGameTurn();



        ImageView saveGameButton = findViewById(R.id.saveGameButton);
        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Step 1: Show an input dialog to get the file name from the user
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayRoundActivity.this);
                builder.setTitle("Save Game");

                // Add an input field
                final EditText input = new EditText(PlayRoundActivity.this);
                input.setHint("Enter file name");
                builder.setView(input);

                // Add Save and Cancel buttons
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String fileName = input.getText().toString().trim();

                    // Step 2: Validate the file name
                    if (fileName.isEmpty()) {
                        //**Toast.makeText(PlayRoundActivity.this, "File name cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Call the saveGame() function
                    // saveGame(fileName);
                    saveGameHandler(fileName);

                    // Step 3: Show a confirmation dialog that the game has been saved
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(PlayRoundActivity.this);
                    confirmationDialog.setTitle("Game Saved");
                    confirmationDialog.setMessage("Your game has been saved successfully.");
                    confirmationDialog.setPositiveButton("OK", (confirmation, which1) -> {
                        // Exit the app
                        finishAffinity(); // Exit the app
                    });

                    confirmationDialog.show();
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.show();
            }
        });


        //ImageView helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<Integer> selectedDiceInds = new ArrayList<>();
                for (int i = 0; i < diceViews.length; i++) {
                    if (isSelected[i]) {
                        selectedDiceInds.add(i); // Add the dice index (0-5)
                    }
                }

                int categoryReceived = MainActivity.tournament.getRound().getHuman().playTurn(HUMAN, Currentdice, rollsCount, selectedDiceInds);

                // Create the dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayRoundActivity.this);
                builder.setTitle("Help");

                // Initialize a StringBuilder for the message
                StringBuilder messageBuilder = new StringBuilder();

                // Determine the message to display
                if (categoryReceived == -1) {
                    // Generate dice values from KeptDiceInd
                    StringBuilder values = new StringBuilder();
                    for (int index : selectedDiceInds) {
                        if (index >= 0 && index < Currentdice.size()) {
                            values.append(Currentdice.get(index)).append(" ");
                        }
                    }
                    messageBuilder.append("You may keep these dices: ").append(values.toString().trim());

                    if (selectedDiceInds.isEmpty()) {
                        messageBuilder.setLength(0); // Clears the existing content
                        messageBuilder.append("You may re-roll all dices:");
                    }
                    else {
                        // Append the reasoning message
                        messageBuilder.append("\n\n").append(MainActivity.tournament.getRound().getHuman().getReasoning());
                    }
                } else {
                    messageBuilder.append("You may score at Category: ").append(combinations.getCategoryName(categoryReceived));
                    messageBuilder.append("\n\n").append(MainActivity.tournament.getRound().getHuman().getReasoning());
                }



                logger.log("Computer suggested: "+ messageBuilder);

                // Set the message in the dialog
                builder.setMessage(messageBuilder.toString());

                // Add OK button and show the dialog
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
            }
        });


        ImageView logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlayRoundActivity.this);
                builder.setTitle("Log Contents");

                // Set the log contents directly as the dialog message
                builder.setMessage(Logger.getInstance().getLog());

                // Add the OK button
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                // Create and show the dialog
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    /**
     * This method handles the game turn for both the human and computer players. It sets up the button click listener for the rollButton and handles the view logic.
     s*/
    public void handleGameTurn() {

        // Set the button click listener
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unhighlightBoard();

                if (MainActivity.tournament.getCurrentPlayerId() == COMPUTER) {



                    if(manualChoice){
                        manualInputDialog(KeptDiceInd, newDice -> {
                            // Roll dice with manually entered values
                            Currentdice = new ArrayList<>(newDice);
                            logger.log("Computer rolled\n" + Currentdice);
                            computerRoll();
                        });
                    }
                    else{
                        ArrayList<Integer> dice = generateDice(KeptDiceInd); // Important ** DO NOT MESS //
                        logger.log("Computer rolled\n" + dice);
                        computerRoll();
                    }

                    return;
                }

                compRollCount = 0; //reset
                KeptDiceInd.clear();


                // Collect values of selected dice
                for (int i = 0; i < diceViews.length; i++) {
                    if (isSelected[i]) {
                        selectedDiceInd.add(i); // Add the dice index (0-5)

                    }
                }

                ImageView helpButton = findViewById(R.id.helpButton);
                if(rollsCount >= 0 ){
                    helpButton.setVisibility(View.VISIBLE);
                }



                // Can't be unselected if selected before
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

                StringBuilder messageBuilder = new StringBuilder();
                ArrayList<Integer> humanKeptDices = new ArrayList<>(new HashSet<>(selectedDiceInd));
                if (!selectedDiceInd.isEmpty()){
                    for (int index : humanKeptDices) {
                        messageBuilder.append(Currentdice.get(index)).append(" ");
                    }
                    logger.log("Human kept dices: \n" + messageBuilder);
                }

                if(manualChoice){
                    manualInputDialog(selectedDiceInd, newDice -> {
                        // Roll dice with manually entered values
                        Currentdice = new ArrayList<>(newDice);
                        logger.log("Human rolled\n "+ Currentdice);
                        rollDices(newDice); // Call rollDices with the manually generated dice
                    });
                }
                else{
                    rollDices(generateDice(selectedDiceInd)); // Important ** DO NOT MESS
                    logger.log("Human rolled\n "+ Currentdice.toString());
                }
                //rollDices(generateDice(selectedDiceInd)); // Important ** DO NOT MESS
                //updatePlayScores();

                // need to keep track of the number of dice rolls
                //
                // Final Roll
                if (rollsCount > 2) {
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

                        //***Toast.makeText(PlayRoundActivity.this, "You must select a category to score now.", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * This method highlights the score, player, and round boxes on the scorecard that are available for scoring.
     */
    public void highlightBoard(){
        // Loop through all the available combinations

        int highlightColor = Color.parseColor("#EBEAEF"); // Highlight color

        ArrayList<Integer> categoriesAvailable = combinations.availableToScoreCategories();

        for (Integer categoryIndex : categoriesAvailable) {
            // Increment by 1 because table layout indices start from 1
            int adjustedIndex = categoryIndex + 1;

            // Highlight score box
            String scoreId = "score" + adjustedIndex;
            int scoreResID = getResources().getIdentifier(scoreId, "id", getPackageName());
            TextView scoreTextView = findViewById(scoreResID);
            if (scoreTextView != null) {
                scoreTextView.setBackgroundColor(highlightColor);
                scoreTextView.setText(valueOf(combinations.getScore(categoryIndex)));
            }

            // Highlight player box
            String playerId = "player" + adjustedIndex;
            int playerResID = getResources().getIdentifier(playerId, "id", getPackageName());
            TextView playerTextView = findViewById(playerResID);
            if (playerTextView != null) {
                playerTextView.setBackgroundColor(highlightColor);
               playerTextView.setText(valueOf(MainActivity.tournament.getCurrentPlayerId()));
            }

            // Highlight round box
            String roundId = "r" + adjustedIndex;
            int roundResID = getResources().getIdentifier(roundId, "id", getPackageName());
            TextView roundTextView = findViewById(roundResID);
            if (roundTextView != null) {
                roundTextView.setBackgroundColor(highlightColor);
                roundTextView.setText(valueOf(Round.getRoundNo()));
            }
        }

    }


    /**
     * This method unhighlights the score, player, and round boxes on the scorecard that are available for scoring.
     */
    public void unhighlightBoard() {

        int highlightColor = Color.parseColor("#EBEAEF"); // Highlight color

        // Loop through all the categories (1 to 12)
        for (int i = 1; i <= 12; i++) {
            // Unhighlight score box
            String scoreId = "score" + i;
            int scoreResID = getResources().getIdentifier(scoreId, "id", getPackageName());
            TextView scoreTextView = findViewById(scoreResID);
            if (scoreTextView != null) {
                // Check if the background is GRAY and reset to transparent
                if (scoreTextView.getBackground() instanceof ColorDrawable) {
                    int color = ((ColorDrawable) scoreTextView.getBackground()).getColor();
                    if (color == highlightColor) {
                        scoreTextView.setBackgroundColor(Color.TRANSPARENT);
                        scoreTextView.setText("-");
                    }
                }
            }

            // Unhighlight player box
            String playerId = "player" + i;
            int playerResID = getResources().getIdentifier(playerId, "id", getPackageName());
            TextView playerTextView = findViewById(playerResID);
            if (playerTextView != null) {
                if (playerTextView.getBackground() instanceof ColorDrawable) {
                    int color = ((ColorDrawable) playerTextView.getBackground()).getColor();
                    if (color == highlightColor) {
                        playerTextView.setBackgroundColor(Color.TRANSPARENT);
                        playerTextView.setText("-");
                    }
                }
            }

            // Unhighlight round box
            String roundId = "r" + i;
            int roundResID = getResources().getIdentifier(roundId, "id", getPackageName());
            TextView roundTextView = findViewById(roundResID);
            if (roundTextView != null) {
                if (roundTextView.getBackground() instanceof ColorDrawable) {
                    int color = ((ColorDrawable) roundTextView.getBackground()).getColor();
                    if (color == highlightColor) {
                        roundTextView.setBackgroundColor(Color.TRANSPARENT);
                        roundTextView.setText("-");
                    }
                }
            }
        }
    }





    /**
     * This method generates 5 random dice rolls, keeping the dice values that are selected by the user.
     * @param keptDicesInd The indices of the dice values to keep.
     @return The new dice values after generating random rolls and keeping the selected dice values.
     */
    private ArrayList<Integer> generateDice(ArrayList<Integer> keptDicesInd) {
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




    /**
     * This method rolls the dice and displays the dice images on the screen. It also highlights the available categories for scoring.
     * @param dice The dice values to display.
     */
    private void rollDices(ArrayList<Integer> dice) {


        initBoard();


        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setImageResource(diceImages[dice.get(i) - 1]);
        }

        combinations = new Combinations(dice);

        availableCombinations = combinations.availableToScoreCategories();


// Convert ArrayList to a single String
        StringBuilder combinationsText = new StringBuilder();
        for (Integer combination : availableCombinations) {
            combinationsText.append(combination + 1).append("\n");
        }

        ArrayList<Integer> potentialCategories = new ArrayList<>(MainActivity.tournament.getRound().getHuman().potentialCategories(Currentdice,rollsCount));


        // Loop through each available combination and apply the border to corresponding TextViews
        for (Integer categoryNumber : potentialCategories) {

            String textViewId = "category" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(textViewId, "id", getPackageName());

            TextView categoryTextView = findViewById(resID);
            if (categoryTextView != null) {
                // Apply the highlight border to the TextView
                categoryTextView.setBackgroundResource(R.drawable.highlight_border);
            }
        }

        highlightBoard();


        // Make only the specified buttons in availableCategories visible
        for (Integer categoryNumber : availableCombinations) {
            String buttonId = "scoreButton" + (categoryNumber + 1);
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
            final int categoryNumber = i - 1; // Store the button number for use in the listener

            if (scoreButton != null) {
                scoreButton.setOnClickListener(v -> {

                    unhighlightBoard();
                    // Get the score for the clicked category
                    rollButton.setEnabled(true);
                    int score = combinations.getScore(categoryNumber);


                    String textId = "score" + (categoryNumber + 1);
                    int resID2 = getResources().getIdentifier(textId, "id", getPackageName());
                    TextView textScore = findViewById(resID2);

                    String playerText = "player" + (categoryNumber + 1);
                    int resID3 = getResources().getIdentifier(playerText, "id", getPackageName());
                    TextView playerScoredText = findViewById(resID3);

                    String roundText = "r" + (categoryNumber + 1);
                    int resID4 = getResources().getIdentifier(roundText, "id", getPackageName());
                    TextView roundNoText = findViewById(resID4);


                    if (textScore != null) {
                        // Set the textScore to display the score
                        textScore.setText(valueOf(score));


                        playerScoredText.setText(valueOf(HUMAN));

                        roundNoText.setText(valueOf(Round.getRoundNo()));

                        // update the scorecard.....

                        MainActivity.tournament.getRound().playTurn(HUMAN, dice, categoryNumber + 1);

                        logger.log("Human scored "+ score + " on "+ combinations.getCategoryName(categoryNumber));

                        rollsCount = 0; //reset

                        TextView rollLeftText = findViewById(R.id.rollLeftText);
                        rollLeftText.setText(R.string.no_rolls_left);

                        handleGameEnd(); // check if scorecard filled

                        playNextTurn();
                    }

                });
            }
        }

    }

    /**
     * This method plays the next turn in the game, updating the round number and the current player.
     * It also resets the dice borders and updates the player scores.
     */
    public void playNextTurn() {

        handleGameEnd();
        ImageView helpButton = findViewById(R.id.helpButton);

        String playerName = (MainActivity.tournament.getCurrentPlayerId()  == 1) ? "Human" : "Computer";

        logger.log(playerName + "'s Turn Ended!");

        initBoard();
        // find out who plays next, accordingly update the round number and turn's field
        MainActivity.tournament.getRound().playRoundt();
        MainActivity.tournament.setCurrentPlayerId(MainActivity.tournament.getRound().findNextPlayer(MainActivity.tournament.getCurrentPlayerId()));

        // by this point, i should have new round num or new player: update those
        updateUIPlayerRoundNo();

        // updateRollsLeftText(4);
        initDiceBorders();
        updatePlayScores();

        helpButton.setVisibility(View.INVISIBLE);


        playerName = (MainActivity.tournament.getCurrentPlayerId()  == 1) ? "Human" : "Computer";
        logger.log(playerName+ "'s Turn Started!");


    }


    /**
     * This method updates the player and round number displayed on the screen.
     * It sets the text of the playerTurnText and roundNumberText TextViews to the current player and round number, respectively.
     */
    public void updateUIPlayerRoundNo() {

        TextView winnerTextView = findViewById(R.id.playerTurnText);

        String player = (MainActivity.tournament.getCurrentPlayerId() == 1) ? "Human" : "Computer";

        // Display a message indicating which player won the toss
        winnerTextView.setText(format("%s's Turn", player));


        TextView RoundNo = findViewById(R.id.roundNumberText);

        // Display a message indicating which player won the toss
        RoundNo.setText(format("Round No: %d", Round.getRoundNo()));
    }


    /**
     * This method initializes the scorecard board by resetting the background of the categories and hiding the score buttons.
     */
    public void initBoard() {
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
                scoreButton.setText(R.string.score);       // Set text to "Available"
                scoreButton.setClickable(true);        // Make the button not clickable
            }
        }

    }

    /**
     * This method checks if the scorecard is full and displays a dialog with the winner information.
     */
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
            logger.log(winnerMessage);

            // Show a dialog with the winner information
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(winnerMessage)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Reset Tournament and redirect to MainActivity
                        MainActivity.tournament.getRound().resetRound();
                        MainActivity.tournament = new Tournament(); // Reset the tournament instance

                        // Reset the scorecard
                        combinations.resetScorecard();
                        // Redirect to MainActivity
                        Intent intent = new Intent(PlayRoundActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure a fresh MainActivity
                        startActivity(intent);
                    })
                    .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                    .show();
        }
    }

    /**
     * This method resets the dice borders to the default border and resets the selected and kept states of the dice.
     */
    private void initDiceBorders() {
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setBackgroundResource(R.drawable.default_border); // Reset border to default
            isSelected[i] = false; // Reset selected state
            isKept[i] = false; // Reset kept state
        }
        //***Toast.makeText(this, "Dice selection has been reset.", Toast.LENGTH_SHORT).show();
        updateRollsLeftText(rollsCount);
    }

    /**
     * This method updates the rolls left of player per turn
     * @param rollsCount The number of rolls left for the player.
     */
    private void updateRollsLeftText(int rollsCount) {
        // Reference to the TextView
        TextView rollLeftText = findViewById(R.id.rollLeftText);

        // Update text based on rollsCount
        switch (rollsCount) {
            case 0:
                rollLeftText.setText(R.string._3_rolls_left);
                break;
            case 1:
                rollLeftText.setText(R.string._2_rolls_left);
                break;
            case 2:
                rollLeftText.setText(R.string._1_roll_left);
                break;
            default:
                rollLeftText.setText(R.string.no_rolls_leftt);
                break;
        }
    }

    /**
     * This method initializes the computer's turn logic, including rolling the dice and scoring.
     * @param keptDiceInd (ArrayList<Integer>) The dice values that the computer has decided to keep.
     */
    public void computerPlay(ArrayList<Integer> keptDiceInd) {


        combinations.updateDice(Currentdice);
        availableCombinations = combinations.availableToScoreCategories();

        // Display dice in the UI
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setImageResource(diceImages[Currentdice.get(i) - 1]);
        }

        // Make only the specified buttons in availableCategories visible
        for (Integer categoryNumber : availableCombinations) {
            String buttonId = "scoreButton" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            if (scoreButton != null) {
                scoreButton.setVisibility(View.VISIBLE); // Make specified buttons visible
                scoreButton.setText(R.string.available);       // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button not clickable
            }
        }
        highlightBoard();

        // decidedToKeepMsg(diceVals, keptDiceInd,1);
        // Use a Handler to delay the scoring logic
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Call scoring logic after displaying dice
            if (!computerFirstRoll(Currentdice, keptDiceInd)) {


                StringBuilder messageBuilder = new StringBuilder();

                if (!keptDiceInd.isEmpty()) {
                    messageBuilder.append("Computer decided to keep these dice: \n");
                } else {
                    messageBuilder.append("Computer decided to roll all dice. \n");
                }
                for (int index : keptDiceInd) {
                    messageBuilder.append(Currentdice.get(index)).append(" ");
                    diceViews[index].setBackgroundResource(R.drawable.blue_border); // Highlight selected
                }




                if (!keptDiceInd.isEmpty()) {messageBuilder.append("\n\n").append(MainActivity.tournament.getRound().getReasoning());}

                logger.log(messageBuilder.toString());

                new AlertDialog.Builder(this) // Replace with your activity's context
                        .setTitle("Computer's Decision")
                        .setMessage(messageBuilder.toString().trim()) // Display the dynamic message
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss(); // Dismiss the dialog

                        })
                        .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                        .show();
            }

        }, 1000); // 1-second delay

    }

    /**
     * This method initializes the computer's first roll logic, including rolling the dice and scoring.
     * @param diceVals (ArrayList<Integer>) The dice values to roll.
     * @param keptDiceInd (ArrayList<Integer>) The dice values that the computer has decided to keep.
     */
    public boolean computerFirstRoll(ArrayList<Integer> diceVals, ArrayList<Integer> keptDiceInd) {
        // Keep dice indices (if any logic applies)
        //ArrayList<Integer> keptDiceInd = new ArrayList<>();
        displayPotentialComputer(0);

        int categoryReceived = MainActivity.tournament.getRound().playTurnComputer(COMPUTER, diceVals, 0, keptDiceInd);
        if (categoryReceived == -1) {
            return false;
        } else {
            // Show a dialog before updating the scorecard


            logger.log("Computer decided to score in "+ combinations.getCategoryName(categoryReceived));
            new AlertDialog.Builder(this)
                    .setTitle("Computer's Decision")
                    .setMessage("The computer has decided to score in: " + combinations.getCategoryName(categoryReceived))
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Update the scorecard UI after the user presses OK
                        unhighlightBoard();
                        updateScorecardUI(categoryReceived);
                    })
                    .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                    .show();
        }
        return true;
    }

    /**
     * This method updates the player scores displayed on the screen.
     * @param categoryReceived (int) The category number that the player has scored in.
     */
    private void updateScorecardUI(int categoryReceived) {
        // Update the scorecard UI
        String textId = "score" + (categoryReceived + 1);
        int resID2 = getResources().getIdentifier(textId, "id", getPackageName());
        TextView textScore = findViewById(resID2);

        String playerText = "player" + (categoryReceived + 1);
        int resID3 = getResources().getIdentifier(playerText, "id", getPackageName());
        TextView playerScoredText = findViewById(resID3);

        String roundText = "r" + (categoryReceived + 1);
        int resID4 = getResources().getIdentifier(roundText, "id", getPackageName());
        TextView roundNoText = findViewById(resID4);


        int score = combinations.getScore(categoryReceived);
        textScore.setText(valueOf(score));

        playerScoredText.setText(valueOf(MainActivity.tournament.getCurrentPlayerId()));

        roundNoText.setText(valueOf(MainActivity.tournament.getRound().getRoundNo()));


        // Call the next turn logic
        playNextTurn();
    }

    /**
     * This method initializes the computer's second roll logic, including rolling the dice and scoring.
     * @param keptDiceInd (ArrayList<Integer>) The dice values that the computer has decided to keep.
     */
    public void computerSecondRoll(ArrayList<Integer> keptDiceInd) {

        initBoard();
        displayPotentialComputer(1);


        ImageView saveGameButton = findViewById(R.id.saveGameButton);

        // Roll the dice
        combinations.updateDice(Currentdice);
        availableCombinations = combinations.availableToScoreCategories();

        // Display dice in the UI
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setImageResource(diceImages[Currentdice.get(i) - 1]);
        }

        // Make only the specified buttons in availableCategories visible
        for (Integer categoryNumber : availableCombinations) {
            String buttonId = "scoreButton" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            if (scoreButton != null) {
                scoreButton.setVisibility(View.VISIBLE); // Make specified buttons visible
                scoreButton.setText(R.string.available);        // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button not clickable
            }
        }
        highlightBoard();


        int categoryReceived = MainActivity.tournament.getRound().playTurnComputer(COMPUTER, Currentdice, 1, keptDiceInd);

        // check for kept error


        // Use a Handler to delay the scoring logic
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Call scoring logic after displaying dice
            if (categoryReceived == -1) {


                // Prepare the message string to show kept dice values
                StringBuilder messageBuilder = new StringBuilder();

                if (!keptDiceInd.isEmpty()) {
                    messageBuilder.append("Computer decided to keep these dice: \n");
                } else {
                    messageBuilder.append("Computer decided to roll all dice: \n");
                }
                for (int index : keptDiceInd) {
                    messageBuilder.append(Currentdice.get(index)).append(" ");
                    diceViews[index].setBackgroundResource(R.drawable.blue_border); // Highlight selected
                }



                if (!keptDiceInd.isEmpty()) {messageBuilder.append("\n\n").append(MainActivity.tournament.getRound().getReasoning());}

                logger.log(messageBuilder.toString());
                new AlertDialog.Builder(this) // Replace with your activity's context
                        .setTitle("Computer's Decision")
                        .setMessage(messageBuilder.toString().trim()) // Display the dynamic message
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss(); // Dismiss the dialog

                        })
                        .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                        .show();
            } else {
                // Show a dialog before updating the scorecard

                logger.log("Computer decided to score in "+ combinations.getCategoryName(categoryReceived));
                new AlertDialog.Builder(this)
                        .setTitle("Computer's Decision")
                        .setMessage("The computer has decided to score in: " + combinations.getCategoryName(categoryReceived))
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Update the scorecard UI after the user presses OK
                            unhighlightBoard();
                            updateScorecardUI(categoryReceived);
                        })
                        .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                        .show();

            }

            // Make the buttons visible again after scoring
            saveGameButton.setVisibility(View.VISIBLE);
        }, 1000); // 1-second delay

    }

    /**
     * This method initializes the computer's third roll logic, including rolling the dice and scoring.
     * @param keptDiceInd (ArrayList<Integer>) The dice values that the computer has decided to keep.
     */
    public void computerThirdRoll(ArrayList<Integer> keptDiceInd) {

        initBoard();
        displayPotentialComputer(2);


        // Roll the dice
        combinations.updateDice(Currentdice);
        availableCombinations = combinations.availableToScoreCategories();

        // Display dice in the UI
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setImageResource(diceImages[Currentdice.get(i) - 1]);
        }

        // Make only the specified buttons in availableCategories visible
        for (Integer categoryNumber : availableCombinations) {
            String buttonId = "scoreButton" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());

            Button scoreButton = findViewById(resID);
            if (scoreButton != null) {
                scoreButton.setVisibility(View.VISIBLE); // Make specified buttons visible
                scoreButton.setText(R.string.available);       // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button not clickable
            }
        }
        highlightBoard();

        int categoryReceived = MainActivity.tournament.getRound().playTurnComputer(COMPUTER, Currentdice, 2, keptDiceInd);
        if (categoryReceived == -1) {
            logger.log("Computer has nothing to score, turn skipped");
            new AlertDialog.Builder(this) // Replace with your activity's context
                    .setTitle("Computer's Decision")
                    .setMessage("Nothing to Score, skipping turn")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss(); // Dismiss the dialog
                        unhighlightBoard();
                        playNextTurn();   // Call the next turn logic
                    })
                    .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                    .show();
        } else {

            logger.log("Computer decided to score in "+ combinations.getCategoryName(categoryReceived));
            new AlertDialog.Builder(this)
                    .setTitle("Computer's Decision")
                    .setMessage("The computer has decided to score in: " + combinations.getCategoryName(categoryReceived)+ "\n\nSince there no rolls left and this gives highest score.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Update the scorecard UI after the user presses OK
                        unhighlightBoard();
                        updateScorecardUI(categoryReceived);
                    })
                    .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                    .show();
        }

    }

    /**
     * This method updates the player scores displayed on the screen.
     */
    public void updatePlayScores() {
        TextView humanScoreTotal = findViewById(R.id.humanScoreTotal);
        TextView computerScoreTotal = findViewById(R.id.computerScoreTotal);
        humanScoreTotal.setText(valueOf(combinations.getTotal(HUMAN)));
        computerScoreTotal.setText(valueOf(combinations.getTotal(COMPUTER)));
    }

    /**
     * This method loads a file from the internal Downloads directory and reads its contents.
     * @param fileName (String) The name of the file to load and read.
     */
    public void loadFileAndRead(String fileName) {
        try {
            // Locate the file in the internal Downloads directory
            File downloadsDir = new File(getFilesDir(), "Downloads");
            File fileToLoad = new File(downloadsDir, fileName);

            // Check if the file exists
            if (!fileToLoad.exists()) {
                logger.log("File not found in Downloads directory: ");
                return;
            }

            // Open the file and create a BufferedReader
            BufferedReader fileReader = new BufferedReader(new FileReader(fileToLoad));

            // Call the readFile method in your model and pass the BufferedReader
            MainActivity.tournament.loadTournament(fileReader);


        } catch (IOException e) {
            logger.log("Failed to load file: " + fileName);
            return ;
        }
    }

    /** loadedScorecard
     * This method updates the player scores displayed on the screen.
     */
    public void loadedScorecard() {
        int maxCategoryNo = 11;

        Combinations scorecard = new Combinations();
        for (int i = 0; i <= maxCategoryNo; i++) {
            int score = scorecard.getSetCategoryScore(i); // Get the score for the category

            // Get the TextView IDs dynamically using resource names
            String textId = "score" + (i + 1);
            int resID2 = getResources().getIdentifier(textId, "id", getPackageName());
            TextView textScore = findViewById(resID2);

            String playerText = "player" + (i + 1);
            int resID3 = getResources().getIdentifier(playerText, "id", getPackageName());
            TextView playerScoredText = findViewById(resID3);

            String roundText = "r" + (i + 1);
            int resID4 = getResources().getIdentifier(roundText, "id", getPackageName());
            TextView roundNoText = findViewById(resID4);

            // Update UI elements only if all necessary views are found
            if (score != 0) {
                // Update the score TextView
                textScore.setText(valueOf(score));

                // Update the player who scored
                int currentPlayerId = scorecard.getSetPlayerId(i);
                String playerName = (currentPlayerId == 1) ? "1" : "2";
                playerScoredText.setText(playerName);

                // Update the round number
                int roundNo = scorecard.getSetRoundNo(i);
                roundNoText.setText(valueOf(roundNo));
            }

            TextView humanScoreTotal = findViewById(R.id.humanScoreTotal);
            TextView computerScoreTotal = findViewById(R.id.computerScoreTotal);
            humanScoreTotal.setText(valueOf(scorecard.getTotal(HUMAN)));
            computerScoreTotal.setText(valueOf(scorecard.getTotal(COMPUTER)));
        }
    }

    /**
     * This method saves the current game state to a file in the internal Downloads directory.
     */
    public  void saveGameHandler(String fileName){
        try {
            MainActivity.tournament.saveGame(createFile(fileName));
        } catch (IOException e) {
            // Log the exception for debugging purposes
            //Log.e("SaveGame", "Failed to save game: " + e.getMessage(), e);

            // Show a user-friendly error message
            //Toast.makeText(this, "Error: Failed to save the game. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method creates a new file in the internal Downloads directory and returns a BufferedWriter for writing to the file.
     */
    private BufferedWriter createFile(String fileName) throws IOException {
        // Get the directory for saved files
        File savedFilesDir = new File(getFilesDir(), "Downloads");
        if (!savedFilesDir.exists()) {
            savedFilesDir.mkdirs(); // Create the directory if it doesn't exist
        }
        fileName = fileName +".txt";
        // Create the file
        File file = new File(savedFilesDir, fileName);

        // Return a BufferedWriter for the created file
        return new BufferedWriter(new FileWriter(file));
    }


    /**
     * This method displays a dialog for the user to enter dice values manually.
     */
    @SuppressLint("DefaultLocale")
    private void manualInputDialog(ArrayList<Integer> keptDicesInd, ManualDiceCallback callback) {
        ArrayList<Integer> newDice = new ArrayList<>(5);

        // Initialize newDice with placeholders
        for (int i = 0; i < 5; i++) {
            newDice.add(-1); // Placeholder for unfilled values
        }

        // Preserve the kept dice at their respective indices
        for (int i = 0; i < keptDicesInd.size(); i++) {
            int index = keptDicesInd.get(i);
            newDice.set(index, Currentdice.get(index)); // Keep existing value
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Dice Values");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<RadioGroup> radioGroups = new ArrayList<>();
        ArrayList<Integer> manualIndices = new ArrayList<>();

        // Identify dice indices not in keptDicesInd for manual input
        for (int i = 0; i < 5; i++) {
            if (!keptDicesInd.contains(i)) {
                manualIndices.add(i); // Dice to be manually set

                TextView label = new TextView(this);
                label.setText(String.format("\n  Dice %d:", i + 1));


                RadioGroup group = new RadioGroup(this);
                group.setOrientation(RadioGroup.HORIZONTAL);

                for (int j = 1; j <= 6; j++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(valueOf(j));
                    button.setId(j); // Use dice value as ID
                    group.addView(button);
                }

                layout.addView(label);
                layout.addView(group);
                radioGroups.add(group);
            }
        }

        builder.setView(layout);

        // When user clicks "OK"
        builder.setPositiveButton("OK", (dialog, which) -> {
            int groupIndex = 0;

            for (int i : manualIndices) {
                int selectedId = radioGroups.get(groupIndex).getCheckedRadioButtonId();
                if (selectedId == -1) {
                    newDice.set(i, 1); // Default to 1 if no value is selected
                } else {
                    newDice.set(i, selectedId); // Add the selected dice value
                }
                groupIndex++;
            }

            callback.onDiceValuesSet(newDice); // Pass the final dice values
        });

        // Handle "Cancel"
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    /**
     * This method displays the potential categories that the computer can score in after rolling the dice.
     * @param rollCount (int) The number of rolls that have been made.
     */
    public void displayPotentialComputer(int rollCount){

        ArrayList<Integer> potentialCategories = new ArrayList<>(MainActivity.tournament.getRound().getComputer().potentialCategories(Currentdice,rollCount));


        // Loop through each available combination and apply the border to corresponding TextViews
        for (Integer categoryNumber : potentialCategories) {

            String textViewId = "category" + (categoryNumber + 1);
            int resID = getResources().getIdentifier(textViewId, "id", getPackageName());

            TextView categoryTextView = findViewById(resID);
            if (categoryTextView != null) {
                // Apply the highlight border to the TextView
                categoryTextView.setBackgroundResource(R.drawable.highlight_border);
            }
        }
    }


    /** computerRoll
     * This method handles the computer's roll view logic.
     * It calls the computerPlay, computerSecondRoll, and computerThirdRoll methods based on the number of rolls made.
     */
    public void computerRoll(){
        compRollCount++;
        updateRollsLeftText(compRollCount);
        if (compRollCount == 1) computerPlay(KeptDiceInd);
        if (compRollCount == 2) computerSecondRoll(KeptDiceInd);
        if (compRollCount == 3) {
            computerThirdRoll(KeptDiceInd);
            compRollCount = 0; //reset
            KeptDiceInd.clear();
        }
        updatePlayScores();
    }


}





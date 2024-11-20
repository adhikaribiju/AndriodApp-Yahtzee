package com.example.yahtzee.View;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.Model.Combinations;
import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Scorecard;
import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.R;
import com.google.android.material.button.MaterialButton;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
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

    private boolean manualChoice = false;

    private Logger logger = Logger.getInstance();


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

    MaterialButton manualRollButton;

    private int compRollCount = 0;

    ArrayList<Integer> KeptDiceInd = new ArrayList<>();


    public interface DiceEntryCallback {
        void onDiceEntered(ArrayList<Integer> diceValues);
    }

    public interface ManualDiceCallback {
        void onDiceValuesSet(ArrayList<Integer> diceValues);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_round);

        ImageView helpButton = findViewById(R.id.helpButton);



        combinations = new Combinations();


        rollButton = findViewById(R.id.rollButton);
        //manRoll = findViewById(R.id.Manuallroll);


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
            winnerTextView.setText(winnerName + "'s Turn");

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

        handleHumanTurn();


//        if (Tournament.currentPlayerId == COMPUTER) {
//            computerPlay(KeptDiceInd);
//        } else {
//            handleHumanTurn();
//        }

        // *** MANUAL ROLL WALA****
        // Set the button click listener
//        manRoll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                rollsCount++;
//                updateRollsLeftText(rollsCount);
//                askDiceEntry(PlayRoundActivity.this, diceValues -> {
//                    // Now `diceValues` contains the validated user input
//                    rollDices(diceValues);  // Pass the values to rollDices
//                    // Keep track of the number of dice rolls here if needed
//                });
//
//                if (rollsCount > 3) {
//                    rollsCount = 0; //reset
//                    playNextTurn();
//                }
//
//                //rollDices(askDiceEntry(PlayRoundActivity.this));
//                // need to keep track of the number of dice rolls
//                //
//            }
//        });

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
                // Call playTurnt to get the categoryReceived value

                ArrayList<Integer> selectedDiceInds = new ArrayList<Integer>();
                for (int i = 0; i < diceViews.length; i++) {
                    if (isSelected[i]) {
                        selectedDiceInds.add(i); // Add the dice index (0-5)
                    }
                }

                int categoryReceived = MainActivity.tournament.round.human.playTurnt(HUMAN, Currentdice, rollsCount, selectedDiceInds);

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
                        messageBuilder.append("You may reroll all dices:");
                    }
                    else {
                        // Append the reasoning message
                        messageBuilder.append("\n\n").append(MainActivity.tournament.round.human.getReasoning());
                    }
                } else {
                    messageBuilder.append("You may score at Category: ").append(combinations.getCategoryName(categoryReceived));
                    messageBuilder.append("\n\n").append(MainActivity.tournament.round.human.getReasoning());
                }



                logger.log("Computer suggested: "+ messageBuilder.toString());

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

    public void handleHumanTurn() {

        // Set the button click listener
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unhighlightBoard();

                if (Tournament.currentPlayerId == COMPUTER) {



                    if(manualChoice){
                        manualInputDialog(KeptDiceInd, newDice -> {
                            // Roll dice with manually entered values
                            Currentdice = new ArrayList<>(newDice);
                            logger.log("Computer rolled\n" + Currentdice.toString());
                            computerRoll();
                        });
                    }
                    else{
                        ArrayList<Integer> dice = generateDice(KeptDiceInd); // Important ** DO NOT MESS //
                        logger.log("Computer rolled\n" + dice.toString());
                        computerRoll();
                    }

                    return;
//                    compRollCount++;
//                    updateRollsLeftText(compRollCount);
//                    if (compRollCount == 1) computerPlay(KeptDiceInd);
//                    if (compRollCount == 2) computerSecondRoll(KeptDiceInd);
//                    if (compRollCount == 3) {
//                        computerThirdRoll(KeptDiceInd);
//                        compRollCount = 0; //reset
//                        KeptDiceInd.clear();
//                    }
//                    updatePlayScores();
//                    return;



                }

                compRollCount = 0; //reset
                KeptDiceInd.clear();


                // Clear previously selected dice values
                //selectedDiceInd.clear();

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

                StringBuilder messageBuilder = new StringBuilder();
                ArrayList<Integer> humanKeptDices = new ArrayList<>(new HashSet<>(selectedDiceInd));
                if (!selectedDiceInd.isEmpty()){
                    for (int index : humanKeptDices) {
                        messageBuilder.append(Currentdice.get(index)).append(" ");
                    }
                    logger.log("Human kept dices: \n" +messageBuilder.toString());
                }

                if(manualChoice){
                    manualInputDialog(selectedDiceInd, newDice -> {
                        // Roll dice with manually entered values
                        Currentdice = new ArrayList<>(newDice);
                        logger.log("Human rolled\n "+ Currentdice.toString());
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
                scoreTextView.setText(String.valueOf(combinations.getScore(categoryIndex)));
            }

            // Highlight player box
            String playerId = "player" + adjustedIndex;
            int playerResID = getResources().getIdentifier(playerId, "id", getPackageName());
            TextView playerTextView = findViewById(playerResID);
            if (playerTextView != null) {
                playerTextView.setBackgroundColor(highlightColor);
               playerTextView.setText(String.valueOf(Tournament.currentPlayerId));
            }

            // Highlight round box
            String roundId = "r" + adjustedIndex;
            int roundResID = getResources().getIdentifier(roundId, "id", getPackageName());
            TextView roundTextView = findViewById(roundResID);
            if (roundTextView != null) {
                roundTextView.setBackgroundColor(highlightColor);
                roundTextView.setText(String.valueOf(MainActivity.tournament.round.getRoundNo()));
            }
        }

    }


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
                        //*Toast.makeText(context, "Please enter values between 1 and 6.", Toast.LENGTH_SHORT).show();
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





    private void rollDices(ArrayList<Integer> dice) {

        //updatePlayScores();

        int numRolls = 1; // to track the number of rolls

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

        ArrayList<Integer> potentialCategories = new ArrayList<Integer>(MainActivity.tournament.round.human.potentialCategories(Currentdice,rollsCount));


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
                        textScore.setText(String.valueOf(score));

                        //playerScoredText.setText(String.valueOf(Tournament.currentPlayerId));
                        playerScoredText.setText(String.valueOf(HUMAN));

                        roundNoText.setText(String.valueOf(MainActivity.tournament.round.getRoundNo()));

                        // update the scorecard.....

                        MainActivity.tournament.round.playTurn(HUMAN, dice, categoryNumber + 1);

                        logger.log("Human scored "+ String.valueOf(score) + " on "+ combinations.getCategoryName(categoryNumber));

                        rollsCount = 0; //reset

                        TextView rollLeftText = findViewById(R.id.rollLeftText);
                        rollLeftText.setText("No Rolls Left");

                        handleGameEnd(); // check if scorecard filled

                        playNextTurn();
                    }

                });
            }
        }

    }

    public void playNextTurn() {

        handleGameEnd();
        ImageView helpButton = findViewById(R.id.helpButton);

        String playerName = (Tournament.currentPlayerId  == 1) ? "Human" : "Computer";

        logger.log(playerName + "'s Turn Ended!");

        initBoard();
        // find out who plays next, accordingly update the round number and turn's field
        MainActivity.tournament.round.playRoundt(Tournament.currentPlayerId);
        Tournament.currentPlayerId = (MainActivity.tournament.round.findNextPlayer(Tournament.currentPlayerId));

        // by this point, i should have new round num or new player: update those
        updateUIPlayerRoundNo();

        // updateRollsLeftText(4);
        initDiceBorders();
        updatePlayScores();

        if (Tournament.currentPlayerId == 1) helpButton.setVisibility(View.INVISIBLE);
        if (Tournament.currentPlayerId == 2) helpButton.setVisibility(View.INVISIBLE);

        playerName = (Tournament.currentPlayerId  == 1) ? "Human" : "Computer";
        logger.log(playerName+ "'s Turn Started!");


    }

    public void updateUIPlayerRoundNo() {

        TextView winnerTextView = findViewById(R.id.playerTurnText);

        String player = (Tournament.currentPlayerId == 1) ? "Human" : "Computer";

        // Display a message indicating which player won the toss
        winnerTextView.setText(player + "'s Turn");


        TextView RoundNo = findViewById(R.id.roundNumberText);

        // Display a message indicating which player won the toss
        RoundNo.setText("Round No: " + MainActivity.tournament.round.getRoundNo());
    }


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
                scoreButton.setText("Score");       // Set text to "Available"
                scoreButton.setClickable(true);        // Make the button unclickable
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
            logger.log(winnerMessage);

            // Show a dialog with the winner information
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(winnerMessage)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Reset Tournament and redirect to MainActivity
                        MainActivity.tournament.round.resetRound();
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

    private void initDiceBorders() {
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setBackgroundResource(R.drawable.default_border); // Reset border to default
            isSelected[i] = false; // Reset selected state
            isKept[i] = false; // Reset kept state
        }
        //***Toast.makeText(this, "Dice selection has been reset.", Toast.LENGTH_SHORT).show();
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








    public void computerPlay(ArrayList<Integer> keptDiceInd) {

        //Combinations combinations = new Combinations();
        // Make Help and Save Game Buttons Invisible

        //ArrayList<Integer> keptDiceInd = new ArrayList<>();



        ImageView saveGameButton = findViewById(R.id.saveGameButton);
        ImageView helpButton = findViewById(R.id.helpButton);

       // helpButton.setVisibility(View.INVISIBLE);

        // Roll the dice
        //ArrayList<Integer> diceVals = generateDice(new ArrayList<>());


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
                scoreButton.setText("Available");       // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button unclickable
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




                if (!keptDiceInd.isEmpty()) {messageBuilder.append("\n\n").append(MainActivity.tournament.round.getReasoning());}

                logger.log(messageBuilder.toString());

                new AlertDialog.Builder(this) // Replace with your activity's context
                        .setTitle("Computer's Decision")
                        .setMessage(messageBuilder.toString().trim()) // Display the dynamic message
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss(); // Dismiss the dialog
                            //computerSecondRoll(keptDiceInd);


                        })
                        .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                        .show();
            }

            // Make the buttons visible again after scoring
            //saveGameButton.setVisibility(View.VISIBLE);
            //helpButton.setVisibility(View.VISIBLE);
        }, 1000); // 1-second delay

        //saveGameButton.setVisibility(View.VISIBLE);
        //  helpButton.setVisibility(View.VISIBLE);
    }

    public boolean computerFirstRoll(ArrayList<Integer> diceVals, ArrayList<Integer> keptDiceInd) {
        // Keep dice indices (if any logic applies)
        //ArrayList<Integer> keptDiceInd = new ArrayList<>();
        displayPotentialComputer(0);

        int categoryReceived = MainActivity.tournament.round.playTurnComputer(COMPUTER, diceVals, 0, keptDiceInd);
        if (categoryReceived == -1) {
            return false;
        } else {
            // Show a dialog before updating the scorecard

            String categoryName = "Category " + (categoryReceived + 1); // Replace with actual category name if available

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
        textScore.setText(String.valueOf(score));

        playerScoredText.setText(String.valueOf(Tournament.currentPlayerId));

        roundNoText.setText(String.valueOf(MainActivity.tournament.round.getRoundNo()));


        // Call the next turn logic
        playNextTurn();
    }

    public void computerSecondRoll(ArrayList<Integer> keptDiceInd) {

        initBoard();
        displayPotentialComputer(1);

        ArrayList<Integer> previousKeptDiceInd = new ArrayList<>(keptDiceInd);
        //keptDiceInd.clear();


        ImageView saveGameButton = findViewById(R.id.saveGameButton);
        ImageView helpButton = findViewById(R.id.helpButton);

        // Roll the dice
        //ArrayList<Integer> diceVals = generateDice(previousKeptDiceInd);
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
                scoreButton.setText("Available");       // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button unclickable
            }
        }
        highlightBoard();


        int categoryReceived = MainActivity.tournament.round.playTurnComputer(COMPUTER, Currentdice, 1, keptDiceInd);

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



                if (!keptDiceInd.isEmpty()) {messageBuilder.append("\n\n").append(MainActivity.tournament.round.getReasoning());}

                logger.log(messageBuilder.toString());
                new AlertDialog.Builder(this) // Replace with your activity's context
                        .setTitle("Computer's Decision")
                        .setMessage(messageBuilder.toString().trim()) // Display the dynamic message
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss(); // Dismiss the dialog
                            //computerThirdRoll(keptDiceInd);

                            //playNextTurn();   // Call the next turn logic
                        })
                        .setCancelable(false) // Prevent dismissing the dialog without clicking OK
                        .show();
            } else {
                // Show a dialog before updating the scorecard

                String categoryName = "Category " + (categoryReceived + 1); // Replace with actual category name if available

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
            //helpButton.setVisibility(View.VISIBLE);
        }, 1000); // 1-second delay

    }


    public void computerThirdRoll(ArrayList<Integer> keptDiceInd) {

        initBoard();
        displayPotentialComputer(2);

        ImageView saveGameButton = findViewById(R.id.saveGameButton);
        ImageView helpButton = findViewById(R.id.helpButton);
        // Roll the dice
        //ArrayList<Integer> diceVals = generateDice(keptDiceInd);
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
                scoreButton.setText("Available");       // Set text to "Available"
                scoreButton.setClickable(false);        // Make the button unclickable
            }
        }
        highlightBoard();

        int categoryReceived = MainActivity.tournament.round.playTurnComputer(COMPUTER, Currentdice, 2, keptDiceInd);
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
            // Show a dialog before updating the scorecard

            String categoryName = "Category " + (categoryReceived + 1); // Replace with actual category name if available

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

    public void updatePlayScores() {
        TextView humanScoreTotal = findViewById(R.id.humanScoreTotal);
        TextView computerScoreTotal = findViewById(R.id.computerScoreTotal);
        humanScoreTotal.setText(String.valueOf(combinations.getTotal(HUMAN)));
        computerScoreTotal.setText(String.valueOf(combinations.getTotal(COMPUTER)));
    }

    public boolean loadFileAndRead(String fileName) {
        try {
            // Locate the file in the internal Downloads directory
            File downloadsDir = new File(getFilesDir(), "Downloads");
            File fileToLoad = new File(downloadsDir, fileName);

            // Check if the file exists
            if (!fileToLoad.exists()) {
                //Log.e("LoadFile", "File not found in Downloads directory: " + fileToLoad.getAbsolutePath());
                return false;
            }

            // Open the file and create a BufferedReader
            BufferedReader fileReader = new BufferedReader(new FileReader(fileToLoad));

            // Call the readFile method in your model and pass the BufferedReader
            return MainActivity.tournament.loadTournament(fileReader);

        } catch (IOException e) {
            e.printStackTrace();
            //Log.e("LoadFile", "Failed to load file: " + fileName, e);
            return false;
        }
    }


    public void loadedScorecard() {
        int maxCategoryNo = 11;

        Combinations scorecard = new Combinations();
        for (int i = 0; i <= maxCategoryNo; i++) {
            int score = scorecard.getSetCategoryScore(i);; // Get the score for the category

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
                textScore.setText(String.valueOf(score));

                // Update the player who scored
                int currentPlayerId = scorecard.getSetPlayerId(i);
                String playerName = (currentPlayerId == 1) ? "1" : "2";
                playerScoredText.setText(playerName);

                // Update the round number
                int roundNo = scorecard.getSetRoundNo(i);
                roundNoText.setText(String.valueOf(roundNo));
            } else {
                // Log a warning if any of the views are missing
                //Log.w("loadedScorecard", "Missing TextView for category: " + (i + 1));
            }

            TextView humanScoreTotal = findViewById(R.id.humanScoreTotal);
            TextView computerScoreTotal = findViewById(R.id.computerScoreTotal);
            humanScoreTotal.setText(String.valueOf(scorecard.getTotal(HUMAN)));
            computerScoreTotal.setText(String.valueOf(scorecard.getTotal(COMPUTER)));
        }
    }

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
                label.setText("\n  Dice " + (i + 1) + ":");

                RadioGroup group = new RadioGroup(this);
                group.setOrientation(RadioGroup.HORIZONTAL);

                for (int j = 1; j <= 6; j++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(String.valueOf(j));
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

    public void displayPotentialComputer(int rollCount){

        ArrayList<Integer> potentialCategories = new ArrayList<Integer>(MainActivity.tournament.round.computer.potentialCategories(Currentdice,rollCount));


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





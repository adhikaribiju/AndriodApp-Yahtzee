package com.example.yahtzee.Model;
import java.util.ArrayList;


public abstract class Player {

    /**
     * protected data members
     */

    protected final int DICE_COUNT = 5;   // Number of dice in the game

    protected int player_id;              // 1 for human, 2 for computer
    protected int num_rolls;
    protected char player_choice;

    protected ArrayList<Integer> dice;    // List to store dice values
    protected ArrayList<Integer> combinations; // List to store available unsocred categories as per dice

    /**
     * Default constructor
     */
    public Player() {
        player_id = 0;
        num_rolls = 1;
        player_choice = ' ';
        dice = new ArrayList<>(DICE_COUNT);

        // Initializing dice values to 0
        for (int i = 0; i < DICE_COUNT; i++) {
            dice.add(0);
        }
    }

    /**
     * Constructor with player id
     */
    public Player(int p_id) {
        this();
        player_id = p_id;
    }



    /**
     * Abstract method to play a turn, to be implemented by subclasses
     * @param player_id (int) The player id
     * @param dice (int) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (int) The indices of the dice kept
     * @return The category chosen by the player
     */
    public abstract int playTurn(int player_id, ArrayList<Integer> dice, int rollCount, ArrayList<Integer> keptDiceInd);


}

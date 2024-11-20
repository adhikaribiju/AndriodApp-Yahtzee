package com.example.yahtzee.Model;
import java.util.ArrayList;


public class Round {

    /**
     * private data members
     */
    private static int numOfRounds = 1; // static variable to track the number of rounds

    private final int NUM_OF_DICE = 5; // constant for the number of dice


    private ArrayList<Integer> dice;

    private static Integer NUM_OF_TURNS = 0;

    private Human human; // Create a human player object
    private Computer computer; // Create a computer player object

    private Combinations scorecard = new Combinations();


    /**
     * Default constructor for the Round class.
     */
    public Round() {
        human = new Human();
        computer = new Computer();


        dice = new ArrayList<>();

        // Initialize dice values to 0
        for (int i = 0; i < NUM_OF_DICE; i++) {
            dice.add(0);
        }
    }

    /**
     * setters and getters
     */
    public Human getHuman() {
        return human;
    }

    public Computer getComputer(){
        return computer;
    }

    // Getter function to return the number of rounds played
    public static int getRoundNo() {
        return numOfRounds;
    }

    public static void setNumOfRounds(int round_no){
        numOfRounds = round_no;
    }

    /**
     * Method to play a round of the game.
     */
    public void playRoundt(){
        NUM_OF_TURNS++;
    }


    /**
     * Method to find the next player to play.
     * @param currentPlayerID The ID of the current player.
     * @return The ID of the next player.
     */
    public int findNextPlayer(int currentPlayerID){

        if (NUM_OF_TURNS % 2 == 0 ){
            // increase the round number
            // return the player_id with the lowest score
            numOfRounds++;
            return scorecard.playerWithLowestScore();
        }
        else{
            return (currentPlayerID == 1) ? 2 : 1;  // return the other player
        }
    }

    /**
     * Method to play a turn in the game.
     * @param player_id (int) The ID of the player.
     * @param dice (ArrayList<Integer>) The list of dice values.
     * @param category_choice (int) The category chosen by the player.
     * @return The score of the player.
     */
    public int playTurn(int player_id, ArrayList<Integer> dice, int category_choice)
    {
        human.playTurn(player_id, dice, category_choice);
        return 0;
    }


    /**
     * Method to play a turn for the computer player.
     * @param player_id (int) The ID of the player.
     * @param dice (ArrayList<Integer>) The list of dice values.
     * @param rollCount (int) The number of rolls.
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept by the player.
     * @return The score of the player.
     */
    public int playTurnComputer(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd){

        // based on the dice combination, find out the available combinations
        // if there are available combinations, find out the highest one, score it and return the category number
        // if not, return -1

        return computer.playTurn(player_id, dice,rollCount,keptDiceInd);

    }

    /**
     * Method to get the score of the human player.
     * @return The score of the human player.
     */
    public String getReasoning(){
        return computer.getReasoningMsg();
    }

    /**
     * Method to reset the round.
     */
    public void resetRound(){
        numOfRounds=0;
        NUM_OF_TURNS = 0;
    }

}

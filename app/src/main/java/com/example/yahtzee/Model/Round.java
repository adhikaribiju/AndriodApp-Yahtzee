package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.Scanner;

public class Round {

    public static int numOfRounds = 1; // static variable to track the number of rounds

    private final int NUM_OF_DICE = 5; // constant for the number of dice
    private int player_id;
    private char player_choice;
    private int num_rolls;

    private ArrayList<Integer> dice;

    public static Integer NUM_OF_TURNS = 0;

    public Human human = new Human(); // Create a human player object
    public Computer computer = new Computer(); // Create a computer player object

    private Combinations scorecard = new Combinations();

    public Round() {
        player_id = 0;
        player_choice = ' ';
        num_rolls = 1;
        dice = new ArrayList<>();




        // Initialize dice values to 0
        for (int i = 0; i < NUM_OF_DICE; i++) {
            dice.add(0);
        }
    }

    public void playRoundt(int player_id){


       // human.playTurnt();

        if (player_id == 1) {
            NUM_OF_TURNS++;
           // human.playTurn();
        } else {
            NUM_OF_TURNS++;
           //computer.playTurn();
        }

    }


    public int findNextPlayer(int currentPlayerID){

        if (NUM_OF_TURNS % 2 == 0 ){
            // increase the round number
            // return the player_id with the lowest score
            //
            numOfRounds++;
            return scorecard.playerWithLowestScore();
        }
        else{
            return (currentPlayerID == 1) ? 2 : 1;  // return the other player
        }
    }

    // Need to add numOfReRolls and KeptDice Array
    public int playTurn(int player_id, ArrayList<Integer> dice, int category_choice)
    {
        human.playTurn(player_id, dice, category_choice);
        return 0;
    }






    // Starts a round consisting of two turns
    public void playRound(int player_id) {
        Human human = new Human(); // Create a human player object
        Computer computer = new Computer(); // Create a computer player object

        //System.out.println("\nRound " + numOfRounds);

        if (player_id == 1) {
            human.playTurn();
            computer.playTurn();
        } else {
            computer.playTurn();
            human.playTurn();
        }

//        if (isSaveGame()) {
//            Serialization serialize = new Serialization();
//            if (serialize.saveGame()) {
//                System.out.println("Game saved!");
//                System.exit(0); // Exit the program
//            }
//        }

       // numOfRounds++;
    }

    // Resumes a round consisting of two turns
    public void resumeRound(int player_id) {
        Human human = new Human();
        Computer computer = new Computer();
        Combinations c = new Combinations();

        System.out.println("\nRound " + numOfRounds);

        if (player_id == 1) {
            human.playTurn();
            if (!c.isScorecardFull()) computer.playTurn();
        } else {
            computer.playTurn();
            if (!c.isScorecardFull()) human.playTurn();
        }

        if (isSaveGame()) {
            Serialization serialize = new Serialization();
            if (serialize.saveGame()) {
                System.out.println("Game saved!");
                System.exit(0); // Exit the program
            }
        }

        numOfRounds++;
    }

    // Finds the index of the dice to keep
    public int diceIndex(int dice_to_keep) {
        for (int i = 0; i < NUM_OF_DICE; i++) {
            if (dice.get(i) == dice_to_keep) {
                return i;
            }
        }
        return -1;
    }

    // Displays the dice values
    public void displayDice() {
        for (int i = 0; i < NUM_OF_DICE; i++) {
            System.out.println("Dice " + (i + 1) + ": " + dice.get(i));
        }
        System.out.println();
    }

    // Getter function to return the number of rounds played
    public int getRoundNo() {
        return numOfRounds;
    }

    // Checks if the user wants to save the game
    public boolean isSaveGame() {
        Scanner scanner = new Scanner(System.in);
        String save_game;

        // Loop until a valid response is provided
        while (true) {
            System.out.print("Do you wish to save the game? (Y/N): ");
            save_game = scanner.nextLine().trim();

            if (save_game.length() == 1) {
                char answer = Character.toLowerCase(save_game.charAt(0));

                if (answer == 'y') {
                    System.out.println("Saving now...");
                    return true;
                } else if (answer == 'n') {
                    return false;
                }
            }

            System.out.println("Invalid input. Please enter Y/N only.");
        }
    }
}

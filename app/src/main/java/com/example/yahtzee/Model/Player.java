package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public abstract class Player {

    protected final int MAX_ROLLS = 3;    // Maximum number of rolls allowed
    protected final int DICE_COUNT = 5;   // Number of dice in the game

    protected int player_id;              // 1 for human, 2 for computer
    protected int num_rolls;
    protected char player_choice;

    protected ArrayList<Integer> dice;    // List to store dice values
    protected ArrayList<Integer> combinations; // List to store available unsocred categories as per dice

    // Default constructor
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

    // Parameterized constructor
    public Player(int p_id) {
        this();
        player_id = p_id;
    }

    // Abstract method to play a turn, to be implemented by subclasses
    public abstract void playTurn();

    // Rolls the dice, either manually or randomly
    public void playRoll() {
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you wish to manually input all dices? (Y/N)");
        String choice = scanner.nextLine().trim();

        if (choice.equalsIgnoreCase("Y")) {
            // Manually enter dice values
            for (int i = 0; i < DICE_COUNT; i++) {
                int value;
                do {
                    System.out.print("Enter the value for dice " + (i + 1) + ": ");
                    value = scanner.nextInt();
                    if (value < 1 || value > 6) {
                        System.out.println("Invalid entry! Enter values from 1-6.");
                    }
                } while (value < 1 || value > 6);
                dice.set(i, value);
            }
        } else if (choice.equalsIgnoreCase("N")) {
            // Generate random dice values
            System.out.println("Rolling the dice randomly...");
            for (int i = 0; i < DICE_COUNT; i++) {
                dice.set(i, rand.nextInt(6) + 1);
            }
        } else {
            System.out.println("Invalid entry! Enter Y or N.");
        }
    }

    // Displays the dice values
    public void displayDice() {
        System.out.print("Dice: ");
        for (int value : dice) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    // Finds the indices of dice to keep based on a given value
    public ArrayList<Integer> diceIndex(int dice_to_reroll) {
        ArrayList<Integer> recordIndices = new ArrayList<>();
        for (int i = 0; i < DICE_COUNT; i++) {
            if (dice.get(i) == dice_to_reroll) {
                recordIndices.add(i);
            }
        }
        return recordIndices;
    }
}

package com.example.yahtzee.Model;

import java.util.ArrayList;
import java.util.Collections;

public class Combinations extends Scorecard {

    private final int DICE_COUNT = 5;
    private final int MAX_DICE_VALUE = 6;

    private ArrayList<Integer> dice; // Stores the dice values
    private ArrayList<Integer> available_combinations; // Stores available combinations
    private int[] counts; // Frequency counts of dice values (indices 1-6)

    // Default Constructor
    public Combinations() {
        dice = new ArrayList<>(Collections.nCopies(DICE_COUNT, 0));
        available_combinations = new ArrayList<>();
        counts = new int[MAX_DICE_VALUE + 1]; // Frequency counts from 1 to 6
    }

    // Constructor that accepts dice values
    public Combinations(ArrayList<Integer> dice_values) {
        this();
        dice = new ArrayList<>(dice_values);
    }

    // Method to check if there are three of a kind
    public boolean hasThreeOfAKind() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    // Method to check if there are four of a kind
    public boolean hasFourOfAKind() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] >= 4) {
                return true;
            }
        }
        return false;
    }

    // Method to check if there is a Yahtzee (five of a kind)
    public boolean hasYahtzee() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] == 5) {
                return true;
            }
        }
        return false;
    }

    // Method to check if there is a full house
    public boolean hasFullHouse() {
        boolean hasThree = false;
        boolean hasTwo = false;
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] == 3) {
                hasThree = true;
            } else if (counts[i] == 2) {
                hasTwo = true;
            }
        }
        return (hasThree && hasTwo); // Yahtzee counts as a full house
    }

    // Method to check for a four straight (small straight)
    public boolean hasFourStraight() {
        return (counts[1] >= 1 && counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1) ||
               (counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1) ||
               (counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1 && counts[6] >= 1);
    }

    // Method to check for a five straight (large straight)
    public boolean hasFiveStraight() {
        return (counts[1] >= 1 && counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1) ||
               (counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1 && counts[6] >= 1);
    }

    // Calculate score for a specific number in the upper section
    public int calculateUpperSectionScore(int number) {
        int sum = 0;
        for (int die : dice) {
            if (die == number) {
                sum += die;
            }
        }
        return sum;
    }

    // Sum of all dice values
    public int sumAllDice() {
        int total = 0;
        for (int die : dice) {
            total += die;
        }
        return total;
    }

    // Determine available combinations based on current dice values
    public ArrayList<Integer> availableCombinations() {
        available_combinations.clear();
        countDiceFace();

        // Upper Section (Aces to Sixes)
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            int score = calculateUpperSectionScore(i);
            if (score > 0) {
                available_combinations.add(i - 1);
            }
        }

        if (hasThreeOfAKind()) available_combinations.add(6);
        if (hasFourOfAKind()) available_combinations.add(7);
        if (hasFullHouse()) available_combinations.add(8);
        if (hasFourStraight()) available_combinations.add(9);
        if (hasFiveStraight()) available_combinations.add(10);
        if (hasYahtzee()) available_combinations.add(11);

        return available_combinations;
    }

    public ArrayList<Integer> availableToScoreCategories() {
        available_combinations.clear();
        countDiceFace();

        // Display categories and score options
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            int score = calculateUpperSectionScore(i);
            if (score != 0 && !isCategoryFill(i - 1)) {
                available_combinations.add(i - 1);
                //System.out.println("Category No. " + i + ": Score for " + scoreCard.get(i - 1).name + " : " + score);
            }
        }

        int totalDice = sumAllDice();
        if (hasThreeOfAKind() && !isCategoryFill(6)) available_combinations.add(6);
        if (hasFourOfAKind() && !isCategoryFill(7)) available_combinations.add(7);
        if (hasFullHouse() && !isCategoryFill(8)) available_combinations.add(8);
        if (hasFourStraight() && !isCategoryFill(9)) available_combinations.add(9);
        if (hasFiveStraight() && !isCategoryFill(10)) available_combinations.add(10);
        if (hasYahtzee() && !isCategoryFill(11)) available_combinations.add(11);

        return available_combinations;
    }




    // Display available combinations for scoring
    public void displayAvailableCombinations() {
        countDiceFace();
        System.out.print("Dice: ");
        for (int die : dice) {
            System.out.print(die + " ");
        }
        System.out.println("\n\nPotential categories to score (if any):\n");

        // Display categories and score options
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            int score = calculateUpperSectionScore(i);
            if (score != 0 && !isCategoryFill(i - 1)) {
                System.out.println("Category No. " + i + ": Score for " + scoreCard.get(i - 1).name + " : " + score);
            }
        }

        int totalDice = sumAllDice();
        if (hasThreeOfAKind() && !isCategoryFill(6)) System.out.println("Category No. 7: Three of a Kind! Score: " + totalDice);
        if (hasFourOfAKind() && !isCategoryFill(7)) System.out.println("Category No. 8: Four of a Kind! Score: " + totalDice);
        if (hasFullHouse() && !isCategoryFill(8)) System.out.println("Category No. 9: Full House! Score: 25");
        if (hasFourStraight() && !isCategoryFill(9)) System.out.println("Category No. 10: Small Straight! Score: 30");
        if (hasFiveStraight() && !isCategoryFill(10)) System.out.println("Category No. 11: Large Straight! Score: 40");
        if (hasYahtzee() && !isCategoryFill(11)) System.out.println("Category No. 12: Yahtzee! Score: 50");
    }

    // Update dice values
    public void updateDice(ArrayList<Integer> dice_values) {
        dice.clear();
        dice.addAll(dice_values);

        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            counts[i] = 0;
        }
    }

    // Count occurrences of each die face
    public void countDiceFace() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            counts[i] = 0;
        }
        for (int die : dice) {
            if (die >= 1 && die <= 6) {
                counts[die]++;
            }
        }
    }

    // Set score for a category
    public boolean setScore(int category, int player_id) {
        int score = 0;

        switch (category) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                score = calculateUpperSectionScore(category + 1);
                break;
            case 6:
                if (hasThreeOfAKind()) score = sumAllDice();
                break;
            case 7:
                if (hasFourOfAKind()) score = sumAllDice();
                break;
            case 8:
                if (hasFullHouse()) score = 25;
                break;
            case 9:
                if (hasFourStraight()) score = 30;
                break;
            case 10:
                if (hasFiveStraight()) score = 40;
                break;
            case 11:
                if (hasYahtzee()) score = 50;
                break;
            default:
                return false;
        }

        if (score > 0) {
            scoreCard.get(category).score = score;
            scoreCard.get(category).player_id = player_id;
            scoreCard.get(category).round_no = Round.numOfRounds;
            System.out.println("\nScored : " + score + " points at the category " + scoreCard.get(category).name);
            return true;
        }
        return false;
    }

    // Check if there are available scoreable categories
    public boolean hasScoreableCategory() {
        ArrayList<Integer> available = availableCombinations();
        for (int category : available) {
            if (scoreCard.get(category).score == 0) {
                return true;
            }
        }
        return false;
    }

    public Integer getScore(int category) {
        int score = 0;
        countDiceFace();
        switch (category) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                score = calculateUpperSectionScore(category + 1);
                break;
            case 6:
                if (hasThreeOfAKind()) score = sumAllDice();
                break;
            case 7:
                if (hasFourOfAKind()) score = sumAllDice();
                break;
            case 8:
                if (hasFullHouse()) score = 25;
                break;
            case 9:
                if (hasFourStraight()) score = 30;
                break;
            case 10:
                if (hasFiveStraight()) score = 40;
                break;
            case 11:
                if (hasYahtzee()) score = 50;
                break;
            default:
                return score;
        }
        return score;
    }

    public String getCategoryName(int categoryNum) {
        switch (categoryNum) {
            case 0:
                return "Ones";
            case 1:
                return "Twos";
            case 2:
                return "Threes";
            case 3:
                return "Fours";
            case 4:
                return "Fives";
            case 5:
                return "Sixes";
            case 6:
                return "Three of a Kind";
            case 7:
                return "Four of a Kind";
            case 8:
                return "Full House";
            case 9:
                return "Four Straight";
            case 10:
                return "Five Straight";
            case 11:
                return "Yahtzee";
            default:
                return "Unknown Category"; // Handle invalid category numbers
        }
    }




}

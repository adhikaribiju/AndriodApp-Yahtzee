package com.example.yahtzee.Model;

import java.util.ArrayList;
import java.util.Collections;

public class Combinations extends Scorecard {


    /**
     * private data members
     */

    private final int DICE_COUNT = 5;
    private final int MAX_DICE_VALUE = 6;

    private ArrayList<Integer> dice; // Stores the dice values
    private ArrayList<Integer> available_combinations; // Stores available combinations
    private int[] counts; // Frequency counts of dice values (indices 1-6)

    /**
     * Default constructor
     */
    public Combinations() {
        dice = new ArrayList<>(Collections.nCopies(DICE_COUNT, 0));
        available_combinations = new ArrayList<>();
        counts = new int[MAX_DICE_VALUE + 1]; // Frequency counts from 1 to 6
    }

    /**
     * Constructor with dice values as parameter ArrayList<Integer>
     */
    public Combinations(ArrayList<Integer> dice_values) {
        this();
        dice = new ArrayList<>(dice_values);
    }

    /**
     * Checks if there is three of a kind on the current dice values
     * @return true if there is three of a kind, false otherwise
     */
    public boolean hasThreeOfAKind() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is four of a kind on the current dice values
     * @return true if there is four of a kind, false otherwise
     */
    public boolean hasFourOfAKind() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] >= 4) {
                return true;
            }
        }
        return false;
    }

   /**
     * Checks if there is a Yahtzee on the current dice values
     * @return true if there is a Yahtzee, false otherwise
     */
    public boolean hasYahtzee() {
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            if (counts[i] == 5) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a full house on the current dice values
     * @return true if there is a full house, false otherwise
     */
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

    /**
     * Checks if there is a four straight on the current dice values
     * @return true if there is a four straight, false otherwise
     */
    public boolean hasFourStraight() {
        return (counts[1] >= 1 && counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1) ||
               (counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1) ||
               (counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1 && counts[6] >= 1);
    }

    /**
     * Checks if there is a five straight on the current dice values
     * @return true if there is a five straight, false otherwise
     */
    public boolean hasFiveStraight() {
        return (counts[1] >= 1 && counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1) ||
               (counts[2] >= 1 && counts[3] >= 1 && counts[4] >= 1 && counts[5] >= 1 && counts[6] >= 1);
    }

    /**
     * Calculate the score for the upper section (Aces to Sixes)
     * @param number The integer to calculate the score for (1-6) in the scorecard
     * @return : The score for the number
     */
    public int calculateUpperSectionScore(int number) {
        int sum = 0;
        for (int die : dice) {
            if (die == number) {
                sum += die;
            }
        }
        return sum;
    }

    /**
     * Calculate the total sum of all dice values
     * @return : The total sum of all dice values as an integer
     */
    public int sumAllDice() {
        int total = 0;
        for (int die : dice) {
            total += die;
        }
        return total;
    }

    /**
     * Find available combinations for the current dice values
     * @return : An ArrayList of integers representing the available combinations
     */
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


    /**
     * Find available combinations for the current dice values
     * @return : An ArrayList of integers representing the available combinations
     */
    public ArrayList<Integer> availableToScoreCategories() {
        available_combinations.clear();
        countDiceFace();

        // Display categories and score options
        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            int score = calculateUpperSectionScore(i);
            if (score != 0 && !isCategoryFill(i - 1)) {
                available_combinations.add(i - 1);
            }
        }

        if (hasThreeOfAKind() && !isCategoryFill(6)) available_combinations.add(6);
        if (hasFourOfAKind() && !isCategoryFill(7)) available_combinations.add(7);
        if (hasFullHouse() && !isCategoryFill(8)) available_combinations.add(8);
        if (hasFourStraight() && !isCategoryFill(9)) available_combinations.add(9);
        if (hasFiveStraight() && !isCategoryFill(10)) available_combinations.add(10);
        if (hasYahtzee() && !isCategoryFill(11)) available_combinations.add(11);

        return available_combinations;
    }





    /**
     * Update the dice values in the Combinations class
     * @param dice_values : An ArrayList of integers representing the dice values
     */
    public void updateDice(ArrayList<Integer> dice_values) {
        dice.clear();
        dice.addAll(dice_values);

        for (int i = 1; i <= MAX_DICE_VALUE; i++) {
            counts[i] = 0;
        }
    }

   /**
     * Count the frequency of each dice face
     */
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

    /**
     * Set the score for a given category and player_id on the scorecard
     * @param category The category to set the score for as an integer
     * @param player_id The player id to set the score for as an integer
     * @return : True if the score was set successfully, false otherwise
     */
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
            scoreCard.get(category).round_no = Round.getRoundNo();
            System.out.println("\nScored : " + score + " points at the category " + scoreCard.get(category).name);
            return true;
        }
        return false;
    }

    /**
     * Check if there is a scoreable category available on the scorecard
     * @return : True if there is a scoreable category available, false otherwise
     */
    public boolean hasScoreableCategory() {
        ArrayList<Integer> available = availableCombinations();
        for (int category : available) {
            if (scoreCard.get(category).score == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the score for a given category
     * @param category The category to get the score for as an integer
     * @return : The score for the category as an integer
     */
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

    /**
     * Get the name of a category based on the category number
     * @param categoryNum The category number as an integer
     * @return : The name of the category as a string
     */
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

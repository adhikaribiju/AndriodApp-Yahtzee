package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Human extends Player {

    /**
     * private data members
     */
	 private class Pair<K, V> {
	    public K first;
	    public V second;

	    public Pair(K first, V second) {
	        this.first = first;
	        this.second = second;
	    }
	}

    private final int HUMAN = 1;

    private final int KEEP_ERROR = -99;

    private ArrayList<Pair<Integer, Integer>> availableScores = new ArrayList<>();

    private String reasoningMsg;

    private String tempMsg;

    private ArrayList<Integer> indicesAvailableToKeep;
    private ArrayList<Human.Pair<Integer, Integer>> scoresAvailable = new ArrayList<>();

    private ArrayList<Integer> currentKeptDiceInd;
    private ArrayList<Integer> availableCategory = new ArrayList<>();

    private Combinations board = new Combinations();

    /**
     * Default constructor
     */
    public Human() {
        player_id = 1; // 1 for human
        reasoningMsg = "";
        tempMsg ="";
        indicesAvailableToKeep = new ArrayList<>();
        currentKeptDiceInd = new ArrayList<>();
    }



    /**
     * Method to get the reasoning message
     * @return The reasoning message in String
     */
    public String getReasoning(){
        return reasoningMsg;
    }




    /**
     * Method to play a turn for the human player
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    @Override
    public int playTurn(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd){
        // based on the dice combination, find out the available combinations
        // if there are available combinations, find out the highest one, score it and return the category number
        // if not, return -1

        tempMsg = reasoningMsg;

        currentKeptDiceInd.addAll(keptDiceInd);
        keptDiceInd.clear();

        int categoryToReturn = -1;

        board.updateDice(dice);
        availableCategory = board.availableCombinations();
        findCategoryScore();


        categoryToReturn = humanDecide(player_id, dice,rollCount,keptDiceInd);

        // check if indicesAvailableToKeep has all the values in keptDiceInd

        if (!keptDiceInd.containsAll(indicesAvailableToKeep)) {categoryToReturn = KEEP_ERROR;}

        if (categoryToReturn == KEEP_ERROR) {
            keptDiceInd.clear();
            keptDiceInd.addAll(currentKeptDiceInd);
            reasoningMsg = tempMsg;
        }

        indicesAvailableToKeep.clear();
        currentKeptDiceInd.clear();

        if (rollCount!=0){
            if (categoryToReturn== KEEP_ERROR) return -1;
            return categoryToReturn;
        }
        else {
            if (categoryToReturn == -1 || categoryToReturn== KEEP_ERROR){ //3rd roll ma cha ra kunai ni best category available chaina vane

                return scoreHighestAvailable();

            }
            else{
                return categoryToReturn;
            }
        }
    }



    /**
     * Method to find scores available in each category
     */
    public void findCategoryScore() {
        scoresAvailable.clear();

        if (!availableCategory.isEmpty()) {
            for (int i = 1; i <= DICE_COUNT+1; i++) {
                int score = board.calculateUpperSectionScore(i);
                if ((score != 0) && (!board.isCategoryFill(i-1))) {
                    scoresAvailable.add(new Human.Pair<>(i - 1, score));
                }
            }

            int totalDice = board.sumAllDice();

            if (board.hasThreeOfAKind() && !board.isCategoryFill(6)) {
                scoresAvailable.add(new Human.Pair<>(6, totalDice));
            }
            if (board.hasFourOfAKind() && !board.isCategoryFill(7)) {
                scoresAvailable.add(new Human.Pair<>(7, totalDice));
            }
            if (board.hasFullHouse() && !board.isCategoryFill(8)) {
                scoresAvailable.add(new Human.Pair<>(8, 25));
            }
            if (board.hasFourStraight() && !board.isCategoryFill(9)) {
                scoresAvailable.add(new Human.Pair<>(9, 30));
            }
            if (board.hasFiveStraight() && !board.isCategoryFill(10)) {
                scoresAvailable.add(new Human.Pair<>(10, 40));
            }
            if (board.hasYahtzee() && !board.isCategoryFill(11)) {
                scoresAvailable.add(new Human.Pair<>(11, 50));
            }
        }
    }

    /**
     * Method to decide the category to score for human help
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int humanDecide(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd) {

        indicesAvailableToKeep.clear();

        if (lowerSectionFilled()) {
            // try upper section fill
            if (!availableCategory.isEmpty()) {
                return scoreHighestAvailable();
            } else {
                return -1; // **** IMP **** No change to keptDices
            }
        }
        else {
            return checkLowerSection(player_id, dice,  rollCount,keptDiceInd);
        }
    }


    /**
     * Method to process the lower section of the scorecard to find the best category to score for the human player
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int checkLowerSection(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd){
        int[] count = new int[7];
        boolean fourOfAKind = false;
        boolean threeOfAKind = false;
        boolean twoOfAKind = false;
        int targetValueFourOfAKind = -1;
        int targetValueThreeOfAKind = -1;
        int targetValueTwoOfAKind = -1;

        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

        for (int i = 1; i < count.length; i++) {
            if (count[i] >= 4) {
                fourOfAKind = true;
                targetValueFourOfAKind = i;
            }
        }

        for (int i = 1; i < count.length; i++) {
            if (count[i] >= 3) {
                threeOfAKind = true;
                targetValueThreeOfAKind = i;
            }
        }

        if (!threeOfAKind) {
            for (int i = 1; i < count.length; i++) {
                if (count[i] >= 2) {
                    twoOfAKind = true;
                    targetValueTwoOfAKind = i;
                }
            }
        }


        if(!board.isCategoryFill(11)) {
            if (board.hasYahtzee()){
                // score it
                reasoningMsg = "Yahtzee is available to score! You may score it! ";
                return 11;
            }
            else{
                // let's try to get Yahtzee
                if(board.hasFourOfAKind()){
                    for(int i = 0; i < dice.size(); i++) {
                        if (targetValueFourOfAKind == dice.get(i)){
                            keptDiceInd.add(i);
                            indicesAvailableToKeep.add(i);
                            reasoningMsg = "Yahtzee is available and there are four of a kind on the current dice combination.";
                        }
                    }
                    return -1;
                }
                else{
                    if(board.hasThreeOfAKind()){

                        //check full house// if available scored...
                        if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                            reasoningMsg = "Full House is available to score! You may score it! ";
                            return 8;
                        }

                        for(int i = 0; i < dice.size(); i++) {
                            if (targetValueThreeOfAKind== dice.get(i)){
                                keptDiceInd.add(i);
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Yahtzee is available and there are three of a kind on the current dice combination.";
                            }
                        }
                        return -1;
                    }
                    else{
                        return trySequential(player_id, dice, rollCount, keptDiceInd);
                    }
                }
            }
        }
        else {
            // Yahtzee is not AVAILABLE

            // check if Four of a kind available to score, if yes, score it
            if(!board.isCategoryFill(7)) {
                if (board.hasFourOfAKind()){
                    // score it
                    reasoningMsg = "Four of a kind is available to score! You may score it! ";
                    return 7;
                }
                else{
                    // let's try to get Four of a kind
                    if(board.hasThreeOfAKind()){

                        //check full house// if available scored...
                        if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                            reasoningMsg = "Full House is available to score! You may score it! ";
                            return 8;
                        }

                        for(int i = 0; i < dice.size(); i++) {
                            if (targetValueThreeOfAKind== dice.get(i)){
                                keptDiceInd.add(i);
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Four of a kind is available and there are three of a kind on the current dice combination.";
                            }
                        }
                        return -1;
                    }
                    else{
                        return trySequential(player_id, dice, rollCount, keptDiceInd);
                    }
                }
            }
            else{
                // Four of a kind is not available...

                if(!board.isCategoryFill(6)) {
                    if (board.hasThreeOfAKind()){
                        // score it
                        reasoningMsg = "Three of a kind is available to score! You may score it! ";
                        return 6;
                    }
                    else{
                        if(targetValueTwoOfAKind!=-1){
                            for(int i = 0; i < dice.size(); i++) {

                                if (targetValueTwoOfAKind == dice.get(i)) {
                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Three of a kind is available and there are two of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        }
                        else {
                            return trySequential(player_id, dice, rollCount, keptDiceInd);
                        }
                    }
                }
                else {
                    return trySequential(player_id, dice, rollCount, keptDiceInd);
                }
            }
        }

    }

    /**
     * Method to check if the lower section of the scorecard is filled
     * @return boolean: True if the lower section is filled, False otherwise
     */
    public boolean lowerSectionFilled() {
        for (int i = 6; i < 12; i++) {
            if (Scorecard.scoreCard.get(i).player_id == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to find the highest available score
     * @return int: The category number with the highest score, -1 if no category available
     */
    public int scoreHighestAvailable() {

        // yo chai last roll ma garne kura
        int highestScore=0,highestIndex=-1;


        for (int i = scoresAvailable.size()-1; i >= 0; i--) {
            if (!board.isCategoryFill(scoresAvailable.get(i).first) && scoresAvailable.get(i).second > highestScore) {
                highestScore = scoresAvailable.get(i).second;
                highestIndex = i;
            }
        }

        if (highestIndex != -1) {
            reasoningMsg = board.getCategoryName(scoresAvailable.get(highestIndex).first) +" scores the most! You may score it! ";
            //isScoreSet = true;
            return scoresAvailable.get(highestIndex).first;

        }
        else{
            return -1;
        }


    }


    /**
     * Method to try to get a sequential combination for the human player
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int trySequential(int player_id, ArrayList<Integer> dice, int rollCount, ArrayList<Integer> keptDiceInd) {
        int[] count = new int[7];
        boolean threeSequenceFound = false;
        boolean fourSequenceFound = false;
        boolean twoSequenceFound = false;
        boolean diceRoll = false;
        boolean repeatCheck = true;

// Count occurrences of each dice value
        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

// Check for three-sequence
        if ((count[1] >= 1 && count[2] >= 1 && count[3] >= 1) ||
                (count[2] >= 1 && count[3] >= 1 && count[4] >= 1) ||
                (count[3] >= 1 && count[4] >= 1 && count[5] >= 1) ||
                (count[4] >= 1 && count[5] >= 1 && count[6] >= 1)) {
            threeSequenceFound = true;
        }

// Check for four-sequence
        if ((count[1] >= 1 && count[2] >= 1 && count[3] >= 1 && count[4] >= 1) ||
                (count[2] >= 1 && count[3] >= 1 && count[4] >= 1 && count[5] >= 1) ||
                (count[3] >= 1 && count[4] >= 1 && count[5] >= 1 && count[6] >= 1)) {
            fourSequenceFound = true;
        }

// Check for two-sequence
        if ((count[1] >= 1 && count[2] >= 1) ||
                (count[2] >= 1 && count[3] >= 1) ||
                (count[3] >= 1 && count[4] >= 1) ||
                (count[4] >= 1 && count[5] >= 1) ||
                (count[5] >= 1 && count[6] >= 1)) {
            twoSequenceFound = true;
        }


        boolean threeOfAKind = false;
        int targetValueThreeOfAKind = -1;
        int targetValueTwoOfAKind = -1;


        for (int i = 1; i < count.length; i++) {
            if (count[i] >= 3) {
                threeOfAKind = true;
                targetValueThreeOfAKind = i;
            }
        }

        if (!threeOfAKind) {
            for (int i = 1; i < count.length; i++) {
                if (count[i] >= 2) {
                    targetValueTwoOfAKind = i;
                }
            }
        }

        if (!board.isCategoryFill(10)) {
            // if five straight available to score
            if (board.hasFiveStraight()) {
                reasoningMsg = "Five Straight is available to score! You may score it! ";
                return 10;
            } else {
                // try to get five straight
                int blockIndex = findBlockingDice(dice);
                if (board.hasFourStraight()) {
                    if (blockIndex != -1) {
                        indicesAvailableToKeep.add(blockIndex);
                        //if(!indicesAvailableToKeep.contains(blockIndex)) return KEEP_ERROR;
                        keptDiceInd.add(blockIndex);
                        reasoningMsg = "Five Straight is available and there is one more dice value to match it.";
                        return -1;
                    }
                    keptDiceInd.addAll(getIndicesInFourStraight(dice));
                    indicesAvailableToKeep = getIndicesInFourStraight(dice);
                    reasoningMsg = "Five Straight is available and there are four straight on the current dice combination.";
                    return -1;
                }
                else if (blockIndex != -1) {
                    ArrayList<Integer> diceInd = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        diceInd.add(i);
                    }
                    diceInd.remove(blockIndex);
                    keptDiceInd.addAll(diceInd);
                    indicesAvailableToKeep.addAll(diceInd);
                    reasoningMsg = "Five Straight is available and there is one more dice value to match it.";
                    return -1;
                }
                else if (threeSequenceFound) {
                    keptDiceInd.addAll(getIndicesInThreeStraight(dice));
                    indicesAvailableToKeep = getIndicesInThreeStraight(dice);
                    reasoningMsg = "Five Straight is available and there are three straight on the current dice combination.";
                    return -1;
                } else{

                    if(!board.isCategoryFill(8) && findOddDiceIndex(dice) != -1){

                        ArrayList<Integer> diceInd = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            diceInd.add(i);
                        }
                        diceInd.remove(findOddDiceIndex(dice));

                        keptDiceInd.addAll(diceInd);
                        indicesAvailableToKeep.addAll(diceInd);
                        reasoningMsg = "Full House is available and there are 2 two of a kinds on the current dice combination.";
                        return -1;
                    }
                }
            }
        } else {
            if (!board.isCategoryFill(9)) {
                // try to get four straight
                if (board.hasFourStraight()) {
                    reasoningMsg = "Four Straight is available to score! You may score it! ";
                    return 9;
                } else {
                    if (threeSequenceFound) {
                        keptDiceInd.addAll(getIndicesInThreeStraight(dice));
                        indicesAvailableToKeep = getIndicesInThreeStraight(dice);
                        reasoningMsg = "Four Straight is available and there are three straight on the current dice combination.";
                        return -1;
                    } else {
                        if (twoSequenceFound) {
                            keptDiceInd.addAll(getIndicesInTwoStraight(dice));
                            indicesAvailableToKeep = getIndicesInTwoStraight(dice);
                            reasoningMsg = "Four Straight is available and there are two straight on the current dice combination.";
                            return -1;
                        }
                        else{
                            if(findOddDiceIndex(dice) != -1){
                                ArrayList<Integer> diceInd = new ArrayList<>();
                                for (int i = 0; i < 5; i++) {
                                    diceInd.add(i);
                                }
                                diceInd.remove(findOddDiceIndex(dice));

                                keptDiceInd.addAll(diceInd);
                                indicesAvailableToKeep.addAll(diceInd);
                                reasoningMsg = "Full House is available and there are 2 pair of same dices on the current dice combination.";
                                return -1;
                            }


                            if (board.hasThreeOfAKind() && !board.hasFourOfAKind()) {

                                for (int i = 0; i < dice.size(); i++) {
                                    if (targetValueThreeOfAKind == dice.get(i)) {
                                        //if (!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                        keptDiceInd.add(i);
                                        indicesAvailableToKeep.add(i);
                                        reasoningMsg = "Full House is available and there are three of a kind on the current dice combination.";
                                    }
                                }
                                return -1;
                            } else {
                                return -1;
                            }
                        }
                    }





                }
            } else {

                if (!board.isCategoryFill(8)) {
                    if (board.hasFullHouse()) {
                        // score it
                        reasoningMsg = "Full House is available to score! You may score it! ";
                        return 8;
                    } else {

                        if(findOddDiceIndex(dice) != -1){
                            ArrayList<Integer> diceInd = new ArrayList<>();
                            for (int i = 0; i < 5; i++) {
                                diceInd.add(i);
                            }
                            diceInd.remove(findOddDiceIndex(dice));

                            keptDiceInd.addAll(diceInd);
                            indicesAvailableToKeep.addAll(diceInd);
                            reasoningMsg = "Full House is available and there are 2 pair of same dices on the current dice combination.";
                            return -1;
                        }


                        if (board.hasThreeOfAKind() && !board.hasFourOfAKind()) {

                            for (int i = 0; i < dice.size(); i++) {
                                if (targetValueThreeOfAKind == dice.get(i)) {
                                    //if (!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Full House is available and there are three of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        } else {

                            //check full house// if available scored...
                            if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                                reasoningMsg = "Full House is available to score! You may score it! ";
                                return 8;
                            }

                            if (targetValueTwoOfAKind != -1) {
                                for (int i = 0; i < dice.size(); i++) {

                                    if (targetValueTwoOfAKind == dice.get(i)) {
                                        //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                        keptDiceInd.add(i);
                                        indicesAvailableToKeep.add(i);
                                        reasoningMsg = "Full House is available and there are two of a kind on the current dice combination.";
                                    }
                                }
                                return -1;
                            }
                            return -1;
                        }
                    }
                }
                return -1;
            }
        }
        return -1;
    }


    /**
     * Method to find the blocking dice for a five-straight sequence
     * @param dice (ArrayList<Integer>) The dice values
     * @return int: The index of the blocking dice, -1 if no blocking dice found
     */
    public int findBlockingDice(ArrayList<Integer> dice) {
        int[] count = new int[7]; // To store occurrences of each dice value (1-6)

        // Count occurrences of each dice value
        for (int die : dice) {
            if (die >= 1 && die <= 6) {
                count[die]++;
            }
        }

        // Check potential five-straight sequences
        for (int start = 1; start <= 2; start++) { // Check ranges 1-5 and 2-6
            int missingCount = 0;
            int missingValue = -1;

            // Verify the sequence and identify missing values
            for (int i = start; i < start + 5; i++) {
                if (i >= 1 && i <= 6) { // Ensure bounds are respected
                    if (count[i] == 0) {
                        missingCount++;
                        missingValue = i; // Track the missing value
                    }
                    if (missingCount > 1) {
                        break; // More than one missing number, not a valid sequence
                    }
                }
            }

            // If exactly one value is missing, check for the blocking dice
            if (missingCount == 1) {
                for (int i = 0; i < dice.size(); i++) {
                    if (dice.get(i) == missingValue) {
                        return i; // Return the index of the blocking dice
                    }
                }
            }
        }

        // If no blocking dice is found, return -1
        return -1;
    }

    /**
     * Method to find the index of the odd dice in a full house sequence (e.g 1 1 2 2 5) - 5 is the odd dice
     * @param dice (ArrayList<Integer>) The dice values
     * @return int: The index of the odd dice, -1 if no odd dice found
     */
    public int findOddDiceIndex(ArrayList<Integer> dice) {
        int[] count = new int[7]; // Array to count occurrences of each dice value
        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

        // Count the number of "2 of a kind"
        int twoOfAKindCount = 0;
        ArrayList<Integer> valuesWithTwoOfAKind = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            if (count[i] == 2) {
                twoOfAKindCount++;
                valuesWithTwoOfAKind.add(i);
            }
        }

        // If we don't have exactly two "2 of a kind", return -1
        if (twoOfAKindCount != 2) return -1;

        // Find the index of the odd dice
        for (int i = 0; i < dice.size(); i++) {
            if (!valuesWithTwoOfAKind.contains(dice.get(i))) {
                return i; // Return the index of the dice that doesn't match
            }
        }

        return -1; // Return -1 if no odd dice is found
    }


    /**
     * Method to check if a die is part of a four-straight sequence
     * @param die (int) The die value
     * @param dice (ArrayList<Integer>) The dice values
     * @return boolean: True if the die is part of a four-straight sequence, False otherwise
     */
    private boolean isPartOfFourStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any four-sequence
        return (dice.contains(die - 3) && dice.contains(die - 2) && dice.contains(die - 1)) ||
                (dice.contains(die - 2) && dice.contains(die - 1) && dice.contains(die + 1)) ||
                (dice.contains(die - 1) && dice.contains(die + 1) && dice.contains(die + 2)) ||
                (dice.contains(die + 1) && dice.contains(die + 2) && dice.contains(die + 3));
    }

    /**
     * Method to check if a die is part of a three-straight sequence
     * @param die (int) The die value
     * @param dice (ArrayList<Integer>) The dice values
     * @return boolean: True if the die is part of a three-straight sequence, False otherwise
     */
    private boolean isPartOfThreeStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any three-sequence
        return (dice.contains(die - 2) && dice.contains(die - 1)) ||
                (dice.contains(die - 1) && dice.contains(die + 1)) ||
                (dice.contains(die + 1) && dice.contains(die + 2));
    }

    /**
     * Method to check if a die is part of a two-straight sequence
     * @param die (int) The die value
     * @param dice (ArrayList<Integer>) The dice values
     * @return boolean: True if the die is part of a two-straight sequence, False otherwise
     */
    private boolean isPartOfTwoStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any two-sequence
        return dice.contains(die - 1) || dice.contains(die + 1);
    }

    /**
     * Method to get the indices of the dice in a three-straight sequence
     * @param dice (ArrayList<Integer>) The dice values
     * @return ArrayList<Integer>: The indices of the dice in a three-straight sequence
     */
    public ArrayList<Integer> getIndicesInThreeStraight(ArrayList<Integer> dice) {
        ArrayList<Integer> indices = new ArrayList<>();
        HashSet<Integer> used = new HashSet<>();

        for (int i = 0; i < dice.size(); i++) {
            int die = dice.get(i);
            if (!used.contains(die) && isPartOfThreeStraight(die, dice)) {
                indices.add(i); // Add index of the die
                used.add(die);  // Mark die as used in this sequence
            }
        }

        return indices;
    }

    /**
     * Method to get the indices of the dice in a four-straight sequence
     * @param dice (ArrayList<Integer>) The dice values
     * @return ArrayList<Integer>: The indices of the dice in a four-straight sequence
     */
    public ArrayList<Integer> getIndicesInFourStraight(ArrayList<Integer> dice) {
        ArrayList<Integer> indices = new ArrayList<>();
        HashSet<Integer> used = new HashSet<>();

        for (int i = 0; i < dice.size(); i++) {
            int die = dice.get(i);
            if (!used.contains(die) && isPartOfFourStraight(die, dice)) {
                indices.add(i); // Add index of the die
                used.add(die);  // Mark die as used in this sequence
            }
        }
        return indices;
    }

    /**
     * Method to get the indices of the dice in a two-straight sequence
     * @param dice (ArrayList<Integer>) The dice values
     * @return ArrayList<Integer>: The indices of the dice in a two-straight sequence
     */
    public ArrayList<Integer> getIndicesInTwoStraight(ArrayList<Integer> dice) {
        ArrayList<Integer> indices = new ArrayList<>();
        HashSet<Integer> used = new HashSet<>();

        for (int i = 0; i < dice.size(); i++) {
            int die = dice.get(i);
            if (!used.contains(die) && isPartOfTwoStraight(die, dice)) {
                indices.add(i); // Add index of the die
                used.add(die);  // Mark die as used in this sequence
            }
        }

        return indices;
    }


    /**
     * Method to score a category for the human player
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param category_choice (int) The category chosen by the player
     * @return int: 0
     */
    public int playTurn(int player_id, ArrayList<Integer> dice,int category_choice){
        Combinations board = new Combinations(dice); // Initialize combinations based on dice
        combinations = board.availableCombinations();
        scoreCategory(board,category_choice, player_id);
        return 0;
    }


    /**
     * Method to score a category for the human player
     * @param board (Combinations) The board object
     * @param category_choice (int) The category chosen by the player
     * @param player_id (int) The player id
     */
    public void scoreCategory(Combinations board, int category_choice, int player_id) {
        board.setScore(category_choice-1, player_id);
    }


    /**
     * Method to calculate the available categories for the human player on the current dice combination
     */
    public void findScorebyCategory() {
        availableScores.clear();
        if (!board.availableCombinations().isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                int score = board.calculateUpperSectionScore(i);
                if (score != 0) {
                    availableScores.add(new Pair<>(i - 1, score));
                }
            }

            int totalDice = board.sumAllDice();

            if (board.hasThreeOfAKind() && !board.isCategoryFill(6)) {
                availableScores.add(new Pair<>(6, totalDice));
            }
            if (board.hasFourOfAKind() && !board.isCategoryFill(7)) {
                availableScores.add(new Pair<>(7, totalDice));
            }
            if (board.hasFullHouse() && !board.isCategoryFill(8)) {
                availableScores.add(new Pair<>(8, 25));
            }
            if (board.hasFourStraight() && !board.isCategoryFill(9)) {
                availableScores.add(new Pair<>(9, 30));
            }
            if (board.hasFiveStraight() && !board.isCategoryFill(10)) {
                availableScores.add(new Pair<>(10, 40));
            }
            if (board.hasYahtzee() && !board.isCategoryFill(11)) {
                availableScores.add(new Pair<>(11, 50));
            }
        }
    }

    /**
     * Method to view help for the human player
     * @param dice (ArrayList<Integer>) The dice values
     */
    public void viewHelp(ArrayList<Integer> dice) {
        int highestIndex = -1;
        int highestScore = -1;

        board.updateDice(dice);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to use help? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            findScorebyCategory();
            for (Pair<Integer, Integer> score : availableScores) {
                if (score.second > highestScore && !board.isCategoryFill(score.first)) {
                    highestScore = score.second;
                    highestIndex = score.first;
                }
            }

            if (highestIndex >= 0) {
                System.out.println("Category No. " + (highestIndex + 1) + " is recommended for scoring since it scores the highest.");
            } else {
                System.out.println("Try rolling all available dice since there are limited/no options at the moment.");
            }
        }
    }

    /**
     * Method to get the potential categories for the human player
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @return ArrayList<Integer>: The potential categories for the human player
     */
    public ArrayList<Integer> potentialCategories(ArrayList<Integer> dice, int rollCount) {

        ArrayList<Integer> potentialOnes = new ArrayList<>();
        int[] count = new int[7];
        boolean threeSequenceFound = false;
        int targetValueTwoOfAKind = -1;

        board.updateDice(dice);
        board.countDiceFace();


        for (int i = 1; i < count.length; i++) {
            if (count[i] >= 2) {
                targetValueTwoOfAKind = i;
            }
        }

        // Count occurrences of each dice value
        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

        // Check for three-sequence
        if ((count[1] >= 1 && count[2] >= 1 && count[3] >= 1) ||
                (count[2] >= 1 && count[3] >= 1 && count[4] >= 1) ||
                (count[3] >= 1 && count[4] >= 1 && count[5] >= 1) ||
                (count[4] >= 1 && count[5] >= 1 && count[6] >= 1)) {
            threeSequenceFound = true;
        }


        // upper section
        for (int i = 1; i <= 6; i++) {
            int score = board.calculateUpperSectionScore(i);
            if ((score > 0) && !board.isCategoryFill(i-1)) {
                potentialOnes.add(i - 1);
            }
        }

        if (rollCount != 0) {
            if (board.hasThreeOfAKind()) {
                if ( !board.isCategoryFill(11)) potentialOnes.add(11);
                if ( !board.isCategoryFill(8)) potentialOnes.add(8);
                if (!board.isCategoryFill(7)) potentialOnes.add(7);
                if (!board.isCategoryFill(6)) potentialOnes.add(6);
            }
            else {
                if(targetValueTwoOfAKind!=-1){
                    if ( !board.isCategoryFill(8)) potentialOnes.add(8); // full house possible
                }
            }


            if (threeSequenceFound) {
                if (!board.isCategoryFill(9)) potentialOnes.add(9);
                if (!board.isCategoryFill(10)) potentialOnes.add(10);
            }
        }
        else{
            return board.availableToScoreCategories(); // for last roll, just send the available combinations
        }
        return potentialOnes;
    }
}

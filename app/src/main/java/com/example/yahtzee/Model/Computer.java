package com.example.yahtzee.Model;


import java.util.ArrayList;
import java.util.HashSet;


public class Computer extends Player {

    /**
     * private data members
     */
    public class Pair<K, V> {
        public K first;
        public V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    private final int COMPUTER = 2;
    private final int KEEP_ERROR = -99;


    private ArrayList<Pair<Integer, Integer>> scoresAvailable = new ArrayList<>();
    private ArrayList<Integer> availableCategory = new ArrayList<>();
    private Combinations board = new Combinations();

    private String reasoningMsg;

    private String tempMsg;

    private ArrayList<Integer> indicesAvailableToKeep;

    private ArrayList<Integer> currentKeptDiceInd;

    /**
     * Default Constructor for Computer
     */
    public Computer() {
        num_rolls = 1;
        reasoningMsg = "";
        tempMsg = "";
        indicesAvailableToKeep = new ArrayList<>();
        currentKeptDiceInd = new ArrayList<>();
    }

    /**
     * Getter for reasoningMsg
     */
    public String getReasoningMsg() {
        return reasoningMsg;
    }

    /**
     * Play the turn for the computer, given the dice values and the roll count, the computer will decide which category to score
     * @param player_id (int) The player id
     * @param dice (int) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (int) The indices of the dice kept
     * @return
     */
    @Override
    public int playTurn(int player_id, ArrayList<Integer> dice, int rollCount, ArrayList<Integer> keptDiceInd) {
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


        categoryToReturn = computerDecide(player_id, dice, rollCount, keptDiceInd);

        // check if indicesAvailableToKeep has all the values in keptDiceInd

        if (!keptDiceInd.containsAll(indicesAvailableToKeep)) {
            categoryToReturn = KEEP_ERROR;
        }

        if (categoryToReturn == KEEP_ERROR) {
            keptDiceInd.clear();
            keptDiceInd.addAll(currentKeptDiceInd);
            reasoningMsg = tempMsg;
        }

        indicesAvailableToKeep.clear();
        currentKeptDiceInd.clear();

        if (rollCount != 2) {
            if (categoryToReturn == KEEP_ERROR) return -1;
            return categoryToReturn;
        } else {
            if (categoryToReturn == -1 || categoryToReturn == KEEP_ERROR) { // if no category is available, score the highest available

                return scoreHighestAvailable();

            } else {
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
            for (int i = 1; i <= DICE_COUNT + 1; i++) {
                int score = board.calculateUpperSectionScore(i);
                if ((score != 0) && (!board.isCategoryFill(i - 1))) {
                    scoresAvailable.add(new Pair<>(i - 1, score));
                }
            }

            int totalDice = board.sumAllDice();

            if (board.hasThreeOfAKind() && !board.isCategoryFill(6)) {
                scoresAvailable.add(new Pair<>(6, totalDice));
            }
            if (board.hasFourOfAKind() && !board.isCategoryFill(7)) {
                scoresAvailable.add(new Pair<>(7, totalDice));
            }
            if (board.hasFullHouse() && !board.isCategoryFill(8)) {
                scoresAvailable.add(new Pair<>(8, 25));
            }
            if (board.hasFourStraight() && !board.isCategoryFill(9)) {
                scoresAvailable.add(new Pair<>(9, 30));
            }
            if (board.hasFiveStraight() && !board.isCategoryFill(10)) {
                scoresAvailable.add(new Pair<>(10, 40));
            }
            if (board.hasYahtzee() && !board.isCategoryFill(11)) {
                scoresAvailable.add(new Pair<>(11, 50));
            }
        }
    }
    /**
     * Method to decide the category to score for computer decision
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int computerDecide(int player_id, ArrayList<Integer> dice, int rollCount, ArrayList<Integer> keptDiceInd) {

        indicesAvailableToKeep.clear();



        if (lowerSectionFilled()) {
            // try upper section fill
            if (!availableCategory.isEmpty()) {
                return scoreHighestAvailable();
            } else {
                return -1; // **** IMP **** No change to keptDices
            }
        } else {
            return checkLowerSection(player_id, dice, rollCount, keptDiceInd);
        }
    }

    /**
     * Method to check the lower section of the scorecard - score the lower section if possible
     * @param player_id (int) The player id
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @param keptDiceInd (ArrayList<Integer>) The indices of the dice kept
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int checkLowerSection(int player_id, ArrayList<Integer> dice, int rollCount, ArrayList<Integer> keptDiceInd) {
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


        if (!board.isCategoryFill(11)) {
            if (board.hasYahtzee()) {
                // score it
                board.setScore(11, COMPUTER);
                return 11;
            } else {
                // let's try to get Yahtzee
                if (board.hasFourOfAKind()) {
                    for (int i = 0; i < dice.size(); i++) {
                        if (targetValueFourOfAKind == dice.get(i)) {
                            keptDiceInd.add(i);
                            indicesAvailableToKeep.add(i);
                            reasoningMsg = "Yahtzee is available and there are four of a kind on the current dice combination.";
                        }
                    }
                    return -1;
                } else {
                    if (board.hasThreeOfAKind()) {

                        //check full house// if available scored...
                        if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                            board.setScore(8, COMPUTER);
                            return 8;
                        }

                        for (int i = 0; i < dice.size(); i++) {
                            if (targetValueThreeOfAKind == dice.get(i)) {
                                keptDiceInd.add(i);
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Yahtzee is available and there are three of a kind on the current dice combination.";
                            }
                        }
                        return -1;
                    } else {
                        return trySequential(player_id, dice, rollCount, keptDiceInd);
                    }
                }
            }
        } else {
            // Yahtzee is not AVAILABLE

            if (!board.isCategoryFill(7)) {
                if (board.hasFourOfAKind()) {
                    board.setScore(7, COMPUTER);
                    return 7;
                } else {
                    // let's try to get Four of a kind
                    if (board.hasThreeOfAKind()) {

                        //check full house// if available scored...
                        if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                            board.setScore(8, COMPUTER);
                            return 8;
                        }

                        for (int i = 0; i < dice.size(); i++) {
                            if (targetValueThreeOfAKind == dice.get(i)) {
                                keptDiceInd.add(i);
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Four of a kind is available and there are three of a kind on the current dice combination.";
                            }
                        }
                        return -1;
                    } else {
                        return trySequential(player_id, dice, rollCount, keptDiceInd);
                    }
                }
            } else {
                // Four of a kind is not available...

                // if three of a kind available, score it
                if (!board.isCategoryFill(6)) {
                    if (board.hasThreeOfAKind()) {
                        // score it
                        board.setScore(6, COMPUTER);
                        return 6;
                    } else {
                        // let's try to get Three of a kind
                        // if two of a kind availble
                        if (targetValueTwoOfAKind != -1) {
                            for (int i = 0; i < dice.size(); i++) {

                                if (targetValueTwoOfAKind == dice.get(i)) {
                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Three of a kind is available and there are two of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        } else {
                            return trySequential(player_id, dice, rollCount, keptDiceInd);
                        }
                    }
                } else {
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
     * Method to score the highest available category
     * @return int: The category chosen by the player or -1 if no category chosen
     */
    public int scoreHighestAvailable() {

        // yo chai last roll ma garne kura
        int highestScore = 0, highestIndex = -1;
        findCategoryScore();


        for (int i = scoresAvailable.size()-1; i >= 0; i--) {
            if (!board.isCategoryFill(scoresAvailable.get(i).first) && scoresAvailable.get(i).second > highestScore) {
                highestScore = scoresAvailable.get(i).second;
                highestIndex = i;
            }
        }

        if (highestIndex != -1) {
            board.setScore(scoresAvailable.get(highestIndex).first, 2);
            return scoresAvailable.get(highestIndex).first;

        } else {
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
                board.setScore(10, COMPUTER);
                return 10;
            } else {
                int blockIndex = findBlockingDice(dice);
                // try to get five straight
                if (board.hasFourStraight()) {
                    //int blockIndex = findBlockingDice(dice);
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
                    board.setScore(9, COMPUTER);
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
                        board.setScore(8, COMPUTER);
                        return 8;
                    } else {

                        //check full house// if available scored...
                        if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                            board.setScore(8, COMPUTER);
                            return 8;
                        }

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
                                    keptDiceInd.add(i);
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Full House is available and there are three of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        } else {

                            //check full house// if available scored...
                            if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                                board.setScore(8, COMPUTER);
                                return 8;
                            }

                            if (targetValueTwoOfAKind != -1) {
                                for (int i = 0; i < dice.size(); i++) {

                                    if (targetValueTwoOfAKind == dice.get(i)) {
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
     * Method to get the potential categories for the computer player
     * @param dice (ArrayList<Integer>) The dice values
     * @param rollCount (int) The number of rolls
     * @return ArrayList<Integer>: The potential categories for the computer player
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

        if (rollCount != -2) {
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





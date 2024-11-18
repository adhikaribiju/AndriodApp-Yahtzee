package com.example.yahtzee.Model;

import com.example.yahtzee.Model.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Computer extends Player {
	
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

    private int num_rolls;
    private boolean reroll;
    private boolean keepError;
    private boolean isScoreSet;

    private ArrayList<Pair<Integer, Integer>> scoresAvailable = new ArrayList<>();
    private ArrayList<Integer> keptDices = new ArrayList<>();
    private ArrayList<Integer> availableCategory = new ArrayList<>();
    private Combinations board = new Combinations();

    private String reasoningMsg;

    private String tempMsg;

    private ArrayList<Integer> indicesAvailableToKeep;

    private ArrayList<Integer> currentKeptDiceInd;



    public Computer() {
        num_rolls = 1;
        reroll = false;
        keepError = false;
        isScoreSet = false;
        reasoningMsg = "";
        tempMsg ="";
        indicesAvailableToKeep = new ArrayList<>();
        currentKeptDiceInd = new ArrayList<>();
    }

    public String getReasoningMsg(){
        return  reasoningMsg;
    }
    public int playTurnt(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd){
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


        categoryToReturn = computerDecide(player_id, dice,rollCount,keptDiceInd);

        // check if indicesAvailableToKeep has all the values in keptDiceInd

        if (!keptDiceInd.containsAll(indicesAvailableToKeep)) {categoryToReturn = KEEP_ERROR;}

        if (categoryToReturn == KEEP_ERROR) {
            keptDiceInd.clear();
            keptDiceInd.addAll(currentKeptDiceInd);
            reasoningMsg = tempMsg;
        }

        indicesAvailableToKeep.clear();
        currentKeptDiceInd.clear();

        if (rollCount!=2){
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

    @Override
    public void playTurn() {
//        System.out.println("\nComputer is playing.....");
//        isScoreSet = false;
//
//        playRoll();
//        displayDice();
//
//        board.updateDice(dice);
//        board.displayScorecard();
//        availableCategory = board.availableCombinations();
//        board.displayAvailableCombinations();
//
//        keptDices.clear();
//
//        while (num_rolls <= 3 && !reroll && !keepError) {
//            if (num_rolls >= 2) {
//                displayDice();
//            }
//            findCategoryScore();
//            reroll = computerDecide();
//            num_rolls++;
//        }
//
//        if (keepError && !isScoreSet) {
//            int highestIndex = -1;
//            int highestScore = -1;
//            findCategoryScore();
//
//            for (int i = 0; i < scoresAvailable.size(); i++) {
//                if (!board.isCategoryFill(scoresAvailable.get(i).first) && scoresAvailable.get(i).second > highestScore) {
//                    highestScore = scoresAvailable.get(i).second;
//                    highestIndex = i;
//                }
//            }
//
//            if (highestIndex != -1) {
//                board.setScore(scoresAvailable.get(highestIndex).first, 2);
//                isScoreSet = true;
//            }
//        }
//
//        if (!isScoreSet) {
//            System.out.println("Nothing to score, so skipping turn");
//        }
//
//        System.out.println("\n\nComputer's Turn Ended!");
    }

    public void findCategoryScore() {
        scoresAvailable.clear();

        if (!availableCategory.isEmpty()) {
            for (int i = 1; i <= DICE_COUNT+1; i++) {
                int score = board.calculateUpperSectionScore(i);
                if ((score != 0) && (!board.isCategoryFill(i-1))) {
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

    public int computerDecide(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd) {
       // isScoreSet = false;
//        for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
//            if (scoresAvailable.get(i).second >= 20 && !board.isCategoryFill(scoresAvailable.get(i).first)) {
//                board.setScore(scoresAvailable.get(i).first, 2);
//                isScoreSet = true;
//                return i;
//            }
//        }
        //indicesAvailableToKeep.addAll(keptDiceInd);
        indicesAvailableToKeep.clear();
        // find out indices available to keep
//        for(int i=0;i<5;i++){
//            if(!keptDiceInd.contains(i)) indicesAvailableToKeep.add(i);
//        }





            if (lowerSectionFilled()) {
                // try upper section fill
                if (!availableCategory.isEmpty()) {
                    return scoreHighestAvailable();
                } else {
                    // No change to keptDices
                    // Reroll all the dices
                    // Computer Decided to reroll all the dices since no categories available to score
                    return -1; // **** IMP **** No change to keptDices
                }
            } else {
//                 if (isSequentialAvailable()) {
//                    board.updateDice(dice);
//                    board.displayAvailableCombinations();
//                    return -1;
//                } else {
//                    tryYahtzeeOrFullHouse();
//                    return -1;
//                }
                return checkLowerSection(player_id, dice,  rollCount,keptDiceInd);
            }
    }

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
                board.setScore(11,COMPUTER);
                return 11;
            }
            else{
                // let's try to get Yahtzee
                if(board.hasFourOfAKind()){
                    for(int i = 0; i < dice.size(); i++) {
                        if (targetValueFourOfAKind == dice.get(i)){
                            //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
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
                            board.setScore(8,COMPUTER);
                            return 8;
                        }

                        for(int i = 0; i < dice.size(); i++) {
                            if (targetValueThreeOfAKind== dice.get(i)){
                                //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                keptDiceInd.add(i);
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Yahtzee is available and there are three of a kind on the current dice combination.";
                            }
                        }
                        return -1;
                    }
                    else{
                        return trySequential(player_id, dice, rollCount, keptDiceInd);
//                        if(targetValueTwoOfAKind!=-1){
//                            for(int i = 0; i < dice.size(); i++) {
//
//                                if (targetValueTwoOfAKind == dice.get(i)) {
//                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
//                                    keptDiceInd.add(i);
//                                    reasoningMsg = "Yahtzee is available and there are two of a kind on the current dice combination.";
//                                }
//                            }
//                            return -1;
//                        }
//                        else {
//                            return trySequential(player_id, dice, rollCount, keptDiceInd);
//                        }
                    }
                }
            }
        }
        else {
            // Yahtzee is not AVAILABLE

            // check full house
//            if (!board.isCategoryFill(9) && board.hasFullHouse()) {
//                board.setScore(9,COMPUTER);
//                return 9;
//            }


            //return trySequential(player_id, dice, rollCount, keptDiceInd);


            // check if Four of a kind available to score, if yes, score it
            if(!board.isCategoryFill(7)) {
                if (board.hasFourOfAKind()){
//                    if(!board.isCategoryFill(6)){
//                        // score it
//                        board.setScore(6,COMPUTER);
//                        return 6;
//                    }
                    // score it
                    board.setScore(7,COMPUTER);
                    return 7;
                }
                else{
                    // let's try to get Four of a kind
                        if(board.hasThreeOfAKind()){

                            //check full house// if available scored...
                            if (!board.isCategoryFill(8) && board.hasFullHouse()) {
                                board.setScore(8,COMPUTER);
                                return 8;
                            }

                            for(int i = 0; i < dice.size(); i++) {
                                if (targetValueThreeOfAKind== dice.get(i)){
                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Four of a kind is available and there are three of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        }
                        else{
                            return trySequential(player_id, dice, rollCount, keptDiceInd);
//                        if(targetValueTwoOfAKind!=-1){
//                            for(int i = 0; i < dice.size(); i++) {
//
//                                if (targetValueTwoOfAKind == dice.get(i)) {
//                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
//                                    keptDiceInd.add(i);
//                                    reasoningMsg = "Yahtzee is available and there are two of a kind on the current dice combination.";
//                                }
//                            }
//                            return -1;
//                        }
//                        else {
//                            return trySequential(player_id, dice, rollCount, keptDiceInd);
//                        }

                    }
                }
            }
            else{
                // Four of a kind is not available...

                // if three of a kind available, score it
                if(!board.isCategoryFill(6)) {
                    if (board.hasThreeOfAKind()){
                        // score it
                        board.setScore(6,COMPUTER);
                        return 6;
                    }
                    else{
                        // let's try to get Three of a kind
                        // if two of a kind availble
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
                    // 3 of a kind not available, meaning Yahtzee, four of a kind, three of a kind are all filled
                    // if full house not filled, try to get it

//                    if(!board.isCategoryFill(8)) {
//                        if (board.hasFullHouse()) {
//                            // score it
//                            board.setScore(8, COMPUTER);
//                            return 8;
//                        }else {
//                            if(board.hasThreeOfAKind()) {
//
//
//                                for (int i = 0; i < dice.size(); i++) {
//                                    if (targetValueThreeOfAKind == dice.get(i)) {
//                                        if (!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
//                                        keptDiceInd.add(i);
//                                        reasoningMsg = "Full House is available and there are three of a kind on the current dice combination.";
//                                    }
//                                }
//                                return -1;
//                            }
//                            else{
//
//                                if(targetValueTwoOfAKind!=-1){
//                                    for(int i = 0; i < dice.size(); i++) {
//
//                                        if (targetValueTwoOfAKind == dice.get(i)) {
//                                            //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
//                                            keptDiceInd.add(i);
//                                            indicesAvailableToKeep.add(i);
//                                            reasoningMsg = "Full House is available and there are two of a kind on the current dice combination.";
//                                        }
//                                    }
//                                    return -1;
//                                }
//                                else {
//                                    return trySequential(player_id, dice, rollCount, keptDiceInd);
//                                }
//                            }
//                        }
//                    }
//                    else{
//                        return trySequential(player_id, dice, rollCount, keptDiceInd);
//                    }
                     // if filled try sequential
                    return trySequential(player_id, dice, rollCount, keptDiceInd);
                }
            }
        }

    }

    public boolean lowerSectionFilled() {
        for (int i = 6; i < 12; i++) {
            if (Scorecard.scoreCard.get(i).player_id == 0) {
                return false;
            }
        }
        return true;
    }

    public int scoreHighestAvailable() {
//        for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
//            if (!board.isCategoryFill(scoresAvailable.get(i).first)) {
//                board.setScore(scoresAvailable.get(i).first, 2);
//                break;
//            }
//        }

        // yo chai last roll ma garne kura
        int highestScore=0,highestIndex=-1;


        for (int i = 0; i < scoresAvailable.size(); i++) {
            if (!board.isCategoryFill(scoresAvailable.get(i).first) && scoresAvailable.get(i).second > highestScore) {
                highestScore = scoresAvailable.get(i).second;
                highestIndex = i;
            }
        }

        if (highestIndex != -1) {
            board.setScore(scoresAvailable.get(highestIndex).first, 2);
            isScoreSet = true;
            return scoresAvailable.get(highestIndex).first;

        }
        else{
            return -1;
        }


    }

    public boolean isSequentialAvailable() {
        int[] count = new int[7];
        boolean sequenceFound = false;
        boolean diceRoll = false;

        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

        if ((count[1] >= 1 && count[2] >= 1 && count[3] >= 1) || (count[2] >= 1 && count[3] >= 1 && count[4] >= 1)) {
            sequenceFound = true;
        }

        if (sequenceFound && (!board.isCategoryFill(9) || !board.isCategoryFill(10))) {
            for (int i = 0; i < dice.size(); i++) {
                int value = dice.get(i);
                if ((value == 1 || value == 2 || value == 3 || value == 4)) {
                    if (!keptDices.contains(i)) {
                        keptDices.add(i);
                    } else {
                        keepError = true;
                    }
                }
            }

            for (int i = 0; i < dice.size(); i++) {
                int value = dice.get(i);
                if ((value != 1 && value != 2 && value != 3) && (value != 2 && value != 3 && value != 4)) {
                    dice.set(i, askIfInputManual(value));
                    diceRoll = true;
                }
            }
            if (!diceRoll && !keepError) {
                playRoll();
                board.updateDice(dice);
            }
            return true;
        }
        return false;
    }

    public void tryYahtzeeOrFullHouse() {
        int[] count = new int[7];
        boolean threeOfAKind = false;
        boolean twoOfAKind = false;
        int targetValueThreeOfAKind = -1;
        int targetValueTwoOfAKind = -1;

        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
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

        Random rand = new Random();
        for (int i = 0; i < dice.size(); i++) {
            if (threeOfAKind && dice.get(i) != targetValueThreeOfAKind) {


                dice.set(i, askIfInputManual(dice.get(i)));


            } else if (twoOfAKind && dice.get(i) != targetValueTwoOfAKind) {


                dice.set(i, askIfInputManual(dice.get(i)));


            } else if (!threeOfAKind && !twoOfAKind) {


                dice.set(i, rand.nextInt(6) + 1);

            }
        }
        board.updateDice(dice);
        board.displayAvailableCombinations();
    }

    public int askIfInputManual(int diceNo) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nDo you wish to manually input the dice value for " + diceNo + " (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            int num;
            do {
                System.out.print("Enter the value for dice " + diceNo + " : ");
                num = scanner.nextInt();
                if (num >= 1 && num <= 6) return num;
                System.out.println("Invalid entry! Enter values from 1-6.");
            } while (true);
        } else {
            return new Random().nextInt(6) + 1;
        }
    }

    public int trySequential(int player_id, ArrayList<Integer> dice, int rollCount,ArrayList<Integer>  keptDiceInd) {
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

        if(!board.isCategoryFill(10)){
            // if five straight available to score
            if(board.hasFiveStraight()){
                board.setScore(10,COMPUTER);
                return 10;
            }
            else {
                // try to get five straight
                if (board.hasFourStraight()) {
                    int blockIndex = findBlockingDice(dice);
                    if(blockIndex != -1 ){
                        indicesAvailableToKeep.add(blockIndex);
                        //if(!indicesAvailableToKeep.contains(blockIndex)) return KEEP_ERROR;
                        keptDiceInd.add(blockIndex);
                        reasoningMsg = "Five Straight is available and there is one more dice value to match it.";


                    }

                    // Manually determine the Four Straight sequence and add unmatched dice to keptDice
                    for (int i = 0; i < dice.size(); i++) {
                        if (isPartOfFourStraight(dice.get(i), dice)) {
                            if(repeatCheck && targetValueTwoOfAKind == dice.get(i)){
                                repeatCheck = false;
                                continue;
                            }
                            //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                            keptDiceInd.add(i); // Store the index of the die
                            indicesAvailableToKeep.add(i);
                            reasoningMsg = "Five Straight is available and there are four straight on the current dice combination.";
                        }
                    }
                    return -1;
                } else if (threeSequenceFound) {
                    // Manually determine the Three Straight sequence and add unmatched dice to keptDice
                    for (int i = 0; i < dice.size(); i++) {
                        if (isPartOfThreeStraight(dice.get(i), dice)) {
                            if(repeatCheck && (targetValueTwoOfAKind == dice.get(i))){
                                repeatCheck = false;
                                continue;
                            }
                            //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                            keptDiceInd.add(i); // Store the index of the die
                            indicesAvailableToKeep.add(i);
                            reasoningMsg = "Five Straight is available and there are three straight on the current dice combination.";
                        }
                    }
                    return -1;
                } else if (twoSequenceFound) {
                    // Manually determine the Two Straight sequence and add unmatched dice to keptDice
                    for (int i = 0; i < dice.size(); i++) {
                        if (isPartOfTwoStraight(dice.get(i), dice)) {
                            //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                            keptDiceInd.add(i); // Store the index of the die
                            indicesAvailableToKeep.add(i);
                            reasoningMsg = "Five Straight is available and there are two straight on the current dice combination.";
                        }
                    }
                    return -1;
                }

            }

        }
        else {

            if(!board.isCategoryFill(9))
            {
                // try to get four stright
                if (board.hasFourStraight()) {
                    board.setScore(9,COMPUTER);
                    return 9;

                } else {
                    if (threeSequenceFound) {
                        // Manually determine the Three Straight sequence and add unmatched dice to keptDice
                        for (int i = 0; i < dice.size(); i++) {
                            if (isPartOfThreeStraight(dice.get(i), dice)) {
                                //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                keptDiceInd.add(i); // Store the index of the die
                                indicesAvailableToKeep.add(i);
                                reasoningMsg = "Four Straight is available and there are three straight on the current dice combination.";
                            }
                        }
                        return -1;
                    }
                    else {
                        if (twoSequenceFound) {
                            // Manually determine the Two Straight sequence and add unmatched dice to keptDice
                            for (int i = 0; i < dice.size(); i++) {
                                if (isPartOfTwoStraight(dice.get(i), dice)) {
                                    //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i); // Store the index of the die
                                    indicesAvailableToKeep.add(i);
                                    reasoningMsg = "Four Straight is available and there are two straight on the current dice combination.";
                                }
                            }
                            return -1;
                        }
                    }
                }
            }
            else
            {

                if(!board.isCategoryFill(8)) {
                    if (board.hasFullHouse()) {
                        // score it
                        board.setScore(8, COMPUTER);
                        return 8;
                    }else {

                        if(findOddDiceIndex(dice)!=-1){
                            for (int i = 0; i < dice.size(); i++) {
                                if (findOddDiceIndex(dice) != i ) {
                                    //if (!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    reasoningMsg = "Full House is available and there is only one dice the is unmatched.";
                                }
                            }
                            return -1;
                        }



                        if(board.hasThreeOfAKind()) {

                            for (int i = 0; i < dice.size(); i++) {
                                if (targetValueThreeOfAKind == dice.get(i)) {
                                    //if (!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                    keptDiceInd.add(i);
                                    reasoningMsg = "Full House is available and there are three of a kind on the current dice combination.";
                                }
                            }
                            return -1;
                        }
                        else{

                            if(targetValueTwoOfAKind!=-1){
                                for(int i = 0; i < dice.size(); i++) {

                                    if (targetValueTwoOfAKind == dice.get(i)) {
                                        //if(!indicesAvailableToKeep.contains(i)) return KEEP_ERROR;
                                        keptDiceInd.add(i);
                                        indicesAvailableToKeep.add(i);
                                        reasoningMsg = "Full House is available and there are two of a kind on the current dice combination.";
                                    }
                                }
                                return -1;
                            }
                            else {
                                return -1;
                            }
                        }
                    }
                }
                return -1;
            }
        }

        return -1;
    }


    public int findBlockingDice(ArrayList<Integer> dice) {
        int[] count = new int[7]; // To store occurrences of each dice value (1-6)

        // Count occurrences of each dice value
        for (int die : dice) {
            if (die >= 1 && die <= 6) count[die]++;
        }

        // Check for potential five straight sequences and find the blocking dice
        for (int start = 1; start <= 2; start++) { // Start of the sequence (1-2 or 2-6)
            boolean isStraightPossible = true;
            ArrayList<Integer> missingNumbers = new ArrayList<>();

            // Check for missing numbers in the sequence
            for (int i = start; i < start + 5; i++) {
                if (count[i] == 0) {
                    isStraightPossible = false;
                    missingNumbers.add(i);
                }
            }

            // If one missing number is found, identify its index
            if (!isStraightPossible && missingNumbers.size() == 1) {
                int missingNumber = missingNumbers.get(0);

                // Find the index of the dice that prevents the straight
                for (int i = 0; i < dice.size(); i++) {
                    if (dice.get(i) == missingNumber) {
                        return i; // Return the index of the blocking dice
                    }
                }
            }
        }

        return -1; // Return -1 if no blocking dice found or already a five straight
    }

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




    private boolean isPartOfFourStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any four-sequence
        return (dice.contains(die - 3) && dice.contains(die - 2) && dice.contains(die - 1)) ||
                (dice.contains(die - 2) && dice.contains(die - 1) && dice.contains(die + 1)) ||
                (dice.contains(die - 1) && dice.contains(die + 1) && dice.contains(die + 2)) ||
                (dice.contains(die + 1) && dice.contains(die + 2) && dice.contains(die + 3));
    }
    private boolean isPartOfThreeStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any three-sequence
        return (dice.contains(die - 2) && dice.contains(die - 1)) ||
                (dice.contains(die - 1) && dice.contains(die + 1)) ||
                (dice.contains(die + 1) && dice.contains(die + 2));
    }

    private boolean isPartOfTwoStraight(int die, ArrayList<Integer> dice) {
        // Check if this die is part of any two-sequence
        return dice.contains(die - 1) || dice.contains(die + 1);
    }

}

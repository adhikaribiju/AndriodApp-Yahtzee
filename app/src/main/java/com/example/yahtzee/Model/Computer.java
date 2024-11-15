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


    private int num_rolls;
    private boolean reroll;
    private boolean keepError;
    private boolean isScoreSet;

    private ArrayList<Pair<Integer, Integer>> scoresAvailable = new ArrayList<>();
    private ArrayList<Integer> keptDices = new ArrayList<>();
    private ArrayList<Integer> availableCategory = new ArrayList<>();
    private Combinations board = new Combinations();

    public Computer() {
        num_rolls = 1;
        reroll = false;
        keepError = false;
        isScoreSet = false;
    }

    @Override
    public void playTurn() {
        System.out.println("\nComputer is playing.....");
        isScoreSet = false;

        playRoll();
        displayDice();

        board.updateDice(dice);
        board.displayScorecard();
        availableCategory = board.availableCombinations();
        board.displayAvailableCombinations();

        keptDices.clear();

        while (num_rolls <= 3 && !reroll && !keepError) {
            if (num_rolls >= 2) {
                displayDice();
            }
            findCategoryScore();
            reroll = computerDecide();
            num_rolls++;
        }

        if (keepError && !isScoreSet) {
            int highestIndex = -1;
            int highestScore = -1;
            findCategoryScore();

            for (int i = 0; i < scoresAvailable.size(); i++) {
                if (!board.isCategoryFill(scoresAvailable.get(i).first) && scoresAvailable.get(i).second > highestScore) {
                    highestScore = scoresAvailable.get(i).second;
                    highestIndex = i;
                }
            }

            if (highestIndex != -1) {
                board.setScore(scoresAvailable.get(highestIndex).first, 2);
                isScoreSet = true;
            }
        }

        if (!isScoreSet) {
            System.out.println("Nothing to score, so skipping turn");
        }

        System.out.println("\n\nComputer's Turn Ended!");
    }

    public void findCategoryScore() {
        scoresAvailable.clear();

        if (!availableCategory.isEmpty()) {
            for (int i = 1; i <= DICE_COUNT; i++) {
                int score = board.calculateUpperSectionScore(i);
                if (score != 0) {
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

    public boolean computerDecide() {
        isScoreSet = false;

        for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
            if (scoresAvailable.get(i).second >= 20 && !board.isCategoryFill(scoresAvailable.get(i).first)) {
                board.setScore(scoresAvailable.get(i).first, 2);
                isScoreSet = true;
                return true;
            }
        }

        if (!isScoreSet && num_rolls <= 3) {
            if (lowerSectionFilled()) {
                if (!availableCategory.isEmpty()) {
                    scoreHighestAvailable();
                    return true;
                } else {
                    playRoll();
                    board.updateDice(dice);
                    board.displayAvailableCombinations();
                    return true;
                }
            } else {
                for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
                    if (scoresAvailable.get(i).second >= 10 && !board.isCategoryFill(scoresAvailable.get(i).first)) {
                        board.setScore(scoresAvailable.get(i).first, 2);
                        isScoreSet = true;
                        return true;
                    }
                }

                for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
                    if ((scoresAvailable.get(i).first + 1 == 7 || scoresAvailable.get(i).first + 1 == 8) &&
                            !board.isCategoryFill(scoresAvailable.get(i).first)) {
                        board.setScore(scoresAvailable.get(i).first, 2);
                        isScoreSet = true;
                        return true;
                    }
                }

                if (isSequentialAvailable()) {
                    board.updateDice(dice);
                    board.displayAvailableCombinations();
                    return false;
                } else {
                    tryYahtzeeOrFullHouse();
                    return false;
                }
            }
        } else {
            scoreHighestAvailable();
            return true;
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

    public void scoreHighestAvailable() {
        for (int i = scoresAvailable.size() - 1; i >= 0; i--) {
            if (!board.isCategoryFill(scoresAvailable.get(i).first)) {
                board.setScore(scoresAvailable.get(i).first, 2);
                break;
            }
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
}

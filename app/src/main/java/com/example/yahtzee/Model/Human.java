package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Human extends Player {
	
	public class Pair<K, V> {
	    public K first;
	    public V second;

	    public Pair(K first, V second) {
	        this.first = first;
	        this.second = second;
	    }
	}


    private String user_dice;
    private int dice_to_reroll;
    private int dice_num;
    private int category_choice;
    private int diceLocation;

    private boolean helpShown;
    private boolean keepFlag;

    private ArrayList<Integer> keepDice = new ArrayList<>();
    private ArrayList<Pair<Integer, Integer>> availableScores = new ArrayList<>();
    public Combinations help_board = new Combinations();

    public Human() {
        player_id = 1; // 1 for human
        user_dice = "";
        dice_to_reroll = 0;
        dice_num = 0;
        category_choice = 0;
        diceLocation = 0;
        helpShown = false;
        keepFlag = false;
    }


    public void playTurnt(){
        // need the dice rolls
        // depending on the dice rolls,


    }

    @Override
    public void playTurn() {
        System.out.println("\nYou are playing.....");

        playRoll(); // Roll the dice
        displayDice(); // Display current dice values

        Combinations board = new Combinations(dice); // Initialize combinations based on dice
        board.displayScorecard();
        board.displayAvailableCombinations();
        combinations = board.availableCombinations();
        //viewHelp(dice); // Display help
        keepDice.clear();

        while (num_rolls < MAX_ROLLS && player_choice != 'N') {
            System.out.println("\n\nDo you wish to roll again (Y/N): ");
            System.out.print("Your choice (Y/N): ");
            Scanner scanner = new Scanner(System.in);
            player_choice = scanner.next().charAt(0);

            while (player_choice != 'Y' && player_choice != 'N') {
                System.out.print("Invalid input. Please enter 'Y' or 'N': ");
                player_choice = scanner.next().charAt(0);
            }

            if (player_choice == 'Y') {
                reRoll();
                displayDice();
                board.updateDice(dice);
                board.displayAvailableCombinations();
                combinations = board.availableCombinations();
                viewHelp(dice);
                num_rolls++;
            }
        }

        if (board.hasScoreableCategory()) {
            scoreCategory(board);
        } else {
            System.out.println("No category available to score. Next turn will be played by the other player.");
        }

        System.out.println("\n\nYour Turn Ended!");
    }


    public int playTurn(int player_id, ArrayList<Integer> dice,int category_choice){
        Combinations board = new Combinations(dice); // Initialize combinations based on dice
        combinations = board.availableCombinations();
        scoreCategoryt(board,category_choice, player_id);
        return 0;
    }


    public void scoreCategoryt(Combinations board, int category_choice, int player_id) {
        board.setScore(category_choice-1, player_id);
    }


    public void customFill() {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < DICE_COUNT; i++) {
            do {
                System.out.print("Enter the value for dice " + (i + 1) + ": ");
                dice_num = scanner.nextInt();
                if (dice_num < 1 || dice_num > 6) {
                    System.out.println("Invalid entry! Enter values from 1-6.");
                } else {
                    dice.set(i, dice_num);
                }
            } while (dice_num < 1 || dice_num > 6);
        }
    }

    public void scoreCategory(Combinations board) {
        boolean hasScored = false;
        Scanner scanner = new Scanner(System.in);
        if (!board.hasScoreableCategory()) {
            System.out.println("No category available to score. Next turn will be played by the other player.");
        } else {
            do {
                System.out.print("Enter the category number you wish to score: ");
                category_choice = scanner.nextInt();

                while (category_choice < 1 || category_choice > 12) {
                    System.out.print("Invalid input. Please enter a number between 1 and 12: ");
                    category_choice = scanner.nextInt();
                }

                if (board.isCategoryFill(category_choice - 1)) {
                    System.out.println("Category already scored. Please choose another category.");
                } else {
                    board.setScore(category_choice - 1, player_id);
                    hasScored = true;
                }
            } while (!hasScored);
        }
    }

    public void reRoll() {
        System.out.println("Enter the dice numbers you wish to roll again, separated by spaces.");
        if (keepDice.isEmpty()) {
            System.out.println("If you want to re-roll all dice, enter 0.");
        } else {
            System.out.println("Note: These dice were kept previously: ");
            for (int index : keepDice) {
                System.out.print(dice.get(index) + " ");
            }
            System.out.println();
        }

        System.out.print("Your choice: ");
        Scanner scanner = new Scanner(System.in);
        user_dice = scanner.nextLine();
        StringTokenizer tokenizer = new StringTokenizer(user_dice);

        ArrayList<Integer> dicesToReroll = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            dice_to_reroll = Integer.parseInt(tokenizer.nextToken());
            if (dice_to_reroll == 0 && keepDice.isEmpty()) {
                playRoll();
                keepDice.clear();
                return;
            } else if (dice_to_reroll > 0 && dice_to_reroll <= 6) {
                dicesToReroll.add(dice_to_reroll - 1);
            }
        }

        System.out.print("Do you wish to manually input the selected dice? (Y/N): ");
        String choice = scanner.nextLine();
        Random rand = new Random();

        if (choice.equalsIgnoreCase("Y")) {
            for (int dieIndex : dicesToReroll) {
                do {
                    System.out.print("Enter the value for dice " + (dieIndex + 1) + ": ");
                    dice.set(dieIndex, scanner.nextInt());
                } while (dice.get(dieIndex) < 1 || dice.get(dieIndex) > 6);
            }
        } else if (choice.equalsIgnoreCase("N")) {
            System.out.println("Rolling the selected dice randomly...");
            for (int dieIndex : dicesToReroll) {
                dice.set(dieIndex, rand.nextInt(6) + 1);
            }
        }
    }

    public void findScorebyCategory() {
        availableScores.clear();
        if (!help_board.availableCombinations().isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                int score = help_board.calculateUpperSectionScore(i);
                if (score != 0) {
                    availableScores.add(new Pair<>(i - 1, score));
                }
            }

            int totalDice = help_board.sumAllDice();

            if (help_board.hasThreeOfAKind() && !help_board.isCategoryFill(6)) {
                availableScores.add(new Pair<>(6, totalDice));
            }
            if (help_board.hasFourOfAKind() && !help_board.isCategoryFill(7)) {
                availableScores.add(new Pair<>(7, totalDice));
            }
            if (help_board.hasFullHouse() && !help_board.isCategoryFill(8)) {
                availableScores.add(new Pair<>(8, 25));
            }
            if (help_board.hasFourStraight() && !help_board.isCategoryFill(9)) {
                availableScores.add(new Pair<>(9, 30));
            }
            if (help_board.hasFiveStraight() && !help_board.isCategoryFill(10)) {
                availableScores.add(new Pair<>(10, 40));
            }
            if (help_board.hasYahtzee() && !help_board.isCategoryFill(11)) {
                availableScores.add(new Pair<>(11, 50));
            }
        }
    }

    public void viewHelp(ArrayList<Integer> dice) {
        int highestIndex = -1;
        int highestScore = -1;
        keepFlag = false;

        help_board.updateDice(dice);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to use help? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            findScorebyCategory();
            for (Pair<Integer, Integer> score : availableScores) {
                if (score.second > highestScore && !help_board.isCategoryFill(score.first)) {
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

    public boolean lowerSectionFilled() {
        for (int i = 6; i < 12; i++) {
            if (Scorecard.scoreCard.get(i).player_id == 0) return false;
        }
        return true;
    }
}

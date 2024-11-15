package com.example.yahtzee.Model;
import java.util.Random;
import java.util.Scanner;

public class Tournament {
    private static final int HUMAN = 1;     // ID for the human player
    private static final int COMPUTER = 2;  // ID for the computer player


    //private int humanRoll;
    //private int computerRoll;
    private Serialization serialize;
    public Round round;

    public static int currentPlayerId;

    public Tournament() {
      //  humanRoll = 0;
        //computerRoll = 0;
       // serialize = new Serialization();
        round = new Round();
        currentPlayerId = 0;
    }

    // Starts the tournament
    public void startTournament(int player_id) {
        //Scorecard scorecard = new Scorecard();
        Round round = new Round();
        currentPlayerId = player_id;


        /*
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nLet's roll the dice to see who gets the highest!\n");
        System.out.println("Press Enter to roll the dice");
        scanner.nextLine();  // Waiting for user input

        Random rand = new Random();

        do {
            humanRoll = rand.nextInt(6) + 1;
            computerRoll = rand.nextInt(6) + 1;

            System.out.println("Human rolled: " + humanRoll);
            System.out.println("Computer rolled: " + computerRoll);

            if (humanRoll > computerRoll) {
                System.out.println("Congrats! You go first!");
            } else if (humanRoll < computerRoll) {
                System.out.println("Computer goes first!");
            } else {
                System.out.println("It's a tie! Let's roll again!");
                System.out.println("Press Enter to continue");
                scanner.nextLine();
            }
        } while (humanRoll == computerRoll);

        // Start the round with the player who won the dice roll
        if (humanRoll > computerRoll) {
            round.playRound(HUMAN);
        } else {
            round.playRound(COMPUTER);
        }
        */

        // Continue playing rounds until the scorecard is full
       // do {
         //   round.playRound(scorecard.playerWithLowestScore());
        //} while (!scorecard.isScorecardFull());

        // Display the scorecard and winner
        // System.out.println();
        //scorecard.displayScorecard();
        //displayWinner(scorecard.getTotal(HUMAN), scorecard.getTotal(COMPUTER));
    }







    // Loads a tournament from a file
    public void loadTournament() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file name: ");
        String fileName = scanner.nextLine();

        if (!serialize.validateFile(fileName)) {
            System.err.println("Error: File not found.");
            return;
        }

        if (!serialize.readFile(fileName)) {
            System.err.println("Error processing file.");
        } else {
            serialize.displayLoadedScorecard();
            Scorecard scorecard = new Scorecard();
            Round round = new Round();
            Round.numOfRounds = serialize.getCurrentRound();

            System.out.println("\n\nHuman Score: " + scorecard.getTotal(HUMAN));
            System.out.println("Computer Score: " + scorecard.getTotal(COMPUTER));

            do {
                round.resumeRound(scorecard.playerWithLowestScore());
            } while (!scorecard.isScorecardFull());

            System.out.println();
            scorecard.displayScorecard();
            displayWinner(scorecard.getTotal(HUMAN), scorecard.getTotal(COMPUTER));
        }
    }

    // Displays the winner of the tournament
    public void displayWinner(int player1, int player2) {
        System.out.println("\n\nYour Score: " + player1);
        System.out.println("Computer's Score: " + player2);

        System.out.println("\n---------------------------");
        if (player1 > player2) {
            System.out.println("You win!");
        } else if (player1 < player2) {
            System.out.println("Computer wins!");
        } else {
            System.out.println("It's a tie!");
        }
        System.out.println("---------------------------");
    }
}

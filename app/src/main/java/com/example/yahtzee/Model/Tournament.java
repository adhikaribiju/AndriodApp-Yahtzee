package com.example.yahtzee.Model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
// Loads a tournament from a file
    public boolean loadTournament(BufferedReader fileReader) {
        int currentRound = 0;

        initScorecard();
        try {
            String line;

            // Read the first line and check for "Round:"
            line = fileReader.readLine();
            if (line == null || !line.startsWith("Round:")) {
                System.err.println("Error: Invalid format, missing round information.");
                return false;
            }
            currentRound = Integer.parseInt(line.split(":")[1].trim());

            fileReader.readLine(); // Skip the blank line
            line = fileReader.readLine(); // Read "Scorecard:" line
            if (line == null || !line.equals("Scorecard:")) {
                System.err.println("Error: Expected 'Scorecard:' line.");
                return false;
            }

            int i = 0; // Index for scorecard entries
            while ((line = fileReader.readLine()) != null) {
                if (line.isEmpty()) continue; // Skip empty lines

                String[] parts = line.split(" ");
                int score = Integer.parseInt(parts[0]);
                if (score == 0) {
                    i++;
                    continue; // Skip unfilled categories
                }

                String player = parts[1];
                int round_no = Integer.parseInt(parts[2]);

                // Update the scorecard
                Scorecard.scoreCard.get(i).score = score;
                Scorecard.scoreCard.get(i).round_no = round_no;

                if (player.equals("Human")) {
                    Scorecard.scoreCard.get(i).player_id = 1;
                } else if (player.equals("Computer")) {
                    Scorecard.scoreCard.get(i).player_id = 2;
                } else {
                    System.err.println("Error: Invalid player information in scorecard.");
                    return false;
                }
                i++;
            }

            // Update round and determine the player with the lowest score
            Round.numOfRounds = currentRound;

            Scorecard scorecard = new Scorecard();
            int humanScore = scorecard.getTotal(HUMAN);
            int computerScore = scorecard.getTotal(COMPUTER);

            // Determine the player with the lowest score
            if (humanScore < computerScore) {
                currentPlayerId = HUMAN; // Set to human's player ID
            } else if (computerScore < humanScore) {
                currentPlayerId = COMPUTER; // Set to computer's player ID
            } else {
                currentPlayerId = HUMAN; // Default to HUMAN for tie case
            }

            return true; // Successfully read the file
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return false;
        }



//        if (!serialize.readFile(fileName)) {
//            System.err.println("Error processing file.");
//            return false;
//        } else {
//
//
//            Round round = new Round();
//            Scorecard scorecard = new Scorecard();
//
//            Round.numOfRounds = serialize.getCurrentRound();
//
//
//            int humanScore = scorecard.getTotal(HUMAN);
//            int computerScore = scorecard.getTotal(COMPUTER);
//            int lowestScore = Math.min(humanScore, computerScore);
//
//
//            currentPlayerId = lowestScore;
//
//            return true;
//
//        }
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

    public void initScorecard(){
        for(int i=0;i<12;i++){
            Scorecard.scoreCard.get(i).player_id = 0;
            Scorecard.scoreCard.get(i).score = 0;
            Scorecard.scoreCard.get(i).round_no = 0;
        }
    }

    public void saveGame(BufferedWriter outFile) {
        try {
            Round current_round = new Round();

            // Write the current round information
            outFile.write("Round: " + current_round.getRoundNo()  + "\n\n");

            // Write the scorecard header
            outFile.write("Scorecard:\n");

            // Write each scorecard entry
            for (int i = 0; i < 12; i++) {
                if (Scorecard.scoreCard.get(i).score == 0) {
                    outFile.write("0\n"); // Write 0 if the score is not filled
                } else {
                    String player = (Scorecard.scoreCard.get(i).player_id == 1) ? "Human" : "Computer";
                    outFile.write(
                            Scorecard.scoreCard.get(i).score + " " + player + " " + Scorecard.scoreCard.get(i).round_no + "\n"
                    );
                }
            }

            // Flush the BufferedWriter to ensure data is written to the file
            outFile.flush();

            System.out.println("Scorecard saved successfully.");
        } catch (IOException e) {
            System.err.println("Error: Could not write to the file.");
        }
    }

}

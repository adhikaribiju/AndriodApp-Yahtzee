package com.example.yahtzee.Model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class Tournament {

    /**
     * private data members
     */
    private final Round round;

    private static int currentPlayerId;

    /**
     * Constructs a new tournament with a round.
     */
    public Tournament() {
        round = new Round();
        currentPlayerId = 0;
    }


    /**
     * setters and getters
     */
    public Round getRound() {
        return round;
    }

    public int getCurrentPlayerId(){
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int id){
        currentPlayerId = id;
    }


    /**
     * Starts a new tournament with the specified player.
     * @param player_id The ID of the player starting the tournament.
     */
    public void startTournament(int player_id) {
        //Scorecard scorecard = new Scorecard();
        Round round = new Round();
        currentPlayerId = player_id;
    }

    /**
     * Loads a saved game from the specified file.
     * @param outFile The BufferedReader object to read the file.
     */
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

    /**
     * Initializes the scorecard with default values.
     */
    public void initScorecard(){
        for(int i=0;i<12;i++){
            Scorecard.scoreCard.get(i).player_id = 0;
            Scorecard.scoreCard.get(i).score = 0;
            Scorecard.scoreCard.get(i).round_no = 0;
        }
    }


    /**
     * Loads a tournament from the specified file.
     * @param fileReader The BufferedReader object to read the file.
     * @return (boolean) True if the file was successfully read, false otherwise.
     */
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
            Round.setNumOfRounds(currentRound);


            Scorecard scorecard = new Scorecard();
            int humanScore = scorecard.getTotal(1);
            int computerScore = scorecard.getTotal(2);

            // Determine the player with the lowest score
            if (humanScore < computerScore) {
                currentPlayerId = 1; // Set to human's player ID
            } else if (computerScore < humanScore) {
                currentPlayerId = 2; // Set to computer's player ID
            } else {
                currentPlayerId = 1; // Default to HUMAN for tie case
            }

            return true; // Successfully read the file
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return false;
        }

    }

}

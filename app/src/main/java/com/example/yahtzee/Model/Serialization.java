package com.example.yahtzee.Model;
import java.io.*;
import java.util.Scanner;
import java.io.BufferedReader;

import java.io.IOException;


public class Serialization {
    private int currentRound;
    private Scorecard scorecard;

    public Serialization() {
        this.currentRound = 0;
        this.scorecard = new Scorecard();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean validateFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public boolean readFile(BufferedReader file) {
        try {
            String line;

            // Read the first line and check for "Round:"
            line = file.readLine();
            if (line == null || !line.startsWith("Round:")) {
                System.err.println("Error: Invalid format, missing round information.");
                return false;
            }
            currentRound = Integer.parseInt(line.split(":")[1].trim());

            file.readLine(); // Skip the blank line
            line = file.readLine(); // Read "Scorecard:" line
            if (line == null || !line.equals("Scorecard:")) {
                System.err.println("Error: Expected 'Scorecard:' line.");
                return false;
            }

            int i = 0; // Index for scorecard entries
            while ((line = file.readLine()) != null) {
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

            return true; // Successfully read the file
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return false;
        }
    }


    public void saveGame(BufferedWriter outFile) {
        try {
            Round current_round = new Round();

            // Write the current round information
            outFile.write("Round: " + (current_round.getRoundNo() - 1) + "\n\n");

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


    public void displayLoadedScorecard() {
        scorecard.displayScorecard();
    }
}

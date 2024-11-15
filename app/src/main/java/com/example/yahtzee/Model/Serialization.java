package com.example.yahtzee.Model;
import java.io.*;
import java.util.Scanner;

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

    public boolean readFile(String fileName) {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            String line;

            line = file.readLine();
            if (line == null || !line.startsWith("Round:")) {
                System.err.println("Error: Invalid format, missing round information.");
                return false;
            }
            currentRound = Integer.parseInt(line.split(":")[1].trim());

            file.readLine(); 
            line = file.readLine(); 
            if (line == null || !line.equals("Scorecard:")) {
                System.err.println("Error: Expected 'Scorecard:' line.");
                return false;
            }

            int i = 0; 
            while ((line = file.readLine()) != null) {
                if (line.isEmpty()) continue; 

                String[] parts = line.split(" ");
                int score = Integer.parseInt(parts[0]);
                if (score == 0) {
                    i++;
                    continue; 
                }

                String player = parts[1];
                int round_no = Integer.parseInt(parts[2]);

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

            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return false;
        }
    }

    public boolean saveGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the file to save the scorecard: ");
        String fileName = scanner.nextLine() + ".txt";

        try (FileWriter outFile = new FileWriter(fileName)) {
            Round current_round = new Round();

            outFile.write("Round: " + (current_round.getRoundNo() - 1) + "\n\n");
            outFile.write("Scorecard:\n");

            for (int i = 0; i < 12; i++) {
                if (Scorecard.scoreCard.get(i).score == 0) {
                    outFile.write("0\n");
                } else {
                    String player = (Scorecard.scoreCard.get(i).player_id == 1) ? "Human" : "Computer";
                    outFile.write(Scorecard.scoreCard.get(i).score + " " + player + " " + Scorecard.scoreCard.get(i).round_no + "\n");
                }
            }

            System.out.println("Scorecard saved successfully to " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("Error: Could not open the file for writing.");
            return false;
        }
    }

    public void displayLoadedScorecard() {
        scorecard.displayScorecard();
    }
}

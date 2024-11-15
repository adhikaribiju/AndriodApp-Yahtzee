package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.Arrays;

class Card {
    String name;
    int score;
    int player_id;
    int round_no;

    public Card(String name, int score, int player_id, int round_no) {
        this.name = name;
        this.score = score;
        this.player_id = player_id;
        this.round_no = round_no;
    }
}

public class Scorecard {

    public static ArrayList<Card> scoreCard = new ArrayList<>(Arrays.asList(
            new Card("Aces", 0, 0, 0),
            new Card("Twos", 0, 0, 0),
            new Card("Threes", 0, 0, 0),
            new Card("Fours", 0, 0, 0),
            new Card("Fives", 0, 0, 0),
            new Card("Sixes", 0, 0, 0),
            new Card("Three of a Kind", 0, 0, 0),
            new Card("Four of a Kind", 0, 0, 0),
            new Card("Full House", 0, 0, 0),
            new Card("Four Straight", 0, 0, 0),
            new Card("Five Straight", 0, 0, 0),
            new Card("Yahtzee", 0, 0, 0)
    ));

    private ArrayList<String> categories;

    public Scorecard() {
        categories = new ArrayList<>();
        categories.add("Aces");
        categories.add("Twos");
        categories.add("Threes");
        categories.add("Fours");
        categories.add("Fives");
        categories.add("Sixes");
        categories.add("Three of a Kind");
        categories.add("Four of a Kind");
        categories.add("Full House");
        categories.add("Four Straight");
        categories.add("Five Straight");
        categories.add("Yahtzee");

        // Initialize the scoreCard
//        scoreCard.add(new Card("Aces", 0, 0, 0));
//        scoreCard.add(new Card("Twos", 0, 0, 0));
//        scoreCard.add(new Card("Threes", 0, 0, 0));
//        scoreCard.add(new Card("Fours", 0, 0, 0));
//        scoreCard.add(new Card("Fives", 0, 0, 0));
//        scoreCard.add(new Card("Sixes", 0, 0, 0));
//        scoreCard.add(new Card("Three of a Kind", 0, 0, 0));
//        scoreCard.add(new Card("Four of a Kind", 0, 0, 0));
//        scoreCard.add(new Card("Full House", 0, 0, 0));
//        scoreCard.add(new Card("Four Straight", 0, 0, 0));
//        scoreCard.add(new Card("Five Straight", 0, 0, 0));
//        scoreCard.add(new Card("Yahtzee", 0, 0, 0));
    }


    public void resetScorecard(){
        // Initialize the scoreCard
        scoreCard.clear();
      scoreCard.add(new Card("Aces", 0, 0, 0));
        scoreCard.add(new Card("Twos", 0, 0, 0));
       scoreCard.add(new Card("Threes", 0, 0, 0));
        scoreCard.add(new Card("Fours", 0, 0, 0));
        scoreCard.add(new Card("Fives", 0, 0, 0));
       scoreCard.add(new Card("Sixes", 0, 0, 0));
        scoreCard.add(new Card("Three of a Kind", 0, 0, 0));
     scoreCard.add(new Card("Four of a Kind", 0, 0, 0));
        scoreCard.add(new Card("Full House", 0, 0, 0));
        scoreCard.add(new Card("Four Straight", 0, 0, 0));
        scoreCard.add(new Card("Five Straight", 0, 0, 0));
        scoreCard.add(new Card("Yahtzee", 0, 0, 0));
    }


    public int getCategoryScore(int category) {
        return scoreCard.get(category).score;
    }

    public int getTotal(int player_id) {
        int sum = 0;
        for (Card card : scoreCard) {
            if (card.player_id == player_id) {
                sum += card.score;
            }
        }
        return sum;
    }

    public boolean isScorecardFull() {
        for (Card card : scoreCard) {
            if (card.score == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isCategoryFill(int category_id) {
        return scoreCard.get(category_id).score != 0;
    }

    public int playerWithLowestScore() {
        int player1_score = getTotal(1);
        int player2_score = getTotal(2);
        return (player1_score < player2_score) ? 1 : 2;
    }

    public void setScore(int category_id, int score, int player_id) {
        scoreCard.get(category_id).score = score;
        scoreCard.get(category_id).player_id = player_id;
    }

    public void displayScorecard() {
        System.out.println("\n\t\t\tScorecard");
        System.out.println("\n--------------------------------------------------------------");
        System.out.printf("%-17s %-25s %-10s %-10s %-10s\n", "Category No.", "Category", "Score", "Player", "Round");
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < 12; i++) {
            Card card = scoreCard.get(i);
            String player = (card.player_id == 1) ? "You" : (card.player_id == 2) ? "Computer" : "X";
            System.out.printf("%-17d %-25s %-10d %-10s %-10d\n", i + 1, card.name, card.score, player, card.round_no);
        }
    }
}

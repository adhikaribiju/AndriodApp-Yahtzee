package com.example.yahtzee.Model;
import java.util.ArrayList;
import java.util.Arrays;

class Card {

    /**
     * private data members
     */
    String name;
    int score;
    int player_id;
    int round_no;

    /**
     * Default Constructor
     */
    public Card(String name, int score, int player_id, int round_no) {
        this.name = name;
        this.score = score;
        this.player_id = player_id;
        this.round_no = round_no;
    }
}

public class Scorecard {

    /**
     * private data member
     */
    private ArrayList<String> categories;


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

    /**
     * Default Constructor
     */
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
    }



    /**
     * Method to get the score of a category
     * @param category The category to get the score of
     * @return The score of the category
     */
    public int getCategoryScore(int category) {
        return scoreCard.get(category).score;
    }


    /**
     * Method to get the total score of the given player
     * @param player_id (int) The player id
     * @return int The total score of the player
     */
    public int getTotal(int player_id) {
        int sum = 0;
        for (Card card : scoreCard) {
            if (card.player_id == player_id) {
                sum += card.score;
            }
        }
        return sum;
    }

    /**
     * Method to check if the scorecard is full
     * @return boolean True if the scorecard is full, false otherwise
     */
    public boolean isScorecardFull() {
        for (Card card : scoreCard) {
            if (card.score == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to check if a category is filled
     * @param category_id (int) The category id
     * @return boolean True if the category is filled, false otherwise
     */
    public boolean isCategoryFill(int category_id) {
        return scoreCard.get(category_id).score != 0;
    }

    /**
     * Method to get the player with the lowest score
     * @return int The player with the lowest score
     */
    public int playerWithLowestScore() {
        int player1_score = getTotal(1);
        int player2_score = getTotal(2);
        return (player1_score < player2_score) ? 1 : 2;
    }

    /**
     * Method to set the score of a category
     * @param category_id (int) The category id
     * @param score (int) The score to set
     * @param player_id (int) The player id
     */

    public void setScore(int category_id, int score, int player_id) {
        scoreCard.get(category_id).score = score;
        scoreCard.get(category_id).player_id = player_id;
    }


    /**
     * Method to get the category score of the given category num if it is set
     * @param categoryNum (int) The category number
     */
    public int getSetCategoryScore(int categoryNum){
        Card card = scoreCard.get(categoryNum);
        return card.score;
    }

    /**
     * Method to get the player id of the given category num if it is set
     * @param categoryNum (int) The category number
     */
    public int getSetPlayerId(int categoryNum){
        Card card = scoreCard.get(categoryNum);
        return card.player_id;
    }

    /**
     * Method to get the round number of the given category num if it is set
     * @param categoryNum (int) The category number
     */
    public int getSetRoundNo(int categoryNum){
        Card card = scoreCard.get(categoryNum);
        return card.round_no;
    }

    /**
     * Method to reset the scorecard
     */
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


}

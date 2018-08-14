import java.util.ArrayList;
import java.lang.*;
import javax.swing.*;
import javax.swing.ImageIcon;
/*
user [][][]
money[][][]
credit[][][]

Class used to create a scoreboard object
Scoreboard holds a list of users, a scoreboard table
And a list that has indexed costs to upgrade in money and credits
*/
public class Scoreboard{
    //attributes
    private ArrayList<User> listOfUsers;
    private int[][] scoreboard_table;
    public ArrayList<Integer> upgrade_cost_money;
    public ArrayList<Integer> upgrade_cost_credits;
    private int  thewinnerScore;
    public Scoreboard(ArrayList<User> listOfUsers){
    }
    /*
    creates scores for each player and compares the scores to determine the high score(s) and player associated with them
     */
    public ArrayList<User> calculate_winner(){
        int tempScore;
        int[] totalScores = new int[listOfUsers.size()];
        ArrayList<User> winnerName = new ArrayList<User>();
        int winnerScore = 0;
        //calculate and print out individual information for each User
        for(int i = 0; i < listOfUsers.size(); i++){
            tempScore = scoreboard_table[i][0] + scoreboard_table[i][1] + (listOfUsers.get(i).getRank() * 5);
            totalScores[i] = tempScore;
        }
        //compare scores in totalScores array
        for(int j=0; j< listOfUsers.size(); j++){

            //if the score is higher than the current score:
            //clear winner names and update top score
            if(winnerScore < totalScores[j]){
                winnerScore = totalScores[j];
                winnerName.clear();
                winnerName.add(listOfUsers.get(j));
            }
            //if the score is equal to top score, then add the players name to the list of winners
            else if(winnerScore == totalScores[j]){
                winnerName.add(listOfUsers.get(j));
            }
        }
        thewinnerScore = winnerScore;
        return winnerName;
    }

    public int getWinnerScore(){
        return this.thewinnerScore;
    }
    /*
    Takes in a list of the costs to upgrade with money by level and sets it within the object
     */
    public void setUpgrade_cost_money(ArrayList<Integer> upgrade_cost_money){
        this.upgrade_cost_money = upgrade_cost_money;
    }

    public ArrayList<User> getListOfUsers(){
        return listOfUsers;
    }

    /*
    Takes in a list of the costs to upgrade with credit by level and sets it within the object
     */
    public void setUpgrade_cost_credits(ArrayList<Integer> upgrade_cost_credits){
        this.upgrade_cost_credits = upgrade_cost_credits;
    }

    // Takes in a list of users and intializes the scoreboard object (with money and credits starting at 0)
    // and list of users.
    public void setListOfUsers(ArrayList<User> listOfUsers){
        this.listOfUsers = listOfUsers;
        this.scoreboard_table = new int[listOfUsers.size()][2];

        int i = 0;
        for(User usr : listOfUsers){
            scoreboard_table[i][0] = 0;
            scoreboard_table[i][1] = 0;
            i++;
        }
    }

    // Takes in a user and finds their current index (so we can index into the scoreboard array)
    public int findUserIndex(User user){
        int iter = 0;
        for(User cur_user : listOfUsers){
            if(cur_user == user){
                return iter;
            }
            iter++;
        }
        return -1;
    }

    /*
    Takes in a User object, a type of currency to upgrade with and a desired level
    Determines whether or not a user can upgrade to that level
    And if so, sets their level to that and subtracts the cost of upgrading from their preferred type
     */
    public void upgrade(User user, String type, int level, BoardLayersListener board){
        int userIndex = findUserIndex(user);
        if(userIndex == -1) {
            JOptionPane.showMessageDialog(board, "That's a nonexistant user!");
            return;
        }
        int userMoney = scoreboard_table[userIndex][0];
        int userCredit = scoreboard_table[userIndex][1];

        if(level <= getRank(user) || level < 2 || level > 6){
            JOptionPane.showMessageDialog(board, "You cannot upgrade to that level!");
            board.setfailedupgradebuttons();
            board.setHasActed(false);
            return;
        }
        if(type.equals("$")){
            int money_needed = upgrade_cost_money.get(level - 2);
            int money_remaining = userMoney - money_needed; // get the users indexed money and subtract by money needed
            // if less then 0 print out error and do nothing. Otherwise sent rank to be new and set money to be new
            if(money_remaining < 0){
                JOptionPane.showMessageDialog(board, "You do not have enough money to upgrade to that level!");
                board.setfailedupgradebuttons();
                board.setHasActed(false);
            }
            else{
                scoreboard_table[userIndex][0] = money_remaining;
                user.setRank(level);
                ImageIcon img = new ImageIcon ("dice/"+ user.getDisplayName() + Integer.toString(level) + ".png");
                user.getUserGUI().getJlabel().setIcon(img);
                board.updateCurUser();
                board.setonlyEndbuttons();
                board.setHasActed(true);
            }
        }
        else if(type.equals("cr")){
            int credits_needed = upgrade_cost_credits.get(level - 2);
            int credit_remaining = userCredit - credits_needed;
            if(credit_remaining < 0){
                JOptionPane.showMessageDialog(board,"You do not have enough credits to upgrade to that level!");
                board.setfailedupgradebuttons();
                board.setHasActed(false);
            }
            else{
                scoreboard_table[userIndex][1] = credit_remaining;
                user.setRank(level);
                ImageIcon img = new ImageIcon ("dice/"+ user.getDisplayName() + Integer.toString(level) + ".png");
                user.getUserGUI().getJlabel().setIcon(img);
                board.updateCurUser();
                board.setonlyEndbuttons();
                board.setHasActed(true);
            }
        }
    }

    /*
    Takes a user and a amount of credits to be added to that user
    Finds the users index in the scoreboard table and adds the credits to their credits
     */
    public void addCredit(User current_user, int addedAmount){
        int usr_idx = findUserIndex(current_user);
        int currentAmount = scoreboard_table[usr_idx][1];
        scoreboard_table[usr_idx][1] = currentAmount + addedAmount;
    }

    /*
    Takes a user and a amount of money to be added to that user
    Finds the users index in the scoreboard table and adds the money to their money
     */
    public void addMoney(User current_user, int addedAmount){
        int usr_idx = findUserIndex(current_user);
        if(usr_idx == -1){
            return;
        }
        int currentAmount = scoreboard_table[usr_idx][0];
        scoreboard_table[usr_idx][0] = currentAmount + addedAmount;
    }

    // Takes in a User and returns the users money
    public int getMoney(User current_user){  //need user to get money for that particular user

        for(int i = 0; i < scoreboard_table.length; i++){
            if(listOfUsers.get(i) == current_user){
                return scoreboard_table[i][0];
            }
        }
        return -1;
    }

    // Takes in a User and returns the users credits
    public int getCredit(User current_user){

        for(int i = 0; i < scoreboard_table.length; i++){
            if(listOfUsers.get(i) == current_user){
                return scoreboard_table[i][1];
            }
        }
        return -1;
    }

    // Takes in a user and returns that users rank
    public int getRank(User current_user){
       return current_user.getRank();
    }
}

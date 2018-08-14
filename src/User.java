import javax.swing.*;

/*
Class used to create a User object
Which holds the users name, rank, Position object (has users scene, room, role)
 */
public class User {

  private String name;
  private int rank;
  private Position position;
  private GUIUser userGUI;
  private JLabel scoreboardLabel;
  private String displayName;
  private int userNumber;

  public User(String name, Room startingRoom, GUIUser userGUI, String dname, int userNumber) {
    this.name = name;
    this.rank = 1;
    this.position = new Position(startingRoom, null, null);
    this.userGUI = userGUI;
    this.displayName = dname;
    this.userNumber = userNumber;
  }

  public int getUserNumber(){
    return userNumber;
  }

  public void setScoreboardLabel(JLabel scoreboardLabel){
    this.scoreboardLabel = scoreboardLabel;
  }

  public JLabel getScoreboardLabel(){
    return scoreboardLabel;
  }

  public String getName() { 
    return this.name;
  }

  public String getDisplayName(){ return this.displayName;}

  public int getRank() {
    return this.rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public Position getPosition() {
    return this.position;
  }

  public GUIUser getUserGUI(){
    return userGUI;
  }

}

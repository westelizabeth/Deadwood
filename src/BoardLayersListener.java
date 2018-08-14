/*
    Class Name: BoardLayersListerner.java
    Description: We eliminated the Controller class combining the controller aspects with the view in this class.
   Classes Used: JFrame, JLabel, JButton, JLayeredPane
*/
//imports
import javax.swing.JOptionPane;
import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.util.ArrayList;

public class BoardLayersListener extends JFrame {

  // Private Attributes
  private GameSystem the_system;
  private boolean hasActed = false;
  private boolean hasMoved = false;

  // JLabels
  JLabel boardlabel;
  JLabel cardlabel;
  JLabel mLabel;
  JLabel cardBackLabel;
  JLabel Box;
  JLabel cur_user_label;
  JLabel cur_user_img_label;

  //JButtons
  JButton bAct;
  JButton bRehearse;
  JButton bMove;
  JButton bWork;
  JButton bEndTurn;
  JButton bUpgrade;
  JButton endGame;

  // JLayered Pane
  JLayeredPane bPane;

  //JComboBox
  JComboBox moveChoices;
  JComboBox workChoices;
  JComboBox numOfUsers;
  JComboBox currencyBox;
  JComboBox levelBox;

  // Constructor
  public BoardLayersListener(GameSystem the_system) {
      // Set the title of the JFrame
      super("Deadwood");
      this.the_system = the_system;
      // Set the exit option for the JFrame
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      super.setResizable(true);

      // Create the JLayeredPane to hold the display, cards, dice and buttons
      bPane = getLayeredPane();

      // Create the deadwood board
      boardlabel = new JLabel();
      ImageIcon icon = new ImageIcon("mainBoard.jpg");
      boardlabel.setIcon(icon);
      boardlabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());

      // Add the board to the lowest layer
      bPane.add(boardlabel, new Integer(0));
      // Set the size of the GUI
      setSize(icon.getIconWidth() + 200, icon.getIconHeight() + 100);

      //Background box for the buttons
      Box = new JLabel();
      Box.setOpaque(true);
      Box.setBounds(1300,325,250,440);
      Box.setBackground(new Color(52, 113, 163));
      bPane.add(Box, new Integer(2));

      //Scoreboard Label
      mLabel = new JLabel("SCOREBOARD");
      mLabel.setFont(new Font(mLabel.getFont().getName(), mLabel.getFont().getStyle(), 15));
      mLabel.setBounds(icon.getIconWidth() + 40, 45, 150, 50);
      bPane.add(mLabel, new Integer(2));

      //this button makes it possible to end the game from the middle of the game.
      // No winner will be established but the game window will close after a short message
      endGame = new JButton("Quit Game");
      endGame.setForeground(Color.white);
      endGame.setBounds(1350, 700, 150, 40);
      endGame.setBackground(new Color(0,0,0));
      endGame.addMouseListener(new boardMouseListener());

      // Act button
      bAct = new JButton("ACT");
      bAct.setBackground(Color.white);
      bAct.setBounds(1350, 450, 150, 40);
      bAct.addMouseListener(new boardMouseListener());
      bAct.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              if(hasActed == true){
                  JOptionPane.showMessageDialog(bPane, "You cannot act as you have already performed an action during this turn.");
              }
              else if(the_system.getCurrent_user().getPosition().hasUserRole() == false){
                  JOptionPane.showMessageDialog(bPane, "You cannot act as you do not have a role.");
              }
              else if(hasActed == false){
                  hasActed = the_system.act();
                  updateScoreboard();
              }
              //set buttons
              bAct.setEnabled(false);
              bMove.setEnabled(false);
              bRehearse.setEnabled(false);
              bWork.setEnabled(false);
              bUpgrade.setEnabled(false);
              bEndTurn.setEnabled(true);
              endGame.setEnabled(true);
          }
      });

      //Rehearse  button
      bRehearse = new JButton("REHEARSE");
      bRehearse.setBackground(Color.white);
      bRehearse.setBounds(1350, 500, 150, 40);
      bRehearse.addMouseListener(new boardMouseListener());
      bRehearse.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              if(the_system.getCurrent_user().getPosition().getCurrentRoom().getRoomName().equals("trailer") || the_system.getCurrent_user().getPosition().getCurrentRoom().getRoomName().equals("office")){
                  JOptionPane.showMessageDialog(bPane, "You can't rehearse in this room, fool.");
              }
              else {
                  if (hasActed == true) {
                      JOptionPane.showMessageDialog(bPane, "You cannot rehearse as you have already performed an action during this turn.");
                  }
                  else if (hasActed == false) {
                      hasActed = the_system.rehearse();
                      updateScoreboard();
                      if(hasActed == true){
                          JOptionPane.showMessageDialog(bPane, "You rehearsed your butt off!");
                      }
                      else{
                          JOptionPane.showMessageDialog(bPane, "Silly. You can't rehearse as you do not have a role.");
                      }
                  }
              }

              bAct.setEnabled(false);
              bMove.setEnabled(false);
              bRehearse.setEnabled(false);
              bWork.setEnabled(false);
              bUpgrade.setEnabled(false);
              bEndTurn.setEnabled(true);
              endGame.setEnabled(true);
          }
      });

      //Move button
      bMove = new JButton("MOVE");
      bMove.setBackground(Color.white);
      bMove.setBounds(1350, 350, 150, 40);
      bMove.addMouseListener(new boardMouseListener());
      bMove.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {

              if(hasActed == true){
                  JOptionPane.showMessageDialog(bPane, "You cannot move as you have already performed an action during this turn.");
              }
              else{
                  String room = "";
                  JPanel movePanel = new JPanel();
                  movePanel.add(new JLabel("Please select a room to move too!"));
                  DefaultComboBoxModel adjRooms = new DefaultComboBoxModel();
                  //get adjacent room
                  for(String adjRoom : the_system.getAdjRooms()){
                      adjRooms.addElement(adjRoom);
                  }
                  moveChoices = new JComboBox(adjRooms);
                  movePanel.add(moveChoices);

                  int result = JOptionPane.showConfirmDialog(null, movePanel, "MoveRoom", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                  switch (result) {
                      case JOptionPane.OK_OPTION:
                          room = adjRooms.getSelectedItem().toString();
                          break;
                  }
                  boolean moved = the_system.move(room);
                  if(moved){
                      User current_user = the_system.current_user;
                      Room cur_room = current_user.getPosition().getCurrentRoom();
                      GUIUser user_gui = current_user.getUserGUI();
                      ArrayList<Integer> userBounds = user_gui.getBounds();
                      ArrayList<Integer> roomBounds = cur_room.getBounds();
                      userBounds.set(1, roomBounds.get(1) + 120);//moves player
                      userBounds.set(0, roomBounds.get(0) + (46 * (current_user.getUserNumber())));
                      current_user.getUserGUI().setBounds(userBounds);
                      updateScoreboard();
                      //if the room they move to is over subtract work or office or trailer
                      if(current_user.getPosition().getCurrentRoom().getRoomName() == "office" ||
                              current_user.getPosition().getCurrentRoom().getRoomName() == "trailer" ||
                              current_user.getPosition().getCurrentRoom().getCreditsRemaining() == 0) {
                          bAct.setEnabled(false);
                          bMove.setEnabled(false);
                          bRehearse.setEnabled(false);
                          bWork.setEnabled(false);
                          bUpgrade.setEnabled(false);
                          bEndTurn.setEnabled(true);
                          endGame.setEnabled(true);
                      }
                      else{
                          bAct.setEnabled(false);
                          bMove.setEnabled(false);
                          bRehearse.setEnabled(false);
                          bWork.setEnabled(true);
                          bUpgrade.setEnabled(false);
                          bEndTurn.setEnabled(true);
                          endGame.setEnabled(true);
                      }
                  }
                  hasActed = moved;
                  hasMoved = moved;
                  if(room == "" || room == "none"){
                      hasActed = false;
                      hasMoved = false;
                  }
              }

          }
      });

      //work button
      bWork = new JButton("WORK");
      bWork.setBackground(Color.white);
      bWork.setBounds(1350, 400, 150, 40);
      bWork.addMouseListener(new boardMouseListener());
      bWork.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              if(hasMoved == false && hasActed == true){
                  JOptionPane.showMessageDialog(bPane, "You cannot work a role as you have already performed an action during this turn.");
              }
              else{
                  String role = "";
                  JPanel workPanel = new JPanel();
                  workPanel.add(new JLabel("Please select a role to work"));
                  DefaultComboBoxModel possibleRoles = new DefaultComboBoxModel();
                  if(the_system.getPossibleRoles() != null){
                      for(String cur_role : the_system.getPossibleRoles()){
                          possibleRoles.addElement(cur_role);
                      }
                  }
                  else {
                      possibleRoles.addElement("None");
                  }
                  workChoices = new JComboBox(possibleRoles);
                  workPanel.add(workChoices);

                  int result = JOptionPane.showConfirmDialog(null, workPanel, "MoveRoom", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                  switch (result) {
                      case JOptionPane.OK_OPTION:
                          role = possibleRoles.getSelectedItem().toString();
                          break;
                  }
                  if(role == "" || role == "None"){
                      hasActed = false;
                  }
                  hasActed = the_system.work(role);
                  //logic implemented in work in GameSystem
                  //if has acted end buttons
                  //if hasn't acted && hasn't moved work/move is okay
                  //if hasn't acted && has moved only work
                  User current_user = the_system.current_user;
                  Role cur_role = current_user.getPosition().getUserRole();
                  Room cur_room = current_user.getPosition().getCurrentRoom();
                  ArrayList<Integer> roomBounds = cur_room.getBounds();
                  if(cur_role != null && cur_role.isRoleOnCard()) {
                      ArrayList<Integer> bounds = cur_role.getBounds();
                      bounds.set(0, bounds.get(0) + roomBounds.get(0));
                      bounds.set(1, bounds.get(1) + roomBounds.get(1));
                      current_user.getUserGUI().setBounds(bounds);
                  }
                  else if(cur_role != null && !cur_role.isRoleOnCard()){
                      ArrayList<Integer> bounds = cur_role.getBounds();
                      bounds.set(0, bounds.get(0));
                      bounds.set(1, bounds.get(1));
                      current_user.getUserGUI().setBounds(bounds);
                  }
              }
          }
      });

      //end turn button
      bEndTurn = new JButton("END TURN");
      bEndTurn.setBackground(Color.white);
      bEndTurn.setBounds(1350, 550, 150, 40);
      bEndTurn.addMouseListener(new boardMouseListener());
      bEndTurn.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              hasActed = false;
              hasMoved = false;
              the_system.endTurn();
              updateCurUser();
              JOptionPane.showMessageDialog(bPane, "Your turn is now over.");
          }
      });

      //upgrade button
      bUpgrade = new JButton("UPGRADE");
      bUpgrade.setBackground(Color.white);
      bUpgrade.setBounds(1350, 600, 150, 40);
      bUpgrade.addMouseListener(new boardMouseListener());
      bUpgrade.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              //check if the User has acted
              if(hasActed == true){
                  JOptionPane.showMessageDialog(bPane, "You cannot upgrade as you have already performed an action during this turn.");
              }
              else{
                  if(the_system.canUpgrade() == true) {
                      upgradeButton();
                  }
                  else{
                      //call error box
                      errorBox("Error: You are not permitted to upgrade at this time!", "upgrade");
                      hasActed = false;
                  }
              }
          }
      });

      // Place the action buttons in the top layer
      bPane.add(bAct, new Integer(3));
      bPane.add(bRehearse, new Integer(3));
      bPane.add(bMove, new Integer(3));
      bPane.add(bWork, new Integer(3));
      bPane.add(bEndTurn, new Integer(3));
      bPane.add(bUpgrade, new Integer(3));
      bPane.add(endGame, new Integer(3));
  }
  //set hasActed vairable
  public void setHasActed(boolean bool){
      hasActed = bool;
  }

  //set buttons for case if upgrade failed for some reason
  public void setfailedupgradebuttons(){
      bAct.setEnabled(false);
      bMove.setEnabled(true);
      bRehearse.setEnabled(false);
      bWork.setEnabled(false);
      bUpgrade.setEnabled(true);
      bEndTurn.setEnabled(true);
      endGame.setEnabled(true);
  }

   //set buttons so only end turn and end game are true
  public void setonlyEndbuttons(){
          bAct.setEnabled(false);
          bMove.setEnabled(false);
          bRehearse.setEnabled(false);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
  }
    //for new turn and new day
  public void setButtons(User newuser){
      //if the user has a role they can only act, rehearse, end turn or end game
      if (newuser.getPosition().hasUserRole() == true){
          bAct.setEnabled(true);
          bMove.setEnabled(false);
          bRehearse.setEnabled(true);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
      //if a room has no credits they can move, end turn or end game
      else if(newuser.getPosition().getCurrentRoom().getCreditsRemaining() == 0){
          bAct.setEnabled(false);
          bMove.setEnabled(true);
          bRehearse.setEnabled(false);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
      //in trailer
      else if(newuser.getPosition().getCurrentRoom().getRoomName().equalsIgnoreCase("trailer")){
          bAct.setEnabled(false);
          bMove.setEnabled(true);
          bRehearse.setEnabled(false);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
      //in the office new turn
      else if(newuser.getPosition().getCurrentRoom().getRoomName() == "office"){
          bAct.setEnabled(false);
          bMove.setEnabled(true);
          bRehearse.setEnabled(false);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(true);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
      //no role, not trailer, not office
      else if(newuser.getPosition().getUserRole() == null && (newuser.getPosition().getCurrentRoom().getRoomName()!= "office" ||
              newuser.getPosition().getCurrentRoom().getRoomName() != "trailer")){
          bAct.setEnabled(false);
          bMove.setEnabled(true);
          bRehearse.setEnabled(false);
          bWork.setEnabled(true);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
      //on a role
      else if(newuser.getPosition().getUserRole() != null){
          bAct.setEnabled(true);
          bMove.setEnabled(false);
          bRehearse.setEnabled(true);
          bWork.setEnabled(false);
          bUpgrade.setEnabled(false);
          bEndTurn.setEnabled(true);
          endGame.setEnabled(true);
      }
  }

  //Description: This method is used to determine the number of players in the game
  //It is called from GameSystem when it is time to create players
  public int numberOfUsers(){
      int numUsers = 0;
      JPanel numUsersPanel = new JPanel();
      numUsersPanel.add(new JLabel("<html> Play Deadwood! <br> How many Users are playing? </html>"));//using HTML for style
      DefaultComboBoxModel possibleNumUsers = new DefaultComboBoxModel();
      possibleNumUsers.addElement(2);
      possibleNumUsers.addElement(3);
      numOfUsers = new JComboBox(possibleNumUsers);
      numUsersPanel.add(numOfUsers);
      int result = JOptionPane.showConfirmDialog(null, numUsersPanel, "Start Game: Number of Players", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
      switch (result) {
          case JOptionPane.OK_OPTION:
               numUsers = Integer.parseInt(possibleNumUsers.getSelectedItem().toString());
              break;
      }
      return numUsers;
  }

    //Description: This method resets the players position on the board at the beginning of the day
  public void startDay(){
      ArrayList<User> users = the_system.getUsers();
      int xBound = 1025;
      int yBound = 320;

      for(User cur_user : users){
          ArrayList<Integer> bounds = new ArrayList<>();
          bounds.add(xBound);
          bounds.add(yBound);
          cur_user.getUserGUI().setBounds(bounds);
          xBound += 46;
      }
  }

  //Description: This method updates the top of the Scoreboard which indicates who's turn it is
  // It updates the name of the user and the image associated with that User
  public void updateCurUser(){
      cur_user_label.setText("Current user: " + the_system.getCurrent_user().getName());
      ImageIcon cur_user_Icon = new ImageIcon ("dice/"+ the_system.current_user.getDisplayName() + Integer.toString(the_system.getCurrent_user().getRank()) + ".png");
      cur_user_img_label.setIcon(cur_user_Icon);
  }

  //Description: This method has to populate the cards but shows back of the card
  public void createCard(Scene scene, Room room){
      cardBackLabel = new JLabel();
      ImageIcon backIcon = new ImageIcon("backCard.png");
      cardBackLabel.setIcon(backIcon);
      ArrayList<Integer> bounds = room.getBounds();
      cardBackLabel.setBounds(bounds.get(0), bounds.get(1), backIcon.getIconWidth(), backIcon.getIconHeight());
      cardBackLabel.setOpaque(false);

      cardlabel = new JLabel();
      ImageIcon cIcon = new ImageIcon("cards/" + scene.getImage());
      cardlabel.setIcon(cIcon);
      cardlabel.setBounds(bounds.get(0), bounds.get(1), cIcon.getIconWidth(), cIcon.getIconHeight());
      cardlabel.setOpaque(false);
      cardlabel.setVisible(false);

      if(room.hasScene){
          room.setSceneGui(cardlabel);
      }

      ArrayList<JLabel> shot_counters = new ArrayList<>();
      for(ArrayList<Integer> shot_bounds : room.getTakes()){
          JLabel shotlabel = new JLabel();
          ImageIcon shotIcon = new ImageIcon("shot.png");
          shotlabel.setIcon(shotIcon);
          shotlabel.setBounds(shot_bounds.get(1), shot_bounds.get(2), shotIcon.getIconWidth(), shotIcon.getIconHeight());
          shot_counters.add(shotlabel);
          bPane.add(shotlabel, new Integer(2));
      }
      //set shot counters
      room.setShot_counters(shot_counters);
      bPane.add(cardBackLabel, new Integer(1));
      bPane.add(cardlabel, new Integer(2));
  }

  //Description: This method initiates the Scoreboard
  public void setUpScoreboard(Scoreboard scoreboard){
      //Label for the current User at the top of the scoreboard area
      int heightOffset = 0;
      cur_user_label = new JLabel("Current user");
      cur_user_label.setBounds( boardlabel.getIcon().getIconWidth() + 40, heightOffset+25,300, 20);
      cur_user_label.setFont(new Font(cur_user_label.getFont().getName(), cur_user_label.getFont().getStyle(), 20));
      cur_user_img_label = new JLabel();

      //place a blank dice shaped image where the current User Dice will be displayed for the current User
      ImageIcon cur_user_Icon = new ImageIcon("dice/new.png");
      cur_user_img_label.setIcon(cur_user_Icon);
      cur_user_img_label.setBounds( boardlabel.getIcon().getIconWidth() + 275,heightOffset+15, cur_user_Icon.getIconWidth(), cur_user_Icon.getIconWidth());
      bPane.add(cur_user_img_label, new Integer(2));
      bPane.add(cur_user_label, new Integer(2));
      heightOffset += 50;

      //for each player show name and stats
      for(User cur_user : scoreboard.getListOfUsers()){
          heightOffset += 40;
          int rehearsalPts = cur_user.getPosition().getRoleRank();
          mLabel = new JLabel(cur_user.getName() + " Rank: " + scoreboard.getRank(cur_user) + " Money: " + scoreboard.getMoney(cur_user)
          + " Credits: " + scoreboard.getCredit(cur_user) + " Rehearsal Rank: " + rehearsalPts);
          mLabel.setBounds( boardlabel.getIcon().getIconWidth() + 40, heightOffset, 400, 20);
          bPane.add(mLabel, new Integer(2));
          cur_user.setScoreboardLabel(mLabel);
      }
  }
  //Description: Updates information after a player has acquired money, credits or upgraded.
  public void updateScoreboard(){
      User current_user = the_system.getCurrent_user();
      JLabel scoreboardLabel = current_user.getScoreboardLabel();
      Scoreboard scoreboard = the_system.getScoreboard();
      scoreboardLabel.setText(current_user.getName() + " \nRank: " + scoreboard.getRank(current_user) + " \nMoney: "
              + scoreboard.getMoney(current_user) + " \nCredits: " + scoreboard.getCredit(current_user) +
              " \nRehearsal Rank: " + current_user.getPosition().getRoleRank());
  }
  //Description: This is an error message used by the upgrade button if the player can't upgrade
  public void errorBox(String infoMessage, String titleBar) {
      JOptionPane.showMessageDialog(null, infoMessage, "ErrorBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
  }

  //Description: This method gets the type of currency the player wants to use and the level
  // they want to upgrade to. It sends this information to a method in Scoreboard where logic
  // determines if that is possible or not. The scoreboard is then updated.
  // This method is called by the upgrade button after it is confirmed by the GameSystem
  // that the player has the credentials to upgrade
  public void upgradeButton(){
      String currency = "";
      int levelchosen = 0;
      JPanel currencyPanel = new JPanel();
      currencyPanel.add(new JLabel("What type of currency would you like to use?"));

      DefaultComboBoxModel currencyOpt = new DefaultComboBoxModel();
      currencyOpt.addElement("Money");
      currencyOpt.addElement("Credits");
      currencyBox = new JComboBox(currencyOpt);
      currencyPanel.add(currencyBox);

      int result = JOptionPane.showConfirmDialog(null, currencyPanel, "TypeOfCurrency", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
      switch (result) {
          case JOptionPane.OK_OPTION:
              currency = currencyOpt.getSelectedItem().toString();
              break;
      }
      if (currency == "Money"){
          currency = "$";
      }
      else{
          currency = "cr";
      }
      JPanel levelPanel = new JPanel();
      levelPanel.add(new JLabel("Rank desired:"));
      DefaultComboBoxModel levelOpt = new DefaultComboBoxModel();
      levelOpt.addElement(2);
      levelOpt.addElement(3);
      levelOpt.addElement(4);
      levelOpt.addElement(5);
      levelOpt.addElement(6);
      levelBox = new JComboBox(levelOpt);
      levelPanel.add(levelBox);

      int result2 = JOptionPane.showConfirmDialog(null, levelPanel, "DesiredLevel", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
      switch (result2) {
          case JOptionPane.OK_OPTION:
              levelchosen = Integer.parseInt(levelOpt.getSelectedItem().toString());
              break;
      }
      the_system.upgrade(currency, levelchosen);
      updateScoreboard();
  }

  // This class implements Mouse Events
  class boardMouseListener implements MouseListener{
      // If a button is clicked...
      public void mouseClicked(MouseEvent e) {
          //*** These might seem redundant but the game won't work without them
          //Act button
         if (e.getSource()== bAct){
         }
         //rehearse button
         else if (e.getSource()== bRehearse){
         }
         //move button
         else if (e.getSource()== bMove){
         }
         //work button
         else if (e.getSource()== bWork){
         }
         //end turn button
         else if (e.getSource()== bEndTurn){
             //action listener
         }
         //end game button
         else if(e.getSource() == endGame){
             JOptionPane.showMessageDialog(bPane, "Lame, quitter! Bye!");
             System.exit(1);
          }
         //upgrade button
         else if (e.getSource()== bUpgrade){
         }
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
   }
}

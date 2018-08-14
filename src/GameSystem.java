/*
Class: GameSystem.java
Description: This class has the most logic behind the scenes for the game.

 */

//imports
import java.io.File;
import java.util.*;
import java.lang.Integer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.swing.*;
import javax.swing.ImageIcon;

public class GameSystem {
    //attributes
    BoardLayersListener board;
    ArrayList<Room> listOfRooms = new ArrayList<Room>();
    ArrayList<Scene> listOfScenes = new ArrayList<Scene>();
    ArrayList<User> listOfUsers = new ArrayList<User>();
    Scoreboard scoreboard = new Scoreboard(listOfUsers);
    User current_user;
    int current_user_index = 0;
    int daysPlayed = 0;
    int roomsLeft;
    int scene_counter = 0;
    /*
        Create all rooms and scenes from parsed file
        Create all users from reading their input and then creating player objects
        Adds all the users into the scoreboard.
        Calls dawn which starts the game
    */
    public void newGame(BoardLayersListener board){

        this.board = board;
        createRooms(); // parses xml file and creates all room objects and adds to listOfRooms
        createScenes(); // parses xml file and creates all scene objects and adds to listOfScenes
        createPlayers(); // Creates User objects and adds to listOfUsers
        scoreboard.setListOfUsers(listOfUsers); // Adds the listOfUsers to the scoreboard object
        board.setUpScoreboard(scoreboard);

        dawn(); // Starts the first day
    }
    /*
    Implements the acting functionality of the game.
    If user is in a valid spot and has a role
    Will roll a dice and according to dice roll and budget either success or failure
    resulting in payment or nothing.
    Potentially triggers end of scene
     */
    public boolean act(){
        Position current_user_position = current_user.getPosition();
        Room currentRoom = current_user_position.getCurrentRoom();
        Scene currentScene = current_user_position.getCurrentScene();
        Role currentRole = current_user_position.getUserRole();
        if(currentRole == null){
            JOptionPane.showMessageDialog(board,"Cannot act when you don't have a role!");
            return false;
        }
        //dice rolling to decide if the user rolls correctly or not.
        int diceRoll = rollDice();
        int roleRank = current_user_position.getRoleRank(); // the role rank is rank user has by rehearsing.
        int budget = currentScene.getBudget();
        int rollOutcome = (diceRoll + roleRank) - budget;
        if(rollOutcome >= 0){ // check if roll is high enough.
            if(currentRole.isRoleOnCard()){ // if roll is budget or higher and player is on a scene
                JOptionPane.showMessageDialog(board, "Success! You are on the scene so you get two credits.");
                scoreboard.addCredit(current_user, 2);
            }
            else{ // if roll is budget or higher and play is off scene
                JOptionPane.showMessageDialog(board, "Success! You are off the scene so you get $1 and 1 credit.");
                scoreboard.addMoney(current_user, 1);
                scoreboard.addCredit(current_user, 1);
            }
            if(currentRoom.removeCredit(1) < 1){ // this removes counters from a scene. If none left ends scene
                JOptionPane.showMessageDialog(board, "Room has no more credits remaining! The scene is now done.");
                endScene(currentRoom, currentScene);
            }
            currentRoom.take_off_counter();
        }
        else{ // Case if roll plus rank is less than budget
            JOptionPane.showMessageDialog(board, "Sorry! You rolled a " + diceRoll + " and that wasn't enough to get to the budget of " + budget +
            " you were " + rollOutcome + " short.");
            if(!currentRole.isRoleOnCard()){ // is user is not on a scene
                JOptionPane.showMessageDialog(board, "You earned $1");
                scoreboard.addMoney(current_user, 1);
            }else{ // if a user is on a scene
                JOptionPane.showMessageDialog(board, "No luck! You're on the card so you earn nothing!");
            }
        }
        return true;
    }
    /*
    When a scene ends. Complete a bonus roll for any users on scene
    Remove all users from roles
    Subtract one from the rooms left
    Users won't be able to take any roles in this scene again because there's a check if
    room has no credits left no one can take a role
     */
    private void endScene(Room current_room, Scene current_scene){
        // For each
        for(Role cur_role : current_room.getRoles()){
            User userWithRole = getUserWithRole(cur_role);
            if(userWithRole != null){
                userWithRole.getPosition().resetRoleRank();
                Room cur_room = userWithRole.getPosition().getCurrentRoom();
                GUIUser user_gui = userWithRole.getUserGUI();
                ArrayList<Integer> userBounds = user_gui.getBounds();
                ArrayList<Integer> roomBounds = cur_room.getBounds();

                userBounds.set(1, roomBounds.get(1) + 120); // not room move player
                userBounds.set(0, roomBounds.get(0) + (46 * (userWithRole.getUserNumber())));
                userWithRole.getUserGUI().setBounds(userBounds);
            }
            if(userWithRole != null) {
                JOptionPane.showMessageDialog(board, "Sorry! You are off the scene, no bonus roll for you.");
                userWithRole.getPosition().setUserRole(null);
            }
        }
        int num_roles_scene = current_scene.getRoles().size();
        for(Role cur_role : current_scene.getRoles()){
            ArrayList<Integer> bonusRoll = bonusRoll(num_roles_scene);
            User userWithRole = getUserWithRole(cur_role);

            if(userWithRole != null){
                userWithRole.getPosition().resetRoleRank();
                Room cur_room = userWithRole.getPosition().getCurrentRoom();
                GUIUser user_gui = userWithRole.getUserGUI();
                ArrayList<Integer> userBounds = user_gui.getBounds();
                ArrayList<Integer> roomBounds = cur_room.getBounds();
                userBounds.set(1, roomBounds.get(1) + 120); // not room move player
                userBounds.set(0, roomBounds.get(0) + (46 * (userWithRole.getUserNumber())));

                userWithRole.getUserGUI().setBounds(userBounds);
            }
            int roleHierarchy = cur_role.getSceneHierarchyRank();
            int moneyWon = bonusRoll.get(roleHierarchy);

            if(userWithRole != null) {
                JOptionPane.showMessageDialog(board, "User " + userWithRole.getName() + " wins " + moneyWon + " dollars from bonus roll");
                scoreboard.addMoney(userWithRole, moneyWon);
                userWithRole.getPosition().setUserRole(null);
            }
        }
        board.setonlyEndbuttons();
            roomsLeft--;
    }

    /*
    Used to get a user that is acting in a current role
     */
    public User getUserWithRole(Role role){
        for(User cur_user : listOfUsers){
            Position current_position = cur_user.getPosition();
            Role current_role = current_position.getUserRole();
            if(current_role != null) {
                if (current_role.getRoleName().equals(role.getRoleName())) {
                    return cur_user;
                }
            }
        }
        return null;
    }

    public User getCurrent_user(){
        return current_user;
    }

    public Scoreboard getScoreboard(){
        return scoreboard;
    }
    /*
    Upgrade command. Checks if in valid location
    If so, calls upgrade on the scoreboard object
     */
    public boolean canUpgrade(){
        Position current_user_position = current_user.getPosition();
        Room currentRoom = current_user_position.getCurrentRoom();
        if(currentRoom.getRoomName().toLowerCase().equals("office")){
            return true;
        }else{
            return false;
        }
    }

    /*
    passes information to scoreboard upgrade method if user can upgrade
     */
    public void upgrade(String type, int level){
        Position current_user_position = current_user.getPosition();
        Room currentRoom = current_user_position.getCurrentRoom();
        if(currentRoom.getRoomName().toLowerCase().equals("office")){
            if(current_user.getRank() <= level) {
                scoreboard.upgrade(current_user, type, level, board);
            }
            else{
                JOptionPane.showMessageDialog(board,"Why would you want to pay to be the same level?!");
                board.setHasActed(false);
            }
        }
        else{
            JOptionPane.showMessageDialog(board,"You must be in the office to upgrade!");
        }
    }

    /*
    Adds 1 to your current score as an actor on a specific role
    Gets reset when you end a role
     */
    public boolean rehearse(){
        boolean outcome = current_user.getPosition().rehearse();

        return outcome;
    }
    /*
    Takes in the name of a room in a string and returns the room object
     */
    public Room getRoom(String room){
        for(Room curRoom : listOfRooms){
            if(curRoom.getRoomName().toLowerCase().equals(room.toLowerCase())){
                return curRoom;
            }
        }
        return null;
    }
    /*
    Takes in the name of a role the user wants
    Does all the neccesary checks to if a user is
    in the proper location, if they already have a role if there isn't in one
    the user wants to take.
    If everything checks out, then adds the user to that role
     */
    public boolean work(String role){
        Position current_user_position = current_user.getPosition();
        Room currentRoom = current_user_position.getCurrentRoom();
        if(!currentRoom.hasScene){ // if room doesn't have scene there are no roles to work
            JOptionPane.showMessageDialog(board, "This room doesn't have a scene or roles so there are no roles to work in!");
            board.setonlyEndbuttons();
            return false;
        }
        Scene currentScene = current_user_position.getCurrentScene();
        Role current_role = current_user_position.getUserRole();
        Role roleInRoom = currentRoom.hasRole(role.toLowerCase());
        Role roleInScene = currentScene.hasRole(role.toLowerCase());
        if(current_role != null){ // Cannot work if you already have a role
            JOptionPane.showMessageDialog(board, "You cannot work another role if you already have one!");
            board.setonlyEndbuttons();
            return false;
        }
        if(currentRoom.getCreditsRemaining() < 1){ // If room doesn't have any credits left you cannot take a role to act in it
            JOptionPane.showMessageDialog(board,"This room has no more remaining credits! You cannot take this role.");
            board.setonlyEndbuttons();
            return false;
        }
        if(roleInRoom != null){ // If role exists validate user can take role and add role to position object
            int userRank = current_user.getRank();
            int roleRank = roleInRoom.getRankNeeded();
            if(roleRank > userRank){ // Checks if user has high enough rank to take role
                JOptionPane.showMessageDialog(board, "Cannot take a role you don't have the rank for! Your rank is " + userRank + ", and the rank needed is " + roleRank);

                return false;
            }
            //role rank is equal or lower thank user rank
            else{
                if(roleInRoom.getUserOnRole()){ // Checks if someone already has this role
                    JOptionPane.showMessageDialog(board, "Someone is already acting in that role!");
                    return false;
                }
                else { // If everything checks out adds the role to the current users position object
                    current_user_position.setUserRole(roleInRoom);
                    roleInRoom.setUserOnRole(true); // sets the role as having someone working it
                    board.setonlyEndbuttons();
                    return true;
                }
            }
        }
        else if(roleInScene != null){ // If role exists in scene
            int userRank = current_user.getRank();
            int roleRank = roleInScene.getRankNeeded();
            if(roleRank > userRank){ // Checks if user has high enough rank to take role
                JOptionPane.showMessageDialog(board, "Cannot take a role you don't have the rank for! Your rank is " + userRank + " and the rank needed is " + roleRank);
                return false;
            }
            else {
                if(roleInScene.getUserOnRole()){ // Checks if someone already has this role
                    JOptionPane.showMessageDialog(board, "Someone is already acting in that role!");

                    return false;
                }
                else { // If everything checks out adds the role to the current users position object
                    current_user_position.setUserRole(roleInScene);
                    roleInScene.setUserOnRole(true); // sets the role as having someone working it
                    board.setonlyEndbuttons();
                    return true;
                }
            }
        }
        else{
            return false;
        }
    }
    /*
    Takes in the name of the room
    If that rooms is adjacent and exists moves the current user to that room
     */
    public boolean move(String room){
        Position current_user_position = current_user.getPosition();
        Room currentRoom = current_user_position.getCurrentRoom();
        Room destinationRoom = getRoom(room);
        if(destinationRoom == null){
            return false;
        }
        if(currentRoom.isAdjacent(room)){
            if(current_user_position.getUserRole() == null) {
                current_user_position.setCurrentRoom(destinationRoom);
                current_user_position.setCurrentScene(destinationRoom.getScene());
                currentRoom.removeUser(current_user);
                destinationRoom.addUser(current_user);
            }
            else{
                JOptionPane.showMessageDialog(board, "You cannot move if you are acting in a role!");
                return false;
            }
        }
        else{
            JOptionPane.showMessageDialog(board,"That room is not adjacent, you cannot go there!");
            return false;
        }
        return true;
    }
    /*
    This method parses an xml file named cards.xml
    It creates all the scene cards and creates the roles on them in the process of making the scenes
    repetitively traversing a tree
     */
    public void createScenes(){
        //variables
        int budget;
        String partName = "";
        int partLevel = 0;
        String image = "";
        try {
            //read file
            File inputFile = new File("cards.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            //create list of type element card
            NodeList cardList = doc.getElementsByTagName("card");
            //for each card do this
            for (int temp = 0; temp < cardList.getLength(); temp++) {
                //create node for card
                Node cardNode = cardList.item(temp);

                if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                    //type cast to element
                    Element cardElement = (Element) cardNode;
                    //get attributes for card
                    image = cardElement.getAttribute("img");
                    budget = Integer.parseInt(cardElement.getAttribute("budget"));
                    //new node for parts on scene
                    NodeList partList = cardElement.getElementsByTagName("part");
                    //create ArrayList to add Roles to
                    ArrayList<Role> roles = new ArrayList<Role>();
                    int sceneHierarchyRank = 0;
                    //for each role
                    for (int temp2 = 0; temp2 < partList.getLength(); temp2++) {
                        //type cast to Node
                        Node partNode = partList.item(temp2);
                        //get all attributes from element
                        if(partNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element partElement = (Element) partNode;
                            partName = partElement.getAttribute("name");
                            partLevel = Integer.parseInt(partElement.getAttribute("level"));
                            Node partAreaNode = partElement.getElementsByTagName("area").item(0);
                            Element partAreaElement = (Element) partAreaNode;
                            ArrayList<Integer> bounds = new ArrayList<Integer>();
                            bounds.add(Integer.parseInt(partAreaElement.getAttribute("x")));
                            bounds.add(Integer.parseInt(partAreaElement.getAttribute("y")));
                            bounds.add(Integer.parseInt(partAreaElement.getAttribute("h")));
                            bounds.add(Integer.parseInt(partAreaElement.getAttribute("w")));
                            roles.add(new Role(partName,  partLevel, true, sceneHierarchyRank, bounds));
                            sceneHierarchyRank++;
                        }
                    }
                    //create card
                    listOfScenes.add(new Scene( budget, roles, image));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    Parse an xml file called board.xml and creates rooms, the office, trailer and
    casting office, and creates roles for the rooms
     */
    public void createRooms(){
        try {
            //read file
            File inputFile = new File("board.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList setList = doc.getElementsByTagName("set");
            //for each set
            for (int temp = 0; temp < setList.getLength(); temp++) {
                //Create ArrayList of adj rooms
                ArrayList<String> adjRooms = new ArrayList<String>();
                Node setNode = setList.item(temp);
                Element setElement = (Element) setNode;
                String setName = setElement.getAttribute("name");
                //get neighbors
                NodeList neighborList = setElement.getElementsByTagName("neighbor");
                //get name of each neighbor
                for (int temp2 = 0; temp2 < neighborList.getLength(); temp2++) {
                    Node neighborNode = neighborList.item(temp2);
                    if(neighborNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element neighborElement = (Element) neighborNode;
                        String neighborName = neighborElement.getAttribute("name");
                        //add name to the ArrayList
                        adjRooms.add(neighborName);
                    }
                }
                //number of takes
                int numOfTakes = setElement.getElementsByTagName("take").getLength();
                ArrayList<Integer> bounds = new ArrayList<Integer>();

                NodeList areaNodes = setElement.getElementsByTagName("area");
                Node areaNode = areaNodes.item(0);
                Element areaElement = (Element) areaNode;
                bounds.add(Integer.parseInt(areaElement.getAttribute("x")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("y")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("h")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("w")));

                ArrayList<ArrayList<Integer>> takes = new ArrayList<>();
                NodeList takeNodes = setElement.getElementsByTagName("take");

                for(int a = 0; a < takeNodes.getLength(); a++){
                    Node takeNode = takeNodes.item(a);
                    if(takeNode.getNodeType() == Node.ELEMENT_NODE) {
                        ArrayList<Integer> takeBounds = new ArrayList<Integer>();
                        Element takeElement = (Element) takeNode;
                        takeBounds.add(Integer.parseInt(takeElement.getAttribute("number")));

                        NodeList takeAreaNodes = takeElement.getElementsByTagName("area");
                        Node takeAreaNode = takeAreaNodes.item(0);
                        Element takeAreaElement = (Element) takeAreaNode;
                        takeBounds.add(Integer.parseInt(takeAreaElement.getAttribute("x")));
                        takeBounds.add(Integer.parseInt(takeAreaElement.getAttribute("y")));
                        takeBounds.add(Integer.parseInt(takeAreaElement.getAttribute("h")));
                        takeBounds.add(Integer.parseInt(takeAreaElement.getAttribute("w")));
                        takes.add(takeBounds);
                    }
                }
                NodeList partList = setElement.getElementsByTagName("part");
                //Create ArrayList of Roles in set
                ArrayList<Role> roles = new ArrayList<Role>();
                int rankRoleHierarchy = 0;
                //for each Role
                for (int temp3 = 0; temp3 < partList.getLength(); temp3++) {
                    Node partNode = partList.item(temp3);
                    if(partNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element partElement = (Element) partNode;
                        String partName = partElement.getAttribute("name");
                        int partLevel = Integer.parseInt(partElement.getAttribute("level"));

                        NodeList partAreaNodes = partElement.getElementsByTagName("area");
                        Node partAreaNode = partAreaNodes.item(0);
                        Element partAreaElement = (Element) partAreaNode;
                        ArrayList<Integer> partBounds = new ArrayList<Integer>();
                        partBounds.add(Integer.parseInt(partAreaElement.getAttribute("x")));
                        partBounds.add(Integer.parseInt(partAreaElement.getAttribute("y")));
                        partBounds.add(Integer.parseInt(partAreaElement.getAttribute("h")));
                        partBounds.add(Integer.parseInt(partAreaElement.getAttribute("w")));
                        //create role
                        roles.add(new Role(partName,  partLevel, false, rankRoleHierarchy, partBounds));
                        rankRoleHierarchy++;
                    }
                }
                //create room and add to list of rooms
                listOfRooms.add(new Room(setName.toLowerCase(), adjRooms, roles, numOfTakes, true, bounds, takes));
            }
            //--trailer--//
            //get list of neighbors
            NodeList trailerList = doc.getElementsByTagName("trailer");
            for (int temp4 = 0; temp4 < trailerList.getLength(); temp4++) {
                Node trailerNode = trailerList.item(temp4);
                Element trailerElement = (Element) trailerNode;

                NodeList areaList = trailerElement.getElementsByTagName("area");
                Node areaNode = areaList.item(0);
                Element areaElement = (Element) areaNode;
                ArrayList<Integer> bounds = new ArrayList<Integer>();
                bounds.add(Integer.parseInt(areaElement.getAttribute("x")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("y")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("h")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("w")));

                NodeList neighborList = trailerElement.getElementsByTagName("neighbor");
                ArrayList<String> adjRooms = new ArrayList<String>();
                for (int temp5 = 0; temp5 < neighborList.getLength(); temp5++) {
                    Node neighborNode = neighborList.item(temp5);
                    if(neighborNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element neighborElement = (Element) neighborNode;
                        String neighborName = neighborElement.getAttribute("name");
                        adjRooms.add(neighborName);
                    }
                }
                listOfRooms.add(new Room("trailer", adjRooms, null, 1, false, bounds, null));
            }
            //--office--//
            //get neighbors
            NodeList officeList = doc.getElementsByTagName("office");
            for (int temp6 = 0; temp6 < officeList.getLength(); temp6++) {
                Node officeNode = officeList.item(temp6);
                Element officeElement = (Element) officeNode;
                NodeList neighborList = officeElement.getElementsByTagName("neighbor");

                NodeList areaList = officeElement.getElementsByTagName("area");
                Node areaNode = areaList.item(0);
                Element areaElement = (Element) areaNode;

                ArrayList<Integer> bounds = new ArrayList<Integer>();
                bounds.add(Integer.parseInt(areaElement.getAttribute("x")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("y")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("h")));
                bounds.add(Integer.parseInt(areaElement.getAttribute("w")));

                ArrayList<String> adjRooms = new ArrayList<String>();
                for (int temp7 = 0; temp7 < neighborList.getLength(); temp7++) {
                    Node neighborNode = neighborList.item(temp7);
                    if(neighborNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element neighborElement = (Element) neighborNode;
                        String neighborName = neighborElement.getAttribute("name");
                        adjRooms.add(neighborName);
                    }
                }

                //create room and add room to list of rooms
                listOfRooms.add(new Room("office", adjRooms, null, 1, false, bounds, null));
                //--upgrades--//
                NodeList upgradeList = officeElement.getElementsByTagName("upgrade");
                //create upgrade ArrayLists
                ArrayList<Integer> upgrade_cost_money = new ArrayList<Integer>();
                ArrayList<Integer> upgrade_cost_credits = new ArrayList<Integer>();

                for (int temp8 = 0; temp8 < upgradeList.getLength(); temp8++) {
                    Node upgradeNode = upgradeList.item(temp8);
                    if(upgradeNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element upgradeElement = (Element) upgradeNode;
                        String currency = upgradeElement.getAttribute("currency");
                        int amt = Integer.parseInt(upgradeElement.getAttribute("amt"));
                        if(currency.equals("dollar")){
                            upgrade_cost_money.add(amt);
                        }else{
                            upgrade_cost_credits.add(amt);
                        }
                    }
                }
                //create upgrade part of scoreboard
                scoreboard.setUpgrade_cost_money(upgrade_cost_money);
                scoreboard.setUpgrade_cost_credits(upgrade_cost_credits);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> getUsers(){
        return listOfUsers;
    }
    /*
    At the start prompts for # of users and their names and creates user object out of them
     */
    public void createPlayers(){
        ArrayList<GUIUser> guiUserList = new ArrayList<GUIUser>();
        int num_users = board.numberOfUsers();
        String userName;
        //gui
        String[] userImgs = {"b1.png", "r1.png", "c1.png", "g1.png", "o1.png", "p1.png", "v1.png", "y1.png"};
        String[] userNames = {"Blue", "Red", "Cyan", "Green", "Orange", "Pink", "Violet", "Yellow"};
        String[] dNames = {"b", "r", "c", "g", "o", "p", "v", "y"};
        int xBound = 1025;
        int yBound = 320;

        for(int i = 0; i < num_users; i++){
            userName = userNames[i];
            Room startingRoom = getRoom("trailer");
            JLabel userLabel = new JLabel();
            ImageIcon pIcon = new ImageIcon("dice/" + userImgs[i]);
            userLabel.setIcon(pIcon);
            userLabel.setBounds(xBound,yBound,pIcon.getIconWidth(),pIcon.getIconHeight());
            GUIUser newGuiUser = new GUIUser(userLabel, pIcon, xBound, yBound);
            if (i < 2) {
                xBound += 46;
                yBound += 0;
            }
            else if (i == 2) {
                xBound += 0;
                yBound += 46;
            }
            guiUserList.add(newGuiUser);
            board.bPane.add(newGuiUser.getJlabel(), new Integer(3));
            User newUser = new User(userName, startingRoom, newGuiUser, dNames[i], i);
            listOfUsers.add(newUser);
        }
    }
    //prints what the winning score is, at the end of the game
    public void printScore(){
        int score =  scoreboard.getWinnerScore();
        StringBuilder winnerPrint = new StringBuilder("The winning Score is! ");
        winnerPrint.append(score);
        JOptionPane.showMessageDialog(board, winnerPrint);
    }

    /*
    Returns a random number between 1-6
     */
    public static int rollDice(){
        Random random = new Random();
        return random.nextInt(6) + 1;
    }
    /*
    Does everything that needs to be done at the start of a day
    Moves all the players to the trailer
    Adds scenes to all the rooms
    Sets the current user and number of rooms left and starts the first users turn
     */
    public void dawn(){
        current_user = listOfUsers.get(0);
        board.setButtons(current_user);
        Room trailer = getTrailer();
        for(User curUser : listOfUsers){
            Position current_usr_position = curUser.getPosition();
            current_usr_position.setCurrentRoom(trailer);
            current_usr_position.setCurrentScene(null);
        }
        int num_scenes = listOfScenes.size();
        for(Room cur_room : listOfRooms){
            if(cur_room.hasScene) {
                cur_room.setScene(listOfScenes.get(scene_counter % num_scenes));
                if(cur_room.getSceneGui() != null) {
                    cur_room.getSceneGui().setVisible(false);
                }
                board.createCard(listOfScenes.get(scene_counter % num_scenes), cur_room);
                cur_room.resetCredits();
                scene_counter++;
            }
        }
        board.startDay();
        board.updateCurUser();
        roomsLeft = listOfRooms.size() - 2;
    }
    /*
    Does everything that needs to be done at the end of the day
    Checks if it's the end of day, if so calls end of game
    Removes all users from roles and calls
    dawn for start of new day if not end of game
     */
    public void dusk(){
        daysPlayed++;
        //resets all the rehearsal points of all the users to 0
        for(int i = 0; i < listOfUsers.size(); i++){
            listOfUsers.get(i).getPosition().resetRoleRank();
            board.updateScoreboard();
        }
        JOptionPane.showMessageDialog(board, "End of day " + daysPlayed);

        if(daysPlayed > 2){
            endOfGame();
        }
        for(User curUser : listOfUsers){
            Position current_usr_position = curUser.getPosition();
            Room current_room = current_usr_position.getCurrentRoom();
            Scene current_scene = current_usr_position.getCurrentScene();
            if(current_room != null){
                current_room.clearRoles();
            }
            if(current_scene != null){
                current_scene.clearRoles();
                current_usr_position.setCurrentScene(null);
            }
            current_usr_position.setUserRole(null);
        }
        dawn();
    }
    /*
    Returns an arraylist of rolled dice to represent the bonus roll
     */
    public static ArrayList<Integer> bonusRoll(int numTimes){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < numTimes; i++){
            int num_rolled = rollDice();
            result.add(num_rolled);
        }
        Collections.sort(result);
        return result;
    }

    /*
    Prints out the winner after it's been calculated by the scoreboard and then exits
     */
    public void endOfGame(){
        ArrayList<User> winners = scoreboard.calculate_winner();
        printScore();
        StringBuilder winnerPrint = new StringBuilder("The winner of the game is! ");

        for(int i = 0; i < winners.size(); i++){
            winnerPrint.append(winners.get(i).getName() + " ");
        }
        String winnerString = winnerPrint.toString();
        JOptionPane.showMessageDialog(board, winnerString);
        System.exit(0);

     }
    /*
    Sets the current user to be the next user. Uses remainder from the users size
    If the number of rooms left is lower than 1 calls dusk
    Otherwises calls userturn again
     */
    public void endTurn(){
        current_user_index++;
        current_user_index %= listOfUsers.size();
        current_user = listOfUsers.get(current_user_index);
        if(roomsLeft < 2){
            dusk();
        }
        board.setButtons(current_user);
    }

    public ArrayList<String> getAdjRooms(){
        Room cur_room = current_user.getPosition().getCurrentRoom();
        ArrayList<String> adjRooms = cur_room.getAdjRooms();
        return adjRooms;
    }

    public ArrayList<String> getPossibleRoles(){
        Room cur_room = current_user.getPosition().getCurrentRoom();
        ArrayList<String> allRoles = cur_room.getRoleNames(current_user.getRank());
        return allRoles;
    }
    /*
    Returns the trailer room
     */
    public Room getTrailer(){
        for(Room curRoom : listOfRooms){
            if(curRoom.getRoomName().toLowerCase().equals("trailer")){
                return curRoom;
            }
        }
        return null;
    }

}

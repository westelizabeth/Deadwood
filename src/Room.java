/*
Class used to create a Room object
Which has a name, a num of credits at start and number of credits left
A List of all the adjacent rooms names, a list of all the users in the room, a list of all the roles in the room
and a check if a room has a scene and a scene object.
 */

import javax.swing.*;
import java.util.ArrayList;

public class Room {

    private String roomName;
    private int creditsRemaining; // Credits at start
    private int currentCredits; // Credits left
    private ArrayList<String> adjRooms = new ArrayList<String>();
    private ArrayList<User> current_users = new ArrayList<User>();
    private ArrayList<Role> roles = new ArrayList<Role>();
    private Scene scene = null;
    public boolean hasScene;
    private ArrayList<Integer> bounds;
    private JLabel sceneGui;
    private boolean beenVisited;
    private ArrayList<ArrayList<Integer>> takes;
    private ArrayList<JLabel> shot_counters;

    public Room(String roomName, ArrayList<String> adjRooms, ArrayList<Role> roles, int creditsRemaining, boolean hasScene, ArrayList<Integer> bounds, ArrayList<ArrayList<Integer>> takes){
        this.roomName = roomName;
        this.adjRooms = adjRooms;
        this.roles = roles;
        this.creditsRemaining = creditsRemaining;
        this.currentCredits = creditsRemaining;
        this.hasScene = hasScene;
        this.bounds = bounds;
        this.takes = takes;
    }

    public void take_off_counter(){
        shot_counters.get(currentCredits).setVisible(false);
    }

    public ArrayList<ArrayList<Integer>> getTakes(){
        return takes;
    }

    public void setShot_counters(ArrayList<JLabel> shot_counters){
        this.shot_counters = shot_counters;
    }

    //returns list of roles player can take
    public ArrayList<String> getRoleNames(int rank){
        ArrayList<String> roleNames = new ArrayList<String>();
        if(roles == null){
            return null;
        }
        for(Role role : roles){
            //rank matches users current rank
            if(rank >= role.getRankNeeded()) {
                roleNames.add(role.getRoleName());
            }
        }
        if(hasScene){
            for(Role role : scene.getRoles()) {
                //rank matches users current rank
                if (rank >= role.getRankNeeded()){
                    roleNames.add(role.getRoleName());
                }
            }
        }
        return roleNames;
    }


    public void setSceneGui(JLabel scene){
        this.sceneGui = scene;
    }

    public JLabel getSceneGui(){
        return sceneGui;
    }

    public ArrayList<String> getAdjRooms(){
        return adjRooms;
    }

    public ArrayList<Integer> getBounds(){
        return bounds;
    }

    // Takes in a Scene object and if the room can have a scene then sets the rooms scene to that Scene
    public void setScene(Scene scene){
        if(hasScene){
            this.scene = scene;
        }

    }

    // Clears all the roles of checks that a user is on the Room (done day to day)
    public void clearRoles(){
        if(roles == null){
            return;
        }
        for(Role curRole : roles){
            curRole.setUserOnRole(false);
        }
    }

    // Resets the current credits of the Room to the initial amount (done day to day)
    public void resetCredits(){
        this.currentCredits = creditsRemaining;
    }

    public Scene getScene(){
        return scene;
    }

    public int getCreditsRemaining(){
        return currentCredits;
    }

    // Takes in a number of credits to be removed from the current credits and returns the number of credits left after
    public int removeCredit(int amountToRemove){
        currentCredits -= amountToRemove;
        return currentCredits;
    }

    // Takes in the name of a role and checks if the room has that role name, if so returns the Role object
    public Role hasRole(String roleName){
        if(roles == null) return null;
        for(Role curRole : roles){
            if(curRole.getRoleName().toLowerCase().equals(roleName.toLowerCase())){
                return curRole;
            }
        }
        return null;
    }

    public String getRoomName(){
        return roomName;
    }

    // Takes in the name of a room and checks if that room is adjacent to this one.
    public boolean isAdjacent(String destRoom){
        for(String curRoomName : adjRooms){
            if(curRoomName.toLowerCase().equals(destRoom.toLowerCase())) return true;
        }
        return false;
    }

    public ArrayList<User> getUsers(){
        return current_users;
    }

    // Takes a User and adds them to the list of users in the room
    public void addUser(User newUser){
        if(!beenVisited){
            if(hasScene){
                sceneGui.setVisible(true);
            }
        }
        else{
            beenVisited = true;
        }
        this.current_users.add(newUser);
    }

    // Takes a user object and removes them from the list of users in the room
    public void removeUser(User leavingUser){
        current_users.remove(leavingUser);
    }

    public ArrayList<Role> getRoles(){
        return roles;
    }
}

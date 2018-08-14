/*
Class used to create a role object
Which has a role name, a speech line, a rank needed to take the role, a hierarchy rank for the bonus roll
a check for if a user has the role and a check if the role is on a scene or not.
 */

import java.util.ArrayList;

public class Role{
    //attributes
    private String roleName;
    private int rankNeeded;
    private int sceneHierarchyRank;// 0 is lowest and higher you go the better// note to self: The ranking of the role for bonus payout Need to calculate somehow?
    private boolean userOnRole;
    private boolean roleOnCard;
    private ArrayList<Integer> bounds;
    //constructor
    public Role(String roleName,  int rankNeeded, boolean roleOnCard, int sceneHierarchyRank, ArrayList<Integer> bounds){
        this.roleName = roleName;
        this.rankNeeded = rankNeeded;
        this.roleOnCard = roleOnCard;
        this.sceneHierarchyRank = sceneHierarchyRank;
        this.bounds = bounds;
    }

    //Getters and setters
    public ArrayList<Integer> getBounds(){
        return bounds;
    }

    public boolean getUserOnRole(){
        return userOnRole;
    }

    public void setUserOnRole(boolean userOnRole){
        this.userOnRole = userOnRole;
    }

    public String getRoleName(){
        return roleName;
    }

    public int getRankNeeded(){
        return rankNeeded;
    }

    public int getSceneHierarchyRank(){
        return sceneHierarchyRank;
    }

    public boolean isRoleOnCard(){
        return roleOnCard;
    }

}

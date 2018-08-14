/*
Class used to create a Position object.
Each user will have a position object
Position object holds, a current room, scene, role and the rank the user has for their current role(helps the user act).
 */
public class Position{
  //attributes
  private Room currentRoom;
  private Scene currentScene;
  private Role userRole;
  //roleRank is Rehearsal Points.
  private int roleRank = 0;

  //constructor: parameters are currentroom, currentScene, userRole)
  public Position(Room currentRoom, Scene currentScene, Role userRole){
    this.currentRoom = currentRoom;
    this.currentScene = currentScene;
    this.userRole = userRole;
  }

  // Just adds to the roleRank by one
  public boolean rehearse(){
    if(hasUserRole()){
      roleRank++;
      return true;
    }
    else{
      return false;
    }
  }

  //changes the roleRank to 0
  public void resetRoleRank(){
    this.roleRank = 0;
  }

  // Returns the current room
  public Room getCurrentRoom(){
    return this.currentRoom;
  }

  // Sets the current room
  public void setCurrentRoom(Room newRoom){
    this.currentRoom = newRoom;
  }

  // Gets the users role
  public Role getUserRole(){
    return userRole;
  }

  // Sets the user's role
  public void setUserRole(Role newRole){
    this.userRole = newRole;
  }

  // Returns true or false depending on if the user currently has a role or not
  public boolean hasUserRole(){
    if(this.userRole != null)
      return true;
    else
      return false;
  }

  // Returns the users scene
  public Scene getCurrentScene(){
    return currentScene;
  }

  // Sets the current scene
  public void setCurrentScene(Scene newScene){
    this.currentScene = newScene;
  }

  // Returns the current role rank
  public int getRoleRank(){
    return roleRank;
  }
}

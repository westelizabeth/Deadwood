import java.util.ArrayList;
/*
Class that is used to crete a scene object
Scene has a name, budget, description and a list of roles
 */
public class Scene {
    private int budget;
    private ArrayList<Role> roles = new ArrayList<Role>();
    private String image;

    public Scene( int budget,  ArrayList<Role> roles, String image) {
        this.budget = budget;
        this.roles = roles;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    // Takes in a role name and returns the role object if it's in that scene
    public Role hasRole(String roleName) {
        for (Role curRole : roles) {
            if (curRole.getRoleName().toLowerCase().equals(roleName.toLowerCase())) {
                return curRole;
            }
        }
        return null;
    }

    // Sets the check for if a user is on a role to false for all roles on the scene
    public void clearRoles() {
        if (roles == null) {
            return;
        }
        for (Role curRole : roles) {
            curRole.setUserOnRole(false);
        }
    }

    // Returns a list of all the roles on the scene
    public ArrayList<Role> getRoles() {
        return roles;
    }

    // Returns the budget for the scene
    public int getBudget() {
        return budget;
    }


}

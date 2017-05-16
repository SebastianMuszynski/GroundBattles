package futuremakers.groundbattles;

import com.hypertrack.lib.models.User;

public class UserData {
    private static final UserData userDataInstance = new UserData();
    private User userInstance;

    public static UserData getInstance() {
        return userDataInstance;
    }

    public void setUser(User u) {
        userInstance = u;
    }

    public User getUser() {
        return userInstance;
    }

}

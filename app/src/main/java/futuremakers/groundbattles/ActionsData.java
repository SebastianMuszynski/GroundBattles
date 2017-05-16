package futuremakers.groundbattles;

import java.util.ArrayList;
import java.util.List;

public class ActionsData {
    private static final ActionsData actionsData = new ActionsData();
    private List<String> actionIds = new ArrayList<>();

    public static ActionsData getInstance() {
        return actionsData;
    }

    public void addAction(String actionId) {
        actionIds.add(actionId);
    }

    public List<String> getActionIds() {
        return actionIds;
    }
}

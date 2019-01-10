package MAIN_CLASSES;

import java.util.HashMap;

public class Chat {

    String percentage, Groupid;
    HashMap<String,Boolean> members;

    public String getGroupid() {
        return Groupid;
    }

    public void setGroupid(String groupid) {
        Groupid = groupid;
    }

    String name;

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }


}

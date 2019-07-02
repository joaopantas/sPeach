package MAIN_CLASSES;

import java.util.Date;
import java.util.HashMap;

public class Group {

    HashMap<String,Boolean> members;
    HashMap<String,Date> chatslist;
    String name;
    String creator;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    String uri;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    //TODO insert color to be inserted
    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }

    public HashMap<String, Date> getChatslist() {
        return chatslist;
    }

    public void setChatslist(HashMap<String, Date> chatslist) {
        this.chatslist = chatslist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

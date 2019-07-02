package MAIN_CLASSES;

import java.util.HashMap;

public class Chat {

    String percentage;
    String groupid;

    public String getFirst_message() {
        return first_message;
    }

    public void setFirst_message(String first_message) {
        this.first_message = first_message;
    }

    String first_message;
    HashMap<String,Boolean> members;

    public HashMap<String, String> getEncryptedid() {
        return encryptedid;
    }

    public void setEncryptedid(HashMap<String, String> encryptedid) {
        this.encryptedid = encryptedid;
    }

    HashMap<String,String> encryptedid;



    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupId) {
        groupid = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    String topic;

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }


}

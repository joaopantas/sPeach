package MAIN_CLASSES;

import java.util.HashMap;

public class PublicChat {

    String percentage;
    String groupid;
    String creator;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    int size;

    public String getFirst_message() {
        return first_message;
    }

    public void setFirst_message(String first_message) {
        this.first_message = first_message;
    }

    String first_message;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    String topic;

    public HashMap<String, String> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }

    HashMap<String,String> members;

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


    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }


}

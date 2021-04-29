package developer.adithya.chatapp.Model;

import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;

public class ChatList {

    private String id;

    public ChatList() {
    }

    public ChatList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

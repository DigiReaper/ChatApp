package developer.adithya.chatapp.Model;

public class Users {
    private String id;
    private String Name;
    private String PhoneNumber;
    private String imageUrl = "default";
    private String status;

    public Users() {
    }

    public Users(String id, String name, String phoneNumber, String imageUrl, String status) {
        this.id = id;
        Name = name;
        PhoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public Users(String id, String name, String phoneNumber, String imageUrl) {
        this.id = id;
        Name = name;
        PhoneNumber = phoneNumber;
        this.imageUrl = imageUrl;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

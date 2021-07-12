package android.example.videocallapp.Models;


//It stores the data of user i.e. email, password, profilePicUrl , uid , etc
public class User {
    private String name , email , pass , profileImage = null , uid;
    public User(){

    }

    public User(String name, String profileImage, String uid) {
        this.name = name;
        this.profileImage = profileImage;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String name, String email, String pass, String profileImage) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.profileImage = profileImage;
    }



    public User(String name){
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}

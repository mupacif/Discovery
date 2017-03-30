package be.formation.mupacif.discovery.model;


public class User {
    private String username;
    private String password;
    private String email;

    private String salt;


    public User(String username, String password, String email, String salt) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {return salt; }

    public void setSalt(String salt) { this.salt = salt; }
}

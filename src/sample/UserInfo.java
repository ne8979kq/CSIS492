package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserInfo {
    private SimpleIntegerProperty gamesWon; private SimpleStringProperty userName;
    private SimpleStringProperty passWord; private SimpleStringProperty email;
    private SimpleIntegerProperty gamesLost;

    public UserInfo(String email, String un, String pw, int won, int lost) {
        this.email = new SimpleStringProperty(email);
        this.userName = new SimpleStringProperty(un);
        this.passWord = new SimpleStringProperty(pw);
        this.gamesWon = new SimpleIntegerProperty(won);
        this.gamesLost = new SimpleIntegerProperty(lost);
    }

    public int getGamesWon() {
        return gamesWon.get();
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = new SimpleIntegerProperty(gamesWon);
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName = new SimpleStringProperty(userName);
    }

    public String getPassWord() {
        return passWord.get();
    }

    public void setPassWord(String passWord) {
        this.passWord = new SimpleStringProperty(passWord);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email = new SimpleStringProperty(email);
    }

    public int getGamesLost() {
        return gamesLost.get();
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = new SimpleIntegerProperty(gamesLost);
    }
}

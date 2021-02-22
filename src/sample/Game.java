package sample;


public class Game {
    private int gameID;
    private String gameUser;
    private long curPoints;
    private int isDone;

    public Game(int id, String name, long points, int done) {

    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameUser() {
        return gameUser;
    }

    public void setGameUser(String gameUser) {
        this.gameUser = gameUser;
    }

    public long getCurPoints() {
        return curPoints;
    }

    public void setCurPoints(long curPoints) {
        this.curPoints = curPoints;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }
}


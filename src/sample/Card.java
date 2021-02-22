package sample;


public class Card {
    static private int nextID = 0;
    private int cardID;
    private String cardName;
    private long pointValue;
    private String cardURL;

    public Card(String name, long points, String url) {
        this.setCardID();
        this.setCardName(name); this.setPointValue(points);
        this.setCardURL(url);
    }

    public Card() {
        this.setCardID();
        this.setCardName(""); this.setPointValue(0);
        this.setCardURL("");
    }

    private void setCardID() {
        this.cardID = nextID;
        nextID++;
    }

    public int getCardID() {
        return cardID;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public long getPointValue() {
        return pointValue;
    }

    public void setPointValue(long pointValue) {
        this.pointValue = pointValue;
    }

    public String getCardURL() {
        return cardURL;
    }

    public void setCardURL(String cardURL) {
        this.cardURL = cardURL;
    }

    public void resetNextID() {
        nextID = 0;
    }
}

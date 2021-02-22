package sample;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static javafx.application.Platform.exit;

public class GameController implements Initializable {
    ObservableList<UserInfo> userInfo;
    static String userName; boolean isTurn; int score = 0; boolean done = false;
    int round; Connection connection;
    ArrayList<Card> cardArray;
    ArrayList<Card> userCards; ArrayList<Card> comCards;
    Card temporaryCard = new Card();
    static ResultSet rs;

    @FXML TableView<UserInfo> userTable;
    @FXML TableColumn<UserInfo, String> nameCol;
    @FXML TableColumn<UserInfo, Integer> winsCol;
    @FXML TableColumn<UserInfo, Integer> lossCol;
    @FXML VBox comCard1; @FXML VBox comCard2; @FXML VBox comCard3;
    @FXML VBox userCard1; @FXML VBox userCard2; @FXML VBox userCard3;
    @FXML VBox cardDeck; @FXML VBox discardPile; @FXML VBox chip1;
    @FXML VBox chip2; @FXML VBox chip3; @FXML VBox comChip1; @FXML VBox comChip2;
    @FXML VBox comChip3;
    @FXML Button btnStartGame; @FXML Button btnExitGame; @FXML Button btnRestartGame;
    @FXML Button btnCard1; @FXML Button btnCard2; @FXML Button btnCard3;
    @FXML Label lblComCards; @FXML Label lblYourCards; @FXML Label lblGoFirst;
    @FXML Label lblScore; @FXML MenuItem cmiSave; @FXML MenuItem cmiLoad;
    @FXML MenuItem cmiLogout; @FXML MenuItem cmiStartNew; @FXML MenuItem cmiChangeDeck;
    @FXML MenuItem cmiRules; @FXML MenuItem cmiAbout; @FXML MenuItem cmiDelete;
    @FXML Button btnPrev; @FXML Button btnNext; @FXML Label lblUN; @FXML Label lblWins;
    @FXML Label lblLosses; @FXML TableView<UserInfo> searchTable; @FXML TableColumn<UserInfo, String> tcSearchName;
    @FXML TableColumn<UserInfo, Integer> tcSearchWins; @FXML TableColumn<UserInfo, Integer> tcSearchLosses;
    @FXML Label lblQuery1; @FXML Label lblQuery2; @FXML ComboBox comboQuery1; @FXML ComboBox comboQuery2;
    @FXML Button btnSearch1; @FXML TableView<Game> tblGame; @FXML TableColumn<Game, Integer> tcID;
    @FXML TableColumn<Game, String> tcUserName; @FXML TableColumn<Game, Integer> tcPoints;
    @FXML TableColumn<Game, Integer> tcComplete;

    @FXML
    public void onSearch() {
        try {
            ObservableList<UserInfo> searchList = FXCollections.observableArrayList();
            PreparedStatement st = connection.prepareStatement("select * from Users where " +
                    comboQuery1.getValue().toString() + " = '" + comboQuery2.getValue().toString() + "'");
            rs = st.executeQuery();
            while (rs.next()) {
                searchList.add(new UserInfo(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), rs.getInt(5)));
            }
            setUpSearchTable(searchList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUpComboBoxes() {
        ObservableList<String> obColList = FXCollections.observableArrayList();
        obColList.add("username"); obColList.add("gamesWon"); obColList.add("gamesLost");
        comboQuery1.setValue(obColList.get(0));
        comboQuery1.setItems(obColList);
        setUpCombo2();
        comboQuery1.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                setUpCombo2();
            }
        });

    }

    public void setUpCombo2() {
        ObservableList<String> obValueList = FXCollections.observableArrayList();
        switch (comboQuery1.getValue().toString()) {
            case "username":
                for (UserInfo ui:userInfo) {
                    obValueList.add(ui.getUserName());
                }
                break;
            case "gamesWon":
                for (UserInfo ui:userInfo) {
                    if (!obValueList.contains(String.valueOf(ui.getGamesWon())))
                        obValueList.add(String.valueOf(ui.getGamesWon()));
                }
                break;
            case "gamesLost":
                for (UserInfo ui:userInfo) {
                    if (!obValueList.contains(String.valueOf(ui.getGamesLost())))
                        obValueList.add(String.valueOf(ui.getGamesLost()));
                }
                break;
        }
        comboQuery2.setValue(obValueList.get(0));
        comboQuery2.setItems(obValueList);
    }

    @FXML
    public void prevUserClicked(ActionEvent event) throws  SQLException {
        if (!rs.isFirst()) {
            rs.previous();
        }
        lblUN.setText(rs.getString(2)); lblWins.setText(String.valueOf(rs.getInt(4)));
        lblLosses.setText(String.valueOf(rs.getInt(5)));
    }

    @FXML
    public void nextUserClicked(ActionEvent event) throws SQLException {
        if (!rs.isLast()) {
            rs.next();
        }
        lblUN.setText(rs.getString(2)); lblWins.setText(String.valueOf(rs.getInt(4)));
        lblLosses.setText(String.valueOf(rs.getInt(5)));
    }

    @FXML
    private void saveGame(ActionEvent event) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement("insert into Games (username, currentScore," +
                    " completed) values (?, ?, ?)");
            for (UserInfo ui : userInfo) {
                if (ui.getEmail().equals(userName) || ui.getUserName().equals(userName))
                    userName = ui.getUserName();
            }
            insertStatement.setString(1, userName);
            insertStatement.setString(2, String.valueOf(score));
            insertStatement.setString(3, String.valueOf(0));
            int row = insertStatement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadGame(ActionEvent e) {
        Alert loadAlert = new Alert(Alert.AlertType.CONFIRMATION);
        loadAlert.setTitle("Load Game");
        loadAlert.setHeaderText("No games to load right now!");
        loadAlert.showAndWait();
        //TableView<UserInfo> loadTable = new TableView<>();
        //TableColumn<U>
    }

    @FXML
    private void handleLogout(ActionEvent e) throws IOException {
        temporaryCard.resetNextID();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Scene detailScene = new Scene(root);
        System.out.println("Switching to Scene 1...");
        Stage stage = (Stage) btnCard2.getScene().getWindow();
        stage.setScene(detailScene);
        stage.centerOnScreen(); stage.show();
    }

    @FXML
    private void startNewGame(ActionEvent e) throws Exception {
        restartGame();
    }

    @FXML
    private void changeDeck(ActionEvent e) throws FileNotFoundException {
        FileChooser fCh = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("IMAGE files (*.gif, *.jpg, *.png)",
                        "*.gif", "*.jpg", "*.png");
        fCh.getExtensionFilters().add(extFilter);
        fCh.setTitle("Select New Card Back");
        File imgFile = fCh.showOpenDialog(btnCard2.getScene().getWindow());
        if (imgFile.isFile()) {
            System.out.println(imgFile.getAbsolutePath());
            String path = imgFile.getAbsolutePath().substring(imgFile.getAbsolutePath().lastIndexOf("src") + 4);
            System.out.println(path);
            cardDeck.setStyle("-fx-background-image: url('" + path + "');");
            comCard1.setStyle("-fx-background-image: url('" + path + "');");
            comCard2.setStyle("-fx-background-image: url('" + path + "');");
            comCard3.setStyle("-fx-background-image: url('" + path + "');");
        }
    }

    @FXML
    private void deleteAccount(ActionEvent event) throws IOException {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Deleting Account...");
        deleteAlert.setHeaderText("Are you sure?");
        deleteAlert.setContentText("If you click ok, you won't be able to undo this change.");
        Optional<ButtonType> choice = deleteAlert.showAndWait();
        if (choice.get() == ButtonType.OK) {
            for (UserInfo ui:userInfo) {
                if (ui.getUserName().equals(userName) || ui.getEmail().equals(userName)) {
                    userName = ui.getUserName();
                }
            }
            try (Statement statement = connection.createStatement();) {
                statement.executeUpdate("delete from Users  where username='" + userName + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        handleLogout(event);
    }

    @FXML
    private void showRules(ActionEvent e) {
        Alert rules = new Alert(Alert.AlertType.INFORMATION);
        rules.setTitle("Rules for 99");
        rules.setHeaderText("The rules are as follows: ");
        rules.setContentText("The player who goes first is selected at random.\n" +
                "The player will choose a card to add to the total card sum; there are 4 special cards.\n" +
                "The special cards are:\n" +
                "1. Threes --> Playing a three will automatically take the counter to 99.\n" +
                "2. Fours --> Playing a four would normally reverse the direction of play, but there are only two players here.\n" +
                "3. Nines --> Playing a nine would normally skip the next player, but there are only two players here.\n" +
                "4. Tens --> Playing a ten decrements the total sum by 10.\n" +
                "If the total sum is at 99, the player must have one of the special cards to keep playing. The total score cannot go above 99, and cards that would cause this to happen cannot be played. If the player has " +
                "no special cards, then they lose the round and a chip. When all the player's chips are exhausted, the game is lost.");
        rules.showAndWait();
    }

    @FXML
    private void showAbout(ActionEvent e) {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About the App");
        about.setContentText("Author: Ronni Kurtzhals\nApp Version: 1.0");
        about.showAndWait();
    }

    @FXML
    private void onExit(ActionEvent event) {
        exit();
    }

    public void readJSON() throws Exception {
        cardArray = new ArrayList<>();
        Object obj = new JSONParser().parse(new FileReader("cards.json"));
        JSONObject jObj = (JSONObject) obj;
        JSONArray jsonArray = (JSONArray) jObj.get("cards");
        Iterator itr = jsonArray.iterator();
        while (itr.hasNext()) {
            JSONObject cardObj = (JSONObject) itr.next();
            String cardName = (String) cardObj.get("name");
            long cardPoints = (long) cardObj.get("points");
            String url = (String) cardObj.get("url");
            cardArray.add(new Card(cardName, cardPoints, url));
        }
    }

    public void selectComputerCards() {
        comCards = new ArrayList<>();
        Random rand = new Random();
        int com1 = rand.nextInt(52);
        int com2 = rand.nextInt(52);
        while (com2 == com1) {
            com2 = rand.nextInt(52);
        }
        int com3 = rand.nextInt(52);
        while (com3 == com2 || com3 == com1) {
            com3 = rand.nextInt(52);
        }
        for (Card card:cardArray) {
            if (card.getCardID() == com1 || card.getCardID() == com2 || card.getCardID() == com3) {
                comCards.add(card);
            }
        }
        for (Card com:comCards) {
            cardArray.remove(com);
        }
    }

    public void selectUserCards() {
        userCards = new ArrayList<>();
        ArrayList<Integer> cardIdsRemaining = new ArrayList<>();
        for (Card card1:cardArray) {
            cardIdsRemaining.add(card1.getCardID());
        }
        Random rand = new Random();
        int user1 = rand.nextInt(52);
        while (!cardIdsRemaining.contains(user1)) {
            user1 = rand.nextInt(52);
        }
        cardIdsRemaining.remove(cardIdsRemaining.indexOf(user1));
        int user2 = rand.nextInt(52);
        while (!cardIdsRemaining.contains(user2)) {
            user2 = rand.nextInt(52);
        }
        cardIdsRemaining.remove(cardIdsRemaining.indexOf(user2));
        int user3 = rand.nextInt(52);
        while (!cardIdsRemaining.contains(user3)) {
            user3 = rand.nextInt(52);
        }
        for (Card card2:cardArray) {
            if (card2.getCardID() == user1 || card2.getCardID() == user2 || card2.getCardID() == user3) {
                userCards.add(card2);
            }
        }
        for (Card user:comCards) {
            cardArray.remove(user);
        }
    }

    public void drawCard() {
        int index; Card tmpCard = new Card();
        ArrayList<Integer> cardIdsRemaining = new ArrayList<>();
        for (Card card1:cardArray) {
            cardIdsRemaining.add(card1.getCardID());
        }
        Random rand = new Random();
        int newCard = rand.nextInt(52);
        while (!cardIdsRemaining.contains(newCard)) {
            newCard = rand.nextInt(52);
        }
        System.out.println(temporaryCard.getCardName());
        if (userCards.contains(temporaryCard)) {
            index = userCards.indexOf(temporaryCard);
            for (Card c:cardArray) {
                if (c.getCardID() == newCard) {
                    userCards.add(index, c);
                    userCards.remove(temporaryCard);

                    tmpCard = c;
                    btnCard1.setStyle("-fx-background-image: url('" + userCards.get(0).getCardURL() + "');");
                    btnCard2.setStyle("-fx-background-image: url('" + userCards.get(1).getCardURL() + "');");
                    btnCard3.setStyle("-fx-background-image: url('" + userCards.get(2).getCardURL() + "');");
                }
            }
        }
        else if (comCards.contains(temporaryCard)) {
            index = comCards.indexOf(temporaryCard);
            for (Card c:cardArray) {
                if (c.getCardID() == newCard) {
                    comCards.remove(temporaryCard);
                    comCards.add(index, c);
                    tmpCard = c;
                }
            }
        }
        cardArray.remove(tmpCard);
        System.out.print("tmpCard: ");
        System.out.println(tmpCard.getCardName());
        System.out.println("From HERE");
        for (Card c:cardArray) {

            System.out.println(c.getCardName());

        }
        System.out.println("To HERE");
    }

    @FXML
    public void startGame() {
        selectComputerCards(); selectUserCards();
        System.out.println("TESTING");
        score = 0;
        createScoreLabel();
        btnStartGame.setVisible(false); btnExitGame.setVisible(true); btnRestartGame.setVisible(true);
        comCard1.setVisible(true); comCard2.setVisible(true); comCard3.setVisible(true);
        userCard1.setVisible(true); userCard2.setVisible(true); userCard3.setVisible(true);
        lblYourCards.setVisible(true); lblComCards.setVisible(true);
        cardDeck.setVisible(true); discardPile.setVisible(true);
        if (round == 0) {
            chip1.setVisible(true);
            chip2.setVisible(true);
            chip3.setVisible(true);
            comChip1.setVisible(true);
            comChip2.setVisible(true);
            comChip3.setVisible(true);
        }
        btnCard1.setStyle("-fx-background-image: url('" + userCards.get(0).getCardURL() + "');");
        btnCard2.setStyle("-fx-background-image: url('" + userCards.get(1).getCardURL() + "');");
        btnCard3.setStyle("-fx-background-image: url('" + userCards.get(2).getCardURL() + "');");
        isTurn = whoGoesFirst();
        playGame();
    }

    public void endTurn() {
        playGame();
    }

    public void roundOver(String name) {
        if (done) {
            Alert roundDone = new Alert(Alert.AlertType.INFORMATION);
            roundDone.setTitle("Round over");
            if (name.equals("user")) {
                roundDone.setContentText("You have lost this round...");
                if (chip1.isVisible() && chip2.isVisible() && chip3.isVisible()) chip1.setVisible(false);
                else if (!chip1.isVisible() && chip2.isVisible() && chip3.isVisible()) chip2.setVisible(false);
                else chip3.setVisible(false);
            } else if (name.equals("com")) {
                roundDone.setContentText("You have won this round!");
                if (comChip1.isVisible() && comChip2.isVisible() && comChip3.isVisible()) comChip1.setVisible(false);
                else if (!comChip1.isVisible() && comChip2.isVisible() && comChip3.isVisible()) comChip2.setVisible(false);
                else comChip3.setVisible(false);
            }
            roundDone.showAndWait();
            round++;
            done = false;
            if (!chip1.isVisible() && !chip2.isVisible() && !chip3.isVisible()) gameOver("user");
            else if (!comChip1.isVisible() && !comChip2.isVisible() && !comChip3.isVisible()) gameOver("com");
            else {
                try {
                    restartGame();
                } catch (Exception e) {
                    System.out.println("Error in restart");
                }
            }
        }
    }

    public void gameOver(String name) {
        round = 0;
        int gamesWonLost = 0;
        Alert over = new Alert(Alert.AlertType.INFORMATION);
        over.setTitle("Game is over");
        if (name.equals("user")) {
            over.setContentText("You have lost the game! Exiting...");
            for (UserInfo ui:userInfo) {
                if (ui.getUserName().equals(userName) || ui.getEmail().equals(userName)) {
                    gamesWonLost = ui.getGamesLost() + 1;
                    userName = ui.getUserName();
                }
            }
            try (Statement statement = connection.createStatement();) {
                statement.executeUpdate("update Users set gamesLost=" + gamesWonLost + " where username='" + userName + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (name.equals("com")) {
            over.setContentText("You have won the game! Exiting...");
            for (UserInfo ui:userInfo) {
                if (ui.getUserName().equals(userName) || ui.getEmail().equals(userName)) {
                    gamesWonLost = ui.getGamesLost() + 1;
                    userName = ui.getUserName();
                }
            }
            try (Statement statement = connection.createStatement();) {
                statement.executeUpdate("update Users set gamesWon=" + gamesWonLost + " where username='" + userName + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        over.showAndWait();
        try {
            exitGame();
        } catch (Exception e) {
            System.out.println("Error in exit");
        }
        userTable.refresh();
    }

    public void playGame() {
        if (isTurn) {
            btnCard1.disableProperty().setValue(false);
            btnCard2.disableProperty().setValue(false);
            btnCard3.disableProperty().setValue(false);
            boolean canPlay = false;
            if (!canPlay) {
                for (Card userChoice:userCards) {
                    temporaryCard = userChoice;
                    if (ableToPlay()) canPlay = true;
                }
            }
            if (!canPlay) done = true;
            roundOver("user");
        } else {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            PauseTransition tmpPause = new PauseTransition(Duration.seconds(2));
            btnCard1.disableProperty().setValue(true);
            btnCard2.disableProperty().setValue(true);
            btnCard3.disableProperty().setValue(true);
            Random comRand = new Random();
            int comChoice = comRand.nextInt(3);
            pause.setOnFinished(event -> cardSwitch(comChoice));
            if (ableToPlay()) tmpPause.setOnFinished(event -> creatingTurnLabel("Your turn!", ""));
            pause.play();
            tmpPause.play();
            isTurn = true;
            roundOver("com");
            playGame();
        }
    }

    public void createScoreLabel() {
        lblScore.setText("Card Sum: " + score);
        lblScore.setVisible(true);
    }

    public void cardSwitch(int comChoice) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        temporaryCard = comCards.get(comChoice);
        boolean canPlay = ableToPlay();
        if (!canPlay) {
            for (Card cardChoice : comCards) {
                temporaryCard = cardChoice;
                if (ableToPlay()) {
                    canPlay = true;
                }
            }
        }
        if (!canPlay) { this.done = true; }
        if (canPlay) {
            switch (comChoice) {
                case 0:
                    comCard1.setVisible(false);
                    pause.play();
                    drawCard();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    pause.setOnFinished(event -> comCard1.setVisible(true));
                    break;
                case 1:
                    comCard2.setVisible(false);
                    pause.play();
                    drawCard();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    pause.setOnFinished(event -> comCard2.setVisible(true));
                    break;
                case 2:
                    comCard3.setVisible(false);
                    pause.play();
                    drawCard();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    pause.setOnFinished(event -> comCard3.setVisible(true));
                    break;
            }
        }
        if (canPlay && score + temporaryCard.getPointValue() <= 99)
            score += temporaryCard.getPointValue();
        else if (canPlay && temporaryCard.getPointValue() == 99) {
            score = 99;
        }
        createScoreLabel();
    }

    @FXML
    public void onCardClicked(ActionEvent event) {
        boolean canPlay = false;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        switch (event.getSource().toString()) {
            case "Button[id=btnCard1, styleClass=button background-userCards]''":
                temporaryCard = userCards.get(0);
                canPlay = ableToPlay();
                if (canPlay) {
                    userCard1.setVisible(false);
                    //System.out.println("Card 1");
                    pause.play();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    drawCard();
                    pause.setOnFinished(e -> userCard1.setVisible(true));
                    this.isTurn = false;
                }
                break;
            case "Button[id=btnCard2, styleClass=button background-userCards]''":
                temporaryCard = userCards.get(1);
                canPlay = ableToPlay();
                if (canPlay) {
                    userCard2.setVisible(false);
                    //System.out.println("Card 2");
                    pause.play();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    drawCard();
                    pause.setOnFinished(e -> userCard2.setVisible(true));
                    this.isTurn = false;
                }
                break;
            case "Button[id=btnCard3, styleClass=button background-userCards]''":
                temporaryCard = userCards.get(2);
                canPlay = ableToPlay();
                if (canPlay) {
                    userCard3.setVisible(false);
                    //System.out.println("Card 3");
                    pause.play();
                    discardPile.setStyle("-fx-background-image: url('" + temporaryCard.getCardURL() + "');");
                    drawCard();
                    pause.setOnFinished(e -> userCard3.setVisible(true));
                    this.isTurn = false;
                }
                break;
        }
        if (canPlay && score + temporaryCard.getPointValue() <= 99) {
            score += temporaryCard.getPointValue();
            creatingTurnLabel("COM's turn!", "");
        }
        else if (canPlay && temporaryCard.getPointValue() == 99) {
            score = 99;
            creatingTurnLabel("COM's turn!", "");
        } else {
            Alert cardError = new Alert(Alert.AlertType.ERROR);
            cardError.setContentText("You cannot play that card!");
            cardError.showAndWait();
        }
        createScoreLabel();
        endTurn();
    }

    public boolean ableToPlay() {
        if (score + temporaryCard.getPointValue() <= 99 || temporaryCard.getPointValue() == -10)
            return true;
        if (temporaryCard.getPointValue() == 0 || temporaryCard.getPointValue() == 99) {
            String name = temporaryCard.getCardName();
            if (name.equals("Three of Hearts") || name.equals("Three of Spades") || name.equals("Three of Diamonds") || name.equals("Three of Clubs")) {
                return true;
            } else if (name.equals("Four of Hearts") || name.equals("Four of Spades") || name.equals("Four of Diamonds") || name.equals("Four of Clubs")) {
                return true;
            } else if (name.equals("Nine of Hearts") || name.equals("Nine of Spades") || name.equals("Nine of Diamonds") || name.equals("Nine of Clubs")) {
                return true;
            }
        }
        return false;
    }

    @FXML
    public void exitGame() throws Exception {
        round = 0;
        btnStartGame.setVisible(true); btnExitGame.setVisible(false); btnRestartGame.setVisible(false);
        comCard1.setVisible(false); comCard2.setVisible(false); comCard3.setVisible(false);
        userCard1.setVisible(false); userCard2.setVisible(false); userCard3.setVisible(false);
        lblComCards.setVisible(false); lblYourCards.setVisible(false); lblScore.setVisible(false);
        cardDeck.setVisible(false); discardPile.setVisible(false);
        chip1.setVisible(false); chip2.setVisible(false); chip3.setVisible(false);
        comChip1.setVisible(false); comChip2.setVisible(false); comChip3.setVisible(false);
        discardPile.setStyle(null);
        Card tmpCard = new Card(); tmpCard.resetNextID();
        cardArray.clear(); readJSON();
    }

    @FXML
    public void restartGame() throws Exception {
        //round = 0;
        startGame(); discardPile.setStyle(null);
        Card tmpCard = new Card(); tmpCard.resetNextID();
        cardArray.clear(); readJSON();
        //System.out.println("Game has been restarted");
    }

    private Timeline createBlinker(Node node) {
        Timeline blink = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)), new KeyFrame(Duration.seconds(1),
                new KeyValue(node.opacityProperty(), 0, Interpolator.DISCRETE)), new KeyFrame(Duration.seconds(1),
                new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)));
        blink.setCycleCount(0);
        return blink;
    }

    private FadeTransition createFader(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }

    public boolean whoGoesFirst() {
        Random rand = new Random();
        int yourTurn = rand.nextInt(2);
        if (yourTurn == 1) {
            creatingTurnLabel("You go first!", "Good Luck!");
            return true;
        } else {
            creatingTurnLabel("COM goes first!", "Good Luck!");
            return false;
        }
    }

    public void creatingTurnLabel(String initial, String ending) {
        lblGoFirst.setText(initial);
        Timeline blinker = createBlinker(lblGoFirst);
        blinker.setOnFinished(event -> lblGoFirst.setText(ending));
        FadeTransition fader = createFader(lblGoFirst);
        SequentialTransition blinkThenFade = new SequentialTransition(lblGoFirst, blinker, fader);
        blinkThenFade.play();
    }

    public void sendUserInfo(ObservableList<UserInfo> userList, String user, Connection conn) {
        connection = conn;
        userInfo = userList; userName = user;
        //setUpTable(); setUpComboBoxes();
    }
/*
    public void setUpTable() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        winsCol.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));
        lossCol.setCellValueFactory(new PropertyValueFactory<>("gamesLost"));
        userTable.setItems(userInfo);
        userTable.setOnMouseClicked((MouseEvent event) -> {
            int index;
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                index = userTable.getSelectionModel().getSelectedIndex();
                try {
                    UserInfo tmpUser = userTable.getItems().get(index);
                    Alert showGraph = new Alert(Alert.AlertType.INFORMATION);
                    showGraph.setTitle(tmpUser.getUserName() + "'s W/L Ratio");
                    CategoryAxis xAxis = new CategoryAxis();
                    NumberAxis yAxis = new NumberAxis();
                    BarChart bcWvL = new BarChart<String, Number>(xAxis, yAxis);
                    yAxis.setAutoRanging(false);
                    yAxis.setUpperBound(20.0);
                    yAxis.setTickUnit(5.0);
                    yAxis.setMinorTickCount(5);
                    yAxis.setLabel("Amount");
                    setUpBarChart(bcWvL, tmpUser);
                    showGraph.setHeaderText("This is the selected user's wins and losses.");
                    showGraph.getDialogPane().setContent(bcWvL);
                    showGraph.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/
    public void setUpSearchTable(ObservableList searchList) {
        tcSearchName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tcSearchWins.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));
        tcSearchLosses.setCellValueFactory(new PropertyValueFactory<>("gamesLost"));
        searchTable.setItems(searchList);
        ObservableList<Game> gameList = FXCollections.observableArrayList();
        searchTable.setOnMouseClicked((MouseEvent event) -> {
            int index;
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                index = searchTable.getSelectionModel().getSelectedIndex();
                try {
                    UserInfo tmpUser = searchTable.getItems().get(index);
                    rs = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("select * from Games where username = '" + tmpUser.getUserName() + "'");
                    rs.first();
                    while (rs.next()) {
                        System.out.println( rs.getString(2));
                        gameList.add(new Game(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
                    }
                    setUpGamesTable(gameList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUpGamesTable(ObservableList gameList) {
        tcID.setCellValueFactory(new PropertyValueFactory<>("gameID"));
        tcUserName.setCellValueFactory(new PropertyValueFactory<>("gameUser"));
        tcPoints.setCellValueFactory(new PropertyValueFactory<>("curPoints"));
        tcComplete.setCellValueFactory(new PropertyValueFactory<>("isDone"));
        tblGame.setItems(gameList);
        tblGame.setVisible(true);

    }

    public void setUpBarChart(BarChart barChart, UserInfo tmpUser) {
        barChart.setBarGap(10.0);
        barChart.setCategoryGap(5.0);
        barChart.setTitle("Wins vs. Losses");
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data("Wins", tmpUser.getGamesWon()));
        series1.getData().add(new XYChart.Data("Losses", tmpUser.getGamesLost()));

        barChart.getData().addAll(series1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection conn = connectionClass.getConnection();
        try {
            rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("select * from Users");
            rs.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        round = 0;
        try {
            readJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
        comCard1.setVisible(false); comCard2.setVisible(false); comCard3.setVisible(false);
        userCard1.setVisible(false); userCard2.setVisible(false); userCard3.setVisible(false);
        btnRestartGame.setVisible(false); btnExitGame.setVisible(false);
        lblComCards.setVisible(false); lblYourCards.setVisible(false);
        cardDeck.setVisible(false); discardPile.setVisible(false); lblScore.setVisible(false);
        chip1.setVisible(false); chip2.setVisible(false); chip3.setVisible(false);
        comChip1.setVisible(false); comChip2.setVisible(false); comChip3.setVisible(false);
        cardDeck.setStyle("-fx-background-image: url('redCard.jpg');");
        comCard1.setStyle("-fx-background-image: url('redCard.jpg');");
        comCard2.setStyle("-fx-background-image: url('redCard.jpg');");
        comCard3.setStyle("-fx-background-image: url('redCard.jpg');");
        //tblGame.setVisible(false);
    }
}

package sample;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    static ResultSet rs;
    ObservableList<UserInfo> obList = FXCollections.observableArrayList();

    @FXML TextField tfLoginEmail;
    @FXML PasswordField pfLoginPw;
    @FXML TextField tfCreateEmail;
    @FXML PasswordField pfCreatePw;
    @FXML TextField tfUserName;
    @FXML Button btnLogin;
    @FXML Button btnCreate;
    @FXML Hyperlink showCreate;
    @FXML GridPane login;
    @FXML GridPane create;
    @FXML Label accountStatus;
    @FXML VBox vBoxLogin;
    @FXML VBox vBoxCreate;

    Connection connection;
    BooleanBinding loginBinding; BooleanBinding createBinding;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionClass connectionClass = new ConnectionClass();
        connection = connectionClass.getConnection();
        try {
            rs = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("select * from Users");
            rs.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("select * from Users")) {
            while (rs.next()) {
                obList.add(new UserInfo(rs.getString("email"),
                        rs.getString("username"), rs.getString("password"),
                        rs.getInt("gamesWon"), rs.getInt("gamesLost")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tfLoginEmail.requestFocus();
        btnLogin.setDefaultButton(true);
        loginBinding = new BooleanBinding() {
            {
                super.bind(tfLoginEmail.textProperty(),
                        pfLoginPw.textProperty());
            }
            @Override
            protected boolean computeValue() {
                return (tfLoginEmail.getText().isEmpty() ||
                        pfLoginPw.getText().isEmpty());
            }
        };
        createBinding = new BooleanBinding() {
            {
                super.bind(tfCreateEmail.textProperty(),
                        tfUserName.textProperty(),
                        pfCreatePw.textProperty());
            }
            @Override
            protected boolean computeValue() {
                return (tfCreateEmail.getText().isEmpty() ||
                        tfUserName.getText().isEmpty() ||
                        pfCreatePw.getText().isEmpty());
            }
        };
        btnLogin.disableProperty().bind(loginBinding);
        create.setVisible(false);
        vBoxCreate.setStyle("-fx-background-image: url('cards2.png') transparent; -fx-background-size:stretch");
}

    @FXML
    public void switchLoginCreate() {
        if (login.isVisible()) {
            tfCreateEmail.requestFocus();
            btnCreate.setDefaultButton(true);
            btnLogin.setDefaultButton(false);
            login.setVisible(false);
            create.setVisible(true);
            accountStatus.setText("Already have an account?");
            vBoxCreate.setStyle(null);
            vBoxLogin.setStyle("-fx-background-image: url('cards1.png') transparent; -fx-background-size:contain; -fx-background-repeat:no-repeat" +
                    "; -fx-background-position:center");
            tfLoginEmail.clear(); pfLoginPw.clear();
            btnCreate.disableProperty().bind(createBinding);
            btnLogin.disableProperty().unbind();
        } else {
            tfLoginEmail.requestFocus();
            btnCreate.setDefaultButton(false);
            btnLogin.setDefaultButton(true);
            login.setVisible(true);
            create.setVisible(false);
            accountStatus.setText("Don't have an account?");
            vBoxLogin.setStyle(null);
            vBoxCreate.setStyle("-fx-background-image: url('cards2.png') transparent; -fx-background-size:stretch");
            tfCreateEmail.clear(); pfCreatePw.clear(); tfUserName.clear();
            btnLogin.disableProperty().bind(loginBinding);
            btnCreate.disableProperty().unbind();
        }
    }

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        boolean flag = false;
        Alert inputError = new Alert(Alert.AlertType.ERROR);
        inputError.setContentText("The email/username or password you entered is invalid.");
        inputError.setTitle("Incorrect Input");
        String userEmail = tfLoginEmail.getText();
        String userPw = pfLoginPw.getText();
        for (UserInfo user : obList) {
            if (userEmail.equals(user.getEmail()) || userEmail.equals(user.getUserName())) {
                if (userPw.equals(user.getPassWord())) {
                    flag = true;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("game.fxml"));
                    Parent root = loader.load();
                    Scene detailScene = new Scene(root);
                    System.out.println("Switching to Scene 2...");
                    GameController gameController = loader.getController();
                    gameController.sendUserInfo(obList, userEmail, connection);
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(detailScene);
                    stage.centerOnScreen(); stage.show();
                }
            }
        }
        if (!flag) {
            inputError.showAndWait();
        }
        tfLoginEmail.clear(); pfLoginPw.clear();
        tfLoginEmail.requestFocus();
    }

    @FXML
    public void onCreate(ActionEvent event)  throws Exception {
        boolean flag = false;
        String userEmail = tfCreateEmail.getText();
        String userPw = pfCreatePw.getText();
        String username = tfUserName.getText();
        Alert inputError = new Alert(Alert.AlertType.ERROR);
        inputError.setTitle("Incorrect Input");
        for (UserInfo user : obList) {
            if (userEmail.equals(user.getEmail())) {
                inputError.setContentText("That email is already in use.");
                flag = true;
            } else if (username.equals(user.getUserName())) {
                inputError.setContentText("That username is already in use.");
                flag = true;
            }
        }
        if (flag) {
            inputError.showAndWait();
        } else {
            PreparedStatement insertStatement = connection.prepareStatement("insert into Users (email, username, password," +
                    " gamesWon, gamesLost) values (?, ?, ?, 0, 0)");
            insertStatement.setString(1, userEmail); insertStatement.setString(2, username);
            insertStatement.setString(3, userPw);
            int row = insertStatement.executeUpdate();
            System.out.println(row);
            obList.add(new UserInfo(userEmail, username, userPw, 0, 0));
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("game.fxml"));
            Parent root = loader.load();
            Scene detailScene = new Scene(root);
            System.out.println("Switching to Scene 2...");
            GameController gameController = loader.getController();
            gameController.sendUserInfo(obList, userEmail, connection);
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
            stage.centerOnScreen();
            stage.show();
        }
        tfCreateEmail.clear(); pfCreatePw.clear(); tfUserName.clear();
        tfCreateEmail.requestFocus();
    }
}

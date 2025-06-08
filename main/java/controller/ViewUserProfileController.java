package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Model;
import util.PasswordUtils;
import util.TransitionUtils;

public class ViewUserProfileController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label preferredNameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private VBox vBox;
    @FXML
    private Button updateProfileButton;

    private Model model;
    private Runnable onProfileUpdate;
    private static Stage stage;

    public ViewUserProfileController(Model model, Runnable onProfileUpdate, Stage stage) {
        this.model = model;
        this.onProfileUpdate = onProfileUpdate;
        this.stage = stage;
    }

    @FXML
    public void initialize() {

        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(updateProfileButton);

//        String encryptedPassword = model.getCurrentUser().getPassword();
        String decryptedPassword = PasswordUtils.decrypt(model.getCurrentUser().getPassword());

        usernameLabel.setText("Username: " + model.getCurrentUser().getUsername());
        preferredNameLabel.setText("Preferred Name: " + model.getCurrentUser().getPreferredName());
        passwordLabel.setText("Password: " + decryptedPassword);

        updateProfileButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateUserProfileView.fxml"));
                UpdateUserProfileController updateController = new UpdateUserProfileController(model, this::userRefresh);
                loader.setController(updateController);
                Pane root = loader.load();
                Stage updateStage = new Stage();
                updateStage.setTitle("Update Profile");
                updateStage.setScene(new Scene(root));
                updateStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }


    private void userRefresh(){
        if (model.getCurrentUser() != null){
            usernameLabel.setText("Username: " + model.getCurrentUser().getUsername());
            preferredNameLabel.setText("Preferred Name: " + model.getCurrentUser().getPreferredName());
            passwordLabel.setText("Password: " + PasswordUtils.decrypt(model.getCurrentUser().getPassword()));
            onProfileUpdate.run();
        }
    }

    public static void showStage(VBox root) {
        Scene scene = new Scene(root, 300,  450);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("User Profile");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}




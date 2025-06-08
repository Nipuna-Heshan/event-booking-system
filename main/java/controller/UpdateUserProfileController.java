package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Model;
import util.PasswordUtils;
import util.TransitionUtils;

import java.sql.SQLException;

public class UpdateUserProfileController {
    @FXML private TextField preferredNameField;
    @FXML private PasswordField passwordField;
    @FXML private Button updateButton;
    @FXML private Label statusLabel;
    @FXML
    private VBox vBox;

    private final Model model;
    private final Runnable onProfileUpdate;

    public UpdateUserProfileController(Model model, Runnable onProfileUpdate) {
        this.model = model;
        this.onProfileUpdate = onProfileUpdate;
    }

    @FXML
    public void initialize() {

        preferredNameField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN -> passwordField.requestFocus();
                case ENTER -> passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> updateButton.fire();
                case UP -> preferredNameField.requestFocus();
                case DOWN -> updateButton.requestFocus();
            }
        });

        TransitionUtils.buttonTransition(updateButton);
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.textFieldTransition(preferredNameField);
        TransitionUtils.passwordFieldTransition(passwordField);

        String prevPassword = PasswordUtils.decrypt(model.getCurrentUser().getPassword());
        preferredNameField.setText(model.getCurrentUser().getPreferredName());
        passwordField.setText(prevPassword);

        updateButton.setOnAction(e -> {
            String newPreferredName = preferredNameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            if (newPreferredName.isEmpty() && newPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Password and preferred name cannot be empty.");
                return;
            }

            try {
                String encrypted = (newPassword.equals(prevPassword)? model.getCurrentUser().getPassword(): PasswordUtils.encrypt(newPassword));
                System.out.println(encrypted);
                model.getUserDao().updateUserProfile(model.getCurrentUser().getUsername(), newPreferredName, encrypted);
                model.getCurrentUser().setPassword(encrypted);
                model.getCurrentUser().setPreferredName(newPreferredName);
                showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully.");
                onProfileUpdate.run();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Failed: " + ex.getMessage());
            }

        });
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

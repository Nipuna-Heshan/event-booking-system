package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Model;
import util.MessageCodeUtils;
import util.PasswordUtils;
import util.TransitionUtils;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

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
        String prevPassword = PasswordUtils.decrypt(model.getCurrentUser().getPassword());
        preferredNameField.setText(model.getCurrentUser().getPreferredName());
        passwordField.setText(prevPassword);

        updateButton.setOnAction(e -> {
            String newPreferredName = preferredNameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            if (newPreferredName.isEmpty() && newPassword.isEmpty()) {
                MessageCodeUtils.showError(statusLabel, "Password and preferred name cannot be empty.");
                return;
            }

            try {
                String encrypted = (newPassword.equals(prevPassword)? model.getCurrentUser().getPassword(): PasswordUtils.encrypt(newPassword));
                System.out.println(encrypted);
                model.getUserDao().updateUserProfile(model.getCurrentUser().getUsername(), newPreferredName, encrypted);
                model.getCurrentUser().setPassword(encrypted);
                model.getCurrentUser().setPreferredName(newPreferredName);
                MessageCodeUtils.showSuccess(statusLabel, "Profile updated successfully.");
                onProfileUpdate.run();
            } catch (SQLException ex) {
                MessageCodeUtils.showError(statusLabel, "Failed: " + ex.getMessage());
            }

        });
    }
}

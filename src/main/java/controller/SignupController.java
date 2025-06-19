package controller;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Model;
import model.User;
import util.PasswordUtils;
import util.TransitionUtils;

public class SignupController {
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private TextField preferredName;
	@FXML
	private Button createUser;
	@FXML
	private Button close;
	@FXML
	private GridPane formContainer;
	
	private Stage stage;
	private Stage parentStage;
	private Model model;
	
	public SignupController(Stage parentStage, Model model) {
		this.stage = new Stage();
		this.parentStage = parentStage;
		this.model = model;
	}

	@FXML
	public void initialize() {

		username.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case DOWN -> preferredName.requestFocus();
				case ENTER -> preferredName.requestFocus(); //Enter also moves down
			}
		});

		preferredName.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case UP -> username.requestFocus();
				case DOWN -> password.requestFocus();
				case ENTER -> password.requestFocus(); //Enter also moves down
			}
		});

		password.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> createUser.fire(); // Same as clicking Login button
				case UP -> preferredName.requestFocus(); // UP moves back
				case DOWN -> createUser.requestFocus();
			}
		});

		createUser.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> createUser.fire(); // Same as clicking Login button
				case UP -> password.requestFocus(); //  UP moves back
				case RIGHT -> close.requestFocus();
			}
		});

		close.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> close.fire(); // Same as clicking Login button
				case UP -> password.requestFocus(); //  UP moves back
				case LEFT -> createUser.requestFocus();
			}
		});

		TransitionUtils.gridTransition(formContainer);
		TransitionUtils.textFieldTransition(username);
		TransitionUtils.textFieldTransition(preferredName);
		TransitionUtils.textFieldTransition(password);
		TransitionUtils.buttonTransition(createUser);
		TransitionUtils.buttonTransition(close);

		createUser.setOnAction(event -> {
			if (!username.getText().isEmpty() && !password.getText().isEmpty() && !preferredName.getText().isEmpty()) {
				User user;
				try {
					String encryptedPassword = PasswordUtils.encrypt(password.getText());
					user = model.getUserDao().createUser(username.getText(), preferredName.getText(), encryptedPassword);
					if (user != null) {
						showAlert(Alert.AlertType.INFORMATION, user.getUsername() + " created successfully!");
					} else {
						showAlert(Alert.AlertType.ERROR, "Cannot create user.");
					}
				} catch (SQLException e) {
					if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("constraint")) {
						showAlert(Alert.AlertType.ERROR, "Username already exists.");
					} else {
						showAlert(Alert.AlertType.ERROR, e.getMessage());
					}
				}
				
			} else {
				showAlert(Alert.AlertType.ERROR, "Empty username or preferred name or password");
			}
		});

		close.setOnAction(event -> {
			stage.close();
			parentStage.show();
		});
	}
	
	public void showStage(Pane root) {
		Scene scene = new Scene(root, 400, 550);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Sign up");
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

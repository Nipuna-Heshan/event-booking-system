package controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Model;
import model.User;
import util.MessageCodeUtils;
import util.PasswordUtils;
import util.TransitionUtils;

public class LoginController {
	@FXML
	private TextField name;
	@FXML
	private PasswordField password;
	@FXML
	private Label message;
	@FXML
	private Button login;
	@FXML
	private Button signup;
	@FXML
	private GridPane formContainer;

	private Model model;
	private Stage stage;
	
	public LoginController(Stage stage, Model model) {
		this.stage = stage;
		this.model = model;
	}



	
	@FXML
	public void initialize() {
		TransitionUtils.gridTransition(formContainer);
		TransitionUtils.buttonTransition(login);
		TransitionUtils.buttonTransition(signup);

		name.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case DOWN -> password.requestFocus();
				case ENTER -> password.requestFocus(); //Enter also moves down
			}
		});

		password.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> login.fire(); // Same as clicking Login button
				case UP -> name.requestFocus(); // UP moves back
				case DOWN -> login.requestFocus();
			}
		});

		login.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> login.fire(); // Same as clicking Login button
				case UP -> password.requestFocus(); //  UP moves back
				case RIGHT -> signup.requestFocus();
			}
		});

		signup.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case ENTER -> signup.fire(); // Same as clicking Login button
				case UP -> password.requestFocus(); //  UP moves back
				case LEFT -> login.requestFocus();
			}
		});


		login.setOnAction(event -> {
			if (!name.getText().isEmpty() && !password.getText().isEmpty()) {
				User user;
				try {
					if (name.getText().equals("admin") && password.getText().equals("Admin321")){
						try{
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminHomeView.fxml"));
							AdminHomeController adminHomeController = new AdminHomeController(stage, model);

							loader.setController(adminHomeController);
							VBox root = loader.load();

							adminHomeController.showStage(root);
							stage.close();
						}catch (IOException e){
							e.printStackTrace();
						}
						return;
					}
					String encryptedInput = PasswordUtils.encrypt(password.getText());
					user = model.getUserDao().getUser(name.getText(), encryptedInput);
					if (user != null) {
						model.setCurrentUser(user);
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HomeView.fxml"));
							HomeController homeController = new HomeController(stage, model);
							
							loader.setController(homeController);
							VBox root = loader.load();
	
							homeController.showStage(root);
							stage.close();
						}catch (IOException e) {
							message.setText(e.getMessage());
						}
						
					} else {
						MessageCodeUtils.showError(message, "Wrong username or password");
					}
				} catch (SQLException e) {
					message.setText(e.getMessage());
				}
				
			} else {
				MessageCodeUtils.showError(message,"Empty username or password");
				name.clear();
				password.clear();
			}
		});
		
		signup.setOnAction(event -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignupView.fxml"));
				
				// Customize controller instance
				SignupController signupController =  new SignupController(stage, model);

				loader.setController(signupController);
				VBox root = loader.load();
				
				signupController.showStage(root);
				
				message.setText("");
				name.clear();
				password.clear();
				
				stage.close();
			} catch (IOException e) {
				message.setText(e.getMessage());
			}});
	}
	
	public void showStage(Pane root) {
		Scene scene = new Scene(root, 500, 300);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Welcome");
		stage.show();
	}
}


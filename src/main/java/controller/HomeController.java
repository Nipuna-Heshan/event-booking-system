package controller;

import javafx.animation.Transition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Cart;
import model.CartItem;
import model.Event;
import model.Model;
import util.BookingUtils;
import util.TransitionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class HomeController {
	private Model model;
	private Cart cart = new Cart();
	private Stage stage;
	private Stage parentStage;
	private List<Event> events;
	private TableView<Event> table = new TableView<>();
	private ObservableList<Event> allEvents;


	@FXML
	private MenuItem viewProfile; // Corresponds to the Menu item "viewProfile" in HomeView.fxml
	@FXML
	private Label welcomeLabel;
	@FXML
	private VBox vBox;

	@FXML private VBox homeRoot;
	@FXML private AnchorPane contentArea;
	@FXML private MenuItem viewEvents;
	@FXML private MenuItem viewCartMenuItem;
	@FXML private MenuItem viewOrderHistory;
	@FXML private MenuItem logoutMenuItem;


	public HomeController(Stage parentStage, Model model) {
		this.stage = new Stage();
		this.parentStage = parentStage;
		this.model = model;
	}

	// Add your code to complete the functionality of the program
	@FXML
	public void initialize() throws IOException {

		TransitionUtils.vBoxHomeTransition(vBox);

		if (model.getCurrentUser() != null) {
			welcomeLabel.setText("Welcome, " + model.getCurrentUser().getPreferredName() + "!");
		}

		viewProfile.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ViewUserProfileView.fxml"));
				Stage viewStage = new Stage();
				ViewUserProfileController viewController = new ViewUserProfileController(model, this::welcomeRefresh, viewStage);
				loader.setController(viewController);
				VBox root = loader.load();
				ViewUserProfileController.showStage(root);
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, ex.getMessage());
			}
		});

		showEventTable();
		viewCartMenuItem.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartView.fxml"));
				Stage cartStage = new Stage();
				CartViewController cartController = new CartViewController(cart, model, cartStage, this::refreshTable);
				loader.setController(cartController);
				Pane root = loader.load();
				cartStage.setTitle("Your Cart");
				cartStage.setScene(new Scene(root));
				cartStage.show();
			} catch (IOException ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, ex.getMessage());
			}
		});
		try{
			List<CartItem> cartItems = model.getUserCartDao().getUserCart(model.getCurrentUser().getUsername(), model);
			cart.setItems(cartItems);
		}catch (SQLException e){
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}

		logoutMenuItem.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
				LoginController loginController = new LoginController(parentStage, model);
				loader.setController(loginController);
				GridPane loginRoot = loader.load();
				loginController.showStage(loginRoot);
				stage.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, ex.getMessage());
			}
		});


		viewOrderHistory.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OrderHistoryView.fxml"));
				Stage historyStage = new Stage();
				OrderHistoryController orderHistoryController = new OrderHistoryController(model, historyStage);
				loader.setController(orderHistoryController);
				Pane root = loader.load();
				historyStage.setTitle("Order History");
				historyStage.setScene(new Scene(root));
				historyStage.initModality(Modality.APPLICATION_MODAL);
				historyStage.show();
			} catch (IOException ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, ex.getMessage());
			}
		});


	}

	public void showStage(VBox root) {
		Scene scene = new Scene(root, 800,  800);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Home");
		stage.show();
	}

	private void welcomeRefresh(){
		if (model.getCurrentUser() != null){
			welcomeLabel.setText("Welcome, " + model.getCurrentUser().getPreferredName() + "!");
		}
	}

	private void showEventTable() {
		table.setRowFactory(tv -> {
			TableRow<Event> row = new TableRow<>();
			row.setPrefHeight(50); // Set row height to 40 pixels
			return row;
		});

		TableColumn<Event, String> titleCol = new TableColumn<>("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

		TableColumn<Event, String> locationCol = new TableColumn<>("Location");
		locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

		TableColumn<Event, String> dayCol = new TableColumn<>("Day");
		dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));

		TableColumn<Event, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableColumn<Event, Integer> soldCol = new TableColumn<>("Sold");
		soldCol.setCellValueFactory(new PropertyValueFactory<>("soldTickets"));

		TableColumn<Event, Integer> totalCol = new TableColumn<>("Total");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalTickets"));

		TableColumn<Event, Integer> remainCol = new TableColumn<>("Remaining");
		remainCol.setCellValueFactory(cellData ->
				new ReadOnlyObjectWrapper<>(cellData.getValue().getRemainingTickets()));

		TableColumn<Event, Void> addToCartCol = new TableColumn<>("Action");
		addToCartCol.setCellFactory(col -> new TableCell<>() {
			private final Button button = new Button("Add to Cart");



			{
				TransitionUtils.buttonTransition(button);
				button.setOnAction(e -> {
					Event selectedEvent = getTableView().getItems().get(getIndex());
					showAddToCartModal(selectedEvent);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Event event = getTableView().getItems().get(getIndex());
					if (BookingUtils.canBook(event.getDay())) {
						setGraphic(button);
                        button.setDisable(event.getRemainingTickets() == 0);
					} else {
						setGraphic(new Label("Not Available"));
					}
				}
			}
		});

		table.getColumns().addAll(titleCol, locationCol, dayCol, priceCol, soldCol, totalCol, remainCol, addToCartCol);
		allEvents = FXCollections.observableArrayList();
		try {
			events = model.getEventDao().getAllEvents();
			for (Event event: events){
				if (event.getEnabled()){
					allEvents.add(event);
				}
			}
			table.setItems(allEvents);
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, ex.getMessage());
		}

		contentArea.getChildren().clear();
		contentArea.getChildren().add(table);
		AnchorPane.setTopAnchor(table, 0.0);
		AnchorPane.setBottomAnchor(table, 0.0);
		AnchorPane.setLeftAnchor(table, 0.0);
		AnchorPane.setRightAnchor(table, 0.0);

	}

	private void refreshTable(){
		table.refresh();
	}

	private void showAddToCartModal(Event event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddToCartView.fxml"));
			Stage modalStage = new Stage();
			AddToCartController controller = new AddToCartController(event, model, cart, modalStage);
			loader.setController(controller);
			Pane root = loader.load();

			modalStage.initModality(Modality.APPLICATION_MODAL);
			modalStage.setScene(new Scene(root));
			modalStage.setTitle("Add to Cart");
			modalStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}
	}

	private void showAlert(Alert.AlertType type, String msg) {
		Alert alert = new Alert(type);
		alert.setTitle("Cart");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}

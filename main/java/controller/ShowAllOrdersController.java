package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Model;
import model.UserEvent;
import util.TransitionUtils;

import java.sql.SQLException;
import java.util.List;

public class ShowAllOrdersController {
    @FXML
    private TableView<UserEvent> ordersTable;
    @FXML
    private TableColumn<UserEvent, String> usernameCol;
    @FXML
    private TableColumn<UserEvent, String> titleCol;
    @FXML
    private TableColumn<UserEvent, String> locationCol;
    @FXML
    private TableColumn<UserEvent, String> dayCol;
    @FXML
    private TableColumn<UserEvent, Integer> quantityCol;
    @FXML
    private TableColumn<UserEvent, String> timestampCol;
    @FXML
    private TableColumn<UserEvent, Double> totalCol;
    @FXML
    private VBox vBox;

    private final Model model;
    private final Stage stage;

    public ShowAllOrdersController(Model model, Stage stage) {
        this.model = model;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        TransitionUtils.vBoxTransition(vBox);

        ordersTable.setRowFactory(tv -> {
            TableRow<UserEvent> row = new TableRow<>();
            row.setPrefHeight(50); // Set row height to 40 pixels
            return row;
        });

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadOrders();
    }

    private void loadOrders() {
        try {
            List<UserEvent> orders = model.getUserEventDao().getAllUserEvents();
            ObservableList<UserEvent> observableOrders = FXCollections.observableArrayList(orders);
            ordersTable.setItems(observableOrders);
        } catch (SQLException e) {
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

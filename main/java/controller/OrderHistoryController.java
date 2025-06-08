package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import util.ExportToFile;
import util.GenerateOrders;
import util.TransitionUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TransferQueue;

public class OrderHistoryController {
    @FXML
    private TableView<OrderItem> orderTable;

    @FXML
    private TableColumn<OrderItem, String> idColumn;

    @FXML
    private TableColumn<OrderItem, String> dateTimeColumn;

    @FXML
    private TableColumn<OrderItem, String> summaryColumn;

    @FXML
    private TableColumn<OrderItem, Double> totalColumn;

    @FXML
    private Button exportButton;

    @FXML
    private VBox vBox;

    private Model model;
    private Stage stage;

    public OrderHistoryController(Model model, Stage stage) {
        this.model = model;
        this.stage = stage;
    }

    @FXML
    public void initialize() throws IOException{
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(exportButton);

        orderTable.setRowFactory(tv -> {
            TableRow<OrderItem> row = new TableRow<>();
            row.setPrefHeight(50); // Set row height to 40 pixels
            return row;
        });

        idColumn.setCellValueFactory(cellData -> cellData.getValue().orderNoProperty());
        dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().nowProperty());
        summaryColumn.setCellValueFactory(cellData -> cellData.getValue().summaryProperty());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        exportButton.setOnAction(e -> ExportToFile.exportToFile(orderTable.getItems(), stage));
        loadOrders();
    }

    private void loadOrders() {
        try {
            List<UserEvent> userOrder = model.getUserEventDao().getEventsByUsername(model.getCurrentUser().getUsername());
            List<OrderItem> orders = GenerateOrders.generateOrder(userOrder);
            orderTable.getItems().setAll(orders);
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
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

package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Model;
import model.OrderItem;
import model.User;
import model.UserEvent;
import util.ExportToFile;
import util.GenerateOrders;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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

    private Model model;
    private Stage stage;

    public OrderHistoryController(Model model, Stage stage) {
        this.model = model;
        this.stage = stage;
    }

    @FXML
    public void initialize() throws IOException{
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
            e.printStackTrace();
        }
    }
}

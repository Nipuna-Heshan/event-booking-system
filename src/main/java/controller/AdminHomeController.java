package controller;

import controller.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Event;
import model.Model;
import util.TransitionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AdminHomeController {
    @FXML
    private Label welcomeLabel;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> dayColumn;
    @FXML private TableColumn<Event, Double> priceColumn;
    @FXML private TableColumn<Event, Integer> capacityColumn;
    @FXML private TableColumn<Event, Integer> soldColumn;
    @FXML private TableColumn<Event, Boolean> disableColumn;

    @FXML private MenuItem addEventMenu;
    @FXML private MenuItem logoutMenu;
    @FXML private MenuItem viewAllOrdersMenu;
    @FXML private AnchorPane contentArea;
    @FXML private VBox vBox;

    private Stage stage;
    private Stage parentStage;
    private Model model;
    private ObservableList<Event> allEvents = FXCollections.observableArrayList();

    public AdminHomeController(Stage parentStage, Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        TransitionUtils.vBoxHomeTransition(vBox);


        welcomeLabel.setText("Welcome, Admin!");
        setupTable();
        loadEvents();

        logoutMenu.setOnAction(e -> logout());
        addEventMenu.setOnAction(e -> openAddEventModal());
        viewAllOrdersMenu.setOnAction(e -> openAllOrdersView());
    }

    private void setupTable() {
        eventTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                TableRow<Event> row = new TableRow<>();
                row.setPrefHeight(50);
                if (event == null || empty) {
                    setStyle("");
                } else if (!event.getEnabled()) {
                    setStyle("-fx-background-color: #f2f2f2; -fx-text-fill: gray;");
                } else {
                    setStyle("");
                }
            }
        });

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        soldColumn.setCellValueFactory(new PropertyValueFactory<>("soldTickets"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("totalTickets"));

        disableColumn.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    boolean newStatus = !event.getEnabled();  // toggle status
                    event.setEnabled(newStatus);             // update in memory

                    try {
                        model.getEventDao().updateEventEnabledStatus(event.getId(), newStatus);
                        refreshTable();// update DB
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // optionally show alert to user
                    }

                    checkBox.setSelected(newStatus); // update checkbox UI
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Event event = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(event.getEnabled());
                    setGraphic(checkBox);
                }
            }
        });

        TableColumn<Event, Void> updateCol = new TableColumn<>("Update");
        updateCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");

            {
                TransitionUtils.buttonTransition(updateBtn);
                updateBtn.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    openUpdateEventModal(event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Event event = getTableView().getItems().get(getIndex());
                    updateBtn.setDisable(!event.getEnabled());
                    setGraphic(updateBtn);
                }
            }
        });

        TableColumn<Event, Void> deleteCol = new TableColumn<>("Delete");

        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                TransitionUtils.buttonTransition(deleteBtn);
                deleteBtn.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());

                    try {
                        boolean hasBookings = model.getUserEventDao()
                                .hasBookingsForEvent(event.getTitle(), event.getLocation(), event.getDay());

                        if (hasBookings) {
                            // Show confirmation alert
                            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmAlert.setTitle("Confirm Deletion");
                            confirmAlert.setHeaderText("This event has user bookings.");
                            confirmAlert.setContentText("Are you sure you want to delete this event?");

                            ButtonType confirmButton = new ButtonType("Delete");
                            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            confirmAlert.getButtonTypes().setAll(confirmButton, cancelButton);

                            confirmAlert.showAndWait().ifPresent(response -> {
                                if (response == confirmButton) {
                                    deleteEvent(event);
                                }
                            });
                        } else {
                            // No bookings — delete directly
                            deleteEvent(event);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error: Failed to check bookings or delete event.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Event event = getTableView().getItems().get(getIndex());
                    deleteBtn.setDisable(!event.getEnabled());
                    setGraphic(deleteBtn);
                }
            }

            private void deleteEvent(Event event) {
                try {
                    model.getEventDao().deleteEvent(event.getId());
                    refreshTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error: Failed to delete event.");
                }
            }
        });

        eventTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getLocation().isEmpty()) {
                    setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
                } else if (!item.getEnabled()) {
                    setStyle("-fx-background-color: #e8e8e8; -fx-text-fill: gray;");
                } else {
                    setStyle("");
                }
            }
        });


        eventTable.getColumns().addAll(updateCol, deleteCol);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(eventTable);
        AnchorPane.setTopAnchor(eventTable, 0.0);
        AnchorPane.setBottomAnchor(eventTable, 0.0);
        AnchorPane.setLeftAnchor(eventTable, 0.0);
        AnchorPane.setRightAnchor(eventTable, 0.0);

    }

    private void loadEvents() {
       refreshTable();
    }

    private void refreshTable(){
        try {
            List<Event> newEvents = model.getEventDao().getAllEvents();
            allEvents.setAll(newEvents);
            eventTable.setItems(allEvents);
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void openAddEventModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddEventView.fxml"));
            Stage modalStage = new Stage();
            AddEventController addEventController = new AddEventController(model, this::refreshTable, modalStage);
            loader.setController(addEventController);
            VBox root = loader.load();
            modalStage.setTitle("Add New Event");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void openUpdateEventModal(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateEventView.fxml"));
            Stage modalStage = new Stage();
            UpdateEventController updateController = new UpdateEventController(model, event, this::refreshTable, modalStage);
            loader.setController(updateController);
            VBox root = loader.load();
            modalStage.setTitle("Update Event");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }


    private void openAllOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShowAllOrdersView.fxml"));
            Stage orderStage = new Stage();
            ShowAllOrdersController showAllOrdersController = new ShowAllOrdersController(model, orderStage);
            loader.setController(showAllOrdersController);
            VBox root = loader.load();
            orderStage.setTitle("All User Orders");
            orderStage.setScene(new Scene(root));
            orderStage.initModality(Modality.APPLICATION_MODAL);
            orderStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            LoginController loginController = new LoginController(parentStage, model);
            loader.setController(loginController);
            GridPane loginRoot = loader.load();
            loginController.showStage(loginRoot);
            stage.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root, 900, 800);
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.setResizable(false);
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

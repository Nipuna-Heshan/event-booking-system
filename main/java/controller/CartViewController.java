package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import util.BookingUtils;
import util.TransitionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartViewController {
    @FXML
    private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> titleCol;
    @FXML private TableColumn<CartItem, String> dayCol;
    @FXML private TableColumn<CartItem, Integer> qtyCol;
    @FXML private TableColumn<CartItem, Double> subtotalCol;
    @FXML private TableColumn<CartItem, Void> actionCol;
    @FXML private TableColumn<CartItem, Void> updateCol;

    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;
    @FXML private Button closeButton;
    @FXML private VBox vBox;

    private Cart cart;
    private Stage stage;
    private final Model model;
    private List<Event> allEvents = new ArrayList<>();
    private ObservableList<Event> observableEvents;
    private List<Event> originalEvents;
    private Runnable onCartCheckout;

    public CartViewController(Cart cart, Model model, Stage stage, Runnable onCartCheckout) {
        this.cart = cart;
        this.model = model;
        this.stage = stage;
        this.onCartCheckout = onCartCheckout;
    }
    @FXML
    public void initialize() {
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(checkoutButton);
        TransitionUtils.buttonTransition(closeButton);

        cartTable.setRowFactory(tv -> {
            TableRow<CartItem> row = new TableRow<>();
            row.setPrefHeight(50); // Set row height to 40 pixels
            return row;
        });

        titleCol.setCellValueFactory(cell -> cell.getValue().getEvent().titleProperty());
        dayCol.setCellValueFactory(cell -> cell.getValue().getEvent().dayProperty());
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subtotalCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getSubtotal()));

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Remove");

            {
                TransitionUtils.buttonTransition(deleteBtn);
                deleteBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cart.removeItem(item.getEvent());
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        updateCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");

            {
                TransitionUtils.buttonTransition(updateBtn);
                updateBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    openUpdateModal(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateBtn);
            }
        });

        cartTable.getItems().setAll(cart.getItems());
        refreshTable();
        if (cart.getItems().isEmpty()){
            checkoutButton.setDisable(true);
        }
        checkoutButton.setOnAction(e -> {
            try {
                originalEvents = model.getEventDao().getAllEvents();
                handleCheckout();
                for (int i = 0; i < originalEvents.size(); i++) {
                    Event orig = originalEvents.get(i);
                    for (Event updated : allEvents) {
                        if (updated.getId() == orig.getId()) {
                            originalEvents.set(i, updated);
                            break;
                        }
                    }
                }
                model.getEventDao().saveEvents(originalEvents);
                model.getUserCartDao().removeUserCart(model.getCurrentUser().getUsername());
                onCartCheckout.run();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        closeButton.setOnAction(e -> stage.close());
    }

    private void refreshTable() {
        cartTable.getItems().setAll(cart.getItems());
        totalLabel.setText("Total: $" + String.format("%.2f", cart.getTotalPrice()));
    }

    private void handleCheckout() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmation Required");
        dialog.setHeaderText("Enter 6-digit confirmation code");
        dialog.setContentText("Code:");

        String code = dialog.showAndWait().orElse("");
        if (!isValidConfirmationCode(code)) {
            showAlert(Alert.AlertType.ERROR, "Invalid code. Please enter a valid code!");
            return;
        }
        try {
            for (CartItem item : cart.getItems()) {
                Event event = item.getEvent();
                allEvents.add(event);
                int qty = item.getQuantity();
                if (!BookingUtils.canBook(event.getDay()) || !event.getEnabled()){
                    showAlert(Alert.AlertType.ERROR, event.getTitle() + " - " + event.getLocation() + " - " + event.getDay() +  " is unavailable for bookings!");
                    return;
                }

                // Check availability
                int available = event.getRemainingTickets() - cart.getQuantityInCart(event) + qty;
                if (qty > available) {
                    showAlert(Alert.AlertType.ERROR, "Not enough available tickets for " + event.getTitle() + " " + event.getDay());
                    return;
                }

                // Update sold tickets (in memory)
                event.setSoldTickets(event.getSoldTickets() + cart.getQuantityInCart(event));
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                double total = item.getQuantity() * event.getPrice();

                // Save to user_events
                UserEvent booking = new UserEvent(
                        model.getCurrentUser().getUsername(),
                        item.getEvent().getTitle(),
                        item.getEvent().getLocation(),
                        item.getEvent().getDay(),
                        item.getQuantity(),
                        now,
                        total
                );
                model.getUserEventDao().addUserEvent(booking);
                // Prompt for confirmation code

            }

            // Clear cart and update view
            cart.clear();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Checkout complete!");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openUpdateModal(CartItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateQuantityView.fxml"));
            Stage modal = new Stage();
            modal.setTitle("Update Booking");
            modal.initOwner(stage);
            UpdateQuantityController controller = new UpdateQuantityController(item, cart, modal, this::refreshTable);
            loader.setController(controller);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(loader.load()));
            modal.showAndWait();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean isValidConfirmationCode(String code) {
        return code.matches("\\d{6}");
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}

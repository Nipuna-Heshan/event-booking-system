package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Cart;
import model.CartItem;
import model.Event;
import model.Model;
import util.TransitionUtils;

import java.sql.SQLException;

public class AddToCartController {
    @FXML
    private Label eventNameLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label availabilityLabel;
    @FXML private Button addToCartButton;
    @FXML private VBox vBox;

    private Event event;
    private Cart cart;
    private Stage stage;
    private Model model;

    public AddToCartController(Event event, Model model, Cart cart, Stage stage) {
        this.event = event;
        this.model = model;
        this.cart = cart;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(addToCartButton);
        TransitionUtils.spinnerTransition(quantitySpinner);

        eventNameLabel.setText(event.getTitle());
        int available = event.getRemainingTickets() - cart.getQuantityInCart(event);
        if (available == 0) {
            addToCartButton.setDisable(true);
            availabilityLabel.setText("Not enough tickets are available!");
        }
        available = Math.max(available, 0);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, available);
        quantitySpinner.setValueFactory(valueFactory);
        valueFactory.setValue(0);
        availabilityLabel.setText("Tickets available: " + available);

        addToCartButton.setOnAction(e -> handleAddToCart());
    }

    public void handleAddToCart() {
        int qty = quantitySpinner.getValue();
        if (qty > event.getRemainingTickets() - cart.getQuantityInCart(event)) {
            showAlert(Alert.AlertType.ERROR, "Not enough tickets available.");
            return;
        }

        cart.addItem(event, qty);
        try {
            for (CartItem item : model.getUserCartDao().getUserCart(model.getCurrentUser().getUsername(), model)) {
                if (item.getEvent().getId() == event.getId()) {
                    model.getUserCartDao().updateQty(item.getEvent().getTitle(), item.getEvent().getLocation(), item.getEvent().getDay(), item.getQuantity() + qty);
                    stage.close();
                    return;
                }
            }
            model.getUserCartDao().addToCart(model.getCurrentUser().getUsername(), event, qty);
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

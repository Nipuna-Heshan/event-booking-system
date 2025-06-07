package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Cart;
import model.CartItem;
import model.Event;
import util.TransitionUtils;

public class UpdateQuantityController {
    @FXML private Label eventLabel;
    @FXML private Label availableLabel;
    @FXML private TextField quantityField;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private VBox vBox;

    private final CartItem item;
    private final Cart cart;
    private final Runnable onUpdate;
    private final Stage stage;


    public UpdateQuantityController(CartItem item, Cart cart, Stage stage, Runnable onUpdate) {
        this.item = item;
        this.cart = cart;
        this.stage = stage;
        this.onUpdate = onUpdate;

    }

    @FXML
    public void initialize() {
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(updateButton);
        TransitionUtils.buttonTransition(cancelButton);


        Event event = item.getEvent();
        int maxAvailable = event.getRemainingTickets() - item.getQuantity();

        eventLabel.setText("Event: " + event.getTitle());
        availableLabel.setText("Available: " + (maxAvailable<0?"Not enough tickets are available":maxAvailable));
        if(maxAvailable < 0){
            quantityField.setDisable(true);
            updateButton.setDisable(true);
        }else quantityField.setText(String.valueOf(1));

        updateButton.setOnAction(e -> {
            try {
                int updateQty = Integer.parseInt(quantityField.getText());
                int newQty = Integer.parseInt(quantityField.getText()) + item.getQuantity();
                if (updateQty <= 0 || updateQty > maxAvailable) {
                    showAlert("Only " + maxAvailable + " tickets are available!");
                    return;
                }
                item.setQuantity(newQty);
                onUpdate.run();
                stage.close();
            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid number.");
            }
        });

        cancelButton.setOnAction(e -> stage.close());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}

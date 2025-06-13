package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Model;
import util.TransitionUtils;

public class UpdateEventController {
    @FXML
    private TextField locationField;
    @FXML private ComboBox<String> dayCombo;
    @FXML private TextField priceField;
    @FXML private TextField totalTicketsField;
    @FXML private Button updateBtn;
    @FXML private Button cancelBtn;
    @FXML private VBox vBox;
    @FXML private Label title;

    private final Model model;
    private final Event event;
    private final Runnable refreshCallback;
    private final Stage modalStage;

    public UpdateEventController(Model model, Event event, Runnable refreshCallback, Stage modalStage) {
        this.model = model;
        this.event = event;
        this.refreshCallback = refreshCallback;
        this.modalStage = modalStage;
    }

    @FXML
    public void initialize() {
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.buttonTransition(updateBtn);
        TransitionUtils.buttonTransition(cancelBtn);
        TransitionUtils.textFieldTransition(locationField);
        TransitionUtils.textFieldTransition(priceField);
        TransitionUtils.textFieldTransition(totalTicketsField);
        TransitionUtils.comboBoxTransition(dayCombo);

        locationField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN, ENTER -> priceField.requestFocus();
            }
        });

        priceField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> totalTicketsField.requestFocus();
                case UP -> locationField.requestFocus();
            }
        });

        totalTicketsField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> updateBtn.requestFocus();
                case UP -> priceField.requestFocus();
            }
        });

        updateBtn.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> updateBtn.fire();
                case UP -> totalTicketsField.requestFocus();
                case RIGHT -> cancelBtn.requestFocus();
            }
        });

        cancelBtn.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> cancelBtn.fire();
                case UP -> totalTicketsField.requestFocus();
                case LEFT -> updateBtn.requestFocus();
            }
        });


        locationField.setText(event.getLocation());
        dayCombo.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        dayCombo.setValue(event.getDay());
        priceField.setText(String.valueOf(event.getPrice()));
        totalTicketsField.setText(String.valueOf(event.getTotalTickets()));
        title.setText(event.getTitle());

        updateBtn.setOnAction(e -> {
            handleUpdate();
        });
        cancelBtn.setOnAction(e -> handleCancel());
    }

    @FXML
    private void handleUpdate() {
        try {
            event.setLocation(locationField.getText());
            event.setDay(dayCombo.getValue());
            event.setPrice(Double.parseDouble(priceField.getText()));
            event.setTotalTickets(Integer.parseInt(totalTicketsField.getText()));
            //Duplicate Event Validation for Updating
            Event existing = model.getEventDao().findEvent(event.getTitle(), event.getLocation(), event.getDay());
            if (existing != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate event exists. Cannot add.");
                return;
            }

            model.getEventDao().updateEventDetails(event);
            refreshCallback.run();
            modalStage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        modalStage.close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}

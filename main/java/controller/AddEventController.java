package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Model;
import util.TransitionUtils;

public class AddEventController {
    @FXML
    private TextField titleField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> dayCombo;
    @FXML private TextField priceField;
    @FXML private TextField totalTicketsField;
    @FXML private TextField soldTicketsField;
    @FXML private CheckBox enabledCheckBox;
    @FXML private Label errorLabel;
    @FXML private Button addEvent;
    @FXML private Button cancel;
    @FXML private VBox vBox;

    private Model model;
    private Runnable onAddEvent;
    private Stage stage;

    public AddEventController(Model model, Runnable onAddEvent, Stage stage) {
        this.model = model;
        this.onAddEvent = onAddEvent;
        this.stage = stage;
    }

    public void initialize(){
        TransitionUtils.vBoxTransition(vBox);
        TransitionUtils.textFieldTransition(titleField);
        TransitionUtils.textFieldTransition(locationField);
        TransitionUtils.textFieldTransition(priceField);
        TransitionUtils.textFieldTransition(totalTicketsField);
        TransitionUtils.textFieldTransition(soldTicketsField);
        TransitionUtils.comboBoxTransition(dayCombo);
        TransitionUtils.buttonTransition(addEvent);
        TransitionUtils.buttonTransition(cancel);


        titleField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN, ENTER -> locationField.requestFocus();
            }
        });

        locationField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> priceField.requestFocus();
                case UP -> titleField.requestFocus();
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
                case ENTER, DOWN -> soldTicketsField.requestFocus();
                case UP -> priceField.requestFocus();
            }
        });

        soldTicketsField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> addEvent.requestFocus();
                case UP -> totalTicketsField.requestFocus();
            }
        });


        addEvent.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> addEvent.fire();
                case UP -> soldTicketsField.requestFocus();
                case RIGHT -> cancel.requestFocus();
            }
        });

        cancel.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> cancel.fire();
                case UP -> soldTicketsField.requestFocus();
                case LEFT -> addEvent.requestFocus();
            }
        });

        dayCombo.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        dayCombo.setValue("Mon");
        addEvent.setOnAction(e -> {
            handleAddEvent();
            onAddEvent.run();
        });
        cancel.setOnAction(e -> handleCancel());
    }

    public void handleAddEvent() {
        String title = titleField.getText().trim();
        String location = locationField.getText().trim();
        String day = dayCombo.getValue();
        String priceText = priceField.getText().trim();
        String totalText = totalTicketsField.getText().trim();
        String soldText = soldTicketsField.getText().trim();
        boolean isEnabled = enabledCheckBox.isSelected();

        if (title.isEmpty() || location.isEmpty() || day.isEmpty() || priceText.isEmpty() || totalText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int totalTickets = Integer.parseInt(totalText);
            int soldTickets = soldText.isEmpty() ? 0 : Integer.parseInt(soldText);

            if (soldTickets > totalTickets) {
                showAlert(Alert.AlertType.ERROR, "Sold tickets cannot exceed total tickets.");
                return;
            }

            Event newEvent = new Event(0, title, location, day, price, soldTickets, totalTickets, isEnabled);
            Event existing = model.getEventDao().findEvent(newEvent.getTitle(), newEvent.getLocation(), newEvent.getDay());
            if (existing != null) {
                // Duplicate found, return -1 or throw an exception
                showAlert(Alert.AlertType.ERROR, "Duplicate event exists. Cannot add.");
                return;
            }
            model.getEventDao().addEvent(newEvent);
            stage.close();



        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter valid numbers for price, total tickets, and sold tickets.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    public void handleCancel() {
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

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Event;
import model.Model;

public class UpdateEventController {
    @FXML
    private TextField locationField;
    @FXML private ComboBox<String> dayCombo;
    @FXML private TextField priceField;
    @FXML private TextField totalTicketsField;
    @FXML private Button updateBtn;
    @FXML private Button cancelBtn;

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
        locationField.setText(event.getLocation());
        dayCombo.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        dayCombo.setValue(event.getDay());
        priceField.setText(String.valueOf(event.getPrice()));
        totalTicketsField.setText(String.valueOf(event.getTotalTickets()));

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
            Event existing = model.getEventDao().findEvent(event.getTitle(), event.getLocation(), event.getDay());
            if (existing != null) {
                // Duplicate found, return -1 or throw an exception
                System.out.println("Duplicate event exists. Cannot add.");
                return;
            }

            model.getEventDao().updateEventDetails(event);
            refreshCallback.run();
            modalStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        modalStage.close();
    }
}

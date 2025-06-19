package util;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.OrderItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportToFile {

    public static void exportToFile(List<OrderItem> orders, Stage ownerStage) {
        if (orders.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "There are no orders to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Order History");
        fileChooser.setInitialFileName("order_history.txt");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save dialog
        File file = fileChooser.showSaveDialog(ownerStage);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (OrderItem order : orders) {
                    writer.write("Order ID: " + order.getOrderNo());
                    writer.newLine();
                    writer.write("Date & Time: " + order.getNow());
                    writer.newLine();
                    writer.write("Summary:\n" + order.getSummary());
                    writer.newLine();
                    writer.write(String.format("Total: $%.2f", order.getTotalPrice()));
                    writer.newLine();
                    writer.write("=====================================");
                    writer.newLine();
                }

                showAlert(Alert.AlertType.INFORMATION,"Export Successful,\nOrder history exported to:\n" + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Export Failed\nAn error occurred while exporting:\n" + e.getMessage());
            }
        }
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Cart");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

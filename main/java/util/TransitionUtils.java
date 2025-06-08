package util;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TransitionUtils {
    public static void buttonTransition(Button button){
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
            button.setStyle("-fx-background-color: #FFBB00; -fx-text-fill: #011E26;");
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1);
            st.setToY(1);
            st.play();
            button.setStyle("-fx-background-color: #011E26; -fx-text-fill: #FFBB00;");
        });
    }

    public static void textFieldTransition(TextField textField){
        textField.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), textField);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        textField.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), textField);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public static void passwordFieldTransition(PasswordField passwordField){
        passwordField.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), passwordField);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        passwordField.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), passwordField);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public static void spinnerTransition(Spinner spinner){
        spinner.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), spinner);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        spinner.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), spinner);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public static void comboBoxTransition(ComboBox comboBox){
        comboBox.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), comboBox);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        comboBox.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), comboBox);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public static void gridTransition(GridPane gridPane){
        gridPane.setManaged(false);

        gridPane.setLayoutY(gridPane.getLayoutY());

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(gridPane.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(gridPane.translateYProperty(), 100)),
                new KeyFrame(Duration.millis(700), new KeyValue(gridPane.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(700), new KeyValue(gridPane.translateYProperty(), 0))
        );

        timeline.play();
    }

    public static void vBoxHomeTransition(VBox vBox){
        vBox.setManaged(false);

        vBox.setLayoutY(vBox.getLayoutY());

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.translateYProperty(), 200)),
                new KeyFrame(Duration.millis(2000), new KeyValue(vBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(2000), new KeyValue(vBox.translateYProperty(), 0))
        );

        timeline.play();

    }

    public static void vBoxTransition(VBox vBox){
        vBox.setManaged(false);

        vBox.setLayoutY(vBox.getLayoutY());

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.translateYProperty(), 50)),
                new KeyFrame(Duration.millis(500), new KeyValue(vBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(500), new KeyValue(vBox.translateYProperty(), 0))
        );

        timeline.play();

    }
}

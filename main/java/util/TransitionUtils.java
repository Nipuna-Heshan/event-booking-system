package util;

import javafx.animation.*;
import javafx.scene.control.Button;
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
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public static void gridTransition(GridPane gridPane){
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), gridPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.millis(1000));
        fadeIn.play();

        gridPane.setTranslateY(300); // Start off-screen

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(1900), gridPane);
        slideIn.setToY(0);
        slideIn.setDelay(Duration.millis(1000));
        slideIn.play();
    }

    public static void vBoxTransition(VBox vBox){
        vBox.setManaged(false);

        vBox.setLayoutY(vBox.getLayoutY());

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(vBox.translateYProperty(), 100)),
                new KeyFrame(Duration.millis(1000), new KeyValue(vBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(750), new KeyValue(vBox.translateYProperty(), 0))
        );

        timeline.play();

    }
}

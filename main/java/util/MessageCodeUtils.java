package util;

import javafx.scene.paint.Color;
import javafx.scene.control.Label;

public class MessageCodeUtils {

    public static void showError(Label label, String msg){
        label.setText(msg);
        label.setTextFill(Color.RED);
    }

    public static void showSuccess(Label label, String msg){
        label.setText(msg);
        label.setTextFill(Color.GREEN);
    }
}

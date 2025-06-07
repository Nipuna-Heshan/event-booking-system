package util;

import model.OrderItem;
import model.UserEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GenerateOrders {

    public static List<OrderItem> generateOrder(List<UserEvent> userEvents) {
        List<OrderItem> orderItems = new ArrayList<>();
        int counter = 1;

        for (UserEvent ue : userEvents) {

            // Format order number as 0001, 0002, ...
            String orderNo = String.format("%04d", counter++);

            String now = ue.getTimeStamp();

            // Combine title, location, and day into summary
            String summary = ue.getTitle() + " - " + ue.getLocation() + " on " + ue.getDay() + " - " + ue.getQuantity() + " seats.";

            // Create new OrderItem
            OrderItem item = new OrderItem(orderNo, now, summary, ue.getTotal());

            // Add to list
            orderItems.add(item);
        }

        return orderItems.reversed();
    }

}

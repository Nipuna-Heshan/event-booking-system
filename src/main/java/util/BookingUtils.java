package util;

import java.util.List;

public class BookingUtils {
    private static final List<String> weekDays = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");


    public static boolean canBook(String eventDay) {
        String today = java.time.LocalDate.now().getDayOfWeek().toString().substring(0, 3);
        today = today.substring(0, 1).toUpperCase() + today.substring(1).toLowerCase(); // Convert MONDAY -> Mon
        int todayIndex = weekDays.indexOf(today);
        int eventIndex = weekDays.indexOf(eventDay);
        return eventIndex >= todayIndex;
    }
}

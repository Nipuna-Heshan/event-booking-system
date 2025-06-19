package model;

import java.util.List;

public class GroupedEvents {
    private String title;
    private List<String> options; // e.g., ["Theatre Nova - Mon", "Theatre Nova - Tue"]

    public GroupedEvents(String title, List<String> options) {
        this.title = title;
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getOptionsAsString() {
        return String.join(", ", options);
    }
}

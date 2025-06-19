package model;

import javafx.beans.property.*;

public class Event {
    private IntegerProperty id;
    private StringProperty title;
    private StringProperty location;
    private StringProperty day;
    private DoubleProperty price;
    private IntegerProperty totalTickets;
    private IntegerProperty soldTickets;
    private BooleanProperty isEnabled;

    public Event(int id, String title, String location, String day, double price, int sold, int total, boolean isEnabled) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.day = new SimpleStringProperty(day);
        this.price = new SimpleDoubleProperty(price);
        this.totalTickets = new SimpleIntegerProperty(total);
        this.soldTickets = new SimpleIntegerProperty(sold);
        this.isEnabled = new SimpleBooleanProperty(isEnabled);
    }


    public IntegerProperty id() { return id; }
    public int getId() { return id.get(); }

    public StringProperty title() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public StringProperty location() { return location; }
    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public StringProperty locationProperty() { return location; }

    public StringProperty day() { return day; }
    public String getDay() { return day.get(); }
    public void setDay(String value) { day.set(value); }
    public StringProperty dayProperty() { return day; }

    public DoubleProperty price() { return price; }
    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price = new SimpleDoubleProperty(price); }

    public IntegerProperty totalTickets() { return totalTickets; }
    public int getTotalTickets() { return totalTickets.get(); }
    public void setTotalTickets(int totalTickets) { this.totalTickets = new SimpleIntegerProperty(totalTickets); }

    public IntegerProperty soldTickets() { return soldTickets; }
    public int getSoldTickets() { return soldTickets.get(); }
    public void setSoldTickets(int soldTickets) {
        this.soldTickets = new SimpleIntegerProperty(soldTickets);
    }
    public int getRemainingTickets() { return totalTickets.get() - soldTickets.get(); }

    public BooleanProperty isEnabled() { return isEnabled; }
    public boolean getEnabled() {
        return isEnabled.get();
    }
    public void setEnabled(boolean enabled) {
        isEnabled = new SimpleBooleanProperty(enabled);
    }

}

package model;

import javafx.beans.property.*;

public class UserEvent {
    private IntegerProperty id;
    private StringProperty username;
    private StringProperty title;
    private StringProperty location;
    private StringProperty day;
    private IntegerProperty quantity;
    private StringProperty timeStamp;
    private DoubleProperty total;

    public UserEvent(String username, String title, String location, String day, int quantity, String timeStamp, double total) {
        this.username = new SimpleStringProperty(username);
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.day = new SimpleStringProperty(day);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.timeStamp = new SimpleStringProperty(timeStamp);
        this.total = new SimpleDoubleProperty(total);
    }

    // For loading with ID
    public UserEvent(int id, String username, String title, String location, String day, int quantity, String timeStamp, double total) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.day = new SimpleStringProperty(day);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.timeStamp = new SimpleStringProperty(timeStamp);
        this.total = new SimpleDoubleProperty(total);
    }


    // Property getters for table binding
    public IntegerProperty idProperty() { return id; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty titleProperty() {return title;}
    public StringProperty locationProperty() {return location;}
    public StringProperty dayProperty() {return  day;}
    public IntegerProperty quantityProperty() {return quantity;}
    public StringProperty timeStampProperty() { return timeStamp; }
    public DoubleProperty totalProperty() { return total; }

    // Value getters
    public int getId() { return id.get(); }
    public String getUsername() { return username.get(); }
    public String getTitle() { return title.get(); }
    public String getLocation() { return location.get(); }
    public String getDay() { return  day.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getTimeStamp() { return timeStamp.get(); }
    public double getTotal() { return total.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setUsername(String username) { this.username.set(username); }
    public void setTitle(String title) { this.title.set(title); }
    public void setLocation(String location) { this.location.set(location); }
    public void setDay(String day) { this.day.set(day); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setTimeStamp(String timeStamp) { this.timeStamp.set(timeStamp); }
    public void setTotal(double total) { this.total.set(total); }
}

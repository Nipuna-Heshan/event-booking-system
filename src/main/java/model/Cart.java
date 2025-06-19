package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Event event, int quantity) {
        for (CartItem item : items) {
            if (item.getEvent().getTitle().equals(event.getTitle()) && item.getEvent().getLocation().equals(event.getLocation()) &&
                    item.getEvent().getDay().equals(event.getDay())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(event, quantity));
    }

    public void updateItemQuantity(Event event, int newQuantity) {
        for (CartItem item : items) {
            if (item.getEvent().equals(event)) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }

    public void removeItem(Event event) {
        items.removeIf(item -> item.getEvent().equals(event));
    }

    public int getQuantityInCart(Event event) {
        for (CartItem item : items) {
            if (item.getEvent().getTitle().equals(event.getTitle()) && item.getEvent().getLocation().equals(event.getLocation()) &&
                    item.getEvent().getDay().equals(event.getDay())) {
                return item.getQuantity();
            }

        }
        return 0;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

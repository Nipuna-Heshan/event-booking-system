package dao;

import model.CartItem;
import model.Event;
import model.Model;

import java.sql.SQLException;
import java.util.List;

public interface UserCartDao {
    void setup() throws SQLException;
    void addToCart(String username, Event event, int quantity) throws SQLException;
    void updateQty(String username, int quantity) throws  SQLException;
    List<CartItem> getUserCart(String username, Model model) throws SQLException;
    void removeUserCart(String username) throws SQLException;
}

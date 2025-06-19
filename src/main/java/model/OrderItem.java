package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderItem {
    private StringProperty orderNo;
    private StringProperty now;
    private StringProperty summary;
    private DoubleProperty totalPrice;

    public OrderItem(String orderNo, String now, String summary, double totalPrice){
        this.orderNo = new SimpleStringProperty(orderNo);
        this.now = new SimpleStringProperty(now);
        this.summary = new SimpleStringProperty(summary);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
    }

    public StringProperty orderNoProperty(){
        return orderNo;
    }
    public StringProperty nowProperty(){
        return now;
    }
    public StringProperty summaryProperty(){
        return summary;
    }
    public DoubleProperty totalPriceProperty(){
        return totalPrice;
    }

    public String getOrderNo(){
        return orderNo.get();
    }
    public String getNow(){
        return  now.get();
    }
    public String getSummary(){
        return summary.get();
    }
    public double getTotalPrice(){
        return  totalPrice.get();
    }

}

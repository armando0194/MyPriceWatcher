package cs4330.cs.utep.edu;

import java.util.UUID;

public class Item {
    private long ID;
    private String name;
    private String url;
    private double initialPrice;
    private double currentPrice;
    private double percentageChange;

    public Item(long ID, String name, String url, double initialPrice, double currentPrice) {
        this.name = name;
        this.url = url;
        this.initialPrice = initialPrice;
        this.currentPrice = currentPrice;
        this.percentageChange = Util.calculatePercentageChange(currentPrice, initialPrice);
        this.ID = ID;
    }

    public Item(long ID, String name, String url, double price) {
        this.name = name;
        this.url = url;
        this.initialPrice = price;
        this.currentPrice = price;
        this.percentageChange = 0;
        this.ID = ID;
    }

    public long getID(){
        return ID;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.percentageChange = Util.calculatePercentageChange(currentPrice, initialPrice);
    }
}

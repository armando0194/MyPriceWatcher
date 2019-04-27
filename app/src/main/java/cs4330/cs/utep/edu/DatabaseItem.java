package cs4330.cs.utep.edu;

class DatabaseItem extends Item{
    public DatabaseItem(long ID, String name, String url, double initialPrice, double currentPrice) {
        super(ID, name, url, initialPrice, currentPrice);
    }

    public DatabaseItem(long ID, String name, String url, double price) {
        super(ID, name, url, price);
    }
}

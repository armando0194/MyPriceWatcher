package cs4330.cs.utep.edu;

public interface PriceFinder {
    public double getPrice(String url);
    public void getPrice(String url, boolean isNew, int pos);
}

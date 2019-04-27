package cs4330.cs.utep.edu;

import java.util.Random;

public class RandomPriceFinder implements PriceFinder {
    Random r;
    double rangeMin;
    double rangeMax;

    public RandomPriceFinder(){
        rangeMin = 0;
        rangeMax = 5000;
        this.r = new Random();
    }

    @Override
    public void getPrice(String url, boolean isNew, int pos) {
    }

    /**
     * Generates random number between the min and max range
     * @param url - url of item
     * @return - random number
     */
    @Override
    public double getPrice(String url){
        return Util.round(rangeMin + (rangeMax - rangeMin) * r.nextDouble(), 2);
    }
}

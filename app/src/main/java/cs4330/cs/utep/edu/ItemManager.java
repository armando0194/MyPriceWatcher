package cs4330.cs.utep.edu;

import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class ItemManager {


    private ArrayList<Item> items;
    private ArrayList<Item> filteredItems;

    public Item lastDeletedItem;
    private NetworkPriceFinder priceFinder;
    private int lastDeletedPos;
    public int lastDeletedPosFiltered;


    public ItemManager(){
        this.items = new ArrayList<>();
        this.filteredItems = new ArrayList<>(items);
    }

    public void setPriceFinder(NetworkPriceFinder priceFinder){
        this.priceFinder = priceFinder;
    }

    public Item get(int pos){
        return filteredItems.get(pos);
    }

    public ArrayList<Item> getFilteredItems() {
        return filteredItems;
    }

    public int size() {
        return filteredItems.size();
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
    }

    public Item addItem(Item item){
        items.add(item);
        filteredItems = new ArrayList<>(items);
        return item;
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public void setFilteredItems(ArrayList<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    /**
     * Updates item in the array
     * @param name - new name
     * @param url - new url
     * @param price - new price
     * @param pos - position of the item
     */
    public void updateItem(String name, String url, double price, int pos){
        Item changedItem = filteredItems.get(pos);
        changedItem.setName(name);
        changedItem.setCurrentPrice(price);
        changedItem.setUrl(url);
    }

    /**
     * Updates item in the array
     * @param name - new name
     * @param url - new url
     * @param pos - position of the item
     */
    public void updateItem(String name, String url, double currPrice, double initPrice, int pos){
        Item changedItem = filteredItems.get(pos);
        changedItem.setName(name);
        changedItem.setCurrentPrice(currPrice);
        changedItem.setInitialPrice(initPrice);
        changedItem.setUrl(url);
    }

    /**
     * Updates item in the array
     * @param name - new name
     * @param url - new url
     * @param pos - position of the item
     */
    public void updateItem(String name, String url, int pos){
        Item changedItem = filteredItems.get(pos);
        changedItem.setName(name);
        changedItem.setUrl(url);
    }

    /**
     * Deletes item and saves its value in case the user wants to
     * undo the action
     * @param pos - position of the item
     */
    public void deleteItem(int pos){
        lastDeletedItem = filteredItems.get(pos);
        lastDeletedPosFiltered = pos;
        lastDeletedPos = findItemIndexById(lastDeletedItem.getID());

        filteredItems.remove(pos);
        items.remove(lastDeletedPos);
    }


    /**
     * Adds last removed item back
     */
    public int undoDelete(){
        filteredItems.add(lastDeletedPosFiltered, lastDeletedItem);
        items.add(lastDeletedPos, lastDeletedItem);
        return lastDeletedPosFiltered;
    }


    public void clearFilters(){
        filteredItems = new ArrayList<>(items);;
    }

    public int findItemIndexById(long ID){
        for (int i = 0; i<= items.size(); i++){
            if (items.get(i).getID() == ID){
                return i;
            }
        }
        return -1;
    }
}

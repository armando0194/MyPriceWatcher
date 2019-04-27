package cs4330.cs.utep.edu;

import android.content.Context;

import java.util.ArrayList;

public class DatabaseItemManager extends ItemManager {
    private ItemDatabaseHelper itemDatabaseHelper;
    private WatchlistActivity ctx;

    public DatabaseItemManager(WatchlistActivity ctx){
        super();
        this.itemDatabaseHelper = new ItemDatabaseHelper(ctx);
        this.ctx = ctx;
        super.setItems((ArrayList<Item>)(ArrayList<?>) itemDatabaseHelper.allItems());

    }

    public DatabaseItem addItem(String name, String url, Double price){
        DatabaseItem item = itemDatabaseHelper.addItem(name, url, price);

        if (item != null){
            super.addItem(item);
        }

        return item;
    }

    public void updateItem(String name, String url, double price, int pos){
        DatabaseItem item = (DatabaseItem) get(pos);
        boolean success = itemDatabaseHelper.updateItem(item.getID(), name, url, price, price);

        if (success)
            super.updateItem(name, url, price, pos);

    }

    public void updateItem(String name, String url, int pos){
        DatabaseItem item = (DatabaseItem) get(pos);

        boolean success = itemDatabaseHelper.updateItem(item.getID(), name, url,
                item.getCurrentPrice(), item.getInitialPrice());

        if (success)
            super.updateItem(name, url, pos);

    }

    public void updateItem(double price, int pos){
        DatabaseItem item = (DatabaseItem) get(pos);
        boolean success = itemDatabaseHelper.updateItem(item.getID(), item.getName(), item.getUrl(),
                                                        price, item.getInitialPrice());

        if (success)
            super.updateItem(item.getName(), item.getUrl(), price, pos);

    }

    public void updateItem(double currPrice, double initPrice, int pos){
        DatabaseItem item = (DatabaseItem) get(pos);
        boolean success = itemDatabaseHelper.updateItem(item.getID(), item.getName(), item.getUrl(),
                currPrice, initPrice);

        if (success)
            super.updateItem(item.getName(), item.getUrl(), currPrice, initPrice, pos);

    }

    public void deleteItem(int pos){
        DatabaseItem item = (DatabaseItem) get(pos);
        boolean success = itemDatabaseHelper.deleteItem(item.getID());

        if (success)
            super.deleteItem(pos);
    }

    public int undoDelete(){
        DatabaseItem item = addItem(lastDeletedItem.getName(),
                                    lastDeletedItem.getUrl(),
                                    lastDeletedItem.getCurrentPrice());
        lastDeletedItem = item;
        return lastDeletedPosFiltered;
    }

    /**
     * Uses price finder to update the prices of all the items
     */
    public void updateItemsPrice() {
        ArrayList<Item> items = getFilteredItems();
        for (int i = 0; i < items.size(); i++){
            new NetworkPriceFinder(ctx).getPrice(items.get(i).getUrl(),false, i);
        }
    }

    /**
     * Uses price finder to update the price of specific item
     */
    public void updateLastPriceItem() {
        int pos = getFilteredItems().size()-1;
        Item item = getFilteredItems().get(pos);
        new NetworkPriceFinder(ctx).getPrice(item.getUrl(),true, pos);

    }

}

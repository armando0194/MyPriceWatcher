package cs4330.cs.utep.edu;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ItemAdapter extends RecyclerViewEmptySupport.Adapter<ItemAdapter.ViewHolder>
        implements Filterable {

    public enum FilterType {
        NAME, STORE;
    };

    DatabaseItemManager itemManager;
    WatchlistActivity ctx;
    FilterType filterType;

    public ItemAdapter(WatchlistActivity ctx){
        this.itemManager = new DatabaseItemManager(ctx);
        this.ctx = ctx;
        this.filterType = FilterType.NAME;
    }

    public Context getContext(){
        return ctx;
    }

    /***
     * inflate the item layout and create the holder,
     */
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    /**
     * Populate row with data from the model
     * @param viewHolder - row view
     * @param position   - row number
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Item item = itemManager.get(position);
        // Set item views based on your views and data model
        viewHolder.nameTextView.setText(String.format(" %s", item.getName()));
        viewHolder.initialPriceTextView.setText(String.format(Locale.US," %.2f$", item.getInitialPrice()));
        viewHolder.currentPriceTextView.setText(String.format(Locale.US, " %.2f$", item.getCurrentPrice()));
        viewHolder.percentageChangeTextView.setText(String.format(Locale.US, " %.2f%%", item.getPercentageChange()));

        viewHolder.itemView.setOnClickListener(v -> {
            openWeb(item.getUrl());
        });
    }

    /**
     * @return - Returns the number of items
     */
    @Override
    public int getItemCount() {
        return itemManager.size();
    }

    /**
     * Adds an item to the recycler view

     */
    public void addItem(String name, String url, Double price){
        itemManager.addItem(name, url, price);
        notifyItemInserted(itemManager.size()-1);
    }

    /**
     * It opens a dialog to update an item
     * @param pos - position of the item in the array
     */
    public void updateItem(int pos){
        Item changedItem = itemManager.get(pos);
        ctx.showDialog(changedItem.getName(), changedItem.getUrl(),
                changedItem.getInitialPrice(), pos);
        notifyItemChanged(pos);
    }

    /**
     * Updates item in the array
     * @param name - new name
     * @param url - new url
     * @param price - new price
     * @param pos - position of the item
     */
    public void updateItem(String name, String url, double price, int pos){
        itemManager.updateItem(name, url, price, pos);
        notifyItemChanged(pos);
    }

    public void updateItem(String name, String url, int pos){
        itemManager.updateItem(name, url, pos);
        notifyItemChanged(pos);
    }

    public void updateItem(double price, int pos){
        itemManager.updateItem(price, pos);
        notifyItemChanged(pos);
    }

    public void updateItem(double currPrice, double initPrice, int pos){
        itemManager.updateItem(currPrice, initPrice, pos);
        notifyItemChanged(pos);
    }

    /**
     * Uses price finder to update the prices of all the items
     */
    public void updateItemsPrice() {
        itemManager.updateItemsPrice();
        notifyDataSetChanged();
    }

    public void updateLastPriceItem() {
        itemManager.updateLastPriceItem();
        notifyDataSetChanged();
    }

    /**
     * Deletes item and saves its value in case the user wants to
     * undo the action
     * @param pos - position of the item
     */
    public void deleteItem(int pos){
        itemManager.deleteItem(pos);
        notifyItemRemoved(pos);
        showUndoSnackbar();
    }

    /**
     * Shows snackbar to user in case user wants to undo a delete
     */
    private void showUndoSnackbar() {
        View view = ctx.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_undo_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    /**
     * Adds last removed item back
     */
    private void undoDelete(){
        notifyItemInserted(itemManager.undoDelete());
    }

    /**
     * Opens url of the item at an specific position
     * @param pos - position of the item
     */
    public void openWeb(int pos){
        openWeb(itemManager.get(pos).getUrl());
    }

    /**
     * Opens a url in a custom tab
     * @param url - Url
     */
    public void openWeb(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        Bitmap icon = Util.getBitmapFromVectorDrawable(ctx, R.drawable.ic_share_white_24dp);

        PendingIntent menuItemPendingIntent = createPendingIntent();
        builder.setToolbarColor(ctx.getResources().getColor(R.color.colorPrimary));
        builder.setActionButton(icon, "adds item", menuItemPendingIntent);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(ctx, Uri.parse(url));
    }

    /**
     * Creates pending intent taht register to the action boradcast reciever
     * @return
     */
    private PendingIntent createPendingIntent() {
        Intent actionIntent = new Intent(ctx, ActionBroadcastReceiver.class);
        return PendingIntent.getBroadcast(ctx, -1, actionIntent, 0);
    }

    /**
     * Returns a filter to filter the arraylist based on stores or name
     * @return - Filter
     */
    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                List<Item> filtered = new ArrayList<>();
                ArrayList<Item> items = itemManager.getItems();
                if (query.isEmpty()) {
                    filtered = itemManager.getItems();
                } else {
                    for (Item item : items) {
                        switch(filterType){
                            case NAME:
                                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                                    filtered.add(item);
                                }
                                break;
                            case STORE:
                                if (item.getUrl().toLowerCase().contains(query.toLowerCase())) {
                                    filtered.add(item);
                                }
                                break;
                        }

                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemManager.setFilteredItems((ArrayList<Item>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public void setFilterType(FilterType filterType){
        this.filterType = filterType;
    }

    public void clearFilters(){
        itemManager.clearFilters();
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView initialPriceTextView;
        public TextView currentPriceTextView;
        public TextView percentageChangeTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            initialPriceTextView = itemView.findViewById(R.id.item_initial_price);
            currentPriceTextView = itemView.findViewById(R.id.item_current_price);
            percentageChangeTextView = itemView.findViewById(R.id.item_percentage);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            int pos = getAdapterPosition();

            MenuItem open = contextMenu.add(Menu.NONE, 2, 2, "Open Browser");
            MenuItem edit = contextMenu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");


            open.setOnMenuItemClickListener(v -> {
                openWeb(pos);
                return true;
            });
            edit.setOnMenuItemClickListener(v -> {
                updateItem(pos);
                return true;
            });
            delete.setOnMenuItemClickListener(v -> {
                deleteItem(pos);
                return true;
            });

        }
    }

}
package cs4330.cs.utep.edu;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

public class WatchlistActivity extends AppCompatActivity {

    final static String GOOGLE_URL = "https://google.com";
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watchlist);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            showSettingDialog();
        }

        String url = getIntent().getStringExtra("url");

        if(url != null && !url.equals("")){
            // if the user sends an url, show a dialog with the url filled
            showDialog(url);
        }

        setUpRecyclerView();
        setUpSwipeController();
    }

    public void showSettingDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(WatchlistActivity.this).create();
        alertDialog.setMessage("Please Connect to WiFI");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Go to settings!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(settingsIntent);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    /**
     * Creates a recycler view and sets the item touch helper
     */
    private void setUpRecyclerView() {
        RecyclerViewEmptySupport rvItems = findViewById(R.id.rvItems);

        adapter = new ItemAdapter(this);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);
        rvItems.setEmptyView(findViewById(R.id.list_empty));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeController(adapter));
        itemTouchHelper.attachToRecyclerView(rvItems);
    }

    public ItemAdapter getAdapter(){
        return adapter;
    }

    /**
     * Sets the swipe controller that will refresh the item prices when pull down on
     * the list
     */
    public void setUpSwipeController(){
        SwipeRefreshLayout swipeContainer = findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.updateItemsPrice();
                swipeContainer.setRefreshing(false);
            }

        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchlist_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.clearFilters();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.add:
                showDialog();
                break;
            case R.id.filter:
                View menuItemView = findViewById(R.id.filter);
                showPopupMenu(menuItemView, false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // TO-DO implement filter with pop up menu
    private void showPopupMenu(View anchor, boolean isWithIcons) {
        PopupMenu popup = new PopupMenu(WatchlistActivity.this, anchor);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.name_menu_item:
                        adapter.setFilterType(ItemAdapter.FilterType.NAME);
                        break;
                    case R.id.store_menu_item:
                        adapter.setFilterType(ItemAdapter.FilterType.STORE);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void showDialog(){
        showDialog("", "", 0.0, -1);
    }

    public void showDialog(String url){
        showDialog("", url, 0.0, -1);
    }

    public void showDialog(String name, String url, double price, int pos){
        FragmentManager fm = getSupportFragmentManager();
        LayoutInflater inflater = LayoutInflater.from(WatchlistActivity.this);

        final View dialogView = inflater.inflate(R.layout.dialog_add, null);
        final EditText etName = dialogView.findViewById(R.id.name);
        final EditText etUrl = dialogView.findViewById(R.id.url);
        final ImageButton webButton = dialogView.findViewById(R.id.btn_web);
        webButton.setOnClickListener(view -> {
            adapter.openWeb(GOOGLE_URL);
        });

        etName.setText(name);
        etUrl.setText(url);

        final AlertDialog dialog = new AlertDialog.Builder(WatchlistActivity.this)
                .setTitle("Enter item information")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(isEmpty(etName) || isEmpty(etUrl)) {
                            showToast(getResources().getString(R.string.error_required));
                        } else {
                            String name = etName.getText().toString();
                            String url = etUrl.getText().toString();

                            if (pos != -1)
                                adapter.updateItem(name, url, pos);
                            else {
                                adapter.addItem(name, url, 0.0);
                                adapter.updateLastPriceItem();
                            }

                            dialog.dismiss();
                        }

                    }
                });
            }
        });
        dialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}

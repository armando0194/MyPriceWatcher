package cs4330.cs.utep.edu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

class ItemDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "itemDB";
    private static final String ITEM_TABLE = "itemDB";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_CURR_PRICE = "current";
    private static final String KEY_INITIAL_PRICE = "initial";

    public ItemDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + ITEM_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_URL + " TEXT,"
                + KEY_CURR_PRICE + " DOUBLE,"
                + KEY_INITIAL_PRICE + " DOUBLE"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        onCreate(db);
    }

    public DatabaseItem addItem(String name, String url, Double price){
        SQLiteDatabase db = this.getWritableDatabase();
        long id;

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_URL, url);
        values.put(KEY_CURR_PRICE, price);
        values.put(KEY_INITIAL_PRICE, price);

        id = db.insert(ITEM_TABLE, null, values);
        db.close();

        return (id != -1)? new DatabaseItem(id, name, url, price) : null;

    }

    public Boolean updateItem(long id, String name, String url, Double currPrice, Double initPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean success = false;

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_URL, url);
        values.put(KEY_CURR_PRICE, currPrice);
        values.put(KEY_INITIAL_PRICE, initPrice);

        success = db.update(ITEM_TABLE,
                values,
                KEY_ID + " = ?",
                new String[] { Long.toString(id) } ) >= 1;
        db.close();

        return success;
    }

    public Boolean deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Boolean success = db.delete(ITEM_TABLE,
                KEY_ID + " = ?",
                new String[] { Long.toString(id) } ) >= 1;
        db.close();

        return success;
    }

    public ArrayList<DatabaseItem> allItems() {
        ArrayList<DatabaseItem> items = new ArrayList<DatabaseItem>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ITEM_TABLE,null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String url = cursor.getString(cursor.getColumnIndex(KEY_URL));
                double initPrice = cursor.getDouble(cursor.getColumnIndex(KEY_INITIAL_PRICE));
                double currPrice = cursor.getDouble(cursor.getColumnIndex(KEY_CURR_PRICE));

                items.add(new DatabaseItem(id, name, url, initPrice, currPrice));
                cursor.moveToNext();
            }
        }

        return items;
    }
}

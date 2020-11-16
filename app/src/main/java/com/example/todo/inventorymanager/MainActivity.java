package com.example.todo.inventorymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.todo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.todo.inventorymanager.data.InventoryDbHelper;
import com.example.todo.inventorymanager.data.StockItem;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    InventoryDbHelper dbHelper;
    StockCursorAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new InventoryDbHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStock();

        adapter = new StockCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) return;
                    final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > lastVisibleItem) {
                        fab.show();
                    } else if (currentFirstVisibleItem < lastVisibleItem) {
                        fab.hide();
                    }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                // add dummy data for testing
                addDummyData();
                adapter.swapCursor(dbHelper.readStock());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add data for demo purposes
     */
    private void addDummyData() {
        StockItem gummibears = new StockItem(
            "Gummibears",
                "₹10",
                45,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/gummibear");
        dbHelper.insertItem(gummibears);

        StockItem peaches = new StockItem(
                "Peaches",
                "₹10",
                24,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/peach");
        dbHelper.insertItem(peaches);

        StockItem cherries = new StockItem(
                "Cherries",
                "₹11",
                74,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/cherry");
        dbHelper.insertItem(cherries);

        StockItem cola = new StockItem(
                "Cola",
                "₹13",
                44,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/cola");
        dbHelper.insertItem(cola);

        StockItem fruitSalad = new StockItem(
                "Fruit salad",
                "₹20",
                34,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/fruit_salad");
        dbHelper.insertItem(fruitSalad);

        StockItem smurfs = new StockItem(
                "Smurfs",
                "₹11",
                26,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/smurfs");
        dbHelper.insertItem(smurfs);

        StockItem fresquito = new StockItem(
                "Fresquito",
                "₹9",
                54,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/fresquito");
        dbHelper.insertItem(fresquito);

        StockItem hotChillies = new StockItem(
                "Hot chillies",
                "₹13",
                12,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/hot_chillies");
        dbHelper.insertItem(hotChillies);

        StockItem lolipopStrawberry = new StockItem(
                "Lolipop strawberry",
                "₹12",
                62,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/lolipop");
        dbHelper.insertItem(lolipopStrawberry);

        StockItem heartGummy = new StockItem(
                "Heart gummy jellies",
                "₹13",
                22,
                "ABC suppliers",
                "+91 9800000000",
                "abc@gmail.com",
                "android.resource://com.example.todo/drawable/heart_gummy");
        dbHelper.insertItem(heartGummy);
    }
}

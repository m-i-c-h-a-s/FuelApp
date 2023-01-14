package com.mp.fuelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mp.fuelapp.db.DatabaseHelper;
import com.mp.fuelapp.refueling.Refueling;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    RecyclerView recyclerView;
    FloatingActionButton addButton;
    ImageView noDataImageView;
    TextView noDataLabel;
    TextView dataTextView;

    DatabaseHelper databaseHelper;
    ArrayList<String> _id, fuel_amount, total_price, price_per_liter, date;
    ArrayList<byte[]> image;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.navigationView);
        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        noDataImageView = findViewById(R.id.noDataImageView);
        noDataLabel = findViewById(R.id.noDataLabel);
        dataTextView = findViewById(R.id.dataTextView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.openStatisticsItem:
                        Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("fuel_amount", fuel_amount);
                        bundle.putStringArrayList("total_price", total_price);
                        bundle.putStringArrayList("price_per_liter", price_per_liter);
                        bundle.putStringArrayList("date", date);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 2);
                        break;
                    case R.id.deleteAllRefuelingsItem:
                        confirmDialog();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivityForResult(intent, 1);
        });

        databaseHelper = new DatabaseHelper(MainActivity.this);
        _id = new ArrayList<>();
        fuel_amount = new ArrayList<>();
        total_price = new ArrayList<>();
        price_per_liter = new ArrayList<>();
        date = new ArrayList<>();
        image = new ArrayList<>();

        loadDataToArrays();

        customAdapter = new CustomAdapter(MainActivity.this, this, _id, fuel_amount, total_price, price_per_liter, date, image);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    private void loadDataToArrays() {
        Cursor cursor = databaseHelper.readAllData();
        if (cursor.getCount() == 0) {
            noDataImageView.setVisibility(View.VISIBLE);
            noDataLabel.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                _id.add(cursor.getString(0));
                fuel_amount.add(cursor.getString(1));
                total_price.add(cursor.getString(2));
                price_per_liter.add(cursor.getString(3));
                date.add(cursor.getString(4));
                image.add(cursor.getBlob(5));
            }
            noDataImageView.setVisibility(View.GONE);
            noDataLabel.setVisibility(View.GONE);
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure that you want to delete all refuelings?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                databaseHelper.deleteAllRefuelings();
                Toast.makeText(MainActivity.this, "All refuelings deleted.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.deleteAllRefuelingsButton:
                confirmDialog();
                break;
            case R.id.generateExampleData:
                byte[] img = new ByteArrayOutputStream().toByteArray();

                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                databaseHelper.addRefueling(36.8, 279.68, 7.60, "01-01-2023", img);
                databaseHelper.addRefueling(54.5, 412.02, 7.56, "02-01-2023", img);
                databaseHelper.addRefueling(21.5, 165.55, 7.70, "03-01-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.36, "04-01-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.33, "14-02-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.64, "24-02-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.23, "12-03-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.33, "24-03-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.66, "02-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.16, "12-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.26, "22-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,8.26, "23-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.28, "24-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.53, "25-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.26, "26-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.36, "27-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.26, "28-04-2023", img);
                databaseHelper.addRefueling(11.2, 82.43,7.16, "29-04-2023", img);
                this.recreate();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
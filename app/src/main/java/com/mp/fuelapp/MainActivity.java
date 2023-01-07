package com.mp.fuelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mp.fuelapp.db.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton addButton;
    ImageView noDataImageView;
    TextView noDataLabel;

    DatabaseHelper databaseHelper;
    ArrayList<String> _id, fuel_amount, total_price, price_per_liter, date;
    ArrayList<byte[]> image;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        noDataImageView = findViewById(R.id.noDataImageView);
        noDataLabel = findViewById(R.id.noDataLabel);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        databaseHelper = new DatabaseHelper(MainActivity.this);
        _id = new ArrayList<>();
        fuel_amount = new ArrayList<>();
        total_price = new ArrayList<>();
        price_per_liter = new ArrayList<>();
        date = new ArrayList<>();
        image = new ArrayList<>();

        loadDataToArrays();
        reverseRefuelingsOrderChronologically(_id, fuel_amount, total_price, price_per_liter, date, image);

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

    void loadDataToArrays() {
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

    void reverseRefuelingsOrderChronologically(ArrayList _id, ArrayList fuel_amount, ArrayList total_price, ArrayList price_per_liter, ArrayList date, ArrayList image) {
        Collections.reverse(_id);
        Collections.reverse(fuel_amount);
        Collections.reverse(total_price);
        Collections.reverse(price_per_liter);
        Collections.reverse(date);
        Collections.reverse(image);
    }

    void confirmDialog() {
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
                this.recreate();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
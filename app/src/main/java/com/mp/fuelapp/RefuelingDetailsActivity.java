package com.mp.fuelapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.fuelapp.db.DatabaseHelper;

public class RefuelingDetailsActivity extends AppCompatActivity {

    TextView fuelAmountValue, totalPriceValue, pricePerLiterValue, dateValue;
    ImageView receiptImageDetail;
    Button deleteRefuelingButton;

    String id, fuelAmount, totalPrice, pricePerLiter, refuelingDate;
    byte[] receiptImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refueling_details);

        fuelAmountValue = findViewById(R.id.fuelAmountValue);
        totalPriceValue = findViewById(R.id.totalPriceValue);
        pricePerLiterValue = findViewById(R.id.pricePerLiterValue);
        dateValue = findViewById(R.id.dateValue);
        deleteRefuelingButton = findViewById(R.id.deleteRefuelingButton);
        receiptImageDetail = findViewById(R.id.receiptImageDetail);

        getIntentData();

        deleteRefuelingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    void getIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("fuelAmount")
            && getIntent().hasExtra("totalPrice") && getIntent().hasExtra("pricePerLiter") && getIntent().hasExtra("refuelingDate")) {
            id = getIntent().getStringExtra("id");
            fuelAmount = getIntent().getStringExtra("fuelAmount");
            totalPrice = getIntent().getStringExtra("totalPrice");
            pricePerLiter = getIntent().getStringExtra("pricePerLiter");
            refuelingDate = getIntent().getStringExtra("refuelingDate");

            Bundle bundle = getIntent().getExtras();
            receiptImage = bundle.getByteArray("receiptImage");
            Bitmap image = BitmapFactory.decodeByteArray(receiptImage, 0, receiptImage.length);

            fuelAmountValue.setText(fuelAmount + "L");
            totalPriceValue.setText(totalPrice + "zł");
            pricePerLiterValue.setText(pricePerLiter + "zł/L");
            dateValue.setText(refuelingDate);
            receiptImageDetail.setImageBitmap(image);
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure that you want to delete this refueling?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper databaseHelper = new DatabaseHelper(RefuelingDetailsActivity.this);
                databaseHelper.deleteRefueling(id);
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
}
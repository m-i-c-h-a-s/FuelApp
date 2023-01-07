package com.mp.fuelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    String CURRENCY = "zł";
    String VOLUME_UNIT = "L";

    private Context context;
    private Activity activity;
    private ArrayList _id, fuel_amount, total_price, price_per_liter, date, image;

    Animation translate_anim;

    CustomAdapter(Activity activity,
                  Context context,
                  ArrayList _id,
                  ArrayList fuel_amount,
                  ArrayList total_price,
                  ArrayList price_per_liter,
                  ArrayList date,
                  ArrayList image
    ){
        this.activity = activity;
        this.context = context;
        this._id = _id;
        this.fuel_amount = fuel_amount;
        this.total_price = total_price;
        this.price_per_liter = price_per_liter;
        this.date = date;
        this.image = image;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_refueling_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.refuelingPrice.setText(String.valueOf(total_price.get(position)) + CURRENCY);
        holder.refuelingFuelAmount.setText(String.valueOf(fuel_amount.get(position)) + VOLUME_UNIT);
        holder.refuelingDate.setText(String.valueOf(date.get(position)));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RefuelingDetailsActivity.class);
                intent.putExtra("id", String.valueOf(_id.get(position)));
                intent.putExtra("fuelAmount", String.valueOf(fuel_amount.get(position)));
                intent.putExtra("totalPrice", String.valueOf(total_price.get(position)));
                intent.putExtra("pricePerLiter", String.valueOf(price_per_liter.get(position)));
                intent.putExtra("refuelingDate", String.valueOf(date.get(position)));

                Bundle bundle = new Bundle();
                bundle.putByteArray("receiptImage", (byte[]) image.get(position));
                intent.putExtras(bundle);

                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView refuelingPrice, refuelingFuelAmount, refuelingDate;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            refuelingPrice = itemView.findViewById(R.id.refuelingPrice);
            refuelingFuelAmount = itemView.findViewById(R.id.refuelingFuelAmount);
            refuelingDate = itemView.findViewById(R.id.refuelingDate);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }
}

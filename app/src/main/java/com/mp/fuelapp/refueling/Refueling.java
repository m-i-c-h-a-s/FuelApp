package com.mp.fuelapp.refueling;

import android.graphics.Bitmap;

import java.time.LocalDate;
import java.util.Date;

public class Refueling {
    private int id;
    private Double fuelAmount;
    private Double totalPrice;
    private Double pricePerLiter;
    private String date;
    private Bitmap receiptImage;

    public Refueling(int id, Double fuelAmount, Double totalPrice, Double pricePerLiter, String date, Bitmap receiptImage) {
        this.id = id;
        this.fuelAmount = fuelAmount;
        this.totalPrice = totalPrice;
        this.pricePerLiter = pricePerLiter;
        this.date = date;
        this.receiptImage = receiptImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(Double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(Double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getReceiptImage() {
        return receiptImage;
    }

    public void setReceiptImage(Bitmap receiptImage) {
        this.receiptImage = receiptImage;
    }
}

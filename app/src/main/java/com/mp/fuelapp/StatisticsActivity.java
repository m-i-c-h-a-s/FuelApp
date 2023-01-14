package com.mp.fuelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private TextView yearlyFuelPriceLabelTextView, monthlyFuelPriceLabelTextView, yearlyFuelAmountLabelTextView, monthlyFuelAmountLabelTextView;
    private TextView yearlyFuelPriceValueTextView, monthlyFuelPriceValueTextView, yearlyFuelAmountValueTextView, monthlyFuelAmountValueTextView;
    LineChart fuelPriceInTimeChart;

    private LocalDate currentDate;
    private String currentMonth, currentYear;
    private String yearlyFuelPrice = "", monthlyFuelPrice = "", yearlyFuelAmount = "", monthlyFuelAmount = "";
    private ArrayList<String> fuel_amount, total_price, price_per_liter, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            currentYear = String.valueOf(currentDate.getYear());
            currentMonth = currentDate.getMonth().toString().substring(0,1).toUpperCase() + currentDate.getMonth().toString().substring(1).toLowerCase();
        }

        try {
            getIntentData();
            generateStatistics(currentDate);
            declareLayoutElements();
            setValuesToLayoutElements();
            setupChart();
        } catch (Exception e) {
            Toast.makeText(this, "Statistics cannot be generated because data has not been provided.", Toast.LENGTH_LONG).show();
        }
    }

    private void getIntentData() {
        if (!getIntent().getExtras().getStringArrayList("fuel_amount").isEmpty()) {
            Bundle bundle = getIntent().getExtras();
            fuel_amount = bundle.getStringArrayList("fuel_amount");
            total_price = bundle.getStringArrayList("total_price");
            price_per_liter = bundle.getStringArrayList("price_per_liter");
            date = bundle.getStringArrayList("date");
        }
    }

    private void generateStatistics(LocalDate currentDate) {
        Double yearlyPrice = 0.0, yearlyAmount = 0.0, monthlyPrice = 0.0, monthlyAmount = 0.0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int i = 0; i < date.size(); i++) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate refuelingDate = LocalDate.parse(date.get(i), formatter);

                if (refuelingDate.getYear() == currentDate.getYear()) {
                    yearlyPrice += Double.parseDouble(total_price.get(i));
                    yearlyAmount += Double.parseDouble(fuel_amount.get(i));

                    if (refuelingDate.getMonth() == currentDate.getMonth()) {
                        monthlyPrice += Double.parseDouble(total_price.get(i));
                        monthlyAmount += Double.parseDouble(fuel_amount.get(i));
                    }
                }
            }
        }

        yearlyFuelPrice = String.format("%.2f", yearlyPrice);
        yearlyFuelAmount = String.format("%.2f", yearlyAmount);
        monthlyFuelPrice = String.format("%.2f", monthlyPrice);
        monthlyFuelAmount = String.format("%.2f", monthlyAmount);
    }

    private void declareLayoutElements() {
        yearlyFuelPriceLabelTextView = findViewById(R.id.yearlyFuelPriceLabel);
        monthlyFuelPriceLabelTextView = findViewById(R.id.monthlyFuelPriceLabel);
        yearlyFuelAmountLabelTextView = findViewById(R.id.yearlyFuelAmountLabel);
        monthlyFuelAmountLabelTextView = findViewById(R.id.monthlyFuelAmountLabel);

        yearlyFuelPriceValueTextView = findViewById(R.id.yearlyFuelPriceValue);
        monthlyFuelPriceValueTextView = findViewById(R.id.monthlyFuelPriceValue);
        yearlyFuelAmountValueTextView = findViewById(R.id.yearlyFuelAmountValue);
        monthlyFuelAmountValueTextView = findViewById(R.id.monthlyFuelAmountValue);

        fuelPriceInTimeChart = findViewById(R.id.fuelPriceInTimeChart);
    }

    private void setValuesToLayoutElements() {
        yearlyFuelPriceLabelTextView.setText("In " + currentYear + ": ");
        monthlyFuelPriceLabelTextView.setText("In " + currentMonth + ": ");
        yearlyFuelAmountLabelTextView.setText("In " + currentYear + ": ");
        monthlyFuelAmountLabelTextView.setText("In " + currentMonth + ": ");

        yearlyFuelPriceValueTextView.setText(yearlyFuelPrice + "zł");
        monthlyFuelPriceValueTextView.setText(monthlyFuelPrice + "zł");
        yearlyFuelAmountValueTextView.setText(yearlyFuelAmount + "L");
        monthlyFuelAmountValueTextView.setText(monthlyFuelAmount + "L");
    }

    private void setupChart() {
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Entry> yAxisValues = new ArrayList<>();

        for (int i = 0; i < date.size(); i++) {
            xAxisValues.add(date.get(i));
            yAxisValues.add(new Entry(i, Float.parseFloat(price_per_liter.get(i))));
        }

        LineDataSet dataSet = new LineDataSet(yAxisValues, "");
        LineData data = new LineData(dataSet);

        dataSet.setColor(getResources().getColor(R.color.red_1));
        dataSet.setCircleColor(getResources().getColor(R.color.yellow_800));
        dataSet.setLineWidth(2);
        dataSet.setValueTextSize(14);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                return df.format(value);
            }
        });

        fuelPriceInTimeChart.setData(data);
        fuelPriceInTimeChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        fuelPriceInTimeChart.getXAxis().setGranularityEnabled(true);
        fuelPriceInTimeChart.getXAxis().setGranularity(1.0f);
        fuelPriceInTimeChart.getXAxis().setLabelRotationAngle(-90);

        fuelPriceInTimeChart.setBackgroundColor(Color.WHITE);
        fuelPriceInTimeChart.setPinchZoom(true);
        fuelPriceInTimeChart.setVisibleXRangeMaximum(10);
        fuelPriceInTimeChart.getLegend().setEnabled(false);
        fuelPriceInTimeChart.getDescription().setEnabled(false);
        fuelPriceInTimeChart.getAxisRight().setEnabled(false);
    }

}
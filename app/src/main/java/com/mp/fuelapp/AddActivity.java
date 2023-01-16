package com.mp.fuelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.mp.fuelapp.db.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    public static final String DATE_REGEX = "[0-9]{2}-[0-9]{2}-[0-9]{4}";
    public static final String AMOUNT_X_PRICE_REGEX = "[1-9]{1}[0-9]{0,2}\\.[0-9]{2}[*,+,%,x,X][1-9]\\.[0-9]{2}";

    Uri image_uri;
    Bitmap bitmap;
    EditText fuelAmountInput, totalPriceInput, pricePerLiterInput, refuelingDateInput;
    Button saveButton, captureButton, recogniseButton;
    ImageView receiptImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fuelAmountInput = findViewById(R.id.fuelAmountInput);
        totalPriceInput = findViewById(R.id.totalPriceInput);
        pricePerLiterInput = findViewById(R.id.pricePerLiterInput);
        refuelingDateInput = findViewById(R.id.refuelingDateInput);
        receiptImage = findViewById(R.id.receiptImage);
        saveButton = findViewById(R.id.saveButton);
        captureButton = findViewById(R.id.captureButton);
        recogniseButton = findViewById(R.id.recogniseButton);

        refuelingDateInput.addTextChangedListener(dateTextWatcher);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!fuelAmountInput.getText().toString().isEmpty() && !totalPriceInput.getText().toString().isEmpty()
                    && !pricePerLiterInput.getText().toString().isEmpty() && !refuelingDateInput.getText().toString().isEmpty()) {

                    bitmap = ((BitmapDrawable) receiptImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArray);
                    byte[] img = byteArray.toByteArray();

                    DatabaseHelper databaseHelper = new DatabaseHelper(AddActivity.this);
                    databaseHelper.addRefueling(Double.valueOf(fuelAmountInput.getText().toString().trim()),
                            Double.valueOf(totalPriceInput.getText().toString()),
                            Double.valueOf(pricePerLiterInput.getText().toString().trim()),
                            refuelingDateInput.getText().toString().trim(),
                            img
                    );
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "Enter the correct data.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recogniseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recogniseText();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiptImage.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
                askCameraPermission();
            }
        });

    }



    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Picture from the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is required to use Camera", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            File fdelete = new File(getFilePath(image_uri));
            if (fdelete.exists()) {
                receiptImage.setImageURI(image_uri);
                recogniseText();
                fdelete.delete();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDataFromImage(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");

                String date = findDataByRegex(textBlock.getValue(), DATE_REGEX);
                if (date != "") {
                    refuelingDateInput.setText(date.trim());
                }

                String amountXPrice = findDataByRegex(textBlock.getValue(), AMOUNT_X_PRICE_REGEX);
                if (amountXPrice != "") {
                    int asteriskPosition = amountXPrice.indexOf("*");
                    if (asteriskPosition == -1)
                        asteriskPosition = amountXPrice.indexOf("%");
                    else if (asteriskPosition == -1)
                        asteriskPosition = amountXPrice.indexOf("x");
                    else if (asteriskPosition == -1)
                        asteriskPosition = amountXPrice.indexOf("+");

                    if (asteriskPosition != -1) {
                        String amount, price;
                        amount = amountXPrice.substring(0, asteriskPosition);
                        price = amountXPrice.substring(asteriskPosition+1);
                        Double totalPrice = Double.parseDouble(amount) * Double.parseDouble(price);
                        DecimalFormat dcf = new DecimalFormat("000.00");

                        fuelAmountInput.setText(amount.trim());
                        pricePerLiterInput.setText(price.trim());
                        totalPriceInput.setText(dcf.format(totalPrice));
                    }
                }

            }
        }
    }

    private String findDataByRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String result = matcher.group();
            return result;
        } else return "";
    }

    private void recogniseText() {
        try {
            bitmap = ((BitmapDrawable) receiptImage.getDrawable()).getBitmap();
            getDataFromImage(bitmap);
        } catch (Exception e) {
            Toast.makeText(AddActivity.this, "Cannot recognise text.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

    TextWatcher dateTextWatcher = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }

                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s-%s-%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                refuelingDateInput.setText(current);
                refuelingDateInput.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
}
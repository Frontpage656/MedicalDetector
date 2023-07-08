package com.example.medicaldetector;

import static java.lang.String.valueOf;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button buttonScan;
    TextView category, product, county_code, exDate, timeRemain, code_number, price, description, years, months, days, exDateLabel;
    String results_code;
    int remainYears, remainMonth, remainDays;
    LinearLayout expireLinear;
    TableLayout table;
    MaterialCardView theTable;

    FirebaseDatabase databese;


    String productName;

    String countyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.buttonScan);
        category = findViewById(R.id.category);
        product = findViewById(R.id.product);
        county_code = findViewById(R.id.county_code);
        theTable = findViewById(R.id.theTable);
        exDate = findViewById(R.id.exDate);
        code_number = findViewById(R.id.code_number);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        exDateLabel = findViewById(R.id.exDateLabel);
        expireLinear = findViewById(R.id.expireLinear);
        table = findViewById(R.id.table);

        years = findViewById(R.id.years);
        months = findViewById(R.id.months);
        days = findViewById(R.id.days);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this)
                        .setPrompt("Use volume up for flash light!")
                        .setBeepEnabled(true)
                        .setCaptureActivity(Capture.class)
                        .setBarcodeImageEnabled(true)
                        .setOrientationLocked(true);
                integrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            String results = intentResult.getContents();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] decript = Base64.getMimeDecoder().decode(results);
                results_code = new String(decript);
            }
            execution();
        }
    }

    private void execution() {

        try {

            String categoryType = results_code.substring(0, results_code.indexOf("@"));
            String code = results_code.substring(results_code.indexOf("@") + 1, results_code.indexOf("["));
            productName = results_code.substring(results_code.indexOf("[") + 1, results_code.indexOf("]"));
            String productPrice = results_code.substring(results_code.indexOf("]") + 1, results_code.indexOf("*"));
            String eDate = results_code.substring(results_code.indexOf("*") + 1, results_code.indexOf("~"));
            String eMonth = results_code.substring(results_code.indexOf("~") + 1, results_code.indexOf("!"));
            String eYear = results_code.substring(results_code.indexOf("!") + 1, results_code.indexOf("{"));
            String explanation = results_code.substring(results_code.indexOf("{") + 1, results_code.indexOf("}"));


            String dateDate = eDate + "/" + eMonth + "/" + eYear;

            int date = Integer.parseInt(eDate);
            int month = Integer.parseInt(eMonth);
            int year = Integer.parseInt(eYear);
            int categoryNo = Integer.parseInt(categoryType);


            LocalDate localDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                localDate = LocalDate.now();
                LocalDate expireDate = LocalDate.of(year, month, date);

                if (localDate.equals(expireDate)) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Warning")
                            .setMessage("Product Expired!!!!!!!!!! today " + dateDate)
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Details sent", Toast.LENGTH_LONG).show();
                                }
                            }).show();

                } else if (localDate.isAfter(expireDate)) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Warning")
                            .setMessage("Product Expired!!!!!!!!!! since " + dateDate)
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                                            && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                MainActivity.this,
                                                new String[]{Manifest.permission.SEND_SMS,
                                                        Manifest.permission.RECEIVE_SMS},
                                                123);
                                    } else {

                                        sendBackMessageToUser("0788836673", "Bidhaa feki imegunduliwa\n" + "Name: " + productName + "\n" + "Time: " + reportedTime(LocalDateTime.now()));
                                    }
                                    Toast.makeText(getApplicationContext(), "Details sent", Toast.LENGTH_LONG).show();
                                }
                            }).show();

                } else {


                    // Upload to firebase...

                    HashMap<Object, String> hashMap = new HashMap<>();

                    hashMap.put("name", productName);
                    hashMap.put("country", countyName);
                    hashMap.put("price", productPrice);
                    hashMap.put("eDate",eDate+"/"+month+"/"+year);
                    hashMap.put("discription", explanation);


                    databese = FirebaseDatabase.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("item");

                    LocalDate finalLocalDate = localDate;
                    myRef.push().setValue(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //I did not know before!!!
                                    int duration = (int) ChronoUnit.DAYS.between(finalLocalDate, expireDate);

                                    switch (categoryNo) {
                                        case 1:
                                            category.setText("Foods & Drinks");
                                            break;
                                        case 2:
                                            category.setText("Medicine");
                                            break;
                                        case 3:
                                            category.setText("Pesticides");
                                            break;
                                        case 4:
                                            category.setText("Others");
                                            county_code.setText(code);
                                            product.setText(productName);
                                            price.setText(productPrice);
                                            description.setText(explanation);
                                            expireLinear.setVisibility(View.GONE);
                                            timeRemain.setVisibility(View.GONE);
                                            table.setVisibility(View.GONE);
                                            theTable.setVisibility(View.GONE);
                                            break;
                                    }


                                    if (code.equals("255")) {
                                        countyName = "TANZ";
                                    } else if (code.equals("254")) {
                                        countyName = "KENY";
                                    } else {
                                        countyName = "IND";
                                    }

                                    county_code.setText(countyName);
                                    product.setText(productName);
                                    price.setText(productPrice);
                                    exDate.setText(dateDate);
                                    description.setText(explanation);


                                    // Interval function
                                    remainYears = duration / 365;
                                    remainMonth = (duration % 365) / 30;
                                    remainDays = duration - ((remainYears * 365) + (remainMonth * 30));

                                    years.setText(valueOf(remainYears));
                                    months.setText(valueOf(remainMonth));
                                    days.setText(valueOf(remainDays));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to save data to list", Toast.LENGTH_SHORT).show();
                                }
                            });





                }
            }

        } catch (Exception e) {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Warning!!!")
                    .setMessage("Product not registered")
                    .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendBackMessageToUser("0763686917", "Bidhaa isiyosajiriwa imegunduliwa\n" + "Name: " + productName + "\n" + "Time: " + reportedTime(LocalDateTime.now()));
                        }
                    })
                    .show();
        }
    }

    private void sendBackMessageToUser(String userNumber, String messageBody) {
        //TODO: send message back to user
        // Get the default SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Set the destination phone number and the message body
        // Send the message
        // Split the message into parts
        ArrayList<String> parts = smsManager.divideMessage(messageBody);

        smsManager.sendMultipartTextMessage(userNumber, null, parts, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String reportedTime(LocalDateTime date) {

        //LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = date.format(formatter);

        return formattedTime;
    }
}

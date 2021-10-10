package com.example.personalfinancialmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncomeActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    private Spinner dropdown_categories;


    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dates;

    private TextView calenderT;
    private EditText amountE, desrciptionE;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseUser user;
    private ValueEventListener a;

    String month_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        user = auth.getCurrentUser();


        amountE = findViewById(R.id.amount);

        desrciptionE = findViewById(R.id.textArea_information);


        calenderT = findViewById(R.id.calender);

        //array to populate start time spinner
        String[] arraySpinner = new String[]{
                "Salary", "Investment", "Revenues"

        };

        //array to populate durationn spinner

        dropdown_categories = findViewById(R.id.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_categories.setAdapter(adapter);


        dates = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        findViewById(R.id.apply).setOnClickListener(this);
        findViewById(R.id.calender).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);


    }


    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        String monthFormat = "MMMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        SimpleDateFormat mdf = new SimpleDateFormat(monthFormat, Locale.getDefault());

        month_date = mdf.format(myCalendar.getTime());
        calenderT.setText(sdf.format(myCalendar.getTime()));
    }

    //function to check if the date is overlap
    public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }

    //add hours to date and return in date formtat
    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply:
                final String date = calenderT.getText().toString().trim();
                final String amount = amountE.getText().toString().trim();
                final String description = desrciptionE.getText().toString().trim();
                final String categories = dropdown_categories.getSelectedItem().toString();

//                 check if any field is empty
                if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amount)
                        || TextUtils.isEmpty(description) || TextUtils.isEmpty(categories)) {
                    Toast.makeText(getApplicationContext(), "Please key in all field", Toast.LENGTH_SHORT).show();
                    return;
                }

                Income income = new Income();
                income.setdate(date);
                income.setAmount(amount);
                income.setcategories(categories);
                income.setDescription(description);


                ref.child(user.getUid()).child("Transaction_Record").child(month_date).child(date).child("Income").push().setValue(income, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getApplicationContext(), "Income Added Successfully.", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.calender:
                DatePickerDialog a = new DatePickerDialog(AddIncomeActivity.this, dates, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar b = Calendar.getInstance();
                b.add(Calendar.DAY_OF_MONTH, 0);
                b.set(Calendar.HOUR_OF_DAY, 8);
                b.set(Calendar.MINUTE, 0);

                a.getDatePicker().setMaxDate(b.getTimeInMillis());
                a.show();
                break;
            case R.id.back:
                finish();
                break;
        }


    }

}
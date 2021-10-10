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

import com.example.personalfinancialmanagement.AddExpenseActivity;
import com.example.personalfinancialmanagement.Expense;
import com.example.personalfinancialmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    private Spinner dropdown_categories;


    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dates;

    private TextView calenderT;
    private EditText amountE, desrciptionE;
    private final SimpleDateFormat  monthFormat = new SimpleDateFormat("MMMM-yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseUser user;
    private ValueEventListener a;

    String month_date;

    String prev_date,prev_amount, prev_categories, prev_description, key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        user = auth.getCurrentUser();

        amountE = findViewById(R.id.amount);
        desrciptionE = findViewById(R.id.textArea_information);
        calenderT = findViewById(R.id.calender);

        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                prev_date = null;
                prev_amount = null;
                prev_categories = null;
                prev_description = null;
                key=null;
            } else {
                prev_date= extras.getStringExtra("date");
                prev_amount = extras.getStringExtra("amount");
                prev_categories = extras.getStringExtra("categories");
                prev_description = extras.getStringExtra("description");
                key= extras.getStringExtra("key");

            }
        } else {
            prev_date = (String) savedInstanceState.getSerializable("date");
            prev_amount = (String) savedInstanceState.getSerializable("amount");
            prev_categories = (String) savedInstanceState.getSerializable("categories");
            prev_description = (String) savedInstanceState.getSerializable("description");
            key= (String) savedInstanceState.getSerializable("key");
        }

        //array to populate start time spinner
        String[] arraySpinner = new String[]{
                "Food", "Entertainment", "Apparel", "Fuels", "Sports",

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

        if (prev_date != null) {
//            String monthFormat = "MMMM-yyyy";
//            SimpleDateFormat mdf = new SimpleDateFormat(monthFormat, Locale.getDefault());

            try {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date date = format.parse(prev_date);
                if (date != null) {
                    month_date = monthFormat.format(date);
                } else {
                    month_date=monthFormat.format(new Date());
                }
            }catch(ParseException e){
                Toast.makeText(getApplicationContext(), "Date not found", Toast.LENGTH_SHORT).show();
            }
            calenderT.setText(prev_date);
        }

        if (prev_amount != null) {
            amountE.setText(prev_amount);
        }

        if (prev_description!= null) {
            desrciptionE.setText(prev_description);
        }

        if (prev_categories!= null) {
            int selectionPosition= adapter.getPosition(prev_categories);
            dropdown_categories.setSelection(selectionPosition);
        }




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
//                final String location = locationE.getText().toString().trim();
                final String description = desrciptionE.getText().toString().trim();
                final String categories = dropdown_categories.getSelectedItem().toString();

//                 check if any field is empty
                if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amount)
                        || TextUtils.isEmpty(description) || TextUtils.isEmpty(categories)) {
                    Toast.makeText(getApplicationContext(), "Please key in all field", Toast.LENGTH_SHORT).show();
                    return;
                }



                Expense expense = new Expense();
                expense.setAmount(amount);
                expense.setdate(date);
                expense.setcategories(categories);
                expense.setDescription(description);



                ref.child(user.getUid()).child("Transaction_Record").child(month_date).child(date).child("Expense").child(key).setValue(expense, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getApplicationContext(), "Expense Added Successfully.", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.calender:
                DatePickerDialog a = new DatePickerDialog(EditExpenseActivity.this,dates, myCalendar
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
                if(HomeActivity.homeActivity==null){
                    Intent intent = new Intent(EditExpenseActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
        }


    }
}
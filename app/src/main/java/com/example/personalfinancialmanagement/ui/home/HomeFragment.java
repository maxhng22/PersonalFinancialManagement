package com.example.personalfinancialmanagement.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.personalfinancialmanagement.Expense;
import com.example.personalfinancialmanagement.HomeActivity;
import com.example.personalfinancialmanagement.Income;
import com.example.personalfinancialmanagement.R;
import com.example.personalfinancialmanagement.ui.notifications.NotificationsViewModel;
import com.example.personalfinancialmanagement.ui.notifications.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class HomeFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private static ArrayList<Expense> data;
    private static ArrayList<Income> data_income;
    private static ArrayList<Product> income_cat, exp_cat;
    static View.OnClickListener myOnClickListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    private double total_income, total_exp;
    private TextView exp_textview, inc_textview, balance_textview;
    String date;
    String month_date, filterBy;
    Pie pie;
    IntentFilter filter = new IntentFilter("com.codinginflow.EXAMPLE_ACTION");
    private BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDataFromFirebase();
//            Log.e("smsBroadcastReceiver", "onReceive");
        }
    };

    @Override
    public void onDestroyView() {

        getActivity().unregisterReceiver(smsBroadcastReceiver);
        super.onDestroyView();
    }

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        exp_textview = root.findViewById(R.id.exp);
        inc_textview = root.findViewById(R.id.income);
        balance_textview = root.findViewById(R.id.balance);

        data = new ArrayList<Expense>();
        data_income = new ArrayList<Income>();
        income_cat = new ArrayList<Product>();
        exp_cat = new ArrayList<Product>();

        getDataFromFirebase();

        getActivity().registerReceiver(smsBroadcastReceiver, filter);

        return root;
    }

    public void getDataFromFirebase() {


        if (HomeActivity.homeActivity != null) {
            date = HomeActivity.getExtra_date();
            month_date = (String) HomeActivity.getExtra_month();
            filterBy = (String) HomeActivity.getFilter();

        }

        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data.clear();
                data_income.clear();
                income_cat.clear();
                exp_cat.clear();
                if (dataSnapshot.exists()) {

                    if (filterBy.equals("Monthly")) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            for (DataSnapshot areaSnapshot : snapshot.child("Expense").getChildren()) {
                                Expense key_Detail = areaSnapshot.getValue(Expense.class);
                                data.add(key_Detail);
                            }
                            for (DataSnapshot areaSnapshot : snapshot.child("Income").getChildren()) {
                                Income key_Detail = areaSnapshot.getValue(Income.class);
                                data_income.add(key_Detail);
                            }

                            formatData(data, data_income);
                        }
                    } else {

                        for (DataSnapshot areaSnapshot : dataSnapshot.child("Expense").getChildren()) {
                            Expense key_Detail = areaSnapshot.getValue(Expense.class);
                            data.add(key_Detail);
                        }
                        for (DataSnapshot areaSnapshot : dataSnapshot.child("Income").getChildren()) {
                            Income key_Detail = areaSnapshot.getValue(Income.class);
                            data_income.add(key_Detail);
                        }
                        Log.i("lol2 top",String.valueOf( data.size()));
                        formatData(data, data_income);
                    }

                } else {
                    exp_textview.setText("0");
                    inc_textview.setText("0");
                    balance_textview.setText("0");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        if (filterBy.equals("Monthly")) {
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ", "-")).addListenerForSingleValueEvent(a);

        } else {
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ", "-")).child(date).addListenerForSingleValueEvent(a);

        }
    }

    public void formatData(ArrayList<Expense> expenses, ArrayList<Income> incomes) {

        double total_in = 0, total_ex = 0;


        for (int i = 0; i < expenses.size(); i++) {
            Log.i("lol",String.valueOf( expenses.size()));
            double ex_double=0;
            try {
                ex_double = Double.parseDouble(expenses.get(i).getAmount());
            } catch (NumberFormatException e) {
                ex_double = 0;
            }

            total_ex += ex_double;
        }

        for (int i = 0; i < incomes.size(); i++) {
            double in_double=0;
            try {
                in_double = Double.parseDouble(incomes.get(i).getAmount());
            } catch (NumberFormatException e) {
                in_double = 0;
            }

            total_in += in_double;
        }


        Log.i("l2ol",String.valueOf(total_ex));

        setBalance(total_ex, total_in);
    }

    public void setBalance(double exp, double income) {
        inc_textview.setText(String.valueOf(income));
        exp_textview.setText(String.valueOf(exp));
        balance_textview.setText(String.valueOf(income-exp));
    }
}
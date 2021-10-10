package com.example.personalfinancialmanagement.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.example.personalfinancialmanagement.Expense;
import com.example.personalfinancialmanagement.HomeActivity;
import com.example.personalfinancialmanagement.Income;
import com.example.personalfinancialmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private static ArrayList<Expense> data;
    private static ArrayList<Income> data_income;
    private static ArrayList<Product> income_cat, exp_cat;
    static View.OnClickListener myOnClickListener;


    private AnyChartView anyChartView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    private double total_income, total_exp;
    List<DataEntry> data_list;
    String date;
    String month_date, filterBy;
    Pie pie;

    boolean isDoubleCliked=false;


    Handler handler=new Handler();
    Runnable r=new Runnable(){
        @Override
        public void run(){
            //Actions when Single Clicked
            isDoubleCliked=false;
        }
    };


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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        data = new ArrayList<Expense>();
        data_income = new ArrayList<Income>();
        income_cat = new ArrayList<Product>();
        exp_cat = new ArrayList<Product>();

        pie = AnyChart.pie();
        pie.labels().position("outside");
        pie.legend().title().enabled(true);
        pie.radius(120);
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {

                if(isDoubleCliked){
                    //Actions when double Clicked
                    isDoubleCliked=false;
                    //remove callbacks for Handlers
                    setData(event.getData().get("x"));
                    handler.removeCallbacks(r);
                }else{
                    isDoubleCliked=true;
                    Toast.makeText(getActivity(), "Double Click to view detail", Toast.LENGTH_SHORT).show();

                    handler.postDelayed(r,500);
                }

            }
        });
        getDataFromFirebase();

        data_list = new ArrayList<>();

        getActivity().registerReceiver(smsBroadcastReceiver, filter);

        anyChartView = root.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar));
        anyChartView.setChart(pie);

        return root;
    }

    public void setData(String type) {
        data_list.clear();
        switch (type) {
            case "Income":
                Log.i("here","income");
                for (int i = 0; i < income_cat.size(); i++) {
                    Log.i("hello here",income_cat.get(i).getCategories());
                    data_list.add(new ValueDataEntry(income_cat.get(i).getCategories(), income_cat.get(i).getAmount()));
                }
                setPieChart(data_list, "Income Report", "Income Categories");
                break;
            case "Expenses":
                Log.i("here","expenses");
                for (int i = 0; i < exp_cat.size(); i++) {
                    data_list.add(new ValueDataEntry(exp_cat.get(i).getCategories(), exp_cat.get(i).getAmount()));
                }
                setPieChart(data_list, "Expenses Report", "Expenses Categories");
                break;
            default:
                data_list.add(new ValueDataEntry("Income", total_income));
                data_list.add(new ValueDataEntry("Expenses", total_exp));
                setPieChart(data_list, "Overall Report", "Type");
                break;

        }

    }


    public void setPieChart(List<DataEntry> data, String title, String category) {
        pie.data(data);
        pie.title(title);
        pie.legend().title()
                .text(category)
                .padding(0d, 0d, 4d, 0d);



    }

    public void getDataFromFirebase() {

        if (HomeActivity.homeActivity != null) {
            date = HomeActivity.getExtra_date();
            month_date = (String) HomeActivity.getExtra_month();
            filterBy=(String) HomeActivity.getFilter();

        }

        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                data_income.clear();
                income_cat.clear();
                exp_cat.clear();
                if (dataSnapshot.exists()) {

                    if(filterBy.equals("Monthly")){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.i("here inc",month_date);
                            for (DataSnapshot areaSnapshot : snapshot.child("Expense").getChildren()) {

                                Expense key_Detail = areaSnapshot.getValue(Expense.class);
                                Log.i("here exp",key_Detail.getcategories());
                                data.add(key_Detail);
                            }

                            for (DataSnapshot areaSnapshot : snapshot.child("Income").getChildren()) {
                                Income key_Detail = areaSnapshot.getValue(Income.class);

                                data_income.add(key_Detail);
                            }


                            formatData(data, data_income);
                        }
                    }else{
                        for (DataSnapshot areaSnapshot : dataSnapshot.child("Expense").getChildren()) {
                            Expense key_Detail = areaSnapshot.getValue(Expense.class);
                            Log.i("here exp",key_Detail.getcategories());
                            data.add(key_Detail);
                        }

                        for (DataSnapshot areaSnapshot : dataSnapshot.child("Income").getChildren()) {
                            Income key_Detail = areaSnapshot.getValue(Income.class);

                            data_income.add(key_Detail);
                        }

                        formatData(data, data_income);
                    }

                }else{
                    data_list.clear();
                    data_list.add(new ValueDataEntry("Income", 0));
                    data_list.add(new ValueDataEntry("Expenses", 0));
                    setPieChart(data_list, "Overall Report", "Type");
                    Log.i("here",month_date);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        if(filterBy.equals("Monthly")){
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ","-")).addListenerForSingleValueEvent(a);

        }else{
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ","-")).child(date).addListenerForSingleValueEvent(a);

        }
    }


    public void formatData(ArrayList<Expense> expenses, ArrayList<Income> incomes) {

        double total_in = 0, total_ex = 0;
        double ex_double, in_double;

        for (int i = 0; i < expenses.size(); i++) {
            try {
                ex_double = Double.parseDouble(expenses.get(i).getAmount());
            } catch (NumberFormatException e) {
                ex_double = 0;
            }
            Log.i("checking ", "expense");
            Log.i("checking ", Integer.toString(expenses.size()));
            int ind = getExistLocation(expenses.get(i).getcategories(), "expense");
            if (ind >= 0) {
                exp_cat.get(ind).setAmount(exp_cat.get(ind).getAmount() + ex_double);
            } else {
                Product product = new Product();
                product.setAmount(ex_double);
                product.setCategories(expenses.get(i).getcategories());
                exp_cat.add(product);
            }
            total_ex += ex_double;
        }

        for (int i = 0; i < incomes.size(); i++) {
            try {
                in_double = Double.parseDouble(incomes.get(i).getAmount());
            } catch (NumberFormatException e) {
                in_double = 0;
            }

            Log.i("checking ", "income");
            int ind = getExistLocation(incomes.get(i).getcategories(), "income");
            if (ind >= 0) {
                income_cat.get(ind).setAmount(income_cat.get(ind).getAmount() + in_double);
            } else {
                Product product = new Product();
                product.setAmount(in_double);
                product.setCategories(incomes.get(i).getcategories());
                income_cat.add(product);
            }
            total_in += in_double;
        }

        total_exp = total_ex;
        total_income = total_in;


        setData("default");
    }

    private int getExistLocation(String name, String type) {

        Log.i("checking ", name);
        if (type.equals("income")) {
            for (int i = 0; i < income_cat.size(); i++) {
                if (income_cat.get(i).getCategories().equals(name)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < exp_cat.size(); i++) {
                if (exp_cat.get(i).getCategories().equals(name)) {
                    return i;
                }
            }
        }

        return -1;

    }
}
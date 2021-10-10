package com.example.personalfinancialmanagement.ui.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.personalfinancialmanagement.Expense;
import com.example.personalfinancialmanagement.Income;
import com.example.personalfinancialmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class IncomeAdapter extends  RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {

    private ArrayList<Income> dataSet;
    private int show_button = 1;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Activity activity;
    private Location Location_cur;
    int row_index=-1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView categories;
        TextView amount;
        TextView description;
        LinearLayout relative;
        ImageView imageView;



//        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.date = (TextView) itemView.findViewById(R.id.dates);
            this.categories = (TextView) itemView.findViewById(R.id.categories);
            this.amount= (TextView) itemView.findViewById(R.id.amount);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.relative = (LinearLayout) itemView.findViewById(R.id.relative);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);




        }
    }

    public IncomeAdapter (ArrayList<Income> data, Activity activity) {
        this.activity = activity;
        this.dataSet = data;

    }

    @Override
    public IncomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_income_adapter, parent, false);



        IncomeAdapter.MyViewHolder myViewHolder = new IncomeAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final IncomeAdapter.MyViewHolder holder, final int listPosition) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        TextView date = holder.date;
        TextView categories = holder.categories;
        TextView amount = holder.amount;
        TextView description = holder.description;
        LinearLayout relative = holder.relative;
//        final ImageView money_icon = holder.money_icon;
        ImageView imageView=holder.imageView;

        //set value into textview
        date.setText(dataSet.get(listPosition).getdate());
        categories.setText(String.valueOf(dataSet.get(listPosition).getcategories()));
        amount.setText("$"+dataSet.get(listPosition).getAmount());
        description.setText(dataSet.get(listPosition).getDescription());

//        holder.money_icon.setImageResource(R.drawable.ic_baseline_money_off_24);

        switch(dataSet.get(listPosition).getcategories()){
            case "Salary":
                imageView.setImageResource(R.drawable.salary);
                break;
            case "Investment":
                imageView.setImageResource(R.drawable.investment);
                break;
            case "Revenues":
                imageView.setImageResource(R.drawable.revenue);
                break;
            default:
                imageView.setImageResource(R.drawable.misc);
                break;

        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
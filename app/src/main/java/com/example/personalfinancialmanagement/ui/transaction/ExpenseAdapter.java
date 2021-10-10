package com.example.personalfinancialmanagement.ui.transaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.example.personalfinancialmanagement.EditExpenseActivity;
import com.example.personalfinancialmanagement.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinancialmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private ArrayList<Expense> dataSet;
    private int show_button = 1;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Activity activity;
    private Location Location_cur;
    int row_index=-1;
    private ArrayList<String>   key;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView categories;
        TextView amount;
        TextView description;
        ImageView imageView;
        CardView card_view;




//        ImageView imageViewIcon;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.date = (TextView) itemView.findViewById(R.id.dates);
            this.categories = (TextView) itemView.findViewById(R.id.categories);
            this.amount= (TextView) itemView.findViewById(R.id.amount);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);




        }
    }

    public ExpenseAdapter(ArrayList<Expense> data,  Activity activity, ArrayList<String> key) {
        this.activity = activity;
        this.dataSet = data;
        this.key = key;


    }

    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_expense_adapter, parent, false);



        ExpenseAdapter.MyViewHolder myViewHolder = new ExpenseAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ExpenseAdapter.MyViewHolder holder, final int listPosition) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        TextView date = holder.date;
        TextView categories = holder.categories;
        TextView amount = holder.amount;
        TextView description = holder.description;

        ImageView imageView=holder.imageView;
        CardView card_view = holder.card_view;



        //set value into textview
        date.setText(dataSet.get(listPosition).getdate());
        categories.setText(String.valueOf(dataSet.get(listPosition).getcategories()));
        amount.setText("$"+dataSet.get(listPosition).getAmount());
        description.setText(dataSet.get(listPosition).getDescription());
//        "Food", "Entertainment", "Apparel", "Fuels", "Sports",
        switch(dataSet.get(listPosition).getcategories()){
            case "Food":
                imageView.setImageResource(R.drawable.food);
                break;
            case "Entertainment":
                imageView.setImageResource(R.drawable.entertaiment);
                break;
            case "Apparel":
                imageView.setImageResource(R.drawable.apparel);
                break;
                case "Fuels":
                imageView.setImageResource(R.drawable.fuel);
                break;
            case "Sports":
                imageView.setImageResource(R.drawable.sport);
                break;
            default:
                imageView.setImageResource(R.drawable.misc);
                break;

        }
//    imageView.setImageResource(R.drawable.ic_baseline_money_off_24);

        final Intent intent = new Intent(activity, EditExpenseActivity.class);
        ;
        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("date", dataSet.get(listPosition).getdate());
                intent.putExtra("categories", String.valueOf(dataSet.get(listPosition).getcategories()));
                intent.putExtra("amount", dataSet.get(listPosition).getAmount());
                intent.putExtra("description", dataSet.get(listPosition).getDescription());
                intent.putExtra("key", key.get(listPosition));
                activity.startActivity(intent);


            }
        });




    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
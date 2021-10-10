package com.example.personalfinancialmanagement.ui.transaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class IncomeFragment extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Income> data, pending;
    static View.OnClickListener myOnClickListener;



    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    String month_date, filterBy;
    String date;


    IntentFilter filter = new IntentFilter("com.codinginflow.EXAMPLE_ACTION");
    private BroadcastReceiver smsBroadcastReceiver=new BroadcastReceiver() {
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


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Income>();
        pending = new ArrayList<Income>();



        getDataFromFirebase();

        getActivity().registerReceiver(smsBroadcastReceiver, filter);
//        data.add(new Plan("s","s","s","s"));


//            i++;
        adapter = new IncomeAdapter(data,getActivity());


        recyclerView.setAdapter(adapter);


        //populate the adapter
        return view;
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
                if (dataSnapshot.exists()) {
                    if (filterBy.equals("Monthly")) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot areaSnapshot : snapshot.child("Income").getChildren()) {
                                Income key_Detail = areaSnapshot.getValue(Income.class);
                                data.add(key_Detail);
                            }
                        }
                    } else {
                        for (DataSnapshot areaSnapshot : dataSnapshot.child("Income").getChildren()) {
                            Income key_Detail = areaSnapshot.getValue(Income.class);
                            data.add(key_Detail);
                        }

                    }


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        if (filterBy.equals("Monthly")) {
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ", "-")).addValueEventListener(a);

        } else {
            ref.child(user.getUid()).child("Transaction_Record").child(month_date.replace(" ", "-")).child(date).addValueEventListener(a);

        }
    }

}
package com.example.personalfinancialmanagement.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.personalfinancialmanagement.HomeActivity;
import com.example.personalfinancialmanagement.ui.Adapter.SectionPageAdapter;

import com.example.personalfinancialmanagement.R;
import com.google.android.material.tabs.TabLayout;

public class TransactionFragment extends Fragment {

    View myFragment;

    ViewPager viewPager;
    TabLayout tabLayout;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        myFragment = inflater.inflate(R.layout.fragment_transaction, container, false);

        viewPager = myFragment.findViewById(R.id.viewPager);
        tabLayout = myFragment.findViewById(R.id.tabLayout);

        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter (getChildFragmentManager());

        adapter.addFragment(new ExpenseFragment(),"Expense");
        adapter.addFragment(new IncomeFragment(),"Income");

        viewPager.setAdapter(adapter);
    }
}

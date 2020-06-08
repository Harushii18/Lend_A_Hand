package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class DonorRegActivity extends AppCompatActivity {
    private CustomViewPager vpDonorReg;
    private TabLayout tbDonorReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_donor_reg);

        //initialise views
        vpDonorReg=(CustomViewPager)findViewById(R.id.vpDonorReg);
        tbDonorReg=(TabLayout)findViewById(R.id.tbDonorReg);

        //set views to be in line with tabs
        tbDonorReg.setupWithViewPager(vpDonorReg);
        //due to icons disappearing on setting up with viewpager:
        tbDonorReg.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(vpDonorReg) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        //TODO: validation on input


                        super.onTabSelected(tab);
                        //don't allow the user to move forward to next tabs, only tab 2
                        if (tab.getPosition()==0){
                            tbDonorReg.getTabAt(0).setIcon(R.drawable.indicator_selector_one);
                            tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_looks_two_next);
                            tbDonorReg.getTabAt(2).setIcon(R.drawable.indicator_selector_three);
                            tbDonorReg.getTabAt(3).setIcon(R.drawable.indicator_selector_four);

                            //don't allow clicking of these
                            LinearLayout tabStrip3 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });

                        }


                        //don't allow the user to move forward to next tabs, only tab 3
                        if (tab.getPosition()==1){
                            tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(1).setIcon(R.drawable.indicator_selector_two);
                            tbDonorReg.getTabAt(2).setIcon(R.drawable.ic_looks_3_next);
                            tbDonorReg.getTabAt(3).setIcon(R.drawable.indicator_selector_four);


                            //for the following, the true property will hide the tab
                            //false will show the tab
                            //don't allow clicking of these
                            LinearLayout tabStrip1 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip1.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip3 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });

                        }

                        //don't allow the user to move forward to next tabs, only tab 4
                        if (tab.getPosition()==2){
                            tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(2).setIcon(R.drawable.indicator_selector_three);
                            tbDonorReg.getTabAt(3).setIcon(R.drawable.ic_looks_4_next);

                            //don't allow clicking of these

                            LinearLayout tabStrip2 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                        }

                        //don't allow the user to move behind, go to next activity now
                        if (tab.getPosition()==3){
                            tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete);
                            tbDonorReg.getTabAt(3).setIcon(R.drawable.ic_progress_complete);

                            //don't allow clicking of these

                            LinearLayout tabStrip3 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout)tbDonorReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });

                        }




                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


        vpDonorReg.setAdapter(new DonorAdapter());
        tbDonorReg.setupWithViewPager(vpDonorReg);
        //disable scrolling on viewpager
        vpDonorReg.setPagingEnabled(false);
    }

    public class DonorAdapter extends PagerAdapter{
        LayoutInflater layoutInflater;
        int[] layouts={
                R.layout.reg_common_one,
                R.layout.reg_common_three,
                R.layout.reg_common_three,
                R.layout.reg_donor_final
        };

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view==(ConstraintLayout)object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View one=layoutInflater.inflate(R.layout.reg_common_one,container,false);
            View two=layoutInflater.inflate(R.layout.reg_common_two,container,false);
            View three=layoutInflater.inflate(R.layout.reg_common_three,container,false);
            View four=layoutInflater.inflate(R.layout.reg_donor_final,container,false);
            View viewarr[]={one,two,three,four};
            container.addView(viewarr[position]);
            return viewarr[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ConstraintLayout)object);

        }
    }
}

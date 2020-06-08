package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class DoneeRegActivity extends AppCompatActivity {
    private CustomViewPager vpDoneeReg;
    private TabLayout tbDoneeReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide title bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_donee_reg);

        //initialise views
        vpDoneeReg=(CustomViewPager)findViewById(R.id.vpDoneeReg);
        tbDoneeReg=(TabLayout)findViewById(R.id.tbDoneeReg);

        //set views to be in line with tabs
        tbDoneeReg.setupWithViewPager(vpDoneeReg);
        //due to icons disappearing on setting up with viewpager:
        tbDoneeReg.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(vpDoneeReg) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        //TODO: validation on input


                        super.onTabSelected(tab);
                        //don't allow the user to move forward to next tabs, only tab 2
                        if (tab.getPosition() == 0) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.indicator_selector_one);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_looks_two_next);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.indicator_selector_three);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.indicator_selector_four);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.indicator_selector_five);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.indicator_selector_six);

                            //don't allow clicking of these
                            LinearLayout tabStrip3 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip5 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip6 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip6.getChildAt(5).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                        }


                        //don't allow the user to move forward to next tabs, only tab 3
                        if (tab.getPosition() == 1) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.indicator_selector_two);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_looks_3_next);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.indicator_selector_four);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.indicator_selector_five);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.indicator_selector_six);


                            //for the following, the true property will hide the tab
                            //false will show the tab
                            //don't allow clicking of these
                            LinearLayout tabStrip1 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip1.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip3 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });

                        }

                        //don't allow the user to move forward to next tabs, only tab 4
                        if (tab.getPosition() == 2) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.indicator_selector_three);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.ic_looks_4_next);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.indicator_selector_five);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.indicator_selector_six);

                            //don't allow clicking of these

                            LinearLayout tabStrip2 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip4 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                        }

                        //don't allow the user to move behind, go to next activity now
                        if (tab.getPosition() == 3) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.indicator_selector_four);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.ic_looks_5_next);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.indicator_selector_six);

                            //don't allow clicking of these

                            LinearLayout tabStrip3 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip5 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                        }

                        //don't allow the user to move behind, go to next activity now
                        if (tab.getPosition() == 4) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.indicator_selector_five);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.ic_looks_6_next);

                            //don't allow clicking of these

                            LinearLayout tabStrip4 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip6 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip6.getChildAt(5).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                        }

                        if (tab.getPosition() == 5) {
                            tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(3).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(4).setIcon(R.drawable.ic_progress_complete);
                            tbDoneeReg.getTabAt(5).setIcon(R.drawable.ic_progress_complete);


                            //don't allow clicking of these

                            LinearLayout tabStrip5 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                            LinearLayout tabStrip6 = ((LinearLayout) tbDoneeReg.getChildAt(0));
                            tabStrip6.getChildAt(5).setOnTouchListener(new View.OnTouchListener() {
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

        vpDoneeReg.setAdapter(new DoneeAdapter());
        tbDoneeReg.setupWithViewPager(vpDoneeReg);
        //disable scrolling on viewpager
        vpDoneeReg.setPagingEnabled(false);
    }

    public void DoneeLogin(View view) {
        //go to log in activity
        Intent intent = new Intent(this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }


    public class DoneeAdapter extends PagerAdapter {
        LayoutInflater layoutInflater;
        int[] layouts={
                R.layout.reg_common_one,
                R.layout.reg_common_one_point_five,
                R.layout.reg_common_three,
                R.layout.reg_common_three,
                R.layout.reg_donee_five,
                R.layout.reg_donee_final
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
            View two=layoutInflater.inflate(R.layout.reg_common_one_point_five,container,false);
            View three=layoutInflater.inflate(R.layout.reg_common_two,container,false);
            View four=layoutInflater.inflate(R.layout.reg_common_three,container,false);
            View five=layoutInflater.inflate(R.layout.reg_donee_five,container,false);
            View six=layoutInflater.inflate(R.layout.reg_donee_final,container,false);
            View viewarr[]={one,two,three,four,five,six};
            container.addView(viewarr[position]);
            return viewarr[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ConstraintLayout)object);

        }
    }
}


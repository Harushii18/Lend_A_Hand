package com.example.lendahand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;

    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int arrImages[] = {
            R.drawable.ic_lockdownicon,
            R.drawable.ic_donateboy,
            R.drawable.ic_donatewoman,
            R.drawable.ic_donateicon,
            R.drawable.ic_join_us
    };

    int arrHeadings[] = {
            R.string.first_screen_title,
            R.string.second_screen_title,
            R.string.third_screen_title,
            R.string.fourth_screen_title,
            R.string.fifth_screen_title
    };

    int arrDescriptions[] = {
            R.string.first_screen_desc,
            R.string.second_screen_desc,
            R.string.third_screen_desc,
            R.string.fourth_screen_desc,
            R.string.fifth_screen_desc
    };

    @Override
    public int getCount() {
        //returns number of "slides"
        return arrHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout, container, false);

        //Hooks

        //get 3 main components on layout
        ImageView imgSlider = view.findViewById(R.id.img_slider);
        TextView txtHeading = view.findViewById(R.id.txt_heading_slider);
        TextView txtDescription = view.findViewById(R.id.txt_slider_desc);
        if (position==2 || position==4){
            imgSlider.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            imgSlider.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        //sets the components to the array contents
        imgSlider.setImageResource(arrImages[position]);
        txtHeading.setText(arrHeadings[position]);
        txtDescription.setText(arrDescriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}

package com.example.lendahand;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    //need to add donate image when they allow me to download it
    //put heart icon for now
    int arrImages[] = {
            R.drawable.ic_lockdownicon,
            R.drawable.ic_ob_donate,
            R.drawable.ic_receiveicon,
            R.drawable.ic_hearticon,
            R.drawable.ic_ob_lendahand
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
        ImageView imgSlider = (ImageView)view.findViewById(R.id.img_slider);
        TextView txtHeading = (TextView)view.findViewById(R.id.txt_heading_slider);
        TextView txtDescription = (TextView)view.findViewById(R.id.txt_slider_desc);


        /*
        btnGetStarted.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //switches to main activity
                Intent intent = new Intent(context,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        */

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

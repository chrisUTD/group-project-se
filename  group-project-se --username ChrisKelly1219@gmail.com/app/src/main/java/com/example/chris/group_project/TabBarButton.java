package com.example.chris.group_project;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Jonny on 10/20/14.
 */
public class TabBarButton extends Button {

    private Color background = new Color();

    private String label;
    private Drawable defaultImage;
    private Drawable selectedImage;
    private boolean isSelected;

    private TabBarButton(Context context){
        super(context);
    }

    public TabBarButton(Context context, String label, Drawable defaultImage, Drawable selectedImage){
        super(context);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.tab_bar_button, null);


        isSelected = false;
        this.defaultImage = defaultImage;
        this.selectedImage = selectedImage;
        this.label = label;

        view.findViewById(R.id.tab_button_indicator).setVisibility(View.INVISIBLE);
        ((ImageView)view.findViewById(R.id.tab_button_image)).setImageDrawable(defaultImage);
        getRootView().setBackgroundColor(getContext().getResources().getColor(R.color.tab_bar_button_background));

    }




    public TabBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //--- Additional custom code --
    }

    public TabBarButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //--- Additional custom code --
    }

    public void setSelected(boolean shouldSetSelected){
        if (shouldSetSelected && !isSelected){
            isSelected = true;
            if (selectedImage != null){
                findViewById(R.id.tab_button_indicator).setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.tab_button_image)).setImageDrawable(selectedImage);
                getRootView().setBackgroundColor(getContext().getResources().getColor(R.color.tab_bar_button_selected_background));
            }
        }
        else if (!shouldSetSelected && isSelected){
            findViewById(R.id.tab_button_indicator).setVisibility(View.INVISIBLE);
            ((ImageView)findViewById(R.id.tab_button_image)).setImageDrawable(defaultImage);
            getRootView().setBackgroundColor(getContext().getResources().getColor(R.color.tab_bar_button_background));
        }
        invalidate();
        requestLayout();
    }

}

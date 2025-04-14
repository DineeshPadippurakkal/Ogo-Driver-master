package webtech.com.courierdriver.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/*
Created by ​
Hannure Abdulrahim


on 4/18/2019.
 */






public class AvenirNextCondensedEditText extends AppCompatEditText {


    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public AvenirNextCondensedEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AvenirNextCondensedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public AvenirNextCondensedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "roboto.ttf");
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "roboto.ttf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "roboto.ttf");
        super.setTypeface(tf);
    }
}

package webtech.com.courierdriver.utilities;
/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 4/9/2018.
 */


import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }
        public char charAt(int index) {
            return '*'; // This is the important part
        }
        public int length() {
            return mSource.length(); // Return default
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
};
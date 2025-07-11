package com.example.eventplanner.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

public class TriStateCheckBox extends MaterialCheckBox {
    public TriStateCheckBox(Context context) {
        super(context);
    }

    public TriStateCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TriStateCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        switch (getCheckedState()) {
            case MaterialCheckBox.STATE_CHECKED:
                setCheckedState(MaterialCheckBox.STATE_UNCHECKED);
                break;
            case MaterialCheckBox.STATE_UNCHECKED:
                setCheckedState(MaterialCheckBox.STATE_INDETERMINATE);
                break;
            case MaterialCheckBox.STATE_INDETERMINATE:
                setCheckedState(MaterialCheckBox.STATE_CHECKED);
                break;
        }
    }
}

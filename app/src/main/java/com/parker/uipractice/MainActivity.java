package com.parker.uipractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private SwitchView mDefaultSwitchView;
    private GradientSwitchView mGradientSwitchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDefaultSwitchView = findViewById(R.id.default_switch);
        mGradientSwitchView = findViewById(R.id.gradient_switch);
        mDefaultSwitchView.setOnSwitchChangeListener(new SwitchView.OnSwitchChangeListener() {
            @Override
            public void onSwitchChange(boolean switchStatus) {
                Log.d(TAG, "onSwitchChange: " + switchStatus);
            }
        });

        mGradientSwitchView.setOnSwitchChangeListener(new GradientSwitchView.OnSwitchChangeListener() {
            @Override
            public void onSwitchChange(boolean switchStatus) {
                Log.d(TAG, "onSwitchChange: " + switchStatus);
            }
        });
    }
}

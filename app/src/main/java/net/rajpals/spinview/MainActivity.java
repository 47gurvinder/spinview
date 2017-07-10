package net.rajpals.spinview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


/**
 * Author: Gurwinder Singh
 * Email: 47gurvinder@gmail.com
 * 07-07-17
 **/
public class MainActivity extends AppCompatActivity {
    SpinView mSpinView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinView = (SpinView) findViewById(R.id.mpie_view);
        findViewById(R.id.spin_the_wheel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinView.spin();
            }
        });
        findViewById(R.id.check_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float mLastAngle = mSpinView.getAngle();
                float item = mSpinView.getSelectedItem();
                Toast.makeText(getApplicationContext(), "rotation: " + mLastAngle + " item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

       int[] ints = new int[]{
                R.drawable.bg1,
                R.drawable.bg2,
                R.drawable.bg3,
                R.drawable.bg4,
                R.drawable.bg5,
                R.drawable.bg6,
                R.drawable.bg7,
                R.drawable.bg8,
                R.drawable.bg9,
                R.drawable.bg10,
                R.drawable.bg11,
                R.drawable.bg12
        };
        /*int[] ints = new int[]{
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4,
                R.color.color5,
                R.color.color6,
                R.color.color7,
                R.color.color8,
                R.color.color9,
                R.color.color10,
                R.color.color11,
                R.color.color12
        };*/

        mSpinView.setShowDefaultSelectedItem(true);
        mSpinView.setImageList(ints);
        mSpinView.setOnSpinChangeListener(new SpinView.OnSpinChangeListener() {
            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(), "onStart", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete(int selectedPosition) {
                Toast.makeText(getApplicationContext(), "onComplete: "+selectedPosition, Toast.LENGTH_SHORT).show();

            }
        });


    }

}

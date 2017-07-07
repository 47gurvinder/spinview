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
        mSpinView.setImageList(ints);


    }

}

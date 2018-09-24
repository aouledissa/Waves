package com.github.andromedcodes.demo.waves;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.andromedcodes.demo.R;
import com.github.andromedcodes.waves.Waves;

//@Shimmerize(color = R.color.colorPrimary, views = {R.id.sampleImg, R.id.sampleTitle, R.id.sampleDesc})
public class MainActivity extends AppCompatActivity {

    private TextView sampleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sampleTitle = findViewById(R.id.sampleTitle);

        Waves.on(sampleTitle)
                .on(findViewById(R.id.sampleImg))
                .on(findViewById(R.id.sampleDesc))
                .on(findViewById(R.id.sampleDesc2))
                //.leader(sampleTitle)
                .stopAllAtOnce(false)
                //.apply(this, R.color.colorPrimary, 1300, false);
                .start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sampleTitle.setText("It's working!!!");
            }
        }, 2000);
    }
}

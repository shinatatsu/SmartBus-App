package com.example.root.finalbletest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomerActivity extends Activity implements View.OnClickListener {

    private TextView city_tex,traffic_tex;
    private Button cityphone_btn,trafficphone_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);


        city_tex = (TextView)findViewById(R.id.tainancity);
        traffic_tex = (TextView)findViewById(R.id.tainantraffic);

        cityphone_btn = (Button)findViewById(R.id.tananCity_phone);
        cityphone_btn.setOnClickListener(this);
        trafficphone_btn = (Button)findViewById(R.id.tainanTraffic_phone);
        trafficphone_btn.setOnClickListener(this);

        city_tex.setText("台南市市政府：06-12435678");
        traffic_tex.setText("台南市交通局：06-44213457");


    }

    public void onClick(View v){
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        if(v.getId() == cityphone_btn.getId()){
            it.setData(Uri.parse("tel:06-12435678"));
        }else
            it.setData(Uri.parse("tel:06-44213457"));

        startActivity(it);
    }
}

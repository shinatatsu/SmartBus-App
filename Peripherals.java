package com.example.root.finalbletest;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Peripherals extends ListActivity implements OnClickListener{

  // TODO(g-ortuno): Implement heart rate monitor peripheral
  private static final String[] PERIPHERALS_NAMES = new String[]{"舒適度查詢", "周遭站點"};
  private static final String[] PERIPHERALS_Situation = new String[]{"開始測試目前搭乘之公車舒適程度", "可以查詢附近公車站點及其時刻表"};
  List<HashMap<String, String>> list1 = new ArrayList<>();
  public final static String EXTRA_PERIPHERAL_INDEX = "PERIPHERAL_INDEX";
  private Button BusSituation_btn,BusSchedual_btn,BusStation_btn,Customer_btn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_peripherals_list);

    BusSituation_btn = (Button)findViewById(R.id.button_BusSituation);
    BusSituation_btn.setOnClickListener(this);
//    BusSchedual_btn = (Button)findViewById(R.id.button_BusTimeSchedual);
//    BusSchedual_btn.setOnClickListener(this);
    Customer_btn = (Button)findViewById(R.id.button_CustomerService);
    Customer_btn.setOnClickListener(this);
    BusStation_btn = (Button)findViewById(R.id.button_BusStation);
    BusStation_btn.setOnClickListener(this);


    for(int i=0;i<2;i++){
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("NAMES",PERIPHERALS_NAMES[i]);
      hashMap.put("Situation",PERIPHERALS_Situation[i]);
      list1.add(hashMap);
    }
//    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//        /* layout for the list item */ android.R.layout.simple_list_item_2,
//        /* id of the TextView to use */ android.R.id.text1,
//        /* values for the list */ PERIPHERALS_NAMES);
//    setListAdapter(adapter);
    ListAdapter listAdapter = new SimpleAdapter(
            this,
            list1,
            android.R.layout.simple_expandable_list_item_2,
            new String[]{"NAMES","Situation"},
            new int[]{android.R.id.text1,android.R.id.text2});

      setListAdapter(listAdapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Intent intent = new Intent(this, Peripheral.class);					//頁面跳轉、傳遞參數
    intent.putExtra(EXTRA_PERIPHERAL_INDEX, position);
    startActivity(intent);
  }

  public void onClick(View v) {

    if(v.getId() == BusSituation_btn.getId()){
      Intent intent1 = new Intent(this, Peripheral.class);					//頁面跳轉、傳遞參數
      intent1.putExtra(EXTRA_PERIPHERAL_INDEX,0);
      startActivity(intent1);
    }else if(v.getId() == BusStation_btn.getId()){
      Intent intent1 = new Intent(this, MapsActivity.class);					//頁面跳轉、傳遞參數
//      intent1.putExtra(EXTRA_PERIPHERAL_INDEX,1);
      startActivity(intent1);
    }else if(v.getId() == Customer_btn.getId()) {
      Intent intent1 = new Intent();                    //頁面跳轉、傳遞參數
      intent1.setClass(this, CustomerActivity.class);
//      intent1.putExtra(EXTRA_PERIPHERAL_INDEX,2);
      startActivity(intent1);
    }
  }

}
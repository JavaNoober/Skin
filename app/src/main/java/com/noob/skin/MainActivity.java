package com.noob.skin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    int themeState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeState = getIntent().getIntExtra("themeState", 0);
        if(themeState == 0){
            setTheme(R.style.LightTheme);
        }else {
            setTheme(R.style.BlackTheme);
        }
        LayoutInflater.from(this).setFactory(new LayoutInflater.Factory() {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                Log.e("MainActivity", "name :" + name);
                int count = attrs.getAttributeCount();
                for (int i = 0; i < count; i++) {
                    Log.e("MainActivity", "AttributeName :" + attrs.getAttributeName(i) + "AttributeValue :"+ attrs.getAttributeValue(i));
                }
                return null;
            }
        });

        setContentView(R.layout.activity_main);

        //listView中使用
        LayoutInflater.from(this).inflate(R.layout.item, null);




//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent intent = getIntent();
//                if(themeState == 0){
//                    intent.putExtra("themeState", 1);
//                }else {
//                    intent.putExtra("themeState", 0);
//                }
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//            }
//        });
//
//
//        ResourceLoad.loadUnInstallApkRes(this, (ImageView) findViewById(R.id.image));

    }
}

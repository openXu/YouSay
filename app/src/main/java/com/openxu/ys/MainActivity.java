package com.openxu.ys;

import android.content.Intent;
import android.widget.Button;

import com.iflytek.voicedemo.IatDemo;

public class MainActivity extends BaseActivity {


    Button btn1, btn2,btn3;


    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn1.setOnClickListener(v->{
            startActivity( new Intent(mContext, IatDemo.class));
        });
        btn2.setOnClickListener(v->{
            startActivity( new Intent(mContext, YyzxActivity.class));
        });
        btn3.setOnClickListener(v->{
            startActivity( new Intent(mContext, YzsActivity.class));
        });
    }


}

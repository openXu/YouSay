package com.openxu.ys;

import android.content.Intent;

import com.iflytek.voicedemo.IatDemo;
import com.openxu.offLine.DictationOffLineActivity;
import com.openxu.offLine.DictationOffLineTestActivity;

public class MainActivity extends BaseActivity {



    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.btn1).setOnClickListener(v->{
            startActivity( new Intent(mContext, IatDemo.class));
        });
        findViewById(R.id.btn2).setOnClickListener(v->{
            startActivity( new Intent(mContext, DictationTestActivity.class));
        });
        findViewById(R.id.btn3).setOnClickListener(v->{
            startActivity( new Intent(mContext, DictationActivity.class));
        });
        findViewById(R.id.btn4).setOnClickListener(v->{
            startActivity( new Intent(mContext, DictationOffLineTestActivity.class));
        });
        findViewById(R.id.btn5).setOnClickListener(v->{
            startActivity( new Intent(mContext, DictationOffLineActivity.class));
        });
    }


}

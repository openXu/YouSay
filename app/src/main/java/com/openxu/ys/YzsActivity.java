package com.openxu.ys;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.speech.util.JsonParser;
import com.openxu.oxlib.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class YzsActivity extends BaseActivity {

    TextView tv_result;
    ScrollView sv_result;

    // 语音听写对象
    private SpeechRecognizer mIat;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_yzs;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_result = findViewById(R.id.tv_result);
        sv_result = findViewById(R.id.sv_result);
    }

    @Override
    protected void init_Data() {
        //初始化识别无UI识别对象
        //使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(mContext, i -> LogUtil.w(TAG, "初始化识别无UI识别对象："+i));
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIat.setParameter( SpeechConstant.SUBJECT, null );
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //此处engineType为“cloud”
        mIat.setParameter( SpeechConstant.ENGINE_TYPE, "cloud");
        //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"1");
        //开始识别，并设置监听器
        mIat.startListening(mRecognizerListener);
    }

    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {
            LogUtil.w(TAG, "?????????????当前正在说话，音量大小：" + volume);
        }
        @Override
        public void onBeginOfSpeech() {
            LogUtil.i(TAG, ">>>>>>>>>>>>>开始说话"+Thread.currentThread());
//            ToastAlone.show("请开始说话");
        }
        @Override
        public void onEndOfSpeech() {
            LogUtil.e(TAG, "<<<<<<<<<<<<<结束说话");
            //开始识别，并设置监听器
            mIat.startListening(mRecognizerListener);
//            ToastAlone.show("结束说话");
        }
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            apendResult(recognizerResult, isLast);
        }
        @Override
        public void onError(SpeechError speechError) {
            String error = "onError发生错误："+speechError.getErrorCode()+" "+speechError.getErrorDescription();
//            ToastAlone.show(error);
            LogUtil.e(TAG, error);
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            try {
                LogUtil.v(TAG, "onEvent事件：" + i + "  " + i1 + "  " + i2 + "  " + bundle.toString());
            }catch (Exception e){
            }
        }
    };

    private void apendResult(RecognizerResult results, boolean isLast){
        LogUtil.i(TAG, "onResult结果："+isLast+results.getResultString());
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn"); // 读取json结果中的sn字段
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_result.setText(tv_result.getText().toString()+text);
        if (isLast) {
            // TODO 最后的结果
        }
        //设置默认滚动到底部
        sv_result.post(() -> sv_result.fullScroll(ScrollView.FOCUS_DOWN));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != mIat ){
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

}

package com.openxu.ys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.speech.util.JsonParser;
import com.openxu.oxlib.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class YyzxActivity extends BaseActivity {


    TextView tv_pro, tv_result;
    Button btn_start;
    ScrollView sv_pro,sv_result;

    // 语音听写对象
    private SpeechRecognizer mIat;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_yyzx;
    }

    @Override
    protected void initView() {
        super.initView();

        tv_pro = findViewById(R.id.tv_pro);
        tv_result = findViewById(R.id.tv_result);
        sv_pro = findViewById(R.id.sv_pro);
        sv_result = findViewById(R.id.sv_result);
        btn_start = findViewById(R.id.btn_start);

        btn_start.setOnClickListener(v->{
            //开始识别，并设置监听器
            int ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                LogUtil.e(TAG, "听写失败,错误码：" + ret);
            } else {
            }
        });

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

        //2.离线听写 离线听写与在线听写主要差别在于引擎类型和听写资源
        //此处engineType为“local”
//        mIat.setParameter( SpeechConstant.ENGINE_TYPE, "local");
//        if ("local".equals(SpeechConstant.TYPE_LOCAL)) {
//            mIat.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());// 设置本地识别资源
//        }
//        resourcce设置种包含两个资源，common.jet 和 sms_16k.jet, 从资源配置中可以看出，目前离线听写仅支持16k音频

//        private String getResourcePath(){
//            StringBuffer tempBuffer = new StringBuffer();
//            //识别通用资源
//            tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
//            tempBuffer.append(";");
//            tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
//            //识别8k资源-使用8k的时候请解开注释
//            return tempBuffer.toString();
//        }
//        Copy
//        在线听写应用可以通过上传词典（又称个性化用户热词），提高听写的匹配率，参考后面更新词典的章节。


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

    }

    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {
            LogUtil.w(TAG, "?????????????当前正在说话，音量大小：" + volume);
            appendPro("当前正在说话，音量大小： "+ volume);
        }
        @Override
        public void onBeginOfSpeech() {
            LogUtil.i(TAG, ">>>>>>>>>>>>>开始说话"+Thread.currentThread());
            appendPro(">>>>>>>>>>>>>开始说话");
//            ToastAlone.show("请开始说话");
        }
        @Override
        public void onEndOfSpeech() {
            LogUtil.e(TAG, "<<<<<<<<<<<<<结束说话");
            appendPro("<<<<<<<<<<<<<结束说话");
//            ToastAlone.show("结束说话");
        }
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            appendPro("结果："+isLast+recognizerResult.getResultString());
            apendResult(recognizerResult, isLast);
        }
        @Override
        public void onError(SpeechError speechError) {
            String error = "onError发生错误："+speechError.getErrorCode()+" "+speechError.getErrorDescription();
            appendPro(error);
//            ToastAlone.show(error);
            LogUtil.e(TAG, error);
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            try {
                LogUtil.v(TAG, "onEvent事件：" + i + "  " + i1 + "  " + i2 + "  " + bundle.toString());
                appendPro("onEvent事件：" + i + "  " + i1 + "  " + i2 + "  " + bundle.toString());
            }catch (Exception e){
            }
        }
    };

    private void appendPro(String text) {
        tv_pro.setText(tv_pro.getText().toString()+"\n"+text);
        sv_pro.post(() -> sv_pro.fullScroll(ScrollView.FOCUS_DOWN));
    }
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

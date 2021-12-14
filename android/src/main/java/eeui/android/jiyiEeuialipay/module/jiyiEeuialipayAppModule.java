package eeui.android.jiyiEeuialipay.module;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.OpenAuthTask;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import app.eeui.framework.extend.base.WXModuleBase;

public class jiyiEeuialipayAppModule extends WXModuleBase {

    @JSMethod(uiThread = false)
    public void alipayLogin(String appid, JSCallback callback) {
        // 传递给支付宝应用的业务参数  ss
        final Map<String, String> bizParams = new HashMap<>();
        bizParams.put("url", "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id"+ appid +"&scope=auth_user&state=init");
        // 支付宝回跳到您的应用时使用的 Intent Scheme。
        // 请设置为一个不和其它应用冲突的值，并在 AndroidManifest.xml 中为 AlipayResultActivity 的 android:scheme 属性
        // 指定相同的值。实际使用时请勿设置为 __alipaysdkdemo__ 。
        // 如果不设置，OpenAuthTask.execute() 在用户未安装支付宝，使用网页完成业务流程后，将无法回跳至您的应用。
        final String scheme = "__alipaysdkdemo__";
        // 防止在支付宝 App 被强行退出等意外情况下，OpenAuthTask.Callback 一定时间内无法释放，导致
        // Activity 泄漏
        final WeakReference<Activity> ctxRef = new WeakReference<>((Activity) mWXSDKInstance.getContext());
        // 唤起授权业务
        final OpenAuthTask task = new OpenAuthTask((Activity)mWXSDKInstance.getContext());
        task.execute(
                scheme,    // Intent Scheme
                OpenAuthTask.BizType.AccountAuth, // 业务类型
                bizParams, // 业务参数
                new OpenAuthTask.Callback() {
                    @Override
                    public void onResult(int i, String s, Bundle bundle) {
                        final Context ref = ctxRef.get();
                        if (ref != null) {
                            Log.d("JY", i + ";" + s + ";" + bundle.toString());
                            JSONObject result = new JSONObject();
                            result.put("errorCode", "0");

                            JSONObject data = new JSONObject();
                            data.put("code", i);
                            data.put("msg", s);
                            data.put("code", bundle.getString("auth_code"));
                            result.put("msg", "登录成功");
                            result.put("data", data);
                            callback.invoke(result);
                        }else  {
                            JSONObject result = new JSONObject();
                            result.put("errorCode", "1");
                            result.put("msg", "未获取到数据");
                            callback.invoke(result);
                        }
                    }
                }, // 业务结果回调
                true); // 是否需要在用户未安装支付宝 App 时，使用 H5 中间页中转。建议设置为 true。
    }

    /**
     * 简单演示
     * @param msg
     */
    @JSMethod
    public void simple(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 回调演示
     * @param msg
     * @param callback
     */
    @JSMethod
    public void call(final String msg, final JSCallback callback) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
        localBuilder.setTitle("demo");
        localBuilder.setMessage(msg);
        localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.invoke("返回：" + msg); //多次回调请使用invokeAndKeepAlive
                }
            }
        });
        AlertDialog dialog = localBuilder.setCancelable(false).create();
        dialog.show();
    }

    /**
     * 同步返回演示
     * @param msg
     * @return
     */
    @JSMethod(uiThread = false)
    public String retMsg(String msg) {
        return "返回：" + msg;
    }
}

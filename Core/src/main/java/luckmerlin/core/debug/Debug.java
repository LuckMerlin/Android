package luckmerlin.core.debug;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Debug {
    private static boolean mPrint=true;

    private Debug(){
    }

    private final static String TAG = "LM";

    public static void D(String msg) {
        D(null, msg);
    }

    public static void D(String tag, Object msgObj) {
        String msg=null!=msgObj?msgObj.toString():null;
        if (null!=msg){
            Log.d(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG, msg);
        }
    }

    public static void W(String msg) {
        W(null, msg);
    }

    public static void W(String tag, Object msgObj) {
        String msg=null!=msgObj?msgObj.toString():null;
        if (null!=msg){
            Log.w(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG, msg);
        }
    }

    public static void TD(Object msg,Object encrypt) {
        TD(null,msg,encrypt);
    }

    public static void TD(String tag, Object msg,Object encrypt) {
        T(tag,msg,encrypt);
    }

    public static void TW(Object msg,Object encrypt) {
        T(msg,encrypt);
    }

    public static void TW(String tag, String msg,String encrypt) {
        T(tag,msg,encrypt);
    }

    public static void TE(Object msg,Object encrypt) {
        T(msg,encrypt);
    }

    public static void TE(String tag, Object msg,Object encrypt) {
        T(tag,msg,encrypt);
    }

    private static void T(Object msg,Object encrypt) {
        T(null, msg,encrypt);
    }

    private static void T(String tag, Object msg,Object encrypt) {
        boolean enableEncryptLog=mPrint;
        if (!enableEncryptLog&&null!=msg) {
            D(tag, msg);
        }
        if (enableEncryptLog) {
            Log.d(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG,(null!=msg?msg:"")+" "+ (null != encrypt ? encrypt : ""));
        }
    }

    public static void E(Object msg) {
        E(msg, null);
    }

    public static void E(Object msg, Throwable throwable) {
        E(null, msg, throwable);
    }

    public static void E(String tag, Object msgObj, Throwable throwable) {
        tag = null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG;
        String msg = null != msgObj ? msgObj.toString() : null;
        if (null!=msg){
            if (null!=throwable){
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                Log.e(tag, msg+" Exception stack trace:\n" + stringWriter);
            }else{
                Log.e(tag, msg);
            }
        }
    }
}

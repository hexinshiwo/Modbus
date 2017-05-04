package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xinhe002614 on 2017/4/21.
 */

public class CommonUnit {
    private static Toast toast = null;

    /**
     * 显示最后的Toast
     */
    public static void showToast(Context context, String msg, int length) {
        if (toast == null) toast = Toast.makeText(context, msg, length);
        else toast.setText(msg);
        toast.show();
    }

    // 转化字符串为十六进制编码
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    // 转化十六进制编码为字符串
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}

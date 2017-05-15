package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static com.example.xinhe002614.modbustest.Unit.CommonUnit.showToast;

/**
 * Created by xinhe002614 on 2017/5/7.
 */

public class SocketUnit {
    private Vector<ConnectSocket> vector = new Vector();
    public static int TAG = 0;//标志位
    public Context context;
    public static final byte[] DATE_FROM_COIL = null;
    public static final byte[] REQ_PRIMARY_COIL = {0x3A, 0x05, 0x04, 0x00, 0x00, 0x00, 0x10};//向原边寄存器发送的命令
    public static final byte[] REQ_SECOND_COIL = {0x3A, 0x05, 0x04, 0x00, 0x10, 0x00, 0x18};//向副边寄存器发送的命令

    public SocketUnit(int tag, Context context) {
        this.TAG = tag;
        this.context = context;
    }

    public Socket connect() {
        Socket socket = null;
        try {
            ServerSocket e = new ServerSocket(3325);
            System.out.println("Waiting");

            socket = e.accept();

            System.out.println("Connecting success");
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        return socket;
    }

    public void add(ConnectSocket csSocket) {
        this.vector.add(csSocket);
    }

    public byte[] getDataFromCoil() {
        return DATE_FROM_COIL;
    }
}

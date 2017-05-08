package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

import static com.example.xinhe002614.modbustest.Unit.CommonUnit.showToast;

/**
 * Created by xinhe002614 on 2017/5/7.
 */

public class ConnectSocket extends Thread {
    Socket socket;
    Context context;
    DataInputStream dis = null;
    DataOutputStream dos=null;
    Vector<ConnectSocket> vector;


    public ConnectSocket(Socket s, Vector<ConnectSocket> vector,Context context) {
        this.socket = s;
        this.vector = vector;
        this.context=context;
        Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
    }

    public void out( byte[] obj) {
        try {
            if(dos!=null)
            {
                dos.write(obj);
                dos.flush();
            }
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void run() {
        try {

            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException var13) {
            var13.printStackTrace();
        }

        try {
            if(SocketUnit.DATE_FROM_COIL!=null)
            {

                Toast.makeText(context, "接收到数据" + Arrays.toString(SocketUnit.DATE_FROM_COIL), Toast.LENGTH_LONG).show();
                Looper.loop();
            }


            dis.read( SocketUnit.DATE_FROM_COIL);//将接收到的数据存入SocketUnit之中
            this.sendMessage(this, SocketUnit.TAG);//根据TAG来发送数据
        } catch (IOException var12) {
            var12.printStackTrace();
        }


    }


    public void sendMessage(ConnectSocket cs, int  tag) {
        if(tag==0)
    {
        out(SocketUnit.REQ_PRIMARY_COIL);// 发送消息给原边
    }
        else
    {
        out(SocketUnit.REQ_SECOND_COIL);// 发送消息给副边
    }
    }

    private void delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep((long)ms);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

    }
}

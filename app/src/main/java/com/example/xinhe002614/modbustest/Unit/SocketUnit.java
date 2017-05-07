package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.xinhe002614.modbustest.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

import static com.example.xinhe002614.modbustest.Unit.CommonUnit.showToast;

/**
 * Created by xinhe002614 on 2017/5/7.
 */

public class SocketUnit {
    private Vector<ConnectSocket> vector = new Vector();
    public static  int TAG=0;//标志位
    public  Context context;
    public static final byte[] DATE_FROM_COIL=null;
    public static final byte[] REQ_PRIMARY_COIL = {0x3A, 0x05, 0x04, 0x00, 0x00, 0x00, 0x10};//向原边寄存器发送的命令
    public static final byte[] REQ_SECOND_COIL = {0x3A, 0x05, 0x04, 0x00, 0x10, 0x00, 0x18};//向副边寄存器发送的命令
    public SocketUnit(int tag,Context context) {
        this.TAG=tag;
        this.context=context;

    }
   public void connect()
   {
       try {
           ServerSocket e = new ServerSocket(3325);

           while(true) {
               Looper.prepare();
               System.out.println("Waiting");
               Toast.makeText(context, "连接中", Toast.LENGTH_LONG).show();
               Looper.loop();
               Socket socket = e.accept();
               System.out.println("Connecting");
               Looper.prepare();
               Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
               Looper.loop();
               ConnectSocket cs = new ConnectSocket(socket, this.vector);
               this.add(cs);
               cs.start();
               if(DATE_FROM_COIL!=null)
               {
                   Looper.prepare();
                   Toast.makeText(context, "接收到数据" + Arrays.toString(DATE_FROM_COIL), Toast.LENGTH_LONG).show();
                   Looper.loop();
               }

           }
       } catch (IOException var4) {
           var4.printStackTrace();
       }

   }
    public void add(ConnectSocket csSocket) {
        this.vector.add(csSocket);
    }
    public byte[] getDataFromCoil()
    {
        return DATE_FROM_COIL;
    }
}

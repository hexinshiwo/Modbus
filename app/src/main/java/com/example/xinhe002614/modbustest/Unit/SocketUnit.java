package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

import static com.example.xinhe002614.modbustest.MainActivity.READ_COIL_1;
import static com.example.xinhe002614.modbustest.MainActivity.READ_COIL_2;
import static com.example.xinhe002614.modbustest.MainActivity.handler;

/**
 * Created by xinhe002614 on 2017/5/7.
 */

public class SocketUnit {
    public Context context;
    public static final byte[] REQ_PRIMARY_COIL = {0x3A, 0x05, 0x04, 0x00, 0x00, 0x00, 0x10};//向原边寄存器发送的命令
    public static final byte[] REQ_SECOND_COIL = {0x3A, 0x05, 0x04, 0x00, 0x10, 0x00, 0x18};//向副边寄存器发送的命令
    private DataInputStream dis;
    private DataOutputStream dos;
    private byte readBuffer[] = new byte[60];
    public String Sinput_rate, Scoil_elect_1, Sinput_elect, Sinput_voltage;
    public String Soutput_rate, Scoil_elect_2, SBUCK_voltage, SBUCK_elect, Stemp;
    public Float Sspeed;
    public int mport = 0;

    public SocketUnit(Context context) {
        this.context = context;
    }

    public Socket connect(int port) {
        Socket socket = null;
        try {
            ServerSocket e = new ServerSocket(port);

            System.out.println("Waiting");
            socket = e.accept();
            System.out.println("Connecting success");

        } catch (IOException var4) {
            var4.printStackTrace();
        }
        return socket;
    }

    public void sendData(Socket socket, int tag) {
        if (socket != null) {
            int count = 0;
            try {
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                if (tag == 0) dos.write(REQ_PRIMARY_COIL);// 发送消息给原边
                else dos.write(REQ_SECOND_COIL);// 发送消息给副边
                dos.flush();
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                count = dis.read(readBuffer);
                if (count != -1) {
                    mport = socket.getPort();
                    receiveData(readBuffer, tag, mport);
                } else {
                    //System.out.println("未接收到数据");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendControl(Socket socket, byte[] bytes) {
        if (socket != null) {
            try {
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                if (bytes.length == 8) dos.write(bytes);
                else {
                    //发送速度

                }
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveData(byte[] bytes, int tag, int port) {
        if (tag == 0) {
            if (bytes[0] == 4) {
                byte Pin[] = {bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9]};
                byte I1[] = {bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15], bytes[16], bytes[17]};
                byte Iin[] = {bytes[18], bytes[19], bytes[20], bytes[21], bytes[22], bytes[23], bytes[24], bytes[25]};
                byte Vin[] = {bytes[26], bytes[27], bytes[28], bytes[29], bytes[30], bytes[31], bytes[32], bytes[33]};
                Sinput_rate = String.valueOf(getDouble(Pin));
                Scoil_elect_1 = String.valueOf(getDouble(I1));
                Sinput_elect = String.valueOf(getDouble(Iin));
                Sinput_voltage = String.valueOf(getDouble(Vin));
                mport = port;

                Message msg = Message.obtain();
                msg.what = READ_COIL_1;
                handler.sendMessage(msg);//用handler在UI线程执行
            }
        } else {
            if (bytes[0] == 4) {
                byte Vnow[] = {bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9]};
                byte Pout[] = {bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15], bytes[16], bytes[17]};
                byte I2[] = {bytes[18], bytes[19], bytes[20], bytes[21], bytes[22], bytes[23], bytes[24], bytes[25]};
                byte Vbuck[] = {bytes[26], bytes[27], bytes[28], bytes[29], bytes[30], bytes[31], bytes[32], bytes[33]};
                byte Ibuck[] = {bytes[34], bytes[35], bytes[36], bytes[37], bytes[38], bytes[39], bytes[40], bytes[41]};
                byte Tcoil[] = {bytes[42], bytes[43], bytes[44], bytes[45], bytes[46], bytes[47], bytes[48], bytes[49]};
                DecimalFormat df = new DecimalFormat("#####0.00");
                String str = df.format(getDouble(Vnow));
                Sspeed = Float.valueOf(str);
                Soutput_rate = String.valueOf(getDouble(Pout));
                Scoil_elect_2 = String.valueOf(getDouble(I2));
                SBUCK_voltage = String.valueOf(getDouble(Vbuck));
                SBUCK_elect = String.valueOf(getDouble(Ibuck));
                Stemp = String.valueOf(getDouble(Tcoil));

                Message msg = Message.obtain();
                msg.what = READ_COIL_2;
                handler.sendMessage(msg);//用handler在UI线程执行
            }
        }
    }

    private double getDouble(byte[] b) {
        long l;
        l = b[7];
        l &= 0xff;
        l |= ((long) b[6] << 8);
        l &= 0xffff;
        l |= ((long) b[5] << 16);
        l &= 0xffffff;
        l |= ((long) b[4] << 24);
        l &= 0xffffffffL;
        l |= ((long) b[3] << 32);
        l &= 0xffffffffffL;
        l |= ((long) b[2] << 40);
        l &= 0xffffffffffffL;
        l |= ((long) b[1] << 48);
        l &= 0xffffffffffffffL;
        l |= ((long) b[0] << 56);
        return Double.longBitsToDouble(l);
    }
}

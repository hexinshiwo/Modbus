package com.example.xinhe002614.modbustest.Unit;

import android.support.annotation.NonNull;
import android.util.Log;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by xinhe002614 on 2017/4/20.
 */

public class ModbusUnit {

//可以读写的布尔类型(0x)    请求类：ReadCoilsRequest                     响应类：ReadCoilsResponse
//只能读的布尔类型(1x)      请求类：ReadInputDiscretesRequest            响应类：ReadInputDiscretesResponse
//只能读的数字类型(3x)      请求类：ReadInputRegistersRequest            响应类：ReadInputRegistersResponse
//可以读写的数字类型(4x)    请求类：ReadMultipleRegistersRequest         响应类：ReadMultipleRegistersResponse



    /**
     * 发送读取寄存器数据的请求以及接收数据
     * @param ip
     * @param port
     * @param address
     * @param slaveId
     * @return
     */
    private static int SYSTEM_INPUT_POWER=0;//系统输入功率
    private static int SYSTEM_OUTPUT_POWER=1;//系统输出功率
    private static int TOTAL_INPUT_CURRENT=2;//整流输入电流
    private static int TOTAL_INPUT_VOLTAGE=3;//整流输入电压
    private static int PRIMARY_COIL_CURRENT=4;//原边线圈电流
    private static int SECOND_COIL_CURRENT=5;//副边线圈电流
    private static int SMALL_CAR_SPEED=6;//小车速度
    private static int SECONE_COLI_TEM=7;//线圈温度
    private static int BUCK_INPUT_VOLTAGE=8;//BUCK输入电压
    private static int BUCK_OUT_CURRENT=9;//BUCK输出电流

    private static int CONTROL_FORWARD=10;//控制前进
    private static int CONTROL_BACK=11;//控制后退
    private static int CONTROL_BRAKE=12;//控制刹车
    private static int CONTROL_TURN_ON=13;//控制电源开
    private static int CONTROL_TURN_OFF=14;//控制电源关
    private static int CONTROL_COIL_UP=15;//控制线圈升
    private static int CONTROL_COIL_DOWN=16;//控制线圈降
    private static int CONTROL_TIMED_CRUISE=17;//;定时巡航开
    private static int CONTROL_UNTIMED_CRUISE=18;//;定时巡航关
    private static int CONTROL_EXPECT_SPEED=19;//控制小车速度
    private static String baseControlReq="3A050600";
    private static String baseReq="3A050400";
    private static String baseControlSpeedReq="3A0E100035000408";
    private static int responseData=0;
    private static  byte[] REQ_PRIMARY_COIL={0x3A,0x05,0x04,0x0000,0x0010};//向原边寄存器发送的命令
    private static byte[]  REQ_SECOND_COIL={0x3A,0x05,0x04,0x0010,0x0018};//向副边寄存器发送的命令
    /**
     * 向原边dsp发送命令并读取返回的数据
     * @param ip
     * @param port
     * @param address
     * @param slaveId,设置从站的地址
     * @param Tag
     * @param registerNumber
     * @return
     */
//    public static boolean readResponseFromPrimary(String ip, int port, int address,
//                                       int slaveId,int Tag,int registerNumber) {
//        String commandCode="";//发送的命令
//        if(registerNumber>4||registerNumber<1)
//            return false;
//        else {
//            if (Tag == SYSTEM_INPUT_POWER)
//                commandCode = baseReq + "00" ;
//            else if (Tag == PRIMARY_COIL_CURRENT)
//                commandCode = baseReq + "04" ;
//            else if(Tag==TOTAL_INPUT_CURRENT)
//                commandCode = baseReq + "08" ;
//            else if(Tag==TOTAL_INPUT_VOLTAGE)
//                commandCode=baseReq+"0C";
//            else
//                return false;
//            commandCode=commandCode+registerNumber;
//
//            try {
//                InetAddress addr = InetAddress.getByName(ip);
//                TCPMasterConnection con = new TCPMasterConnection(addr);
//                con.setPort(port);
//                con.connect();//开始建立连接
//                ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);//第一个参数是寄存器的地址，第二个参数是读取多少个数据
//                req.setUnitID(slaveId);//设置从站的地址
//                dataOutput.writeBytes(commandCode);
//                req.writeData(dataOutput);
//                ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
//
//                trans.setRequest(req);//建立连接请求
//
//                trans.execute();//开始查询数据
//
//                ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
//
//                responseData = res.getRegisterValue(0);//获取返回的数据
//
//
//
//                con.close();//关闭连接
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if(responseData!=0)
//                return false;
//            else
//                return false;
//        }
//
//    }
//
//    /**
//     * 向副边dsp发送命令并读取返回的数据
//     * @param ip
//     * @param port
//     * @param address
//     * @param slaveId
//     * @param Tag
//     * @param registerNumber
//     * @return
//     */
//    public static boolean readResponseFromSecond(String ip, int port, int address,
//                                                  int slaveId,int Tag,int registerNumber) {
//        String commandCode="";//发送的命令
//        if(registerNumber>4||registerNumber<1)
//            return false;
//        else {
//            if (Tag == SMALL_CAR_SPEED)
//                commandCode = baseReq + "10" ;
//            else if (Tag ==SYSTEM_OUTPUT_POWER)
//                commandCode = baseReq + "14" ;
//            else if(Tag==SECOND_COIL_CURRENT)
//                commandCode = baseReq + "18" ;
//            else if(Tag==BUCK_INPUT_VOLTAGE)
//                commandCode=baseReq+"1C";
//            else if(Tag==BUCK_OUT_CURRENT)
//                commandCode = baseReq + "20" ;
//            else if(Tag==SECONE_COLI_TEM)
//                commandCode=baseReq+"24";
//            else
//                return false;
//            commandCode=commandCode+registerNumber;
//
//            try {
//                InetAddress addr = InetAddress.getByName(ip);
//                TCPMasterConnection con = new TCPMasterConnection(addr);
//                con.setPort(port);
//                con.connect();//开始建立连接
//                ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);//第一个参数是寄存器的地址，第二个参数是读取多少个数据
//                req.setUnitID(slaveId);
//                dataOutput.writeBytes(commandCode);
//                req.writeData(dataOutput);
//                ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
//
//                trans.setRequest(req);//建立连接请求
//
//                trans.execute();//开始查询数据
//
//                ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
//
//                responseData = res.getRegisterValue(0);//获取返回的数据
//
//                con.close();//关闭连接
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if(responseData>0)
//                return false;
//            else
//                return false;
//        }
//
//    }
//
//
//    /**
//     * 给副边dsp发送控制命令，数据类型是RE
//     *
//     * @param ip
//     * @param port
//     * @param slaveId
//     * @param address
//     * @param Tag
//     */
//    public static Boolean sendCommandToSecond(String ip, int port, int slaveId,
//                                     int address, int Tag) {
//
//        String commandCode="";//发送的命令
//           if (Tag == CONTROL_FORWARD)
//                commandCode = baseControlReq + "30FF00" ;
//            else if (Tag ==CONTROL_BACK)
//                commandCode = baseControlReq + "300000" ;
//            else if(Tag==CONTROL_BRAKE)
//                commandCode = baseControlReq + "31FF00" ;
//            else if(Tag==CONTROL_TURN_ON)
//                commandCode=baseControlReq+"32FF00";
//            else if(Tag==CONTROL_TURN_OFF)
//                commandCode = baseControlReq + "320000" ;
//            else if(Tag==CONTROL_COIL_UP)
//                commandCode=baseControlReq+"33FF00";
//           else if(Tag==CONTROL_COIL_DOWN)
//               commandCode=baseControlReq+"330000";
//           else if(Tag==CONTROL_TIMED_CRUISE)
//               commandCode=baseControlReq+"34FF00";
//           else if(Tag==CONTROL_UNTIMED_CRUISE)
//               commandCode=baseControlReq+"340000";
//
//           else
//                return false;
//
//
//        try {
//            InetAddress addr = InetAddress.getByName(ip);
//
//            TCPMasterConnection connection = new TCPMasterConnection(addr);
//            connection.setPort(port);
//            connection.connect();
//
//            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);
//
//            UnityRegister register = new UnityRegister(Integer.valueOf(commandCode));
//
//            WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(address, register);
//
//            req.setUnitID(slaveId);
//            trans.setRequest(req);
//
//            System.out.println("ModbusSlave: FC" + req.getFunctionCode()
//                    + " ref=" + req.getReference() + " value="
//                    + register.getValue());
//            trans.execute();
//
//            connection.close();
//            return true;
//        } catch (Exception ex) {
//            System.out.println("Error in code");
//            ex.printStackTrace();
//        }
//        return false;
//    }
//    /**
//     * 给副边dsp发送控制命令，数据类型是RE
//     *
//     * @param ip
//     * @param port
//     * @param slaveId
//     * @param address
//     * @param expectSpeed
//     */
//    public static Boolean sendCommandToSecondForCarSpeed(String ip, int port, int slaveId,
//                                              int address, int expectSpeed) {
//
//        String commandCode="";//发送的命令
//        commandCode=baseControlSpeedReq+String.valueOf(expectSpeed);
//        try {
//            InetAddress addr = InetAddress.getByName(ip);
//            TCPMasterConnection connection = new TCPMasterConnection(addr);
//            connection.setPort(port);
//            connection.connect();
//            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);
//            UnityRegister register = new UnityRegister(Integer.valueOf(commandCode));
//            WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(address, register);
//            req.setUnitID(slaveId);
//            trans.setRequest(req);
//            System.out.println("ModbusSlave: FC" + req.getFunctionCode()
//                    + " ref=" + req.getReference() + " value="
//                    + register.getValue());
//            trans.execute();
//
//            connection.close();
//            return true;
//        } catch (Exception ex) {
//            System.out.println("Error in code");
//            ex.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 向原边线圈或副边线圈发起读取数据的命令，这里默认认为通过ip和port就能连接上线圈
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromCoil(String ip, int port
                                           ,int Tag) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
                InetAddress addr = InetAddress.getByName(ip);
                TCPMasterConnection con = new TCPMasterConnection(addr);
                con.setPort(port);
                con.connect();//开始建立连接
                ReadCoilsRequest request=new ReadCoilsRequest();
            if(Tag==1)
             {
                 dataOutput.write(REQ_PRIMARY_COIL);
             }
            else
                dataOutput.write(REQ_SECOND_COIL);
                request.writeData(dataOutput);
                ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
                trans.setRequest(request);//建立连接请求
                trans.execute();//开始查询数据
                ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
                response.readData(dataInput);//将返回的数据写入dataInput之中
                con.close();//关闭连接
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(dataInput!=null)//如果有数据返回
                result=1;
            return result;
        }
    /**
     * 向原边线圈或副边线圈发起读取数据的命令，这里默认认为通过ip和port无法连接上线圈，还需要通过设置从站的id，即 slaveId
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromCoil(String ip, int port,int slaveId
            ,int Tag) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            ReadCoilsRequest request=new ReadCoilsRequest();
            if(Tag==1)
            {
                dataOutput.write(REQ_PRIMARY_COIL);
            }
            else
                dataOutput.write(REQ_SECOND_COIL);
            request.writeData(dataOutput);
            request.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(request);//建立连接请求
            trans.execute();//开始查询数据
            ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
            response.readData(dataInput);//将返回的数据写入dataInput之中
            con.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataInput!=null)//如果有数据返回
            result=1;
        return result;
    }
    /**
     * 向原边线圈或副边线圈发起读取数据的命令，这里默认认为通过ip和port无法连接上线圈，还需要通过设置从站的id，即 slaveId,以及设置线圈上寄存器的地址和读取寄存器上多少个数据,这里默认是一个
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromCoil(String ip, int port,int slaveId
            ,int Tag,int address) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            ReadCoilsRequest request=new ReadCoilsRequest(address,1);
            if(Tag==1)
            {
                dataOutput.write(REQ_PRIMARY_COIL);
            }
            else
                dataOutput.write(REQ_SECOND_COIL);
            request.writeData(dataOutput);
            request.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(request);//建立连接请求
            trans.execute();//开始查询数据
            ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
            response.readData(dataInput);//将返回的数据写入dataInput之中
            con.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataInput!=null)//如果有数据返回
            result=1;
        return result;
    }
    /**
     * 向原边线圈或副边线圈上的寄存器发起读取数据的命令，这里默认认为通过ip和port就能连接上线圈
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromRegister(String ip, int port
            ,int Tag) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            ReadMultipleRegistersRequest request=new ReadMultipleRegistersRequest();
            if(Tag==1)
            {
                dataOutput.write(REQ_PRIMARY_COIL);
            }
            else
                dataOutput.write(REQ_SECOND_COIL);
            request.writeData(dataOutput);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(request);//建立连接请求
            trans.execute();//开始查询数据
            ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
            response.readData(dataInput);//将返回的数据写入dataInput之中
            con.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataInput!=null)//如果有数据返回
            result=1;
        return result;
    }
    /**
     * 向原边线圈或副边线圈上的寄存器发起读取数据的命令，这里默认认为通过ip和port无法连接上线圈，还需要通过设置从站的id，即 slaveId
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromRegister(String ip, int port
            ,int Tag,int slaveId) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            ReadMultipleRegistersRequest request=new ReadMultipleRegistersRequest();
            if(Tag==1)
            {
                dataOutput.write(REQ_PRIMARY_COIL);
            }
            else
                dataOutput.write(REQ_SECOND_COIL);
            request.writeData(dataOutput);
            request.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(request);//建立连接请求
            trans.execute();//开始查询数据
            ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
            response.readData(dataInput);//将返回的数据写入dataInput之中
            con.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataInput!=null)//如果有数据返回
            result=1;
        return result;
    }
    /**
     * 向原边线圈或副边线圈上的寄存器发起读取数据的命令，这里默认认为通过ip和port无法连接上线圈，还需要通过设置从站的id，即 slaveId,以及设置线圈上寄存器的地址和读取寄存器上多少个数据,这里默认是一个
     * @param ip
     * @param port
     * @param Tag, Tag为1表示向原变发送请求，为0向副边发送请求
     * @return
     */
    public static int readResponseFromRegister(String ip, int port,int slaveId
            ,int Tag,int address) {
        int result=0;
        DataOutput dataOutput=null;
        DataInput dataInput=null;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            ReadMultipleRegistersRequest request=new ReadMultipleRegistersRequest(address,1);
            if(Tag==1)
            {
                dataOutput.write(REQ_PRIMARY_COIL);
            }
            else
                dataOutput.write(REQ_SECOND_COIL);
            request.writeData(dataOutput);
            request.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(request);//建立连接请求
            trans.execute();//开始查询数据
            ReadCoilsResponse response = (ReadCoilsResponse) trans.getResponse();
            response.readData(dataInput);//将返回的数据写入dataInput之中
            con.close();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataInput!=null)//如果有数据返回
            result=1;
        return result;
    }




}







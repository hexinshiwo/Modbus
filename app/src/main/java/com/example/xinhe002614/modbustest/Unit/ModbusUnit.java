package com.example.xinhe002614.modbustest.Unit;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;

/**
 * Created by xinhe002614 on 2017/4/20.
 */

public class ModbusUnit {
    /**
     * 读取寄存器的数据
     * @param ip
     * @param port
     * @param address
     * @param slaveId
     * @return
     */
    public static int readDigitalInput(String ip,int port,int address,int slaveId)
    {
        int data=0;
        try{
            InetAddress addr=InetAddress.getByName(ip);
            TCPMasterConnection con=new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();//开始建立连接
            //第一个参数是寄存器的地址，第二个参数是读取多少个数据
            ReadInputDiscretesRequest request=new ReadInputDiscretesRequest(address,1);

            request.setUnitID(slaveId);

            ModbusTCPTransaction transaction=new ModbusTCPTransaction(con);

            transaction.setRequest(request);//建立连接请求

            transaction.execute();//开始查询数据

            ReadInputDiscretesResponse response=(ReadInputDiscretesResponse)transaction.getResponse();
            //示例方法
            if(response.getDiscretes().getBit(0))
            {
                data=1;

            }
            con.close();//关闭连接
        }catch(Exception e)
        {
            e.printStackTrace();
        }


        return data;
    }

}

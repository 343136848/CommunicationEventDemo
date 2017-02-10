package org.hy.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;





/**
 * 服务间相互通讯的事件机构(事件类型可自定义)的客户端演示Demo 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-10
 * @version     v1.0
 */
@Xjava(value=XType.XML)
public class ClientDemo
{
    
    //测试1.0.0   
    public static void main(String [] args) throws Exception
    {
        // 在执行前，请先执行服务端应用ServerDemo
        
        XJava.parserAnnotation(ClientDemo.class.getName());
        
        Map<String ,Object> v_DataMap = new HashMap<String ,Object>();
        v_DataMap.put("String"  ,"ABCDEFG");
        v_DataMap.put("Integer" ,1234567890);
        v_DataMap.put("Date"    ,new Date());
        
        (new ClientDemo()).send(v_DataMap);
    }
    
    
    
    @SuppressWarnings("unchecked")
    public void send(Map<String ,Object> i_DataMap)
    {
        List<ClientSocket>    v_Servers      = this.getServers();
        CommunicationRequest  v_RequestData  = new CommunicationRequest();
        CommunicationResponse v_ResponseData = null;
        List<String>          v_Results      = null;
        
        v_RequestData.setEventType("Demo");     // 通讯的事件类型。如果没有设置此属性，默认为XJava事件类型
        v_RequestData.setDataXID(  "2017");     // 通讯数据的标识ID。可选的，按具体业务而定
        v_RequestData.setData(      i_DataMap); // 通讯数据。可选的，按具体业务而定。可为任何Java类型，须实现 java.io.Serializable 接口
        
        // 只要有一台服务器返回有效数据，只为成功
        for (ClientSocket v_Server : v_Servers)
        {
            v_ResponseData = v_Server.send(v_RequestData);
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                v_Results = (List<String>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Results) ) {break;}
            }
        }
        
        if ( !Help.isNull(v_Results) )
        {
            System.out.println("-- 访问结果为：");
            Help.print(v_Results);
        }
        else
        {
            System.out.println("-- 服务器异常 或 网络异常。");
        }
    }
    
    
    
    /**
     * 获取服务端集群配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-10
     * @version     v1.0
     *
     * @return
     */
    public List<ClientSocket> getServers()
    {
        String []          v_ClusterServers = StringHelp.replaceAll(XJava.getParam("Servers").getValue() ,new String[]{" " ,"\t" ,"\r" ,"\n"} ,new String[]{""}).split(",");
        List<ClientSocket> v_Clusters       = new ArrayList<ClientSocket>();
        
        for (String v_Server : v_ClusterServers)
        {
            String [] v_HostPort = (v_Server.trim() + ":1721").split(":");
            
            v_Clusters.add(new ClientSocket(v_HostPort[0] ,Integer.parseInt(v_HostPort[1])));
        }
        
        return v_Clusters;
    }
    
}

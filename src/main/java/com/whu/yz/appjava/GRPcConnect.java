package com.whu.yz.appjava;



import java.nio.file.Path;
import java.nio.file.Paths;
import org.hyperledger.fabric.gateway.*;
import java.io.IOException;
import java.net.MalformedURLException;


import org.hyperledger.fabric_ca.sdk.HFCAClient;
import java.util.Properties;

public class GRPcConnect {

    private GRPcConnect(){}


    //创建gRPC连接
    public static Gateway.Builder  creatChannel() throws IOException {
        Path networkConfigPath = Paths.get("./config/connection-web.yaml");
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("./wallet"));
        Gateway.Builder builder = Gateway.createBuilder();
        try{
            builder.identity(wallet, "javaUser").networkConfig(networkConfigPath).discovery(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        return builder;
    }

    // 创建CA client
    // 连接CA的url，返回HFCAClient
    public static HFCAClient fabricCAInteraction() throws MalformedURLException {
        // todo CA服务器的URL
        String caUrl = "https://web.ifantasy.net:7350";
        Properties props = new Properties();
        props.put("pemFile", "/home/c308/zyz/APP-Java/Pem/ca-cert.pem");  // CA的TLS证书文件路径
        props.put("allowAllHostNames", "true"); 
        HFCAClient client = HFCAClient.createNewInstance(caUrl, props);
        System.out.println("HFCACient end"); // for dubug
        return client;
    }

}

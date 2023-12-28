package com.whu.yz.appjava;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import org.hyperledger.fabric_ca.sdk.HFCAClient;


import java.util.Properties;

public class GRPcConnect {

    private GRPcConnect(){}


    //创建gRPC连接
    public static Gateway.Builder  creatChannel() throws CertificateException, IOException, InvalidKeyException{
        System.out.println("GRPC Header here"); // for dubug
        Path adminPrivateKeyPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin1/msp/keystore/ec099d8e6db40ad535f88721e8b6dff8ec8a1d370993dc83f67aa751ee233a05_sk");
        Path adminCertPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin1/msp/signcerts/cert.pem");
        byte[] certBytes = Files.readAllBytes(adminCertPath);
        byte[] privateKeyBytes = Files.readAllBytes(adminPrivateKeyPath);
        String ceString = new String(certBytes);
        String pString = new String(privateKeyBytes);
        X509Certificate certificate = Identities.readX509Certificate(ceString);
        PrivateKey privateKey = Identities.readPrivateKey(pString);
        Identity identity = new X509Identity("webMSP", certificate);
        Signer signer = Signers.newPrivateKeySigner(privateKey);
        
        // todo 改channel 地址
        ManagedChannel grpcChannel = Grpc.newChannelBuilder("127.0.0.1:7058", TlsChannelCredentials.create())
                .build();
        //gateway.example.org:1337 gRPC服务地址

        return Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .connection(grpcChannel);
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

package com.whu.yz.appjava;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Path;
import java.nio.file.Paths;

// Main
@SpringBootApplication
public class AppJavaApplication {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(AppJavaApplication.class, args);
        System.out.println("fabric APP start ！");

        // 指定钱包路径
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        // 检查用户是否已在钱包中
        if (wallet.get("javaAppUser") == null) {
            System.out.println("请先注册用户");
            // EnrollAdmin.main(new String[0]);
            RegisterUser.main(new String[0]);
        }

        

        // 创建Gateway连接
        Gateway.Builder builder = GRPcConnect.creatChannel();
        // 使用Gateway连接
        try(Gateway gateway = builder.connect()) {
            // 获取网络和合约引用
            // todo 改NetworkName
            Network network = gateway.getNetwork("testchannel");
            // todo 改ChaincodeName,
            Contract contract = network.getContract("basic");

            // 资产转移逻辑
            byte[] result;

            // 创建资产
            contract.submitTransaction("CreateAsset", "asset1", "red", "10", "Alice", "5000");
            System.out.println("创建资产··············");
            System.out.println("Asset creat : \"asset1\", \"red\", \"10\", \"Alice\", \"5000\"");

            // 读取资产
            result = contract.evaluateTransaction("ReadAsset", "asset1");
            //打印
            System.out.println("读取资产··············");
            System.out.println("Asset: " + new String(result));

            // 更新资产
            contract.submitTransaction("UpdateAsset", "asset1", "blue", "15", "Alice", "5500");
            System.out.println("更新资产··············");
            // 转移资产
            contract.submitTransaction("TransferAsset", "asset1", "Bob");
            System.out.println("转移资产··············");
            // 再次读取资产以验证
            result = contract.evaluateTransaction("ReadAsset", "asset1");
            System.out.println("Updated Asset: " + new String(result));
        } finally {
            // 清理和退出处理
            System.out.println("退出");
        }
    }
}






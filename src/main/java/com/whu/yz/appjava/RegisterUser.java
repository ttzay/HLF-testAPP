package com.whu.yz.appjava;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.nio.file.Files;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class RegisterUser {

    public static void main(String[] args) throws Exception {
        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
        // Check to see if we've already enrolled the user.
        if (wallet.get("javaAppUser") != null) {
            System.out.println("An identity for the user \"javaAppUser\" already exists in the wallet");
            return;
        }

        // 使用管理注册user
        // 加载管理员身份
        Path adminPrivateKeyPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin1/msp/keystore/ec099d8e6db40ad535f88721e8b6dff8ec8a1d370993dc83f67aa751ee233a05_sk");
        Path adminCertPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin1/msp/signcerts/cert.pem");
        byte[] certBytes = Files.readAllBytes(adminCertPath);
        byte[] privateKeyBytes = Files.readAllBytes(adminPrivateKeyPath);
        String ceString = new String(certBytes);
        String pString = new String(privateKeyBytes);
        // 加载管理员密钥和证书
        X509Certificate certificate = Identities.readX509Certificate(ceString);
        PrivateKey privateKey = Identities.readPrivateKey(pString);
        X509Identity adminIdentity = Identities.newX509Identity("webMSP", certificate, privateKey);
        wallet.put("admin1", adminIdentity);
        System.out.println("Successfully enrolled user \"admin1\" and imported it into the wallet");


        
        // 内部类admin继承USer 
        User admin1 = new User() {
            @Override
            public String getName() {
                return "admin1";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return "web.ifantasy.net";
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return "webMSP";
            }

        };


        // Create a CA client for interacting with the CA.
        HFCAClient caClient = GRPcConnect.fabricCAInteraction();
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // RegistrationRequest registrationRequest = new RegistrationRequest("peer1");
        // String enrollmentSecret = caClient.register(registrationRequest, admin1);
        // Enrollment enrollment = caClient.enroll("peer1", enrollmentSecret);
        // X509Identity userIdentity = Identities.newX509Identity("webMSP", enrollment);
        // wallet.put("javaAPPUser", userIdentity);
        // System.out.println("Successfully enrolled user \"javaAppUser\" and imported it into the wallet");

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest("javaAppUser");
        // todo 隶属改一下
        registrationRequest.setAffiliation("web.ifantasy.net");
        registrationRequest.setEnrollmentID("javaAppUser");
        String enrollmentSecret = caClient.register(registrationRequest, admin1);
        Enrollment enrollment = caClient.enroll("javaAppUser", enrollmentSecret);
        Identity user = Identities.newX509Identity("webMSP", enrollment);
        wallet.put("javaAppUser", user);
        System.out.println("Successfully enrolled user \"javaAppUser\" and imported it into the wallet");
    }

}

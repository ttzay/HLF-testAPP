package com.whu.yz.appjava;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Set;
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
        
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
        // 使用管理注册user
        // 加载管理员身份
        Path adminPrivateKeyPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin/msp/keystore/dfa04c1a3e62c6804c04545310644f8eade33648190b934113eb4eedbaf6e909_sk");
        Path adminCertPath = Paths.get("/home/c308/zyz/APP-Java/Pem/admin/msp/signcerts/cert.pem");
        byte[] certBytes = Files.readAllBytes(adminCertPath);
        byte[] privateKeyBytes = Files.readAllBytes(adminPrivateKeyPath);
        String ceString = new String(certBytes);
        String pString = new String(privateKeyBytes);
        // 加载管理员密钥和证书
        X509Certificate certificate = Identities.readX509Certificate(ceString);
        PrivateKey privateKey = Identities.readPrivateKey(pString);
        X509Identity adminIdentity = Identities.newX509Identity("webMSP", certificate, privateKey);
        wallet.put("admin", adminIdentity);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
         //Create a wallet for managing identities

        //Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
        // // Check to see if we've already enrolled the user.
        // if (wallet.get("javaAppUser") != null) {
        //     System.out.println("An identity for the user \"javaAppUser\" already exists in the wallet");
        //     return;
        // }

        // X509Identity adminIdentity = (X509Identity)wallet.get("admin");
		// if (adminIdentity == null) {
		// 	System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
		// 	return;
		// }

        
        // 内部类admin继承USer 
        User admin = new User() {
            @Override
            public String getName() {
                return "admin";
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
        RegistrationRequest registrationRequest = new RegistrationRequest("javaUser");
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID("javaUser");
        registrationRequest.setMaxEnrollments(-1);
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll("javaUser", enrollmentSecret);
        Identity user = Identities.newX509Identity("webMSP", enrollment);
        wallet.put("javaUser", user);
        System.out.println("Successfully enrolled user \"javaUser\" and imported it into the wallet");
    }

}

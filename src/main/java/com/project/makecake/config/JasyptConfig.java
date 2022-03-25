//package com.project.makecake.config;
//
//import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
//import org.jasypt.encryption.StringEncryptor;
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@EnableEncryptableProperties
//@Configuration
//public class JasyptConfig {
//
//    @Value("${jasypt.encryptor.password}")
//    private String encryptorPassword;
//
//    @Bean("jasyptStringEncryptor")
//    public StringEncryptor stringEncryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        // 암호화 키
//        config.setPassword(encryptorPassword);
//        // 암호화 알고리즘 Two Way Encryption
//        config.setAlgorithm("PBEWithMD5AndDES");
//        // 인코딩 방식
//        config.setStringOutputType("base64");
//        // 인스턴스 pool
//        config.setPoolSize("1");
//        // 반복할 해싱 횟수
//        config.setKeyObtentionIterations("1000");
//        // salt 생성 클래스
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        encryptor.setConfig(config);
//        return encryptor;
//    }
//}

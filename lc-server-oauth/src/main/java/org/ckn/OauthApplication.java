package org.ckn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ckn
 * @date 2023/2/27
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class OauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class);
    }
}

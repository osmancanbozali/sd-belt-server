package gtu.cse.cse396.sdbelt.system.infra.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class asdsa {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

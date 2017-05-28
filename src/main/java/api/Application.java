package api;

import api.DAO.Queries;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by algys on 24.02.17.
 */

@SpringBootApplication
@ConfigurationProperties("spring.datasource")
public class Application {

    @Bean
    public Queries queries(){
        return new Queries();
    }

    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(Application.class, args);
    }
}

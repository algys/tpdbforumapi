package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by algys on 24.02.17.
 */

@SpringBootApplication
@ConfigurationProperties("spring.datasource")
public class Application {

    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(Application.class, args);
    }
}

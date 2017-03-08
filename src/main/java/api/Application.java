package api;

import org.omg.CORBA.Environment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

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

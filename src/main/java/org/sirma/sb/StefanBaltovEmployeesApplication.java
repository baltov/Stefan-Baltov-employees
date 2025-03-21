package org.sirma.sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class StefanBaltovEmployeesApplication {

    public static void main(String[] args) {
        SpringApplication.run(StefanBaltovEmployeesApplication.class, args);
    }

}

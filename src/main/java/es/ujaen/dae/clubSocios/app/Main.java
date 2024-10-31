package es.ujaen.dae.clubSocios.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "es.ujaen.dae.clubSocios.servicios",
        "es.ujaen.dae.clubSocios.repositorios"
})
@EntityScan(basePackages = "es.ujaen.dae.clubSocios.entidades")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
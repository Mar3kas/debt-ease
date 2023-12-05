package com.dm.debtease;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Debt Management System API", version = "1.0",
        description = "Debt Management System API"))
@SecurityScheme(name = "dmapi", scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER, description = "JWT Authorization header using Bearer scheme")
public class DebtEaseApplication {

    public static void main(String[] args) {
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(DebtEaseApplication.class, args);
    }
}

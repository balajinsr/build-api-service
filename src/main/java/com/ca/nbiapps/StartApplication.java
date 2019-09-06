package com.ca.nbiapps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StartApplication extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(StartApplication.class);

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
        log.info("Build API Service Up and Running");
    }
}
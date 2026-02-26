package com.example.travelserver;

// 注意！这里的 import 必须是 mybatis 的这个
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.lang.String;


@SpringBootApplication
@MapperScan("com.example.travelserver.mapper")
public class TravelServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelServerApplication.class, args);
    }
}
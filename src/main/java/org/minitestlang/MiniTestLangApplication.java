package org.minitestlang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniTestLangApplication {


    public static void main(String[] args){
        System.out.printf("Hello");
        SpringApplication.run(MiniTestLangApplication.class, args);
    }

}

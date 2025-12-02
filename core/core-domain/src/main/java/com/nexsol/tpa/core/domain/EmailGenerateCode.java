package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class EmailGenerateCode {

    public String generateCode(){
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}

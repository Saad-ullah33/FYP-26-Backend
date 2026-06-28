package com.propsightai.Service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("EMAIL TO: " + to);
        System.out.println("LINK: " + body);
    }
}
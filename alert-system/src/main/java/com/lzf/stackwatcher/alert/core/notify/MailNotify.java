package com.lzf.stackwatcher.alert.core.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailNotify implements NotifyMethod {

    @Autowired private JavaMailSender mailSender = new JavaMailSenderImpl();


}

package com.lzf.stackwatcher.alert.core.notify;

import com.lzf.stackwatcher.alert.configuration.AlertMessageConfig;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Notify;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.common.ConfigManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Component
public class MailNotify implements NotifyMethod {

    private final JavaMailSender mailSender;

    private String pattern;

    @Value("${spring.mail.username}") private String mailFrom;

    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    public MailNotify(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired @Lazy
    public void setPattern(ConfigManager configManager) {
        this.pattern = Objects.requireNonNull(configManager.getConfig(
                AlertMessageConfig.NAME, AlertMessageConfig.class)).getPattern();
    }

    @Override
    public final String methodName() {
        return "MAIL";
    }

    @Override
    public void send(Data data, Rule rule, Notify notify) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom);
        msg.setTo(notify.getArgument());
        msg.setSubject(rule.getName());
        msg.setText(decodePattern(data, rule));

        mailSender.send(msg);
    }

    private String decodePattern(Data data, Rule rule) {
        String str = pattern.replace("${HOST}", data.getHost())
               .replace("${DEVICE}", data.getDevice() == null ? "" : data.getDevice())
               .replace("${RULE.NAME}", rule.getName())
               .replace("${RULE.ITEM}", Rule.Type.getById(data.getType()).value)
               .replace("${RULE.PERIOD}", rule.getPeriod().toString())
               .replace("${VALUE}", String.valueOf(data.getValue()));

        Date date = new Date(data.getTime());
        String d = FORMAT_DATE.format(date);
        String t = FORMAT_TIME.format(date);

        return str.replace("${DATE}", d).replace("${TIME}", t);
    }
}

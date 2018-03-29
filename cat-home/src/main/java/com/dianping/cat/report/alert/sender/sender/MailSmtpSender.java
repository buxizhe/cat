package com.dianping.cat.report.alert.sender.sender;

import com.dianping.cat.report.alert.sender.AlertChannel;
import com.dianping.cat.report.alert.sender.AlertMessageEntity;
import com.site.lookup.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by gaikou on 2018/3/12.
 */
public class MailSmtpSender extends AbstractSender {

//    public static final String ID = AlertChannel.SMTPMAIL.getName();

    Logger logger = LoggerFactory.getLogger(MailSmtpSender.class);

    public static final String ID = AlertChannel.MAIL.getName();
    private final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean send(AlertMessageEntity message) {
        logger.info("gaikuo send message");
        com.dianping.cat.home.sender.entity.Sender sender = querySender();
        boolean result = false;
        String emailsStr = message.getReceiverString();
        if(StringUtils.isEmpty(emailsStr)){
            return false;
        }
        List<String> emails = Arrays.asList(emailsStr.split(","));
        result = sendEmail(message, emails, sender);
        return result;
    }

    private boolean sendEmail(AlertMessageEntity message, List<String> receivers,
                              com.dianping.cat.home.sender.entity.Sender sender) {
        String title = message.getTitle().replaceAll(",", " ");
        String content = message.getContent().replaceAll(",", " ");
        Map<String, String> urlPars = m_senderConfigManager.queryParMap(sender);
        String username = urlPars.get("username");
        String password = urlPars.get("password");

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        MimeMessage mailMessage = new MimeMessage(session);

        Transport transport = null;
        try {
            transport = session.getTransport();

            mailMessage.setContent(content, "text/html;charset=UTF-8");
            mailMessage.setFrom(new InternetAddress(username));
            mailMessage.setSubject(title);
            for(String receiver: receivers){
                mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            }
//            mailMessage.setRecipients(Message.RecipientType.TO, "buxizhe@163.com");
            // 设置显示的发件时间
            mailMessage.setSentDate(new Date());
            // 保存前面的设置
            mailMessage.saveChanges();

            transport.connect(username, password);
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

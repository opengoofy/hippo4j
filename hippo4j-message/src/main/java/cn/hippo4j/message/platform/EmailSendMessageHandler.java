/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.message.platform;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.FileUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.Singleton;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
import cn.hippo4j.message.platform.base.AbstractRobotSendMessageHandler;
import cn.hippo4j.message.platform.base.RobotMessageActualContent;
import cn.hippo4j.message.platform.base.RobotMessageExecuteDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.Charset;
import java.util.*;

import static cn.hippo4j.message.platform.constant.EmailAlarmConstants.Email_ALARM_TITLE;
import static cn.hippo4j.message.platform.constant.EmailAlarmConstants.Email_NOTICE_TITLE;

/**
 * Send Email notification message.
 */
@Slf4j
public class EmailSendMessageHandler extends AbstractRobotSendMessageHandler {

    @Override
    public String getType() {
        return NotifyPlatformEnum.Email.name();
    }

    @Override
    protected RobotMessageActualContent buildMessageActualContent() {
        String emailAlarmTxtKey = "message/robot/dynamic-thread-pool/email-alarm.txt";
        String emailConfigTxtKey = "message/robot/dynamic-thread-pool/email-config.txt";
        return RobotMessageActualContent.builder()
                .receiveSeparator(", @")
                .changeSeparator(" -> ")
                .alarmMessageContent(Singleton.get(emailAlarmTxtKey, () -> FileUtil.readUtf8String(emailAlarmTxtKey)))
                .configMessageContent(Singleton.get(emailConfigTxtKey, () -> FileUtil.readUtf8String(emailConfigTxtKey)))
                .build();
    }

    @Override
    protected void execute(RobotMessageExecuteDTO robotMessageExecuteDTO) {

        NotifyConfigDTO notifyConfig = robotMessageExecuteDTO.getNotifyConfig();
        String content = robotMessageExecuteDTO.getText();
        String receives = notifyConfig.getReceives();
        String secretKey = notifyConfig.getSecretKey();
        String[] recipients = receives.split(",");

        MailAccount mailAccount = JSONUtil.parseObject(secretKey, MailAccount.class);
        Assert.isTrue(mailAccount != null, "mailAccount is null");
        mailAccount.setUser(mailAccount.getFrom());
        String subject = Objects.equals(notifyConfig.getType(), "CONFIG") ? Email_NOTICE_TITLE : Email_ALARM_TITLE;
        try {
            MimeMessage mimeMessage = buildMsg(mailAccount, recipients, subject, content);
            Transport.send(mimeMessage);
        } catch (Exception ex) {
            log.error("Email failed to send message", ex);
        }
    }

    private MimeMessage buildMsg(MailAccount mailAccount, String[] recipients, String subject, String content) throws MessagingException {

        UserPassAuthenticator authenticator = new UserPassAuthenticator(mailAccount.getUser(), mailAccount.getPass());
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", mailAccount.getHost().split("\\.")[0]);
        properties.put("mail.smtp.host", mailAccount.getHost());
        properties.put("mail.smtp.port", mailAccount.getPort());
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, authenticator);

        MimeMessage msg = new MimeMessage(session);
        // 发件人
        String from = mailAccount.getFrom();
        msg.setFrom(from);
        // 标题
        msg.setSubject(subject);
        // 发送时间
        msg.setSentDate(new Date());
        // 内容和附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        MimeBodyPart body = new MimeBodyPart();
        body.setContent(content, "text/html; charset=" + Charset.defaultCharset());
        mimeMultipart.addBodyPart(body);
        msg.setContent(mimeMultipart);

        // 收件人
        List<Address> addressList = new ArrayList<>(recipients.length);
        for (String recipient : recipients) {
            Address to = new InternetAddress(recipient);
            addressList.add(to);
        }
        Address[] addresses = addressList.toArray(new Address[0]);
        msg.setRecipients(MimeMessage.RecipientType.TO, addresses);
        return msg;
    }

    @Data
    private static class MailAccount {

        /**
         * SMTP服务器域名
         */
        private String host;
        /**
         * SMTP服务端口
         */
        private Integer port;
        /**
         * 是否需要用户名密码验证
         */
        private Boolean auth;
        /**
         * 用户名
         */
        private String user;
        /**
         * 密码
         */
        private String pass;

        /**
         * 发送方
         */
        private String from;
    }

    private static class UserPassAuthenticator extends Authenticator {

        private final String user;
        private final String pass;

        /**
         * 构造
         *
         * @param user 用户名
         * @param pass 密码
         */
        public UserPassAuthenticator(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.pass);
        }
    }
}
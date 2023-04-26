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

import cn.hippo4j.common.toolkit.FileUtil;
import cn.hippo4j.common.toolkit.Singleton;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
import cn.hippo4j.message.platform.constant.EmailAlarmConstants;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.service.SendMessageHandler;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Send Email notification message.
 */
@Slf4j
public class EmailSendMessageHandler implements SendMessageHandler<AlarmNotifyRequest, ChangeParameterNotifyRequest> {

    @Resource
    private JavaMailSender emailSender;

    @Resource
    MailProperties mailProperties;

    @Override
    public String getType() {
        return NotifyPlatformEnum.EMAIL.name();
    }

    @Override
    public void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest) {
        try {
            String emailAlarmTxtKey = "message/robot/dynamic-thread-pool/email-alarm.ftl";
            Map<String, String> dataModel = getDataModel(alarmNotifyRequest);
            dataModel.put("interval", notifyConfig.getInterval().toString());
            String emailAlarmTxt = Singleton.get(emailAlarmTxtKey, () -> FileUtil.readUtf8String(emailAlarmTxtKey));
            String renderedEmailAlarmTxt = render(dataModel, emailAlarmTxt);
            String alarmSubject = render(dataModel, EmailAlarmConstants.Email_ALARM_TITLE);
            String[] recipients = notifyConfig.getReceives().split(",");
            execute(recipients, alarmSubject, renderedEmailAlarmTxt);
        } catch (Exception e) {
            log.error("Email failed to send message", e);
        }
    }

    @Override
    public void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        try {
            String emailConfigTxtKey = "message/robot/dynamic-thread-pool/email-config.ftl";
            Map<String, String> dataModel = getDataModel(changeParameterNotifyRequest);
            String emailConfigTxt = Singleton.get(emailConfigTxtKey, () -> FileUtil.readUtf8String(emailConfigTxtKey));
            String renderedEmailConfigTxt = render(dataModel, emailConfigTxt);
            String configSubject = render(dataModel, EmailAlarmConstants.Email_NOTICE_TITLE);
            String[] recipients = notifyConfig.getReceives().split(",");
            execute(recipients, configSubject, renderedEmailConfigTxt);
        } catch (Exception e) {
            log.error("Email failed to send message", e);
        }
    }

    private String render(Map<String, String> dataModel, String stringTemplate) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("renderTemplate", stringTemplate);
        cfg.setTemplateLoader(stringLoader);
        Writer out = new StringWriter(2048);
        try {
            Template tpl = cfg.getTemplate("renderTemplate", "UTF-8");
            tpl.process(dataModel, out);
        } catch (Exception e) {
            log.error("failed to render template,dataModel:{},stringTemplate:{}", dataModel, stringTemplate);
        }
        return out.toString();
    }

    @SneakyThrows
    private Map<String, String> getDataModel(Object bean) {
        Map<String, String> dataModel = BeanUtils.describe(bean);
        dataModel.put("from", mailProperties.getUsername());
        dataModel.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dataModel;
    }

    private void execute(String[] to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
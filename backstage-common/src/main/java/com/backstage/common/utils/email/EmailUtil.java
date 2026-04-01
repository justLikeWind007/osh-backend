package com.backstage.common.utils.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 15:44
 */
@Component
public class EmailUtil {
    // 邮件主题
    public final String subject = "open source helper";
    // 发件人
    public final String from = "18482663265@163.com";
    // 收件人
    public final String[] to = new String[]{"3210728077@qq.com","3891715998@qq.com"};

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    /**
     * 发送邮件
     * @param username 用户名
     * @param email 用户邮箱
     * @return 唯一标识
     */
    public String sendEmailGetUniqueId(String username, String email) throws MessagingException {
        checkEmail(email);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        // 准备模板数据
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("message", "欢迎使用我们的服务！");
        String uniqueId = UUID.randomUUID() + "-" + System.currentTimeMillis();
        context.setVariable("uniqueId", uniqueId);
        // 通过模板引擎生成HTML内容
        String content = springTemplateEngine.process("GetUniqueId", context);
        // 设置邮件内容
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setFrom(from);
        //
        ClassPathResource imageResource = new ClassPathResource("static/open-source-helper.jpg");
        mimeMessageHelper.addInline("logoImage", imageResource);
        javaMailSender.send(mimeMessage);
        return uniqueId;
    }
    /**
     * 发送普通邮件
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendEmailCommon(String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        // 准备模板数据
        Context context = new Context();
        context.setVariable("subject", subject);
        context.setVariable("content", content);
        context.setVariable("sendTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 通过模板引擎生成HTML内容
        String html = springTemplateEngine.process("Common", context);
        // 设置邮件内容
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setFrom(from);
        //
        ClassPathResource imageResource = new ClassPathResource("static/open-source-helper.jpg");
        mimeMessageHelper.addInline("logoImage", imageResource);
        javaMailSender.send(mimeMessage);
    }

    public void checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex)) {
            throw new RuntimeException("邮箱格式错误");
        }
    }
}

package com.cn.common.utils;

import com.cn.common.constant.CacheExpireConstant;
import com.cn.common.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

import static com.cn.common.constant.VerificationCodeConstant.EMAIL_CODE_KEY_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {

    @Value(value = "${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final RedisUtils redisUtils;

    /**
     * Gets code.
     *
     * @param email the email
     * @return the code
     */
    public String getCode(final String email) {
        final Object value = redisUtils.getValue(EMAIL_CODE_KEY_PREFIX + email);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }


    /**
     * Clear code.
     *
     * @param email the email
     */
    public void clearCode(final String email) {
        redisUtils.delKey(EMAIL_CODE_KEY_PREFIX + email);
    }

    @Async
    public void send(final String email,final String code){
        Context context = new Context();
        context.setVariable("code", code);
        String emailContent = templateEngine.process("emailCode.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("Cogni X");
            helper.setFrom(username);
            helper.setTo(email);
            helper.setSentDate(new Date());
            helper.setText(emailContent, true);
            redisUtils.setValueTimeout(EMAIL_CODE_KEY_PREFIX + email, code, CacheExpireConstant.EXPIRE_5_MINUTES);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("构建邮件失败!", e);
            throw new EmailException("获取邮箱验证码失败!请稍后再试!", 500);
        } catch (MailSendException e) {
            log.error("发送邮件失败，邮箱: {}", email, e);
            throw new EmailException("您输入的邮箱账号不存在", 500);
        }
    }
}

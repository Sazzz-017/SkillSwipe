package com.backend.SkillSwipe.kafka.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Value("${app.mail.from}")
    private String fromAddress;

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLikeNotificationEmail(String toEmail,
                                           String likedUserName,
                                           String likedByUserName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Someone liked your SkillSwipe profile \u2764\uFE0F");
            helper.setText(buildEmailBody(likedUserName, likedByUserName), true);

            mailSender.send(message);

            log.info("[Email Service] Notification email sent successfully → recipient='{}', likedBy='{}'",
                    toEmail, likedByUserName);

        } catch (MessagingException e) {
            log.error("[Email Service] Failed to send notification email to '{}': {}",
                    toEmail, e.getMessage(), e);
        }
    }

    private String buildEmailBody(String likedUserName, String likedByUserName) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>SkillSwipe Notification</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f7;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#ffffff;border-radius:8px;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.08);overflow:hidden;">

                          <!-- Header -->
                          <tr>
                            <td style="background-color:#6c63ff;padding:32px;text-align:center;">
                              <h1 style="margin:0;color:#ffffff;font-size:26px;letter-spacing:1px;">
                                SkillSwipe
                              </h1>
                            </td>
                          </tr>

                          <!-- Body -->
                          <tr>
                            <td style="padding:40px 48px;">
                              <p style="font-size:18px;color:#333333;margin:0 0 16px;">
                                Hi <strong>%s</strong>,
                              </p>
                              <p style="font-size:16px;color:#555555;line-height:1.6;margin:0 0 24px;">
                                <strong>%s</strong> liked your profile on SkillSwipe! \uD83C\uDF1F
                              </p>
                              <p style="font-size:16px;color:#555555;line-height:1.6;margin:0 0 32px;">
                                Open SkillSwipe to view their profile and connect.
                              </p>

                              <!-- CTA Button -->
                              <table cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="background-color:#6c63ff;border-radius:6px;padding:14px 28px;">
                                    <a href="#"
                                       style="color:#ffffff;font-size:15px;font-weight:bold;
                                              text-decoration:none;display:inline-block;">
                                      View Profile
                                    </a>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>

                          <!-- Footer -->
                          <tr>
                            <td style="background-color:#f9f9f9;padding:24px 48px;text-align:center;
                                       border-top:1px solid #eeeeee;">
                              <p style="font-size:12px;color:#aaaaaa;margin:0;">
                                You're receiving this because you have an account on SkillSwipe.<br/>
                                &copy; 2025 SkillSwipe. All rights reserved.
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(likedUserName, likedByUserName);
    }
}

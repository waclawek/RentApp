package pl.alpaka.rentalapp.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${waclawek.path}")
    String path;

    public void sendVerificationEmail(User user, String token){
        Context context = new Context();
        context.setVariable("title", "Verify your email address.");
        context.setVariable("link", "http://" + path + "/activate?token=" + token);
        String body = templateEngine.process("emailVerificationNewAccount.html", context);
        sendEmail(user.getEmail(),"Email address verification - RentApp", body);
    }

    private void sendEmail(String email, String title, String body){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(body, true);
            javaMailSender.send(message);
            System.out.println("Email send to [" + email + "] with tilte [" + title + "]");
        }catch (MessagingException e){
            System.out.println(e.getMessage());
        }
    }

    public void sendResetPasswordEmail(User user, String token) {
        Context context = new Context();
        context.setVariable("title", "Reset your password.");
        context.setVariable("link", "http://" + path + "/login/resetPassword?token=" + token);
        String body = templateEngine.process("emailResetPassword.html", context);
        sendEmail(user.getEmail(),"Reset password - RentApp", body);
    }

    public void sendResetPasswordSuccessEmail(User user) {
        Context context = new Context();
        context.setVariable("title", "Password changed");
        context.setVariable("text", "Yours password was successfully changed.");
        String body = templateEngine.process("emailEmpty.html", context);
        sendEmail(user.getEmail(),"Password changed - RentApp", body);
    }
}

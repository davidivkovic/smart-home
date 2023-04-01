package bsep.email;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.CheckedTemplate;

@CheckedTemplate
public class ConfirmEmail {

    public static native MailTemplate.MailTemplateInstance confirmEmail(String name, String code);

    public static void send(String email, String name, String code) {
        confirmEmail(name, code)
            .to(email)
            .subject("Confirm your email address")
            .send()
            .subscribe()
            .with(
                id -> System.out.println("Email sent to " + email),
                failure -> {
                    System.out.println("Email failed to " + email);
                    failure.printStackTrace();
                }
            );
    }
}

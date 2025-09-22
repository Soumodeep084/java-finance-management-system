package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;

public class MailService {

    public enum SendType {
        REGISTRATION, FORGOT_OTP, DELETE_CONFIRM, GENERIC
    }

    private static final SecureRandom SECURE = new SecureRandom();

    private final Session mailSession;

    public MailService(String host, int port, String username, String password) {
        Properties p = new Properties();
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", String.valueOf(port));
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        this.mailSession = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void send(SendType type, String fromEmail, String toEmail, Map<String, String> data) throws MessagingException {
        String subject = subjectFor(type, data);
        String[] bodies = bodyFor(type, data);

        MimeMessage msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        msg.setSubject(subject, "UTF-8");

        MimeBodyPart text = new MimeBodyPart();
        text.setText(bodies[0], "UTF-8");

        MimeBodyPart html = new MimeBodyPart();
        html.setContent(bodies[1], "text/html; charset=UTF-8");

        Multipart alt = new MimeMultipart("alternative");
        alt.addBodyPart(text);
        alt.addBodyPart(html);

        msg.setContent(alt);
        Transport.send(msg);
    }

    public static String generateOtp6() {
        int n = SECURE.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private String subjectFor(SendType t, Map<String, String> d) {
        return switch (t) {
            case REGISTRATION ->
                "Welcome to Wealthory";
            case FORGOT_OTP ->
                "Your Password Reset OTP";
            case DELETE_CONFIRM ->
                "Account Deletion OTP";
            case GENERIC ->
                d.getOrDefault("subject", "Notification");
        };
    }

    private String[] bodyFor(SendType t, Map<String, String> d) {
        String appName = d.getOrDefault("appName", "Wealthory");

        // Shared header and footer
        String header = """
      <tr>
        <td style="text-align:center;padding:24px 0;background:#0f766e;border-radius:12px 12px 0 0">
          <img src="https://www.canva.com/design/DAGrb85KVjE/diTbFOvxAdQEW9rjPrlj1A/view?utm_content=DAGrb85KVjE&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h5f53336e11" alt="Wealthory"
               style="display:block;margin:0 auto;max-width:160px">
          <h1 style="margin:12px 0 0;font-size:22px;color:#ffffff">%s</h1>
        </td>
      </tr>
    """;

        String footer = """
      <tr>
        <td style="text-align:center;padding:20px;background:#f1f5f9;
                   border-top:1px solid #e2e8f0;border-radius:0 0 12px 12px">
          <p style="margin:0;font-size:14px;color:#64748b">
            © 2025 Wealthory, All rights reserved.<br/>
            <a href="https://wealthory.com" style="color:#0f766e;text-decoration:none">Visit our website</a>
          </p>
        </td>
      </tr>
    """;

        return switch (t) {
            case REGISTRATION -> {
                String name = d.getOrDefault("name", "there");
                String text = "Hi " + name + ", Welcome to " + appName + "! Your account has been created successfully.";
                String html = """
              <div style="font-family:Inter,Arial,sans-serif;background:#f9fafb;padding:20px">
                <table align="center" width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:12px;
                              box-shadow:0 4px 12px rgba(0,0,0,0.05)">
                  %s
                  <tr>
                    <td style="padding:24px 32px;color:#0f172a">
                      <p style="margin:0 0 12px;font-size:16px">Hi %s,</p>
                      <p style="margin:0 0 12px;font-size:16px">
                        Your account has been created successfully. We’re excited to have you onboard 🚀
                      </p>
                      <p style="margin:0;font-size:16px;color:#475569">
                        Thanks,<br/><b>The Wealthory Team</b>
                      </p>
                    </td>
                  </tr>
                  %s
                </table>
              </div>
            """.formatted(header.formatted("Welcome to Wealthory!"), name, footer);
                yield new String[]{text, html};
            }
            case FORGOT_OTP -> {
                String otp = d.getOrDefault("otp", "000000");
                String minutes = d.getOrDefault("minutes", "10");
                String text = "Use this OTP to reset your password: " + otp + " (valid " + minutes + " minutes).";
                String html = """
              <div style="font-family:Inter,Arial,sans-serif;background:#f9fafb;padding:20px">
                <table align="center" width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:12px;
                              box-shadow:0 4px 12px rgba(0,0,0,0.05)">
                  %s
                  <tr>
                    <td style="padding:24px 32px;color:#0f172a">
                      <h2 style="color:#0f766e;margin:0 0 12px">Password Reset</h2>
                      <p style="margin:0 0 12px;font-size:16px">
                        Use this OTP to reset your password:
                      </p>
                      <div style="font-size:22px;letter-spacing:4px;font-weight:700;
                                  background:#ecfeff;color:#0e7490;
                                  display:inline-block;padding:12px 20px;
                                  border-radius:8px;margin:12px 0">%s</div>
                      <p style="margin:12px 0;font-size:16px;color:#475569">
                        Valid for %s minutes.
                      </p>
                      <p style="color:#0f172a;margin:12px 0 0;font-weight:600">Important:</p>
                      <ul style="color:#475569;margin:6px 0 0;padding-left:18px">
                        <li>Complete this reset on the <b>same device and browser</b> that requested the OTP.</li>
                        <li>Do not share this code with anyone.</li>
                      </ul>
                    </td>
                  </tr>
                  %s
                </table>
              </div>
            """.formatted(header.formatted("Password Reset"), otp, minutes, footer);
                yield new String[]{text, html};
            }
            case DELETE_CONFIRM -> {
                String otp = d.getOrDefault("otp", "000000");
                String minutes = d.getOrDefault("minutes", "10");
                String text = "Confirm account deletion with this OTP: " + otp + " (valid " + minutes + " minutes).";
                String html = """
              <div style="font-family:Inter,Arial,sans-serif;background:#f9fafb;padding:20px">
                <table align="center" width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:12px;
                              box-shadow:0 4px 12px rgba(0,0,0,0.05)">
                  %s
                  <tr>
                    <td style="padding:24px 32px;color:#0f172a">
                      <h2 style="color:#b91c1c;margin:0 0 12px">Confirm Account Deletion</h2>
                      <p style="margin:0 0 12px;font-size:16px">Enter this OTP to proceed:</p>
                      <div style="font-size:22px;letter-spacing:4px;font-weight:700;
                                  background:#fee2e2;color:#b91c1c;
                                  display:inline-block;padding:12px 20px;
                                  border-radius:8px;margin:12px 0">%s</div>
                      <p style="margin:12px 0;font-size:16px;color:#475569">
                        Valid for %s minutes.
                      </p>
                      <p style="color:#0f172a;margin:12px 0 0;font-weight:600">Important:</p>
                      <ul style="color:#475569;margin:6px 0 0;padding-left:18px">
                        <li>Continue on the <b>same device and browser</b> that requested this OTP.</li>
                        <li>Do not share this code with anyone.</li>
                      </ul>
                    </td>
                  </tr>
                  %s
                </table>
              </div>
            """.formatted(header.formatted("Confirm Deletion"), otp, minutes, footer);
                yield new String[]{text, html};
            }
            case GENERIC -> {
                String text = d.getOrDefault("text", "Hello from " + appName);
                String content = d.getOrDefault("html", """
              <p style="margin:0;font-size:16px">%s</p>
            """.formatted(text));
                String html = """
              <div style="font-family:Inter,Arial,sans-serif;background:#f9fafb;padding:20px">
                <table align="center" width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:12px;
                              box-shadow:0 4px 12px rgba(0,0,0,0.05)">
                  %s
                  <tr>
                    <td style="padding:24px 32px;color:#0f172a">
                      %s
                    </td>
                  </tr>
                  %s
                </table>
              </div>
            """.formatted(header.formatted("Notification"), content, footer);
                yield new String[]{text, html};
            }
        };
    }

    public static MailService usingMailtrap(String username, String password) {
        return new MailService("sandbox.smtp.mailtrap.io", 587, username, password);
    }
}

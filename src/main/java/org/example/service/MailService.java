package org.example.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.example.dto.register.RegisterDataDto;

import com.fasterxml.jackson.core.util.RequestPayload;

public class MailService {

    private static final SecureRandom random = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();
    private final Map<String, RegisterDataDto> users = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    // Configuración SMTP (tu correo Gmail)
    private final String smtpUser;     // ejemplo: "tucorreo@gmail.com"
    private final String smtpAppPass;  // App Password de Google

    public MailService(String smtpUser, String smtpAppPass) {
        this.smtpUser = smtpUser;
        this.smtpAppPass = smtpAppPass;

        // Limpieza automática de códigos expirados cada 1 min
        cleaner.scheduleAtFixedRate(this::removeExpired, 1, 1, TimeUnit.MINUTES);
    }

    private static class CodeEntry {
        final String code;
        final Instant expiresAt;

        CodeEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    // Genera un código de 6 dígitos (000000–999999)
    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    /**
     * Genera, guarda y envía un código de verificación al correo indicado.
     * @param email correo destinatario
     * @param ttlSeconds tiempo de vida del código en segundos (ej: 300 = 5 minutos)
     * @throws MessagingException si ocurre un error al enviar el correo
     */
    public void generateAndSend(String email, long ttlSeconds, RegisterDataDto dto) throws MessagingException {
        String code = generate6DigitCode();
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);
        codes.put(email.toLowerCase(), new CodeEntry(code, expiresAt));
        users.put(code, dto);

        String subject = "código de verificación";
        String body = "Hola,\n\nTu código de verificación es: " + code +
                      "\nEste código expirará en " + (ttlSeconds / 60) + " minutos.\n\nSaludos.";

        sendEmail(email, subject, body);
    }

    // Verifica un código
    public boolean verifyCode(String email, String code) {
        CodeEntry entry = codes.get(email.toLowerCase());
        if (entry == null) return false;
        if (Instant.now().isAfter(entry.expiresAt)) {
            codes.remove(email.toLowerCase());
            return false;
        }
        boolean ok = entry.code.equals(code);
        if (ok) codes.remove(email.toLowerCase()); // usar solo una vez
        return ok;
    }

    // Elimina los códigos expirados
    private void removeExpired() {
        Instant now = Instant.now();
        codes.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt));
    }

    // Envía el correo con JavaMail (SMTP Gmail)
    private void sendEmail(String to, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpAppPass);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(smtpUser));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    public RegisterDataDto getUserPayload(String Code){
        return users.get(Code);
    }

    public void shutdown() {
        cleaner.shutdown();
    }
}

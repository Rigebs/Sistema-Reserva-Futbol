package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.User; // Asegúrate de que esta clase sea la que representa a los usuarios.
import com.cruz_sur.api.repository.UserRepository; // Este es el repositorio que consultas para obtener los usuarios.
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository; // Este es el repositorio que usas para consultar usuarios.

    // Enviar correo a todos los usuarios con sedeId
    public void sendEmailToUsersWithSedeId(String subject, String text) throws MessagingException {
        List<User> usersWithSedeId = userRepository.findBySedeIsNotNull();

        for (User user : usersWithSedeId) {
            sendVerificationEmailHtml(user.getEmail(), subject, text, user.getUsername());  // Ahora pasamos el userName
        }
    }

    // Enviar correo a todos los usuarios con clienteId
    public void sendEmailToUsersWithClienteId(String subject, String text) throws MessagingException {
        List<User> usersWithClienteId = userRepository.findByClienteIsNotNull();

        for (User user : usersWithClienteId) {
            sendVerificationEmailHtml(user.getEmail(), subject, text, user.getUsername());  // Ahora pasamos el userName
        }
    }
    // Método que ya tienes para enviar correos de verificación (lo reutilizamos)
    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }

    public void sendVerificationEmailHtml(String to, String subject, String text, String userName) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Establecer el destinatario, asunto y el contenido en HTML
        helper.setTo(to);
        helper.setSubject(subject);

        // Aquí va el HTML bonito para el cuerpo del correo
        String htmlContent = buildHtmlEmailContent(text, userName); // Ahora pasamos ambos parámetros

        helper.setText(htmlContent, true); // El 'true' asegura que el contenido se maneje como HTML

        emailSender.send(message);
    }

    // Método para crear el contenido HTML bonito
    private String buildHtmlEmailContent(String messageContent, String userName) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "    body { font-family: 'Arial', sans-serif; color: #333333; background-color: #f4f4f4; padding: 20px; }" +
                "    .email-container { background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }" +
                "    .header { text-align: center; padding-bottom: 20px; }" +
                "    .header img { width: 150px; }" +
                "    .content { font-size: 16px; line-height: 1.6; color: #555555; }" +
                "    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777777; }" +
                "    .footer a { color: #0056b3; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='header'>" +
                "            <img src='https://res.cloudinary.com/dpfcpo5me/image/upload/f_auto,q_auto/ogj9ugk9o0sjhqb3aw7c' alt='Zemply Logo' />" +
                "        </div>" +
                "        <div class='content'>" +
                "            <h2>¡Hola, " + userName + "!</h2>" + // Aquí agregamos el nombre del usuario
                "            <p>" + messageContent + "</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Gracias por ser parte de Zemply.</p>" +
                "            <p><a href='https://zemply.vercel.app/'>Visita nuestro sitio web</a></p>" + // Enlace actualizado
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

}

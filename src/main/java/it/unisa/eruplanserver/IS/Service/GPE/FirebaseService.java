package it.unisa.eruplanserver.IS.Service.GPE;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    /**
     * Invia una notifica push a tutti i dispositivi iscritti a un determinato topic.
     * * @param titolo Il titolo della notifica
     * @param corpo Il corpo del messaggio
     * @param topic Il canale di sottoscrizione ("emergenza")
     */
    public String inviaNotificaBroadcast(String titolo, String corpo, String topic) {
        try {
            // Costruiamo la notifica
            Notification notification = Notification.builder()
                    .setTitle(titolo)
                    .setBody(corpo)
                    .build();

            // Costruiamo il messaggio indirizzato al topic
            Message message = Message.builder()
                    .setNotification(notification)
                    .setTopic(topic)
                    .build();

            // Invio effettivo
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notifica inviata con successo: " + response);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore nell'invio: " + e.getMessage();
        }
    }
}
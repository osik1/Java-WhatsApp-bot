package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import static spark.Spark.*;

import com.twilio.Twilio;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;

import java.util.HashMap;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;


public class Main {
    private static final Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String accountSid = dotenv.get("TWILIO_ACCOUNT_SID");
        String authToken = dotenv.get("TWILIO_AUTH_TOKEN");

        // Initialize Twilio
        Twilio.init(accountSid, authToken);

        // Set the port for the server
        port(3000);

        // Configure routes
        post("/incoming", (req, res) -> {
            // Extract the incoming message from the request
            String messageBody = req.queryParams("Body");

            // Create a Twilio MessagingResponse
            MessagingResponse twiml;

            try {
                // Get AI or predefined reply
                String aiReply = reply(messageBody);

                // Build the response
                Message smsMessage = new Message.Builder()
                        .body(new Body.Builder(aiReply).build())
                        .build();
                twiml = new MessagingResponse.Builder().message(smsMessage).build();
            } catch (Exception e) {
                System.err.println("Error handling incoming message: " + e.getMessage());

                // Error response
                Message errorMessage = new Message.Builder()
                        .body(new Body.Builder("Sorry, an error occurred while processing your request.").build())
                        .build();
                twiml = new MessagingResponse.Builder().message(errorMessage).build();
            }

            // Return the response as XML
            res.type("text/xml");
            return twiml.toXml();
        });

        System.out.println("Server running on port 3000...");
    }

    private static String reply(String msg) {
        // Define responses
        Map<String, String> responses = new HashMap<>();
        responses.put("hi", "Hi there! This is our menu: Gob3 = Ghc10 per plate, Banku = Ghc30, Kenkey = Ghc50. Please reply with what you want.");
        responses.put("hello", "Hi there! This is our menu: Gob3 = Ghc10 per plate, Banku = Ghc30, Kenkey = Ghc50. Please reply with what you want.");
        responses.put("gob3", "Alright, Send your location please. It will cost Ghc10.");
        responses.put("banku", "Alright, Send your location please. It will cost Ghc30.");
        responses.put("kenkey", "Alright, Send your location please. It will cost Ghc50.");
        responses.put("location", "You will receive your order in few minutes");
        responses.put("help", "Sure! Please tell me more about your issue. These are the responses I know now, hello, hi, Gob3, Banku, Kenkey, Location, Help, alright, thanks and Bye");
        responses.put("bye", "Goodbye! Have a great day!");
        responses.put("alright", "yeah");
        responses.put("thanks", "you welcome");

        // Convert the input to lowercase for easier matching
        String lowerCaseMsg = msg.toLowerCase();

        // Find a matching response
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (lowerCaseMsg.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default response if no keyword matches
        return "I am not sure how to respond to that. Can you please clarify?";
    }
}

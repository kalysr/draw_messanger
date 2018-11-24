package com.example.apple.geektech.Library;

import java.io.IOException;
import java.net.URL;

import static java.lang.System.out;

public class App {


    public static void telegram(String text) {

        if (text.length() > 0) {

            out.println("TAG: " + text);

            String telegramURI = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
            String BOT_API_KEY = "665163646:AAErLypAacYvnMCRWTkO2u40fkvJadv_zks";
            String CHANNEL = "@draw_messenger";

            String urlString = String.format(telegramURI, BOT_API_KEY, CHANNEL, text);

            try {
                URL url = new URL(urlString);
                url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

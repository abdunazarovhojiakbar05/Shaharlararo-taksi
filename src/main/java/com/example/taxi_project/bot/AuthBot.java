package com.example.taxi_project.bot;

 import com.example.taxi_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AuthBot extends TelegramLongPollingBot {



    @Value("${telegram.bot.token}")
    private String botToken;




    @Value("${telegram.bot.username}")
    private String botUsername;

    private static final String digits = "0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final AuthService authService;


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (text.equals("/start")) {
                 org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();

                Long telegramId = tgUser.getId();
                String firstName = tgUser.getFirstName();
                String lastName = tgUser.getLastName();
                String username = tgUser.getUserName();


                String code = generateCode();

                authService.data(telegramId, firstName, lastName, username, code);

                String response = "Sizning kodingiz: " + code + "\n\n" +
                        "👤 Ism: " + firstName  +  " <= shu nom ostida ilovaga otishingiz mumkin!  \n" +
                        (lastName != null ? " " + lastName : "") +
                        (username != null ? "\n🔗 Username: @" + username : "") +
                        "\n🆔 Telegram ID: " + telegramId;

                sendMessage(chatId, response);
            }


        }
    }

    public String generateCode(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 6; i++){
            sb.append(digits.charAt(random.nextInt(digits.length())));
        }
        return sb.toString();
    }

    public void sendMessage(long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }


}

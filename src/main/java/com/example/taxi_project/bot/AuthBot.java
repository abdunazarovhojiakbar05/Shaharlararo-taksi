package com.example.taxi_project.bot;

import com.example.taxi_project.enums.BotState;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.service.AuthService;
import com.example.taxi_project.service.DriverService;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final DriverService driverService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private static final String digits = "0123456789";
    private static final SecureRandom random = new SecureRandom();

     private final Map<Long, BotState> userStateMap = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> userDataMap = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public String getBotToken() { return botToken; }

    @Override
    public String getBotUsername() { return botUsername; }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
            return;
        }

        if (!update.hasMessage()) return;
        long chatId = update.getMessage().getChatId();

        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            if (text.equals("/start")) {
                handleStartCommand(update, chatId);
                return;
            }

            BotState state = userStateMap.get(chatId);
            if (state == BotState.ENTER_CAR_MODEL) {
                handleCarModelInput(text, chatId);
                return;
            }
        }

        if (update.getMessage().hasContact()) {
            handleContactReceived(update, chatId);
            return;
        }

        if (update.getMessage().hasPhoto()) {
            BotState state = userStateMap.get(chatId);
            if (state != null) {
                String fileId = update.getMessage().getPhoto()
                        .get(update.getMessage().getPhoto().size() - 1)
                        .getFileId();
                handlePhotoInput(state, fileId, chatId);
            }
        }
    }

    private void requestContact(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("📱 Davom etish uchun telefon raqamingizni yuboring:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton contactButton = new KeyboardButton("📱 Raqamni yuborish");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Contact so'rovida xatolik: ", e);
        }
    }

    private void handleStartCommand(Update update, long chatId) {
        Optional<User> existing = userRepository.findByChatId(chatId);

        if (existing.isPresent() && existing.get().isActive()) {
            sendDriverRegistrationMenu(chatId, "✅ Siz allaqachon ro'yxatdan o'tgansiz. Ilovaga kiring.\n\nAgar haydovchi sifatida ro'yxatdan o'tmoqchi bo'lsangiz, pastdagi tugmani bosing:");
            return;
        }

        if (existing.isPresent() && existing.get().getExpired_at() != null && existing.get().getExpired_at().isAfter(LocalDateTime.now())) {
            sendDriverRegistrationMenu(chatId, "⏳ Kod hali amal qilmoqda. 5 daqiqa kuting.\n\nAgar haydovchi sifatida hujjat topshirmoqchi bo'lsangiz, tugmani bosing:");
            return;
        }

        requestContact(chatId);
    }

    private void handleContactReceived(Update update, long chatId) {
        org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
        String firstName = tgUser.getFirstName();
        String username = tgUser.getUserName();
        String phone = update.getMessage().getContact().getPhoneNumber();

        String code = generateCode();

        String response = "✅ Tasdiqlash kodi tayyor!\n\n" +
                "<pre><code>" + code + "</code></pre>\n\n" +
                "👤 Ism: " + firstName + "\n" +
                (username != null ? "🔗 @" + username + "\n" : "") +
                "⏳ Kod 5 daqiqa ichida amal qiladi.\n" +
                "📱 Ilovaga kirib kodni kiriting.\n\n" +
                "————————————————————\n" +
                "🚖 Agar siz <b>Haydovchi</b> sifatida ro'yxatdan o'tmoqchi bo'lsangiz, pastdagi tugmani bosing:";

        try {
            authService.data(firstName, username, code, chatId, phone);
            sendDriverRegistrationMenu(chatId, response);
        } catch (ValidationException e) {
            log.error("Validation xatoligi: ", e);
            sendMessage(chatId, "❌ Ro'yxatdan o'tishda xatolik yuz berdi. Qayta urinib ko'ring.");
        } catch (Exception e) {
            log.error("Kutilmagan xatolik: ", e);
            sendMessage(chatId, "❌ Tizimda xatolik yuz berdi.");
        }
    }


    private void handleCallbackQuery(Update update) {
        String callData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callData.equals("register_as_driver")) {
            userStateMap.put(chatId, BotState.UPLOAD_PASSPORT_IMG);
            sendMessage(chatId, "📸 Bo'ldi, boshladik! Birinchi bo'lib Pasportingiz rasmini yuboring:");
        }
    }

    private void handleCarModelInput(String carModel, long chatId) {

        Map<String, String> data = userDataMap.getOrDefault(chatId, new java.util.concurrent.ConcurrentHashMap<>());
        data.put("car_model", carModel);
        userDataMap.put(chatId, data);

        userStateMap.put(chatId, BotState.UPLOAD_CAR_IMG);
        sendMessage(chatId, "📸 Mashina rasmini yuboring\nmashina raqami aniq korinsin!");
    }

    private void handlePhotoInput(BotState state, String fileId, long chatId) {

        Map<String, String> data = userDataMap.getOrDefault(chatId, new java.util.concurrent.ConcurrentHashMap<>());
        if (state == BotState.UPLOAD_PASSPORT_IMG) {
            data.put("passport_image", fileId);
            userDataMap.put(chatId, data);
            userStateMap.put(chatId, BotState.UPLOAD_LICENSE_IMG);
            sendMessage(chatId, "📸 Haydovchilik guvohnomasi (prava) rasmini yuboring:");

        } else if (state == BotState.UPLOAD_LICENSE_IMG) {
            data.put("license_image", fileId);
            userDataMap.put(chatId, data);
            userStateMap.put(chatId, BotState.ENTER_CAR_MODEL);
            sendMessage(chatId, "🚗 Mashina modelini kiriting (masalan: Nexia 3):");

         } else if (state == BotState.UPLOAD_CAR_IMG) {
            data.put("car_image", fileId);
            userDataMap.put(chatId, data);

            try {
                driverService.apply(chatId, data);
                userDataMap.remove(chatId);
                userStateMap.remove(chatId);

                sendMessage(chatId, "✅ Arizangiz muvaffaqiyatli yuborildi! Administratorlar ko'rib chiqib javobini yuborishadi.");
            } catch (Exception e) {
                log.error("Haydovchi arizasini saqlashda xatolik: ", e);
                sendMessage(chatId, "❌ Arizani yuborishda xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
            }
        }

    }



    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xatolik: ", e);
        }
    }

    private void sendDriverRegistrationMenu(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("🚖 Haydovchi bo'lib ro'yxatdan o'tish");
        button.setCallbackData("register_as_driver");

        rowInline.add(button);
        rowsInline.add(rowInline);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Tugmali xabar yuborishda xatolik: ", e);
        }
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(digits.charAt(random.nextInt(digits.length())));
        }
        return sb.toString();
    }
}
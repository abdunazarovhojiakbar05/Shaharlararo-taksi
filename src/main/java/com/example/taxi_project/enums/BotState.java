package com.example.taxi_project.enums;

public enum BotState {

    START,
    SHARE_CONTACT,     // Telefon raqam olish (Verify qilish)
    MAIN_MENU,         // Asosiy menyu (Shu yerda "Haydovchi bo'lish" tugmasi chiqadi)

    // Haydovchilik bosqichlari (Step-by-Step)

    UPLOAD_PASSPORT_IMG,
    UPLOAD_LICENSE_IMG,
    ENTER_CAR_MODEL,
    UPLOAD_CAR_IMG,
    APPLICATION_SUBMITTED
}

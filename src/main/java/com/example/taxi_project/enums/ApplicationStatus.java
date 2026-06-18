package com.example.taxi_project.enums;

public enum ApplicationStatus {
    PENDING,    // Ariza topshirildi, admin tekshiruvini kutyapti
    APPROVED,   // Admin tasdiqladi (Foydalanuvchi endi Driver-ga aylandi)
    REJECTED    // Rasmlar xato yoki soxta bo'lgani uchun rad etildi
}

const API_URL = 'http://localhost:8081/api/v1/auth/verify';
let timeLeft = 5 * 60; // 5 daqiqa soniyalarda
const countdownElement = document.getElementById('countdown');
const submitBtn = document.getElementById('submitBtn');
const otpInput = document.getElementById('otpCode');

// 1. Taymer mantiqi
const timer = setInterval(() => {
    if (timeLeft <= 0) {
        clearInterval(timer);
        countdownElement.innerText = "Vaqt tugadi!";
        submitBtn.disabled = true;
        otpInput.disabled = true;
    } else {
        timeLeft--;
        let minutes = Math.floor(timeLeft / 60);
        let seconds = timeLeft % 60;
        minutes = minutes < 10 ? '0' + minutes : minutes;
        seconds = seconds < 10 ? '0' + seconds : seconds;
        countdownElement.innerText = `${minutes}:${seconds}`;
    }
}, 1000);

// 2. API-ga kodni yuborish
async function verifyCode() {
    const codeValue = otpInput.value.trim();
    const messageBox = document.getElementById('messageBox');

    if (codeValue.length < 6) {
        messageBox.className = "message-box error";
        messageBox.innerText = "Iltimos, 6 xonali kodni to'liq kiriting!";
        return;
    }

    try {
        submitBtn.disabled = true;
        messageBox.className = "message-box";
        messageBox.innerText = "Tekshirilmoqda...";

        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ code: codeValue }) // VerifyOtpRequest-dagi 'code' fieldi
        });

        if (response.ok) {
            const data = await response.json(); // LoginResponseDto keladi

            messageBox.className = "message-box success";
            messageBox.innerText = `Xush kelibsiz, ${data.name}! Yo'naltirilmoqda...`;

            // Backend-dan kelgan aniq field nomlari bo'yicha saqlaymiz
            if (data.access_token) {
                localStorage.setItem('access_token', data.access_token);
                localStorage.setItem('refresh_token', data.refresh_token);
                localStorage.setItem('user_name', data.name);
            }

            setTimeout(() => {
                window.location.href = '../dashboard/index.html';
            }, 1500);

        } else {
            const errorData = await response.json().catch(() => ({}));
            messageBox.className = "message-box error";
            messageBox.innerText = errorData.message || "Kod noto'g'ri yoki muddati o'tgan!";
            submitBtn.disabled = false;
        }

    } catch (error) {
        messageBox.className = "message-box error";
        messageBox.innerText = "Server bilan aloqa uzildi: " + error.message;
        submitBtn.disabled = false;
    }
}
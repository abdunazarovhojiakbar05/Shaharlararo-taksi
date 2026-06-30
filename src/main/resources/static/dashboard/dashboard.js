const USER_API = 'http://localhost:8081/api/v1/user';

document.addEventListener("DOMContentLoaded", () => {
    checkAuthAndLoadUser();
});

async function checkAuthAndLoadUser() {
    // 1. Tokenni localStoragedan tekshiramiz
    const token = localStorage.getItem('access_token');

    if (!token) {
        alert("Siz tizimga kirmagansiz! Iltimos, login qiling.");
        window.location.href = '../auth/login.html';
        return;
    }

    try {
        // 2. API-ga tokenni Header orqali yuboramiz
        const response = await fetch(USER_API, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });

        // Agar server muvaffaqiyatli javob bersa (Status: 200)
        if (response.ok) {
            const userResponse = await response.json();
            console.log("Backenddan kelgan ma'lumot:", userResponse);

            // Ma'lumotlarni maydonlar nomiga qarab chiqaramiz
            document.getElementById('currentUser').innerText = userResponse.name || userResponse.username || "Foydalanuvchi";
            document.getElementById('userId').innerText = userResponse.id || "-";
            document.getElementById('userName').innerText = userResponse.name || "-";
            document.getElementById('userPhone').innerText = userResponse.phone || "-";
            document.getElementById('userRole').innerText = userResponse.role || "USER";

        } else {
            // Agar 401 yoki 403 xato bo'lsa, konsolda ko'rinadi
            console.error("Server rad etdi. Status kod:", response.status);
            document.getElementById('currentUser').innerText = "Tizimga kirish rad etildi (Status: " + response.status + ")";
        }

    } catch (error) {
        // Agar fetch umuman ishlamasa (tarmoq yoki CORS xatosi)
        console.error("Fetch xatoligi:", error);
        document.getElementById('currentUser').innerText = "Aloqa xatosi: " + error.message;
    }
}

// Bo'limlarni almashtirish
function switchSection(sectionName) {
    document.querySelectorAll('.data-section').forEach(sec => sec.classList.add('hidden'));
    document.querySelectorAll('.menu-item').forEach(item => item.classList.remove('active'));

    document.getElementById(`${sectionName}-section`).classList.remove('hidden');

    // Agar buyurtmalar bo'limi ochilsa, tarixni yuklaymiz
    if (sectionName === 'orders') {
        loadMyOrders();
    }
}

// Chiqish
function logout() {
    localStorage.clear();
    window.location.href = '../auth/login.html';
}


const ORDERS_API = 'http://localhost:8081/api/v1/orders';

// 1. GET /api/v1/orders/my — Buyurtmalar tarixini yuklash
async function loadMyOrders() {
    const token = localStorage.getItem('access_token');
    const tableBody = document.getElementById('myOrdersTableBody');

    try {
        const response = await fetch(`${ORDERS_API}/my`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const orders = await response.json(); // List<MyOrdersResponse> keladi
            tableBody.innerHTML = '';

            if (orders.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center; padding:15px;">Sizda hali buyurtmalar mavjud emas.</td></tr>`;
                return;
            }

            orders.forEach(order => {
                const date = new Date(order.createdAt).toLocaleString();
                tableBody.innerHTML += `
                    <tr>
                        <td style="padding:12px; border-bottom:1px solid #ddd;">${order.id.substring(0, 8)}...</td>
                        <td style="padding:12px; border-bottom:1px solid #ddd;">${order.fromAddress}</td>
                        <td style="padding:12px; border-bottom:1px solid #ddd;">${order.toAddress}</td>
                        <td style="padding:12px; border-bottom:1px solid #ddd;">${order.price.toLocaleString()} UZS</td>
                        <td style="padding:12px; border-bottom:1px solid #ddd;"><span style="background:#e0e0e0; padding:3px 8px; border-radius:4px; font-size:12px;">${order.status}</span></td>
                        <td style="padding:12px; border-bottom:1px solid #ddd;">${date}</td>
                    </tr>
                `;
            });
        } else {
            tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:red; padding:15px;">Yuklashda xatolik: ${response.status}</td></tr>`;
        }
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:red; padding:15px;">Aloqa xatosi: ${error.message}</td></tr>`;
    }
}


async function createNewOrder() {
    const token = localStorage.getItem('access_token');
    const msgBox = document.getElementById('orderMessage');

    const fLat = document.getElementById('fromLat').value.trim();
    const fLon = document.getElementById('fromLon').value.trim();
    const tLat = document.getElementById('toLat').value.trim();
    const tLon = document.getElementById('toLon').value.trim();

    // Faqat koordinatalar to'ldirilganini tekshiramiz
    if (!fLat || !fLon || !tLat || !tLon) {
        msgBox.style.color = "red";
        msgBox.innerText = "Iltimos, barcha koordinatalarni to'ldiring!";
        return;
    }

    const orderData = {
        fromLat: parseFloat(fLat),
        fromLon: parseFloat(fLon),
        toLat: parseFloat(tLat),
        toLon: parseFloat(tLon)
    };

    try {
        const response = await fetch(ORDERS_API, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });

        if (response.ok) {
            msgBox.style.color = "green";
            msgBox.innerText = "Buyurtma muvaffaqiyatli yaratildi! Haydovchi kutilmoqda...";

            // Inputlarni tozalash
            document.getElementById('fromLat').value = '';
            document.getElementById('fromLon').value = '';
            document.getElementById('toLat').value = '';
            document.getElementById('toLon').value = '';

            loadMyOrders(); // Tarix jadvalini yangilash
        } else {
            msgBox.style.color = "red";
            msgBox.innerText = "Xatolik yuz berdi. Status: " + response.status;
        }
    } catch (error) {
        msgBox.style.color = "red";
        msgBox.innerText = "Xatolik: " + error.message;
    }
}
const API_BASE = "http://localhost:8081/api/v1/orders";

// 1. Sahifa yuklanganda token tekshirish va jarayonni boshlash
document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("access_token");
    if (!token) {
        window.location.href = "../login.html"; // driver ichida bo'lgani uchun bitta papka tepaga chiqadi
        return;
    }

    document.getElementById("driverInfo").innerText = "Tizimga haydovchi sifatida kirildi";

    // Bo'sh buyurtmalarni yuklash
    loadPendingOrders();

    // Har 10 sekundda yangi buyurtmalarni avtomat tekshirib turish
    setInterval(loadPendingOrders, 10000);
});

// 2. Bo'sh buyurtmalarni olish (GET /api/v1/orders/pending)
async function loadPendingOrders() {
    const token = localStorage.getItem("access_token");
    const tableBody = document.getElementById("pendingOrdersTable");
    const errorBox = document.getElementById("errorBox");

    try {
        const response = await fetch(`${API_BASE}/pending`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error("Buyurtmalarni yuklashda xatolik: " + response.status);

        const orders = await response.json();
        tableBody.innerHTML = "";
        errorBox.innerText = "";

        if (orders.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="4" style="text-align: center;">Hozircha bo'sh buyurtmalar mavjud emas.</td></tr>`;
            return;
        }

        orders.forEach(order => {
            tableBody.innerHTML += `
                <tr>
                    <td>${order.id.substring(0, 8)}...</td>
                    <td>${order.fromLat}, ${order.fromLon} <a href="https://www.google.com/maps?q=${order.fromLat},${order.fromLon}" target="_blank">🗺️</a></td>
                    <td>${order.toLat}, ${order.toLon} <a href="https://www.google.com/maps?q=${order.toLat},${order.toLon}" target="_blank">🗺️</a></td>
                    <td>
                        <button class="btn btn-accept" onclick="acceptOrder('${order.id}')">Qabul qilish</button>
                    </td>
                </tr>
            `;
        });

    } catch (error) {
        errorBox.innerText = "Aloqa xatosi: " + error.message;
    }
}

// 3. Buyurtmani qabul qilish (POST /api/v1/orders/{id}/accept)
async function acceptOrder(orderId) {
    const token = localStorage.getItem("access_token");

    try {
        const response = await fetch(`${API_BASE}/${orderId}/accept`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            alert("Buyurtma muvaffaqiyatli qabul qilindi!");
            loadPendingOrders();
            // Qabul qilingan zahoti haydovchiga 'accepted' holatidagi interfeysni chizamiz
            showActiveTrip(orderId, 'accepted');
        } else {
            const errorText = await response.text();
            alert("Xatolik: " + errorText);
        }
    } catch (error) {
        alert("Xatolik yuz berdi: " + error.message);
    }
}

// 4. Aktiv safarni boshqarish blokini chizish (Dinamik interfeys)
function showActiveTrip(orderId, currentStatus) {
    const card = document.getElementById("activeTripCard");
    const details = document.getElementById("tripDetails");
    const actions = document.getElementById("tripActions");

    card.style.display = "block";

    if (currentStatus === 'accepted') {
        details.innerHTML = `<b>Buyurtma ID:</b> ${orderId}<br>📍 Mijoz tomon yo'lga chiqdingiz. Manzilga yetib borgach <b>'Yetib keldim'</b> tugmasini bosing.`;
        actions.innerHTML = `
            <button class="btn btn-start" onclick="updateTripStatus('${orderId}', 'arrived')">📍 Yetib keldim</button>
        `;
    }
    else if (currentStatus === 'arrived') {
        details.innerHTML = `<b>Buyurtma ID:</b> ${orderId}<br>⏳ Mijozni kutyapsiz. Mijoz mashinaga o'tirgach <b>'Safarni boshlash'</b> tugmasini bosing.`;
        actions.innerHTML = `
            <button class="btn btn-accept" onclick="updateTripStatus('${orderId}', 'start')">🚀 Safarni Boshlash</button>
        `;
    }
    else if (currentStatus === 'in_progress') {
        details.innerHTML = `<b>Buyurtma ID:</b> ${orderId}<br>🚖 Yo'ldasiz... Manzilga yetib borgach <b>'Safarni yakunlash'</b> tugmasini bosing.`;
        actions.innerHTML = `
            <button class="btn btn-finish" onclick="updateTripStatus('${orderId}', 'finish')">🏁 Safarni Yakunlash</button>
        `;
    }
}

// 5. Umumiy status yangilash funksiyasi (arrived, start, finish uchun)
async function updateTripStatus(orderId, actionType) {
    const token = localStorage.getItem("access_token");
    let endpoint = `${API_BASE}/${orderId}/${actionType}`;

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            if (actionType === 'arrived') {
                alert("Mijozga yetib kelganingiz haqida xabar berildi!");
                showActiveTrip(orderId, 'arrived');
            } else if (actionType === 'start') {
                alert("Safar boshlandi. Oq yo'l!");
                showActiveTrip(orderId, 'in_progress');
            } else if (actionType === 'finish') {
                alert("Safar muvaffaqiyatli yakunlandi!");
                document.getElementById("activeTripCard").style.display = "none";
                loadPendingOrders();
            }
        } else {
            alert("Xatolik yuz berdi. Status: " + response.status);
        }
    } catch (error) {
        alert("Xatolik: " + error.message);
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "../login.html";
}
const apiURL = "http://localhost:8080/user/getUserStores";
const API = "http://localhost:8080";
const token = localStorage.getItem("userToken");


document.addEventListener("DOMContentLoaded", () => {
    fetchStores();
});

function fetchStores() {
    const token = localStorage.getItem("userToken");

    const loading = document.getElementById("loading");
    const errorBox = document.getElementById("errorBox");

    loading.style.display = "block";
    errorBox.innerHTML = "";

    fetch(apiURL, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
        .then(res => {
            if (!res.ok) throw new Error("خطا در دریافت لیست فروشگاه‌ها");
            return res.json();
        })
        .then(data => {
            loading.style.display = "none";
            renderStores(data);
        })
        .catch(err => {
            loading.style.display = "none";
            errorBox.innerHTML = "خطایی رخ داد: " + err.message;
        });
}

function renderStores(stores) {
    const container = document.getElementById("storesContainer");
    container.innerHTML = "";

    if (!stores.length) {
        container.innerHTML = "<p class='error'>هیچ فروشگاهی ثبت نشده است.</p>";
        return;
    }

    stores.forEach(store => {
        container.innerHTML += `
            <div class="store-card">
                <h3>${store.name}</h3>
                <p>${store.description || "بدون توضیحات"}</p>
                <p><strong>آدرس:</strong> ${store.address || "ثبت نشده"}</p>
                <p><strong>تلفن:</strong> ${store.phone || "ثبت نشده"}</p>

                <button class="store-btn" onclick="openStore(${store.id})">
                    ورود به فروشگاه
                </button>
                <button class="store-btn" onclick="deleteStore(${store.id})">
                    حذف فروشگاه
                </button>
            </div>
        `;
    });
}
async function deleteStore(storeId) {
    await fetch(
        `${API}/store/deleteStore?storeId=${storeId}`,
        {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    );

    fetchStores();
}


function openStore(id) {
    window.location.href = `storePage.html?id=${id}`;
}

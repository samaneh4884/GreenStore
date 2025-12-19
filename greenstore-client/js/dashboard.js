const API = "http://localhost:8080";
const token = localStorage.getItem("userToken");

let cart = [];

function goToSignUpStore() {
    window.location.href = "signup-store.html";
}

async function loadBalance() {
    const res = await apiFetch("/user/getBalance");
    if (res.ok) {
        document.getElementById("balance").innerText = res.data;
    }
}

async function increaseBalance() {
    const amount = prompt("چقدر میخواید اضافه کنید؟");
    if (!amount) return;

    const token = localStorage.getItem("userToken");

    const res = await fetch(`${API_BASE}/user/addBalance?balance=${amount}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (res.ok) {
        loadBalance();
    } else {
        alert("خطا در افزایش موجودی");
    }
}

async function loadProducts() {
    const res = await apiFetch("/user/getAllProducts");
    if (res.ok) displayProducts(res.data);
}

function displayProducts(products) {
    const list = document.getElementById("productList");
    list.innerHTML = "";

    products.forEach(p => {
        const div = document.createElement("div");
        div.className = "product-card";

        const relativePath = p.texture.replace(/\\/g, '/');
        const imageUrl = `/../${relativePath}`;
        let actionHtml = "";
        if (p.quantity === 0) {
            actionHtml = `
                <button disabled style="background:#aaa; cursor:not-allowed;">
                    اتمام موجودی
                </button>
            `;
        } else {
            actionHtml = `
                <button onclick="addToCart(${p.storeId},'${p.storeName}' ,${p.id},'${p.name}', ${p.price},${p.quantity})">
                    افزودن به سبد
                </button>
            `;
        }
        div.innerHTML = `
            <img src="${imageUrl}">
            <h4>${p.name}</h4>
            <p><strong>توضیحات:</strong> ${p.description} </p>
            <p><strong>نام فروشگاه:</strong> ${p.storeName} </p>
            <p><strong>قیمت:</strong> ${p.price} تومان</p>
            <p><strong>موجودی:</strong> ${p.quantity} </p>
            ${actionHtml}
        `;

        list.appendChild(div);
    });
}

async function searchProducts() {
    const search = document.getElementById("search").value.toLowerCase();
    const mood = document.getElementById("moodFilter").value;

    const res = await apiFetch(`/user/getFilterProducts?search=${search}&mood=${mood}`);
    if (res.ok) displayProducts(res.data);
}

function addToCart(storeid, storeName, productid, name, price,productQuantity) {
    const existing = cart.find(item => item.productid === productid);
    if (existing) {
        if (existing.quantity < productQuantity) {
            existing.quantity += 1;
        } else {
            alert("موجودی کالا کافی نیست!");
        }
    } else {
        cart.push({ storeid, storeName, productid, name, price, quantity: 1 });
    }
    loadCart();
}

function loadCart() {
    const list = document.getElementById("cartList");
    list.innerHTML = "";

    cart.forEach((item, index) => {
        const li = document.createElement("li");
        li.innerHTML = `
            ${item.name} - ${item.storeName} از ${item.price} تومان 
            (تعداد: ${item.quantity})
            <button onclick="decreaseQuantity(${index})">➖</button>
            <button onclick="removeFromCart(${index})">❌</button>
        `;
        list.appendChild(li);
    });
}

function decreaseQuantity(index) {
    cart[index].quantity -= 1;
    if (cart[index].quantity <= 0) {
        cart.splice(index, 1);
    }
    loadCart();
}

function removeFromCart(index) {
    cart.splice(index, 1);
    loadCart();
}

window.onload = async () => {
    await loadBalance();
    await loadProducts();
    loadCart();
};
async function checkoutCart() {
    if (cart.length === 0) {
        alert("سبد خرید شما خالی است!");
        return;
    }

    try {
        const res = await fetch(`${API}/user/checkout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(cart)
        });

        if (res.ok) {
            alert("خرید با موفقیت انجام شد!");
            cart = [];
            loadCart();
            await loadBalance();
            await loadProducts();
        } else {
            const error = await res.json();
            alert("خطا در خرید: " + (error.message || "نامشخص"));
        }
    } catch (err) {
        alert("خطا در اتصال به سرور");
        console.error(err);
    }
}
async function orderHistory() {
    const res = await fetch(`${API}/user/getOrderHistory`, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!res.ok) return alert("خطا در دریافت تاریخچه");

    const historyStr = await res.text();
    const lines = historyStr.split("\n");

    const container = document.getElementById("orderHistoryList");
    container.innerHTML = "";

    lines.forEach(line => {
        if (!line.trim()) return;

        const parts = line.split("| خرید:");
        const rawDate = parts[0].replace("تاریخ: ", "").trim();
        const orderText = parts[1]?.trim() || "";

        const dateObj = new Date(rawDate);
        const formattedDate = dateObj.toLocaleString("fa-IR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        });

        const li = document.createElement("li");
        li.textContent = `${formattedDate} | خرید: ${orderText}`;
        li.className = "order-history-item";
        container.appendChild(li);
    });
}
function showOrderHistory() {
    document.getElementById("orderHistoryModal").style.display = "block";
    orderHistory();
}

function closeOrderHistory() {
    document.getElementById("orderHistoryModal").style.display = "none";
}

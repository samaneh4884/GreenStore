const API_BASE = "http://localhost:8080";
const token = localStorage.getItem("storeToken");

async function loadStoreInfo() {
    const res = await fetch(`${API_BASE}/store/getInfo`, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (!res.ok) {
        alert("خطا در دریافت اطلاعات فروشگاه!");
        return;
    }

    const store = await res.json();
    document.getElementById("storeName").innerText = store.name;
    document.getElementById("storeAddress").innerText = store.address;
    document.getElementById("storePhone").innerText = store.phone;
    document.getElementById("storeDescription").innerText = store.description;

    loadProducts(store.products);
}


function loadProducts(products) {
    const ul = document.getElementById("products");
    ul.innerHTML = "";

    products.forEach(product => {
        const li = document.createElement("li");
        li.className = "product-item";

        li.innerHTML = `
            <img src="${product.texture}" alt="${product.name}">
            <div>
                <strong>${product.name}</strong>
                <p>${product.description}</p>
                <p>قیمت: ${product.price}</p>
                <p>موجودی: <span id="quantity-${product.id}">${product.quantity}</span></p>
                <button onclick="editProductInfo(${product.id})">ویرایش</button>
                <button onclick="increaseQuantity(${product.id})">افزایش موجودی</button>
                <button onclick="decreaseQuantity(${product.id})">کاهش موجودی</button>
                <button onclick="removeProduct(${product.id})">حذف محصول</button>
            </div>
        `;

        ul.appendChild(li);
    });
}

async function editProductInfo(productId) {
    const newName = prompt("نام جدید محصول:");
    const newPrice = parseFloat(prompt("قیمت جدید محصول:"));
    if (!newName || isNaN(newPrice)) return;

    await fetch(`${API_BASE}/store/editProduct`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId, name: newName, price: newPrice })
    });
    loadStoreInfo();
}

async function increaseQuantity(productId) {
    await fetch(`${API_BASE}/store/increaseQuantity?productId=${productId}`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}` }
    });
    loadStoreInfo();
}

async function decreaseQuantity(productId) {
    await fetch(`${API_BASE}/store/decreaseQuantity?productId=${productId}`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}` }
    });
    loadStoreInfo();
}

async function removeProduct(productId) {
    if (!confirm("آیا از حذف محصول اطمینان دارید؟")) return;
    await fetch(`${API_BASE}/store/removeProduct?productId=${productId}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
    });
    loadStoreInfo();
}

function addProduct() {
    const name = prompt("نام محصول:");
    const description = prompt("توضیحات:");
    const price = parseFloat(prompt("قیمت:"));
    const texture = prompt("آدرس عکس محصول:");
    const quantity = parseInt(prompt("موجودی:"));

    if (!name || isNaN(price) || !texture || isNaN(quantity)) return;

    fetch(`${API_BASE}/store/addProduct`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ name, description, price, texture, quantity })
    }).then(() => loadStoreInfo());
}

loadStoreInfo();

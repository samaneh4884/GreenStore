const API = "http://localhost:8080";
const token = localStorage.getItem("userToken");

const urlParams = new URLSearchParams(window.location.search);
const storeId = urlParams.get("id");

const nameBox = document.getElementById("storeNameBox");
const descBox = document.getElementById("storeDesc");
const phoneBox = document.getElementById("storePhone");
const addressBox = document.getElementById("storeAddress");

document.addEventListener("DOMContentLoaded", () => {
    if (!storeId) {
        alert("فروشگاه یافت نشد!");
        return;
    }

    fetchStoreInfo();
    fetchProducts();
});

async function fetchStoreInfo() {
    try {
        const res = await fetch(`${API}/store/getInfo?id=${storeId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error("خطا در دریافت اطلاعات فروشگاه");

        const store = await res.json();

        nameBox.innerText = store.name;
        descBox.innerText = "توضیحات: " + store.description;
        phoneBox.innerText = "تلفن: " + store.phone;
        addressBox.innerText = "آدرس: " + store.address;

    } catch (err) {
        descBox.innerText = "خطا در لود اطلاعات";
    }
}

async function fetchProducts() {
    try {
        const res = await fetch(`${API}/store/getStoreProducts?id=${storeId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error("خطا در دریافت محصولات");

        const products = await res.json();
        renderProducts(products);

    } catch (err) {
        document.getElementById("error").innerText = err.message;
    }

    document.getElementById("loading").style.display = "none";
}

function renderProducts(products) {
    const container = document.getElementById("productsContainer");
    container.innerHTML = "";

    products.forEach(p => {
        const relativePath = p.texture.replace(/\\/g, '/');
        const imageUrl = `/../${relativePath}`;
        container.innerHTML += `
            <div class="product-card">
                <img src="${imageUrl}" alt="${p.name}" />

                <h3>${p.name}</h3>
                <p><strong>قیمت:</strong> ${p.price} تومان</p>
                <p><strong>موجودی:</strong> ${p.quantity}</p>

                <div class="controls">
                    <button class="btn btn-inc" onclick="changeQuantity(${p.id}, +1)">+</button>
                    <button class="btn btn-dec" onclick="changeQuantity(${p.id}, -1)">-</button>
                </div>

                <button class="btn btn-del" onclick="deleteProduct(${p.id})">
                    حذف محصول
                </button>
            </div>
        `;
    });
}

async function changeQuantity(id, amount) {
    await fetch(`${API}/product/changeQuantity`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            productId: id,
            storeId: storeId,
            amount: amount
        })
    });

    fetchProducts();
}

async function deleteProduct(id) {
    await fetch(`${API}/product/deleteProduct`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            productId: id,
            storeId: storeId
        })
    });

    fetchProducts();
}

function goToAddProduct() {
    window.location.href = `addProduct.html?id=${storeId}`;
}

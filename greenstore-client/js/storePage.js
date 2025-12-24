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
        let preorder = "";
        if (p.preorder) {
            preorder = '<p><strong> پیش فروش </strong></p>';
        }
        let sellStarter = "";
        if (p.preorder) {
            sellStarter = `<button class="btn btn-sellStarter" onclick="sellStarter(${p.id})">آغاز فروش</button>`;
        }


        container.innerHTML += `
            <div class="product-card">
                <img src="${imageUrl}" alt="${p.name}" />

                <h3>${p.name}</h3>
                <p><strong>توضیحات:</strong> ${p.description} </p>
                <p><strong>قیمت:</strong> ${p.price} تومان</p>
                <p><strong>موجودی:</strong> ${p.quantity}</p>
                ${preorder}
                <div class="controls">
                    <button class="btn btn-inc" onclick="changeQuantity(${p.id}, +1)">+</button>
                    <button class="btn btn-dec" onclick="changeQuantity(${p.id}, -1)">-</button>
                </div>
                ${sellStarter}
                <button class="btn btn-comment" onclick="openComments(${p.id})
                
                ">نظرات کاربران</button>
                
                <button class="btn btn-del" onclick="deleteProduct(${p.id})">
                    حذف محصول
                </button>
            </div>
        `;
    });
}

async function changeQuantity(id, amount) {
    await fetch(
        `${API}/store/changeQuantity?storeId=${storeId}&productId=${id}&quantity=${amount}`,
        {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    );

    fetchProducts();
}

async function deleteProduct(productId) {
    await fetch(
        `${API}/store/deleteProduct?storeId=${storeId}&productId=${productId}`,
        {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    );

    fetchProducts();
}

function goToAddProduct() {
    window.location.href = `addProduct.html?id=${storeId}`;
}
function openComments(productId) {
    currentProductId = productId;
    document.getElementById("commentModal").classList.remove("hidden");
    loadComments();
}

function closeComments() {
    document.getElementById("commentModal").classList.add("hidden");
}
async function loadComments() {
    const res = await fetch(`${API}/user/comments?productId=${currentProductId}`, {
        headers: {
            "Authorization": "Bearer " + token
        }
    });
    const comments = await res.json();

    const list = document.getElementById("commentsList");
    list.innerHTML = "";

    comments.forEach(c => {
        list.innerHTML += `
            <div class="comment">
                <strong>${c.username}</strong>
                <div>${c.text}</div>
            </div>
        `;
    });
}
async function sellStarter(productId){
    await fetch(
        `${API}/store/sellStarter?storeId=${storeId}&productId=${productId}`,
        {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    );


    fetchProducts();
}


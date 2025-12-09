let cart = [];
function goToSignUpStore(){
    window.location.href = "signup-store.html";
}
async function loadDashboard() {
    await loadBalance();
    await loadProducts();
    loadCart();
}

async function loadBalance() {
    const res = await apiFetch("/user/getBalance");
    const balanceSpan = document.getElementById("balance");

    if(res.ok) {
        balanceSpan.innerText = res.data;
    } else {
        balanceSpan.innerText = "خطا"; 
    }
}

async function increaseBalance() {
    const amount = prompt("چقدر میخواید اضافه کنید؟");
    if (!amount) return;

    const token = localStorage.getItem("userToken");
    if (!token) {
        alert("کاربر لاگین نکرده است!");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/user/addBalance?balance=${amount}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const text = await res.text();

        if (res.ok) {
            alert(text);
            loadBalance();
        } else {
            alert(text);
        }
    } catch (err) {
        alert("خطا در ارتباط با سرور");
    }
}

async function loadProducts() {
    const res = await apiFetch("/products");
    if(res.ok) displayProducts(res.data);
}

function displayProducts(products) {
    const list = document.getElementById("productList");
    list.innerHTML = "";
    products.forEach(p => {
        const li = document.createElement("li");
        li.innerHTML = `${p.name} - ${p.price} تومان <button onclick="addToCart('${p.name}', ${p.price})">اضافه به سبد خرید</button>`;
        list.appendChild(li);
    });
}

function searchProducts() {
    const search = document.getElementById("search").value.toLowerCase();
    const mood = document.getElementById("moodFilter").value;
    const filtered = []; 
    displayProducts(filtered);
}

function addToCart(name, price) {
    cart.push({name, price});
    loadCart();
}

function loadCart() {
    const list = document.getElementById("cartList");
    list.innerHTML = "";
    cart.forEach(item => {
        const li = document.createElement("li");
        li.innerText = `${item.name} - ${item.price} تومان`;
        list.appendChild(li);
    });
}


window.onload = async () => {
    await loadBalance();
    await loadProducts();
    loadCart();
}

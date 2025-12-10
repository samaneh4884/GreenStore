const API = "http://localhost:8080";
const token = localStorage.getItem("userToken");

const urlParams = new URLSearchParams(window.location.search);
const storeId = urlParams.get("id");

if (!storeId) {
    alert("نام فروشگاه یافت نشد!");
}

async function addProduct() {
    const name = document.getElementById("name").value;
    const price = document.getElementById("price").value;
    const quantity = document.getElementById("quantity").value;
    const mood = document.getElementById("mood").value;
    const description = document.getElementById("description").value;
    const textureFile = document.getElementById("texture").files[0];

    const msg = document.getElementById("msg");

    if (!name || !price || !quantity) {
        msg.style.color = "red";
        msg.innerText = "نام، قیمت و موجودی الزامی هستند.";
        return;
    }

    const product = {
        name,
        price,
        quantity,
        mood,
        description
    };

    try {
        const formData = new FormData();
        formData.append("product", JSON.stringify(product));
        formData.append("storeId", storeId);
        if (textureFile) {
            formData.append("file", textureFile);
        }

        const res = await fetch(`${API}/store/addProduct`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            },
            body: formData
        });

        const text = await res.text();

        if (res.ok) {
            msg.style.color = "green";
            msg.innerText = "محصول با موفقیت ثبت شد!";
            setTimeout(() => {
                window.location.href = `storePage.html?id=${storeId}`;
            }, 1200);
        } else {
            msg.style.color = "red";
            msg.innerText = text;
        }

    } catch (err) {
        msg.style.color = "red";
        msg.innerText = "خطا در ارتباط با سرور";
    }
}

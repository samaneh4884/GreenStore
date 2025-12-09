const API = "http://localhost:8080";
window.onload = function () {
    loadUserInfo();
};

async function loadUserInfo() {
    const token = localStorage.getItem("userToken");

    try {
        const response = await fetch(`${API}/user/getUserInfo`, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("خطا در دریافت اطلاعات کاربر");
            return;
        }

        const user = await response.json();

        document.getElementById("userInfoBox").innerHTML = `
            <li><strong>نام کاربری:</strong> ${user.username}</li>
            <li><strong>ایمیل:</strong> ${user.email}</li>
            <li><strong>آدرس:</strong> ${user.address}</li>
            <li><strong>موجودی:</strong> ${user.balance} تومان</li>
        `;

    } catch (err) {
        alert("خطا در ارتباط با سرور");
        console.log(err);
    }
}
async function changePassword() {
    const oldPassword = document.getElementById("oldPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const passResponse = document.getElementById("passResponse");

    if (!oldPassword || !newPassword || !confirmPassword) {
        passResponse.innerText = "تمام فیلدها باید پر شوند.";
        return;
    }

    if (newPassword !== confirmPassword) {
        passResponse.innerText = "پسورد جدید و تکرار آن یکسان نیستند.";
        return;
    }

    const token = localStorage.getItem("userToken");
    if (!token) {
        passResponse.innerText = "ابتدا وارد حساب خود شوید.";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/user/changePassword", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                oldPassword,
                newPassword,
                confirmPassword
            })
        });

        const data = await response.json();
        if (response.ok) {
            passResponse.innerText = data.message;
            localStorage.setItem("userToken", data.token);
        } else {
            passResponse.innerText = data.message || "خطا در تغییر پسورد";
        }
    } catch (err) {
        passResponse.innerText = "خطا در تغییر پسورد (رمز قبلی اشتباه است)";
    }
}


async function changeEmail() {
    const token = localStorage.getItem("userToken");
    const newEmail = document.getElementById("newEmail").value;

    if (!newEmail) {
        alert("ایمیل را وارد کنید");
        return;
    }

    const response = await fetch(`${API}/user/changeEmail`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({ email: newEmail })
    });

    const text = await response.text();
    alert(text);

    loadUserInfo();
}

async function changeAddress() {
    const token = localStorage.getItem("userToken");
    const newAddress = document.getElementById("newAddress").value;

    if (!newAddress) {
        alert("آدرس را وارد کنید");
        return;
    }

    const response = await fetch(`${API}/user/changeAddress`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({ address: newAddress })
    });

    const text = await response.text();
    alert(text);

    loadUserInfo();
}

function goBack() {
    window.location.href = "dashboard.html";
}

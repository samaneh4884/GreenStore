const API_BASE = "http://localhost:8080";

async function loginStore() {
    const name = document.getElementById("name").value;
    const password = document.getElementById("password").value;

    try {
        const url = new URL(`${API_BASE}/auth/LoginStore`);
        url.searchParams.append("name", name);
        url.searchParams.append("password", password);

        const res = await fetch(url, { method: "GET" });
        const messageDiv = document.getElementById("response");

        if (res.ok) {
            const token = await res.text();
            localStorage.setItem("storeToken", token);
            messageDiv.style.color = "green";
            messageDiv.innerText = "ورود موفق!";

            setTimeout(()=> window.location.href="storeDashboard.html", 1500);
        } else {
            const text = await res.text();
            messageDiv.style.color = "red";
            messageDiv.innerText = text;
        }
    } catch(err) {
        document.getElementById("response").innerText = "خطا در ارتباط با سرور";
    }
}

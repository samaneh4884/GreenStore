const API_BASE = "http://localhost:8080";

async function apiFetch(path, method="GET", body=null, tokenKey="userToken") {
    const headers = {"Content-Type": "application/json"};
    const token = localStorage.getItem(tokenKey);
    if(token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null
    });

    let data;
    try {
        data = await res.json();
    } catch {
        data = await res.text();
    }
    return { ok: res.ok, status: res.status, data };
}

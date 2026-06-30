// ============================================================
// Higieniza+ — Login do painel admin
// ============================================================

document.addEventListener("DOMContentLoaded", () => {

    // se já estiver logado, vai direto pro dashboard
    if (getToken()) {
        window.location.href = "dashboard.html";
        return;
    }

    const form = document.getElementById("loginForm");
    const btn = document.getElementById("loginBtn");
    const errorMsg = document.getElementById("loginError");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        errorMsg.hidden = true;

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        btn.disabled = true;
        btn.textContent = "Entrando...";

        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                const body = await response.json().catch(() => ({}));
                throw new Error(body.message || "Usuário ou senha inválidos");
            }

            const data = await response.json();
            setSession(data.token, data.username);
            window.location.href = "dashboard.html";

        } catch (err) {
            errorMsg.textContent = err.message || "Não foi possível entrar. Tente novamente.";
            errorMsg.hidden = false;
        } finally {
            btn.disabled = false;
            btn.textContent = "Entrar";
        }
    });
});

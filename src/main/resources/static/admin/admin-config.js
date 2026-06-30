// ============================================================
// Higieniza+ — Configuração do painel admin
// Ajuste API_BASE_URL se o backend não estiver em localhost:8080
// ============================================================

const API_BASE_URL = window.location.origin; // mesmo host onde o admin está sendo servido

const STATUS_LABELS = {
    NOVO: "Novo",
    EM_ANDAMENTO: "Em andamento",
    CONCLUIDO: "Concluído",
    CANCELADO: "Cancelado"
};

function getToken() {
    return localStorage.getItem("higienizaplus_admin_token");
}

function getUsername() {
    return localStorage.getItem("higienizaplus_admin_user");
}

function setSession(token, username) {
    localStorage.setItem("higienizaplus_admin_token", token);
    localStorage.setItem("higienizaplus_admin_user", username);
}

function clearSession() {
    localStorage.removeItem("higienizaplus_admin_token");
    localStorage.removeItem("higienizaplus_admin_user");
}

function authHeaders() {
    const token = getToken();
    return token ? { "Authorization": "Bearer " + token } : {};
}

function requireAuthOrRedirect() {
    if (!getToken()) {
        window.location.href = "index.html";
        return false;
    }
    return true;
}

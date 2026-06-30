// ============================================================
// Higieniza+ — Dashboard de pedidos do painel admin
// ============================================================

let currentPage = 0;
const PAGE_SIZE = 20;

document.addEventListener("DOMContentLoaded", () => {

    if (!requireAuthOrRedirect()) return;

    document.getElementById("userLabel").textContent = getUsername() || "";

    document.getElementById("logoutBtn").addEventListener("click", () => {
        clearSession();
        window.location.href = "index.html";
    });

    document.getElementById("refreshBtn").addEventListener("click", () => {
        currentPage = 0;
        carregarPedidos();
    });

    document.getElementById("statusFilter").addEventListener("change", () => {
        currentPage = 0;
        carregarPedidos();
    });

    document.getElementById("prevPageBtn").addEventListener("click", () => {
        if (currentPage > 0) {
            currentPage--;
            carregarPedidos();
        }
    });

    document.getElementById("nextPageBtn").addEventListener("click", () => {
        currentPage++;
        carregarPedidos();
    });

    carregarPedidos();
});

async function carregarPedidos() {
    const loadingMsg = document.getElementById("loadingMsg");
    const emptyMsg = document.getElementById("emptyMsg");
    const table = document.getElementById("pedidosTable");

    loadingMsg.hidden = false;
    emptyMsg.hidden = true;
    table.hidden = true;

    const status = document.getElementById("statusFilter").value;
    const params = new URLSearchParams({
        page: currentPage,
        size: PAGE_SIZE,
        sort: "criadoEm,desc"
    });
    if (status) params.append("status", status);

    try {
        const response = await fetch(`${API_BASE_URL}/api/orcamentos?${params.toString()}`, {
            headers: authHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            clearSession();
            window.location.href = "index.html";
            return;
        }

        if (!response.ok) throw new Error("Falha ao carregar pedidos");

        const page = await response.json();
        renderizarPedidos(page);

    } catch (err) {
        loadingMsg.textContent = "Erro ao carregar pedidos. Tente atualizar a página.";
        loadingMsg.hidden = false;
    }
}

function renderizarPedidos(page) {
    const loadingMsg = document.getElementById("loadingMsg");
    const emptyMsg = document.getElementById("emptyMsg");
    const table = document.getElementById("pedidosTable");
    const body = document.getElementById("pedidosBody");
    const pageLabel = document.getElementById("pageLabel");

    loadingMsg.hidden = true;

    const pedidos = page.content || [];

    if (pedidos.length === 0) {
        emptyMsg.hidden = false;
        table.hidden = true;
        pageLabel.textContent = "";
        return;
    }

    body.innerHTML = "";

    pedidos.forEach(p => {
        const tr = document.createElement("tr");

        const dataFormatada = new Date(p.criadoEm).toLocaleString("pt-AO", {
            day: "2-digit", month: "2-digit", year: "numeric",
            hour: "2-digit", minute: "2-digit"
        });

        tr.innerHTML = `
            <td>${dataFormatada}</td>
            <td>${escapeHtml(p.nome)}</td>
            <td>${escapeHtml(p.whatsapp)}</td>
            <td>${escapeHtml(p.email)}</td>
            <td>${escapeHtml(p.servico)}</td>
            <td class="mensagem-cell">${escapeHtml(p.mensagem || "—")}</td>
            <td>
                <select class="status-select" data-id="${p.id}">
                    ${Object.entries(STATUS_LABELS).map(([value, label]) =>
                        `<option value="${value}" ${value === p.status ? "selected" : ""}>${label}</option>`
                    ).join("")}
                </select>
            </td>
            <td class="row-actions">
                <a class="icon-btn" href="https://wa.me/${p.whatsapp.replace(/\D/g, "")}" target="_blank" rel="noopener">WhatsApp</a>
                <button class="icon-btn danger" data-delete-id="${p.id}">Excluir</button>
            </td>
        `;

        body.appendChild(tr);
    });

    table.hidden = false;
    emptyMsg.hidden = true;

    pageLabel.textContent = `Página ${page.number + 1} de ${Math.max(page.totalPages, 1)} · ${page.totalElements} pedido(s)`;

    document.getElementById("prevPageBtn").disabled = page.first;
    document.getElementById("nextPageBtn").disabled = page.last;

    body.querySelectorAll(".status-select").forEach(select => {
        select.addEventListener("change", (e) => atualizarStatus(e.target.dataset.id, e.target.value));
    });

    body.querySelectorAll("[data-delete-id]").forEach(btn => {
        btn.addEventListener("click", (e) => excluirPedido(e.target.dataset.deleteId));
    });
}

async function atualizarStatus(id, novoStatus) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orcamentos/${id}/status?status=${novoStatus}`, {
            method: "PATCH",
            headers: authHeaders()
        });

        if (!response.ok) throw new Error("Falha ao atualizar status");

    } catch (err) {
        alert("Não foi possível atualizar o status. Tente novamente.");
        carregarPedidos();
    }
}

async function excluirPedido(id) {
    if (!confirm("Tem certeza que deseja excluir este pedido?")) return;

    try {
        const response = await fetch(`${API_BASE_URL}/api/orcamentos/${id}`, {
            method: "DELETE",
            headers: authHeaders()
        });

        if (!response.ok) throw new Error("Falha ao excluir pedido");

        carregarPedidos();

    } catch (err) {
        alert("Não foi possível excluir o pedido. Tente novamente.");
    }
}

function escapeHtml(str) {
    if (str == null) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

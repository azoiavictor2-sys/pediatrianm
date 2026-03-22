const API_BASE = 'http://localhost:8080';
const token = localStorage.getItem('ambulato_token');
const userRaw = localStorage.getItem('ambulato_user');

// Protege a rota — redireciona se não estiver logado
if (!token || !userRaw) {
    window.location.href = '/index.html';
}

const user = JSON.parse(userRaw);

// ============================================
// INICIALIZAÇÃO DA UI
// ============================================
document.getElementById('userNome').textContent = user.nomeCompleto;
document.getElementById('userTurma').textContent = user.turma;
document.getElementById('userAvatar').textContent = user.nomeCompleto.charAt(0).toUpperCase();

// ============================================
// NAVEGAÇÃO
// ============================================
function showSection(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    if (section === 'novo') {
        document.getElementById('sectionNovo').classList.add('active');
        document.getElementById('navNovoProntuario').classList.add('active');
    } else {
        document.getElementById('sectionLista').classList.add('active');
        document.getElementById('navMeusProntuarios').classList.add('active');
        carregarProntuarios();
    }

    // Fecha sidebar no mobile
    document.getElementById('sidebar').classList.remove('open');
    return false;
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('open');
}

// ============================================
// LOGOUT
// ============================================
function logout() {
    localStorage.removeItem('ambulato_token');
    localStorage.removeItem('ambulato_user');
    window.location.href = '/index.html';
}

// ============================================
// HEADERS COM JWT
// ============================================
function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// ============================================
// SALVAR PRONTUÁRIO
// ============================================
const prontuarioForm = document.getElementById('prontuarioForm');

prontuarioForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formError = document.getElementById('formError');
    const formSuccess = document.getElementById('formSuccess');
    formError.classList.add('hidden');
    formSuccess.classList.add('hidden');

    const saveBtn = document.getElementById('saveBtn');
    saveBtn.disabled = true;
    saveBtn.querySelector('.btn-text').classList.add('hidden');
    saveBtn.querySelector('.btn-loading').classList.remove('hidden');

    const payload = {
        pacienteNome: document.getElementById('pacienteNome').value.trim(),
        celularContato: document.getElementById('celularContato').value.trim(),
        tipagemSanguinea: document.getElementById('tipagemSanguinea').value,
        dnpmAndarQuando: document.getElementById('dnpmAndarQuando').value.trim(),
        dnpmFalaQuando: document.getElementById('dnpmFalaQuando').value.trim(),
        dnpmDesfraldeQuando: document.getElementById('dnpmDesfraldeQuando').value.trim(),
        subjetivo: document.getElementById('subjetivo').value.trim(),
        objetivo: document.getElementById('objetivo').value.trim(),
        avaliacao: document.getElementById('avaliacao').value.trim(),
        plano: document.getElementById('plano').value.trim()
    };

    try {
        const response = await fetch(`${API_BASE}/api/prontuarios/salvar`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.erro || err.pacienteNome || 'Erro ao salvar o prontuário.');
        }

        formSuccess.classList.remove('hidden');
        document.getElementById('formSuccessText').textContent = `Prontuário de ${payload.pacienteNome} salvo com sucesso!`;
        prontuarioForm.reset();
        setTimeout(() => formSuccess.classList.add('hidden'), 4000);

    } catch (err) {
        if (err.message.includes('401') || err.message.includes('403')) {
            logout();
        }
        document.getElementById('formErrorText').textContent = err.message;
        formError.classList.remove('hidden');
    } finally {
        saveBtn.disabled = false;
        saveBtn.querySelector('.btn-text').classList.remove('hidden');
        saveBtn.querySelector('.btn-loading').classList.add('hidden');
    }
});

// ============================================
// CARREGAR PRONTUÁRIOS
// ============================================
async function carregarProntuarios() {
    const loading = document.getElementById('listaLoading');
    const vazia = document.getElementById('listaVazia');
    const lista = document.getElementById('listaProntuarios');

    loading.classList.remove('hidden');
    vazia.classList.add('hidden');
    lista.classList.add('hidden');
    lista.innerHTML = '';

    try {
        const response = await fetch(`${API_BASE}/api/prontuarios/meus`, {
            headers: authHeaders()
        });

        if (response.status === 401 || response.status === 403) { logout(); return; }
        if (!response.ok) throw new Error('Erro ao carregar prontuários.');

        const prontuarios = await response.json();
        loading.classList.add('hidden');

        if (prontuarios.length === 0) {
            vazia.classList.remove('hidden');
            return;
        }

        lista.classList.remove('hidden');
        prontuarios
            .sort((a, b) => new Date(b.dataAtendimento) - new Date(a.dataAtendimento))
            .forEach(p => lista.appendChild(criarCard(p)));

    } catch (err) {
        loading.classList.add('hidden');
        lista.innerHTML = `<div class="error-message">⚠️ ${err.message}</div>`;
        lista.classList.remove('hidden');
    }
}

// ============================================
// CRIAR CARD DO PRONTUÁRIO
// ============================================
function criarCard(p) {
    const card = document.createElement('div');
    card.className = 'prontuario-card';
    card.setAttribute('role', 'article');

    const data = p.dataAtendimento
        ? new Date(p.dataAtendimento).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
        : 'Data desconhecida';

    const preview = p.subjetivo || p.objetivo || 'Nenhuma descrição disponível.';

    card.innerHTML = `
        <div class="prontuario-card-header">
            <div>
                <div class="prontuario-card-name">${escapeHtml(p.pacienteNome)}</div>
                <div class="prontuario-card-date">📅 ${data}</div>
            </div>
            ${p.tipagemSanguinea ? `<span class="prontuario-card-badge">🩸 ${escapeHtml(p.tipagemSanguinea)}</span>` : ''}
        </div>
        <div class="prontuario-card-preview">${escapeHtml(preview)}</div>
        <div class="prontuario-card-actions">
            <button class="btn btn-secondary btn-sm" onclick='abrirModal(${JSON.stringify(p).replace(/'/g, "\\'")}); event.stopPropagation();'>
                🔍 Ver detalhes
            </button>
            <button class="btn btn-danger btn-sm" onclick="deletarProntuario(${p.id}, this); event.stopPropagation();">
                🗑️ Excluir
            </button>
        </div>
    `;

    card.addEventListener('click', () => abrirModal(p));
    return card;
}

// ============================================
// DELETAR PRONTUÁRIO
// ============================================
async function deletarProntuario(id, btn) {
    if (!confirm('Tem certeza que deseja excluir este prontuário? Esta ação não pode ser desfeita.')) return;

    btn.disabled = true;
    btn.textContent = '⏳';

    try {
        const response = await fetch(`${API_BASE}/api/prontuarios/deletar/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        });

        if (response.status === 401 || response.status === 403) { logout(); return; }
        if (!response.ok) throw new Error('Erro ao excluir prontuário.');

        // Remove o card da UI
        btn.closest('.prontuario-card').remove();

        // Verifica se a lista ficou vazia
        if (document.getElementById('listaProntuarios').children.length === 0) {
            document.getElementById('listaProntuarios').classList.add('hidden');
            document.getElementById('listaVazia').classList.remove('hidden');
        }

    } catch (err) {
        alert(`Erro: ${err.message}`);
        btn.disabled = false;
        btn.textContent = '🗑️ Excluir';
    }
}

// ============================================
// MODAL DE DETALHES
// ============================================
function abrirModal(p) {
    const overlay = document.getElementById('modalOverlay');
    const content = document.getElementById('modalContent');

    const data = p.dataAtendimento
        ? new Date(p.dataAtendimento).toLocaleString('pt-BR', { dateStyle: 'long', timeStyle: 'short' })
        : 'Data desconhecida';

    const field = (label, value) => value
        ? `<div class="modal-field"><div class="modal-field-label">${label}</div><div class="modal-field-value">${escapeHtml(value)}</div></div>`
        : '';

    content.innerHTML = `
        <div class="modal-title">👶 ${escapeHtml(p.pacienteNome)}</div>
        <div class="modal-date">📅 Atendimento em ${data}</div>

        <hr class="modal-divider">
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:0.75rem;">
            ${field('Contato', p.celularContato)}
            ${field('Tipagem Sanguínea', p.tipagemSanguinea)}
        </div>

        <hr class="modal-divider">
        <strong style="font-size:0.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.5px">DNPM</strong>
        <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:0.75rem; margin-top:0.75rem;">
            ${field('Andou quando', p.dnpmAndarQuando)}
            ${field('Falou quando', p.dnpmFalaQuando)}
            ${field('Desfralhou quando', p.dnpmDesfraldeQuando)}
        </div>

        <hr class="modal-divider">
        <strong style="font-size:0.75rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.5px">SOAP</strong>
        <div style="margin-top:0.75rem;">
            ${field('S — Subjetivo', p.subjetivo)}
            ${field('O — Objetivo', p.objetivo)}
            ${field('A — Avaliação', p.avaliacao)}
            ${field('P — Plano', p.plano)}
        </div>
    `;

    overlay.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeModal() {
    document.getElementById('modalOverlay').classList.add('hidden');
    document.body.style.overflow = '';
}

// Fechar modal com ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
});

// ============================================
// UTILITÁRIOS
// ============================================
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

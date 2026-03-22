const API_BASE = 'http://localhost:8080';

const loginForm = document.getElementById('loginForm');
const loginBtn = document.getElementById('loginBtn');
const loginError = document.getElementById('loginError');
const loginErrorText = document.getElementById('loginErrorText');
const toggleSenha = document.getElementById('toggleSenha');
const senhaInput = document.getElementById('senha');

// Redireciona se já estiver logado
if (localStorage.getItem('ambulato_token')) {
    window.location.href = '/dashboard.html';
}

// Toggle de visibilidade da senha
toggleSenha.addEventListener('click', () => {
    const isPassword = senhaInput.type === 'password';
    senhaInput.type = isPassword ? 'text' : 'password';
    toggleSenha.textContent = isPassword ? '🙈' : '👁️';
});

// Submit do formulário de login
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    loginError.classList.add('hidden');
    setLoading(true);

    const username = document.getElementById('username').value.trim().replace(/\D/g, '');
    const senha = senhaInput.value;

    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, senha })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Usuário ou senha incorretos.');
        }

        // Salva token e dados do usuário
        localStorage.setItem('ambulato_token', data.token);
        localStorage.setItem('ambulato_user', JSON.stringify({
            id: data.id,
            nomeCompleto: data.nomeCompleto,
            turma: data.turma
        }));

        // Redireciona para o dashboard
        window.location.href = '/dashboard.html';

    } catch (err) {
        loginErrorText.textContent = err.message;
        loginError.classList.remove('hidden');
        setLoading(false);
    }
});

function setLoading(loading) {
    loginBtn.disabled = loading;
    loginBtn.querySelector('.btn-text').classList.toggle('hidden', loading);
    loginBtn.querySelector('.btn-loading').classList.toggle('hidden', !loading);
}

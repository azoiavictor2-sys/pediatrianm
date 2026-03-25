#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
E-FORM Fix Script v1.0
======================
Aplica TODAS as correções necessárias no static/index.html do E-FORM (Ambulato).

Uso:
    python fix_eform.py src/main/resources/static/index.html

O script cria um backup automático (.backup) antes de salvar.
"""

import sys
import os

# ═══════════════════════════════════════════════════════════
#  HTML DA TOPBAR + HEADER (inserido em page-consulta/soap/lista)
# ═══════════════════════════════════════════════════════════
TOPBAR_HEADER = """
        <!-- BARRA SUPERIOR GLOBAL -->
        <div class="topbar-global">
            <span class="t16-brand"><img src="/img/t16a1-logo.png" alt="T16.A1"></span>
            <button class="btn-tema-global" onclick="toggleDarkMode()">
                <span class="tema-texto-sub">MUDAR PARA TEMA ESCURO</span>
                <span class="tema-divider"></span>
            </button>
        </div>
        <!-- HEADER APP -->
        <div class="header-app">
            <div class="logo-eform"><img src="/img/eform-logo.png" alt="E-FORM"></div>
            <div class="user-area" onclick="toggleUserDropdown()">
                <div class="user-name">
                    <span class="dash-user-nome-sync">Victor</span>
                    <small>Seja Bem-Vindo!</small>
                </div>
                <i class="fa-solid fa-chevron-up user-arrow"></i>
            </div>
        </div>
"""

# ═══════════════════════════════════════════════════════════
#  CSS ADICIONAL
# ═══════════════════════════════════════════════════════════
CSS_EXTRA = """
        /* ── Sidebar sticky top – compensa topbar+header fixos (92px) ── */
        #page-consulta .sidebar {
            top: 100px;
            max-height: calc(90vh - 100px);
        }

        /* ── Char counter nos textareas do SOAP ── */
        .char-counter {
            font-size: 0.8em;
            color: #888;
            padding: 4px 0;
        }

"""

# ═══════════════════════════════════════════════════════════
#  FUNÇÕES JS AUSENTES / CORRIGIDAS
# ═══════════════════════════════════════════════════════════
FUNC_ABRIR_RESUMO_SOAP = """
        // Abre SOAP de prontuário PRÓPRIO vindo da busca global
        function abrirResumoSoap(id) {
            var token = localStorage.getItem('ambulato_token');
            var baseUrl = window.location.origin;
            fetch(baseUrl + '/api/prontuarios/' + id, {
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(r) { return r.json(); })
            .then(function(p) {
                prontuarioAberto = p;
                var soapVazio = (!p.subjetivo || p.subjetivo.trim() === '') &&
                                (!p.objetivo  || p.objetivo.trim()  === '') &&
                                (!p.avaliacao || p.avaliacao.trim() === '') &&
                                (!p.plano     || p.plano.trim()     === '');
                if (!soapVazio) {
                    document.getElementById('soap-s').value = p.subjetivo || '';
                    document.getElementById('soap-o').value = p.objetivo  || '';
                    document.getElementById('soap-a').value = p.avaliacao || '';
                    document.getElementById('soap-p').value = p.plano     || '';
                } else if (p.dadosGeraisJson) {
                    localStorage.setItem('prontuarioData', p.dadosGeraisJson);
                    restaurarDadosSalvos();
                    gerarSOAP();
                    prontuarioAberto = p;
                }
                origemSoap = 'lista';
                navegarPara('page-soap');
            })
            .catch(function() { showToast('Erro ao carregar prontuário.', 'danger'); });
        }

"""

FUNC_UPDATE_CHAR_COUNT = """
        // Contador de caracteres dos textareas do SOAP
        function updateCharCount(el) {
            var limites = { 'soap-o': 4000, 'soap-a': 2000, 'soap-p': 2000 };
            var countEl = document.getElementById('counter-' + el.id);
            if (!countEl) return;
            var max = limites[el.id] || 2000;
            var len = el.value.length;
            countEl.textContent = len + ' / ' + max;
            if (len > max)          { countEl.style.color = '#dc3545'; }
            else if (len > max * 0.9) { countEl.style.color = '#ffc107'; }
            else                    { countEl.style.color = '';        }
        }

"""


# ═══════════════════════════════════════════════════════════
#  HELPER
# ═══════════════════════════════════════════════════════════
def fix(content, description, old, new, count=1):
    """Aplica uma substituição e reporta o resultado."""
    if old in content:
        content = content.replace(old, new, count)
        print(f"  \033[92m✓\033[0m {description}")
        return content, True
    else:
        print(f"  \033[93m!\033[0m {description}  ← padrão não encontrado (verifique manualmente)")
        return content, False


# ═══════════════════════════════════════════════════════════
#  MAIN
# ═══════════════════════════════════════════════════════════
def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)

    path = sys.argv[1]
    if not os.path.exists(path):
        print(f"\033[91mErro:\033[0m Arquivo não encontrado: {path}")
        sys.exit(1)

    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()
    content = original

    print(f"\n\033[1m🔧  E-FORM Fix Script\033[0m  →  {path}\n")
    applied = 0
    total   = 0

    # ──────────────────────────────────────────────────────
    #  FIX 1  window.onload: ID errado dash-user-name
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 1 · window.onload: dash-user-name → dash-user-nome",
        "document.getElementById('dash-user-name').innerText = logado.split(' ')[0];",
        "document.getElementById('dash-user-nome').innerText = logado.split(' ')[0];"
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 2  fecharTermosUso() → volta ao dashboard se logado
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 2 · fecharTermosUso(): navega ao dashboard se logado",
        "function fecharTermosUso() { navegarPara('page-login'); } // Se logado pode ser corrigido com base na sessao",
        """function fecharTermosUso() {
            var logado = localStorage.getItem('usuarioLogadoNome');
            navegarPara(logado ? 'page-dashboard' : 'page-login');
        }"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 3  fecharSobreEform() → volta ao dashboard se logado
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 3 · fecharSobreEform(): navega ao dashboard se logado",
        "function fecharSobreEform() { navegarPara('page-login'); }",
        """function fecharSobreEform() {
            var logado = localStorage.getItem('usuarioLogadoNome');
            navegarPara(logado ? 'page-dashboard' : 'page-login');
        }"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 4  fecharNovidadesVersao() → volta ao dashboard se logado
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 4 · fecharNovidadesVersao(): navega ao dashboard se logado",
        "function fecharNovidadesVersao() {\n            navegarPara('page-dashboard');\n        }",
        """function fecharNovidadesVersao() {
            var logado = localStorage.getItem('usuarioLogadoNome');
            navegarPara(logado ? 'page-dashboard' : 'page-login');
        }"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 5  page-termos: main-content → main-content-dash
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 5 · page-termos: classe main-content → main-content-dash",
        """        <div class="main-content">
            <div class="termos-container" style="margin-top:0;">
                <button class="btn-voltar-termos" onclick="fecharTermosUso()">""",
        """        <div class="main-content-dash">
            <div class="termos-container">
                <button class="btn-voltar-termos" onclick="fecharTermosUso()">"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 6  page-sobre: main-content → main-content-dash
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 6 · page-sobre: classe main-content → main-content-dash",
        """        <div class="main-content">
            <div class="termos-container" style="margin-top:0;">
                <button class="btn-voltar-termos" onclick="fecharSobreEform()">""",
        """        <div class="main-content-dash">
            <div class="termos-container">
                <button class="btn-voltar-termos" onclick="fecharSobreEform()">"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 7  page-novidades: main-content → main-content-dash
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 7 · page-novidades: classe main-content → main-content-dash",
        """        <div class="main-content">
            <div class="termos-container" style="margin-top:0;">
                <button class="btn-voltar-termos" onclick="fecharNovidadesVersao()">""",
        """        <div class="main-content-dash">
            <div class="termos-container">
                <button class="btn-voltar-termos" onclick="fecharNovidadesVersao()">"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 8  page-consulta: adicionar topbar + header
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 8 · page-consulta: inserir topbar + header + margin-top",
        '    <div id="page-consulta" class="page">\n        <div class="container-app">\n            <div class="summary-box" style="display: flex; justify-content: space-between; align-items: center;">',
        f'    <div id="page-consulta" class="page">{TOPBAR_HEADER}        <div class="container-app" style="margin-top: 100px;">\n            <div class="summary-box" style="display: flex; justify-content: space-between; align-items: center;">'
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 9  page-soap: adicionar topbar + header
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 9 · page-soap: inserir topbar + header + margin-top",
        '    <div id="page-soap" class="page">\n        <div class="container-app">\n            <div class="card" style="max-width: 1000px; margin: 0 auto;">',
        f'    <div id="page-soap" class="page">{TOPBAR_HEADER}        <div class="container-app" style="margin-top: 100px;">\n            <div class="card" style="max-width: 1000px; margin: 0 auto;">'
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 10  page-lista: adicionar topbar + header
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 10 · page-lista: inserir topbar + header + margin-top",
        '    <div id="page-lista" class="page">\n        <div class="container-app" style="max-width: 900px;">\n            <h1 style="text-align: center;"><i class="fa-solid fa-folder-open"></i> Meus Pacientes Atendidos</h1>',
        f'    <div id="page-lista" class="page">{TOPBAR_HEADER}        <div class="container-app" style="max-width: 900px; margin-top: 100px;">\n            <h1 style="text-align: center;"><i class="fa-solid fa-folder-open"></i> Meus Pacientes Atendidos</h1>'
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 11  CSS extra: sidebar top + char-counter
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 11 · CSS: sidebar top e char-counter",
        "        #print-area {\n            display: none;\n        }",
        CSS_EXTRA + "        #print-area {\n            display: none;\n        }"
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 12  Adicionar função abrirResumoSoap() (ausente)
    # ──────────────────────────────────────────────────────
    total += 1
    # Insere antes de abrirResumoSoapLeitura que já existe
    content, ok = fix(content,
        "FIX 12 · JS: adicionar função abrirResumoSoap()",
        "        function abrirResumoSoapLeitura(id, alunoNome) {",
        FUNC_ABRIR_RESUMO_SOAP + "        function abrirResumoSoapLeitura(id, alunoNome) {"
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 13  Adicionar função updateCharCount() (ausente)
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 13 · JS: adicionar função updateCharCount()",
        "        function abrirResumoSoap(id) {",
        FUNC_UPDATE_CHAR_COUNT + "        function abrirResumoSoap(id) {"
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 14  sincronizarNomeUsuario() na irParaSetup()
    #          garante que o nome no header seja atualizado
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 14 · irParaSetup(): garantir sincronização do nome",
        """        function irParaSetup() {
            navegarPara('page-setup');
            var nomeLogado = localStorage.getItem('usuarioLogadoNome') || 'Aluno';
            var elAluno1 = document.getElementById('setup-aluno-1');
            if (elAluno1) elAluno1.value = nomeLogado;
            sincronizarNomeUsuario();
        }""",
        """        function irParaSetup() {
            navegarPara('page-setup');
            var nomeLogado = localStorage.getItem('usuarioLogadoNome') || 'Aluno';
            var elAluno1 = document.getElementById('setup-aluno-1');
            if (elAluno1) elAluno1.value = nomeLogado;
            atualizarCamposAlunos();
            sincronizarNomeUsuario();
        }"""
    )
    if ok: applied += 1

    # ──────────────────────────────────────────────────────
    #  FIX 15  atualizarDashboard() após login bem-sucedido
    #          (garante que stats apareçam imediatamente)
    # ──────────────────────────────────────────────────────
    total += 1
    content, ok = fix(content,
        "FIX 15 · fazerLogin(): chamar atualizarDashboard + sincronizarNomeUsuario",
        """                    navegarPara('page-dashboard');

                    if (btnLogin) { btnLogin.innerHTML = loginOriginalText; btnLogin.disabled = false; }

                    atualizarDashboard();""",
        """                    navegarPara('page-dashboard');
                    sincronizarNomeUsuario();

                    if (btnLogin) { btnLogin.innerHTML = loginOriginalText; btnLogin.disabled = false; }

                    atualizarDashboard();"""
    )
    if ok: applied += 1

    # ══════════════════════════════════════════════════════
    #  SALVAR
    # ══════════════════════════════════════════════════════
    backup_path = path + '.backup'
    with open(backup_path, 'w', encoding='utf-8') as f:
        f.write(original)

    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

    print()
    print("═" * 58)
    skipped = total - applied
    print(f"  \033[1mResultado:\033[0m  {applied}/{total} correções aplicadas  |  {skipped} já OK/não encontradas")
    print(f"  \033[96mBackup:\033[0m     {backup_path}")
    print(f"  \033[92mArquivo:\033[0m    {path}")
    print("═" * 58)
    if skipped > 0:
        print(f"\n  \033[93mAtenção:\033[0m {skipped} padrão(ões) não encontrado(s).")
        print("  Isso pode significar que já estão corretos ou que a versão")
        print("  do arquivo difere do esperado. Verifique manualmente.")
    print()


if __name__ == '__main__':
    main()

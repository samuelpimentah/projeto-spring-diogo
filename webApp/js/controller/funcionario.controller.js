/**
 * Camada "controller": liga a UI (DOM, diálogos, eventos) à camada de
 * serviço (fetch) e ao model, espelhando o papel do FuncionarioController
 * do back-end — aqui recebendo eventos do usuário em vez de requisições HTTP.
 */
const FuncionarioController = (() => {
  const estado = {
    funcionarios: [],
    filtroStatus: "TODOS",
    termoBusca: "",
    modoDialog: "criar", // "criar" | "editar"
    idEmEdicao: null,
    idEmPatch: null,
    idParaExcluir: null,
  };

  const dom = {};

  function cachearDom() {
    dom.listaIndicadores = document.getElementById("lista-indicadores");
    dom.tabelaWrapper = document.getElementById("tabela-wrapper");
    dom.tabelaCorpo = document.getElementById("tabela-corpo");
    dom.listaVazia = document.getElementById("lista-vazia");
    dom.mensagemStatus = document.getElementById("mensagem-status");
    dom.inputBuscaTexto = document.getElementById("input-busca-texto");

    dom.formBuscaId = document.getElementById("form-busca-id");
    dom.inputBuscaId = document.getElementById("input-busca-id");

    dom.dialogFuncionario = document.getElementById("dialog-funcionario");
    dom.formFuncionario = document.getElementById("form-funcionario");
    dom.tituloDialogFuncionario = document.getElementById("dialog-funcionario-titulo");
    dom.botaoSalvarFuncionario = document.getElementById("botao-salvar-funcionario");
    dom.erroFormFuncionario = document.getElementById("form-funcionario-erro");

    dom.dialogPatch = document.getElementById("dialog-patch");
    dom.formPatch = document.getElementById("form-patch");
    dom.erroFormPatch = document.getElementById("form-patch-erro");

    dom.dialogExcluir = document.getElementById("dialog-excluir");
    dom.nomeExcluir = document.getElementById("nome-excluir");
    dom.erroFormExcluir = document.getElementById("form-excluir-erro");
    dom.botaoConfirmarExclusao = document.getElementById("botao-confirmar-exclusao");

    dom.dialogDetalhes = document.getElementById("dialog-detalhes");
    dom.detalhesLista = document.getElementById("detalhes-lista");
  }

  function escapeHtml(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, (c) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c])
    );
  }

  function exibirMensagem(texto, tipo) {
    dom.mensagemStatus.textContent = texto;
    if (tipo) dom.mensagemStatus.dataset.tipo = tipo;
    else delete dom.mensagemStatus.dataset.tipo;
  }

  function definirCarregando(carregando) {
    dom.tabelaWrapper.setAttribute("aria-busy", String(carregando));
  }

  /* ---------------------------------------------------------
     Carregamento (GET /funcionarios)
     --------------------------------------------------------- */
  async function carregarFuncionarios() {
    definirCarregando(true);
    exibirMensagem("Carregando candidatos...", null);
    try {
      const dados = await FuncionarioService.listar();
      estado.funcionarios = Array.isArray(dados) ? dados : [];
      exibirMensagem("", null);
      renderizarIndicadores();
      renderizarTabela();
    } catch (erro) {
      estado.funcionarios = [];
      exibirMensagem(erro.message, "erro");
      renderizarIndicadores();
      renderizarTabela();
    } finally {
      definirCarregando(false);
    }
  }

  /* ---------------------------------------------------------
     Filtro + busca (client-side sobre a lista já carregada)
     --------------------------------------------------------- */
  function funcionariosFiltrados() {
    const termo = estado.termoBusca.trim().toLowerCase();
    return estado.funcionarios.filter((f) => {
      const passaStatus = estado.filtroStatus === "TODOS" || f.status === estado.filtroStatus;
      if (!passaStatus) return false;
      if (!termo) return true;
      const alvo = [f.nome, f.cargo, infoStatus(f.status).rotulo].join(" ").toLowerCase();
      return alvo.includes(termo);
    });
  }

  function renderizarIndicadores() {
    const contagens = { TODOS: estado.funcionarios.length, EM_ANALISE: 0, APROVADO: 0, REPROVADO: 0, CONTRATADO: 0 };
    estado.funcionarios.forEach((f) => {
      if (contagens[f.status] !== undefined) contagens[f.status] += 1;
    });

    Object.entries(contagens).forEach(([status, valor]) => {
      const el = document.getElementById(`contagem-${status}`);
      if (el) el.textContent = valor;
    });

    dom.listaIndicadores.querySelectorAll(".indicador").forEach((botao) => {
      const ativo = botao.dataset.status === estado.filtroStatus;
      botao.classList.toggle("is-ativo", ativo);
      botao.setAttribute("aria-pressed", String(ativo));
    });
  }

  function renderizarTabela() {
    const lista = funcionariosFiltrados();
    dom.tabelaCorpo.innerHTML = "";
    dom.listaVazia.hidden = lista.length > 0;

    const fragmento = document.createDocumentFragment();
    lista.forEach((f) => {
      const status = infoStatus(f.status);
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td data-rotulo="Nome">${escapeHtml(f.nome)}</td>
        <td data-rotulo="Cargo">${escapeHtml(f.cargo)}</td>
        <td data-rotulo="Departamento">${escapeHtml(f.departamento) || "—"}</td>
        <td data-rotulo="Cidade">${escapeHtml(f.cidade) || "—"}</td>
        <td data-rotulo="Salário">${formatarSalario(f.salario)}</td>
        <td data-rotulo="Status"><span class="badge ${status.classeBadge}">${escapeHtml(status.rotulo)}</span></td>
        <td data-rotulo="Ações">
          <div class="celula-acoes">
            <button type="button" class="acao-icone" command="show-modal" commandfor="dialog-funcionario"
              data-id="${f.id}" aria-label="Editar ${escapeHtml(f.nome)} (PUT)" title="Editar (PUT)">✎</button>
            <button type="button" class="acao-icone" command="show-modal" commandfor="dialog-patch"
              data-id="${f.id}" aria-label="Atualização parcial de ${escapeHtml(f.nome)} (PATCH)" title="Atualização parcial (PATCH)">⚡</button>
            <button type="button" class="acao-icone acao-icone--perigo" command="show-modal" commandfor="dialog-excluir"
              data-id="${f.id}" data-nome="${escapeHtml(f.nome)}" aria-label="Excluir ${escapeHtml(f.nome)} (DELETE)" title="Excluir (DELETE)">🗑</button>
          </div>
        </td>
      `;
      fragmento.appendChild(tr);
    });
    dom.tabelaCorpo.appendChild(fragmento);
  }

  /* ---------------------------------------------------------
     Dialog: criar (POST) / editar (PUT)
     --------------------------------------------------------- */
  function prepararDialogFuncionario(botaoOrigem) {
    const id = botaoOrigem?.dataset?.id;
    dom.erroFormFuncionario.hidden = true;

    if (id) {
      const funcionario = estado.funcionarios.find((f) => String(f.id) === String(id));
      if (!funcionario) return;
      estado.modoDialog = "editar";
      estado.idEmEdicao = funcionario.id;
      dom.tituloDialogFuncionario.textContent = `Editar funcionário — ${funcionario.nome}`;
      dom.botaoSalvarFuncionario.textContent = "Salvar alterações";
      preencherFormFuncionario(funcionario);
    } else {
      estado.modoDialog = "criar";
      estado.idEmEdicao = null;
      dom.tituloDialogFuncionario.textContent = "Novo funcionário";
      dom.botaoSalvarFuncionario.textContent = "Cadastrar";
      preencherFormFuncionario(criarFuncionarioVazio());
    }
  }

  function preencherFormFuncionario(funcionario) {
    const form = dom.formFuncionario;
    form.nome.value = funcionario.nome ?? "";
    form.email.value = funcionario.email ?? "";
    form.telefone.value = funcionario.telefone ?? "";
    form.cargo.value = funcionario.cargo ?? "";
    form.departamento.value = funcionario.departamento ?? "";
    form.salario.value = funcionario.salario ?? "";
    form.cidade.value = funcionario.cidade ?? "";
    form.status.value = funcionario.status ?? StatusFuncionario.EM_ANALISE.valor;
  }

  async function tratarSubmitFuncionario(event) {
    event.preventDefault();
    const form = dom.formFuncionario;
    const payload = {
      nome: form.nome.value.trim(),
      email: form.email.value.trim(),
      telefone: form.telefone.value.trim(),
      cargo: form.cargo.value.trim(),
      departamento: form.departamento.value.trim(),
      salario: form.salario.value === "" ? null : Number(form.salario.value),
      cidade: form.cidade.value.trim(),
      status: form.status.value,
    };

    const erros = validarFuncionario(payload);
    if (erros.length) {
      dom.erroFormFuncionario.textContent = erros.join(" ");
      dom.erroFormFuncionario.hidden = false;
      return;
    }

    dom.botaoSalvarFuncionario.disabled = true;
    try {
      if (estado.modoDialog === "editar") {
        payload.id = estado.idEmEdicao;
        await FuncionarioService.atualizar(estado.idEmEdicao, payload);
        exibirMensagem("Funcionário atualizado com sucesso.", "sucesso");
      } else {
        await FuncionarioService.criar(payload);
        exibirMensagem("Funcionário cadastrado com sucesso.", "sucesso");
      }
      dom.dialogFuncionario.close();
      await carregarFuncionarios();
    } catch (erro) {
      dom.erroFormFuncionario.textContent = erro.message;
      dom.erroFormFuncionario.hidden = false;
    } finally {
      dom.botaoSalvarFuncionario.disabled = false;
    }
  }

  /* ---------------------------------------------------------
     Dialog: atualização parcial (PATCH)
     --------------------------------------------------------- */
  function prepararDialogPatch(botaoOrigem) {
    const id = botaoOrigem?.dataset?.id;
    const funcionario = estado.funcionarios.find((f) => String(f.id) === String(id));
    dom.erroFormPatch.hidden = true;
    if (!funcionario) return;

    estado.idEmPatch = funcionario.id;
    const form = dom.formPatch;
    form.cargo.value = funcionario.cargo ?? "";
    form.salario.value = funcionario.salario ?? "";
    form.status.value = funcionario.status ?? StatusFuncionario.EM_ANALISE.valor;
  }

  async function tratarSubmitPatch(event) {
    event.preventDefault();
    const form = dom.formPatch;
    const payload = {
      cargo: form.cargo.value.trim(),
      salario: form.salario.value === "" ? null : Number(form.salario.value),
      status: form.status.value,
    };

    try {
      await FuncionarioService.atualizarParcial(estado.idEmPatch, payload);
      dom.dialogPatch.close();
      exibirMensagem("Atualização parcial realizada com sucesso.", "sucesso");
      await carregarFuncionarios();
    } catch (erro) {
      dom.erroFormPatch.textContent = erro.message;
      dom.erroFormPatch.hidden = false;
    }
  }

  /* ---------------------------------------------------------
     Dialog: exclusão (DELETE)
     --------------------------------------------------------- */
  function prepararDialogExcluir(botaoOrigem) {
    const id = botaoOrigem?.dataset?.id;
    const nome = botaoOrigem?.dataset?.nome;
    dom.erroFormExcluir.hidden = true;
    estado.idParaExcluir = id ?? null;
    dom.nomeExcluir.textContent = nome || "este candidato";
  }

  async function tratarConfirmarExclusao() {
    if (!estado.idParaExcluir) return;
    dom.botaoConfirmarExclusao.disabled = true;
    try {
      await FuncionarioService.excluir(estado.idParaExcluir);
      dom.dialogExcluir.close();
      exibirMensagem("Funcionário excluído com sucesso.", "sucesso");
      await carregarFuncionarios();
    } catch (erro) {
      dom.erroFormExcluir.textContent = erro.message;
      dom.erroFormExcluir.hidden = false;
    } finally {
      dom.botaoConfirmarExclusao.disabled = false;
    }
  }

  /* ---------------------------------------------------------
     Dialog: detalhes (GET /funcionarios/{id})
     --------------------------------------------------------- */
  function renderizarDetalhes(funcionario) {
    const status = infoStatus(funcionario.status);
    dom.detalhesLista.innerHTML = `
      <dt>ID</dt><dd>${escapeHtml(funcionario.id)}</dd>
      <dt>Nome</dt><dd>${escapeHtml(funcionario.nome)}</dd>
      <dt>E-mail</dt><dd>${escapeHtml(funcionario.email)}</dd>
      <dt>Telefone</dt><dd>${escapeHtml(funcionario.telefone) || "—"}</dd>
      <dt>Cargo</dt><dd>${escapeHtml(funcionario.cargo)}</dd>
      <dt>Departamento</dt><dd>${escapeHtml(funcionario.departamento) || "—"}</dd>
      <dt>Cidade</dt><dd>${escapeHtml(funcionario.cidade) || "—"}</dd>
      <dt>Salário</dt><dd>${formatarSalario(funcionario.salario)}</dd>
      <dt>Status</dt><dd><span class="badge ${status.classeBadge}">${escapeHtml(status.rotulo)}</span></dd>
    `;
  }

  async function tratarBuscaPorId(event) {
    event.preventDefault();
    const id = dom.inputBuscaId.value.trim();
    if (!id) return;

    try {
      const funcionario = await FuncionarioService.buscarPorId(id);
      if (!funcionario) throw new Error("Funcionário não encontrado.");
      renderizarDetalhes(funcionario);
      dom.dialogDetalhes.showModal();
      exibirMensagem("", null);
    } catch (erro) {
      exibirMensagem(erro.message || `Funcionário com ID ${id} não encontrado.`, "erro");
    }
  }

  /* ---------------------------------------------------------
     Invocadores dos diálogos (command / commandfor)
     Com fallback manual para navegadores sem suporte à
     Invoker Commands API.
     --------------------------------------------------------- */
  function configurarInvocadoresDialogo() {
    const dialogos = [dom.dialogFuncionario, dom.dialogPatch, dom.dialogExcluir];
    const suportaCommand = "command" in document.createElement("button");

    function tratarAbertura(dialog, botao) {
      if (dialog === dom.dialogFuncionario) prepararDialogFuncionario(botao);
      else if (dialog === dom.dialogPatch) prepararDialogPatch(botao);
      else if (dialog === dom.dialogExcluir) prepararDialogExcluir(botao);
    }

    if (suportaCommand) {
      dialogos.forEach((dialog) => {
        dialog.addEventListener("command", (event) => {
          if (event.command === "show-modal") tratarAbertura(dialog, event.source);
        });
      });
    } else {
      document.addEventListener("click", (event) => {
        const botao = event.target.closest("[commandfor]");
        if (!botao) return;
        const dialog = document.getElementById(botao.getAttribute("commandfor"));
        if (!dialog) return;
        const comando = botao.getAttribute("command");
        if (comando === "show-modal") {
          tratarAbertura(dialog, botao);
          dialog.showModal();
        } else if (comando === "close" || comando === "request-close") {
          dialog.close();
        }
      });
    }

    // Fechar ao clicar na área do overlay (fora do painel do diálogo).
    [...dialogos, dom.dialogDetalhes].forEach((dialog) => {
      dialog.addEventListener("click", (event) => {
        const rect = dialog.getBoundingClientRect();
        const dentro =
          rect.top <= event.clientY &&
          event.clientY <= rect.bottom &&
          rect.left <= event.clientX &&
          event.clientX <= rect.right;
        if (!dentro) dialog.close();
      });
    });
  }

  /* ---------------------------------------------------------
     Eventos gerais
     --------------------------------------------------------- */
  function configurarEventos() {
    dom.listaIndicadores.addEventListener("click", (event) => {
      const botao = event.target.closest(".indicador");
      if (!botao) return;
      estado.filtroStatus = botao.dataset.status;
      renderizarIndicadores();
      renderizarTabela();
    });

    dom.inputBuscaTexto.addEventListener("input", (event) => {
      estado.termoBusca = event.target.value;
      renderizarTabela();
    });

    dom.formBuscaId.addEventListener("submit", tratarBuscaPorId);
    dom.formFuncionario.addEventListener("submit", tratarSubmitFuncionario);
    dom.formPatch.addEventListener("submit", tratarSubmitPatch);
    dom.botaoConfirmarExclusao.addEventListener("click", tratarConfirmarExclusao);
  }

  function init() {
    cachearDom();
    configurarInvocadoresDialogo();
    configurarEventos();
    carregarFuncionarios();
  }

  return { init };
})();

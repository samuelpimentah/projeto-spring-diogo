/**
 * Camada "service": única responsável por conversar com a API Spring Boot
 * via fetch, espelhando o FuncionarioService do back-end.
 * Toda comunicação HTTP (GET, POST, PUT, PATCH, DELETE) passa por aqui.
 */
const FuncionarioService = (() => {
  async function tratarResposta(response) {
    if (response.status === 204) return null;

    const texto = await response.text();
    let dados = null;
    if (texto) {
      try {
        dados = JSON.parse(texto);
      } catch {
        dados = texto;
      }
    }

    if (!response.ok) {
      const mensagem =
        (dados && typeof dados === "object" && (dados.message || dados.erro || dados.mensagem)) ||
        (typeof dados === "string" && dados) ||
        `Erro ${response.status} ao comunicar com a API.`;
      throw new Error(mensagem);
    }

    return dados;
  }

  async function requisitar(url, opcoes) {
    let response;
    try {
      response = await fetch(url, opcoes);
    } catch {
      throw new Error(
        "Não foi possível conectar à API."
      );
    }
    return tratarResposta(response);
  }

  return {
    /** GET /funcionarios */
    listar() {
      return requisitar(CONFIG.API_BASE_URL, { headers: { Accept: "application/json" } });
    },

    /** GET /funcionarios/{id} */
    buscarPorId(id) {
      return requisitar(`${CONFIG.API_BASE_URL}/${id}`, { headers: { Accept: "application/json" } });
    },

    /** POST /funcionarios */
    criar(funcionario) {
      return requisitar(CONFIG.API_BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(funcionario),
      });
    },

    /** PUT /funcionarios/{id} — atualização completa */
    atualizar(id, funcionario) {
      return requisitar(`${CONFIG.API_BASE_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(funcionario),
      });
    },

    /** PATCH /funcionarios/{id} — atualização parcial */
    atualizarParcial(id, camposAlterados) {
      return requisitar(`${CONFIG.API_BASE_URL}/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(camposAlterados),
      });
    },

    /** DELETE /funcionarios/{id} */
    excluir(id) {
      return requisitar(`${CONFIG.API_BASE_URL}/${id}`, { method: "DELETE" });
    },
  };
})();

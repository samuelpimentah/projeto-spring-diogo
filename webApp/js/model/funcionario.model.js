/**
 * Camada "model": formato da entidade Funcionario e regras de validação,
 * espelhando a classe Funcionario do back-end Spring Boot.
 */
const StatusFuncionario = Object.freeze({
  EM_ANALISE: { valor: "EM_ANALISE", rotulo: "Em análise", classeBadge: "badge--analise" },
  APROVADO: { valor: "APROVADO", rotulo: "Aprovado", classeBadge: "badge--aprovado" },
  REPROVADO: { valor: "REPROVADO", rotulo: "Reprovado", classeBadge: "badge--reprovado" },
  CONTRATADO: { valor: "CONTRATADO", rotulo: "Contratado", classeBadge: "badge--contratado" },
});

function infoStatus(valorStatus) {
  return (
    Object.values(StatusFuncionario).find((s) => s.valor === valorStatus) || {
      valor: valorStatus,
      rotulo: valorStatus || "—",
      classeBadge: "badge--analise",
    }
  );
}

function criarFuncionarioVazio() {
  return {
    nome: "",
    email: "",
    telefone: "",
    cargo: "",
    departamento: "",
    salario: "",
    cidade: "",
    status: StatusFuncionario.EM_ANALISE.valor,
  };
}

/** Valida os campos obrigatórios definidos no desafio: nome, email e cargo. */
function validarFuncionario(funcionario) {
  const erros = [];
  if (!funcionario.nome || !funcionario.nome.trim()) erros.push("Nome é obrigatório.");
  if (!funcionario.email || !funcionario.email.trim()) erros.push("E-mail é obrigatório.");
  if (!funcionario.cargo || !funcionario.cargo.trim()) erros.push("Cargo é obrigatório.");
  return erros;
}

function formatarSalario(valor) {
  const numero = Number(valor);
  if (Number.isNaN(numero) || valor === "" || valor === null || valor === undefined) return "—";
  return numero.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

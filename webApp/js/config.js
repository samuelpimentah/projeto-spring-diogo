/**
 * Configuração central de acesso à API Spring Boot.
 * Ajuste API_BASE_URL caso o back-end rode em outra porta/host.
 *
 * Importante: o back-end precisa liberar CORS para a origem deste front-end,
 * por exemplo com @CrossOrigin(origins = "*") no ClienteController/FuncionarioController.
 */
const CONFIG = Object.freeze({
  API_BASE_URL: "http://localhost:8080/funcionarios",
});

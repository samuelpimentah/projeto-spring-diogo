package com.picpay.contratacao.controller;

import com.picpay.contratacao.dto.FuncionarioPatchDTO;
import com.picpay.contratacao.dto.FuncionarioRequestDTO;
import com.picpay.contratacao.dto.FuncionarioResponseDTO;
import com.picpay.contratacao.model.StatusFuncionario;
import com.picpay.contratacao.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    /*
     * O Controller depende da interface do Service, e nunca acessa o
     * Repository diretamente. Isso separa HTTP das regras de negócio.
     */
    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(
            /* @Valid executa as validações declaradas no DTO antes do cadastro. */
            @Valid @RequestBody FuncionarioRequestDTO request) {
        FuncionarioResponseDTO funcionario = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) StatusFuncionario status) {
        return ResponseEntity.ok(service.listar(nome, cargo, status));
    }

    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Long>> indicadores() {
        return ResponseEntity.ok(service.calcularIndicadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> atualizarCompletamente(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioRequestDTO request) {
        return ResponseEntity.ok(service.atualizarCompletamente(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> atualizarParcialmente(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioPatchDTO patch) {
        return ResponseEntity.ok(service.atualizarParcialmente(id, patch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id) {
        service.deletar(id);

        /*
         * Escolhemos 200, em vez de 204, para mostrar uma confirmação legível
         * durante os testes no Postman, como permitido pelo enunciado.
         */
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Funcionário com id " + id + " excluído com sucesso.");
        return ResponseEntity.ok(resposta);
    }
}

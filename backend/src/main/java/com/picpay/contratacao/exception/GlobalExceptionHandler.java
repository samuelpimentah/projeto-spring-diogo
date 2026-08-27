package com.picpay.contratacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * @RestControllerAdvice centraliza o tratamento dos erros de todos os
 * Controllers. Assim cada endpoint cuida do fluxo de sucesso e os erros
 * mantêm o mesmo formato JSON em toda a API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FuncionarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarFuncionarioNaoEncontrado(
            FuncionarioNaoEncontradoException exception) {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    /*
     * @Valid lança MethodArgumentNotValidException antes de executar o método
     * do Controller. Aqui transformamos os detalhes técnicos em mensagens por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErroDeValidacao(
            MethodArgumentNotValidException exception) {
        Map<String, String> erros = new LinkedHashMap<>();

        for (FieldError erro : exception.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Erro de validação");
        resposta.put("erros", erros);
        return ResponseEntity.badRequest().body(resposta);
    }
}

package com.picpay.contratacao.service;

import com.picpay.contratacao.dto.FuncionarioPatchDTO;
import com.picpay.contratacao.dto.FuncionarioRequestDTO;
import com.picpay.contratacao.dto.FuncionarioResponseDTO;
import com.picpay.contratacao.model.StatusFuncionario;

import java.util.List;
import java.util.Map;

public interface FuncionarioService {

    FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO request);

    List<FuncionarioResponseDTO> listar(String nome, String cargo, StatusFuncionario status);

    FuncionarioResponseDTO buscarPorId(Long id);

    FuncionarioResponseDTO atualizarCompletamente(Long id, FuncionarioRequestDTO request);

    FuncionarioResponseDTO atualizarParcialmente(Long id, FuncionarioPatchDTO patch);

    void deletar(Long id);

    Map<String, Long> calcularIndicadores();
}

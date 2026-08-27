package com.picpay.contratacao.repository;

import com.picpay.contratacao.model.Funcionario;

import java.util.List;

public interface FuncionarioRepository {

    Funcionario salvar(Funcionario funcionario);

    List<Funcionario> listarTodos();

    Funcionario buscarPorId(Long id);

    Funcionario atualizar(Long id, Funcionario funcionario);

    boolean deletar(Long id);
}

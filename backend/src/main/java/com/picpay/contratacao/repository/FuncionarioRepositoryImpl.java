package com.picpay.contratacao.repository;

import com.picpay.contratacao.model.Funcionario;
import com.picpay.contratacao.model.StatusFuncionario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final ArrayList<Funcionario> funcionarios = new ArrayList<>();

    /*
     * AtomicLong gera identificadores incrementais sem repetir valores, mesmo
     * se duas requisições chegarem quase ao mesmo tempo. O cliente nunca define o id.
     */
    private final AtomicLong proximoId = new AtomicLong(1);

    public FuncionarioRepositoryImpl() {
        /*
         * Os dados são criados no repositório porque esta é a única camada que
         * conhece a ArrayList. Assim a aplicação já inicia pronta para testes manuais.
         */
        popularDadosIniciais();
    }

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        funcionario.setId(proximoId.getAndIncrement());
        funcionarios.add(funcionario);
        return funcionario;
    }

    @Override
    public List<Funcionario> listarTodos() {
        /*
         * Uma cópia evita que outra camada adicione ou remova itens diretamente
         * da lista original, mantendo o repositório responsável pelo armazenamento.
         */
        return new ArrayList<>(funcionarios);
    }

    @Override
    public Funcionario buscarPorId(Long id) {
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getId().equals(id)) {
                return funcionario;
            }
        }
        return null;
    }

    @Override
    public Funcionario atualizar(Long id, Funcionario funcionarioAtualizado) {
        for (int indice = 0; indice < funcionarios.size(); indice++) {
            Funcionario funcionario = funcionarios.get(indice);
            if (funcionario.getId().equals(id)) {
                funcionarioAtualizado.setId(id);
                funcionarios.set(indice, funcionarioAtualizado);
                return funcionarioAtualizado;
            }
        }
        return null;
    }

    @Override
    public boolean deletar(Long id) {
        for (int indice = 0; indice < funcionarios.size(); indice++) {
            if (funcionarios.get(indice).getId().equals(id)) {
                funcionarios.remove(indice);
                return true;
            }
        }
        return false;
    }

    private void popularDadosIniciais() {
        salvar(new Funcionario(null, "Ana Souza", "ana.souza@email.com", "11999990000",
                "Analista de Dados", "Tecnologia", 4500.00, "São Paulo",
                StatusFuncionario.EM_ANALISE));

        salvar(new Funcionario(null, "Bruno Lima", "bruno.lima@email.com", "21988880000",
                "Desenvolvedor Java", "Tecnologia", 5200.00, "Rio de Janeiro",
                StatusFuncionario.APROVADO));

        salvar(new Funcionario(null, "Carla Mendes", "carla.mendes@email.com", "31977770000",
                "Assistente Administrativo", "Administrativo", 2800.00, "Belo Horizonte",
                StatusFuncionario.REPROVADO));

        salvar(new Funcionario(null, "Diego Santos", "diego.santos@email.com", "11966660000",
                "Analista de RH", "Recursos Humanos", 4100.00, "São Paulo",
                StatusFuncionario.CONTRATADO));
    }
}

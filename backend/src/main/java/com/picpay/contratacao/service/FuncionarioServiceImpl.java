package com.picpay.contratacao.service;

import com.picpay.contratacao.dto.FuncionarioPatchDTO;
import com.picpay.contratacao.dto.FuncionarioRequestDTO;
import com.picpay.contratacao.dto.FuncionarioResponseDTO;
import com.picpay.contratacao.exception.FuncionarioNaoEncontradoException;
import com.picpay.contratacao.model.Funcionario;
import com.picpay.contratacao.model.StatusFuncionario;
import com.picpay.contratacao.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository repository;

    /*
     * A injeção pelo construtor deixa explícita a dependência do Service.
     * Usamos a interface para que uma futura troca da ArrayList por banco de
     * dados não obrigue a alterar as regras de negócio desta classe.
     */
    public FuncionarioServiceImpl(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO request) {
        StatusFuncionario status = request.getStatus();
        if (status == null) {
            status = StatusFuncionario.EM_ANALISE;
        }

        Funcionario funcionario = criarFuncionario(request, status);
        Funcionario funcionarioSalvo = repository.salvar(funcionario);
        return paraResponse(funcionarioSalvo);
    }

    @Override
    public List<FuncionarioResponseDTO> listar(String nome, String cargo, StatusFuncionario status) {
        String nomeNormalizado = normalizarFiltro(nome);
        String cargoNormalizado = normalizarFiltro(cargo);
        List<FuncionarioResponseDTO> resultado = new ArrayList<>();

        for (Funcionario funcionario : repository.listarTodos()) {
            if (nomeNormalizado != null
                    && !funcionario.getNome().toLowerCase(Locale.ROOT).contains(nomeNormalizado)) {
                continue;
            }

            if (cargoNormalizado != null
                    && !funcionario.getCargo().toLowerCase(Locale.ROOT).contains(cargoNormalizado)) {
                continue;
            }

            if (status != null && funcionario.getStatus() != status) {
                continue;
            }

            resultado.add(paraResponse(funcionario));
        }

        return resultado;
    }

    @Override
    public FuncionarioResponseDTO buscarPorId(Long id) {
        return paraResponse(buscarEntidadePorId(id));
    }

    @Override
    public FuncionarioResponseDTO atualizarCompletamente(Long id, FuncionarioRequestDTO request) {
        buscarEntidadePorId(id);

        StatusFuncionario status = request.getStatus();
        if (status == null) {
            /*
             * PUT substitui os dados completos. Como status é opcional no contrato,
             * sua ausência volta ao valor padrão em vez de produzir um valor nulo.
             */
            status = StatusFuncionario.EM_ANALISE;
        }

        Funcionario funcionarioAtualizado = criarFuncionario(request, status);
        funcionarioAtualizado.setId(id);
        return paraResponse(repository.atualizar(id, funcionarioAtualizado));
    }

    @Override
    public FuncionarioResponseDTO atualizarParcialmente(Long id, FuncionarioPatchDTO patch) {
        Funcionario funcionario = buscarEntidadePorId(id);

        /*
         * Cada verificação preserva o valor atual quando o campo não veio no JSON.
         * Essa é a principal diferença entre a atualização parcial (PATCH) e o PUT.
         */
        if (patch.getNome() != null) {
            funcionario.setNome(patch.getNome());
        }
        if (patch.getEmail() != null) {
            funcionario.setEmail(patch.getEmail());
        }
        if (patch.getTelefone() != null) {
            funcionario.setTelefone(patch.getTelefone());
        }
        if (patch.getCargo() != null) {
            funcionario.setCargo(patch.getCargo());
        }
        if (patch.getDepartamento() != null) {
            funcionario.setDepartamento(patch.getDepartamento());
        }
        if (patch.getSalario() != null) {
            funcionario.setSalario(patch.getSalario());
        }
        if (patch.getCidade() != null) {
            funcionario.setCidade(patch.getCidade());
        }
        if (patch.getStatus() != null) {
            funcionario.setStatus(patch.getStatus());
        }

        return paraResponse(repository.atualizar(id, funcionario));
    }

    @Override
    public void deletar(Long id) {
        buscarEntidadePorId(id);
        repository.deletar(id);
    }

    @Override
    public Map<String, Long> calcularIndicadores() {
        long total = 0;
        long emAnalise = 0;
        long aprovados = 0;
        long reprovados = 0;
        long contratados = 0;

        for (Funcionario funcionario : repository.listarTodos()) {
            total++;

            switch (funcionario.getStatus()) {
                case EM_ANALISE:
                    emAnalise++;
                    break;
                case APROVADO:
                    aprovados++;
                    break;
                case REPROVADO:
                    reprovados++;
                    break;
                case CONTRATADO:
                    contratados++;
                    break;
            }
        }

        /* LinkedHashMap mantém a ordem das chaves igual à apresentada no desafio. */
        Map<String, Long> indicadores = new LinkedHashMap<>();
        indicadores.put("total", total);
        indicadores.put("emAnalise", emAnalise);
        indicadores.put("aprovados", aprovados);
        indicadores.put("reprovados", reprovados);
        indicadores.put("contratados", contratados);
        return indicadores;
    }

    private Funcionario buscarEntidadePorId(Long id) {
        Funcionario funcionario = repository.buscarPorId(id);
        if (funcionario == null) {
            throw new FuncionarioNaoEncontradoException(id);
        }
        return funcionario;
    }

    private Funcionario criarFuncionario(FuncionarioRequestDTO request, StatusFuncionario status) {
        return new Funcionario(
                null,
                request.getNome(),
                request.getEmail(),
                request.getTelefone(),
                request.getCargo(),
                request.getDepartamento(),
                request.getSalario(),
                request.getCidade(),
                status
        );
    }

    private String normalizarFiltro(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return null;
        }
        return filtro.toLowerCase(Locale.ROOT);
    }

    /*
     * A entidade representa o dado guardado internamente; o DTO representa o
     * JSON público da API. A conversão explícita evita misturar esses dois papéis.
     */
    private FuncionarioResponseDTO paraResponse(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getTelefone(),
                funcionario.getCargo(),
                funcionario.getDepartamento(),
                funcionario.getSalario(),
                funcionario.getCidade(),
                funcionario.getStatus()
        );
    }
}

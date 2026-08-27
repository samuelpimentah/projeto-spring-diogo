package com.picpay.contratacao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.picpay.contratacao.model.StatusFuncionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/*
 * O id não faz parte deste DTO porque é responsabilidade do servidor.
 * Se o cliente enviar um campo "id" no JSON, esta anotação manda o Jackson
 * ignorá-lo de forma explícita, impedindo que o cliente escolha o identificador.
 */
@JsonIgnoreProperties(value = "id")
public class FuncionarioRequestDTO {

    /* @NotBlank rejeita valor nulo, vazio ou formado somente por espaços. */
    @NotBlank(message = "não pode estar em branco")
    private String nome;

    /* @Email verifica se o texto informado possui um formato de e-mail válido. */
    @NotBlank(message = "não pode estar em branco")
    @Email(message = "deve ser um e-mail válido")
    private String email;

    private String telefone;

    @NotBlank(message = "não pode estar em branco")
    private String cargo;

    private String departamento;

    /* @PositiveOrZero permite zero, mas rejeita valores negativos. */
    @PositiveOrZero(message = "deve ser maior ou igual a zero")
    private Double salario;

    private String cidade;
    private StatusFuncionario status;

    public FuncionarioRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public StatusFuncionario getStatus() {
        return status;
    }

    public void setStatus(StatusFuncionario status) {
        this.status = status;
    }
}

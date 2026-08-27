package com.picpay.contratacao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.picpay.contratacao.model.StatusFuncionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;

/*
 * No PATCH todos os campos são opcionais. Por isso não usamos @NotBlank:
 * um valor nulo significa que aquele campo deve permanecer como está.
 * O id enviado pelo cliente também é ignorado, pois nunca pode ser alterado.
 */
@JsonIgnoreProperties(value = "id")
public class FuncionarioPatchDTO {

    private String nome;

    /* @Email só valida quando há um valor; null continua permitido no PATCH. */
    @Email(message = "deve ser um e-mail válido")
    private String email;

    private String telefone;
    private String cargo;
    private String departamento;

    /* @PositiveOrZero impede salário negativo sem tornar o campo obrigatório. */
    @PositiveOrZero(message = "deve ser maior ou igual a zero")
    private Double salario;

    private String cidade;
    private StatusFuncionario status;

    public FuncionarioPatchDTO() {
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

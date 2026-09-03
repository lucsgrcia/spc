package view;

import java.util.Date;

public class ClienteView {
    private final Integer id;
    private final String nome;
    private final String email;
    private final String cpf;
    private final Date dataNasc;

    public ClienteView(Integer id, String nome, String email, String cpf, Date dataNasc) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataNasc = dataNasc;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public Date getDataNasc() { return dataNasc; }
}

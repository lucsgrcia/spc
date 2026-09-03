package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "clientes")
public class Cliente {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String nome;
    @DatabaseField(canBeNull = false, unique = true)
    private String cpf;
    @DatabaseField
    private Date dataNasc;
    @DatabaseField
    private String email;

    public Cliente() {
    }

    public Cliente(String nome, String email, String cpf, Date dataNasc) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataNasc = dataNasc;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public Date getDataNasc() { return dataNasc; }
    public void setDataNasc(Date dataNasc) { this.dataNasc = dataNasc; }
}

package view;

public class BarbeariaView {
    private final Integer id;
    private final String razaoSocial;
    private final String cnpj;
    private final String endereco;

    public BarbeariaView(Integer id, String razaoSocial, String cnpj, String endereco) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.endereco = endereco;
    }

    public Integer getId() { return id; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getCnpj() { return cnpj; }
    public String getEndereco() { return endereco; }
}

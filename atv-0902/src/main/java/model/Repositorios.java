package model;

public class Repositorios {
    private static final Database DATABASE = new Database("db.sqlite");
    private final Repositorio<Cliente, Integer> clientes =
            new Repositorio<>(DATABASE, Cliente.class);
    private final Repositorio<Barbearia, Integer> barbearias =
            new Repositorio<>(DATABASE, Barbearia.class);

    public Repositorio<Cliente, Integer> getClientes() {
        return clientes;
    }

    public Repositorio<Barbearia, Integer> getBarbearias() {
        return barbearias;
    }

   }

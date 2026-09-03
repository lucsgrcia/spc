package org.example.controller;

import org.example.model.Client;
import org.example.model.ClientRepository;
import org.example.model.Database;

import jakarta.xml.bind.JAXBException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class BarbershopController {

    private Database database;
    private ClientRepository repository;
    private final ObservableList<Client> clientes = FXCollections.observableArrayList();
    private Client clienteSelecionado;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextField idadeField;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, Integer> idColumn;

    @FXML
    private TableColumn<Client, String> nomeColumn;

    @FXML
    private TableColumn<Client, String> cpfColumn;

    @FXML
    private TableColumn<Client, Integer> idadeColumn;

    @FXML
    private TableColumn<Client, Date> dataColumn;

    @FXML
    private void initialize() {
        database = new Database("barbershop.db");

        try {
            repository = new ClientRepository(database);
        } catch (JAXBException e) {
            throw new RuntimeException("Erro ao inicializar repositório do cliente.", e);
        }

        configurarTabela();
        semearDadosIniciais();
        carregarClientes();
        limparFormulario();

        statusLabel.setText("Sistema pronto para uso.");
    }

    private void configurarTabela() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        idadeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        clientTable.setItems(clientes);
        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, atual) -> {
            if (atual != null) {
                clienteSelecionado = atual;
                preencherFormulario(atual);
            }
        });
    }

    @FXML
    private void salvarCliente() {
        String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
        String cpf = cpfField.getText() == null ? "" : cpfField.getText().trim();
        String idadeTexto = idadeField.getText() == null ? "" : idadeField.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || idadeTexto.isEmpty() || dataPicker.getValue() == null) {
            mostrarStatus("Preencha todos os campos antes de salvar.", true);
            return;
        }

        try {
            int idade = Integer.parseInt(idadeTexto);
            if (idade <= 0 || idade > 120) {
                mostrarStatus("Idade fora do intervalo válido.", true);
                return;
            }

            Date data = Date.from(dataPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (clienteSelecionado == null) {
                Client cliente = new Client(nome, cpf, idade, data);
                repository.create(cliente);
                mostrarStatus("Cliente cadastrado com sucesso.", false);
            } else {
                clienteSelecionado.setName(nome);
                clienteSelecionado.setCpf(cpf);
                clienteSelecionado.setAge(idade);
                clienteSelecionado.setDate(data);
                repository.update(clienteSelecionado);
                mostrarStatus("Cliente atualizado com sucesso.", false);
            }

            limparFormulario();
            carregarClientes();
        } catch (NumberFormatException ex) {
            mostrarStatus("Idade deve conter apenas números inteiros.", true);
        }
    }

    @FXML
    private void excluirCliente() {
        if (clienteSelecionado == null) {
            mostrarStatus("Selecione um cliente para excluir.", true);
            return;
        }

        repository.delete(clienteSelecionado);
        mostrarStatus("Cliente removido com sucesso.", false);
        limparFormulario();
        carregarClientes();
    }

    @FXML
    private void limparFormulario() {
        clienteSelecionado = null;
        nomeField.clear();
        cpfField.clear();
        idadeField.clear();
        dataPicker.setValue(LocalDate.now());
        clientTable.getSelectionModel().clearSelection();
    }

    private void carregarClientes() {
        List<Client> lista = repository.loadAll();
        clientes.setAll(lista);
    }

    private void semearDadosIniciais() {
        if (repository.loadAll().isEmpty()) {
            Client c1 = new Client("Lucas", "11111", 23, new Date(126, 7, 20, 14, 30));
            Client c2 = new Client("Mateus", "22222", 20, new Date(126, 7, 19, 10, 0));
            Client c3 = new Client("Marcos", "33333", 30, new Date(126, 7, 19, 20, 0));

            repository.create(c1);
            repository.create(c2);
            repository.create(c3);
            mostrarStatus("Dados iniciais cadastrados com sucesso.", false);
        }
    }

    private void preencherFormulario(Client cliente) {
        if (cliente == null) {
            limparFormulario();
            return;
        }

        nomeField.setText(cliente.getName());
        cpfField.setText(cliente.getCpf());
        idadeField.setText(String.valueOf(cliente.getAge()));

        if (cliente.getDate() != null) {
            LocalDate localDate = cliente.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dataPicker.setValue(localDate);
        }
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        statusLabel.setText(mensagem);
        statusLabel.setTextFill(erro ? javafx.scene.paint.Color.web("#b71c1c") : javafx.scene.paint.Color.web("#1b5e20"));
    }
}
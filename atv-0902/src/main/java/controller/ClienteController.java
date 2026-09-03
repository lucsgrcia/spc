package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import model.Barbearia;
import model.Cliente;
import model.Repositorios;
import view.BarbeariaView;
import view.ClienteView;

import java.time.ZoneId;
import java.util.Date;

public class ClienteController {
    @FXML private TextField clienteNome;
    @FXML private TextField clienteCPF;
    @FXML private DatePicker clienteDataNasc;
    @FXML private TextField clienteEmail;
    @FXML private TableView<ClienteView> clienteTabela;
    @FXML private TableColumn<ClienteView, String> clienteNomeTab, clienteCPFTab, clienteEmailTab;
    @FXML private TableColumn<ClienteView, Date> clienteDataNascTab;

    @FXML private TextField barberRazaoSocial, barberCNPJ, barberEndereco;
    @FXML private TabPane cadastroTabs;
    @FXML private TableView<BarbeariaView> barberTabela;
    @FXML private TableColumn<BarbeariaView, String> barberRazaoTab, barberCNPJTab, barberEnderecoTab;

    private final Repositorios repositorios = new Repositorios();

    @FXML
    public void initialize() {
        clienteNomeTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        clienteCPFTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCpf()));
        clienteEmailTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        clienteDataNascTab.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDataNasc()));
        barberRazaoTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRazaoSocial()));
        barberCNPJTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCnpj()));
        barberEnderecoTab.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEndereco()));
        clienteTabela.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) {
                clienteNome.setText(b.getNome());
                clienteCPF.setText(b.getCpf());
                clienteEmail.setText(b.getEmail());
                clienteDataNasc.setValue(b.getDataNasc() == null ? null :
                        b.getDataNasc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        });
        barberTabela.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) {
                barberRazaoSocial.setText(b.getRazaoSocial());
                barberCNPJ.setText(b.getCnpj());
                barberEndereco.setText(b.getEndereco());
            }
        });
        recarregar();
    }

    private void recarregar() {
        clienteTabela.setItems(FXCollections.observableArrayList());
        for (Cliente c : repositorios.getClientes().loadAll()) {
            clienteTabela.getItems().add(new ClienteView(c.getId(), c.getNome(), c.getEmail(), c.getCpf(), c.getDataNasc()));
        }
        barberTabela.setItems(FXCollections.observableArrayList());
        for (Barbearia b : repositorios.getBarbearias().loadAll()) {
            barberTabela.getItems().add(new BarbeariaView(b.getId(), b.getRazaoSocial(), b.getCnpj(), b.getEndereco()));
        }
    }

    @FXML public void addOnAction() {
        if (cadastroTabs.getSelectionModel().getSelectedIndex() == 0) {
            clienteTabela.getSelectionModel().clearSelection();
            clienteNome.clear(); clienteCPF.clear(); clienteEmail.clear(); clienteDataNasc.setValue(null);
            clienteNome.requestFocus();
        } else {
            barberTabela.getSelectionModel().clearSelection();
            barberRazaoSocial.clear(); barberCNPJ.clear(); barberEndereco.clear();
            barberRazaoSocial.requestFocus();
        }
    }

    @FXML public void salvarOnAction() {
        try {
            if (cadastroTabs.getSelectionModel().getSelectedIndex() == 0) {
                validar(clienteNome.getText(), clienteCPF.getText());
                Cliente c = new Cliente(clienteNome.getText().trim(), clienteEmail.getText().trim(),
                        clienteCPF.getText().trim(), toDate(clienteDataNasc));
                repositorios.getClientes().create(c);
            } else {
                validar(barberRazaoSocial.getText(), barberCNPJ.getText());
                repositorios.getBarbearias().create(new Barbearia(barberRazaoSocial.getText().trim(),
                        barberCNPJ.getText().trim(), barberEndereco.getText().trim()));
            }
            recarregar();
            limpar();
        } catch (RuntimeException e) { erro(e); }
    }

    @FXML public void attOnAction() {
        try {
            if (cadastroTabs.getSelectionModel().getSelectedIndex() == 0) {
                ClienteView cv = clienteTabela.getSelectionModel().getSelectedItem();
                if (cv == null) { aviso("Selecione um cliente para atualizar."); return; }
                validar(clienteNome.getText(), clienteCPF.getText());
                Cliente c = new Cliente(clienteNome.getText().trim(), clienteEmail.getText().trim(),
                        clienteCPF.getText().trim(), toDate(clienteDataNasc)); c.setId(cv.getId());
                repositorios.getClientes().update(c);
            } else {
                BarbeariaView bv = barberTabela.getSelectionModel().getSelectedItem();
                if (bv == null) { aviso("Selecione um registro para atualizar."); return; }
                validar(barberRazaoSocial.getText(), barberCNPJ.getText());
                Barbearia b = new Barbearia(barberRazaoSocial.getText().trim(), barberCNPJ.getText().trim(),
                        barberEndereco.getText().trim()); b.setId(bv.getId());
                repositorios.getBarbearias().update(b);
            }
            recarregar(); limpar();
        } catch (RuntimeException e) { erro(e); }
    }

    @FXML public void delOnAction() {
        try {
            if (cadastroTabs.getSelectionModel().getSelectedIndex() == 0) {
                ClienteView cv = clienteTabela.getSelectionModel().getSelectedItem();
                if (cv == null) { aviso("Selecione um cliente para deletar."); return; }
                repositorios.getClientes().delete(repositorios.getClientes().loadFromId(cv.getId()));
            } else {
                BarbeariaView bv = barberTabela.getSelectionModel().getSelectedItem();
                if (bv == null) { aviso("Selecione um registro para deletar."); return; }
                repositorios.getBarbearias().delete(repositorios.getBarbearias().loadFromId(bv.getId()));
            }
            recarregar(); limpar();
        } catch (RuntimeException e) { erro(e); }
    }

    private Date toDate(DatePicker picker) {
        return picker.getValue() == null ? null : Date.from(picker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    private void validar(String nome, String documento) {
        if (nome == null || nome.trim().isEmpty() || documento == null || documento.trim().isEmpty())
            throw new IllegalArgumentException("Nome e documento são obrigatórios.");
    }
    private void limpar() {
        clienteNome.clear(); clienteCPF.clear(); clienteEmail.clear(); clienteDataNasc.setValue(null);
        barberRazaoSocial.clear(); barberCNPJ.clear(); barberEndereco.clear();
    }
    private void aviso(String mensagem) { new Alert(Alert.AlertType.WARNING, mensagem).showAndWait(); }
    private void erro(Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
}

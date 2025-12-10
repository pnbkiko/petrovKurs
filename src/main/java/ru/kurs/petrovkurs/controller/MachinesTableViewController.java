package ru.kurs.petrovkurs.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.kurs.petrovkurs.HelloApplication;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.kurs.petrovkurs.util.Manager.*;

public class MachinesTableViewController implements Initializable {

    private int itemsCount;

    private MachinesService machinesService = new MachinesService();
    @FXML
    private TableColumn<Machines, String> TableColumnModel;
    @FXML
    private TableColumn<Machines, String> TableColumnInvNumber;
    @FXML
    private TableColumn<Machines, String> TableColumnCommissionedAt;
    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;
    @FXML
    private TextField TextFieldSearch;
    @FXML
    private TableView<Machines> TableViewMachines;
    @FXML
    void TextFieldSearchAction(ActionEvent event) {
        filterData("");
    }
    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("machines-edit-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setTitle("Изменить данные");
        newWindow.initOwner(Manager.secondStage);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.setScene(scene);
        Manager.currentStage = newWindow;
        newWindow.showAndWait();
        Manager.currentStage = null;
        filterData("");
    }
    @FXML
    void TextFieldTextChanged(InputMethodEvent event) {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initController();
    }
    public void initController() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String todayDate = LocalDate.now().format(formatter);
        LabelDate.setText("Сегодня: " + todayDate);
        setCellValueFactories();
        TextFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });

        filterData("");
    }
    void filterData(String searchText) {
        List<Machines> machines = machinesService.findAll();
        itemsCount = machines.size();

        List<Machines> filteredList = machines.stream()
                .filter(machine -> {
                    String model = machine.getPropertyModel().get();
                    if (model != null) {
                        return model.toLowerCase().contains(searchText.toLowerCase());
                    }
                    return false;
                })
                .collect(Collectors.toList());

        TableViewMachines.getItems().setAll(filteredList);

        int filteredItemsCount = filteredList.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }
    private void setCellValueFactories() {
        TableColumnCommissionedAt.setCellValueFactory(cellData -> cellData.getValue().getPropertyCommissionedAt());
        TableColumnInvNumber.setCellValueFactory(cellData -> cellData.getValue().getPropertyInvNumber());
        TableColumnModel.setCellValueFactory(cellData -> cellData.getValue().getPropertyModel());

    }
    @FXML
    void MenuItemAddAction(ActionEvent event) {
        Manager.currentMachines = null;
        ShowEditProductWindow();
        filterData("");
    }
    @FXML
    void MenuItemBackAction(ActionEvent event) {
        if (Manager.secondStage != null) {
        Manager.secondStage.close();
        }
        Manager.mainStage.show();
    }
    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        Machines machines = TableViewMachines.getSelectionModel().getSelectedItem();
        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            machinesService.delete(machines);
            filterData("");
        }
    }
}


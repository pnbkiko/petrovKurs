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
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceActsService;
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

public class MaintenanceActsTableViewController implements Initializable {

    private int itemsCount;

    private MaintenanceActsService maintenanceActsService = new MaintenanceActsService();
    @FXML
    private DatePicker DatePickerFilter;


    @FXML
    private TableColumn<MaintenanceActs, String> TableColumnMachines,TableColumnTypes,TableColumnDate,TableColumnEngineer,TableColumnNotes,TableColumnSigned;

    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;
    @FXML
    private TableView<MaintenanceActs> TableViewMaintenanceActs;

    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-acts-edit-view.fxml"));

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
        filterData(null);
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
        DatePickerFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

        filterData(null);
    }

    void filterData(LocalDate dateFilter) {
        List<MaintenanceActs> maintenanceActs = maintenanceActsService.findAll();
        itemsCount = maintenanceActs.size();

        List<MaintenanceActs> filteredList = maintenanceActs.stream()
                .filter(act -> {
                    if (dateFilter != null) {
                        return act.getDate_().equals(dateFilter);
                    }
                    return true; // если дата не выбрана, показываем все
                })
                .collect(Collectors.toList());

        TableViewMaintenanceActs.getItems().setAll(filteredList);

        int filteredItemsCount = filteredList.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }

    private void setCellValueFactories() {



        TableColumnMachines.setCellValueFactory(cellData -> cellData.getValue().getMachineModel());
        TableColumnTypes.setCellValueFactory(cellData -> cellData.getValue().getTypeName());
        TableColumnDate.setCellValueFactory(cellData -> cellData.getValue().getPropertyDate_());
        TableColumnEngineer.setCellValueFactory(cellData -> cellData.getValue().getPropertyEngineer());
        TableColumnNotes.setCellValueFactory(cellData -> cellData.getValue().getPropertyNotes());
        TableColumnSigned.setCellValueFactory(cellData -> cellData.getValue().getPropertySigned());

    }

    @FXML
    void MenuItemAddAction(ActionEvent event) {
        Manager.currentMaintenanceActs = null;
        ShowEditProductWindow();
        filterData(null);
    }

    @FXML
    void MenuItemBackAction(ActionEvent event) {
        if (Manager.secondStage != null) {
            Manager.secondStage.close();
        }
        Manager.mainStage.show();
    }

    @FXML
    void MenuItemCategoriesAction(ActionEvent event) {
        Manager.LoadSecondStageScene("category-table-view.fxml");
    }

    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        MaintenanceActs maintenanceActs = TableViewMaintenanceActs.getSelectionModel().getSelectedItem();


        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            maintenanceActsService.delete(maintenanceActs);
            filterData(null);
        }
    }

    @FXML
    void MenuItemManufacturersAction(ActionEvent event) {
        Manager.LoadSecondStageScene("manufacturers-table-view.fxml");
    }

    @FXML
    void MenuItemSuppliersAction(ActionEvent event) {
        Manager.LoadSecondStageScene("suppliers-table-view.fxml");
    }

    @FXML
    void MenuItemUnittypesAction(ActionEvent event) {
        Manager.LoadSecondStageScene("unittypes-table-view.fxml");
    }

    @FXML
    void MenuItemUpdateAction(ActionEvent event) {
        MaintenanceActs maintenanceActs = TableViewMaintenanceActs.getSelectionModel().getSelectedItem();
        currentMaintenanceActs = maintenanceActs;
        ShowEditProductWindow();
        filterData(null);
    }
}


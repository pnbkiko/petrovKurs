package ru.kurs.petrovkurs.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.kurs.petrovkurs.model.*;
import ru.kurs.petrovkurs.service.*;
import ru.kurs.petrovkurs.util.Manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MaintenanceActsEditViewController implements Initializable {
    @FXML
    private Button BtnCancel;
    @FXML
    private Button BtnSave;
    private MaintenanceTypesService maintenanceTypesService = new MaintenanceTypesService();
    private MaintenanceActsService maintenanceActsService = new MaintenanceActsService();
    private MachinesService machinesService = new MachinesService();
    @FXML
    private TextField TextFieldEngineer,TextFieldNotes;
    @FXML
    private DatePicker DatePickerDateWork;
    @FXML
    private ComboBox<MaintenanceTypes> ComboBoxType;
    @FXML
    private ComboBox<Machines> ComboBoxMachine;
    @FXML
    private CheckBox CheckBoxSigned;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ComboBoxType.setItems(FXCollections.observableArrayList(maintenanceTypesService.findAll()));
        ComboBoxMachine.setItems(FXCollections.observableArrayList(machinesService.findAll()));
        if (Manager.currentMaintenanceActs != null) {
            ComboBoxMachine.setValue(Manager.currentMaintenanceActs.getMachines());

            TextFieldEngineer.setText(Manager.currentMaintenanceActs.getEngineer());
            TextFieldNotes.setText(Manager.currentMaintenanceActs.getNotes());
            // Устанавливаем дату из модели в DatePicker
            DatePickerDateWork.setValue(Manager.currentMaintenanceActs.getDate_());
            ComboBoxType.setValue(Manager.currentMaintenanceActs.getType());
            CheckBoxSigned.setSelected(Manager.currentMaintenanceActs.getSigned());
        } else {
            Manager.currentMaintenanceActs = new MaintenanceActs();
        }
    }

    @FXML
    void BtnCancelAction(ActionEvent event) {
        Stage stage = (Stage) BtnCancel.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    void BtnSaveAction(ActionEvent event) throws IOException {
        String error = checkFields().toString();
        if (!error.isEmpty()) {
            MessageBox("Ошибка", "Заполните поля", error, Alert.AlertType.ERROR);
            return;
        }
        Manager.currentMaintenanceActs.setMachines(ComboBoxMachine.getValue());
        Manager.currentMaintenanceActs.setMaintenanceTypes(ComboBoxType.getValue());
        Manager.currentMaintenanceActs.setEngineer(TextFieldEngineer.getText());
        Manager.currentMaintenanceActs.setSigned(CheckBoxSigned.isSelected());
        Manager.currentMaintenanceActs.setNotes(TextFieldNotes.getText());
        LocalDate date = DatePickerDateWork.getValue();
        if (date != null) {
            Manager.currentMaintenanceActs.setDate_(date);
        }

        if (Manager.currentMaintenanceActs.getMaintenanceActsId() == null) {

            maintenanceActsService.save(Manager.currentMaintenanceActs);
            MessageBox("Информация", "", "Данные сохранены успешно", Alert.AlertType.INFORMATION);
        } else {
            maintenanceActsService.update(Manager.currentMaintenanceActs);
            MessageBox("Информация", "", "Данные обновлены успешно", Alert.AlertType.INFORMATION);
        }
        Stage stage = (Stage) BtnSave.getScene().getWindow();
        stage.close();
    }

    StringBuilder checkFields() {
        StringBuilder error = new StringBuilder();
        if (ComboBoxMachine.getValue() == null) {
            error.append("Выберите модель\n");
        }
        if (ComboBoxType.getValue() == null) {
            error.append("Выберите тип ТО\n");
        }
        if (TextFieldEngineer.getText().isEmpty()) {
            error.append("Укажите инженера\n");
        }
        if (DatePickerDateWork.getValue() == null) {
            error.append("Укажите дату\n");
        }
        if (CheckBoxSigned == null) {
            error.append("Укажите дату\n");
        }
        if (!CheckBoxSigned.isSelected()) {
            error.append("Пожалуйста, подтвердите согласие\n");
        }

        return error;
    }



}

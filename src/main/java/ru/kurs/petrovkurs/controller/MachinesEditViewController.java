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
import java.util.ResourceBundle;

import static ru.kurs.petrovkurs.util.Manager.MessageBox;

public class MachinesEditViewController implements Initializable {
    @FXML
    private Button BtnCancel;

    @FXML
    private Button BtnSave;
    private MachinesService machinesService = new MachinesService();
    @FXML
    private TextField TextFieldModel, TextFieldInvNumber;
    @FXML
    private DatePicker DatePickerCommissionedAt;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Manager.currentMachines != null) {
            TextFieldModel.setText(Manager.currentMachines.getModel());
            TextFieldInvNumber.setText(Manager.currentMachines.getInvNumber());
            // Устанавливаем дату из модели в DatePicker
            DatePickerCommissionedAt.setValue(Manager.currentMachines.getCommissionedAt());
        } else {
            Manager.currentMachines = new Machines();
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
        Manager.currentMachines.setModel(TextFieldModel.getText());
        Manager.currentMachines.setInvNumber(TextFieldInvNumber.getText());
        LocalDate date = DatePickerCommissionedAt.getValue();
        if (date != null) {
            Manager.currentMachines.setCommissionedAt(date);
        }

        if (Manager.currentMachines.getMachinesId() == null) {

            machinesService.save(Manager.currentMachines);
            MessageBox("Информация", "", "Данные сохранены успешно", Alert.AlertType.INFORMATION);
        } else {
            machinesService.update(Manager.currentMachines);
            MessageBox("Информация", "", "Данные обновлены успешно", Alert.AlertType.INFORMATION);
        }
        Stage stage = (Stage) BtnSave.getScene().getWindow();
        stage.close();
    }

    StringBuilder checkFields() {
        StringBuilder error = new StringBuilder();
        if (TextFieldModel.getText().isEmpty()) {
            error.append("Укажите модель\n");
        }
        if (TextFieldInvNumber.getText().isEmpty()) {
            error.append("Укажите инвентарный номер\n");
        }
        if (DatePickerCommissionedAt.getValue() == null) {
            error.append("Укажите дату\n");
        }

        return error;
    }



}

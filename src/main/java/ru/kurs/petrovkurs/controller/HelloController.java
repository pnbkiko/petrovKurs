package ru.kurs.petrovkurs.controller;

import jakarta.persistence.Query;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.hibernate.Session;
import ru.kurs.petrovkurs.HelloApplication;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;
import ru.kurs.petrovkurs.util.HibernateSessionFactoryUtil;
import ru.kurs.petrovkurs.util.Manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kurs.petrovkurs.util.Manager.ShowErrorMessageBox;
import static ru.kurs.petrovkurs.util.Manager.screenSize;

public class HelloController {
    int secondsLeft;

    MaintenanceScheduleService maintenanceScheduleService = new MaintenanceScheduleService();

    @FXML
    private Button BtnNotify;
    @FXML
    void BtnCancelAction(ActionEvent event) {
        Manager.ShowPopup();
    }

    @FXML
    void BtnMachinesActon(ActionEvent event) {
        Manager.mainStage.hide();
        Stage newWindow = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("machines-table-view.fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(TradeApp.class.getResource("category-table-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), screenSize.getWidth(), screenSize.getHeight());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setMaximized(true);
        newWindow.setScene(scene);
        newWindow.setOnCloseRequest(e -> {
            Manager.mainStage.show();
        });
        Manager.secondStage = newWindow;

        newWindow.show();

    }
    @FXML
    void BtnNotifyAction(ActionEvent event) {
        notifyUpcomingMaintenance();
    }
    private void notifyUpcomingMaintenance() {
        LocalDate today = LocalDate.now();
        List<LocalDate> targetDates = Arrays.asList(
                today,
                today.plusDays(1),
                today.plusDays(2)
        );

        List<MaintenanceSchedule> allSchedules = maintenanceScheduleService.findAll();

        List<MaintenanceSchedule> upcomingSchedules = allSchedules.stream()
                .filter(ms -> ms.getNextDue() != null && targetDates.contains(ms.getNextDue()))
                .collect(Collectors.toList());

        if (upcomingSchedules.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Уведомление");
            alert.setHeaderText(null);
            alert.setContentText("Нет ТО, запланированных на сегодня, завтра или послезавтра.");
            alert.showAndWait();
        } else {
            StringBuilder message = new StringBuilder("ТО запланированы на сегодня, завтра или послезавтра:\n");
            for (MaintenanceSchedule ms : upcomingSchedules) {
                message.append("Модель: ").append(ms.getMachineModel().get()).append("\n");
                message.append("Тип ТО: ").append(ms.getTypeNames()).append("\n");
                message.append("Следующее ТО: ").append(ms.getNextDue().toString()).append("\n\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Уведомление");
            alert.setHeaderText(null);
            alert.setContentText(message.toString());
            alert.showAndWait();
        }
    }
    @FXML
    void BtnMaintenanceActsAction(ActionEvent event) {
        Manager.mainStage.hide();
        Stage newWindow = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-acts-table-view.fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(TradeApp.class.getResource("category-table-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), screenSize.getWidth(), screenSize.getHeight());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setMaximized(true);
        newWindow.setScene(scene);
        newWindow.setOnCloseRequest(e -> {
            Manager.mainStage.show();
        });
        Manager.secondStage = newWindow;

        newWindow.show();

    }
    @FXML
    void BtnMaintenanceSheduleAction(ActionEvent event) {
        Manager.mainStage.hide();
        Stage newWindow = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-schedule-table-view.fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(TradeApp.class.getResource("category-table-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), screenSize.getWidth(), screenSize.getHeight());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setMaximized(true);
        newWindow.setScene(scene);
        newWindow.setOnCloseRequest(e -> {
            Manager.mainStage.show();
        });
        Manager.secondStage = newWindow;

        newWindow.show();

    }
    @FXML
    void BtnMaintenanceTypesAction(ActionEvent event) {
        Manager.mainStage.hide();
        Stage newWindow = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-types-table-view.fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(TradeApp.class.getResource("category-table-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), screenSize.getWidth(), screenSize.getHeight());
            scene.getStylesheets().add("base-styles.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newWindow.setMaximized(true);
        newWindow.setScene(scene);
        newWindow.setOnCloseRequest(e -> {
            Manager.mainStage.show();
        });
        Manager.secondStage = newWindow;

        newWindow.show();

    }













}
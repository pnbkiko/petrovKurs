package ru.kurs.petrovkurs.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.service.MachinesService;
import ru.kurs.petrovkurs.service.MaintenanceActsService;
import ru.kurs.petrovkurs.service.MaintenanceScheduleService;
import ru.kurs.petrovkurs.util.Manager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class MaintenanceScheduleTableViewController implements Initializable {

    private int itemsCount;

    private MaintenanceScheduleService maintenanceScheduleService = new MaintenanceScheduleService();

    @FXML
    private MenuItem MenuItemAdd;

    @FXML
    private MenuItem MenuItemBack;

    @FXML
    private MenuItem MenuItemCategories;

    @FXML
    private MenuItem MenuItemDelete;

    @FXML
    private MenuItem MenuItemManufacturers;

    @FXML
    private MenuItem MenuItemSuppliers;

    @FXML
    private MenuItem MenuItemUnittypes;

    @FXML
    private MenuItem MenuItemUpdate;
    @FXML
    private DatePicker DatePickerFilter;

    @FXML
    private TableColumn<MaintenanceSchedule, String> TableColumnMachines, TableColumnTypes, TableColumnLastDone;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> TableColumnNextDue;

    @FXML
    private Label LabelInfo;
    @FXML
    private Label LabelDate;
    @FXML
    private TextField TextFieldSearch;

    @FXML
    private MenuItem MenuItemPdfOtchet;
    @FXML
    private TableView<MaintenanceSchedule> TableViewMaintenanceSchedule;

    // Объявляем переменную для шрифта
    private Font customFont;

    @FXML
    void ComboBoxDiscountAction(ActionEvent event) {
        filterData(null);
    }

    @FXML
    void ComboBoxProductTypeAction(ActionEvent event) {
        filterData(null);
    }

    @FXML
    void ComboBoxSortAction(ActionEvent event) {
        filterData(null);
    }

    @FXML
    void TextFieldSearchAction(ActionEvent event) {
        filterData(null);
    }

    @FXML
    void MenuItemPdfOtchetAction(ActionEvent event) {
        generatePdfOverdueReport();
    }

    private void generatePdfOverdueReport() {
        List<MaintenanceSchedule> overdueList = maintenanceScheduleService.findAll().stream()
                .filter(ms -> ms.getNextDue() != null && ms.getNextDue().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        if (overdueList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Отчет");
            alert.setHeaderText(null);
            alert.setContentText("Нет просроченных технических обслуживаний для отчета.");
            alert.showAndWait();
            return;
        }

        // Диалог выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить PDF отчет");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF файлы", "pdf");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // пользователь отменил
        }

        File file = fileChooser.getSelectedFile();
        String filePath = file.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf"; // добавляем расширение, если его нет
        }

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Заголовок
            Font boldFont = new Font(customFont.getBaseFont(), 12);
            document.add(new Paragraph("Отчет: Просроченные ТО", boldFont));
            document.add(new Paragraph(" ", boldFont));

            float[] columnWidths = {150, 150, 150, 150};

            PdfPTable table = new PdfPTable(columnWidths);

            // Добавляем заголовки таблицы
            addTableHeader(table, "Модель");
            addTableHeader(table, "Тип ТО");
            addTableHeader(table, "Следующее ТО");
            addTableHeader(table, "Последнее ТО");

            for (MaintenanceSchedule ms : overdueList) {
                PdfPCell cell1 = new PdfPCell(new Phrase(ms.getMachineModel().get(), boldFont));
                cell1.setMinimumHeight(30);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Phrase(ms.getTypeNames(), boldFont));
                cell2.setMinimumHeight(30);
                table.addCell(cell2);

                PdfPCell cell3 = new PdfPCell(new Phrase(ms.getNextDue().toString(), boldFont));
                cell3.setMinimumHeight(30);
                table.addCell(cell3);

                PdfPCell cell4 = new PdfPCell(new Phrase(ms.getLastDone().toString(), boldFont));
                cell4.setMinimumHeight(30);
                table.addCell(cell4);
            }

            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Отчет");
            alert.setHeaderText(null);
            alert.setContentText("PDF отчет успешно создан: " + filePath);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при создании PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setMinimumHeight(30);
        // Используем кастомный шрифт для заголовков
        header.setPhrase(new Paragraph(headerTitle, customFont));
        header.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
        table.addCell(header);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initController();
    }

    public void initController() {
        // Загружаем шрифт
        try {
            // Укажите путь к вашему файлу шрифта
            String fontPath = getClass().getResource("/fonts/arial.ttf").toExternalForm();
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            customFont = new Font(baseFont, 12, Font.NORMAL);

        } catch (IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
            // В случае ошибки можно оставить шрифт по умолчанию
            customFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        }

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
        List<MaintenanceSchedule> maintenanceSchedules = maintenanceScheduleService.findAll();
        itemsCount = maintenanceSchedules.size();

        List<MaintenanceSchedule> filteredList = maintenanceSchedules.stream()
                .filter(act -> {
                    if (dateFilter != null) {
                        return act.getLastDone().equals(dateFilter);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        TableViewMaintenanceSchedule.getItems().setAll(filteredList);

        int filteredItemsCount = filteredList.size();
        LabelInfo.setText("Всего записей " + filteredItemsCount + " из " + itemsCount);
    }

    private void setCellValueFactories() {
        TableColumnMachines.setCellValueFactory(cellData -> cellData.getValue().getMachineModel());
        TableColumnTypes.setCellValueFactory(cellData -> cellData.getValue().getTypeName());

        // Для колонки "Следующее ТО"
        TableColumnNextDue.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNextDue()));
        TableColumnNextDue.setCellFactory(column -> new TableCell<MaintenanceSchedule, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    LocalDate today = LocalDate.now();
                    if (item.isBefore(today)) {
                        // Просроченные
                        setStyle("-fx-background-color: #ff4d4d;"); // красный
                    } else if (item.equals(today)) {
                        // Сегодня
                        setStyle("-fx-background-color: #ffff66;"); // желтый
                    } else if (item.equals(today.plusDays(1))) {
                        // Завтра
                        setStyle("-fx-background-color: #66ffff;"); // голубой
                    } else if (item.equals(today.plusDays(2))) {
                        // Послезавтра
                        setStyle("-fx-background-color: #99ff99;"); // зеленый
                    } else {
                        setStyle(""); // без цвета
                    }
                }
            }
        });

        // Аналогично для "Последнее ТО" если нужно
        TableColumnLastDone.setCellValueFactory(cellData -> cellData.getValue().getPropertyLastDoe());
    }

    @FXML
    void MenuItemAddAction(ActionEvent event) {
        Manager.currentMaintenanceSchedule = null;
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
    void ShowEditProductWindow() {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenance-schedule-edit-view.fxml"));

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
    @FXML
    void MenuItemDeleteAction(ActionEvent event) {
        MaintenanceSchedule maintenanceSchedule = TableViewMaintenanceSchedule.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = ShowConfirmPopup();
        if (result.get() == ButtonType.OK) {
            maintenanceScheduleService.delete(maintenanceSchedule);
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
        MaintenanceSchedule maintenanceSchedule = TableViewMaintenanceSchedule.getSelectionModel().getSelectedItem();
        currentMaintenanceSchedule = maintenanceSchedule;
        ShowEditProductWindow();
        filterData(null);
    }
}

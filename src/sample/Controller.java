package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;


public class Controller {
    @FXML
    public GridPane gpane;
    @FXML
    public TextField nameIn, valueIn;

    @FXML
    public TableView<TableItem> dataTbl;

    @FXML
    private BarChart<String, Double> chart;
    @FXML
    CategoryAxis xAxis;
    @FXML
    NumberAxis yAxis;

    @FXML
    private LineChart<String, Double> chart2;
    @FXML
    CategoryAxis xAxis2;
    @FXML
    NumberAxis yAxis2;

    @FXML
    private Button addBtn, clearBtn, exportBtn, newBtn;

    @FXML
    StackPane spane;


    private final ObservableList<TableItem> listForTable = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<String, Double>> listForChart = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<String, Double>> listForChart2 = FXCollections.observableArrayList();
    private XYChart.Series<String, Double> valuesSerie = new XYChart.Series<String, Double>();
    private XYChart.Series<String, Double> valuesSerieChart2 = new XYChart.Series<String, Double>();
    double sumTotal;
    double sumTotalCum;
    Alert alert = new Alert(Alert.AlertType.NONE);

    private void sumAll() {
        sumTotal = 0;
        for (int i = 0; i < listForTable.size(); i++) {
            double value = listForTable.get(i).getValue();
            sumTotal += value;
        }
    }

    private void sortData() {
        Comparator<TableItem> comparator = Comparator.comparingDouble(TableItem::getValue);
        FXCollections.sort(listForTable, comparator.reversed());

        Comparator<XYChart.Data<String, Double>> comparator2 =
                Comparator.comparingDouble(XYChart.Data<String, Double>::getYValue);
        FXCollections.sort(valuesSerie.getData(), comparator2.reversed());

        Comparator<XYChart.Data<String, Double>> comparator3 =
                Comparator.comparingDouble(XYChart.Data<String, Double>::getYValue);
        FXCollections.sort(valuesSerieChart2.getData(), comparator3);
    }

    private void calcPerc() {
        for (TableItem tableItem : listForTable) {
            double value = tableItem.getValue();
            double percent = value / sumTotal * 100;
            tableItem.setPercent(percent);
        }
    }

    private void calcCumulativ() {
        sumTotalCum = 0;
        for (TableItem tableItem : listForTable) {
            double percent = tableItem.getPercent();
            sumTotalCum += percent;
            tableItem.setPercentCumulativ(sumTotalCum);
        }
    }

    public void initialize() {
        initTable();
        initChart();
        System.out.println("started");
        nameIn.setPromptText("name");
        valueIn.setPromptText("value");
        switchControls();
    }

    @SuppressWarnings("unchecked")
    public void initTable() {

        sortData();
        sumAll();
        calcPerc();
        calcCumulativ();

        TableColumn<TableItem, String> nameCol = new TableColumn<>("name");
        nameCol.setMinWidth(115);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<TableItem, String>("name"));

        TableColumn<TableItem, Double> valueCol = new TableColumn<>("value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory(
                new PropertyValueFactory<TableItem, Double>("value"));

        TableColumn<TableItem, Double> percentCol = new TableColumn<>("percent");
        percentCol.setMinWidth(100);
        percentCol.setCellValueFactory(
                new PropertyValueFactory<TableItem, Double>("percentRounded"));

        TableColumn<TableItem, Double> percentColCum = new TableColumn<>("cumul. %");
        percentColCum.setMinWidth(50);
        percentColCum.setCellValueFactory(
                new PropertyValueFactory<TableItem, Double>("percentCumulativRounded"));

        dataTbl.setPrefHeight(350);
        dataTbl.setPrefWidth(400);
        dataTbl.setEditable(true);
        dataTbl.setItems(listForTable);
        dataTbl.getColumns().addAll(nameCol, valueCol, percentCol, percentColCum);

    }

    @SuppressWarnings("unchecked")
    public void initChart() {
        configureBarChart();
        configureLineChart();
        for (TableItem tableItem : listForTable) {
            valuesSerie.getData().add(new XYChart.Data<>(tableItem.getName(), tableItem.getValue()));
            valuesSerieChart2.getData().add(new XYChart.Data<>(tableItem.getName(), tableItem.getPercentCumulativ()));
        }
        listForChart.addAll(valuesSerie);
        listForChart2.addAll(valuesSerieChart2);
        chart.setData(listForChart);
        chart2.setData(listForChart2);

    }

    private void configureBarChart() {
        chart.setPrefSize(800, 400);
        chart.setLegendVisible(false);
        chart.setBarGap(0);
        chart.setCategoryGap(5);
        chart.setAnimated(false);
        yAxis.setTickLabelRotation(270);
        yAxis.setLabel("Value");
        yAxis.setLowerBound(0);
        xAxis.setTickLabelRotation(270);

    }

    private void configureLineChart() {

        chart2.setPrefSize(800, 400);
        chart2.setLegendVisible(false);
        chart2.setAnimated(false);
        chart2.getXAxis().setOpacity(0);
        chart2.getYAxis().setSide(Side.RIGHT);
        yAxis2.setLowerBound(0);
        yAxis2.setTickLabelRotation(270);
        yAxis2.setLabel("Percent cumulative");
        xAxis2.setTickLabelRotation(270);
    }

    public void refreshChart() {
        try {
            String nameInput = nameIn.getText();
            double add = Double.parseDouble(valueIn.getText());
            listForTable.add(new TableItem(nameInput, add, 0, 0));
            valuesSerie.getData().add(new XYChart.Data<String, Double>(nameInput, add));
            valuesSerieChart2.getData().add(new XYChart.Data<String, Double>(nameInput, 0.0));
            sortData();
            sumAll();
            calcPerc();
            calcCumulativ();
            redrawLineChart();
            switchControls();
        } catch (NumberFormatException e) {
            alert.setContentText("Podaj poprawne dane");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.show();
        }
    }

    private void switchControls() {
        if (nameIn.isDisable()) {
            nameIn.setDisable(false);
            valueIn.setDisable(false);
            addBtn.setDisable(false);
        } else {
            nameIn.setDisable(true);
            valueIn.setDisable(true);
            addBtn.setDisable(true);
        }
    }

    public void redrawLineChart() {
        for (int i = 0; i < listForTable.size(); i++) {
            double percentCumulativ = listForTable.get(i).getPercentCumulativ();
            valuesSerieChart2.getData().get(i).setYValue(percentCumulativ);
            double value = listForTable.get(i).getValue();
            valuesSerie.getData().get(i).setYValue(value);
        }
    }


    public void startEdit() {
        clearControls();
        switchControls();
    }

    private void clearControls() {
        nameIn.clear();
        valueIn.clear();
    }

    public void clearChart() {
        listForTable.clear();
        valuesSerie.getData().clear();
        valuesSerieChart2.getData().clear();
        clearControls();
    }

    public void exportChart() {
        Scene scene = exportBtn.getScene();
        Window window = scene.getWindow();

        switchVisiblityForExport();
        WritableImage image = gpane.snapshot(new SnapshotParameters(), null);
        switchVisiblityForExport();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                alert.setContentText("Export succesful");
                alert.setHeaderText("Great!");
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.show();
            } catch (IOException ex) {
                alert.setContentText("Export failed");
                alert.setHeaderText("Oh my...");
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.show();
            }
        }
    }

    private void switchVisiblityForExport() {
        if (addBtn.isVisible()) {
            addBtn.setVisible(false);
            clearBtn.setVisible(false);
            exportBtn.setVisible(false);
            newBtn.setVisible(false);
            nameIn.setVisible(false);
            valueIn.setVisible(false);
        } else {
            addBtn.setVisible(true);
            clearBtn.setVisible(true);
            exportBtn.setVisible(true);
            newBtn.setVisible(true);
            nameIn.setVisible(true);
            valueIn.setVisible(true);
        }
    }
}

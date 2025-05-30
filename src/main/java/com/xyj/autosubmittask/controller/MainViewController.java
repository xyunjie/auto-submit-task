package com.xyj.autosubmittask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyj.autosubmittask.entity.Config;
import com.xyj.autosubmittask.service.AutoSubmitService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

/**
 * @author xuyunjie
 */
public class MainViewController {

    @FXML private TextField gitTokenField;
    @FXML private TextField gitUserIdField;
    @FXML private TextField taskTokenField;
    @FXML private TextField taskUrlField;
    @FXML private TextField getTaskUrlField;
    @FXML private TextField taskUserIdField;
    @FXML private CheckBox pushCheckBox;
    @FXML private TextField pushUrlField;
    @FXML private Button generateButton;
    @FXML private Button saveButton;

    @FXML private VBox loadingPane;

    private final ObjectMapper mapper = new ObjectMapper();
    private final File configFile = new File("config.json");

    private Config config = new Config();

    @FXML
    public void initialize() {
        loadConfig();
        generateButton.setOnAction(event -> handleGenerate());
        saveButton.setOnAction(event -> saveConfig());
    }

    private void loadConfig() {
        if (configFile.exists()) {
            try {
                config = mapper.readValue(configFile, Config.class);
                gitTokenField.setText(config.getGitToken());
                gitUserIdField.setText(config.getGitUserId());
                taskTokenField.setText(config.getTaskToken());
                taskUrlField.setText(config.getTaskUrl());
                getTaskUrlField.setText(config.getGetTaskUrl());
                taskUserIdField.setText(config.getTaskUserId());
                pushCheckBox.setSelected(config.isPush());
                pushUrlField.setText(config.getPushUrl());
            } catch (IOException e) {
                showAlert("错误", "加载配置失败: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void saveConfig() {
        Config config = new Config(
                gitTokenField.getText(),
                gitUserIdField.getText(),
                taskTokenField.getText(),
                taskUrlField.getText(),
                getTaskUrlField.getText(),
                taskUserIdField.getText(),
                pushCheckBox.isSelected(),
                pushUrlField.getText()
        );

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
            showAlert("提示", "配置已保存", Alert.AlertType.INFORMATION);
            this.loadConfig();
        } catch (IOException e) {
            showAlert("错误", "保存配置失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleGenerate() {
        if (fieldsValid()) {
            showLoading(true);
            new Thread(() -> {
                AutoSubmitService autoSubmitService = new AutoSubmitService();
                String str = autoSubmitService.submitTask(config);
                Platform.runLater(() -> {
                    showLoading(false);
                    showAlert("成功", str, Alert.AlertType.INFORMATION, 400, 400);
                });
            }).start();
        }
    }

    private boolean fieldsValid() {
        if (isEmpty(gitTokenField) || isEmpty(gitUserIdField) || isEmpty(taskTokenField) ||
            isEmpty(taskUrlField) || isEmpty(getTaskUrlField) || isEmpty(taskUserIdField)) {
            showAlert("提示", "请完整填写所有配置信息", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        // 使用不可编辑的 TextArea 显示内容，带自动换行
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(-1);
        textArea.setPrefHeight(-1);

        alert.getDialogPane().setContent(textArea);

        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType type, Integer width, Integer height) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        // 使用不可编辑的 TextArea 显示内容，带自动换行
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(width);
        textArea.setPrefHeight(height);

        alert.getDialogPane().setContent(textArea);

        alert.showAndWait();
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
    }
}

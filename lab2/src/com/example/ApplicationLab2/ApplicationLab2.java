package com.example.ApplicationLab2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

public class ApplicationLab2 extends Application {
    private final StackPane mainContainer = new StackPane();
    private final TextArea activityLog = new TextArea();
    private final Map<Integer, InputComponent> components = new TreeMap<>();
    private int currentId = 6;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryWindow) {
        configurePrimaryWindow(primaryWindow);
        initializeUIComponents();
        setupMainScene(primaryWindow);
        primaryWindow.show();
    }

    private void configurePrimaryWindow(Stage window) {
        window.setTitle("Input Field Synchronizer");
        window.setMinWidth(550);
        window.setMinHeight(550);
        window.setResizable(false);
    }

    private void initializeUIComponents() {
        activityLog.setFont(Font.font("Consolas", 14));
        activityLog.setEditable(false);

        VBox inputArea = createInputFieldSection();
        HBox controlPanel = createControlPanel();

        BorderPane layout = new BorderPane();
        layout.setTop(controlPanel);
        layout.setCenter(new ScrollPane(inputArea));
        layout.setBottom(activityLog);

        mainContainer.getChildren().add(layout);
    }

    private VBox createInputFieldSection() {
        VBox fieldContainer = new VBox(8);
        fieldContainer.setPadding(new Insets(8));

        for (int i = 1; i <= currentId; i++) {
            addInputComponent(fieldContainer, i);
        }
        return fieldContainer;
    }

    private void addInputComponent(VBox container, int id) {
        HBox row = new HBox(10);
        Label identifier = new Label(String.valueOf(id));
        identifier.setFont(Font.font(15));

        TextField input = new TextField("Field " + id);
        input.setFont(Font.font(15));
        input.setMinWidth(350);

        components.put(id, new InputComponent(input, row));
        row.getChildren().addAll(identifier, input);
        container.getChildren().add(row);
    }

    private HBox createControlPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(10));

        Button resizeBtn = createActionButton("Resize", this::showResizeDialog);
        Button toggleBtn = createActionButton("Toggles", this::showToggleDialog);
        Button modifyBtn = createActionButton("Modify Fields", this::showFieldModifier);

        panel.getChildren().addAll(resizeBtn, toggleBtn, modifyBtn);
        return panel;
    }

    private Button createActionButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font(14));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void showResizeDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Window Scaling");
        dialog.setResizable(false);

        GridPane content = new GridPane();
        content.setPadding(new Insets(15));
        content.setVgap(10);
        content.setHgap(10);
        content.setAlignment(Pos.CENTER); // Центрируем содержимое

        Label prompt = new Label("Enter scale factor (0.1-5.0):");
        TextField scaleInput = new TextField();
        scaleInput.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*(\\.\\d*)?")) {
                scaleInput.setText(old);
            }
        });

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER); // Центрируем кнопку
        Button apply = createActionButton("Apply", () -> {
            try {
                double factor = Double.parseDouble(scaleInput.getText());
                adjustWindowSize(factor);
                dialog.close();
            } catch (NumberFormatException ex) {
                recordActivity("Invalid scale value entered");
            }
        });
        buttonContainer.getChildren().add(apply);

        content.addRow(0, prompt);
        content.addRow(1, scaleInput);
        content.addRow(2, buttonContainer);

        dialog.setScene(new Scene(content));
        dialog.showAndWait();
    }

    private void adjustWindowSize(double factor) {
        if (factor <= 0.1 || factor > 5.0) {
            recordActivity("Scale factor must be between 0.1 and 5.0");
            return;
        }

        Stage mainStage = (Stage) mainContainer.getScene().getWindow();
        Screen display = Screen.getPrimary();
        Rectangle2D bounds = display.getVisualBounds();

        double newWidth = Math.min(mainStage.getWidth() * factor, bounds.getWidth());
        double newHeight = Math.min(mainStage.getHeight() * factor, bounds.getHeight());

        mainStage.setWidth(newWidth);
        mainStage.setHeight(newHeight);

        mainStage.setX((bounds.getWidth() - newWidth) / 2);
        mainStage.setY((bounds.getHeight() - newHeight) / 2);

        recordActivity("Window resized by factor: " + factor);
    }

    private void showToggleDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Field Toggles");
        dialog.setResizable(false);

        VBox toggleContainer = new VBox(8);
        toggleContainer.setPadding(new Insets(10));

        components.forEach((id, comp) -> {
            CheckBox toggle = new CheckBox(comp.field().getText());
            toggle.setFont(Font.font(14));

            // Устанавливаем начальное состояние CheckBox в зависимости от текущего шрифта поля
            Font currentFont = comp.field().getFont();
            boolean isBold = currentFont.getStyle().contains("Bold");
            toggle.setSelected(isBold);

            toggle.selectedProperty().addListener((obs, old, selected) -> {
                if (selected) {
                    comp.field().setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                } else {
                    comp.field().setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                }
                recordActivity("Toggle " + id + (selected ? " enabled" : " disabled"));
            });

            comp.field().textProperty().addListener((obs, old, text) -> {
                toggle.setText(text);
            });

            toggleContainer.getChildren().add(toggle);
        });

        dialog.setScene(new Scene(new ScrollPane(toggleContainer), 250, 300));
        dialog.show();
    }

    private void showFieldModifier() {
        Stage dialog = new Stage();
        dialog.setTitle("Manage Fields");
        dialog.setResizable(false);

        VBox controls = new VBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER); // Центрируем содержимое

        // Кнопка Add New Field
        Button addField = createActionButton("Add New Field", () -> {
            currentId++;
            BorderPane root = (BorderPane) mainContainer.getChildren().get(0);
            ScrollPane scrollPane = (ScrollPane) root.getCenter();
            VBox container = (VBox) scrollPane.getContent();
            addInputComponent(container, currentId);
            recordActivity("Added field " + currentId);
        });

        // Устанавливаем минимальную ширину для кнопки
        addField.setMinWidth(200);

        // Панель для удаления поля
        VBox removalPanel = new VBox(10);
        removalPanel.setAlignment(Pos.CENTER);

        TextField fieldIdInput = new TextField();
        fieldIdInput.setPromptText("Enter field ID");
        // Устанавливаем ширину поля ввода как половину от ширины кнопки
        fieldIdInput.setMaxWidth(100); // Половина от 200

        Button removeField = createActionButton("Remove Field", () -> {
            try {
                int id = Integer.parseInt(fieldIdInput.getText());
                if (components.containsKey(id)) {
                    BorderPane root = (BorderPane) mainContainer.getChildren().get(0);
                    ScrollPane scrollPane = (ScrollPane) root.getCenter();
                    VBox container = (VBox) scrollPane.getContent();
                    container.getChildren().remove(components.get(id).container());
                    components.remove(id);
                    recordActivity("Removed field " + id);
                } else {
                    recordActivity("Field " + id + " not found");
                }
            } catch (NumberFormatException ex) {
                recordActivity("Invalid field ID entered");
            }
        });
        removeField.setMinWidth(200); // Такая же ширина как у Add

        removalPanel.getChildren().addAll(removeField, fieldIdInput); // Поменяли порядок - кнопка сверху
        controls.getChildren().addAll(addField, removalPanel);

        dialog.setScene(new Scene(controls));
        dialog.show();
    }

    private void setupMainScene(Stage window) {
        Scene mainScene = new Scene(mainContainer, 600, 500);
        window.setScene(mainScene);
    }

    private void recordActivity(String message) {
        activityLog.appendText(message + "\n");
    }

    private record InputComponent(TextField field, HBox container) {}
}
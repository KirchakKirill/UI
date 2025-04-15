package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SimpleCalculator extends Application {

    private TextField display;
    private double firstNumber = 0;
    private String operator = "";
    private boolean startNewInput = true;

    // Константы для стилей
    private static final String DISPLAY_STYLE = "-fx-font-size: 24pt; -fx-padding: 10px; -fx-background-color: #f8f8f8;";
    private static final String NUMBER_BUTTON_STYLE = "-fx-font-size: 18pt; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1px;";
    private static final String OPERATOR_BUTTON_STYLE = "-fx-font-size: 18pt; -fx-background-color: #f0f0f0; -fx-border-color: #000000; -fx-border-width: 1px;";
    private static final String EQUALS_BUTTON_STYLE = "-fx-font-size: 20pt; -fx-background-color: #ff9800; -fx-text-fill: white; -fx-border-color: #000000; -fx-border-width: 1px;";
    private static final String FUNCTION_BUTTON_STYLE = "-fx-font-size: 18pt; -fx-background-color: #e0e0e0; -fx-border-color: #000000; -fx-border-width: 1px;";

    // Размеры кнопок
    private static final int BUTTON_SIZE = 75;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Калькулятор");

        // Настройка дисплея
        display = new TextField();
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle(DISPLAY_STYLE);
        VBox.setVgrow(display, Priority.NEVER);

        // Создание кнопок
        Button[] numberButtons = new Button[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = createButton(String.valueOf(i), NUMBER_BUTTON_STYLE, event -> {
                if (startNewInput) {
                    display.clear();
                    startNewInput = false;
                }
                display.appendText(((Button)event.getSource()).getText());
            });
        }

        Button acButton = createButton("AC", FUNCTION_BUTTON_STYLE, e -> clearAll());
        Button cButton = createButton("C", FUNCTION_BUTTON_STYLE, e -> clearLast());
        Button signButton = createButton("+/-", FUNCTION_BUTTON_STYLE, e -> changeSign());
        Button percentButton = createButton("%", FUNCTION_BUTTON_STYLE, e -> percent());

        Button addButton = createButton("+", OPERATOR_BUTTON_STYLE, e -> setOperator("+"));
        Button subtractButton = createButton("-", OPERATOR_BUTTON_STYLE, e -> setOperator("-"));
        Button multiplyButton = createButton("×", OPERATOR_BUTTON_STYLE, e -> setOperator("*"));
        Button divideButton = createButton("÷", OPERATOR_BUTTON_STYLE, e -> setOperator("/"));

        Button decimalButton = createButton(".", NUMBER_BUTTON_STYLE, e -> addDecimal());
        Button equalsButton = createButton("=", EQUALS_BUTTON_STYLE, e -> calculate());

        // Настройка сетки кнопок
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(0);
        buttonGrid.setVgap(0);
        buttonGrid.setPadding(new Insets(0));
        buttonGrid.setStyle("-fx-background-color: #000000;");

        // Настройка размеров колонок
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(BUTTON_SIZE);
            col.setPrefWidth(BUTTON_SIZE);
            buttonGrid.getColumnConstraints().add(col);
        }

        // Настройка размеров строк
        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(BUTTON_SIZE);
            row.setPrefHeight(BUTTON_SIZE);
            buttonGrid.getRowConstraints().add(row);
        }

        // Расположение кнопок
        // Первый ряд
        buttonGrid.add(acButton, 0, 0);
        buttonGrid.add(cButton, 1, 0);
        buttonGrid.add(signButton, 2, 0);
        buttonGrid.add(divideButton, 3, 0);

        // Второй ряд
        buttonGrid.add(numberButtons[7], 0, 1);
        buttonGrid.add(numberButtons[8], 1, 1);
        buttonGrid.add(numberButtons[9], 2, 1);
        buttonGrid.add(multiplyButton, 3, 1);

        // Третий ряд
        buttonGrid.add(numberButtons[4], 0, 2);
        buttonGrid.add(numberButtons[5], 1, 2);
        buttonGrid.add(numberButtons[6], 2, 2);
        buttonGrid.add(subtractButton, 3, 2);

        // Четвертый ряд
        buttonGrid.add(numberButtons[1], 0, 3);
        buttonGrid.add(numberButtons[2], 1, 3);
        buttonGrid.add(numberButtons[3], 2, 3);
        buttonGrid.add(addButton, 3, 3);

        // Пятый ряд
        buttonGrid.add(numberButtons[0], 0, 4); // Кнопка 0 под кнопкой 1
        buttonGrid.add(decimalButton, 1, 4);  // Кнопка . под кнопкой 2
        buttonGrid.add(equalsButton, 2, 4, 2, 1); // Кнопка = под кнопками 3 и +

        // Корневой контейнер
        VBox root = new VBox();
        root.setSpacing(0);
        root.getChildren().addAll(display, buttonGrid);
        VBox.setVgrow(buttonGrid, Priority.ALWAYS);

        Scene scene = new Scene(root, BUTTON_SIZE * 4, BUTTON_SIZE * 5 + 50);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button createButton(String text, String style, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setStyle(style);
        button.setOnAction(handler);
        return button;
    }

    private void clearAll() {
        display.clear();
        firstNumber = 0;
        operator = "";
    }

    private void clearLast() {
        String text = display.getText();
        if (!text.isEmpty()) {
            display.setText(text.substring(0, text.length() - 1));
        }
    }

    private void changeSign() {
        if (!display.getText().isEmpty()) {
            double value = Double.parseDouble(display.getText());
            display.setText(String.valueOf(-value));
        }
    }

    private void percent() {
        if (!display.getText().isEmpty()) {
            double value = Double.parseDouble(display.getText());
            display.setText(String.valueOf(value / 100));
        }
    }

    private void addDecimal() {
        if (!display.getText().contains(".")) {
            display.appendText(".");
        }
    }

    private void setOperator(String op) {
        if (!display.getText().isEmpty()) {
            firstNumber = Double.parseDouble(display.getText());
            operator = op;
            startNewInput = true;
        }
    }

    private void calculate() {
        if (!operator.isEmpty() && !display.getText().isEmpty()) {
            double secondNumber = Double.parseDouble(display.getText());
            double result = 0;

            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        display.setText("Ошибка");
                        return;
                    }
                    break;
            }

            if (result == (long) result) {
                display.setText(String.format("%d", (long) result));
            } else {
                display.setText(String.valueOf(result));
            }

            operator = "";
            startNewInput = true;
        }
    }
}
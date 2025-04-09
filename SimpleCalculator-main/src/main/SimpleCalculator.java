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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Калькулятор");


        display = new TextField();
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle("-fx-font-size: 24pt; -fx-padding: 10px;");
        VBox.setVgrow(display, Priority.NEVER);


        Button[] numberButtons = new Button[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = createButton(String.valueOf(i), event -> {
                if (startNewInput) {
                    display.clear();
                    startNewInput = false;
                }
                display.appendText(((Button)event.getSource()).getText());
            });
        }


        Button acButton = createButton("AC", e -> {
            display.clear();
            firstNumber = 0;
            operator = "";
        });

        Button cButton = createButton("C", e -> {
            String text = display.getText();
            if (!text.isEmpty()) {
                display.setText(text.substring(0, text.length() - 1));
            }
        });

        Button signButton = createButton("+/-", e -> {
            if (!display.getText().isEmpty()) {
                double value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(-value));
            }
        });

        Button percentButton = createButton("%", e -> {
            if (!display.getText().isEmpty()) {
                double value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(value / 100));
            }
        });


        Button addButton = createButton("+", e -> setOperator("+"));
        Button subtractButton = createButton("-", e -> setOperator("-"));
        Button multiplyButton = createButton("×", e -> setOperator("*"));
        Button divideButton = createButton("÷", e -> setOperator("/"));
        Button equalsButton = createButton("=", e -> calculate());
        Button decimalButton = createButton(".", e -> {
            if (!display.getText().contains(".")) {
                display.appendText(".");
            }
        });


        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(2);
        buttonGrid.setVgap(2);
        buttonGrid.setPadding(new Insets(2));
        buttonGrid.setStyle("-fx-background-color: #f0f0f0;");


        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setHgrow(Priority.ALWAYS);
        colConstraints.setFillWidth(true);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setFillHeight(true);

        for (int i = 0; i < 4; i++) {
            buttonGrid.getColumnConstraints().add(colConstraints);
        }
        for (int i = 0; i < 5; i++) {
            buttonGrid.getRowConstraints().add(rowConstraints);
        }


        buttonGrid.add(acButton, 0, 0);
        buttonGrid.add(cButton, 1, 0);
        buttonGrid.add(signButton, 2, 0);
        buttonGrid.add(divideButton, 3, 0);


        buttonGrid.add(numberButtons[7], 0, 1);
        buttonGrid.add(numberButtons[8], 1, 1);
        buttonGrid.add(numberButtons[9], 2, 1);
        buttonGrid.add(multiplyButton, 3, 1);


        buttonGrid.add(numberButtons[4], 0, 2);
        buttonGrid.add(numberButtons[5], 1, 2);
        buttonGrid.add(numberButtons[6], 2, 2);
        buttonGrid.add(subtractButton, 3, 2);


        buttonGrid.add(numberButtons[1], 0, 3);
        buttonGrid.add(numberButtons[2], 1, 3);
        buttonGrid.add(numberButtons[3], 2, 3);
        buttonGrid.add(addButton, 3, 3);


        buttonGrid.add(numberButtons[0], 0, 4, 2, 1);
        buttonGrid.add(decimalButton, 2, 4);
        buttonGrid.add(equalsButton, 3, 4);


        VBox root = new VBox();
        root.setSpacing(0);
        root.getChildren().addAll(display, buttonGrid);
        VBox.setVgrow(buttonGrid, Priority.ALWAYS);


        Scene scene = new Scene(root, 300, 450);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setMinSize(60, 60);
        button.setStyle("-fx-font-size: 18pt; -fx-background-radius: 0;");


        if (text.matches("[0-9.]")) {
            button.setStyle(button.getStyle() + "-fx-background-color: #ffffff;");
        } else if (text.equals("=")) {
            button.setStyle(button.getStyle() + "-fx-background-color: #ff9800; -fx-text-fill: white;");
        } else {
            button.setStyle(button.getStyle() + "-fx-background-color: #e0e0e0;");
        }

        button.setOnAction(handler);
        return button;
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
package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorView extends JFrame implements ActionListener {

    //region Properties

    private JTextField displayTextField;

    private double firstNumber = 0;
    private String operatorText = "";
    private boolean shouldClearDisplay = true;

    //endregion

    //region Initialization

    public CalculatorView() {
        setView();
        setComponent();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(CalculatorView::new);
    }

    //endregion

    //region UI Setup

    private void setView() {
        setResizable(false);
        setTitle("Calculator");
        setLocationRelativeTo(null);
        setSize(380, 620);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout(0, 0));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.awt.application.appearance", "system");
            getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }
    }

    private void setComponent() {
        createDisplayPanel();
        createButtonPanel();
    }

    private void createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        displayTextField = new JTextField("0");
        displayTextField.setBorder(null);
        displayTextField.setEditable(false);
        displayTextField.setForeground(Color.WHITE);
        displayTextField.setBackground(Color.BLACK);
        displayTextField.setCaretColor(Color.BLACK);
        displayTextField.setHorizontalAlignment(JTextField.RIGHT);
        displayTextField.setFont(new Font("SF Pro Display", Font.PLAIN, 64));

        displayPanel.add(displayTextField, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.NORTH);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[][] buttons = {
            {"AC", "±", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"0", "0_placeholder", ".", "="}
        };

        for (String[] strings : buttons) {
            for (String text : strings) {
                switch (text) {
                    case "0_placeholder" -> {} // Skip placeholder
                    case "±", "%" -> {
                        JButton emptyButton = new RoundButton("");
                        emptyButton.setEnabled(false);
                        emptyButton.setFocusPainted(false);
                        emptyButton.setBorderPainted(false);
                        emptyButton.setBackground(new Color(80, 80, 80));
                        buttonPanel.add(emptyButton);
                    }
                    default -> {
                        JButton button = createButton(text);
                        buttonPanel.add(button);
                    }
                }
            }
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new RoundButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(this);
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 32));

        ButtonType type = ButtonType.from(text);
        switch (type) {
            case NUMBER, DECIMAL -> {
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(51, 51, 51));
            }
            case OPERATOR, EQUALS -> {
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(255, 149, 0));
            }
            case CLEAR -> {
                button.setForeground(Color.BLACK);
                button.setBackground(new Color(165, 165, 165));
            }
        }

        return button;
    }

    //endregion

    //region Event Handling

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        ButtonType buttonType = ButtonType.from(command);

        switch (buttonType) {
            case NUMBER -> handleNumberButton(command);
            case DECIMAL -> handleDecimalPoint();
            case OPERATOR -> {
                String op = switch (command) {
                    case "÷" -> "/";
                    case "×" -> "*";
                    default -> command;
                };
                handleOperator(op);
            }
            case EQUALS -> calculateExpression();
            case CLEAR -> clearTextField();
        }
    }

    //endregion

    //region Helper Functions

    private void handleNumberButton(String number) {
        String current = shouldClearDisplay ? "0" : displayTextField.getText();
        displayTextField.setText(current.equals("0") ? number : current + number);
        shouldClearDisplay = false;
    }

    private void handleDecimalPoint() {
        String current = displayTextField.getText();
        displayTextField.setText(shouldClearDisplay ? "0." : (!current.contains(".") ? current + "." : current));
        shouldClearDisplay = false;
    }

    private void handleOperator(String op) {
        calculateExpression();
        firstNumber = Double.parseDouble(displayTextField.getText());
        operatorText = op;
        shouldClearDisplay = true;
    }

    private void calculateExpression() {
        if (!operatorText.isEmpty() && !shouldClearDisplay) {
            double secondNumber = Double.parseDouble(displayTextField.getText());

            // TODO: 서버 소켓 통신으로 계산 처리
            // 서버에 firstNumber, operatorText, secondNumber를 전송하고 결과를 받아옴
            double result = 0;

            displayTextField.setText(result == (long) result ? String.valueOf((long) result) : String.valueOf(result));
            operatorText = "";
            shouldClearDisplay = true;
        }
    }

    private void clearTextField() {
        displayTextField.setText("0");
        firstNumber = 0;
        operatorText = "";
        shouldClearDisplay = true;
    }

    //endregion
}

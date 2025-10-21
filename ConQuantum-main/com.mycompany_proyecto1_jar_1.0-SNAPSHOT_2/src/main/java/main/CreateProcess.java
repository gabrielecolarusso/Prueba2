package main;

import primitivas.*;
import classes.ProcessImage;
import classes.ProcessImageCSV;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class CreateProcess extends javax.swing.JFrame {

    public List<Integer> instructions;
    public W1 father;
    
    public CreateProcess(W1 w1) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        father = w1;
        instructions = new List();
        
        // Set default state
        instructionsTextArea.setEnabled(false);
        typeComboBox.setSelectedIndex(1); // Default to CPU Bound
    }
    
    public CreateProcess() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
    
    /**
     * Checks if the ID already exists in procesos.csv
     */
    private boolean isIdUnique(int id) {
        Set<Integer> existingIds = new HashSet<>();
        String filePath = "procesos.csv";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length > 0) {
                    try {
                        int existingId = Integer.parseInt(values[0]);
                        existingIds.add(existingId);
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read procesos.csv: " + e.getMessage());
        }
        
        return !existingIds.contains(id);
    }
    
    /**
     * Validates the ID field
     */
    private boolean validateId() {
        String idText = idTextField.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Process ID cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int id = Integer.parseInt(idText);
            
            if (id < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Process ID must be a positive number", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!isIdUnique(id)) {
                JOptionPane.showMessageDialog(this, 
                    "Process ID " + id + " already exists. Please use a unique ID.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Process ID must be a valid integer", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Validates the name field
     */
    private boolean validateName() {
        String name = nameTextField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Process Name cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the duration field
     */
    private boolean validateDuration() {
        String durationText = durationTextField.getText().trim();
        
        if (durationText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Duration cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int duration = Integer.parseInt(durationText);
            
            if (duration <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Duration must be greater than 0", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Duration must be a valid integer", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Validates the instructions text area for I/O Bound processes
     */
    private boolean validateInstructions() {
        // If CPU Bound, no instructions needed
        if (typeComboBox.getSelectedIndex() == 1) {
            instructions = new List<>();
            return true;
        }
        
        // For I/O Bound, validate instructions
        String input = instructionsTextArea.getText().trim();
        
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "I/O Bound processes must have instructions.\nFormat: position1,duration1,position2,duration2,...", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        instructions = new List<>();
        StringBuilder numberBuilder = new StringBuilder();

        try {
            for (int i = 0; i < input.length(); i++) {
                char currentChar = input.charAt(i);

                if (currentChar == ',') {
                    if (numberBuilder.length() > 0) {
                        int number = Integer.parseInt(numberBuilder.toString().trim());
                        if (number <= 0) {
                            JOptionPane.showMessageDialog(this, 
                                "All instruction values must be greater than 0", 
                                "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        instructions.appendLast(number);
                        numberBuilder.setLength(0);
                    }
                } else if (Character.isDigit(currentChar)) {
                    numberBuilder.append(currentChar);
                } else if (!Character.isWhitespace(currentChar)) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid character found: '" + currentChar + "'\nOnly numbers and commas are allowed", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Add the last number
            if (numberBuilder.length() > 0) {
                int number = Integer.parseInt(numberBuilder.toString().trim());
                if (number <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "All instruction values must be greater than 0", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                instructions.appendLast(number);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Instructions must contain valid integers", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Verify even number of values (position, duration pairs)
        if (instructions.getSize() % 2 == 1) {
            JOptionPane.showMessageDialog(this, 
                "Instructions must be in pairs (position, duration).\nYou have " + instructions.getSize() + " values.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check if even-indexed values (positions) are in ascending order
        for (int i = 2; i < instructions.getSize(); i += 2) {
            if ((int)instructions.getNodoById(i).getValue() <= (int) instructions.getNodoById(i - 2).getValue()) {
                JOptionPane.showMessageDialog(this, 
                    "Instruction positions must be in ascending order", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }
    
    /**
     * Validates all fields and creates the process
     */
    private void createProcess() {
        // Validate all fields
        if (!validateId()) return;
        if (!validateName()) return;
        if (!validateDuration()) return;
        if (!validateInstructions()) return;
        
        // All validations passed, create the process
        try {
            int id = Integer.parseInt(idTextField.getText().trim());
            String name = nameTextField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            int duration = Integer.parseInt(durationTextField.getText().trim());
            
            // Create the process through the parent window
            father.createNewProcess(instructions, name, type, duration, id);
            
            JOptionPane.showMessageDialog(this, 
                "Process created successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error creating process: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        durationLabel = new javax.swing.JLabel();
        durationTextField = new javax.swing.JTextField();
        instructionsLabel = new javax.swing.JLabel();
        instructionsScrollPane = new javax.swing.JScrollPane();
        instructionsTextArea = new javax.swing.JTextArea();
        instructionsHintLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Process");

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        titleLabel.setText("Create New Process");

        idLabel.setText("Process ID:");

        idTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                idTextFieldFocusLost(evt);
            }
        });

        nameLabel.setText("Process Name:");

        typeLabel.setText("Process Type:");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "I/O Bound", "CPU Bound" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        durationLabel.setText("Duration (cycles):");

        instructionsLabel.setText("I/O Instructions:");

        instructionsTextArea.setColumns(20);
        instructionsTextArea.setRows(5);
        instructionsTextArea.setToolTipText("Enter I/O instructions as: position,duration (e.g., 100,200,500,600)");
        instructionsScrollPane.setViewportView(instructionsTextArea);

        instructionsHintLabel.setFont(new java.awt.Font("Dialog", 2, 11));
        instructionsHintLabel.setText("Format: position1,duration1,position2,duration2,... (e.g., 100,200,500,600)");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        createButton.setBackground(new java.awt.Color(0, 204, 153));
        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(durationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(instructionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(idTextField)
                            .addComponent(nameTextField)
                            .addComponent(typeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(durationTextField)
                            .addComponent(instructionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)))
                    .addComponent(instructionsHintLabel)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(durationLabel)
                    .addComponent(durationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instructionsLabel)
                    .addComponent(instructionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instructionsHintLabel)
                .addGap(30, 30, 30)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(createButton))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // Enable/disable instructions based on process type
        if (typeComboBox.getSelectedIndex() == 1) { // CPU Bound
            instructionsTextArea.setEnabled(false);
            instructionsTextArea.setText("");
            instructionsTextArea.setBackground(new java.awt.Color(240, 240, 240));
        } else { // I/O Bound
            instructionsTextArea.setEnabled(true);
            instructionsTextArea.setBackground(java.awt.Color.WHITE);
        }
    }

    private void idTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        // Optionally validate ID when focus is lost
        String idText = idTextField.getText().trim();
        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);
                if (!isIdUnique(id)) {
                    idTextField.setBackground(new java.awt.Color(255, 200, 200));
                } else {
                    idTextField.setBackground(java.awt.Color.WHITE);
                }
            } catch (NumberFormatException e) {
                idTextField.setBackground(new java.awt.Color(255, 200, 200));
            }
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        createProcess();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateProcess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateProcess().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton createButton;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JTextField durationTextField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JLabel instructionsHintLabel;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JScrollPane instructionsScrollPane;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JComboBox<String> typeComboBox;
    private javax.swing.JLabel typeLabel;
}
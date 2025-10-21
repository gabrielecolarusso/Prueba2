package main;

import primitivas.*;
import classes.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

public class W1 extends javax.swing.JFrame {
    public Semaphore onPlay;
    public Semaphore onPlayClock;
    public List readyList;
    public List allProcessList;
    public UtilityGraph w2;
    private Dispatcher dispatcher;
    private MetricsCollector metrics;
    
    private void loadConfig() {
        String filePath = "configuracion.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            if (line != null) {
                String[] values = line.split(",");
                if (values.length < 3) {
                    System.out.println("Error: El archivo de configuración no tiene el formato correcto.");
                    return;
                }

                int selectedAlgorithm = Integer.parseInt(values[0]);
                int numberOfInstructions = Integer.parseInt(values[1]);
                int quantum = Integer.parseInt(values[2]);

                selectDispatcher.setSelectedIndex(selectedAlgorithm);
                timeSlider.setValue(numberOfInstructions);
                quantumSlider.setValue(quantum);
                this.instructionTime.setText(this.timeSlider.getValue() + " ms");
                this.quantumLabel.setText("Quantum: " + this.quantumSlider.getValue());
                System.out.println("Configuración cargada desde CSV.");
            }
        } catch (IOException e) {
            System.out.println("No se encontró el archivo de configuración. Se usarán valores por defecto.");
        }
    }
    
    public W1(Semaphore onPlay, Semaphore onPlay1, List readyList, List allProcess, MetricsCollector metrics) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.onPlay = onPlay;
        this.onPlayClock = onPlay1;
        this.readyList = readyList;
        this.allProcessList = allProcess;
        this.metrics = metrics;
        w2 = new UtilityGraph("CPU usage");
        
        customizeComponents();
        loadConfig();
        this.updatePCBs();
        this.updateMetrics(metrics);
    }

    public W1() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        customizeComponents();
    }
    
    private void customizeComponents() {
        // Set modern colors
        Color primaryColor = new Color(41, 128, 185);
        Color secondaryColor = new Color(52, 152, 219);
        Color backgroundColor = new Color(236, 240, 241);
        Color textColor = new Color(44, 62, 80);
        Color accentColor = new Color(46, 204, 113);
        Color warningColor = new Color(231, 76, 60);
        
        // Main panel
        jPanel1.setBackground(backgroundColor);
        
        // Scroll panes with borders
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color borderColor = new Color(189, 195, 199);
        
        jScrollPane1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "Ready Queue", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            primaryColor));
        readyTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        readyTextArea.setBackground(Color.WHITE);
        
        jScrollPane2.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "CPU Status", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            primaryColor));
        cpuTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        cpuTextArea.setBackground(Color.WHITE);
        
        jScrollPane3.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "Blocked", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            warningColor));
        blockedTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        blockedTextArea.setBackground(Color.WHITE);
        
        jScrollPane4.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "Exit", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            accentColor));
        exitTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        exitTextArea.setBackground(Color.WHITE);
        
        jScrollPane5.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "Process Control Blocks", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            primaryColor));
        pcbTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        pcbTextArea.setBackground(Color.WHITE);
        
        jScrollPane6.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2), 
            "Suspended", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            new Color(243, 156, 18)));
        suspendedTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        suspendedTextArea.setBackground(Color.WHITE);
        
        // Buttons
        playButton.setBackground(accentColor);
        playButton.setForeground(Color.BLACK);
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);

        createProcessButton.setBackground(secondaryColor);
        createProcessButton.setForeground(Color.BLACK);
        createProcessButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createProcessButton.setFocusPainted(false);
        createProcessButton.setBorderPainted(false);

        saveButton.setBackground(new Color(230, 126, 34));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);

        showUsageButton.setBackground(new Color(155, 89, 182));
        showUsageButton.setForeground(Color.BLACK);
        showUsageButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        showUsageButton.setFocusPainted(false);
        showUsageButton.setBorderPainted(false);

        showLogButton.setBackground(new Color(52, 73, 94));
        showLogButton.setForeground(Color.BLACK);
        showLogButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        showLogButton.setFocusPainted(false);
        showLogButton.setBorderPainted(false);

        // Labels
        Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
        jLabel1.setFont(headerFont);
        jLabel1.setForeground(textColor);
        jLabel2.setFont(headerFont);
        jLabel2.setForeground(textColor);
        jLabel4.setFont(headerFont);
        jLabel4.setForeground(textColor);
        jLabel5.setFont(headerFont);
        jLabel5.setForeground(textColor);
        jLabel6.setFont(headerFont);
        jLabel6.setForeground(textColor);
        jLabel9.setFont(headerFont);
        jLabel9.setForeground(textColor);
        
        Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
        jLabel3.setFont(smallFont);
        jLabel3.setForeground(textColor);
        jLabel7.setFont(smallFont);
        jLabel7.setForeground(textColor);
        jLabel8.setFont(smallFont);
        jLabel8.setForeground(textColor);
        jLabel10.setFont(smallFont);
        jLabel10.setForeground(textColor);
        instructionTime.setFont(smallFont);
        instructionTime.setForeground(textColor);
        quantumLabel.setFont(smallFont);
        quantumLabel.setForeground(textColor);
        
        // Metrics labels
        Font metricsFont = new Font("Segoe UI", Font.BOLD, 11);
        Font metricsValueFont = new Font("Segoe UI", Font.PLAIN, 11);
        jLabel11.setFont(metricsFont);
        jLabel11.setForeground(textColor);
        throughputLabel.setFont(metricsValueFont);
        throughputLabel.setForeground(primaryColor);
        jLabel12.setFont(metricsFont);
        jLabel12.setForeground(textColor);
        cpuUtilizationLabel.setFont(metricsValueFont);
        cpuUtilizationLabel.setForeground(primaryColor);
        jLabel13.setFont(metricsFont);
        jLabel13.setForeground(textColor);
        avgWaitingTimeLabel.setFont(metricsValueFont);
        avgWaitingTimeLabel.setForeground(primaryColor);
        jLabel14.setFont(metricsFont);
        jLabel14.setForeground(textColor);
        avgResponseTimeLabel.setFont(metricsValueFont);
        avgResponseTimeLabel.setForeground(primaryColor);
        jLabel15.setFont(metricsFont);
        jLabel15.setForeground(textColor);
        fairnessLabel.setFont(metricsValueFont);
        fairnessLabel.setForeground(primaryColor);
        
        // Text field
        cycleTextField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cycleTextField.setBackground(Color.WHITE);
        cycleTextField.setForeground(primaryColor);
        cycleTextField.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        // Combo box
        selectDispatcher.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectDispatcher.setBackground(Color.WHITE);
        selectDispatcher.setForeground(textColor);
    }
    
    public synchronized void createNewProcess(List list, String name, String type, int duration, int id){
        ProcessImage newProcess = new ProcessImage(list, type, id, "ready", name, 1, 0, duration);
        readyList.appendLast(newProcess);
        allProcessList.appendLast(newProcess);
        updatePCBs();
    }

    public void updatePCBs(){
        NodoList pAux = readyList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += this.makeString(process);
            pAux = pAux.getpNext();
        }
        this.updateProcess(display);
    }

    private String makeString(ProcessImage currentProcess){
        String display = Dispatcher.makeString(currentProcess);
        return display;
    }

    public void updateCPU(String input){
        this.cpuTextArea.setText(input);
    }

    public synchronized void updateDataset(int instruction, String type){
        w2.updateDataset(instruction, type);
    }
    
    public int getSelectAlgorithm(){
        return this.selectDispatcher.getSelectedIndex();
    }

    public void updateReady(String text){
        this.readyTextArea.setText(text);
    } 

    public void updateBlock(String text){
        this.blockedTextArea.setText(text);
    }

    public void updateExit(String text){
        this.exitTextArea.setText(text);
    }

    public void updateSuspended(String text){
        this.suspendedTextArea.setText(text);
    }
    
    public void updateProcess(String text){
        this.pcbTextArea.setText(text);
    }

    public int getTime(){
        return this.timeSlider.getValue();
    }

    public int getQuantum(){
        return this.quantumSlider.getValue();
    }

    public void setDispatcher(Dispatcher d){
        this.dispatcher = d;
    }

    public void updateCycle(int in){
        this.cycleTextField.setText(in + "");
    }

    public void updateMetrics(MetricsCollector metrics){
        this.throughputLabel.setText(String.format("%.4f p/c", metrics.getThroughput()));
        this.cpuUtilizationLabel.setText(String.format("%.2f%%", metrics.getCpuUtilization()));
        this.avgWaitingTimeLabel.setText(String.format("%.2f c", metrics.getAverageWaitingTime()));
        this.avgResponseTimeLabel.setText(String.format("%.2f c", metrics.getAverageResponseTime()));
        this.fairnessLabel.setText(String.format("%.2f%%", metrics.getFairness()));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cpuTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        readyTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        blockedTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        exitTextArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        suspendedTextArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        pcbTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        throughputLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cpuUtilizationLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        avgWaitingTimeLabel = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        avgResponseTimeLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        fairnessLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        cycleTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        timeSlider = new javax.swing.JSlider();
        instructionTime = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        quantumSlider = new javax.swing.JSlider();
        quantumLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        selectDispatcher = new javax.swing.JComboBox<>();
        showUsageButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        showLogButton = new javax.swing.JButton();
        createProcessButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OS Process Scheduler Simulator");

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        cpuTextArea.setEditable(false);
        cpuTextArea.setColumns(20);
        cpuTextArea.setRows(8);
        jScrollPane2.setViewportView(cpuTextArea);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 130, 120));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        readyTextArea.setEditable(false);
        readyTextArea.setColumns(20);
        readyTextArea.setRows(5);
        jScrollPane1.setViewportView(readyTextArea);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 160, 130));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18));
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, -1, -1));

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        blockedTextArea.setEditable(false);
        blockedTextArea.setColumns(20);
        blockedTextArea.setRows(5);
        jScrollPane3.setViewportView(blockedTextArea);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, 160, 120));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18));
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, -1, -1));

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        exitTextArea.setEditable(false);
        exitTextArea.setColumns(20);
        exitTextArea.setRows(5);
        jScrollPane4.setViewportView(exitTextArea);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 390, 160, 100));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 360, -1, -1));

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        suspendedTextArea.setEditable(false);
        suspendedTextArea.setColumns(20);
        suspendedTextArea.setRows(5);
        jScrollPane6.setViewportView(suspendedTextArea);

        jPanel1.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 130, 260));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18));
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pcbTextArea.setEditable(false);
        pcbTextArea.setColumns(20);
        pcbTextArea.setRows(5);
        jScrollPane5.setViewportView(pcbTextArea);

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 60, 170, 430));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18));
        jLabel5.setText("PCBs");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, -1, -1));

        jLabel11.setText("Throughput:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, -1, -1));

        throughputLabel.setText("0.0000 p/c");
        jPanel1.add(throughputLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 60, 90, -1));

        jLabel12.setText("CPU Util:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 90, -1, -1));

        cpuUtilizationLabel.setText("0.00%");
        jPanel1.add(cpuUtilizationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 90, 90, -1));

        jLabel13.setText("Avg Wait:");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 120, -1, -1));

        avgWaitingTimeLabel.setText("0.00 c");
        jPanel1.add(avgWaitingTimeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 120, 90, -1));

        jLabel14.setText("Avg Resp:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, -1, -1));

        avgResponseTimeLabel.setText("0.00 c");
        jPanel1.add(avgResponseTimeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 150, 90, -1));

        jLabel15.setText("Fairness:");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 180, -1, -1));

        fairnessLabel.setText("100.00%");
        jPanel1.add(fairnessLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 180, 90, -1));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 20, 10, 560));

        jLabel7.setText("Cycle:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 30, -1, -1));

        cycleTextField.setEditable(false);
        cycleTextField.setText("0");
        jPanel1.add(cycleTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 30, 130, -1));

        jLabel3.setText("Instruction Time");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 70, -1, 20));

        timeSlider.setMaximum(5000);
        timeSlider.setMinimum(1);
        timeSlider.setValue(5000);
        timeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeSliderStateChanged(evt);
            }
        });
        jPanel1.add(timeSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 90, 190, -1));

        instructionTime.setText("5000 ms");
        jPanel1.add(instructionTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 110, 90, -1));

        jLabel10.setText("Quantum (RR)");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 140, -1, 20));

        quantumSlider.setMaximum(10);
        quantumSlider.setMinimum(1);
        quantumSlider.setValue(5);
        quantumSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                quantumSliderStateChanged(evt);
            }
        });
        jPanel1.add(quantumSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 160, 190, -1));

        quantumLabel.setText("Quantum: 5");
        jPanel1.add(quantumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 180, 90, -1));

        jLabel8.setText("Scheduling Algorithm");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 220, -1, 20));

        selectDispatcher.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SPN", "SRT", "HRR", "Feedback" }));
        jPanel1.add(selectDispatcher, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 240, 190, -1));

        showUsageButton.setText("Show Usage");
        showUsageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUsageButtonActionPerformed(evt);
            }
        });
        jPanel1.add(showUsageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 410, 190, 35));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 460, 190, 35));

        showLogButton.setText("Show Log");
        showLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLogButtonActionPerformed(evt);
            }
        });
        jPanel1.add(showLogButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 510, 190, 35));

        createProcessButton.setText("Create Process");
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });
        jPanel1.add(createProcessButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 310, 190, 40));

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        jPanel1.add(playButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 360, 190, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
    }

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onPlay.release(1);
        onPlayClock.release();
        ProcessImageCSV.saveProcessesToCSV(readyList, "procesos.csv");
        this.createProcessButton.setEnabled(false);
        this.playButton.setEnabled(false);
    }

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {
        CreateProcess newProcess = new CreateProcess(this);
        newProcess.setVisible(true);
    }

    private void timeSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        this.instructionTime.setText(this.timeSlider.getValue() + " ms");
    }

    private void quantumSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        this.quantumLabel.setText("Quantum: " + this.quantumSlider.getValue());
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedAlgorithm = selectDispatcher.getSelectedIndex();
        int numberOfInstructions = timeSlider.getValue();
        int quantum = quantumSlider.getValue();

        String filePath = "configuracion.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(selectedAlgorithm + "," + numberOfInstructions + "," + quantum);
            writer.newLine();
            
            JOptionPane.showMessageDialog(this, "Configuración guardada en " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la configuración");
        }
    }

    private void showUsageButtonActionPerformed(java.awt.event.ActionEvent evt) {
        w2.setSize(800, 400);
        w2.setLocationRelativeTo(null);
        w2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        w2.setVisible(true);
    }

    private void showLogButtonActionPerformed(java.awt.event.ActionEvent evt) {
        LogViewer logViewer = new LogViewer();
        logViewer.setVisible(true);
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
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                W1 w1 = new W1();
                w1.setVisible(true);
            }
        });
    }

    private javax.swing.JLabel avgResponseTimeLabel;
    private javax.swing.JLabel avgWaitingTimeLabel;
    private javax.swing.JTextArea blockedTextArea;
    private javax.swing.JLabel cpuUtilizationLabel;
    private javax.swing.JTextArea cpuTextArea;
    private javax.swing.JButton createProcessButton;
    private javax.swing.JTextField cycleTextField;
    private javax.swing.JTextArea exitTextArea;
    private javax.swing.JLabel fairnessLabel;
    private javax.swing.JLabel instructionTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea pcbTextArea;
    private javax.swing.JButton playButton;
    private javax.swing.JLabel quantumLabel;
    private javax.swing.JSlider quantumSlider;
    private javax.swing.JTextArea readyTextArea;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<String> selectDispatcher;
    private javax.swing.JButton showLogButton;
    private javax.swing.JButton showUsageButton;
    private javax.swing.JTextArea suspendedTextArea;
    private javax.swing.JLabel throughputLabel;
    private javax.swing.JSlider timeSlider;
}
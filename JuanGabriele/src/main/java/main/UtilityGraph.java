package main;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.SwingUtilities;

import javax.swing.JFrame;
import java.awt.Color;

public class UtilityGraph extends JFrame {
    private DefaultCategoryDataset dataset;

    public UtilityGraph(String title) {
        super(title);
        // Create dataset
        dataset = createDataset();
        // Create chart
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Number of Instructions Executed per PCPU",
                "CPU",
                "Number of Instructions",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        
        // Customize the chart
        CategoryPlot plot = chart.getCategoryPlot();
        StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); // User instructions
        renderer.setSeriesPaint(1, Color.RED); // OS instructions
        plot.setRenderer(renderer);
        
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        dataset.addValue(0, "User", "CPU 1");
        dataset.addValue(0, "OS", "CPU 1");

        dataset.addValue(0, "User", "CPU 2");
        dataset.addValue(0, "OS", "CPU 2");

        dataset.addValue(0, "User", "CPU 3");
        dataset.addValue(0, "OS", "CPU 3");

        dataset.addValue(0, "User", "Total");
        dataset.addValue(0, "OS", "Total");
        
        return dataset;
    }

    public void updateDataset(int cpu, String type, int instructions) {
        SwingUtilities.invokeLater(() -> {
        Number existingValue = dataset.getValue(type, "CPU " + cpu);
        int newValue = existingValue.intValue() + instructions;
        dataset.addValue(newValue, type, "CPU " + cpu);

        // Update the total
        Number existingTotalValue = dataset.getValue(type, "Total");
        int newTotalValue = existingTotalValue.intValue() + instructions;
        dataset.addValue(newTotalValue, type, "Total");
        ((ChartPanel) getContentPane()).repaint();
    });
    }
   

    
}
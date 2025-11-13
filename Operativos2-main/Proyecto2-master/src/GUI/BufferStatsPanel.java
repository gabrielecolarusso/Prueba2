/*
 * Panel para mostrar estadísticas gráficas del Buffer Cache
 */
package GUI;

import SISTEMA.BufferCache;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;

public class BufferStatsPanel extends JPanel {
    private BufferCache buffer;
    private ChartPanel hitMissChartPanel;
    private ChartPanel hitRateChartPanel;
    private ChartPanel capacityChartPanel;

    public BufferStatsPanel(BufferCache buffer) {
        this.buffer = buffer;
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inicializarGraficos();
    }

    private void inicializarGraficos() {
        // Gráfico de Hits vs Misses
        hitMissChartPanel = crearGraficoHitsMisses();
        hitMissChartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Gráfico de Hit Rate (Pastel)
        hitRateChartPanel = crearGraficoHitRate();
        hitRateChartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Gráfico de Capacidad del Buffer
        capacityChartPanel = crearGraficoCapacidad();
        capacityChartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Panel de información textual
        JPanel infoPanel = crearPanelInfo();
        
        add(hitMissChartPanel);
        add(hitRateChartPanel);
        add(capacityChartPanel);
        add(infoPanel);
    }

    private ChartPanel crearGraficoHitsMisses() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(buffer.getHits(), "Accesos", "Hits");
        dataset.addValue(buffer.getMisses(), "Accesos", "Misses");

        JFreeChart chart = ChartFactory.createBarChart(
                "📊 Hits vs Misses",
                "Tipo de Acceso",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        
        // 🎨 Configurar colores personalizados por categoría
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        
        // Crear un renderer personalizado para colorear por categoría
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                if (column == 0) {
                    return new Color(220, 53, 69); 
                } else {
                    return new Color(0, 123, 255);  
                }
            }
        };
        
        plot.setRenderer(renderer);
        
        return new ChartPanel(chart);
    }

    private ChartPanel crearGraficoHitRate() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int hits = buffer.getHits();
        int misses = buffer.getMisses();
        int total = hits + misses;
        
        if (total > 0) {
            dataset.setValue("Hits (" + String.format("%.1f%%", buffer.getHitRate()) + ")", hits);
            dataset.setValue("Misses (" + String.format("%.1f%%", 100 - buffer.getHitRate()) + ")", misses);
        } else {
            dataset.setValue("Sin datos", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "🎯 Tasa de Aciertos",
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        
        return new ChartPanel(chart);
    }

    private ChartPanel crearGraficoCapacidad() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(buffer.getSize(), "Bloques", "En Uso");
        dataset.addValue(buffer.getCapacity() - buffer.getSize(), "Bloques", "Libre");

        JFreeChart chart = ChartFactory.createBarChart(
                "💾 Uso del Buffer",
                "Estado",
                "Bloques",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        
        // 🎨 Configurar colores personalizados por categoría
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        
        // Crear un renderer personalizado para colorear por categoría
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                if (column == 0) {
                    return new Color(220, 53, 69); 
                } else {
                    return new Color(0, 123, 255);  
                }
            }
        };
        
        plot.setRenderer(renderer);
        
        return new ChartPanel(chart);
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📋 Información Detallada"));
        panel.setBackground(new Color(250, 250, 250));
        
        JLabel lblPolicy = new JLabel("Política: " + buffer.getPolicy());
        JLabel lblCapacity = new JLabel("Capacidad: " + buffer.getCapacity() + " bloques");
        JLabel lblUsed = new JLabel("En uso: " + buffer.getSize() + " bloques");
        JLabel lblHits = new JLabel("Hits totales: " + buffer.getHits());
        JLabel lblMisses = new JLabel("Misses totales: " + buffer.getMisses());
        JLabel lblHitRate = new JLabel(String.format("Hit Rate: %.2f%%", buffer.getHitRate()));
        
        Font font = new Font("Arial", Font.PLAIN, 13);
        lblPolicy.setFont(font);
        lblCapacity.setFont(font);
        lblUsed.setFont(font);
        lblHits.setFont(font);
        lblMisses.setFont(font);
        lblHitRate.setFont(font);
        
        panel.add(lblPolicy);
        panel.add(lblCapacity);
        panel.add(lblUsed);
        panel.add(lblHits);
        panel.add(lblMisses);
        panel.add(lblHitRate);
        
        return panel;
    }

    public void actualizarGraficos() {
        removeAll();
        inicializarGraficos();
        revalidate();
        repaint();
    }
}
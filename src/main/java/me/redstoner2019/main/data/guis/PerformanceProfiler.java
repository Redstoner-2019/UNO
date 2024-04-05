package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.Main;

import com.sun.management.OperatingSystemMXBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.redstoner2019.main.serverstuff.ServerMain;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;

import org.jfree.data.time.TimeSeries;

import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PerformanceProfiler {
    public static JFrame frame;
    private final int width = 1280;
    private final int height = 940;
    private JFreeChart memoryChart;
    private JFreeChart cpuChart;
    private JFreeChart networkChart;
    private OperatingSystemMXBean osBean;
    private MBeanServer mBeanServer;
    private ObjectName networkInterfaceName;
    private ScheduledExecutorService scheduler;
    private XYSeries memorySeries = new XYSeries("Memory Usage");
    private XYSeries cpuSeries = new XYSeries("CPU Usage");
    private XYSeries networkSeries = new XYSeries("Network Usage");
    private NumberAxis yAxisNetwork;

    public PerformanceProfiler() throws Exception {
        initialize();
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        networkInterfaceName = new ObjectName("java.nio:type=BufferPool,name=direct");
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    private void initialize() throws Exception {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("Performance Profiler");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);

        //frame.setLocation(frame.getX()+(2*1920),frame.getY());

        JPanel panel = new ChartPanel(memoryChart);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        XYAreaRenderer cpuRenderer = new XYAreaRenderer();
        cpuRenderer.setSeriesPaint(0, new Color(255,128,0,128));
        cpuRenderer.setDefaultOutlineStroke(new BasicStroke(3));
        cpuRenderer.setDefaultOutlinePaint(new Color(255,128,0,255));
        cpuRenderer.setOutline(true);

        XYAreaRenderer networkRenderer = new XYAreaRenderer();
        networkRenderer.setSeriesPaint(0, new Color(255,0,0,128));
        networkRenderer.setDefaultOutlineStroke(new BasicStroke(3));
        networkRenderer.setDefaultOutlinePaint(new Color(255,0,0,255));
        networkRenderer.setOutline(true);

        XYAreaRenderer memoryRenderer = new XYAreaRenderer();
        memoryRenderer.setSeriesPaint(0, new Color(0,255,0,128));
        memoryRenderer.setDefaultOutlineStroke(new BasicStroke(3));
        memoryRenderer.setDefaultOutlinePaint(new Color(0,255,0,255));
        memoryRenderer.setOutline(true);

        /**
         * CPU
         */

        NumberAxis yAxisCpu = new NumberAxis("yaxis");
        yAxisCpu.setRange(0.0, 100);
        yAxisCpu.setLabel("CPU usage (%)");
        NumberAxis xAxisCpu = new NumberAxis("xaxis");
        xAxisCpu.setRange(-120.0, 0);
        xAxisCpu.setLabel("Time (S)");

        cpuSeries = new XYSeries("CPU Usage");

        XYSeriesCollection cpuDataset = new XYSeriesCollection();
        cpuDataset.addSeries(cpuSeries);

        XYPlot xyplotCpu = new XYPlot(cpuDataset, xAxisCpu, yAxisCpu, cpuRenderer);
        cpuChart = new JFreeChart("CPU Usage", null, xyplotCpu, true);

        JPanel cpuPanel = new ChartPanel(cpuChart);
        cpuPanel.setBounds(0,300,width-20,300);
        panel.add(cpuPanel);

        /**
         * Network
         */

        yAxisNetwork = new NumberAxis("yaxis");
        yAxisNetwork.setRange(0.0, 1.0);
        yAxisNetwork.setLabel("Usage (Packets/s)");
        NumberAxis xAxisNetwork = new NumberAxis("xaxis");
        xAxisNetwork.setRange(-120.0, 0);
        xAxisNetwork.setLabel("Time (S)");

        networkSeries = new XYSeries("Network Usage");

        XYSeriesCollection networkDataset = new XYSeriesCollection();
        networkDataset.addSeries(networkSeries);

        XYPlot xyplotNetwork = new XYPlot(networkDataset, xAxisNetwork, yAxisNetwork, networkRenderer);
        networkChart = new JFreeChart("Network Usage", null, xyplotNetwork, true);

        JPanel networkPanel = new ChartPanel(networkChart);
        networkPanel.setBounds(0,600,width-20,300);
        panel.add(networkPanel);

        /**
         * Memory
         */

        NumberAxis yAxisMemory = new NumberAxis("yaxis");
        yAxisMemory.setRange(0.0, bytesToMB(Runtime.getRuntime().totalMemory()));
        yAxisMemory.setLabel("Usage (MB)");
        NumberAxis xAxisMemory = new NumberAxis("xaxis");
        xAxisMemory.setRange(-120.0, 0);
        xAxisMemory.setLabel("Time (S)");

        memorySeries = new XYSeries("Memory Usage");

        XYSeriesCollection memoryDataset = new XYSeriesCollection();
        memoryDataset.addSeries(memorySeries);

        XYPlot xyplot = new XYPlot(memoryDataset, xAxisMemory, yAxisMemory, memoryRenderer);
        memoryChart = new JFreeChart("Memory Usage", null, xyplot, true);

        JPanel memoryPanel = new ChartPanel(memoryChart);
        memoryPanel.setBounds(0,0,width-20,300);
        panel.add(memoryPanel);


        frame.setVisible(true);
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Get memory usage
                long freeMemory = Runtime.getRuntime().freeMemory();
                long totalMemory = Runtime.getRuntime().totalMemory();
                long usedMemory = totalMemory - freeMemory;

                if(bytesToMB(usedMemory) > 50) {
                    System.gc();
                    freeMemory = Runtime.getRuntime().freeMemory();
                    totalMemory = Runtime.getRuntime().totalMemory();
                    usedMemory = totalMemory - freeMemory;
                }

                // Get CPU usage
                double cpuUsage = osBean.getProcessCpuLoad();

                // Get network usage
                long networkUsage = (Long) mBeanServer.getAttribute(networkInterfaceName, "MemoryUsed");

                // Update GUI with new values
                updateGUI(usedMemory, cpuUsage, networkUsage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }
    public void updateGUI(long usedMemory, double cpuUsage, long networkUsage) {
        for (int i = 0; i < memorySeries.getItemCount(); i++) {
            XYDataItem item = memorySeries.getDataItem(i);
            double x = item.getXValue();
            double y = item.getYValue();
            memorySeries.remove(i);
            if(x-1>=-120) memorySeries.add(x-1,y);
            //if(x-1>=-120)networkSeries.add(x-1,y);
        }

        for (int i = 0; i < networkSeries.getItemCount(); i++) {
            XYDataItem item = networkSeries.getDataItem(i);
            double x = item.getXValue();
            double y = item.getYValue();
            networkSeries.remove(i);
            if(x-1>=-120) networkSeries.add(x-1,y);
            //if(x-1>=-120)networkSeries.add(x-1,y);
        }

        for (int i = 0; i < cpuSeries.getItemCount(); i++) {
            XYDataItem item = cpuSeries.getDataItem(i);
            double x = item.getXValue();
            double y = item.getYValue();
            cpuSeries.remove(i);
            if(x-1>=-120) cpuSeries.add(x-1,y);
            //if(x-1>=-120)cpuSeries.add(x-1,y);
        }

        memorySeries.add(0, bytesToMB(usedMemory));
        cpuSeries.add(0, cpuUsage*100);
        networkSeries.add(0, ServerMain.packetsrecieved+ServerMain.packetsSent);

        yAxisNetwork.setRange(0.0,Math.max(networkSeries.getMaxY(),10));

        memoryChart.setTitle("Memory Usage " + bytesToMB(usedMemory) + "MB");
        cpuChart.setTitle("Cpu Usage " + String.format("%.2f",cpuUsage*100) + "%");
        networkChart.setTitle("Network Usage " + bytesToKB(networkUsage) + "KB/s (Sending:" + ServerMain.packetsSent + " packets/s, Recieving " + ServerMain.packetsrecieved + " packets/s)");
        ServerMain.packetsSent = 0;
        ServerMain.packetsrecieved = 0;
    }

    public static long bytesToMB(long memory){
        return memory / 1024 / 1024;
    }
    public static long bytesToKB(long memory){
        return memory / 1024;
    }
}
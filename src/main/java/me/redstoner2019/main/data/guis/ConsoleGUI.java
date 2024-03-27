package me.redstoner2019.main.data.guis;

import javax.swing.*;
import java.awt.*;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class ConsoleGUI<d> {
    private JFrame frame;
    private final int width = 700;
    private final int height = 700;
    public static JTextArea area = new JTextArea();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConsoleGUI window = new ConsoleGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ConsoleGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("ConsoleGUI");

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JScrollPane sp = new JScrollPane(area);
        sp.setBounds(0,0,width,height);
        area.setEditable(false);
        panel.add(sp);

        area.setBackground(Color.DARK_GRAY);
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Consolas",Font.PLAIN,15));

        sp.setAutoscrolls(true);
        frame.setVisible(true);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (frame.isVisible()){
                    double threadCpuTime = 0;

                    ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
                    //threadCpuTime = threadMXBean.getThreadCpuTime(Thread.currentThread().getId());


                    OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

                    double systemCpuTime = operatingSystemMXBean.getSystemCpuLoad()*1000000000000d;

                    for(long l : threadMXBean.getAllThreadIds()) threadCpuTime+=threadMXBean.getThreadCpuTime(l);

                    Runtime runtime = Runtime.getRuntime();

                    double cpuUsage = threadCpuTime / systemCpuTime * 100.0;

                    frame.setTitle("CPU " + String.format("%.2f",cpuUsage) + "%    RAM " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + "MB used");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();
    }
}

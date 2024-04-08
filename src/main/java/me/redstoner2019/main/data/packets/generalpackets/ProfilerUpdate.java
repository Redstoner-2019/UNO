package me.redstoner2019.main.data.packets.generalpackets;

import me.redstoner2019.serverhandling.Packet;

public class ProfilerUpdate extends Packet {
    private int memoryUsage = 0;
    private double cpuUsage = 0;

    public ProfilerUpdate() {
    }

    @Override
    public String toString() {
        return "ProfilerUpdate{" +
                "memoryUsage=" + memoryUsage +
                ", cpuUsage=" + cpuUsage +
                '}';
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public ProfilerUpdate(int memoryUsage, double cpuUsage) {
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
    }
}

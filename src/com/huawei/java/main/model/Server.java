package com.huawei.java.main.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 服务器模型
 *
 * @author Kim小根
 * @date 2021/3/11 10:13
 * <p>Description:实现服务器主要功能</p>
 */
public class Server implements Comparable<Server> {

    /**
     * 表示A节点
     */
    private static final String NODE_A = "A";


    /**
     * 自动编号
     */
    private static Integer auto_no = 0;

    /**
     * 服务器编号
     */
    private int no;

    /**
     * 服务器型号
     */
    private String type;

    /**
     * 服务器节点A
     */
    private Node node_A;

    /**
     * 服务器节点B
     */
    private Node node_B;

    /**
     * 服务器硬件成本（一次性购入成本）
     */
    private int cost_of_devices;

    /**
     * 服务器每日能耗成本
     */
    private int cost_of_energy;

    /**
     * 当前服务器上虚拟机集合
     */
    private Set<Virtual> virtualSet;

    /**
     * 利用率
     */
    private double usedRate;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node getNode_A() {
        return node_A;
    }

    public void setNode_A(Node node_A) {
        this.node_A = node_A;
    }

    public Node getNode_B() {
        return node_B;
    }

    public void setNode_B(Node node_B) {
        this.node_B = node_B;
    }

    public int getCost_of_devices() {
        return cost_of_devices;
    }

    public void setCost_of_devices(int cost_of_devices) {
        this.cost_of_devices = cost_of_devices;
    }

    public int getCost_of_energy() {
        return cost_of_energy;
    }

    public void setCost_of_energy(int cost_of_energy) {
        this.cost_of_energy = cost_of_energy;
    }

    public Set<Virtual> getVirtualSet() {
        return virtualSet;
    }

    public void setVirtualSet(Set<Virtual> virtualSet) {
        this.virtualSet = virtualSet;
    }

    public double getUsedRate() {
        return usedRate;
    }

    public void setUsedRate(double usedRate) {
        this.usedRate = usedRate;
    }

    /**
     * 服务器（概念）构造函数
     *
     * @param type            服务器型号
     * @param cores           服务器拥有的CPU核数
     * @param memorize        服务器拥有的内存大小
     * @param cost_of_devices 服务器硬件成本（一次性购入成本）
     * @param cost_of_energy  服务器每日能耗成本
     */
    public Server(String type, int cores, int memorize, int cost_of_devices, int cost_of_energy) {
        int half_cores = cores / 2, half_memorize = memorize / 2;
        this.type = type;
        this.node_A = new Node(half_cores, half_memorize);
        this.node_B = new Node(half_cores, half_memorize);
        this.cost_of_devices = cost_of_devices;
        this.cost_of_energy = cost_of_energy;
        this.virtualSet = new HashSet<>();
        this.node_A.setServer(this);
        this.node_B.setServer(this);
        this.usedRate = 0;
    }

    public Server() {
    }

    /**
     * 生产一个服务器
     *
     * @param auto 是否自动编号，若是，则为true；若不是，则为false
     */
    public Server productServer(boolean auto) {
        if (auto) {
            setNo(auto_no++);
            return this;
        } else {
            Server s = new Server();
            s.setType(type);
            s.setNode_A(new Node(node_A));
            s.setNode_B(new Node(node_B));
            s.setCost_of_devices(cost_of_devices);
            s.setCost_of_energy(cost_of_energy);
            s.setVirtualSet(new HashSet<>(virtualSet));
            s.getNode_A().setServer(s);
            s.getNode_B().setServer(s);
            s.setUsedRate(usedRate);
            return s;
        }
    }

    public int getCores() {
        return this.node_A.getCores() + this.node_B.getCores();
    }

    public int getMemorize() {
        return this.node_A.getMemorize() + this.node_B.getMemorize();
    }

    public int getCoresUsed() {
        return this.node_A.getCores_used() + this.node_B.getCores_used();
    }

    public int getMemorizeUsed() {
        return this.node_A.getMemorize_used() + this.node_B.getMemorize_used();
    }

    /**
     * 添加虚拟机
     *
     * @param virtual 虚拟机
     * @param node    当放入为单节点时，传入A or B；当放入为双节点时，传入null
     */
    public void add(Virtual virtual, String node) {
        virtual.setServer(this);
        this.virtualSet.add(virtual);
        if (virtual.isDoubleNodes()) {
            int cores = virtual.getCores() / 2, mem = virtual.getMemorize() / 2;
            this.node_A.allocate(cores, mem, null);
            this.node_B.allocate(cores, mem, null);
        } else {
            if (NODE_A.equals(node)) this.node_A.allocate(virtual.getCores(), virtual.getMemorize(), virtual.getId());
            else this.node_B.allocate(virtual.getCores(), virtual.getMemorize(), virtual.getId());
            virtual.setNode(node);
        }
        updateUtilizationRate();
    }

    /**
     * 删除虚拟机
     *
     * @param virtual 虚拟机
     */
    public void remove(Virtual virtual) {
        virtual.setServer(null);
        virtual.setNode(null);
        this.virtualSet.remove(virtual);
        if (virtual.isDoubleNodes()) {
            int cores = -virtual.getCores() / 2, mem = -virtual.getMemorize() / 2;
            this.node_A.allocate(cores, mem, null);
            this.node_B.allocate(cores, mem, null);
        } else {
            Integer id = virtual.getId();
            if (this.node_A.contain(id)) this.node_A.allocate(-virtual.getCores(), -virtual.getMemorize(), id);
            else this.node_B.allocate(-virtual.getCores(), -virtual.getMemorize(), id);
        }
        updateUtilizationRate();
    }

    /**
     * 删除虚拟机(NoSet)
     *
     * @param virtual 虚拟机
     */
    public void removeNoSet(Virtual virtual) {
        virtual.setServer(null);
        if (virtual.isDoubleNodes()) {
            int cores = -virtual.getCores() / 2, mem = -virtual.getMemorize() / 2;
            this.node_A.allocate(cores, mem, null);
            this.node_B.allocate(cores, mem, null);
        } else {
            Integer id = virtual.getId();
            if (this.node_A.contain(id)) this.node_A.allocate(-virtual.getCores(), -virtual.getMemorize(), id);
            else this.node_B.allocate(-virtual.getCores(), -virtual.getMemorize(), id);
        }
        updateUtilizationRate();
    }

    /**
     * 计算当前服务器的成本
     *
     * @return 成本值
     */
    public double cost() {
        int used_cores = getCoresUsed(), used_mem = getMemorizeUsed();
        //int total_cores = used_cores + getCores(), total_mem = used_mem + getMemorize();
        //double cost = ((double)used_cores / total_cores + (double)used_mem / total_mem) / 2 * cost_of_devices;
        //double cost = (double) (cost_of_devices + 800 * cost_of_energy) / (used_cores + used_mem);   //605208782
        double cost = (double) (cost_of_devices) / (used_cores + used_mem);    //605169014
        //double cost = 1/(((double)used_cores / total_cores + (double)used_mem / total_mem) / 2);    //1654709526
        //double cost = (double)(cost_of_devices) / (total_cores + total_mem);  //1398922945
        return cost;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(no);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Server) {
            Server o = (Server) obj;
            return o.getNo() == this.no;
        }
        return false;
    }

    /**
     * 更新利用率
     */
    public void updateUtilizationRate() {
        int used_cores = getCoresUsed(), used_mem = getMemorizeUsed();
        int total_cores = used_cores + getCores(), total_mem = used_mem + getMemorize();
        this.usedRate = ((double) used_cores / total_cores + (double) used_mem / total_mem) / 2;
    }

    @Override
    public int compareTo(Server o) {
        if (o.getCores() != getCores()) return getCores() - o.getCores();
        return getMemorize() - o.getMemorize();
    }

//    @Override
//    public String toString() {
//        //return "A:" + node_A.toString() + " B:" + node_B.toString();
//        String s = "";
//        s += "\t\tA节点\t\tB节点\n";
//        s += "占用\t" + node_A.getCores_used() + "/" + node_A.getMemorize_used() + "\t" + node_B.getCores_used() + "/" + node_B.getMemorize_used() + "\n";
//        s += "总量\t" + (node_A.getCores() + node_A.getCores_used()) + "/" + (node_A.getMemorize() + node_A.getMemorize_used()) + "\t" + (node_B.getCores() + node_B.getCores_used()) + "/" + (node_B.getMemorize() + node_B.getMemorize_used()) + "\n";
//        return s;
//    }
}

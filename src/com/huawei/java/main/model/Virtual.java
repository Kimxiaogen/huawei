package com.huawei.java.main.model;

import java.util.Objects;

/**
 * 虚拟机模型
 *
 * @author Kim小根
 * @date 2021/3/11 10:19
 * <p>Description:实现虚拟机的主要功能</p>
 */
public class Virtual implements Comparable<Virtual> {

    /**
     * 虚拟机ID
     */
    private int id;

    /**
     * 虚拟机型号
     */
    private String type;

    /**
     * 虚拟机拥有的CPU核数
     */
    private int cores;

    /**
     * 虚拟机拥有的内存大小
     */
    private int memorize;

    /**
     * 是否为双节点部署，true表示双节点部署，false表示单节点部署
     */
    private boolean isDoubleNodes;

    /**
     * 虚拟机当前所在服务器
     */
    private Server server;

    /**
     * 虚拟机部署于服务器哪个节点（单节点属性）（允许参数 “A” or “B”）
     */
    private String node;

    /**
     * 是否需要销毁该节点，当虚拟机已经部署后，该值为true，并销毁所有绑定该虚拟机的节点
     */
    private boolean destory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getMemorize() {
        return memorize;
    }

    public void setMemorize(int memorize) {
        this.memorize = memorize;
    }

    public boolean isDoubleNodes() {
        return isDoubleNodes;
    }

    public void setDoubleNodes(boolean doubleNodes) {
        isDoubleNodes = doubleNodes;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public boolean isDestory() {
        return destory;
    }

    public void setDestory(boolean destory) {
        this.destory = destory;
    }

    /**
     * 虚拟机构造函数
     *
     * @param type          虚拟机型号
     * @param cores         虚拟机拥有的CPU核数
     * @param memorize      虚拟机拥有的内存大小
     * @param isDoubleNodes 是否为双节点部署，1（true）表示双节点部署，2（false）表示单节点部署
     */
    public Virtual(String type, int cores, int memorize, int isDoubleNodes) {
        this.type = type;
        this.cores = cores;
        this.memorize = memorize;
        this.isDoubleNodes = isDoubleNodes == 1;
        this.destory = false;
    }

    @Override
    public Virtual clone() {
        Virtual v = new Virtual(type, cores, memorize, isDoubleNodes ? 1 : 0);
        v.setNode(node);
        return v;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Virtual) {
            Virtual o = (Virtual) obj;
            return o.getId() == this.id;
        }
        return false;
    }

    @Override
    public int compareTo(Virtual o) {
        if (o.getCores() != cores) return cores - o.getCores();
        return memorize - o.getMemorize();
    }
}

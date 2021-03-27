package com.huawei.java.main.model;

/**
 * 单链表节点，用来绑定虚拟机
 * @author Kim小根
 * @date 2021/3/17 21:39
 * <p>Description:</p>
 */
public class LinkedNode {

    /**
     * 绑定的虚拟机
     */
    private Virtual virtual;

    /**
     * 下一个节点
     */
    private LinkedNode next;



    public Virtual getVirtual() {
        return virtual;
    }

    public void setVirtual(Virtual virtual) {
        this.virtual = virtual;
    }

    public LinkedNode getNext() {
        return next;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    /**
     * 构造单链表节点
     * @param virtual 虚拟机
     */
    public LinkedNode(Virtual virtual) {
        this.virtual = virtual;
    }
}

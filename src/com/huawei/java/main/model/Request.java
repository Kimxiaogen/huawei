package com.huawei.java.main.model;

/**
 * 虚拟机请求模型
 * @author Kim小根
 * @date 2021/3/11 10:37
 * <p>Description: 实现虚拟机请求的主要功能</p>
 */
public class Request{

    /**
     * 请求类型
     */
    private String request_type;

    /**
     * 请求的虚拟机类型
     */
    private String virtual_type;

    /**
     * 请求的虚拟机ID
     */
    private int virtual_id;

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getVirtual_type() {
        return virtual_type;
    }

    public void setVirtual_type(String virtual_type) {
        this.virtual_type = virtual_type;
    }

    public int getVirtual_id() {
        return virtual_id;
    }

    public void setVirtual_id(int virtual_id) {
        this.virtual_id = virtual_id;
    }

    /**
     * 虚拟机请求构造函数
     * @param request_type 请求类型，add表示增加一个虚拟机，del表示删除一个虚拟机
     * @param virtual_type 虚拟机型号（删除操作传入null）
     * @param virtual_id 虚拟机ID
     */
    public Request(String request_type, String virtual_type, int virtual_id) {
        this.request_type = request_type;
        this.virtual_type = virtual_type;
        this.virtual_id = virtual_id;
    }
}

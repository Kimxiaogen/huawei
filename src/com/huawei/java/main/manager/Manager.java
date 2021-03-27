package com.huawei.java.main.manager;

import com.huawei.java.main.model.Request;
import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.util.List;
import java.util.Map;

/**
 * 调度管理接口
 *
 * @author Kim小根
 * @date 2021/3/11 13:39
 * <p>Description:定义调度的主要功能</p>
 */
public interface Manager {

    /**
     * 生成解决方案，并输出到控制台
     */
    void OutputSolution();

    /**
     * 获取可购买服务器集合
     *
     * @return
     */
    Server[] getAvailableServers();

    /**
     * 获取当前拥有服务器集合
     *
     * @return
     */
    List<Server> getCurrentServers();

    /**
     * 获取可获得的虚拟机集合
     *
     * @return
     */
    Map<String, Virtual> getAvailableVirtuals();

    /**
     * 获取当前已有的虚拟机集合
     *
     * @return
     */
    Map<Integer, Virtual> getCurrentVirtuals();

    /**
     * 获取全部请求序列集合
     *
     * @return
     */
    Request[][] getRequests();

}

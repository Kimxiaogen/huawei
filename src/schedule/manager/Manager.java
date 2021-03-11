package schedule.manager;

import schedule.model.Request;
import schedule.model.Server;
import schedule.model.Virtual;

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
     * 数据中心扩容
     */
    void DataCenterExpansion();

    /**
     * 虚拟机迁移
     */
    void VirtualMachineMigration();

    /**
     * 部署虚拟机
     */
    void Deploy();

    /**
     * 输出解决方案
     */
    void OutputSolution();

    /**
     * 获取可购买服务器集合
     * @return
     */
    List<Server> getAvailableServers();

    /**
     * 获取当前拥有服务器集合
     * @return
     */
    List<Server> getCurrentServers();

    /**
     * 获取虚拟机集合
     * @return
     */
    Map<String, Virtual> getAvailableVirtual();

    /**
     * 获取全部请求序列集合
     * @return
     */
    Request[][] getRequests();

}

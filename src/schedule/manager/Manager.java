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
     * 模拟部署，返回需要扩容的服务器
     * @param cost 花费二维数组，一维数组表示服务器（包含已有服务器），二维数组表示待添加虚拟机（可能会有相同类型虚拟机）
     * @param servers 一维数组中服务器
     * @param virtuals 二维数组中虚拟机
     * @return 需要扩容的服务器
     */
    List<Server> tryDeploy(float[][] cost, Server[] servers, Virtual[] virtuals);

    /**
     * 部署虚拟机
     */
    void Deploy();

    /**
     * 生成解决方案，并输出到控制台
     */
    void OutputSolution();

    /**
     * 获取可购买服务器集合
     * @return
     */
    Server[] getAvailableServers();

    /**
     * 获取当前拥有服务器集合
     * @return
     */
    List<Server> getCurrentServers();

    /**
     * 获取虚拟机集合
     * @return
     */
    Virtual[] getAvailableVirtual();

    /**
     * 获取全部请求序列集合
     * @return
     */
    Request[][] getRequests();

}

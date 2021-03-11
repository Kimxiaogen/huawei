package schedule.manager.impl;

import schedule.manager.Manager;
import schedule.model.Request;
import schedule.model.Server;
import schedule.model.Virtual;

import java.util.List;
import java.util.Map;

/**
 * 调度管理实现类
 *
 * @author Kim小根
 * @date 2021/3/11 13:43
 * <p>Description:实现接口方法</p>
 */
public class ManagerImpl implements Manager {
    /**
     * 可购买的服务器类型
     */
    private List<Server> availableServers;

    /**
     * 当前已拥有的服务器类型
     */
    private List<Server> currentServers;

    /**
     * 所有虚拟机类型
     */
    private Map<String, Virtual> availableVirtual;

    /**
     * 所有虚拟机请求，一维数组表示每一天的请求序列，二维数组表示当天的请求序列
     */
    private Request[][] requests;

    public List<Server> getAvailableServers() {
        return availableServers;
    }

    public void setAvailableServers(List<Server> availableServers) {
        this.availableServers = availableServers;
    }

    public List<Server> getCurrentServers() {
        return currentServers;
    }

    public void setCurrentServers(List<Server> currentServers) {
        this.currentServers = currentServers;
    }

    public Map<String, Virtual> getAvailableVirtual() {
        return availableVirtual;
    }

    public void setAvailableVirtual(Map<String, Virtual> availableVirtual) {
        this.availableVirtual = availableVirtual;
    }

    public Request[][] getRequests() {
        return requests;
    }

    public void setRequests(Request[][] requests) {
        this.requests = requests;
    }

    public ManagerImpl(List<Server> availableServers, List<Server> currentServers, Map<String, Virtual> availableVirtual, Request[][] requests) {
        this.availableServers = availableServers;
        this.currentServers = currentServers;
        this.availableVirtual = availableVirtual;
        this.requests = requests;
    }

    @Override
    public void DataCenterExpansion() {

    }

    @Override
    public void VirtualMachineMigration() {

    }

    @Override
    public void Deploy() {

    }

    @Override
    public void OutputSolution() {

    }
}

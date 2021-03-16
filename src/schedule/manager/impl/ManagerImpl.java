package schedule.manager.impl;

import schedule.manager.Manager;
import schedule.model.Node;
import schedule.model.Request;
import schedule.model.Server;
import schedule.model.Virtual;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Server[] availableServers;

    /**
     * 当前已拥有的服务器类型
     */
    private List<Server> currentServers;

    /**
     * 所有虚拟机类型
     */
    private Virtual[] availableVirtual;

    /**
     * 所有虚拟机请求，一维数组表示每一天的请求序列，二维数组表示当天的请求序列
     */
    private Request[][] requests;

    /**
     * 虚拟机与服务器对应成本，一维数组表示不同服务器，二维数组表示不同虚拟机
     */
    private float[][] cost;
    /**
     * 辅助map，用于寻找指定型号虚拟机对应cost数组中二维下标
     */
    private Map<String, Integer> type_to_index;

    @Override
    public Server[] getAvailableServers() {
        return availableServers;
    }

    public void setAvailableServers(Server[] availableServers) {
        this.availableServers = availableServers;
    }

    @Override
    public List<Server> getCurrentServers() {
        return currentServers;
    }

    public void setCurrentServers(List<Server> currentServers) {
        this.currentServers = currentServers;
    }

    @Override
    public Virtual[] getAvailableVirtual() {
        return availableVirtual;
    }

    public void setAvailableVirtual(Virtual[] availableVirtual) {
        this.availableVirtual = availableVirtual;
    }

    @Override
    public Request[][] getRequests() {
        return requests;
    }

    public void setRequests(Request[][] requests) {
        this.requests = requests;
    }

    public ManagerImpl(Server[] availableServers, List<Server> currentServers, Virtual[] availableVirtual, Request[][] requests) {
        this.availableServers = availableServers;
        this.currentServers = currentServers;
        this.availableVirtual = availableVirtual;
        this.requests = requests;
        initialCost();
        printCost();
    }

    /**
     * 打印cost数组
     */
    private void printCost() {
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[i].length; j++) {
                System.out.print(cost[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 初始化cost二维数组
     */
    private void initialCost() {
        int n_servers = availableServers.length, n_virtual = availableVirtual.length, days = requests.length;
        float[] cost_of_servers = new float[n_servers];    //服务器每日花费 / (总CPU+内存)
        this.cost = new float[n_servers][n_virtual];
        this.type_to_index = new HashMap<>();
        for (int i = 0; i < n_servers; i++) {    //服务器每日花费（估计） = 每日能耗费用 + 购入费用 / 使用天数
            Server server = availableServers[i];
            cost_of_servers[i] = server.getCost_of_energy() + (float) server.getCost_of_devices() / days;
            cost_of_servers[i] /= (server.getCores() + server.getMemorize());
        }
        for (int j = 0; j < n_virtual; j++) {
            Virtual virtual = availableVirtual[j];
            for (int i = 0; i < n_servers; i++) {
                this.cost[i][j] = cost_of_servers[i] * (virtual.getCores() + virtual.getMemorize());
            }
        }
    }

    @Override
    public void DataCenterExpansion() {

    }

    @Override
    public void VirtualMachineMigration() {

    }

    @Override
    public List<Server> tryDeploy(float[][] cost, Server[] servers, Virtual[] virtuals) {
        List<Server> buyList = new ArrayList<>();
        int n_server = cost.length, n_virtual = cost[0].length;
        float[] min_cost = new float[n_virtual];  //记录每个虚拟机的最小花费值
        int[] min_cost_index = new int[n_virtual];  //记录每个虚拟机最小花费对应的服务器下标
        int[][] match = new int[n_server][n_virtual];    //记录当前匹配情况
        int i = 0;
        for (int j = 0; j < n_virtual; j++) {   //初始化最小花费数组和最小花费下标数组
            min_cost[j] = Float.MAX_VALUE;
            for (int k = 0; k < n_server; k++) {
                if (min_cost[j] > cost[k][j]) {
                    min_cost[j] = cost[k][j];
                    min_cost_index[j] = k;
                }
            }
        }
        do {

        } while (i++ < n_virtual);
        return buyList;
    }

    /**
     * 当前部署是否会产生冲突
     *
     * @param server  待匹配服务器
     * @param virtual 待匹配虚拟机
     * @return 若冲突，返回true；若不冲突，进行分配，并返回false
     */
    private boolean ifConflict(Server server, Virtual virtual) {
        Node a = server.getNode_A(), b = server.getNode_B();
        int cores = virtual.getCores(), mem = virtual.getMemorize();
        if (virtual.isDoubleNodes()) {
            int div_cores = cores / 2, div_mem = mem / 2;
            boolean enough_cpu = a.getCores() >= div_cores && b.getCores() >= div_cores;
            boolean enough_mem = a.getMemorize() >= div_mem && b.getMemorize() >= div_mem;
            if(enough_cpu && enough_mem){    //进行分配
                a.allocate(div_cores, div_mem);
                b.allocate(div_cores, div_mem);
                return false;
            }
            return true;
        } else {
            boolean can_A = a.getCores() >= cores && a.getMemorize() >= mem;
            if(can_A){
                return false;
            }
            boolean can_B = b.getCores() >= cores && b.getMemorize() >= mem;
            return !(can_A || can_B);
        }
    }

    @Override
    public void Deploy() {

    }

    @Override
    public void OutputSolution() {
        int n_server = this.availableServers.length, n_virtual = this.availableVirtual.length;
        //对每一天分别进行服务器扩容、虚拟机迁移（暂不考虑）、部署虚拟机操作
        //1、判断是否需要进行服务器扩容（尝试部署）
        float[][] base_cost = new float[n_server][n_virtual];
        List<Server> buyList = new ArrayList<>();

        //2、进行虚拟机迁移
        //3、打印部署虚拟机操作
    }
}

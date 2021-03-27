package com.huawei.java.main.tool;

import com.huawei.java.main.model.Server;

/**
 * 数据集分析工具
 *
 * @author Kim小根
 * @date 2021/3/11 16:47
 * <p>Description:</p>
 */
public class AnalysisTool {

    /**
     * 添加虚拟机请求
     */
    private static final String ADD = "add";

    /**
     * 删除虚拟机请求
     */
    private static final String DEL = "del";

    /**
     * 打印每个服务器性价比
     *
     * @param availableServers 可获得的服务器
     * @param days             总天数
     */
    public static void printValueForMoney(Server[] availableServers, int days) {
        System.out.println("服务器类型\tCPU\t\t内存\t\t总CPU\t\t总内存");
        for (Server server : availableServers) {
            float cost = (float) server.getCost_of_devices() / days + server.getCost_of_energy();
            float cpu_cost = cost / server.getCores(), memorize_cost = cost / server.getMemorize();
            System.out.println(server.getType() + "\t" + String.format("%.2f", cpu_cost) + "\t" + String.format("%.2f", memorize_cost) + "\t" + server.getCores() + "\t" + server.getMemorize());
        }
    }

    /*public static void printMaxNeedofDaily(Virtual[] availableVirtual, Request[][] requests) {
        System.out.println("天数\tCPU\t内存");
        Map<Integer, String> inusedVirtual = new HashMap<>();
        for (int i = 0; i < requests.length; i++) {
            System.out.print("第" + (i + 1) + "天：\t");
            int max_cpu = 0, max_mem = 0;
            int curr_cpu = 0, curr_mem = 0;
            for (int j = 0; j < requests[i].length; j++) {
                Request r = requests[i][j];
                String type = r.getRequest_type();
                String vir_type = r.getVirtual_type();
                Virtual virtual = availableVirtual.get(vir_type);
                if (ADD.equals(type)) {
                    curr_cpu += virtual.getCores();
                    curr_mem += virtual.getMemorize();
                    inusedVirtual.put(r.getVirtual_id(), vir_type);
                } else {
                    vir_type = inusedVirtual.get(r.getVirtual_id());
                    virtual = availableVirtual.get(vir_type);
                    curr_cpu -= virtual.getCores();
                    curr_mem -= virtual.getMemorize();
                }
                max_cpu = max_cpu < curr_cpu ? curr_cpu : max_cpu;
                max_mem = max_mem < curr_mem ? curr_mem : max_mem;
            }
            System.out.println(max_cpu + "\t" + max_mem);
        }
    }*/

    public static void main(String[] args) {
        String path = "src/schedule/data/training-1.txt";    //读取训练数据-1
        //String path = "src/schedule/data/training-2.txt";    //读取训练数据-2
//        Manager manager = DataTool.constructDataModel(path);    //调度类
//        Server[] availableServers = manager.getAvailableServers();
//        Virtual[] availableVirtual = manager.getAvailableVirtual();
//        Request[][] requests = manager.getRequests();
//        printValueForMoney(availableServers, manager.getRequests().length);
        //printMaxNeedofDaily(availableVirtual, requests);
    }
}

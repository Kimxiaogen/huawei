package schedule.manager.impl;

import schedule.manager.Manager;
import schedule.model.*;
import schedule.output.StandardOut;

import java.util.*;

/**
 * 调度管理实现类
 *
 * @author Kim小根
 * @date 2021/3/11 13:43
 * <p>Description:实现接口方法</p>
 */
public class ManagerImpl implements Manager {

    /**
     * 表示A节点
     */
    private static final String NODE_A = "A";
    /**
     * 表示B节点
     */
    private static final String NODE_B = "B";

    /**
     * 添加虚拟机请求
     */
    private static final String ADD = "add";

    /**
     * 删除虚拟机请求
     */
    private static final String DEL = "del";
    /**
     * 迁移虚拟机请求
     */
    private static final String MIR = "mir";

    /**
     * 可购买的服务器类型
     */
    private Server[] availableServers;

    /**
     * 当前已拥有的服务器类型
     */
    private List<Server> currentServers;

    /**
     * 所有虚拟机类型，KEY：虚拟机类型（type字段），VALUE：虚拟机对象
     */
    private Map<String, Virtual> availableVirtuals;

    /**
     * 当前已添加的虚拟机，KEY：虚拟机编号（id字段），VALUE：虚拟机对象
     */
    private Map<Integer, Virtual> currentVirtuals;

    /**
     * 所有虚拟机请求，一维数组表示每一天的请求序列，二维数组表示当天的请求序列
     */
    private Request[][] requests;

    /**
     * 辅助map，用来找到指定服务器类型所在availableServers数组中的下标
     */
    private Map<String, Integer> typeToIndexMap;

    /**
     * 辅助map，用来给当前所有虚拟机按照CPU、内存的顺序排序
     * 数据结构 Map --> Map --> Set --> Virtual
     */
    private Map<Integer, Map> currentVirtualSort;


//    /**
//     * 辅助数组，对单节点虚拟机进行分类，一维下标 + 1 表示CPU核数，二维下标 + 1 表示内存容量
//    private List<Virtual>[][] virtualsSingleList;
//
//    *//**
//     * 辅助数组，对双节点虚拟机进行分类，一维下标 + 1 表示CPU核数，二维下标 + 1 表示内存容量
//     *//*
//    private List<Virtual>[][] virtualsDoubleList;*/

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

    public Map<String, Virtual> getAvailableVirtuals() {
        return availableVirtuals;
    }

    public void setAvailableVirtuals(Map<String, Virtual> availableVirtuals) {
        this.availableVirtuals = availableVirtuals;
    }

    public Map<Integer, Virtual> getCurrentVirtuals() {
        return currentVirtuals;
    }

    public void setCurrentVirtuals(Map<Integer, Virtual> currentVirtuals) {
        this.currentVirtuals = currentVirtuals;
    }

    @Override
    public Request[][] getRequests() {
        return requests;
    }

    public void setRequests(Request[][] requests) {
        this.requests = requests;
    }

    public ManagerImpl(Server[] availableServers, Map<String, Virtual> availableVirtuals, Request[][] requests) {
        this.availableServers = availableServers;
        this.currentServers = new ArrayList<>();
        this.availableVirtuals = availableVirtuals;
        this.currentVirtuals = new HashMap<>();
        this.requests = requests;
        this.currentVirtualSort = new HashMap<>();
        initialTypeToIndexMap();
        //initialVirtualsList();
    }

    /*private void initialVirtualsList() {
        virtualsSingleList = new ArrayList[1024][1024];  //暂时以最大服务器CPU、内存作为上限
        virtualsDoubleList = new ArrayList[1024][1024];
        for (String type : availableVirtuals.keySet()) {
            Virtual virtual = availableVirtuals.get(type);
            int x = virtual.getCores() - 1, y = virtual.getMemorize() - 1;
            if (virtual.isDoubleNodes()) {
                if (virtualsDoubleList[x][y] == null) virtualsDoubleList[x][y] = new ArrayList<>();
                virtualsDoubleList[x][y].add(virtual);
            } else {
                if (virtualsSingleList[x][y] == null) virtualsSingleList[x][y] = new ArrayList<>();
                virtualsSingleList[x][y].add(virtual);
            }
        }
        //打印输出
//        System.out.println("单节点虚拟机分布情况：");
//        for (int i = 0; i < virtualsSingleList.length; i++) {
//            for (int j = 0; j < virtualsSingleList[i].length; j++) {
//                System.out.print((virtualsSingleList[i][j] == null ? "\t" : virtualsSingleList[i][j].size()) + "\t");
//            }
//            System.out.println();
//        }
//        System.out.println("双节点虚拟机分布情况：");
//        for (int i = 0; i < virtualsDoubleList.length; i++) {
//            for (int j = 0; j < virtualsDoubleList[i].length; j++) {
//                System.out.print((virtualsDoubleList[i][j] == null ? "\t" : virtualsDoubleList[i][j].size()) + "\t");
//            }
//            System.out.println();
//        }
//        System.out.println();
    }*/

    private void initialTypeToIndexMap() {
        typeToIndexMap = new HashMap<>();
        for (int i = 0; i < availableServers.length; i++) {
            typeToIndexMap.put(availableServers[i].getType(), i);
        }
    }

    @Override
    public void OutputSolution() {
        int total_cost = 0;   //计算总成本
        //int max_cpu = 0, max_mem = 0;  //统计峰值
        //int curr_cpu = 0, curr_mem = 0;   //统计当前数据
        for (int i = 0; i < requests.length; i++) {
            List<Virtual> addList = new ArrayList<>(), outputList = new ArrayList<>(); //当天新增的虚拟机
            List<Virtual> delList = new ArrayList<>();    //当天删除的虚拟机
            List<Server> serverList = new ArrayList<>();   //当天新增的服务器
            List<Virtual> moveList;  //当天迁移的虚拟机
            LinkedNode[] ln;
            for (int j = 0; j < requests[i].length; j++) {
                Request r = requests[i][j];
                if (ADD.equals(r.getRequest_type())) {    //执行新增操作
                    Virtual v = availableVirtuals.get(r.getVirtual_type()).clone();
                    v.setId(r.getVirtual_id());
                    currentVirtuals.put(v.getId(), v);
                    addList.add(v);
                    outputList.add(v);
                    //curr_cpu += v.getCores();
                    //curr_mem += v.getMemorize();
                    updateCurrentVirtualList(v, ADD);
                } else {  //执行删除操作
                    Virtual v = currentVirtuals.remove(r.getVirtual_id());
                    //curr_cpu -= v.getCores();
                    //curr_mem -= v.getMemorize();
                    delList.add(v);
                    updateCurrentVirtualList(v, DEL);
//                    Server s = v.getServer();
//                    s.remove(v);
                }
                //max_cpu = Math.max(max_cpu, curr_cpu);
                //max_mem = Math.max(max_mem, curr_mem);
            }
            ln = constructOrder(addList);
            moveList = generateMigrationVirtuals();
            //填充前一天服务器
            for (int k = 0; k < currentServers.size(); k++) {
                Server s = currentServers.get(k);
                if (s.getCoresUsed() > 0 || s.getMemorizeUsed() > 0) total_cost += s.getCost_of_energy();    //计算开机成本
                int index = typeToIndexMap.get(s.getType());
                LinkedNode p = null, q = ln[index];
                while (q != null) {
                    Virtual v = q.getVirtual();
                    if (v.isDestory()) {
                        if (p == null) ln[index] = q.getNext();
                        else p.setNext(q.getNext());
                        q = q.getNext();
                        continue;
                    }
                    if (tryPut(s, v)) {
                        v.setDestory(true);
                        v.setServer(s);
                    }
                    p = q;
                    q = q.getNext();
                }
            }
            //部署当天服务器
            //System.out.println("第" + (i + 1) + "天");
//            if (i == 133) {
//                System.out.println();
//            }
            do {
                for (int n = 0; n < ln.length; n++) {   //剔除待删除节点
                    LinkedNode h = null, g = ln[n];
                    while (g != null) {
                        Virtual v = g.getVirtual();
                        if (v.isDestory()) {
                            if (h == null) ln[n] = g.getNext();
                            else h.setNext(g.getNext());
                            g = g.getNext();
                            continue;
                        }
                        h = g;
                        g = g.getNext();
                    }
//                    int len = 0;
//                    g = ln[n];
//                    while (g != null) {
//                        len++;
//                        g = g.getNext();
//                    }
//                    System.out.println("删除第" + n + "条节点后长度为：" + len);
                }
                if (ln[0] == null) continue;  //节点部署完毕
                List<Server> candicate_servers = new ArrayList<>();  //候选服务器集合
                for (int m = 0; m < availableServers.length; m++) {   //尝试添加服务器
                    Server s = availableServers[m].productServer(false);  //服务器模板
                    LinkedNode p = ln[m];
                    while (p != null) {
                        Virtual v = p.getVirtual();
                        //if (v.isDestory()) System.out.println("异常节点：" + p.toString());
                        tryPut(s, v);
                        p = p.getNext();
                    }
                    candicate_servers.add(s);
                }
                Server best_server = null;
                double best_cost = Double.MAX_VALUE;
                for (int v = 0; v < candicate_servers.size(); v++) {   //找到最优服务器并购买
                    Server s = candicate_servers.get(v);
                    double c = s.cost();
                    if (c < best_cost) {
                        best_server = s;
                        best_cost = c;
                    }
                }
                Set<Virtual> set = best_server.getVirtualSet();
                for (Virtual virtual : set) {
                    virtual.setDestory(true);
                    virtual.setServer(best_server);
                }
                //best_server.getNode_A().setNodeMessage();
                currentServers.add(best_server);
                serverList.add(best_server);
                total_cost += best_server.getCost_of_devices();   //加上购买成本
            } while (ln[0] != null);
            //StandardOut.purchase(serverList);   //打印当天购买的服务器信息
            //StandardOut.migration(moveList);   //打印当天虚拟机的迁移信息
            //StandardOut.deploy(outputList);   //打印部署当天虚拟机信息
            //StandardOut.showServer(currentServers, true);   //打印服务器使用情况
            for (int d = 0; d < delList.size(); d++) {
                Virtual v = delList.get(d);
                Server s = v.getServer();
                s.remove(v);
            }
            System.out.println("虚拟机数量：" + currentVirtuals.size() + "，可迁移数量：" + currentVirtuals.size() / 200);
        }
        //StandardOut.showServerStorage(currentServers);    //打印服务器总存储量
        //System.out.println("CPU峰值为：" + max_cpu + "C，内存峰值为：" + max_mem + "G");
        System.out.println("总成本：" + total_cost);
    }

    /**
     * 每次请求之后更新当前虚拟机顺序队列
     *
     * @param v  虚拟机
     * @param op 实行操作
     */
    private void updateCurrentVirtualList(Virtual v, String op) {
        int cores = v.getCores(), mem = v.getMemorize();
        if (ADD.equals(op)) {  //执行插入
            Map<Integer, Set> memMap = currentVirtualSort.getOrDefault(cores, null);
            if (memMap == null) {
                memMap = new HashMap<>();
                currentVirtualSort.put(cores, memMap);
            }
            Set<Virtual> virSet = memMap.getOrDefault(mem, null);
            if (virSet == null) {
                virSet = new HashSet<>();
                memMap.put(mem, virSet);
            }
            virSet.add(v);
        } else {   //执行删除
            Map<Integer, Set> memMap = currentVirtualSort.get(cores);
            Set<Virtual> virSet = memMap.get(mem);
            virSet.remove(v);
        }
    }

    /**
     * 分析服务器当前存储情况，构造迁移计划并执行
     * （由于迁移数量限制，迁移主要目的为提高资源利用率，不为空闲出服务器）
     *
     * @return 完成迁移的虚拟机集合
     */
    private List<Virtual> generateMigrationVirtuals() {
        List<Virtual> moveList = new ArrayList<>();  //定义完成迁移的虚拟机集合
        int n_migration = currentVirtuals.size() / 200;  //可迁移虚拟机数量
        int n_server = currentServers.size();  //当前服务器数量
        int[][][] doubleServersSituation = new int[n_server][2][2];  //当前服务器双节点方案
        int[][][] singleServersSituation = new int[n_server * 2][2][2];  //当前服务器单节点方案
        //注：一维数组表示每个服务器，在单节点方案中，每个服务器最小粒度为节点，故将A、B节点看作两个独立的服务器处理
        //    二维数组表示每个服务器的最大可装入虚拟机和最大不可装入虚拟机（两者均由其他服务器决定），故设置大小为2
        //    三维数组表示该虚拟机CPU、内存大小，故设置大小为2
        initialSituations(singleServersSituation, doubleServersSituation);  //初始化服务器方案
        for (int i = 0; i < n_migration; i++) {  //每迁移一次虚拟机后重新评估当前最优迁移方案
            //更新上一次移动后方案
            if (i != 0) updateSituations(singleServersSituation, doubleServersSituation);

            //考虑单节点迁移，使得迁移后服务器A、B节点更加一致

            //考虑双节点迁移，可接受迁移量由节点最小资源决定
            for (int j = 0; j < n_server; j++) {
                Server s = currentServers.get(j);

            }
        }
        return moveList;
    }

    /**
     * 初始化服务器方案
     *
     * @param singleServersSituation 当前服务器单节点方案
     * @param doubleServersSituation 当前服务器双节点方案
     */
    private void initialSituations(int[][][] singleServersSituation, int[][][] doubleServersSituation) {
        int n_server = currentServers.size();  //当前服务器数量
        for (int i = 0; i < n_server; i++) {
            Server s = currentServers.get(i);
            Node node_a = s.getNode_A(), node_b = s.getNode_B();

            //初始化当前最大可装入虚拟机
            singleServersSituation[i * 2][0][0] = node_a.getCores();
            singleServersSituation[i * 2][0][1] = node_a.getMemorize();
            singleServersSituation[i * 2 + 1][0][0] = node_b.getCores();
            singleServersSituation[i * 2 + 1][0][1] = node_b.getMemorize();
            doubleServersSituation[i][0][0] = s.getCores();
            doubleServersSituation[i][0][1] = s.getMemorize();
            //记录当前最大不可装入虚拟机
        }
    }

    /**
     * 更新服务器方案
     *
     * @param singleServersSituation 当前服务器单节点方案
     * @param doubleServersSituation 当前服务器双节点方案
     */
    private void updateSituations(int[][][] singleServersSituation, int[][][] doubleServersSituation) {
    }

    /**
     * 尝试将虚拟机放入服务器中，若可以放入，则直接放入并返回true；若不能放入，则直接返回false
     *
     * @param s 服务器
     * @param v 虚拟机
     */
    private boolean tryPut(Server s, Virtual v) {
        Node node_a = s.getNode_A(), node_b = s.getNode_B();
        if (v.isDoubleNodes()) {
            int half_cores = v.getCores() / 2, half_mem = v.getMemorize() / 2;
            if (node_a.getCores() >= half_cores && node_a.getMemorize() >= half_mem && node_b.getCores() >= half_cores && node_b.getMemorize() >= half_mem) {
                s.add(v, null);
                return true;
            }
        } else {
            //计算该虚拟机与A、B节点的余弦相似度，相似度低的优先放入
            int cores = v.getCores(), mem = v.getMemorize();
            double a = similarity(cores, mem, node_a.getCores(), node_a.getMemorize());
            double b = similarity(cores, mem, node_b.getCores(), node_b.getMemorize());
            if (a > b) {
                if (node_b.getCores() >= cores && node_b.getMemorize() >= mem) {
                    s.add(v, NODE_B);
                    return true;
                } else if (node_a.getCores() >= cores && node_a.getMemorize() >= mem) {
                    s.add(v, NODE_A);
                    return true;
                }
            } else {
                if (node_a.getCores() >= cores && node_a.getMemorize() >= mem) {
                    s.add(v, NODE_A);
                    return true;
                } else if (node_b.getCores() >= cores && node_b.getMemorize() >= mem) {
                    s.add(v, NODE_B);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 对所有服务器按照顺序各自对其虚拟机进行排序
     *
     * @param addList 当天需要添加的虚拟机集合
     * @return 单链表头结点集合
     */
    private LinkedNode[] constructOrder(List<Virtual> addList) {
        int n_server = availableServers.length, n_virtual = addList.size();
        LinkedNode[] result = new LinkedNode[n_server];
        for (int i = 0; i < n_server; i++) {
            Server s = availableServers[i];
            int s_cpu = s.getCores(), s_mem = s.getMemorize();
            LinkedNode node = null, next = null;
            Collections.sort(addList, (o1, o2) -> {     //按照余弦相似度重新排序
                double r = similarity(s_cpu, s_mem, o1.getCores(), o1.getMemorize()) - similarity(s_cpu, s_mem, o2.getCores(), o2.getMemorize());
                if (r > 0) return -1;
                else if (r < 0) return 1;
                else return 0;
            });
            for (int j = n_virtual - 1; j >= 0; j--) {    //构造当前服务器的顺序链表，采用尾插法
                node = new LinkedNode(addList.get(j));
                node.setNext(next);
                next = node;
            }
            result[i] = node;   //获取头结点
        }
        return result;
    }

    /**
     * 计算a、b向量的余弦相似度的绝对值
     *
     * @param a1 一维向量值
     * @param a2 二维向量值
     * @param b1 一维向量值
     * @param b2 二维向量值
     * @return 余弦相似度
     */
    private double similarity(int a1, int a2, int b1, int b2) {
        return Math.abs((a1 * b1 + a2 * b2) / (Math.sqrt(a1 * a1 + a2 * a2) * Math.sqrt(b1 * b1 + b2 * b2)));
    }

}

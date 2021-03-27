package com.huawei.java.main.manager.impl;

import com.huawei.java.main.manager.Manager;
import com.huawei.java.main.model.*;
import com.huawei.java.main.output.StandardOut;

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

//    /**
//     * 线程共享变量，存储当前最小cost
//     */
//    private volatile double min_cost;

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
        initialTypeToIndexMap();
    }

    private void initialTypeToIndexMap() {
        typeToIndexMap = new HashMap<>();
        for (int i = 0; i < availableServers.length; i++) {
            typeToIndexMap.put(availableServers[i].getType(), i);
        }
    }

    @Override
    public void OutputSolution() {
        //int total_cost = 0;   //计算总成本
        //int max_cpu = 0, max_mem = 0;  //统计峰值
        //int curr_cpu = 0, curr_mem = 0;   //统计当前数据
        for (int i = 0; i < requests.length; i++) {
            List<Virtual> addList = new ArrayList<>(), outputList = new ArrayList<>(); //当天新增的虚拟机
            List<Virtual> delList = new ArrayList<>();    //当天删除的虚拟机
            List<Server> serverList = new ArrayList<>();   //当天新增的服务器
            List<Virtual> moveList;  //当天迁移的虚拟机
            LinkedNode[] ln;
            moveList = generateMigrationVirtuals();
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
                } else {  //执行删除操作
                    Virtual v = currentVirtuals.remove(r.getVirtual_id());
                    //curr_cpu -= v.getCores();
                    //curr_mem -= v.getMemorize();
                    delList.add(v);
                }
                //max_cpu = Math.max(max_cpu, curr_cpu);
                //max_mem = Math.max(max_mem, curr_mem);
            }
            ln = constructOrder(addList);
            //填充前一天服务器
            for (int k = 0; k < currentServers.size(); k++) {
                Server s = currentServers.get(k);
                //if (s.getCoresUsed() > 0 || s.getMemorizeUsed() > 0) total_cost += s.getCost_of_energy();    //计算开机成本
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
                        if (!v.isDoubleNodes()) {
                            v.setNode(s.getNode_A().contain(v.getId()) ? NODE_A : NODE_B);
                        }
                    }
                    p = q;
                    q = q.getNext();
                }
            }
            //部署当天服务器
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
                }
//                LinkedNode pq = ln[0];
//                int count = 0;
//                while (pq != null) {
//                    pq = pq.getNext();
//                    count++;
//                }
//                System.out.println("当前长度:" + count);
                if (ln[0] == null) continue;  //节点部署完毕
                List<Server> candicate_servers = new ArrayList<>();  //候选服务器集合
                for (int m = 0; m < availableServers.length; m++) {   //尝试添加服务器
                    Server s = availableServers[m].productServer(false);  //服务器模板
                    LinkedNode p = ln[m];
                    while (p != null) {
                        Virtual v = p.getVirtual();
                        tryPut(s, v);
                        p = p.getNext();
                    }
                    if (s.getCoresUsed() == 0) continue;
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
                    if (!virtual.isDoubleNodes()) {
                        virtual.setNode(best_server.getNode_A().contain(virtual.getId()) ? NODE_A : NODE_B);
                    }
                }
                currentServers.add(best_server);
                serverList.add(best_server);
                //total_cost += best_server.getCost_of_devices();   //加上购买成本
            } while (ln[0] != null);
            StandardOut.purchaseDelay(serverList);   //打印当天购买的服务器信息（延迟）
            StandardOut.deployDelay(outputList);   //打印部署当天虚拟机信息（延迟）
            StandardOut.migrationDelay(moveList);   //打印当天虚拟机的迁移信息（延迟）
            for (int d = 0; d < delList.size(); d++) {
                Virtual v = delList.get(d);
                Server s = v.getServer();
                s.remove(v);
            }
            //StandardOut.writeToFile();  //写入文件
            StandardOut.flush();  //输出所有延迟打印内容
            //System.out.println("虚拟机数量：" + currentVirtuals.size() + "，可迁移数量：" + currentVirtuals.size() / 200);
            //System.out.println("服务器数量：" + currentServers.size());
        }
        //StandardOut.showServerStorage(currentServers);    //打印服务器总存储量
        //System.out.println("CPU峰值为：" + max_cpu + "C，内存峰值为：" + max_mem + "G");
        //System.out.println("总成本：" + total_cost);
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
        //int n_server = currentServers.size();  //当前服务器数量
        List<Server> serverSortList = new ArrayList<>(currentServers);  //构造当前服务器集合的副本，对其进行排序
        //List<Node> nodeSortList = new ArrayList<>();  //构造当前服务器节点集合的副本，对其进行排序

        //TODO 多线程方式
        /*int nThreads = 2;
        min_cost = Double.MAX_VALUE;  //初始化最小cost
        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        MigrationAlgorithm AVNS = new AdaptiveVariableNeighborhoodSearch(currentServers, currentVirtuals, n_migration, min_cost);
        MigrationAlgorithm SAHH = new SimulatedAnnealingbasedHyperHeuristic(currentServers, currentVirtuals, n_migration, min_cost);
        List<Future<List<Virtual>>> tasks = new ArrayList<>();
        tasks.add(es.submit(AVNS));
        tasks.add(es.submit(SAHH));
        for (Future<List<Virtual>> task : tasks) {
            try {
                List<Virtual> r = task.get();
                if (calcCostofVirtuals(r) == min_cost) {
                    moveList = r;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();*/

        //迁移策略，优先腾出平均利用率最低的服务器
        Server lowest_server;
        int j = 0;
        Iterator<Server> its = serverSortList.iterator();
        while (its.hasNext()) {  //剔除空服务器和满服务器
            Server s = its.next();
            if (s.getCoresUsed() == 0 && s.getMemorizeUsed() == 0) its.remove();
            else if (s.getMemorize() == 0 || s.getCores() == 0) its.remove();
        }
        Collections.sort(serverSortList, (o1, o2) -> {  //按照利用率排序
            double r = o1.getUsedRate() - o2.getUsedRate();
            if (r > 0) return 1;
            else if (r < 0) return -1;
            else return 0;
        });
        /*Set<Virtual> totalSet = new HashSet<>();
        do {
            while (totalSet.size() < n_migration && j < n_server) {
                lowest_server = serverSortList.get(j++);
                Set<Virtual> virtualSet = lowest_server.getVirtualSet();
                totalSet.addAll(virtualSet);
            }
            n_migration = putIt(serverSortList, totalSet, n_migration, moveList);
            totalSet.clear();
        } while (n_migration > 0 && j < n_server);*/
        //for (int k = (n_server + j - 1) / 2; k < n_server && n_migration > 0; k++) {  //从中间开始搜索
        while (n_migration > 0 && j < serverSortList.size()) {
            lowest_server = serverSortList.get(j++);
            Set<Virtual> virtualSet = lowest_server.getVirtualSet();
            Iterator<Virtual> it = virtualSet.iterator();
            while (n_migration > 0 && it.hasNext()) {
                Virtual v = it.next();
                for (int k = serverSortList.size() - 1; k > j && n_migration > 0; k--) {  //从利用率最高的服务器开始搜索
                    Server goal_server = serverSortList.get(k);
                    Node a = goal_server.getNode_A(), b = goal_server.getNode_B(), from;
                    if (v.isDoubleNodes()) {
                        int cores = v.getCores() / 2, mem = v.getMemorize() / 2;
                        boolean canPut = cores <= a.getCores() && mem <= a.getMemorize() && cores <= b.getCores() && mem <= b.getMemorize();
                        if (!canPut) continue;  //不可放入，直接跳过
                        lowest_server.getNode_A().allocate(-cores, -mem, null);
                        lowest_server.getNode_B().allocate(-cores, -mem, null);
                        lowest_server.updateUtilizationRate();
                        //lowest_server.removeNoSet(v);
                        goal_server.add(v, null);
                        Virtual clone = v.clone();
                        clone.setId(v.getId());
                        clone.setServer(v.getServer());
                        moveList.add(clone);
                        it.remove();
                        n_migration--;
                        if (goal_server.getCores() == 0 || goal_server.getMemorize() == 0) {
                            serverSortList.remove(k);
                        }
                        break;
                    } else if (v.getCores() <= a.getCores() && v.getMemorize() <= a.getMemorize()) {  //放入A节点
                        from = NODE_A.equals(v.getNode()) ? lowest_server.getNode_A() : lowest_server.getNode_B();
                        from.allocate(-v.getCores(), -v.getMemorize(), v.getId());
                        lowest_server.updateUtilizationRate();
                        //lowest_server.removeNoSet(v);
                        goal_server.add(v, NODE_A);
                        Virtual clone = v.clone();
                        clone.setId(v.getId());
                        clone.setServer(v.getServer());
                        moveList.add(clone);
                        it.remove();
                        n_migration--;
                        if (goal_server.getCores() == 0 || goal_server.getMemorize() == 0) {
                            serverSortList.remove(k);
                        }
                        break;
                    } else if (v.getCores() <= b.getCores() && v.getMemorize() <= b.getMemorize()) {  //放入B节点
                        from = NODE_A.equals(v.getNode()) ? lowest_server.getNode_A() : lowest_server.getNode_B();
                        from.allocate(-v.getCores(), -v.getMemorize(), v.getId());
                        lowest_server.updateUtilizationRate();
                        //lowest_server.removeNoSet(v);
                        goal_server.add(v, NODE_B);
                        Virtual clone = v.clone();
                        clone.setId(v.getId());
                        clone.setServer(v.getServer());
                        moveList.add(clone);
                        it.remove();
                        n_migration--;
                        if (goal_server.getCores() == 0 || goal_server.getMemorize() == 0) {
                            serverSortList.remove(k);
                        }
                        break;
                    }
                }
            }
            if (!virtualSet.isEmpty()) {
                break;
            }
        }

        return moveList;
    }

    /**
     * 使用基数排序，收集时只收集到v所在的堆
     * TODO 可根据set大小和迁移次数关系优化
     *
     * @param serverSortList
     * @param virtualSet
     * @param n_migration
     * @param moveList
     * @return
     */
    private int putIt(List<Server> serverSortList, Set<Virtual> virtualSet, int n_migration, List<Virtual> moveList) {
        int n_virtuals = virtualSet.size(), n_server = serverSortList.size();
        Virtual[] virtuals = new Virtual[n_virtuals];
        Node[] virtualNode = new Node[n_virtuals];
        Node[] server_single = new Node[n_server * 2], server_double = new Node[n_server];
        List<Node>[] divideList_single = new List[10], divideList_double = new List[10];   //分配列表
        List<Node> gatherList_single, gatherList_double;  //收集列表
        int i = 0, double_count = 0, single_count = 0;
        for (int j = 0; j < 10; j++) {
            divideList_single[j] = new ArrayList<>();
            divideList_double[j] = new ArrayList<>();
        }
        for (Virtual v : virtualSet) {
            virtuals[i] = v;
            if (v.isDoubleNodes()) {
                virtualNode[i] = new Node(v.getCores() / 2, v.getMemorize() / 2);
                virtualNode[i].setCores_used(-2);  //用作表示双节点
                virtualNode[i].setMemorize_used(i);  //用来存放下标
                double_count++;
            } else {
                virtualNode[i] = new Node(v.getCores(), v.getMemorize());
                virtualNode[i].setCores_used(-1); //用作表示单节点
                virtualNode[i].setMemorize_used(i);  //用来存放下标
                single_count++;
            }
            i++;
        }
        gatherList_single = new ArrayList<>(n_server * 2 + single_count);
        gatherList_double = new ArrayList<>(n_server + double_count);
        i = 0;
        for (Server s : serverSortList) {
            Node a = s.getNode_A(), b = s.getNode_B();
            server_double[i] = new Node(Math.min(a.getCores(), b.getCores()), Math.min(a.getMemorize(), b.getMemorize()));
            server_double[i].setServer(s);
            server_single[i << 1] = a;
            server_single[1 + (i << 1)] = b;
            i++;
        }
        int bit = 6, c = 0;  //最高考虑3位，CPU\内存总共6位
        while (c < bit) {  //先分配，再收集
            int double_collect = 0, single_collect = 0;
            if (c == 0) {
                //单节点分配
                for (int j = 0; j < server_single.length; j++) {
                    int num = getByBit(server_single[j], c);
                    divideList_single[num].add(server_single[j]);
                }
                //双节点分配
                for (int j = 0; j < server_double.length; j++) {
                    int num = getByBit(server_double[j], c);
                    divideList_double[num].add(server_double[j]);
                }
                //虚拟机分配
                for (int j = 0; j < virtualNode.length; j++) {
                    if (virtualNode[j].getCores_used() == -2) {  //双节点
                        int num = getByBit(virtualNode[j], c);
                        divideList_double[num].add(virtualNode[j]);
                    } else {  //单节点
                        int num = getByBit(virtualNode[j], c);
                        divideList_single[num].add(virtualNode[j]);
                    }
                }
            } else {
                //单节点分配
                for (int j = 0; j < gatherList_single.size(); j++) {
                    Node node = gatherList_single.get(j);
                    int num = getByBit(node, c);
                    divideList_single[num].add(node);
                }
                gatherList_single.clear();
                //双节点分配
                for (int j = 0; j < gatherList_double.size(); j++) {
                    Node node = gatherList_double.get(j);
                    int num = getByBit(node, c);
                    divideList_double[num].add(node);
                }
                gatherList_double.clear();
            }
            c++;
            //单节点收集
            for (int j = 9; j >= 0 && single_collect < single_count; j--) {
                int size = divideList_single[j].size();
                for (int k = 0; k < size; k++) {
                    Node node = divideList_single[j].get(k);
                    if (node.getCores_used() == -1) single_collect++;
                    gatherList_single.add(node);
                }
            }
            //双节点收集
            for (int j = 9; j >= 0 && double_collect < double_count; j--) {
                int size = divideList_double[j].size();
                for (int k = 0; k < size; k++) {
                    Node node = divideList_double[j].get(k);
                    if (node.getCores_used() == -2) double_collect++;
                    gatherList_double.add(node);
                }
            }
            for (int j = 0; j < 10; j++) {
                divideList_single[j].clear();
                divideList_double[j].clear();
            }
        }
        //排序完成，对虚拟机进行迁移
        out:
        for (int j = 0; n_migration > 0 && j < gatherList_double.size(); j++) {  //优先迁移双节点
            Node node = gatherList_double.get(j);
            if (node.getCores_used() == -2) {
                int k = j - 1;
                while (k >= 0) {
                    Node preNode = gatherList_double.get(k);
                    if (preNode.getCores_used() == -2) {
                        //说明无可分配节点
                        break out;
                    } else if (preNode.getCores() < node.getCores() || preNode.getMemorize() < node.getMemorize()) {
                        //已分配给其他节点
                        k--;
                    } else {
                        //执行分配
                        Virtual v = virtuals[node.getMemorize_used()];
                        v.getServer().remove(v);
                        preNode.getServer().add(v, null);
                        Virtual clone = v.clone();
                        clone.setId(v.getId());
                        clone.setServer(v.getServer());
                        moveList.add(clone);
                        n_migration--;
                        break;
                    }
                }
            }
        }
        //若还可迁移，迁移单节点
        out2:
        for (int j = 0; n_migration > 0 && j < gatherList_single.size(); j++) {
            Node node = gatherList_single.get(j);
            if (node.getCores_used() == -1) {
                int k = j - 1;
                while (k >= 0) {
                    Node preNode = gatherList_single.get(k);
                    if (preNode.getCores_used() == -1) {
                        //说明无可分配节点
                        break out2;
                    } else if (preNode.getCores() < node.getCores() || preNode.getMemorize() < node.getMemorize()) {
                        //已分配给其他节点
                        k--;
                    } else {
                        //执行分配
                        Virtual v = virtuals[node.getMemorize_used()];
                        v.getServer().remove(v);
                        preNode.getServer().add(v, preNode.getServer().getNode_A().equals(preNode) ? NODE_A : NODE_B);
                        Virtual clone = v.clone();
                        clone.setId(v.getId());
                        clone.setServer(v.getServer());
                        moveList.add(clone);
                        n_migration--;
                        break;
                    }
                }
            }
        }
        return n_migration;
    }

    private int getByBit(Node node, int bit) {
        switch (bit) {
            case 0:
                return node.getMemorize() % 10;
            case 1:
                return node.getMemorize() / 10 % 10;
            case 2:
                return node.getMemorize() / 100;
            case 3:
                return node.getCores() % 10;
            case 4:
                return node.getCores() / 10 % 10;
            case 5:
                return node.getCores() / 100;
            default:
                return -1;
        }
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
            boolean canPutA = node_a.getCores() >= cores && node_a.getMemorize() >= mem;
            boolean canPutB = node_b.getCores() >= cores && node_b.getMemorize() >= mem;
            if (canPutA && !canPutB) {
                s.add(v, NODE_A);
                return true;
            }
            if (!canPutA && canPutB) {
                s.add(v, NODE_B);
                return true;
            }
            if (canPutA && canPutB) {
                double a = similarity(cores, mem, node_a.getCores(), node_a.getMemorize());
                double b = similarity(cores, mem, node_b.getCores(), node_b.getMemorize());
                if (a > b) s.add(v, NODE_B);
                else s.add(v, NODE_A);
                return true;
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

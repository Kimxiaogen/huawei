package com.huawei.java.main.tool;

import com.huawei.java.main.manager.Manager;
import com.huawei.java.main.model.Request;
import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证结果工具
 *
 * @author Kim小根
 * @date 2021/3/25 22:07
 * <p>Description: 验证结果工具</p>
 */
public class ValidTool {

    /**
     * key : id, value : [cpu_used_a, mem_used_a, cpu_used_b, mem_used_b]
     * [cpu_total_a, mem_total_a, cpu_total_b, mem_total_B]
     */
    private static Map<Integer, Integer[][]> servers;

    /**
     * key : v_id, value : [s_id, type(0-双节点,1-A节点,2-B节点)]
     */
    private static Map<Integer, Integer[]> v_to_s_map;

    /**
     * key : id, value : [cpu, mem]
     */
    private static Map<Integer, Integer[]> virtuals;

    private static Server[] availableServers;
    private static Map<String, Integer> typeToIndexMap;
    private static Map<String, Virtual> availableVirtuals;

    /**
     * auto_id
     */
    private static int auto_id = 0;

    public static void main(String[] args) {
        String dataPath = "src/com/huawei/java/main/data/training-1.txt";
        String resultPath = "src/com/huawei/java/main/data/output1.txt";
        validResult(dataPath, resultPath);
    }

    /**
     * 验证数据集和结果集是否吻合
     *
     * @param dataPath   数据集
     * @param resultPath 结果集
     */
    public static void validResult(String dataPath, String resultPath) {
        servers = new HashMap<>();
        virtuals = new HashMap<>();
        v_to_s_map = new HashMap<>();
        Manager m = DataTool.constructDataModel(dataPath);
        availableServers = m.getAvailableServers();
        List<String> resultList = readFromPath(resultPath);
        availableVirtuals = m.getAvailableVirtuals();
        Request[][] r = m.getRequests();
        typeToIndexMap = new HashMap<>();
        for (int i = 0; i < availableServers.length; i++) {
            typeToIndexMap.put(availableServers[i].getType(), i);
        }
        int index = 0;
        for (int i = 0; i < r.length; i++) {
            String[] message = solve(resultList.get(index++));
            int purchaseNum;
            if (message[0].equals("purchase")) {
                purchaseNum = Integer.valueOf(message[1]);
            } else {
                System.out.println("购买信息错误");
                return;
            }
            //购买服务器阶段
            for (int j = 0; j < purchaseNum; j++) {
                String[] buy = solve(resultList.get(index++));
                int buyNum = Integer.valueOf(buy[1]);
                Server s = findByType(buy[0]);
                for (int k = 0; k < buyNum; k++) {
                    servers.put(auto_id++, new Integer[][]{
                            {s.getNode_A().getCores_used(), s.getNode_A().getMemorize_used(), s.getNode_B().getCores_used(), s.getNode_B().getMemorize_used()}
                            , {s.getNode_A().getCores() + s.getNode_A().getCores_used(), s.getNode_A().getMemorize() + s.getNode_A().getMemorize_used(), s.getNode_B().getCores() + s.getNode_B().getCores_used(), s.getNode_B().getMemorize() + s.getNode_B().getMemorize_used()}});
                }
            }
            //迁移服务器阶段
            String[] mi_message = solve(resultList.get(index++));
            int migrateNum;
            if (mi_message[0].equals("migration")) {
                migrateNum = Integer.valueOf(mi_message[1]);
            } else {
                System.out.println("迁移信息错误");
                return;
            }
            for (int j = 0; j < migrateNum; j++) {
                String[] migration = solve(resultList.get(index++));
                int v_id = Integer.valueOf(migration[0]);
                int s_id = Integer.valueOf(migration[1]);
                //Integer[] v = virtuals.get(v_id);
                //Integer[] v_to_s = v_to_s_map.get(v_id);
                if (migration.length == 3) {  //单节点
                    delVirtual(v_id);
                    addVirtual(v_id, s_id, migration[2].equals("A") ? 1 : 2);
                } else if (migration.length == 2) { //双节点
                    delVirtual(v_id);
                    addVirtual(v_id, s_id, 0);
                } else {
                    System.out.println("异常迁移数据");
                    return;
                }
            }
            for (int j = 0; j < r[i].length; j++) {
                Request request = r[i][j];
                if (request.getRequest_type().equals("add")) {  //添加
                    Virtual virtual = findByVirtualType(request.getVirtual_type());
                    virtuals.put(request.getVirtual_id(), new Integer[]{virtual.getCores(), virtual.getMemorize()});
                    String[] add_message = solve(resultList.get(index++));
                    int server_id = Integer.valueOf(add_message[0]);
                    if (add_message.length == 2 && !virtual.isDoubleNodes()) {  //单节点
                        switch (add_message[1]) {
                            case "A":
                                addVirtual(request.getVirtual_id(), server_id, 1);
                                break;
                            case "B":
                                addVirtual(request.getVirtual_id(), server_id, 2);
                                break;
                            default:
                                System.out.println("异常虚拟机类型数据");
                                return;
                        }
                    } else if (add_message.length == 1 && virtual.isDoubleNodes()) {  //双节点
                        addVirtual(request.getVirtual_id(), server_id, 0);
                    } else {
                        System.out.println("异常添加数据");
                        return;
                    }
                } else {  //删除
                    delVirtual(request.getVirtual_id());
                }
            }
        }
        System.out.println(index == resultList.size());
    }

    /**
     * 添加虚拟机
     *
     * @param virtual_id
     * @param server_id
     * @param type
     */
    private static void addVirtual(int virtual_id, int server_id, int type) {
        Integer[][] s = servers.get(server_id);
        Integer[] v = virtuals.get(virtual_id);
        if (v_to_s_map.get(virtual_id) != null) {
            System.out.println("无法添加已分配的虚拟机，id ： " + virtual_id);
            //System.exit(0);
        }
        v_to_s_map.put(virtual_id, new Integer[]{server_id, type});
        switch (type) {
            case 0:
                s[0][0] += v[0] / 2;
                s[0][1] += v[1] / 2;
                s[0][2] += v[0] / 2;
                s[0][3] += v[1] / 2;
                break;
            case 1:
                s[0][0] += v[0];
                s[0][1] += v[1];
                break;
            case 2:
                s[0][2] += v[0];
                s[0][3] += v[1];
                break;
        }
        String message = checkServer(server_id);
        if (message.equals("success")) return;
        else {
            System.out.println(message);
            //System.exit(0);
        }
    }

    /**
     * 删除虚拟机
     *
     * @param virtual_id
     */
    private static void delVirtual(int virtual_id) {
        Integer[] message = v_to_s_map.get(virtual_id);
        if (message == null) {
            System.out.println("删除未分配的虚拟机，id ： " + virtual_id);
            //System.exit(0);
        }
        Integer[] v = virtuals.get(virtual_id);
        Integer[][] s = servers.get(message[0]);
        v_to_s_map.put(virtual_id, null);
        switch (message[1]) {
            case 0:
                s[0][0] -= v[0] / 2;
                s[0][1] -= v[1] / 2;
                s[0][2] -= v[0] / 2;
                s[0][3] -= v[1] / 2;
                break;
            case 1:
                s[0][0] -= v[0];
                s[0][1] -= v[1];
                break;
            case 2:
                s[0][2] -= v[0];
                s[0][3] -= v[1];
                break;
        }
        String m = checkServer(message[0]);
        if (m.equals("success")) return;
        else {
            System.out.println(m);
            //System.exit(0);
        }
    }

    /**
     * 检查服务器资源是否溢出或为负
     *
     * @param server_id
     * @return 正常返回success，异常返回异常信息
     */
    private static String checkServer(int server_id) {
        Integer[][] s = servers.get(server_id);
//        if (server_id == 13) {
//            showServer(s);
//        }
        if (s[0][0] < 0) return server_id + "：A节点的CPU为负.";
        if (s[0][1] < 0) return server_id + "：A节点的内存为负.";
        if (s[0][2] < 0) return server_id + "：B节点的CPU为负.";
        if (s[0][3] < 0) return server_id + "：B节点的内存为负.";
        if (s[0][0] > s[1][0]) return server_id + "：A节点的CPU溢出（" + s[0][0] + "/" + s[1][0] + "）";
        if (s[0][1] > s[1][1]) return server_id + "：A节点的内存溢出（" + s[0][1] + "/" + s[1][1] + "）";
        if (s[0][2] > s[1][2]) return server_id + "：B节点的CPU溢出（" + s[0][2] + "/" + s[1][2] + "）";
        if (s[0][3] > s[1][3]) return server_id + "：B节点的内存溢出（" + s[0][3] + "/" + s[1][3] + "）";
        return "success";
    }

    private static void showServer(Integer[][] s) {
        System.out.println("\t\tA节点\t\tB节点");
        System.out.println("占用\t" + s[0][0] + "/" + s[0][1] + "\t" + s[0][2] + "/" + s[0][3]);
        System.out.println("总量\t" + s[1][0] + "/" + s[1][1] + "\t" + s[1][2] + "/" + s[1][3]);
    }

    private static Virtual findByVirtualType(String s) {
        return availableVirtuals.get(s);
    }

    private static Server findByType(String s) {
        int index = typeToIndexMap.get(s);
        Server r = availableServers[index];
        return r;
    }

    /**
     * 解析字符串内容
     *
     * @param s
     * @return
     */
    private static String[] solve(String s) {
        s = s.substring(1, s.length() - 1);
        String[] result = s.split(", ");
        return result;
    }

    private static List<String> readFromPath(String path) {
        File file = new File(path);
        FileReader fr;
        BufferedReader br;
        String line;
        List<String> text = new ArrayList<>();
        try {    //读取数据
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                text.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在。");
        } catch (IOException e) {
            System.out.println("文件读取异常。");
        }
        return text;
    }
}

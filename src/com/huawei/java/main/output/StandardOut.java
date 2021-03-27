package com.huawei.java.main.output;

import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 统一输出工具
 *
 * @author Kim小根
 * @date 2021/3/16 17:06
 * <p>Description:统一输出</p>
 */
public class StandardOut {

    private static final String PREFIX = "(";
    private static final String SUFFIX = ")";
    private static final String SPLIT = ", ";

    private static final String PURCHASE = "purchase";
    private static final String MIGRATION = "migration";

    /**
     * 延迟输出集合
     */
    private static List<String> outputPurchaseList = new ArrayList<>();
    private static List<String> outputMigrationList = new ArrayList<>();
    private static List<String> outputDeployList = new ArrayList<>();

    /**
     * 表示A节点
     */
    private static final String NODE_A = "A";
    /**
     * 表示B节点
     */
    private static final String NODE_B = "B";

    /**
     * 延迟输出购买操作
     *
     * @param servers 当前需要购买的服务器集合
     */
    public static void purchaseDelay(List<Server> servers) {
        StringBuilder sb = new StringBuilder();
        if (servers == null) {
            sb.append(PREFIX);
            sb.append(PURCHASE);
            sb.append(SPLIT);
            sb.append(0);
            sb.append(SUFFIX);
            outputPurchaseList.add(sb.toString());
            return;
        }
        int n = servers.size();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Server s = servers.get(i);
            Integer times = map.getOrDefault(s.getType(), null);
            times = times == null ? 1 : times + 1;
            map.put(s.getType(), times);
        }
        sb.append(PREFIX);
        sb.append(PURCHASE);
        sb.append(SPLIT);
        sb.append(map.size());
        sb.append(SUFFIX);
        outputPurchaseList.add(sb.toString());
        for (String type : map.keySet()) {
            for (int i = 0; i < n; i++) {
                if (type.equals(servers.get(i).getType())) servers.get(i).productServer(true);  //赋予编号
            }
            sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(type);
            sb.append(SPLIT);
            sb.append(map.get(type));
            sb.append(SUFFIX);
            outputPurchaseList.add(sb.toString());
        }
    }

    /**
     * 延迟输出迁移操作
     *
     * @param virtuals 迁移后的虚拟机集合
     */
    public static void migrationDelay(List<Virtual> virtuals) {
        StringBuilder sb = new StringBuilder();
        int n = virtuals == null ? 0 : virtuals.size();    //迁移机器数
        sb.append(PREFIX);
        sb.append(MIGRATION);
        sb.append(SPLIT);
        sb.append(n);
        sb.append(SUFFIX);
        outputMigrationList.add(sb.toString());
        for (int i = 0; i < n; i++) {
            sb = new StringBuilder();
            Virtual v = virtuals.get(i);
            if (v.isDoubleNodes()) {
                sb.append(PREFIX);
                sb.append(v.getId());
                sb.append(SPLIT);
                sb.append(v.getServer().getNo());
                sb.append(SUFFIX);
                outputMigrationList.add(sb.toString());
            } else {
                sb.append(PREFIX);
                sb.append(v.getId());
                sb.append(SPLIT);
                sb.append(v.getServer().getNo());
                sb.append(SPLIT);
                sb.append(v.getNode());
                sb.append(SUFFIX);
                outputMigrationList.add(sb.toString());
            }
        }
    }

    /**
     * 延迟输出部署虚拟机操作
     *
     * @param virtuals 虚拟机集合
     */
    public static void deployDelay(List<Virtual> virtuals) {
        for (int i = 0; i < virtuals.size(); i++) {
            StringBuilder sb = new StringBuilder();
            Virtual v = virtuals.get(i);
            if (v.isDoubleNodes()) {
                sb.append(PREFIX);
                sb.append(v.getServer().getNo());
                sb.append(SUFFIX);
                outputDeployList.add(sb.toString());
            } else if (v.getNode().equals(NODE_A)) {
                sb.append(PREFIX);
                sb.append(v.getServer().getNo());
                sb.append(SPLIT);
                sb.append(NODE_A);
                sb.append(SUFFIX);
                outputDeployList.add(sb.toString());
            } else {
                sb.append(PREFIX);
                sb.append(v.getServer().getNo());
                sb.append(SPLIT);
                sb.append(NODE_B);
                sb.append(SUFFIX);
                outputDeployList.add(sb.toString());
            }
        }
    }

    /**
     * 刷新输出所有延迟输出内容
     */
    public static void flush() {
        for (String s : outputPurchaseList) System.out.println(s);
        for (String s : outputMigrationList) System.out.println(s);
        for (String s : outputDeployList) System.out.println(s);
        outputPurchaseList.clear();
        outputMigrationList.clear();
        outputDeployList.clear();
    }

//    static String path = "src/com/huawei/java/main/data/output1.txt";
//    static File file = new File(path);
//    static FileWriter fw;
//    static BufferedWriter bw;
//
//    public static void writeToFile() {
//        try {
//            if (fw == null) fw = new FileWriter(file);
//            if (bw == null) bw = new BufferedWriter(fw);
//            for (int i = 0; i < outputPurchaseList.size(); i++) {
//                bw.write(outputPurchaseList.get(i));
//                bw.newLine();
//            }
//            for (int i = 0; i < outputMigrationList.size(); i++) {
//                bw.write(outputMigrationList.get(i));
//                bw.newLine();
//            }
//            for (int i = 0; i < outputDeployList.size(); i++) {
//                bw.write(outputDeployList.get(i));
//                bw.newLine();
//            }
//            outputPurchaseList.clear();
//            outputMigrationList.clear();
//            outputDeployList.clear();
//            bw.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}

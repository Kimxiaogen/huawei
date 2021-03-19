package schedule.output;

import schedule.model.Server;
import schedule.model.Virtual;

import java.util.*;

/**
 * 统一输出工具
 *
 * @author Kim小根
 * @date 2021/3/16 17:06
 * <p>Description:统一输出</p>
 */
public class StandardOut {

    private static String PREFIX = "(";
    private static String SUFFIX = ")";
    private static String SPLIT = ", ";

    private static String PURCHASE = "purchase";
    private static String MIGRATION = "migration";

    /**
     * 表示A节点
     */
    private static final String NODE_A = "A";
    /**
     * 表示B节点
     */
    private static final String NODE_B = "B";

    /**
     * 输出购买操作
     *
     * @param servers 当前需要购买的服务器集合
     */
    public static void purchase(List<Server> servers) {
        if (servers == null) {
            System.out.println(PREFIX + PURCHASE + SPLIT + 0 + SUFFIX);
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
        System.out.println(PREFIX + PURCHASE + SPLIT + map.size() + SUFFIX);
        for (String type : map.keySet()) {
            for (int i = 0; i < n; i++) {
                if (type.equals(servers.get(i).getType())) servers.get(i).productServer(true);  //赋予编号
            }
            System.out.println(PREFIX + type + SPLIT + map.get(type) + SUFFIX);
        }
    }

    /**
     * 输出迁移操作
     *
     * @param virtuals 迁移后的虚拟机集合
     */
    public static void migration(List<Virtual> virtuals) {
        int n = virtuals == null ? 0 : virtuals.size();    //迁移机器数
        System.out.println(PREFIX + MIGRATION + SPLIT + n + SUFFIX);
        for (int i = 0; i < n; i++) {
            Virtual v = virtuals.get(i);
            if (v.isDoubleNodes()) {
                System.out.println(PREFIX + v.getId() + SPLIT + v.getServer().getNo() + SUFFIX);
            } else {
                System.out.println(PREFIX + v.getId() + SPLIT + v.getServer().getNo() + SPLIT + v.getNode() + SUFFIX);
            }
        }
    }

    /**
     * 输出部署虚拟机操作
     *
     * @param virtuals 虚拟机集合
     */
    public static void deploy(List<Virtual> virtuals) {
        for (int i = 0; i < virtuals.size(); i++) {
            Virtual v = virtuals.get(i);
            if (v.isDoubleNodes()) {
                System.out.println(PREFIX + v.getServer().getNo() + SUFFIX);
            } else if (v.getServer().getNode_A().contain(v.getId())) {
                System.out.println(PREFIX + v.getServer().getNo() + SPLIT + NODE_A + SUFFIX);
            } else {
                System.out.println(PREFIX + v.getServer().getNo() + SPLIT + NODE_B + SUFFIX);
            }
        }
    }

    /**
     * 计算天数
     */
    private static int days = 0;

    /**
     * 输出服务器使用情况
     *
     * @param servers 当前服务器集合
     * @param percent 是否显示百分比，若为true，则显示百分比；若为false，则显示数值
     */
    public static void showServer(List<Server> servers, boolean percent) {
        System.out.print("第" + ++days + "天：");
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (percent) {
                float core_per = (float) s.getCores() / (s.getCores() + s.getCoresUsed());
                float mem_per = (float) s.getMemorize() / (s.getMemorize() + s.getMemorizeUsed());
                System.out.print(String.format("%.2f", core_per) + "%/" + String.format("%.2f", mem_per) + "%");
            } else {
                System.out.print(s.getCores() + "C/" + s.getMemorize() + "G");
            }
            System.out.print("\t");
        }
        System.out.println();
    }

    /**
     * 输出服务器总存储量
     *
     * @param servers 服务器集合
     */
    public static void showServerStorage(List<Server> servers) {
        int cpu_total = 0, mem_total = 0;
        int cpu_used = 0, mem_used = 0;
        int cpu_rest = 0, mem_rest = 0;
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            cpu_used += s.getCoresUsed();
            mem_used += s.getMemorizeUsed();
            cpu_rest += s.getCores();
            mem_rest += s.getMemorize();
        }
        cpu_total += cpu_rest + cpu_used;
        mem_total += mem_rest + mem_used;
        System.out.println("当前服务器有CPU：" + cpu_total + "C，内存" + mem_total + "G");
        System.out.println("当前服务器已使用CPU：" + cpu_used + "C，内存" + mem_used + "G");
        System.out.println("当前服务器空闲CPU：" + cpu_rest + "C，内存" + mem_rest + "G");
    }
}

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
        int n = servers == null ? 0 : servers.size();
        Map<String, Integer> map = new HashMap<>();
        System.out.println(PREFIX + PURCHASE + SPLIT + n + SUFFIX);
        for (int i = 0; i < n; i++) {
            Server s = servers.get(i);
            Integer times = map.getOrDefault(s.getType(), null);
            times = times == null ? 1 : times + 1;
            map.put(s.getType(), times);
        }
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
            } else if(v.getServer().getNode_A().contain(v.getId())){
                System.out.println(PREFIX + v.getServer().getNo() + SPLIT + NODE_A + SUFFIX);
            } else {
                System.out.println(PREFIX + v.getServer().getNo() + SPLIT + NODE_B + SUFFIX);
            }
        }
    }
}

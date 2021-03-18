package schedule.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 服务器节点
 *
 * @author Kim小根
 * @date 2021/3/11 10:25
 * <p>Description:实现服务器节点的主要功能</p>
 */
public class Node {

    /**
     * 表示A节点
     */
    private static final String NODE_A = "A";
    /**
     * 表示B节点
     */
    private static final String NODE_B = "B";

    /**
     * 服务器节点当前拥有的CPU核数
     */
    private int cores;

    /**
     * 服务器节点当前拥有的内存大小
     */
    private int memorize;

    /**
     * 服务器节点正在使用的CPU核数
     */
    private int cores_used;

    /**
     * 服务器节点正在使用的内存大小
     */
    private int memorize_used;

    /**
     * 以单节点方式存放的虚拟机编号集合
     */
    private Set<Integer> virtualNoSet;

    public Node(Node n) {
        this.cores = n.getCores();
        this.memorize = n.getMemorize();
        this.cores_used = n.getCores_used();
        this.memorize_used = n.getMemorize_used();
        this.virtualNoSet = new HashSet<>(n.getVirtualNoSet());
    }


    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getMemorize() {
        return memorize;
    }

    public void setMemorize(int memorize) {
        this.memorize = memorize;
    }

    public int getCores_used() {
        return cores_used;
    }

    public void setCores_used(int cores_used) {
        this.cores_used = cores_used;
    }

    public int getMemorize_used() {
        return memorize_used;
    }

    public void setMemorize_used(int memorize_used) {
        this.memorize_used = memorize_used;
    }

    public Set<Integer> getVirtualNoSet() {
        return virtualNoSet;
    }

    public void setVirtualNoSet(Set<Integer> virtualNoSet) {
        this.virtualNoSet = virtualNoSet;
    }

    /**
     * 服务器节点构造函数
     *
     * @param cores    服务器节点当前拥有的CPU核数
     * @param memorize 服务器节点当前拥有的内存大小
     */
    public Node(int cores, int memorize) {
        this.cores = cores;
        this.memorize = memorize;
        this.cores_used = 0;
        this.memorize_used = 0;
        this.virtualNoSet = new HashSet<>();
    }

    /**
     * 进行资源分配
     *
     * @param cores    CPU资源（释放资源则为负数）
     * @param memorize 内存资源（释放资源则为负数）
     * @param id       单节点虚拟机编号（双节点存放时为null）
     */
    public void allocate(int cores, int memorize, Integer id) {
        this.cores -= cores;
        this.memorize -= memorize;
        this.cores_used += cores;
        this.memorize_used += memorize;
        if (id != null) {
            if (cores < 0) this.virtualNoSet.remove(id);
            else this.virtualNoSet.add(id);
        }
    }

    /**
     * 当前节点是否存在某虚拟机（仅查询单节点方式存放的虚拟机）
     *
     * @param id 虚拟机编号
     * @return 若存在，返回true；若不存在，返回false
     */
    public boolean contain(Integer id) {
        return this.virtualNoSet.contains(id);
    }

}

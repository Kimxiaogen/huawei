package schedule.model;

/**
 * 服务器节点
 * @author Kim小根
 * @date 2021/3/11 10:25
 * <p>Description:实现服务器节点的主要功能</p>
 */
public class Node {

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

    /**
     * 服务器节点构造函数
     * @param cores 服务器节点当前拥有的CPU核数
     * @param memorize 服务器节点当前拥有的内存大小
     */
    public Node(int cores, int memorize) {
        this.cores = cores;
        this.memorize = memorize;
    }
}

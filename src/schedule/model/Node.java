package schedule.model;

/**
 * 服务器节点
 *
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
    }

    /**
     * 进行资源分配
     *
     * @param cores    CPU资源（释放资源则为负数）
     * @param memorize 内存资源（释放资源则为负数）
     */
    public void allocate(int cores, int memorize) {
        this.cores -= cores;
        this.memorize -= memorize;
        this.cores_used += cores;
        this.memorize_used += memorize;
    }
}

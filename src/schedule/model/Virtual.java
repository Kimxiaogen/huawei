package schedule.model;

/**
 * 虚拟机模型
 *
 * @author Kim小根
 * @date 2021/3/11 10:19
 * <p>Description:实现虚拟机的主要功能</p>
 */
public class Virtual {
    /**
     * 最大虚拟机型号长度
     */
    private static final int MAX_LEN_OF_TYPE = 20;

    /**
     * 虚拟机ID
     */
    private int id;

    /**
     * 虚拟机型号
     */
    private String type;

    /**
     * 虚拟机拥有的CPU核数
     */
    private int cores;

    /**
     * 虚拟机拥有的内存大小
     */
    private int memorize;

    /**
     * 是否为双节点部署，true表示双节点部署，false表示单节点部署
     */
    private boolean isDoubleNodes;

    /**
     * 虚拟机当前所在服务器编号
     */
    private int no;

    /**
     * 虚拟机部署于服务器哪个节点（单节点属性）（允许参数 “A” or “B”）
     */
    private char node;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isDoubleNodes() {
        return isDoubleNodes;
    }

    public void setDoubleNodes(boolean doubleNodes) {
        isDoubleNodes = doubleNodes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public char getNode() {
        return node;
    }

    public void setNode(char node) {
        this.node = node;
    }

    /**
     * 虚拟机构造函数
     *
     * @param type          虚拟机型号
     * @param cores         虚拟机拥有的CPU核数
     * @param memorize      虚拟机拥有的内存大小
     * @param isDoubleNodes 是否为双节点部署，1（true）表示双节点部署，2（false）表示单节点部署
     */
    public Virtual(String type, int cores, int memorize, int isDoubleNodes) {
        this.type = type;
        this.cores = cores;
        this.memorize = memorize;
        this.isDoubleNodes = isDoubleNodes == 1;
    }
}

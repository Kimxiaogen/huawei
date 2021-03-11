package schedule.model;

/**
 * 服务器模型
 *
 * @author Kim小根
 * @date 2021/3/11 10:13
 * <p>Description:实现服务器主要功能</p>
 */
public class Server {

    /**
     * CPU最大核数
     */
    private static final int MAX_CPU_CORES = 1024;
    /**
     * 最大硬件成本
     */
    private static final int MAX_COST_OF_DEVICES = 500000;
    /**
     * 最大每日能耗成本
     */
    private static final int MAX_COST_OF_ENERGY = 5000;
    /**
     * 最大服务器型号长度
     */
    private static final int MAX_LEN_OF_TYPE = 20;

    /**
     * 服务器型号
     */
    private String type;

    /**
     * 服务器节点A
     */
    private Node node_A;

    /**
     * 服务器节点B
     */
    private Node node_B;

    /**
     * 服务器硬件成本（一次性购入成本）
     */
    private int cost_of_devices;

    /**
     * 服务器每日能耗成本
     */
    private int cost_of_energy;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node getNode_A() {
        return node_A;
    }

    public void setNode_A(Node node_A) {
        this.node_A = node_A;
    }

    public Node getNode_B() {
        return node_B;
    }

    public void setNode_B(Node node_B) {
        this.node_B = node_B;
    }

    public int getCost_of_devices() {
        return cost_of_devices;
    }

    public void setCost_of_devices(int cost_of_devices) {
        this.cost_of_devices = cost_of_devices;
    }

    public int getCost_of_energy() {
        return cost_of_energy;
    }

    public void setCost_of_energy(int cost_of_energy) {
        this.cost_of_energy = cost_of_energy;
    }

    /**
     * 服务器构造函数
     *
     * @param type            服务器型号
     * @param cores           服务器拥有的CPU核数
     * @param memorize        服务器拥有的内存大小
     * @param cost_of_devices 服务器硬件成本（一次性购入成本）
     * @param cost_of_energy  服务器每日能耗成本
     */
    public Server(String type, int cores, int memorize, int cost_of_devices, int cost_of_energy) {
        int half_cores = cores / 2, half_memorize = memorize / 2;
        this.type = type;
        this.node_A = new Node(half_cores, half_memorize);
        this.node_B = new Node(half_cores, half_memorize);
        this.cost_of_devices = cost_of_devices;
        this.cost_of_energy = cost_of_energy;
    }
}

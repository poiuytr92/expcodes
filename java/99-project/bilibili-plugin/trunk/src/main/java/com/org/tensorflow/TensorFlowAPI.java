package com.org.tensorflow;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.Session.Runner;
import org.tensorflow.Tensor;

/**
 * <PRE>
 * TensorFlow深度学习训练模型调用接口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-04
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TensorFlowAPI {
    
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(TensorFlowAPI.class);
	
    /** 已训练好的PB文件模型 */
    private final String pbModelFilePath;
    
    /** PB训练模型(TensorFlow用数据流图表示模型) */
    private final Graph graph;
    
    /** TensorFlow会话 */
    private final Session session;
    
    /** TensorFlow执行器 */
    private Runner runner;
    
    /** feedNames */
    private List<String> feedNames = new ArrayList<String>();
    
    /** feedTensors */
    private List<Tensor<?>> feedTensors = new ArrayList<Tensor<?>>();
    
    /** fetchNames */
    private List<String> fetchNames = new ArrayList<String>();
    
    /** fetchTensors */
    private List<Tensor<?>> fetchTensors = new ArrayList<Tensor<?>>();
    
    /** runStats */
    private RunStats runStats;
    
    /**
     * 构造函数
     * @param pbModelFilePath 已训练好的PB模型文件路径
     */
    public TensorFlowAPI(String pbModelFilePath) {
        this.pbModelFilePath = pbModelFilePath;
        this.graph = loadGraph(pbModelFilePath);
        this.session = new Session(graph);
        this.runner = session.runner();
    }
    
    /**
     * 加载TensorFlow训练模型
     * @param pbModelFilePath 已训练好的PB模型文件路径
     * @return
     */
    private Graph loadGraph(String pbModelFilePath) {
    	Graph graph = new Graph();
        try {
            byte[] graphDef = Files.readAllBytes(Paths.get(pbModelFilePath));
            graph.importGraphDef(graphDef);
            
        } catch (Exception e) {
            log.error("加载TensorFlow训练模型失败: {}", pbModelFilePath, e);
        }
        return graph;
    }
    
    /**
     * 获取TensorFlow训练模型
     * @return Graph
     */
    public final Graph graph() {
        return graph;
    }
    
    /**
     * 提取模型中所有张量节点的名称和类型
     * @return Map: name->type
     */
    public Map<String, String> listAllNodes() {
    	Map<String, String> nodes = new HashMap<String, String>();
        Iterator<Operation> ops = graph.operations();
        while(ops.hasNext()) {
        	Operation op = ops.next();
        	nodes.put(op.name(), op.type());
        }
        return nodes;
    }
    
    /**
     * 在先前注册的输入节点之间进行推理（通过*）
*和请求的输出节点。然后，输出节点可以用
*获取*方法。
应该由推理填充的输出节点列表
*通过。
     * Runs inference between the previously registered input nodes (via feed*)
     * and the requested output nodes. Output nodes can then be queried with the
     * fetch* methods.
     *
     * @param outputNames
     *            A list of output nodes which should be filled by the inference
     *            pass.
     */
    public void run(String[] outputNames) {
        run(outputNames, false);
    }
    
    /**
     * Runs inference between the previously registered input nodes (via feed*)
     * and the requested output nodes. Output nodes can then be queried with the
     * fetch* methods.
     *
     * @param outputNames
     *            A list of output nodes which should be filled by the inference
     *            pass.
     * @param enableStats enableStats
     */
    public void run(String[] outputNames, boolean enableStats) {
        // Release any Tensors from the previous run calls.
        closeFetches();
        
        // Add fetches.
        for (String o : outputNames) {
            fetchNames.add(o);
            TensorIndex ti = TensorIndex.parse(o);
            runner.fetch(ti.NAME(), ti.IDX());
        }
        
        // Run the session.
        try {
            if (enableStats) {
                Session.Run r = runner.setOptions(RunStats.RUN_OPTIONS()).runAndFetchMetadata();
                fetchTensors = r.outputs;
                
                if (runStats == null) {
                    runStats = new RunStats();
                }
                runStats.add(r.metadata);
            } else {
                fetchTensors = runner.run();
            }
        } catch (RuntimeException e) {
            // Ideally the exception would have been let through, but since this
            // interface predates the
            // TensorFlow Java API, must return -1.
            System.out.println("Failed to run TensorFlow inference with inputs:[" + feedNames + "], outputs:["
                + fetchNames + "]");
            throw e;
        } finally {
            // Always release the feeds (to save resources) and reset the
            // runner, this run is
            // over.
            closeFeeds();
            runner = session.runner();
        }
    }
    
    
    
    /**
     * 方法说明
     * 
     * @param inputName 参数
     * @param src 参数
     * @param dims 参数
     */
    public void feed(String inputName, float[] src, long... dims) {
        addFeed(inputName, Tensor.create(dims, FloatBuffer.wrap(src)));
    }
    
    /**
     * 方法说明
     * @param inputName 参数
     * @param src 参数
     */
    public void feed(String inputName, byte[] src) {
        addFeed(inputName, Tensor.create(src));
    }
    
    /**
     * 方法说明
     * 
     * @param inputName 参数
     * @param t 参数
     */
    private void addFeed(String inputName, Tensor t) {
        // The string format accepted by TF is
        // node_name[:output_index].
        TensorIndex ti = TensorIndex.parse(inputName);
        runner.feed(ti.NAME(), ti.IDX(), t);
        feedNames.add(inputName);
        feedTensors.add(t);
    }
    
    /**
     * 方法说明
     * 
     * @param outputName 参数
     * @param dst 参数
     */
    public void fetch(String outputName, float[] dst) {
        fetch(outputName, FloatBuffer.wrap(dst));
    }
    
    /**
     * 方法说明
     * 
     * @param outputName 参数
     * @param dst 参数
     */
    public void fetch(String outputName, FloatBuffer dst) {
        getTensor(outputName).writeTo(dst);
    }
    
    /**
     * 方法说明
     * 
     * @param outputName 参数
     * @return 参数
     */
    private Tensor getTensor(String outputName) {
        int i = 0;
        for (String n : fetchNames) {
            if (n.equals(outputName)) {
                return fetchTensors.get(i);
            }
            ++i;
        }
        throw new RuntimeException("Node '" + outputName + "' was not provided to run(), so it cannot be read");
    }
    
    /**
     * 方法说明
     * 
     * @param operationName 参数
     * @return 参数
     */
    public Operation graphOperation(String operationName) {
        final Operation operation = graph.operation(operationName);
        if (operation == null) {
            throw new RuntimeException("Node '" + operationName + "' does not exist in model '" +  pbModelFilePath+ "'");
        }
        return operation;
    }
    
    /**
     * Returns the last stat summary string if logging is enabled.
     * 
     * @return 返回
     */
    public String getStatString() {
        return (runStats == null) ? "" : runStats.summary();
    }
    
    /**
     * Cleans up the state associated with this Object. initializeTensorFlow()
     * can then be called again to initialize a new session.
     */
    public void close() {
        closeFeeds();
        closeFetches();
        session.close();
        graph.close();
        if (runStats != null) {
            runStats.close();
        }
        runStats = null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
    
    /**
     * 方法说明 参数
     */
    private void closeFeeds() {
        for (Tensor t : feedTensors) {
            t.close();
        }
        feedTensors.clear();
        feedNames.clear();
    }
    
    /**
     * 方法说明 参数
     */
    private void closeFetches() {
        for (Tensor t : fetchTensors) {
            t.close();
        }
        fetchTensors.clear();
        fetchNames.clear();
    }
}

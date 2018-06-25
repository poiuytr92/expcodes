package exp.libs.algorithm.dl.tensorflow;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.Session.Run;
import org.tensorflow.Session.Runner;
import org.tensorflow.Tensor;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.ListUtils;

/**
 * <PRE>
 * TensorFlow深度学习训练模型调用接口
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-03-04
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TensorFlowAPI {
    
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(TensorFlowAPI.class);
	
    /** PB训练模型(TensorFlow用数据流图表示模�?) */
    private final Graph graph;
    
    /** TensorFlow会话 */
    private final Session session;
    
    /** TensorFlow执行�? */
    private Runner runner;
    
    /** TensorFlow输入张量�? */
    private Map<String, Tensor<?>> feedTensors;
    
    /** TensorFlow输出张量�? */
    private Map<String, Tensor<?>> fetchTensors;
    
    /** 调试模式 */
    private boolean debug;
    
    /** 运行日志统计�? */
    private RunStats runStats;
    
    /**
     * 构造函�?
     * @param pbModelFilePath 已训练好的PB模型文件路径
     */
    public TensorFlowAPI(String pbModelFilePath) {
    	this(pbModelFilePath, false);
    }
    
    /**
     * 构造函�?
     * @param pbModelFilePath 已训练好的PB模型文件路径
     * @param debug 是否启动调试模式：执行运行日志统�? (需调用本地化接口，建议为false)
     */
    public TensorFlowAPI(String pbModelFilePath, boolean debug) {
        this.graph = loadGraph(pbModelFilePath);
        this.session = new Session(graph);
        this.runner = session.runner();
        
        this.feedTensors = new HashMap<String, Tensor<?>>();
        this.fetchTensors = new HashMap<String, Tensor<?>>();
        
        if(debug == true) {
        	this.debug = true;
        	this.runStats = new RunStats();
        }
    }
    
    /**
     * 加载TensorFlow训练模型
     * @param pbModelFilePath 已训练好的PB模型文件路径
     * @return
     */
    private Graph loadGraph(String pbModelFilePath) {
    	Graph graph = new Graph();
        try {
        	File pbModelFile = new File(pbModelFilePath);
            byte[] graphDef = FileUtils.readFileToByteArray(pbModelFile);
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
    public final Graph getGraphDef() {
        return graph;
    }
    
    /**
     * 获取TensorFlow模型图的操作节点
     * @param operationName 操作节点名称
     * @return 操作节点(若不存在返回null)
     */
    public Operation getGraphOperation(String operationName) {
        return graph.operation(operationName);
    }
    
    /**
     * 提取模型中所有张量节点的名称和类�?
     * @return Map: name->type
     */
    public Map<String, String> listAllShapes() {
    	Map<String, String> nodes = new HashMap<String, String>();
        Iterator<Operation> shapes = graph.operations();
        while(shapes.hasNext()) {
        	Operation shape = shapes.next();
        	nodes.put(shape.name(), shape.type());
        }
        return nodes;
    }
    
    /**
     * 设置输入张量的�?
     * @param inputName 输入张量的名�?, 格式�? name:index (若无index则默认为0)
     * @param datas 输入张量的值（降维�?1维矩阵的数据�?
     * @param dims 输入张量的原矩阵维度列表
     */
    public void feed(String inputName, float[] datas, long... dims) {
        addFeed(inputName, Tensor.create(dims, FloatBuffer.wrap(datas)));
    }
    
    /**
     * 设置输入张量的�?
     * @param inputName 输入张量的名�?, 格式�? name:index (若无index则默认为0)
     * @param datas 输入张量的值（降维�?1维矩阵的数据�?
     */
    public void feed(String inputName, byte[] datas) {
        addFeed(inputName, Tensor.create(datas));
    }
    
    /**
     * 添加输入张量
     * @param inputName 输入张量的名�?, 格式�? name:index (若无index则默认为0)
     * @param tensor 输入张量对象
     */
	private void addFeed(String inputName, Tensor<?> tensor) {
    	feedTensors.put(inputName, tensor);
    	
        TensorIndex ti = TensorIndex.parse(inputName);
        runner.feed(ti.NAME(), ti.IDX(), tensor);
    }
    
    /**
     * 运行TensorFlow模型
     * @param outputNames 输出张量的名称列�?, 单个张量名称格式�? name:index (若无index则默认为0)
     * @return 是否运行成功
     */
    public boolean run(String... outputNames) {
    	boolean isOk = false;
    	if(ListUtils.isEmpty(outputNames)) {
    		return isOk;
    	}
    	
    	// 关闭上次运行模型时声明的输出张量
        closeFetches();
        
        // 注册输出张量名称
        for(String outputName : outputNames) {
            TensorIndex ti = TensorIndex.parse(outputName);
            runner.fetch(ti.NAME(), ti.IDX());
        }
        
        // 运行TensorFlow模型
        try {
        	List<Tensor<?>> tensors = null;
            if(debug == true) {
                Run run = runner.setOptions(RunStats.RUN_OPTIONS()).runAndFetchMetadata();
                tensors = run.outputs;
                runStats.add(run.metadata);	
                
            } else {
            	tensors = runner.run();
            }
            
            // 记录得到的所有输出张�?
            for(int i = 0; i < outputNames.length; i++) {
            	String outputName = outputNames[i];
            	Tensor<?> tensor = tensors.get(i);
            	fetchTensors.put(outputName, tensor);
            }
            isOk = true;
            
        } catch(Exception e) {
        	log.error("运行TensorFlow模型失败.\r\n输入张量列表: {}\r\n输出张量列表: {}", 
        			feedTensors.keySet(), Arrays.asList(outputNames), e);
        	
        // 重置模型执行�?
        } finally {
            closeFeeds();
            runner = session.runner();
        }
        return isOk;
    }
    
    /**
     * 获取运行日志统计概要.
     * 	需在执行{@link run}方法时打开debug开�?
     * @return 运行日志统计概要
     */
    public String getRunlog() {
        return (debug ? "" : runStats.summary());
    }
    
    /**
     * 获取输出张量的�?
     * @param outputName 输出张量的名�?, 格式�? name:index (若无index则默认为0)
     * @return 输出张量的值（降维�?1维矩阵的数据�?
     */
    public float[] fetch(String outputName) {
    	
		// 提取输出张量的节�?
		Operation op = getGraphOperation(outputName);
		
		// 获取输出张量降维到一维后的矩阵维�?
		TensorIndex ti = TensorIndex.parse(outputName);
		final int dimension = (int) op.output(ti.IDX()).
				shape().size(1);	// FIXME 输出张量最外层默认有一个空维度, 因此不取size(0)
		
		// 存储输出张量的矩阵数�?
		float[] output = new float[dimension];
		FloatBuffer buffer = FloatBuffer.wrap(output);
		Tensor<?> tensor = fetchTensors.get(outputName);
    	if(tensor != null) {
    		tensor.writeTo(buffer);
    	}
        return output;
    }
    
    /**
     * 清理并关闭TensorFlow模型
     */
    public void close() {
        closeFeeds();
        closeFetches();
        session.close();
        graph.close();
        
        if(debug == true) {
        	runStats.close();
        }
    }
    
    /**
     * 关闭所有输入张�?
     */
    private void closeFeeds() {
    	Iterator<Tensor<?>> tensors = feedTensors.values().iterator();
    	while(tensors.hasNext()) {
    		tensors.next().close();
        }
        feedTensors.clear();
    }
    
    /**
     * 关闭所有输出张�?
     */
    private void closeFetches() {
    	Iterator<Tensor<?>> tensors = fetchTensors.values().iterator();
    	while(tensors.hasNext()) {
    		tensors.next().close();
        }
    	fetchTensors.clear();
    }
    
}

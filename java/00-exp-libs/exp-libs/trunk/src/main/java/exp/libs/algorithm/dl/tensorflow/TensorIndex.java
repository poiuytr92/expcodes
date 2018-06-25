package exp.libs.algorithm.dl.tensorflow;

import exp.libs.utils.num.NumUtils;

/**
 * <PRE>
 * 张量索引
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-03-04
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class TensorIndex {
    
    /** 张量名称 */
    private String name;
    
    /** 张量索引（默认为0�? */
    private int index;
    
    /**
     * 构造函�?
     */
    private TensorIndex() {
    	this.name = "";
    	this.index = 0;
    }
    
    /**
     * 获取张量名称
     * @return 张量名称
     */
    public String NAME() {
    	return name;
    }
    
    /**
     * 获取张量索引
     * @return 张量索引
     */
    public int IDX() {
    	return index;
    }
    
    /**
     * 解析张量名称�? 将其拆分�? name和index两部�?
     * 	若张量名称中不存在index，则index取默认值为0
     * @param tensorName 张量名称, 格式�? name:index
     * @return
     */
    protected static TensorIndex parse(String tensorName) {
        TensorIndex ti = new TensorIndex();
        int pos = tensorName.lastIndexOf(':');
        if (pos < 0) {
        	ti.name = tensorName;
        	ti.index = 0;
        	
        } else {
        	ti.name = tensorName.substring(0, pos);
        	ti.index = NumUtils.toInt(tensorName.substring(pos + 1), 0);
        }
        return ti;
    }
}

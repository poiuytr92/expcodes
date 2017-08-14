package exp.libs.mrp.api;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <PRE>
 * 项目发布插件:Maven调用入口 - clean功能部分。
 * </PRE>
 * 
 * <B>项 目：</B>凯通J2SE开发平台(KTJSDP) 
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * 
 * @version 1.0 2017-08-14
 * @author EXP：272629724@qq.com
 * @since jdk版本：jdk1.6
 * 
 * @goal clean
 * @requiresDependencyResolution runtime
 * @execute phase= "clean"
 */
public class MvnCleanMojo extends org.apache.maven.plugin.AbstractMojo {

	public MvnCleanMojo() {}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		
	}

}

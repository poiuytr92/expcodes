<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":"
            + request.getServerPort() + path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>   
		<base href="<%=basePath%>">
		<title></title>
		<link rel="stylesheet" type="text/css" href="<%=basePath%>uploadify/uploadify.css">
		<script type="text/javascript" src="<%=basePath%>uploadify/jquery.js"></script>
		<script type="text/javascript" src="<%=basePath%>uploadify/swfobject.js"></script>
		<script type="text/javascript" src="<%=basePath%>uploadify/jquery.uploadify.v2.0.1.js"></script>
		
		<link href="<%=basePath%>/jqueryLiger/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
    	<link href="<%=basePath%>/jqueryLiger/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
    
		<script src="<%=basePath%>/jqueryLiger/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
		<script src="<%=basePath%>/jqueryLiger/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
		
		<script type="text/javascript">
    
        var idListStr = "";
		
        function deleteFile(filename) {
      		$.ajax({
                type: 'post', cache: false, dataType: 'json',
                url: 'upload!delete.action',
                data: [
                { name: 'filename', value: filename }
                ],
                success: function (obj)
                {
                	if(1==obj.suc){        		
                		$('#upload').uploadifyClearQueue();
                	}
                }
            });
        }
        
        $(function ()
        {
        	$('#upload').uploadify({
            	   onSelect :function(event,ID,fileObj){
            		  
                },
                'uploader'       : '<%=basePath%>/uploadify/uploadify.swf',
                'script'         : '<%=basePath%>/resource!upload.action;jsessionid=<%=session.getId()%>',
                'cancelImg'      : '<%=basePath%>/uploadify/cancel.png',
                'folder'         : '/uploads',     	
                'auto'           : true, //是否自动开始  
                'multi'          : true, //是否支持多文件上传  
                //'buttonText'     : 'BROWSE', //按钮上的文字  
                'simUploadLimit' : 1, //一次同步上传的文件数目  
                'sizeLimit'      : 204800000000000, //设置单个文件大小限制，单位为byte  
                'queueSizeLimit' : 5, //队列中同时存在的文件个数限制  
                'fileDataName'   : 'uploadify',
                'displayData'    : 'percentage',
                //.jpg .gif .jpeg .png .bmp
                'fileDesc'       : '请选择文件', //如果配置了以下的'fileExt'属性，那么这个属性是必须的  
                //'*.jpg;*.gif;*.jpeg;*.png;*.bmp'
                'fileExt'        : '*.*', //允许的格式
                //'height'         : 34,   //设置浏览按钮的宽度 ，默认值：110
                'width'          : 570,//设置浏览按钮的高度 ，默认值：30。 
                'buttonImg'      : '<%=basePath%>/uploadify/ud.png',
                'simUploadLimit' : 1, //允许同时上传的个数 默认值：1 。 
                'wmode'          : 'transparent' ,              
                onComplete       : function (event, queueID, fileObj, response, data){
             	   
             	   response = eval('('+response+')'); //将字符串为JSON对象
             	   if(1 == response.suc) {
             		  idListStr += response.id + ":";
             	   }
                },  
                    
                onAllComplete    : function (event, data){ 
                	$.ajax({
	                    type: 'post', cache: false, dataType: 'json',
	                    url: 'resource!updateResourceList.action',
	                    data: [
	                    { name: 'idListStr', value: idListStr },
	                    { name: 'sort', value: parent.fileSort }
	                    ],
	                    success: function (obj)
	                    {
	                    	if (obj.suc == 1) 
	                        {
	                    		idListStr = "";
	                    		//$.ligerDialog.success("成功");
	                    		parent.refresh();
	                        } else
	                        {
	                        	$.ligerDialog.error("失败");
	                        }
	                    },
	                    error: function ()
	                    {
	                        alert('系统错误');
	                    },
	                    beforeSend: function ()
	                    {
	                        //$.ligerDialog.waitting("正在读取数据,请稍后...");
	                    },
	                    complete: function ()
	                    {
	                        //$.ligerDialog.closeWaitting();
	                    }
	                });
                	
                },
                
                onError          : function(event, queueID, fileObj){ 
                    alert("文件:" + fileObj.name + " 上传失败");
                }
                
           });
            
       });
        
    </script>
    <style type="text/css">
           
    </style>

</head>

<body>

    <div style="display:inline;float:left">
			<!-- <input type="button" name="uploadify" id="upload"> 
			<a id="upload" style="display:inline;"></a>
			<a>hello</a>
			<img src="uploadify/start_upload.png" />
			<img src="uploadify/space.png" />
			
			<a href="javascript:$('#upload').uploadifyUpload()">上传</a>|         
			<a href="javascript:$('#upload').uploadifyClearQueue()">取消上传</a>      
			
			-->
			<input type="file" name="uploadify" id="upload" />      
        	
	</div>
</body>
</html>
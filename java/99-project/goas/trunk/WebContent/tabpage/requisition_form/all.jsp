﻿<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title></title>
    <link href="../../jqueryLiger/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="../../jqueryLiger/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" /> 
    <script src="../../jqueryLiger/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>   
	<script src="../../jqueryLiger/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
	<script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerWindow2.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
    <script type="text/javascript">

    	var grid = null;
    
        $(function () {
        	menu = $.ligerMenu({ width: 120, items: [
					{ text: '删除', click: onClickDelUser, icon: 'delete' },
					{ line: true },
					{ text: '刷新', click: refresh, icon:'refresh'}]
        	});
            createGrid();
      	});
        
        function createGrid()
        {
        	grid = $("#maingrid").ligerGrid({
                columns: [
                { display: '申请序号', name: 'id',  width: '8%'},
                { display: '类型', name: 'sort',   width: '10%' },
                { display: '提交日期', name: 'date',width: '12%' },
                { display: '状态', name: 'status', width: '20%' },
                { display: '内容', name: 'content',width: '40%' },
                ],
                width:'99%',
                height:'99%',
                rownumbers:true,
                usePager:false,
                allowUnSelectRow:true,
                alternatingRow: true,
                onContextmenu : function (parm, e)
                {
                	if (grid.getSelectedRows().length)
                    	menu.show({ top: e.pageY, left: e.pageX });
                	else
                		alert("请左键选择一条项目再单击右键");
                    return false;
                },
                groupColumnName:'sort',
                groupRender: function (sort, groupdata)
                {
                    return '类型： ' + sort;
                },

                url: 'job!showOnesAll.action'
            });	
            
        }
        
        function refresh() {
        	grid.loadData();
        }
        
        function onClickDelUser(item) {
        	
        	$.ligerDialog.confirm('确定删除?', function(yes) {
    			if (yes) {

	          		$.ajax({
	                  type: 'post', cache: false, dataType: 'json',
	                  url: 'job!del.action',
	                  data: [
	                  { name: 'id', value: grid.getSelectedRows()[0].id }
	                  ],
	                  success: function (obj)
	                  {
	                  	if (obj.suc == 1) 
	                      {
	                  		$.ligerDialog.success("删除成功");
	                  		refresh();
	                      } else
	                      {
	                      	$.ligerDialog.error("删除失败，只能删除未审核申请。");
	                      }
	                  },
	                  error: function ()
	                  {
	                      alert('系统错误');
	                  },
	                  beforeSend: function ()
	                  {
	                      //$.ligerDialog.waitting("正在读取数据,请稍后...");
	                      //$("#btnLogin").attr("disabled", true);
	                  },
	                  complete: function ()
	                  {
	                      //$.ligerDialog.closeWaitting();
	                      //$("#btnLogin").attr("disabled", false);
	                  }
	              	});
    			}
        	});
        }
 </script> 
 
 <style type="text/css"> 
 </style>
 
</head>
<body style="padding:0px; margin:0px">  
      <div id="maingrid"></div>
</body>
</html>

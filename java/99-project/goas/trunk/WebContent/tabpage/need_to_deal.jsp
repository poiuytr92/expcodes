<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title></title>
    <link href="../jqueryLiger/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="../jqueryLiger/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" /> 
    <script src="../jqueryLiger/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>   
	<script src="../jqueryLiger/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
	<script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerWindow2.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="../jqueryLiger/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
    <script type="text/javascript">

    	var grid = null;
    
        $(function () {
        	menu = $.ligerMenu({ width: 120, items: [
					{ text: '审核', click: onClickAudit, icon: 'add' },
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
                { display: '提交人', name: 'name',   width: '10%' },
                { display: '提交日期', name: 'date',width: '12%' },
                { display: '状态', name: 'status', width: '20%' },
                { display: '剩余审核部門', name: 'workflow',width: '20%' },
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

                url: 'job!showNeedDeal.action'
            });	
            
        }
        
        function refresh() {
        	grid.loadData();
        }
        function onClickAudit(item) {
        	var auditing_id = grid.getSelectedRows()[0].id;
        	$.ligerDialog.open({ 
        			url: '../Dialog/audit_dialog.jsp?id=' + auditing_id,
        			height: 430, width: 380,
        			buttons:[
        				{ text: '通过', onclick: function (item, dialog) { audit_pass(auditing_id);dialog.close(); } },
        				{ text: '驳回', onclick: function (item, dialog) { audit_ban(auditing_id);dialog.close(); } },
						{ text: '取消审核', onclick: function (item, dialog) { dialog.close(); } }
        			]
        	});
        }
        
        function audit_pass(adid) {
        	$.ajax({
                type: 'post', cache: false, dataType: 'json',
                url: 'job!audit.action',
                data: [{ name: 'id', value: adid }],
                success: function (obj){
                	if (obj.suc == 1) {
                		$.ligerDialog.success("审核成功");
                    } else{
                    	$.ligerDialog.error("审核失败");
                    }
                	refresh();
                },
                error: function (){
                    alert('系统错误');
                },
                beforeSend: function (){
                    $.ligerDialog.waitting("请稍候...");
                },
                complete: function (){
                    $.ligerDialog.closeWaitting();
                }
            });
        }
		function audit_ban(adid) {
			$.ajax({
                type: 'post', cache: false, dataType: 'json',
                url: 'job!ban.action',
                data: [{ name: 'id', value: adid },],
                success: function (obj){
                	if (obj.suc == 1) {
                		$.ligerDialog.success("驳回成功");
                    } else{
                    	$.ligerDialog.error("驳回失败");
                    }
                	refresh();
                },
                error: function (){
                    alert('系统错误');
                },
                beforeSend: function (){
                    $.ligerDialog.waitting("请稍候...");
                },
                complete: function (){
                    $.ligerDialog.closeWaitting();
                }
            });
        }
 </script> 
 
</head>
<body style="padding:0px; margin:0px">  
      <div id="maingrid"></div>
</body>
</html>

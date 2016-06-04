<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

    <title>money</title>
    <link type="text/css" href="../../jqueryLiger/lib/jqueryUI/css/flick/jquery-ui-1.8.24.custom.css" rel="stylesheet" />
    <link href="../../jqueryLiger/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="../../jqueryLiger/lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />  
    <link rel="stylesheet" href="../../css/base.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="../../css/form.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="../../jqueryLiger/lib/jqueryValid/validationEngine.jquery.css" type="text/css"/>
    
    <script type="text/javascript" src="../../jqueryLiger/lib/jquery/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="../../jqueryLiger/lib/jqueryUI/js/jquery-ui-1.8.23.custom.min.js"></script>
    <!-- liger -->
    <script src="../../jqueryLiger/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerRadio.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerSpinner.js" type="text/javascript"></script>
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script> 
    <script src="../../jqueryLiger/lib/ligerUI/js/plugins/ligerTip.js" type="text/javascript"></script>
	<!-- validation -->
	<script src="../../jqueryLiger/lib/jqueryValid/jquery.validationEngine-zh_CN.js" type="text/javascript" charset="utf-8"></script>
	<script src="../../jqueryLiger/lib/jqueryValid/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>


    <script type="text/javascript" charset="utf-8">
    	$(function () {
    		$("#form-form").validationEngine();
    		
    		// submit button
    		$("#inp-btn").click( function (){
    			if( $('#form-form').validationEngine('validate') ) {
    				$.ajax({
                        type: 'post', cache: false, dataType: 'json',
                        url: 'job!add.action',
                        data: [
	                        { name: 'name', value: '<%=session.getAttribute("name")%>' },
	                        { name: 'user_id', value: '<%=session.getAttribute("user_id")%>' },
	                        { name: 'sort', value: "经费" },
	                        { name: 'content', value: '经费：'+$('#inp-cash').val()+'元~用途：'+$("#inp-reason").val() },
	                        { name: 'status', value: '未审核' },
	                        { name: 'workflow', value: $('#inp-dept').val()+',完成' }
                        ],
                        success: function (obj) {
                            if (obj.suc == 0) {
                                alert('登記失敗，請檢查填寫信息!');
                                return;
                            } else {
                            	$.ligerDialog.success('提交成功！');
                            	return;
                            }
                        },
                        error: function () {
                            alert('系统错误,请联系管理员！');
                        },
                        beforeSend: function () {
                            $.ligerDialog.waitting("正在提交中,请稍候...");
                        },
                        complete: function () {
                            $.ligerDialog.closeWaitting();
                        }
                    });
    			}
            });
    	});
    </script>
</head>
<body>
    <div id="form">
      <form name="holidayForm" id="form-form">
    	<div id='head'>
    		<span><img src="../../jqueryLiger/lib/ligerUI/skins/icons/communication.gif" /></span>
    		<span>经费申请表</span>
    	</div>
    	
    	<!-- base -->
    	<div class="field">
    		<div class="field-head">基本信息</div>
    		<div class="field-con">
    			<span>请假人：</span>
    			<input name="name" type="text" id="inp-name" class='inp' value='<%=session.getAttribute("name")%>' disabled="disabled"/>
    		</div>
    		<div class="field-con">
    			<span>审批部门：</span>
    			<input name="name" type="text" id="inp-dept" class='inp' value='<%=session.getAttribute("dept")%>,财务部' disabled="disabled"/>
    		</div>
       	</div>
       	
       	<!-- timing -->
       	<div class="field">
    		<div class="field-head">内容</div>
    		<div class="field-con">
    			<span>经 费：</span>
    			<input type="text" placeholder="100" id="inp-cash" class='inp validate[required]' />
    		</div>
    		<div class="field-con">
    			<span>用 途：</span>
    			<input name="name" type="text" id="inp-reason" class='inp validate[required]' />
    		</div>
       	</div>
       	
       	<div class='field-btn'>
       		<input type="button" value="提交" id="inp-btn" />
       	</div>
      </form>
    </div>
</body>
</html>


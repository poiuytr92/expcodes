<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>Audit</title>
    <link rel="stylesheet" href="../css/base.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="../css/dialog.css" type="text/css" media="screen" />
    <script type="text/javascript" src="../jqueryLiger/lib/jquery/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="../jqueryLiger/lib/jqueryUI/js/jquery-ui-1.8.23.custom.min.js"></script>

    <script type="text/javascript" charset="utf-8">
    	$(function () {
    		$.ajax({
                 type: 'post', cache: false, dataType: 'json',
                 url: 'job!showOne.action',
                 data: [
                  { name: 'id', value: '<%=request.getParameter("id") %>' },
                 ],
                 success: function (obj) {
                   	$('#sort-span').html(obj.sort);
                   	$('#name-span').html(obj.name);
                   	$('#dept-span').html(obj.dept);
                   	$('#create-time-span').html(obj.date.slice(0,10));
                   	
                   	var cont = '<div class="field-con"><span class="field-ccc">' +
           			obj.content.replace(/~/g,'</span></div><div class="field-con"><span class="field-ccc">') 
           			+ '</span></div>';
           			
                   	$('#content-span').html( cont.replace(/：/g, '：</span><span class="cont">') );
                   	return;
                 },
                 error: function () {
                     alert('系统错误,请联系管理员！');
                 }
        	});
    	});
    </script>
</head>
<body>
    <div id="form">
    	<div id='head'>
    		<span><img src="../jqueryLiger/lib/ligerUI/skins/icons/communication.gif" /></span>
    		<span id='sort-span'>NOVALUE</span> <span> ID: <%=request.getParameter("id") %></span>
    	</div>
    	
    	<!-- base -->
    	<div class="field">
    		<div class="field-head">基本信息</div>
    		
    		<div class="field-con">
    			<span class="field-ccc">申 请 人：</span><span class="cont" id='name-span'>NOVALUE</span>
    		</div>
    		<div class="field-con">
    			<span class="field-ccc">所属部门：</span><span class="cont" id='dept-span'>NOVALUE</span>
    		</div>
    		<div class="field-con">
    			<span class="field-ccc">提交时间：</span><span class="cont" id='create-time-span'>NOVALUE</span>
    		</div>
       	</div>
       	
       	<!-- X -->
       	<div class="field">
    		<div class="field-head">详细内容</div>
    		<div id='content-span'>
    		NOVALUE
    		</div>
       	</div>
    </div>
</body>
</html>


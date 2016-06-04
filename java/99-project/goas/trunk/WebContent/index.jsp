﻿﻿<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<% 
	
	String name = (String)session.getAttribute("name");
	if (name == null || "".equals(name)) {
		response.sendRedirect("login.html");
	}
%>

<!DOCTYPE HTML>
<html lang='cn'>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>GOAS 办公系统</title>
    <link href="jqueryLiger/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="css/index.css" rel="stylesheet" type="text/css" /> 
    
    <script src="jqueryLiger/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="jqueryLiger/lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 
    <script type="text/javascript">
        var accordion = null;
        var tree = null;
        var navtab = null;
        var message = null;
        var tip = null;
        var noticeNum = 0;
        
        $(function () {
        	
        	get_notice();
        	getMessage();
            $("#layout1").ligerLayout({
                leftWidth: 250, height: '100%',heightDiff:-34,space:4, onHeightChanged: f_heightChanged });

            var height = $(".l-layout-center").height();
            $("#framecenter").ligerTab({ height: height });
            navtab = $("#framecenter").ligerGetTabManager();
            <% if ("领导".equals((String)session.getAttribute("role"))) {%>
            $("#accordion1").ligerAccordion({ height: height - 24 - 50 });
            <% } else {%>
            $("#accordion1").ligerAccordion({ height: height - 24 - 25 });
            <%}%>
            $(".l-link").hover(function () {
                    $(this).addClass("l-link-over");
                }, function () {
                    $(this).removeClass("l-link-over");
            });

            accordion = $("#accordion1").ligerGetAccordionManager();
            $("#pageloading").hide();
            
            $("#messageClick").click(function() {
            	if (navtab.isTabItemExist('inbox')) {
		        	navtab.reload('inbox');
		        	navtab.selectTabItem('inbox');		
            	} else {
            		
            		f_addTab('inbox','收信箱','tabpage/inboxPage.html');
            	}
            });
            
            $("#nameClick").click(function() {
            	if (navtab.isTabItemExist('person')) {
		        	navtab.reload('person');
		        	navtab.selectTabItem('person');		
            	} else {
            		
            		f_addTab('person','个人设置','tabpage/personalSettingPage.html');
            	}
            });
            
            setInterval(function(){get_notice();getMessage();}, 10000);
        });
        
        function tiptop() {
            if (!tip) {
                tip = $.ligerDialog.tip({ title: '你有新的消息', content: '你有新的办公事务需要审批，请尽快查看。' });
            }else {
                var visabled = tip.get('visible');
                if (!visabled) tip.show();
        }}
        
        function get_notice() {
	        $.ajax({
                type: 'post', cache: false, dataType: 'json',
                url: 'job!notice.action',
                success: function (obj) {
                	if(obj.Rows.length > noticeNum) {
                		tiptop();
                	}
                	noticeNum = obj.Rows.length;
                },
                error: function () {
                    //alert('系统错误,请联系管理员！');
                }
       		});
        }
        
        function messagetop(num) {
            if (!message) {
            	message = $.ligerDialog.tip({ title: '你有新的消息', content: '你有'+num+'封新的邮件，请查看。' });
            }else {
                var visabled = message.get('visible');
                if (!visabled) message.show();
        	}
        }
        
      //一开始要调用一次，初始化数据
        function getMessage() {
	        $.ajax({
                type: 'post', cache: false, dataType: 'json',
                url: 'message!getMessageCount.action',
                success: function (obj) {
                	var oldNum = $('#messageNum').html();
                	if (obj.num > oldNum)
                		messagetop(obj.num-oldNum);
                	$('#messageNum').html(obj.num);
                },
                error: function () {
                    //alert('系统错误,请联系管理员！');
                }
       		});
        }
        
        function f_heightChanged(options) {
            if (navtab)
            	navtab.addHeight(options.diff);
            if (accordion && options.middleHeight - 24 > 0) {
            	
            <% if ("领导".equals((String)session.getAttribute("role"))) {%>
                accordion.setHeight(options.middleHeight - 24 - 50);
            <% } else {%>
            	accordion.setHeight(options.middleHeight - 24 - 25);
            <%}%>
            }
        }
        
        function f_addTab(tabid,text, url) { 
        	navtab.addTabItem({ tabid : tabid, text: text, url: url });
        } 
     </script> 
</head>
<body style="position: relative">  
	<div style="z-index: 999; position: absolute; right: 20px; top: 135px">
	    <script charset="utf-8" type="text/javascript" id="TOOL_115_COM_JS" src="http://tool.115.com/static/tools/weather/tool_weather_api_text.js"></script>
	</div> 
<div id="pageloading"></div>  
<div id="topmenu" class="l-topmenu">
    <div class="l-topmenu-logo">GOAS办公系统</div>
    <div class="l-topmenu-welcome">
        <span class="l-link2" >已登录  <%=session.getAttribute("dept")%><%=session.getAttribute("role")%> <a id="nameClick" href="#" class="l-link2"><%=session.getAttribute("name")%></a></span> 
        <span class="space">|</span>
        <a id="messageClick" href="#" class="l-link2">邮件 <span id="messageNum">0</span> </a>
        <span class="space">|</span>
         <a href="loginoutaction!out.action" class="l-link2">退出</a>
    </div> 
    <div id="pic">
    </div>
</div>
  <div id="layout1"> 
    <div position="left"  title="导航栏">
        <div position="top"  title="办公系统" id="accordion1">
            <div title="办公事务申请">
                <a class="l-link" href="javascript:f_addTab('leave','请假申请','tabpage/requisition_form/holiday.jsp')">请假申请</a>
                <a class="l-link" href="javascript:f_addTab('ot','加班申请','tabpage/requisition_form/ot.jsp')">加班申请</a>
                <a class="l-link" href="javascript:f_addTab('go_out','出差申请','tabpage/requisition_form/trip.jsp')">出差申请</a>
                <a class="l-link" href="javascript:f_addTab('money','经费申请','tabpage/requisition_form/money.jsp')">经费申请</a>
            	<a class="l-link" href="javascript:f_addTab('all_ago','事务申请历史','tabpage/requisition_form/all.jsp')">事务申请历史</a>
            </div>
            <div title="站內邮箱">
                <a class="l-link" href="javascript:f_addTab('write_message','写信','tabpage/writeMessagePage.html')">写信</a>
                <a class="l-link" href="javascript:f_addTab('inbox','收信箱','tabpage/inboxPage.html')">收信箱</a>
            	<a class="l-link" href="javascript:f_addTab('outbox','写信箱','tabpage/outboxPage.html')">写信箱</a>
            </div>
            <div title="公共资源">
                <a class="l-link" href="javascript:f_addTab('phone','通讯录','tabpage/addressBookPage.html')">通讯录</a>
                <a class="l-link" href="javascript:f_addTab('source','资源共享','tabpage/resourceShare.html')">资源共享</a>
                <a class="l-link" href="javascript:f_addTab('map','地图','tabpage/map.html')">在线地图</a>
                <a class="l-link" href="javascript:f_addTab('translate','在线翻译','http://fanyi.baidu.com/')">在线翻译</a>
                <a class="l-link" href="javascript:f_addTab('year','万年历','tabpage/date.html')">万年历</a>
            </div>
            <div title="高级管理">
                <a class="l-link" href="javascript:f_addTab('source_manager','资源管理','tabpage/resourceManagerPage.html')">资源管理</a>
                <a class="l-link" href="javascript:f_addTab('inform_manager','通告管理','tabpage/informManagerPage.html')">通告管理</a>
                <% if ("领导".equals((String)session.getAttribute("role")) && "组织部".equals((String)session.getAttribute("dept"))) {%>
                <a class="l-link" href="javascript:f_addTab('account_manager','帐号管理','tabpage/userManagerPage.html')">帐号管理</a>
                <% } %>  
            </div>
        </div>
        <div id="accc">
        <% if ("领导".equals((String)session.getAttribute("role"))) {%>
            <a class="l-acc-link" href="javascript:f_addTab('jobs','待处理事务','tabpage/need_to_deal.jsp')">待处理事务</a>
        <% } %>  
            <a class="l-acc-link" href="javascript:f_addTab('person','个人设置','tabpage/personalSettingPage.html')">个人设置</a>
        </div>
    </div>
        <div position="center" id="framecenter"> 
            <div tabid="home" title="办公首页" style="height:300px" >
                <iframe frameborder="0" name="home" id="home" src="welcome.html"></iframe>
            </div> 
        </div>
  </div>
  <div  style="height:32px; line-height:32px; text-align:center;">
          Copyright © 2011-2012 FOSU
  </div>
</body>
</html>

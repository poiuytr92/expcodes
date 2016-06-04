<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

    <title>trip</title>
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
    				
    				// mk datas
    				
    				var conts = '';
    				conts += '人数：'+$('#inp-man').val()+
    						 '~电话：'+$('#inp-phone').val()+
    						 '~原因：'+$("#inp-reason").val()+
    						 '~经费：'+$("#inp-cash").val()+
    						 '~开始时间：'+$("#inp-stt").val()+
    						 '~结束时间：'+$("#inp-edt").val();
    				for (var i = 1; i <= $('#inp-travel-tablebody').find('tr').length; i++) {
    					if( $("#inp-place"+i).val() != "" && $("#inp-time"+i).val() != "" ) {
    						conts += '~地点' + i.toString() + 
							 '：乘' + $("#inp-sit"+i).val() +
							 '至' + $("#inp-place"+i).val() +
							 '逗留' + $("#inp-time"+i).val() + '日';
    					}				
    				}
    				conts += '~返程搭' + $('#inp-sit-back').val();

    				$.ajax({
                        type: 'post', cache: false, dataType: 'json',
                        url: 'job!add.action',
                        data: [
	                        { name: 'name', value: '<%=session.getAttribute("name")%>' },
	                        { name: 'user_id', value: '<%=session.getAttribute("user_id")%>' },
	                        { name: 'sort', value: "出差" },
	                        { name: 'content', value: conts },
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
    		
    		// add travel
    		$('#inp-add-line').click( function() {
    			var i = $('#inp-travel-tablebody').find('tr').length + 1;
        		$('#inp-travel-tablebody').append(
        			'<tr><td><select id="inp-sit' + i.toString() + 
        			'"><option value="巴士">巴士</option><option value="的士">的士</option><option value="飞机">飞机</option><option value="火车">火车</option><option value="轮船">轮船</option></select><td><input type="text" style="padding:0px;width:70px;text-align:center;margin-left:10px;" id="inp-place'
        			+ i.toString() + '" placeholder="逗留地点" class="inp" /><td><input type="text" style="padding:0px;width:50px;text-align:center;margin-left:10px;" id="inp-time'
        			+ i.toString() + '" placeholder="天数" class="inp" />');
    		});
    		
    		// date changer
    		$("#inp-stt").datepicker({ minDate: +0, maxDate: "+1M +10D", altField: "#inp-stt", altFormat: "yy-mm-dd" });
    		$("#inp-edt").datepicker({ minDate: +0, maxDate: "+1M +10D", altField: "#inp-edt", altFormat: "yy-mm-dd" });
    		var date_ch = function() {
    			if( $('#inp-stt').val() != "" && $('#inp-edt').val() != "" ) {
        			var d1 = new Date($('#inp-stt').val());
        			var d2 = new Date($('#inp-edt').val());
        			$('#timelong').text(((d2-d1)/86400000 + 1).toString() + ' 日');
    			}
    			$('#inp-stt').validationEngine('validate');
    			$('#inp-edt').validationEngine('validate');
    		};
    		$('#inp-stt').change(date_ch);
    		$('#inp-edt').change(date_ch);
    	});
    </script>
</head>
<body>
    <div id="form">
      <form name="holidayForm" id="form-form">
    	<div id='head'>
    		<span><img src="../../jqueryLiger/lib/ligerUI/skins/icons/communication.gif" /></span>
    		<span>出差申请表</span>
    	</div>
    	
    	<!-- base -->
    	<div class="field">
    		<div class="field-head">基本信息</div>
    		<div class="field-con">
    			<span>申请人：</span>
    			<input name="name" type="text" id="inp-name" class='inp' value='<%=session.getAttribute("name")%>' disabled="disabled"/>
    		</div>
    		<div class="field-con">
    			<span>审批部门：</span>
    			<input name="name" type="text" id="inp-dept" class='inp' value='<%=session.getAttribute("dept")%>,财务部' disabled="disabled"/>
    		</div>
    		<div class="field-con">
    			<span>人 数：</span>
    			<input name="name" value="1" type="text" id="inp-man" class='inp validate[required]' />
    		</div>
    		<div class="field-con">
    			<span>出差原因：</span>
    			<input name="name" type="text" id="inp-reason" class='inp validate[required]' />
    		</div>
    		<div class="field-con">
    			<span>联系电话：</span>
    			<input name="name" type="text" id="inp-phone" class='inp validate[required,custom[integer,min[8]]]' />
    		</div>
    		<div class="field-con">
    			<span>总经费：</span>
    			<input name="name" value='100' type="text" id="inp-cash" class='inp validate[required,custom[integer],max[10000]]' />
    		</div>
       	</div>
       	
       	<!-- timing -->
       	<div class="field">
    		<div class="field-head">出差时间</div>
    		<div class="field-con">
    			<span>开始时间：</span>
    			<input type="text" id="inp-stt" class='inp validate[required]' />
    		</div>
    		<div class="field-con">
    			<span>结束时间：</span>
    			<input type="text" id="inp-edt" class='inp validate[required]' />
    		</div>
    		<div class="field-con">
    			<span>时间长度：</span><span id="timelong">0 日</span>
    		</div>
       	</div>
       	
       	<!-- travel -->
       	<div class="field">
    		<div class="field-head">交通及住宿</div>
    		<div class="field-con">
    			<span style="margin-bottom: 18px;">地 点：</span>
    			<table class='inp-table'><tbody id='inp-travel-tablebody'>
    				<tr><td>
    				<select id="inp-sit1">
    					<option value="巴士">巴士</option>
    					<option value="的士">的士</option>
    					<option value="飞机">飞机</option>
    					<option value="火车">火车</option>
    					<option value="轮船">轮船</option>
    				</select>
    				<td>
		   			<input type="text" style="padding:0px;width:70px;text-align:center;margin-left:10px;" id="inp-place1" placeholder="逗留地点" class='inp validate[required]' />
		   			<td>
		   			<input type="text" style="padding:0px;width:50px;text-align:center;margin-left:10px;" id="inp-time1" placeholder="天数" class='inp validate[required]' />
		   			<td>
    			</tbody></table>
    			<span id='inp-add-line' class="ui-icon ui-icon-plusthick" style="margin-top: 2px;width: 16px;vertical-align: top;"></span>
    		</div>
    		<div class="field-con">
    			<span>返 程：</span>
    			<select id="inp-sit-back">
    					<option value="巴士">巴士</option>
    					<option value="的士">的士</option>
    					<option value="飞机">飞机</option>
    					<option value="火车">火车</option>
    					<option value="轮船">轮船</option>
    			</select>
    		</div>
       	</div>
       	
       	<div class='field-btn'>
       		<input type="button" value="提交" id="inp-btn" />
       	</div>
      </form>
    </div>
</body>
</html>


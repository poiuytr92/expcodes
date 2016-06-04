//######js脚本实例########
//******************Object入参测试和返回
function QxCmd(inparam) {
	Packages.java.lang.System.out.println("接收到参数：" + inparam);
	return inparam;
}
//******************List入参测试和返回
function QxCmd(inparam) {
	Packages.java.lang.System.out.println("for 循环打印**************");
	for (var i = 0; i < inparam.size(); i++) {
		Packages.java.lang.System.out.println("接收到list参数：" + inparam.get(i));
	}
	Packages.java.lang.System.out.println("for 循环打印结束************");
	inparam.add("success");
	
	return inparam;
}
//****************Map入参测试和返回
function QxCmd(inparam) {
	Packages.java.lang.System.out.println("接收到参数：" + inparam);
	Packages.java.lang.System.out.println("接收到参数：" + inparam.get("string"));
	Packages.java.lang.System.out.println("接收到参数：" + inparam.get("integer"));
	inparam.put("result", "success");
	return inparam;
}
//*****************调用多参数js方法
function QxCmd(inparam1, inparam2, inparam3) {
	return inparam1 + (inparam2 + inparam3);
}
//*************js内部调用java方法
function QxCmd(inparam) {
	var ip = inparam.get("ip");
	var port = inparam.get("port");
	var telnet = Packages.com.catt.util.javascript.test.TestScriptUtils
					.telnet(ip, port);
	var out = Packages.java.util.HashMap();
	out.put("result", "success");
	out.put("telnet", telnet);
	return out;
}
//***********js和java方法json的互转
function QxCmd(inparam) {
	Packages.java.lang.System.out.println("js代码中解析json***************" + inparam);
	// json字符串转换成javascript对象 
	var obj = eval('(' + inparam + ')'); 
	Packages.java.lang.System.out.println("obj.ip：" + obj.ip);
	Packages.java.lang.System.out.println("obj.port：" + obj.port);
	// 创建javascript对象 
	var reObj = new Object();
	reObj.ip = "172.168.10.100";
	reObj.prot = 8999;
	var re = ObjToJson(reObj)
	Packages.java.lang.System.out.println("ObjToJson(reObj): " + re);
	Packages.java.lang.System.out.println("js代码中解析json**********");
	return re;
}
function ObjToJson(obj) {
	var props = "{";
	//p 为属性名称
	for (var p in obj) {
		var val = obj[p];
		if (typeof (val) == "function") {
		} else if (typeof(val) == "number") {
			props += "\"" + p + "\":" + val + ",";
		} else {
			props += "\"" + p + "\":\"" + val + "\",";
		}
	}
	props = props.substring(0, props.length - 1);
	return props + "}";
}

// ######js常用方法########
 // 简单对象转换json字符串
 // {"port":8999,"ip":"172.168.10.100"}
function ObjToJson(obj) {
	var props = "{";
	//p 为属性名称
	for (var p in obj) {
		var val = obj[p];
		if (typeof (val) == "function") {
		} else if (typeof(val) == "number") {
			props += "\"" + p + "\":" + val + ",";
		} else {
			props += "\"" + p + "\":\"" + val + "\",";
		}
	}
	props = props.substring(0, props.length - 1);
	return props + "}";
}

 // 正则取值
 // str = "Packets: Sent = 4, Received = 4, Lost = 0 (0% loss),"
 // patt = /Sent = ([\d]+)/;
 // re = exec(patt, str); //re = 4
function exec(patt, str) {
	var result = null;
	result = patt.exec(str);
	if (result != null ) {
		return result[1];
	} 
	return result;
}

 // 查找指定字符串，返回true/false
function find(findStr, str) {
	return new RegExp(findStr).test(str);
}

 // 键值对输出
function outln(key, value) {
	return key + "#A#" + value + "\n";
}

 // XML格式输出
function xml(key, value) {
	return "<" + key + ">" + value + "</" + key + ">";
}
 // XML格式输出并换行
function xmlln(key, value) {
	return out(key, value) + "\n";
}

 //正则取值
function match(inPara, reg, type) {
	var tmp = inPara.match(reg);
	if (tmp !== null){
		return tmp[1];
	} else {
		return type;
	}
}
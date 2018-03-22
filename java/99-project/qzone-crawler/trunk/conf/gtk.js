
/**
 * QQ空间每次登陆都会生成一个全新的GTK码, 用于获取各种空间信息.
 * 此函数就是GTK编码的生成算法.
 * --------------------------------------
 * 	在登陆页面点击【登陆后】，按F12打开开发者工具，
 * 	通过ctrl+shift+f全局搜索 【g_tk】，可以找到这个js函数
 */
function getACSRFToken(skey) {
	var hash = 5381;
	for (var i = 0, len = skey.length; i < len; ++i)
		hash += (hash << 5) + skey.charCodeAt(i);
	return hash & 2147483647
};


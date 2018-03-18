/*
 * 在登陆页面点击【登陆后】，按F12打开开发者工具，
 * 通过ctrl+shift+f全局搜索 【g_tk】，可以找到这个js函数
 */

function getACSRFToken(skey) {
	var hash = 5381;
	for (var i = 0, len = skey.length; i < len; ++i)
		hash += (hash << 5) + skey.charCodeAt(i);
	return hash & 2147483647
};
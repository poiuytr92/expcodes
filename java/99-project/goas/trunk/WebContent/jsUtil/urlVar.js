
/*
 * 获取url所有参数和对应的值
 */
function getUrlVars (){
	var vars = [], hash;
	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	for(var i = 0; i < hashes.length; i++)
	{
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

/*
 * 获取指定参数的值
 */
function getUrlVar (name){
	return getUrlVars()[name];
}
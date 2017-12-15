package exp.bilibli.plugin;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestRecvMsg {

	public static void main(String[] args) {
		String hex = "000002E60010000000000005000000007B22636D64223A225359535F47494654222C226D7367223A224D69646E69746573746F726D5C75353732385C75373666345C75363461645C753935663434333030355C75373036625C75353239625C75353136385C75356630305C75666630635C75353565385C75376666625C75353136385C75353733615C75666630635C75393031665C75353362625C75353666345C75383963325C75666630635C75386664385C75383066645C75353134645C75386433395C75393838365C75353364365C75373036625C75353239625C75373936385C7566663031222C226D73675F74657874223A224D69646E69746573746F726D5C75353732385C75373666345C75363461645C753935663434333030355C75373036625C75353239625C75353136385C75356630305C75666630635C75353565385C75376666625C75353136385C75353733615C75666630635C75393031665C75353362625C75353666345C75383963325C75666630635C75386664385C75383066645C75353134645C75386433395C75393838365C75353364365C75373036625C75353239625C75373936385C7566663031222C2274697073223A224D69646E69746573746F726D5C75353732385C75373666345C75363461645C753935663434333030355C75373036625C75353239625C75353136385C75356630305C75666630635C75353565385C75376666625C75353136385C75353733615C75666630635C75393031665C75353362625C75353666345C75383963325C75666630635C75386664385C75383066645C75353134645C75386433395C75393838365C75353364365C75373036625C75353239625C75373936385C7566663031222C2275726C223A22687474703A5C2F5C2F6C6976652E62696C6962696C692E636F6D5C2F3433303035222C22726F6F6D6964223A34333030352C227265616C5F726F6F6D6964223A34333030352C22676966744964223A3130362C226D736754697073223A307D";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		String sJson = RegexUtils.findFirst(msg, "[^{]*(.*)");
		System.out.println(sJson);
	}
}
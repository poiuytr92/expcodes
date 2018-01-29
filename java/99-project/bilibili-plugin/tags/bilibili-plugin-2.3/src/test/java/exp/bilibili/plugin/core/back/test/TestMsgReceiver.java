package exp.bilibili.plugin.core.back.test;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class TestMsgReceiver {

	public static void main(String[] args) {
//		"0000007D0010000000000005000000007B22636D64223A2241435449564954595F4556454E54222C2264617461223A7B226B6579776F7264223A226E6577737072696E675F32303138222C2274797065223A22637261636B6572222C226C696D6974223A3330303030302C2270726F6772657373223A35393333347D7D000004EA0010000000000005000000007B22636D64223A2253454E445F47494654222C2264617461223A7B22676966744E616D65223A225C75376561325C75373036665C7537623363222C226E756D223A312C22756E616D65223A225C75346537315C75356663365C7536386136222C2272636F7374223A333132363130342C22756964223A38383735323835342C22746F705F6C697374223A5B7B22756964223A313635303836382C22756E616D65223A224D2D5C75346539615C75376437325C7535613163222C2266616365223A22687474703A2F2F69312E6864736C622E636F6D2F6266732F666163652F626266643162356361666534373139653361353731353461633166663136613965346439633662332E6A7067222C2272616E6B223A312C2273636F7265223A313432313130302C2267756172645F6C6576656C223A322C22697353656C66223A307D2C7B22756964223A38323531343834322C22756E616D65223A225C75366532315C75353261625C75346532645C75373638345C75346530615C75353334335C75346530375C75346532615C75353766615C7534663663222C2266616365223A22687474703A2F2F69312E6864736C622E636F6D2F6266732F666163652F383266643933383632633331653734366437396232636636323039386563383039663662666137382E6A7067222C2272616E6B223A322C2273636F7265223A313131353436342C2267756172645F6C6576656C223A332C22697353656C66223A307D2C7B22756964223A383038313532362C22756E616D65223A225C75396139315C75353137315C75346561625C75353335355C75386636365C75373638345C75376432325C75376635375C75363562395C7536356166222C2266616365223A22687474703A2F2F69312E6864736C622E636F6D2F6266732F666163652F313833666564616636663962613461383039613133613934313433613761323833336434636635322E6A7067222C2272616E6B223A332C2273636F7265223A3534333630302C2267756172645F6C6576656C223A332C22697353656C66223A307D5D2C2274696D657374616D70223A313531363533393130392C22676966744964223A3130392C226769667454797065223A332C22616374696F6E223A225C75386436305C7539303031222C227375706572223A302C227072696365223A323030302C22726E64223A2230222C226E65774D6564616C223A302C226E65775469746C65223A302C226D6564616C223A5B5D2C227469746C65223A22222C22626561744964223A302C2262697A5F736F75726365223A226C697665222C226D65746164617461223A22222C2272656D61696E223A322C22676F6C64223A302C2273696C766572223A302C226576656E7453636F7265223A35393333342C226576656E744E756D223A302C22736D616C6C74765F6D7367223A5B5D2C227370656369616C47696674223A6E756C6C2C226E6F746963655F6D7367223A5B5D2C2263617073756C65223A7B226E6F726D616C223A7B22636F696E223A312C226368616E6765223A302C2270726F6772657373223A7B226E6F77223A393939392C226D6178223A31303030307D7D2C22636F6C6F7266756C223A7B22636F696E223A302C226368616E6765223A302C2270726F6772657373223A7B226E6F77223A323030302C226D6178223A353030307D7D7D2C22616464466F6C6C6F77223A307D7D"
//		{"cmd":"ACTIVITY_EVENT","data":{"keyword":"newspring_2018","type":"cracker","limit":300000,"progress":59334}}
		String hex = "0000007D0010000000000005000000007B22636D64223A2241435449564954595F4556454E54222C2264617461223A7B226B6579776F7264223A226E6577737072696E675F32303138222C2274797065223A22637261636B6572222C226C696D6974223A3330303030302C2270726F6772657373223A35393436327D7D000004EF0010000000000005000000007B22636D64223A2253454E445F47494654222C2264617461223A7B22676966744E616D65223A225C75376561325C75373036665C7537623363222C226E756D223A362C22756E616D65223A225C75383663665C75356330665C7539353335222C2272636F7374223A333132363130342C22756964223A33363430373439302C22746F705F6C697374223A5B7B22756964223A313635303836382C22756E616D65223A224D2D5C75346539615C75376437325C7535613163222C2266616365223A22687474703A2F2F69302E6864736C622E636F6D2F6266732F666163652F626266643162356361666534373139653361353731353461633166663136613965346439633662332E6A7067222C2272616E6B223A312C2273636F7265223A313433393630302C2267756172645F6C6576656C223A322C22697353656C66223A307D2C7B22756964223A38323531343834322C22756E616D65223A225C75366532315C75353261625C75346532645C75373638345C75346530615C75353334335C75346530375C75346532615C75353766615C7534663663222C2266616365223A22687474703A2F2F69312E6864736C622E636F6D2F6266732F666163652F383266643933383632633331653734366437396232636636323039386563383039663662666137382E6A7067222C2272616E6B223A322C2273636F7265223A313132333436342C2267756172645F6C6576656C223A332C22697353656C66223A307D2C7B22756964223A383038313532362C22756E616D65223A225C75396139315C75353137315C75346561625C75353335355C75386636365C75373638345C75376432325C75376635375C75363562395C7536356166222C2266616365223A22687474703A2F2F69322E6864736C622E636F6D2F6266732F666163652F313833666564616636663962613461383039613133613934313433613761323833336434636635322E6A7067222C2272616E6B223A332C2273636F7265223A3534393630302C2267756172645F6C6576656C223A332C22697353656C66223A307D5D2C2274696D657374616D70223A313531363534323035332C22676966744964223A3130392C226769667454797065223A332C22616374696F6E223A225C75386436305C7539303031222C227375706572223A312C227072696365223A323030302C22726E64223A22363536343136393838222C226E65774D6564616C223A302C226E65775469746C65223A302C226D6564616C223A5B5D2C227469746C65223A22222C22626561744964223A22222C2262697A5F736F75726365223A226C697665222C226D65746164617461223A22222C2272656D61696E223A302C22676F6C64223A302C2273696C766572223A302C226576656E7453636F7265223A35393436322C226576656E744E756D223A302C22736D616C6C74765F6D7367223A5B5D2C227370656369616C47696674223A6E756C6C2C226E6F746963655F6D7367223A5B5D2C2263617073756C65223A7B226E6F726D616C223A7B22636F696E223A342C226368616E6765223A322C2270726F6772657373223A7B226E6F77223A3530302C226D6178223A31303030307D7D2C22636F6C6F7266756C223A7B22636F696E223A302C226368616E6765223A302C2270726F6772657373223A7B226E6F77223A302C226D6178223A353030307D7D7D2C22616464466F6C6C6F77223A307D7D";
		foo(hex);
	}
	
	public static void foo(String hex) {
		byte[] bytes = BODHUtils.toBytes(hex);
		String msg = new String(bytes);
		System.out.println(StrUtils.view(msg));
		System.out.println("====");
		
		String sJson = RegexUtils.findFirst(msg.substring(15), "[^{]*([^\0]*)");
		System.out.println(sJson);
	}
	
}
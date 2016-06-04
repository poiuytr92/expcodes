package org.util.sortRule;

import java.util.Comparator;

import org.model.Inform;
import org.util.DateUnit;

//通告记录根据id值升序排序
public class InformSortRuleA implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		String date1= DateUnit.dateToStr(((Inform)o1).getDate(), "MEDIUM");
		String date2= DateUnit.dateToStr(((Inform)o2).getDate(), "MEDIUM");
		String[] ymd1 = date1.split("-");
		String[] ymd2 = date2.split("-");
		if(ymd1[0].equals(ymd2[0])) {
			if(ymd1[1].equals(ymd2[1])) {
				if(ymd1[2].equals(ymd2[2])) {
					return ((Inform)o1).getId().intValue() - ((Inform)o2).getId().intValue();
				}
				else {
					return Integer.parseInt(ymd1[2]) - Integer.parseInt(ymd2[2]);
				}
			}
			else {
				return Integer.parseInt(ymd1[1]) - Integer.parseInt(ymd2[1]);
			}
		}
		else {
			return Integer.parseInt(ymd1[0]) - Integer.parseInt(ymd2[0]);
		}
	}

}

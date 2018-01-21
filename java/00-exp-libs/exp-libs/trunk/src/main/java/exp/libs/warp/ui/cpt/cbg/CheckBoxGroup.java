package exp.libs.warp.ui.cpt.cbg;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class CheckBoxGroup<T> {

	private int size;
	
	private T[] items;
	
	private List<JCheckBox> cbs;
	
	private Map<T, Integer> cbIdx;
	
	public CheckBoxGroup(T[] items) {
		this.items = items;
		this.size = (items == null ? 0 : items.length);
		this.cbs = new ArrayList<JCheckBox>(size);
		this.cbIdx = new HashMap<T, Integer>();
		
		for(int i = 0; i < size; i++) {
			T item = items[i];
			JCheckBox cb = new JCheckBox(item.toString());
			cbs.add(cb);
			cbIdx.put(item, i);
		}
	}
	
	public boolean isSelected(T item) {
		Integer itemIdx = cbIdx.get(item);
		return isSelected(itemIdx);
	}
	
	public boolean isSelected(int itemIdx) {
		boolean isSelected = false;
		JCheckBox cb = cbs.get(itemIdx);
		if(cb != null) {
			isSelected = cb.isSelected();
		}
		return isSelected;
	}
	
	/**
	 * 反选
	 * @param item
	 */
	public void inverse(T item) {
		Integer itemIdx = cbIdx.get(item);
		if(itemIdx != null) {
			inverse(itemIdx);
		}
	}
	
	/**
	 * 反选
	 * @param itemIdx
	 */
	public void inverse(int itemIdx) {
		JCheckBox cb = getCheckBox(itemIdx);
		if(cb != null) {
			cb.setSelected(!cb.isSelected());
		}
	}
	
	/**
	 * 反选所有
	 */
	public void inverseAll() {
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			cb.setSelected(!cb.isSelected());
		}
	}
	
	/**
	 * 选择
	 * @param item
	 */
	public void select(T item) {
		Integer itemIdx = cbIdx.get(item);
		if(itemIdx != null) {
			select(itemIdx);
		}
	}
	
	/**
	 * 选择
	 * @param itemIdx
	 */
	public void select(int itemIdx) {
		JCheckBox cb = getCheckBox(itemIdx);
		if(cb != null) {
			cb.setSelected(true);
		}
	}
	
	/**
	 * 选择所有
	 */
	public void selectAll() {
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			cb.setSelected(true);
		}
	}
	
	/**
	 * 取消选择
	 * @param item
	 */
	public void unselect(T item) {
		Integer itemIdx = cbIdx.get(item);
		if(itemIdx != null) {
			unselect(itemIdx);
		}
	}
	
	/**
	 * 取消选择
	 * @param itemIdx
	 */
	public void unselect(int itemIdx) {
		JCheckBox cb = getCheckBox(itemIdx);
		if(cb != null) {
			cb.setSelected(false);
		}
	}
	
	/**
	 * 取消选择所有
	 */
	public void unselectAll() {
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			cb.setSelected(false);
		}
	}
	
	public void setEnable(boolean enable) {
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			cb.setEnabled(enable);
		}
	}
	
	public T getItem(int itemIdx) {
		T item = null;
		if(items != null && itemIdx >= 0 && itemIdx < size) {
			item = items[itemIdx];
		}
		return item;
	}
	
	public JCheckBox getCheckBox(int itemIdx) {
		JCheckBox cb = null;
		if(itemIdx >= 0 && itemIdx < size) {
			cb = cbs.get(itemIdx);
		}
		return cb;
	}
	
	public List<T> getItems(boolean isSelected) {
		List<T> selectedItems = new LinkedList<T>();
		if(items != null) {
			for(int i = 0; i < size; i++) {
				JCheckBox cb = cbs.get(i);
				if(cb.isSelected() == isSelected) {
					selectedItems.add(items[i]);
				}
			}
		}
		return selectedItems;
	}
	
	public List<JCheckBox> getCheckBoxs(boolean isSelected) {
		List<JCheckBox> selectedCBs = new LinkedList<JCheckBox>();
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			if(cb.isSelected() == isSelected) {
				selectedCBs.add(cb);
			}
		}
		return selectedCBs;
	}
	
	public List<JCheckBox> getAllCheckBoxs() {
		return new LinkedList<JCheckBox>(cbs);
	}
	
	public JPanel toHGridPanel() {
		int col = (size <= 0 ? 1 : size);
		JPanel panel = new JPanel(new GridLayout(1, col));
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			panel.add(cb, i);
		}
		return panel;
	}
	
	public JPanel toVGridPanel() {
		int row = (size <= 0 ? 1 : size);
		JPanel panel = new JPanel(new GridLayout(row, 1));
		for(int i = 0; i < size; i++) {
			JCheckBox cb = cbs.get(i);
			panel.add(cb, i);
		}
		return panel;
	}
	
}

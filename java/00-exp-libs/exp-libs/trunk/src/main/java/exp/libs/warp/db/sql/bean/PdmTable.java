package exp.libs.warp.db.sql.bean;

import java.util.LinkedList;
import java.util.List;

public class PdmTable {

	private String tableName;

	private List<PdmColumn> columns;

	public PdmTable() {
		this.columns = new LinkedList<PdmColumn>();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<PdmColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<PdmColumn> columns) {
		this.columns = columns;
	}
	
}

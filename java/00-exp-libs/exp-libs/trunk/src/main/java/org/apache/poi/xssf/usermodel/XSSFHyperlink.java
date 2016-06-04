package org.apache.poi.xssf.usermodel;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.CellReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;

public class XSSFHyperlink implements Hyperlink {
	private int _type;
	private PackageRelationship _externalRel;
	private CTHyperlink _ctHyperlink;
	private String _location;

	public XSSFHyperlink(int type) {
		this._type = type;
		this._ctHyperlink = CTHyperlink.Factory.newInstance();
	}

	protected XSSFHyperlink(CTHyperlink ctHyperlink,
			PackageRelationship hyperlinkRel) {
		this._ctHyperlink = ctHyperlink;
		this._externalRel = hyperlinkRel;
		if (ctHyperlink.getLocation() != null) {
			this._type = 2;
			this._location = ctHyperlink.getLocation();
		} else if (this._externalRel == null) {
			if (ctHyperlink.getId() != null) {
				throw new IllegalStateException("The hyperlink for cell "
						+ ctHyperlink.getRef() + " references relation "
						+ ctHyperlink.getId() + ", but that didn't exist!");
			}
			this._type = 2;
		} else {
			URI target = this._externalRel.getTargetURI();
			this._location = target.toString();
			if ((this._location.startsWith("http://"))
					|| (this._location.startsWith("https://"))
					|| (this._location.startsWith("ftp://"))) {
				this._type = 1;
			} else if (this._location.startsWith("mailto:")) {
				this._type = 3;
			} else {
				this._type = 4;
			}
		}
	}

	public CTHyperlink getCTHyperlink() {
		return this._ctHyperlink;
	}

	public boolean needsRelationToo() {
		return this._type != 2;
	}

	protected void generateRelationIfNeeded(PackagePart sheetPart) {
		if (needsRelationToo()) {
			PackageRelationship rel = sheetPart
					.addExternalRelationship(this._location,
							XSSFRelation.SHEET_HYPERLINKS.getRelation());

			this._ctHyperlink.setId(rel.getId());
		}
	}

	public int getType() {
		return this._type;
	}

	public String getCellRef() {
		return this._ctHyperlink.getRef();
	}

	public String getAddress() {
		return this._location;
	}

	public String getLabel() {
		return this._ctHyperlink.getDisplay();
	}

	public String getLocation() {
		return this._ctHyperlink.getLocation();
	}

	public void setLabel(String label) {
		this._ctHyperlink.setDisplay(label);
	}

	public void setLocation(String location) {
		this._ctHyperlink.setLocation(location);
	}

	public void setAddress(String address) {
		validate(address);

		this._location = address;
		if (this._type == 2) {
			setLocation(address);
		}
	}

	private void validate(String address) {
		switch (this._type) {
		case 1:
		case 3:
		case 4:
			try {
				new URI(address);
			} catch (URISyntaxException x) {
				IllegalArgumentException y = new IllegalArgumentException(
						"Address of hyperlink must be a valid URI");
				y.initCause(x);
				throw y;
			}
		}
	}

	protected void setCellReference(String ref) {
		this._ctHyperlink.setRef(ref);
	}

	private CellReference buildCellReference() {
		return new CellReference(this._ctHyperlink.getRef());
	}

	public int getFirstColumn() {
		return buildCellReference().getCol();
	}

	public int getLastColumn() {
		return buildCellReference().getCol();
	}

	public int getFirstRow() {
		return buildCellReference().getRow();
	}

	public int getLastRow() {
		return buildCellReference().getRow();
	}

	public void setFirstColumn(int col) {
		this._ctHyperlink.setRef(new CellReference(getFirstRow(), col)
				.formatAsString());
	}

	public void setLastColumn(int col) {
		setFirstColumn(col);
	}

	public void setFirstRow(int row) {
		this._ctHyperlink.setRef(new CellReference(row, getFirstColumn())
				.formatAsString());
	}

	public void setLastRow(int row) {
		setFirstRow(row);
	}

	public String getTooltip() {
		return this._ctHyperlink.getTooltip();
	}

	public void setTooltip(String text) {
		this._ctHyperlink.setTooltip(text);
	}
}

/**
 * 
 */
package com.ibm.bao.ceshell.view;


/**
 *  FEMListLongFormatter
 *  /**
	 * Fields:
	 * type indicator   (in FEM this is indicated by an icon -- folder for folder
	 * d.IsReserved,    (in FEM, indicated by a composite icon, use L for lock)
	 * r.ContainmentName, 
	 * d.ContentSize,  
	 * d.DateCreated, 
	 * d.creator, 
	 * classDescriptionName
	 * d.MajorVersionNumber, 
	 * d.MinorVersionNumber,d
	 * .VersionStatus
	 * @return
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class FEMListLongFormatter {
	
	FieldFormatter typeIndicatorCol;
	BooleanFieldFormatter lockIndicatorCol;
	FieldFormatter nameCol;
	FieldFormatter sizeCol;
	DateFieldFormatter createdCol;
	FieldFormatter creatorCol;
	FieldFormatter classCol;
	FieldFormatter majorVerCol;
	FieldFormatter minorVerCol;
	FieldFormatter verStatus;
	
	@SuppressWarnings("unused")	// some strang reason being flagged as unused
	private FieldFormatter[] femColDefs;
	
	public FEMListLongFormatter() {
		initColDefs();
	}
	
	/**
	 * @param buf
	 * @param item
	 */
	public void formatCustomObjectRow(StringBuffer buf, FemLongListingItem custItem) {
		buf.append(typeIndicatorCol.format(custItem.getItemTypeIndicator()));
		buf.append(lockIndicatorCol.format(custItem.getIsReserved()));
		buf.append(nameCol.format(custItem.getContainmentName()));
		buf.append("");
		buf.append(createdCol.format(custItem.getDateCreated()));
		buf.append(creatorCol.format(custItem.getCreator()));
		buf.append(classCol.format(custItem.getClassDescriptionName()));
		buf.append("");
		buf.append("");
		buf.append("");	
	}

	public void formatDocRow(StringBuffer buf, FemLongListingItem docListing) {
		buf.append(typeIndicatorCol.format(docListing.getItemTypeIndicator()));
		buf.append(lockIndicatorCol.format(docListing.getIsReserved()));
		buf.append(nameCol.format(docListing.getContainmentName()));
		buf.append(sizeCol.format(docListing.getContentSize()));
		buf.append(createdCol.format(docListing.getDateCreated()));
		buf.append(creatorCol.format(docListing.getCreator()));
		buf.append(classCol.format(docListing.getClassDescriptionName()));
		buf.append(majorVerCol.format(docListing.getMajorVersionNumber()));
		buf.append(minorVerCol.format(docListing.getMinorVersionNumber()));
		buf.append(verStatus.format(docListing.getVersionStatus()));
	}
	
	/**
	 * 
	 */
	private void initColDefs() {
		
		 typeIndicatorCol = new FieldFormatter();
		 typeIndicatorCol.init("T",1);
		 
		 lockIndicatorCol = new BooleanFieldFormatter();
		 lockIndicatorCol.setTrueFmt("L");
		 lockIndicatorCol.setFalseFmt("-");
		 lockIndicatorCol.init("Lk", 3);
		 
		 nameCol = new FieldFormatter();
		 nameCol.init("Containment/Folder name", 35);
		 
		 sizeCol = new FieldFormatter();
		 sizeCol.init("Size (KB)", 10);
		 
		 createdCol = new DateFieldFormatter();
		 createdCol.init("Created", 24);
		 createdCol.setDateFormat(DateFieldFormatter.FEM_DATE_FORMAT);
		 
		 creatorCol = new FieldFormatter();
		 creatorCol.init("Creator", 20);
		 
		 classCol = new FieldFormatter();
		 classCol.init("Class", 20);
		 
		 majorVerCol = new FieldFormatter();
		 majorVerCol.init("Maj Ver", 6);
		 
		 minorVerCol = new FieldFormatter();
		 minorVerCol.init("Min Ver", 6);
		 
		 verStatus = new FieldFormatter();
		 verStatus.init("Status", 15);

		 this.femColDefs = new FieldFormatter[] {
			typeIndicatorCol,
			lockIndicatorCol,
			nameCol,
			sizeCol,
			createdCol,
			creatorCol,
			classCol,
			majorVerCol,
			minorVerCol,
			verStatus
		 };
	}
}

package org.apache.fineract.infrastructure.configuration.data;

public class EquifaxRelationTypes {

	private final String relationTypeFather ;
	
	private final String relationTypeHusband ;
	
	private final String relationTypeBrother ;
	
	private final String relationTypeSon ;
	
	private final String relationTypeSonInLaw ;
	
	private final String relationTypeFatherInLaw ;
	
	private final String relationTypeBrotherInLaw ;
	
	private final String relationTypeMother ;
	
	private final String relationTypeWife ;
	
	private final String relationTypeSister ;
	
	private final String relationTypeDaughter ;
	
	private final String relationTypeDaughterInLaw ;
	
	private final String relationTypeMotherInLaw ;
	
	private final String relationTypeSisterInLaw ;
	
	private final String relationTypeSpouse ;
	
	private final String relationTypeOther ;

	
	public EquifaxRelationTypes(String relationTypeFather, String relationTypeHusband, String relationTypeBrother,
			String relationTypeSon, String relationTypeSonInLaw, String relationTypeFatherInLaw,
			String relationTypeBrotherInLaw, String relationTypeMother, String relationTypeWife,
			String relationTypeSister, String relationTypeDaughter, String relationTypeDaughterInLaw,
			String relationTypeMotherInLaw, String relationTypeSisterInLaw, String relationTypeSpouse, String relationTypeOther) {
		super();
		this.relationTypeFather = relationTypeFather;
		this.relationTypeHusband = relationTypeHusband;
		this.relationTypeBrother = relationTypeBrother;
		this.relationTypeSon = relationTypeSon;
		this.relationTypeSonInLaw = relationTypeSonInLaw;
		this.relationTypeFatherInLaw = relationTypeFatherInLaw;
		this.relationTypeBrotherInLaw = relationTypeBrotherInLaw;
		this.relationTypeMother = relationTypeMother;
		this.relationTypeWife = relationTypeWife;
		this.relationTypeSister = relationTypeSister;
		this.relationTypeDaughter = relationTypeDaughter;
		this.relationTypeDaughterInLaw = relationTypeDaughterInLaw;
		this.relationTypeMotherInLaw = relationTypeMotherInLaw;
		this.relationTypeSisterInLaw = relationTypeSisterInLaw;
		this.relationTypeSpouse = relationTypeSpouse ;
		this.relationTypeOther = relationTypeOther;
	}

	public String getRelationTypeFather() {
		return this.relationTypeFather;
	}

	public String getRelationTypeHusband() {
		return this.relationTypeHusband;
	}

	public String getRelationTypeBrother() {
		return this.relationTypeBrother;
	}

	public String getRelationTypeSon() {
		return this.relationTypeSon;
	}

	public String getRelationTypeSonInLaw() {
		return this.relationTypeSonInLaw;
	}

	public String getRelationTypeFatherInLaw() {
		return this.relationTypeFatherInLaw;
	}

	public String getRelationTypeBrotherInLaw() {
		return this.relationTypeBrotherInLaw;
	}

	public String getRelationTypeMother() {
		return this.relationTypeMother;
	}

	public String getRelationTypeWife() {
		return this.relationTypeWife;
	}

	public String getRelationTypeSister() {
		return this.relationTypeSister;
	}

	public String getRelationTypeDaughter() {
		return this.relationTypeDaughter;
	}

	public String getRelationTypeDaughterInLaw() {
		return this.relationTypeDaughterInLaw;
	}

	public String getRelationTypeMotherInLaw() {
		return this.relationTypeMotherInLaw;
	}

	public String getRelationTypeSisterInLaw() {
		return this.relationTypeSisterInLaw;
	}
	
	public String getRelationTypeSpouse() {
		return this.relationTypeSpouse;
	}

	public String getRelationTypeOther() {
		return this.relationTypeOther;
	}
}

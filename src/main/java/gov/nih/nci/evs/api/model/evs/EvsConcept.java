package gov.nih.nci.evs.api.model.evs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EvsConcept {

	private String code;
	private String label;
	private List <EvsDefinition> definitions;
	private String displayName;
	private String preferredName;
	private boolean isStage;
	private String neoplasticStatus;
	private List <EvsSubconcept> subconcepts;
	private List <EvsSuperconcept> superconcepts;
	private List <String> semanticTypes;
	private List <EvsSynonym> synonyms;
	private List <EvsAdditionalProperty> additionalProperties;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List <EvsDefinition> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(List<EvsDefinition> definitions) {
		this.definitions = definitions;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPreferredName() {
		return preferredName;
	}
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}
	
	@JsonIgnore
	public String getNeoplasticStatus() {
		return neoplasticStatus;
	}
	public void setNeoplasticStatus(String neoplasticStatus) {
		this.neoplasticStatus = neoplasticStatus;
	}
	public List<EvsSubconcept> getSubconcepts() {
		return subconcepts;
	}
	public void setSubconcepts(List<EvsSubconcept> subconcepts) {
		this.subconcepts = subconcepts;
	}
	public List<EvsSuperconcept> getSuperconcepts() {
		return superconcepts;
	}
	public void setSuperconcepts(List<EvsSuperconcept> superconcepts) {
		this.superconcepts = superconcepts;
	}
	public List<String> getSemanticTypes() {
		return semanticTypes;
	}
	public void setSemanticTypes(List<String> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	public List<EvsSynonym> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<EvsSynonym> synonyms) {
		this.synonyms = synonyms;
	}
	public List <EvsAdditionalProperty> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(List <EvsAdditionalProperty> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	public boolean getIsStage() {
		return isStage;
	}
	public void setIsStage(boolean isStage) {
		this.isStage = isStage;
	}
}

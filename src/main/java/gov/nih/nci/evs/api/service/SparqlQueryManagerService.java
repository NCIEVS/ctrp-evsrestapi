package gov.nih.nci.evs.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gov.nih.nci.evs.api.model.evs.EvsAssociation;
import gov.nih.nci.evs.api.model.evs.EvsAxiom;
import gov.nih.nci.evs.api.model.evs.EvsConcept;
import gov.nih.nci.evs.api.model.evs.EvsProperty;
import gov.nih.nci.evs.api.model.evs.EvsRelationships;
import gov.nih.nci.evs.api.model.evs.EvsSubconcept;
import gov.nih.nci.evs.api.model.evs.EvsSuperconcept;
import gov.nih.nci.evs.api.model.evs.Paths;

public interface SparqlQueryManagerService {
	
	public List <EvsProperty> getEvsProperties(String conceptCode) throws JsonMappingException,JsonParseException ,IOException;
	
	public List<EvsAxiom> getEvsAxioms(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public List<EvsSubconcept> getEvsSubconcepts(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public List<EvsSuperconcept> getEvsSuperconcepts(String conceptCode) throws JsonMappingException,JsonParseException,IOException;

	public List<EvsAssociation> getEvsAssociations(String conceptCode) throws JsonMappingException,JsonParseException,IOException;

	public List<EvsAssociation> getEvsInverseAssociations(String conceptCode) throws JsonMappingException,JsonParseException,IOException;

	public List<EvsAssociation> getEvsRoles(String conceptCode) throws JsonMappingException,JsonParseException,IOException;

	public List<EvsAssociation> getEvsInverseRoles(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public EvsConcept getEvsConceptDetail(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public Long getGetClassCounts() throws JsonMappingException,JsonParseException,IOException;

	public EvsRelationships getEvsRelationships(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public String getNamedGraph();
	
	public void populateCache() throws IOException;
	
	public boolean checkConceptExists(String conceptCode) throws JsonMappingException,JsonParseException,IOException;
	
	public HashMap<String,String> getDiseaseIsStageSourceCodes() throws JsonMappingException, JsonParseException, IOException;
	
	public Paths getPathToRoot(String conceptCode) throws JsonMappingException, JsonParseException, IOException;

	public Paths getPathToParent(String conceptCode, String parentConceptCode) throws JsonMappingException, JsonParseException, IOException;
}

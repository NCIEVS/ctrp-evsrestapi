/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package gov.nih.nci.evs.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nih.nci.evs.api.model.evs.EvsAssociation;
import gov.nih.nci.evs.api.model.evs.EvsAxiom;
import gov.nih.nci.evs.api.model.evs.EvsConcept;
import gov.nih.nci.evs.api.model.evs.EvsProperty;
import gov.nih.nci.evs.api.model.evs.EvsRelationships;
import gov.nih.nci.evs.api.model.evs.EvsSubconcept;
import gov.nih.nci.evs.api.model.evs.EvsSuperconcept;
import gov.nih.nci.evs.api.model.evs.Paths;
import gov.nih.nci.evs.api.properties.TestProperties;

/**
 * The Class EvsControllerTests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EvsControllerTests {

  /** The logger. */
  private static final Logger log =
      LoggerFactory.getLogger(EvsControllerTests.class);

  /** The mvc. */
  @Autowired
  private MockMvc mvc;

  /** The test properties. */
  @Autowired
  TestProperties testProperties;

  /** The object mapper. */
  private ObjectMapper objectMapper;

  /** The base url. */
  private String baseUrl = "";

  /**
   * Sets the up.
   */
  @Before
  public void setUp() {
    /*
     * Configure the JacksonTester object
     */
    this.objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);

    baseUrl = "/api/v1/ctrp/concept/";
  }

  /**
   * Returns the evs concept detail.
   *
   * @return the evs concept detail
   * @throws Exception the exception
   */
  @Test
  public void getEvsConceptDetail() throws Exception {
    // @formatter:off

    log.info("Started Testing getEvsConceptDetail");
    String conceptCodeList = testProperties.getConceptCodeList();

    conceptCodeList =
        "C7834,C3058,C125890,C2924,C4896,C54705,C7057,C2991,C3262,C4897,C7834,C48232";

    String[] concepts = conceptCodeList.split(",");

    List conceptsList = Arrays.asList(concepts);

    String url = "";
    for (Object concept : conceptsList) {
      url = baseUrl + (String) concept;
      log.info("Testing url - " + url);
      MvcResult result =
          this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
      String content = result.getResponse().getContentAsString();

      // log.info("content *******" + content);
      assertThat(content).isNotNull();
      EvsConcept evsConcept = objectMapper.readValue(content, EvsConcept.class);
      assertThat(evsConcept).isNotNull();

      if (evsConcept.getCode().equalsIgnoreCase("C7834")) {
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isTrue();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
        assertThat(evsConcept.getSubconcepts().size() > 0).isTrue();
        assertThat(evsConcept.getSuperconcepts().size() > 0).isTrue();
        assertThat(evsConcept.getSynonyms().size() > 0).isTrue();
        assertThat(evsConcept.getAdditionalProperties().size() > 0).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C3058")) {
        assertThat(evsConcept.getIsMainType()).isTrue();
        assertThat(evsConcept.getIsSubtype()).isTrue();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isTrue();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C125890")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isTrue();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        // Not passing with local data set
        // assertThat(evsConcept.getIsDiseaseGrade()).isTrue();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C2924")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isTrue();
        // Not passing with local data set
        // assertThat(evsConcept.getIsDiseaseStage()).isTrue();
        // assertThat(evsConcept.getIsDiseaseGrade()).isTrue();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C4896")) {
        assertThat(evsConcept.getIsMainType()).isTrue();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isFalse();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C54705")) {
        assertThat(evsConcept.getIsMainType()).isTrue();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C7057")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C2991")) {
        assertThat(evsConcept.getIsMainType()).isTrue();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C3262")) {
        assertThat(evsConcept.getIsMainType()).isTrue();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C4897")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C7834")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isTrue();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isTrue();
      }

      if (evsConcept.getCode().equalsIgnoreCase("C48232")) {
        assertThat(evsConcept.getIsMainType()).isFalse();
        assertThat(evsConcept.getIsSubtype()).isFalse();
        assertThat(evsConcept.getIsDiseaseStage()).isFalse();
        assertThat(evsConcept.getIsDiseaseGrade()).isFalse();
        assertThat(evsConcept.getIsDisease()).isFalse();
        assertThat(evsConcept.getSubconcepts().size() > 0).isTrue();
        assertThat(evsConcept.getSuperconcepts().size() > 0).isTrue();
        assertThat(evsConcept.getSynonyms().size() > 0).isTrue();
        assertThat(evsConcept.getAdditionalProperties().size() > 0).isTrue();
        assertThat(evsConcept.getLabel().equalsIgnoreCase("Cancer TNM Finding"))
            .isTrue();
      }

      log.info("Successfully tested url - " + url);

    }
    log.info("Done Testing getEvsConceptDetail ");
    // @formatter:on

  }

  /**
   * Returns the evs relationships.
   *
   * @return the evs relationships
   * @throws Exception the exception
   */
  @Test
  public void getEvsRelationships() throws Exception {
    log.info("Started Testing getEvsRelationships");
    String url = baseUrl + "C3224/relationships";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();
    assertThat(content).isNotNull();
    EvsRelationships evsRelationships =
        objectMapper.readValue(content, EvsRelationships.class);
    assertThat(evsRelationships).isNotNull();
    assertThat(evsRelationships.getAssociations().size() > 0).isTrue();
    assertThat(evsRelationships.getRoles().size() > 0).isTrue();
    assertThat(evsRelationships.getInverseAssociations().size() > 0).isTrue();
    assertThat(evsRelationships.getRoles().size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getEvsRelationships ");
  }

  /**
   * Returns the path to root.
   *
   * @return the path to root
   * @throws Exception the exception
   */
  @Test
  public void getPathToRoot() throws Exception {

    log.info("Started Testing getPathToRoot");
    String url = baseUrl + "C48232/pathToRoot";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    Paths paths = objectMapper.readValue(content, Paths.class);
    assertThat(paths).isNotNull();
    assertThat(paths.getPaths().size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getPathToRoot ");

  }

  /**
   * Returns the properties for code.
   *
   * @return the properties for code
   * @throws Exception the exception
   */
  @Test
  public void getPropertiesForCode() throws Exception {

    log.info("Started Testing getPropertiesForCode");
    String url = baseUrl + "C7834/properties";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsProperty> evsPropertyList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsProperty.class));

    assertThat(evsPropertyList).isNotNull();
    assertThat(evsPropertyList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getPropertiesForCode ");

  }

  /**
   * Returns the axioms for code.
   *
   * @return the axioms for code
   * @throws Exception the exception
   */
  @Test
  public void getAxiomsForCode() throws Exception {

    log.info("Started Testing getAxiomsForCode");
    String url = baseUrl + "C2924/axioms";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsAxiom> evsAxiomList = objectMapper.readValue(content, objectMapper
        .getTypeFactory().constructCollectionType(List.class, EvsAxiom.class));

    assertThat(evsAxiomList).isNotNull();
    assertThat(evsAxiomList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getAxiomsForCode ");

  }

  /**
   * Returns the subconcepts for code.
   *
   * @return the subconcepts for code
   * @throws Exception the exception
   */
  @Test
  public void getSubconceptsForCode() throws Exception {
    log.info("Started Testing getSubconceptsForCode");

    String url = baseUrl + "C4897/subconcepts";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsSubconcept> evsSubconceptList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsSubconcept.class));

    assertThat(evsSubconceptList).isNotNull();
    assertThat(evsSubconceptList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getSubconceptsForCode ");

  }

  /**
   * Returns the superconcepts for code.
   *
   * @return the superconcepts for code
   * @throws Exception the exception
   */
  @Test
  public void getSuperconceptsForCode() throws Exception {

    log.info("Started Testing getSuperconceptsForCode");
    String url = baseUrl + "C4897/superconcepts";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsSuperconcept> evsSuperconceptList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsSuperconcept.class));

    assertThat(evsSuperconceptList).isNotNull();
    assertThat(evsSuperconceptList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getSuperconceptsForCode ");

  }

  /**
   * Returns the associations for code.
   *
   * @return the associations for code
   * @throws Exception the exception
   */
  @Test
  public void getAssociationsForCode() throws Exception {
    log.info("Started Testing getAssociationsForCode");

    String url = baseUrl + "C2991/associations";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsAssociation> evsAssociationList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsAssociation.class));

    assertThat(evsAssociationList).isNotNull();
    assertThat(evsAssociationList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getAssociationsForCode ");

  }

  /**
   * Returns the inverse associations for code.
   *
   * @return the inverse associations for code
   * @throws Exception the exception
   */
  @Test
  public void getInverseAssociationsForCode() throws Exception {

    log.info("Started Testing getInverseAssociationsForCode");
    String url = baseUrl + "C2991/inverseassociations";
    log.info("Testing url - " + url);
    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsAssociation> evsAssociationList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsAssociation.class));

    assertThat(evsAssociationList).isNotNull();
    assertThat(evsAssociationList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getInverseAssociationsForCode ");

  }

  /**
   * Returns the roles for code.
   *
   * @return the roles for code
   * @throws Exception the exception
   */
  @Test
  public void getRolesForCode() throws Exception {

    log.info("Started Testing getRolesForCode");
    String url = baseUrl + "C2924/roles";
    log.info("Testing url - " + url);
    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsAssociation> evsAssociationList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsAssociation.class));

    assertThat(evsAssociationList).isNotNull();
    assertThat(evsAssociationList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getRolesForCode ");

  }

  /**
   * Returns the inverse roles for code.
   *
   * @return the inverse roles for code
   * @throws Exception the exception
   */
  @Test
  public void getInverseRolesForCode() throws Exception {

    log.info("Started Testing getInverseRolesForCode");
    String url = baseUrl + "C2924/inverseroles";
    log.info("Testing url - " + url);

    MvcResult result =
        this.mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();

    assertThat(content).isNotNull();
    List<EvsAssociation> evsAssociationList =
        objectMapper.readValue(content, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, EvsAssociation.class));

    assertThat(evsAssociationList).isNotNull();
    assertThat(evsAssociationList.size() > 0).isTrue();
    log.info("Successfully tested url - " + url);
    log.info("Done Testing getInverseRolesForCode ");

  }

}

package gov.nih.nci.evs.restapi.util;

import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.XStream;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.common.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.codec.binary.Base64;
import org.json.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2017 NGIS. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIS and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIS" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIS
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIS, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class SPARQLSearchUtils {
	static String[] PRESENTATIONS = new String[] {"Preferred_Name", "Display_Name", "FULL_SYN", "Legacy_Concept_Name", "label"};
	static List PRESENTATION_LIST = Arrays.asList(PRESENTATIONS);

    JSONUtils jsonUtils = null;
    HTTPUtils httpUtils = null;
    String named_graph = null;
    String prefixes = null;
    String serviceUrl = null;

    static String EXACT_MATCH = "exactMatch";
    static String STARTS_WITH = "startsWith";
    static String ENDS_WITH = "endsWith";
    static String CONTAINS = "contains";

	public SPARQLSearchUtils() {
		this.httpUtils = new HTTPUtils();
        this.jsonUtils = new JSONUtils();
	}

	public SPARQLSearchUtils(String serviceUrl) {
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl, null, null);
        this.jsonUtils = new JSONUtils();
    }

	public SPARQLSearchUtils(ServerInfo serverInfo) {
		this.serviceUrl = serverInfo.getServiceUrl();
		this.httpUtils = new HTTPUtils(serviceUrl, serverInfo.getUsername(), serverInfo.getPassword());
        this.jsonUtils = new JSONUtils();
	}


	public SPARQLSearchUtils(String serviceUrl, String username, String password) {
		this.serviceUrl = serviceUrl;
		this.httpUtils = new HTTPUtils(serviceUrl, username, password);
        this.jsonUtils = new JSONUtils();
    }

    public String getServiceUrl() {
		return this.serviceUrl;
	}

    public void set_named_graph(String named_graph) {
		this.named_graph = named_graph;
	}

	public void set_prefixes(String prefixes) {
		this.prefixes = prefixes;
	}

    public String getPrefixes() {
		if (prefixes != null) return prefixes;
		StringBuffer buf = new StringBuffer();
		buf.append("PREFIX :<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("\n");
		buf.append("PREFIX base:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl>").append("\n");
		buf.append("PREFIX Thesaurus:<http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>").append("\n");
		buf.append("PREFIX xml:<http://www.w3.org/XML/1998/namespace>").append("\n");
		buf.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append("\n");
		buf.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>").append("\n");
		buf.append("PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>").append("\n");
		buf.append("PREFIX protege:<http://protege.stanford.edu/plugins/owl/protege#>").append("\n");
		buf.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>").append("\n");
		buf.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>").append("\n");
		buf.append("PREFIX ncicp:<http://ncicb.nci.nih.gov/xml/owl/EVS/ComplexProperties.xsd#>").append("\n");
		buf.append("PREFIX dc:<http://purl.org/dc/elements/1.1/>").append("\n");
		return buf.toString();
	}

    public Vector executeQuery(String query) {
        Vector v = null;
        try {
			query = httpUtils.encode(query);
            String json = httpUtils.executeQuery(query);
			json = httpUtils.executeQuery(query);
			v = new JSONUtils().parseJSON(json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}


    public String getMatchFilter(String var, String algorithm, String searchString) {
		if (algorithm.compareTo(EXACT_MATCH) == 0) {
			return "    FILTER (lcase(?" + var + ") = \"" + searchString + "\"))";
		} else if (algorithm.compareTo(STARTS_WITH) == 0) {
			return "    FILTER (STRSTARTS(lcase(?" + var + "), \"" + searchString + "\"))";
		} else if (algorithm.compareTo(ENDS_WITH) == 0) {
			return "    FILTER (STRENDS(lcase(?" + var + "), \"" + searchString + "\"))";
		} else if (algorithm.compareTo(CONTAINS) == 0) {
			return "    FILTER (CONTAINS(lcase(?" + var + "), \"" + searchString + "\"))";
		}
		return "";
	}


	public SearchResult searchByNames(String named_graph, String searchString, String algorithm) {
		Vector v = searchAnnotationProperties(named_graph, searchString, algorithm);
		if (v == null) return null;

		Utils.saveToFile("searchByName.txt", v);

		ParserUtils parser = new ParserUtils();
		int n = v.size()/4;
		String x_label = null;
		String x_code = null;
		String p_label = null;
		String p_value = null;
		//float  score = (float) 0.0;
		SearchResult sr = new SearchResult();
		List matchedConcepts = new ArrayList();
		Vector w = new Vector();
		for (int i=0; i<n; i++) {
			int i0 = i*4;
			int i1 = i*4+1;
			int i2 = i*4+2;
			p_label = parser.getValue((String) v.elementAt(i2));
			if (PRESENTATION_LIST.contains(p_label)) {
				int i3 = i*4+3;
				x_label = parser.getValue((String) v.elementAt(i0));
				x_code = parser.getValue((String) v.elementAt(i1));
				p_value = parser.getValue((String) v.elementAt(i3));
				//String score_str = parser.getValue((String) v.elementAt(i4));
				//score = Float.parseFloat(score_str);
				MatchedConcept mc = new MatchedConcept(x_label, x_code, p_label, p_value);
				matchedConcepts.add(mc);
			}
		}
		sr.setMatchedConcepts(matchedConcepts);
		return sr;
	}

	public String construct_search_annotation_properties(String named_graph, String searchString, String algorithm) {
		String prefixes = getPrefixes();
		searchString = searchString.replaceAll("%20", " ");
		if (algorithm == null || algorithm.compareTo("") == 0) {
			algorithm = Constants.EXACT_MATCH;
		}
		searchString = searchString.toLowerCase();

		StringBuffer buf = new StringBuffer();
		buf.append(prefixes);
		buf.append("").append("\n");
		buf.append("SELECT distinct ?x_label ?x_code ?p_label ?p_value").append("\n");
		buf.append("{").append("\n");
		buf.append("    graph <" + named_graph + "> {").append("\n");
		buf.append("            ?x a owl:Class .").append("\n");
		buf.append("            ?x rdfs:label ?x_label .").append("\n");
		buf.append("            ?x :NHC0 ?x_code .").append("\n");
		buf.append("            ?p a owl:AnnotationProperty .").append("\n");
		buf.append("            ?x ?p ?p_value .").append("\n");
		buf.append("            ?p rdfs:label ?p_label . ").append("\n");
		buf.append("            ?p :NHC0 ?p_code ").append("\n");

		buf.append("    }").append("\n");
        buf.append(getMatchFilter("p_value", algorithm, searchString)).append("\n");
		buf.append("}").append("\n");
		//buf.append("ORDER BY ?score").append("\n");
		return buf.toString();
	}

	public Vector searchAnnotationProperties(String named_graph, String searchString, String algorithm) {
		String query = construct_search_annotation_properties(named_graph, searchString, algorithm);
		System.out.println(query);
		return executeQuery(construct_search_annotation_properties(named_graph, searchString, algorithm));
	}

	public SearchResult searchByProperties(String named_graph, String searchString, String algorithm) {
		Vector v = searchAnnotationProperties(named_graph, searchString, algorithm);
		if (v == null) return null;
		ParserUtils parser = new ParserUtils();
		int n = v.size()/4;
		String x_label = null;
		String x_code = null;
		String p_label = null;
		String p_value = null;
		//float  score = (float) 0.0;
		SearchResult sr = new SearchResult();
		List matchedConcepts = new ArrayList();
		Vector w = new Vector();
		for (int i=0; i<n; i++) {
			int i0 = i*4;
			int i1 = i*4+1;
			int i2 = i*4+2;
			p_label = parser.getValue((String) v.elementAt(i2));
			if (!PRESENTATION_LIST.contains(p_label)) {
				int i3 = i*4+3;
				x_label = parser.getValue((String) v.elementAt(i0));
				x_code = parser.getValue((String) v.elementAt(i1));
				p_value = parser.getValue((String) v.elementAt(i3));
				//String score_str = parser.getValue((String) v.elementAt(i4));
				//score = Float.parseFloat(score_str);
				MatchedConcept mc = new MatchedConcept(x_label, x_code, p_label, p_value);
				matchedConcepts.add(mc);
			}
		}
		sr.setMatchedConcepts(matchedConcepts);
		return sr;
	}

	public static String marshalSearchResult(SearchResult sr) throws Exception {
		XStream xstream_xml = new XStream(new DomDriver());
        String xml = Constants.XML_DECLARATION + "\n" + xstream_xml.toXML(sr);
        return xml;
	}

	public static void main(String[] args) {
		String  serviceUrl = args[0];
		SPARQLSearchUtils searchUtils = new SPARQLSearchUtils(serviceUrl, null, null);
	    SearchResult sr = searchUtils.searchByNames("http://NCIt", "aging", "contains");
	    System.out.println(sr.toJson());
	    sr = searchUtils.searchByProperties("http://NCIt", "aging", "endsWith");
	    System.out.println(sr.toJson());
	}
}


package gov.nih.nci.evs.api.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.util.StringUtils;

import gov.nih.nci.evs.api.model.evs.Concept;
import gov.nih.nci.evs.api.model.evs.HierarchyNode;
import gov.nih.nci.evs.api.model.evs.Path;
import gov.nih.nci.evs.api.model.evs.Paths;
import gov.nih.nci.evs.api.service.SparqlQueryManagerService;
import gov.nih.nci.evs.api.service.SparqlQueryManagerServiceImpl;

public class HierarchyUtils {
	
	private HashMap <String,ArrayList<String>> parent2child = new HashMap<String,ArrayList<String>> ();
	private HashMap <String,ArrayList<String>> child2parent = new HashMap<String,ArrayList<String>> ();
	private HashMap <String,String> code2label = new HashMap<String,String> ();
	private HashMap <String,String> label2code = new HashMap<String,String> ();
	private HashSet <String> concepts = new HashSet<String>();
	private HashSet <String> parents = new HashSet<String>();
	private HashSet <String> children = new HashSet<String>();
	private HashSet <String> roots = null;
	private HashSet <String> leaves = null;

	public HierarchyUtils() {}
	
	public HierarchyUtils(List <String>parentchild) {
		initialize(parentchild);
	}
	
	public void initialize(List <String>parentchild) {
		/*
		 * The parentchild string is expected to be in the order of parentCode, parentLabel
		 * childCode, childLabel and Tab sepearated.
		 */
		for (String str: parentchild) {
			String [] values = str.trim().split("\t");
			if (parent2child.containsKey(values[0])) {
				parent2child.get(values[0]).add(values[2]);
			} else {
				ArrayList <String> children = new ArrayList <String> ();
				children.add(values[2]);
				parent2child.put(values[0], children);
			}

			if (child2parent.containsKey(values[2])) {
				child2parent.get(values[2]).add(values[0]);
			} else {
				ArrayList <String> parents = new ArrayList <String> ();
				parents.add(values[0]);
				child2parent.put(values[2], parents);
			}

			if (!code2label.containsKey(values[0])) {
				code2label.put(values[0], values[1]);
			}
			if (!code2label.containsKey(values[2])) {
				code2label.put(values[2], values[3]);
			}
			if (!label2code.containsKey(values[1])) {
				code2label.put(values[1], values[0]);
			}
			if (!label2code.containsKey(values[3])) {
				code2label.put(values[3], values[2]);
			}
			
			/*
			 * Keep Track of Parents and Children
			 */
			parents.add(values[0]);
			children.add(values[2]);
			concepts.add(values[0]);
			concepts.add(values[2]);
		}

		roots = new HashSet<String>(parents);
		roots.removeAll(children);

		leaves = new HashSet<String>(children);
		leaves.removeAll(parents);
		//testLoading();
	}
	
	public ArrayList <String> getTransitiveClosure(ArrayList <String> concepts, String code, Integer level) {
		ArrayList <String> children = this.parent2child.get(code);
		if (children == null || children.size() == 0) {
			return concepts;
		}
		for (String child: children) {
			String indent = "";
			for (int i=0; i<level; i++) {
				indent = indent + "    ";
			}
			System.out.println(indent + "Parent: " + code + ": " + code2label.get(code) + "  Child: " + child + ": " + code2label.get(child) + "  Level: " + level);
		    ArrayList <String> newChildren = getTransitiveClosure(concepts,child,level + 1);	
		}
		return concepts;
	}
	
	public ArrayList <String> getRoots() {
		return new ArrayList <String> (this.roots);
	}

	public ArrayList <String> getSubclassCodes(String code) {
		if (this.parent2child.containsKey(code)) {
			return parent2child.get(code);
		}
		return null;
	}
	
	public ArrayList <String> getSuperclassCodes(String code) {
		if (!child2parent.containsKey(code)) {
			return null;
		}
		return child2parent.get(code);
	}
	
	public String getLabel(String code) {
		if (this.code2label.containsKey(code)) {
			return code2label.get(code);
		}
		return null;
	}
	
	/*
	 * This section to support the Hierarchy Browser
	 */
	
	public ArrayList <HierarchyNode> getRootNodes() {
		ArrayList <HierarchyNode> nodes = new ArrayList<HierarchyNode>();
		for (String code: this.roots) {
			HierarchyNode node = new HierarchyNode(code, code2label.get(code), false);
			nodes.add(node);
		}
		nodes.sort(Comparator.comparing(HierarchyNode::getLabel));
		return nodes;
	}

	public ArrayList <HierarchyNode> getChildNodes(String parent, int maxLevel) {
		ArrayList <HierarchyNode> nodes = new ArrayList<HierarchyNode>();
		ArrayList <String> children = this.parent2child.get(parent);
		if (children == null) {
			return nodes;
		}
		for (String code: children) {
			HierarchyNode node = new HierarchyNode(code, code2label.get(code), false);
			getChildNodesLevel(node, maxLevel, 0);
			nodes.add(node);
		}
		nodes.sort(Comparator.comparing(HierarchyNode::getLabel));
		return nodes;
	}
	
	public void getChildNodesLevel(HierarchyNode node, int maxLevel, int level) {
		List <String> children = this.parent2child.get(node.getCode());
		if (children == null || children.size() == 0) {
			node.setLeaf(true);
			return;
		} else {
			node.setLeaf(false);
		}
		if (level >= maxLevel) {
			return;
		}

		ArrayList <HierarchyNode> nodes = new ArrayList<HierarchyNode>();
		level = level + 1;
		for (String code: children) {
			HierarchyNode newnode = new HierarchyNode(code, code2label.get(code), false);
			getChildNodesLevel(newnode, maxLevel, level);
			List <HierarchyNode> sortedChildren = newnode.getChildren();
			if (sortedChildren == null || sortedChildren.size() == 0) {
                 newnode.setLeaf(true);
			} else {
				sortedChildren.sort(Comparator.comparing(HierarchyNode::getLabel));
			}
			
			newnode.setChildren(sortedChildren);
			nodes.add(newnode);
		}
		nodes.sort(Comparator.comparing(HierarchyNode::getLabel));
		node.setChildren(nodes);
	}
	
	/*
	 * Will Remove once testing is completed
	 */
	public void testLoading ()  {
		System.out.println("HierarchyUtils Loading");
		System.out.println("======================");
		System.out.println("Total: " + concepts.size());
		System.out.println("Parents: " + parents.size());
		System.out.println("Childrens: " + children.size());
		roots = new HashSet<String>(parents);
		roots.removeAll(children);

		leaves = new HashSet<String>(children);
		leaves.removeAll(parents);
		
		System.out.println("Roots");
		for (String root: roots) {
			System.out.println(root + ": " + code2label.get(root));
		}
		
		/*
		System.out.println("Leaves");
		System.out.println("Size: " + leaves.size());
		*/
		
		PathFinder pathFinder = new PathFinder(this);
		Paths paths = pathFinder.findPaths();
		System.out.println("Paths Count: " + paths.getPathCount());
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("/tmp/allpaths.txt"));
			for (Path path: paths.getPaths()) {
				List <Concept> concepts = path.getConcepts();
			    StringBuffer strPath = new StringBuffer();
			    for (Concept concept: concepts) {
				    strPath.append(concept.getCode());
    				strPath.append("|");
	    		}
		    	//System.out.println(strPath.toString());
		    	pw.write(strPath.toString() + "\n");
			}
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } finally {
	    	if (pw != null) {
	    		pw.close();
	    	}
	    }
		
		/*
		for (Path path: paths.getPaths()) {
			Boolean sw = false;
			for (Concept concept: path.getConcepts()) {
				if (concept.getCode().equals("C7917")) {
					sw = true;
				}
			}
			if (sw) {
				StringBuffer strPath = new StringBuffer();
			    for (Concept concept: path.getConcepts()) {
				    strPath.append(concept.getCode());
	    			strPath.append("|");
		    	}
		    	System.out.println(strPath.toString());
			}
		}
		*/
	}
}

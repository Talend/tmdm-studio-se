// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.editors.xsdeditor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;

/**
 * created by liusonbo on 2013-6-8
 */
public class TreeExpandHelper {

    private List<ExpandInfoNode> expandedElements;

    private List<ExpandInfoNode> expandedTypes;

    public TreeExpandHelper() {
        expandedElements = new ArrayList<TreeExpandHelper.ExpandInfoNode>();
        expandedTypes = new ArrayList<TreeExpandHelper.ExpandInfoNode>();
    }

    public void recordExpandState(DataModelMainPage mainPage) {
        if (mainPage == null) {
            return;
        }

        cleanCache();

        TreeViewer elementsViewer = mainPage.getElementsViewer();
        TreeViewer typesViewer = mainPage.getTypesViewer();

        expandedElements = getExpandedNodes(elementsViewer.getExpandedTreePaths());
        expandedTypes = getExpandedNodes(typesViewer.getExpandedTreePaths());
    }

    public void recoverExpandState(DataModelMainPage mainPage) {
        if (mainPage == null) {
            return;
        }

        Object[] expandedEntityElements = getExpandedEntityElements(mainPage);
        mainPage.getElementsViewer().setExpandedElements(expandedEntityElements);

        Object[] expandedTypeElements = getExpandedTypeElements(mainPage);
        mainPage.getTypesViewer().setExpandedElements(expandedTypeElements);

        cleanCache();
    }

    private List<ExpandInfoNode> getExpandedNodes(TreePath[] expandedElementPaths) {
        TreePath[] expandedPaths = removeInvalidTreePaths(expandedElementPaths);

        List<ExpandInfoNode> expanded = new ArrayList<ExpandInfoNode>();
        ExpandInfoNode rootNode = null;
        for (TreePath path : expandedPaths) {
            int segmentCount = path.getSegmentCount();

            Object firstSegment = path.getFirstSegment();
            rootNode = ExpandInfoNode.create(getName(firstSegment), firstSegment.getClass().getName());
            if (expanded.contains(rootNode)) {
                rootNode = expanded.get(expanded.indexOf(rootNode));
            } else {
                expanded.add(rootNode);
            }

            for (int i = 1; i < segmentCount; i++) {
                Object segment = path.getSegment(i);
                ExpandInfoNode newNode = ExpandInfoNode.create(getName(segment), segment.getClass().getName());
                if (rootNode.childs == null) {
                    rootNode.addChild(newNode);
                } else {
                    if (rootNode.childs.contains(newNode)) {
                        newNode = rootNode.childs.get(rootNode.childs.indexOf(newNode));
                    } else {
                        rootNode.addChild(newNode);
                    }
                }

                rootNode = newNode;
            }
        }

        return expanded;
    }

    private TreePath[] removeInvalidTreePaths(TreePath[] expandedElementPaths) {
        Map<Integer, Set<TreePath>> pathMaps = new HashMap<Integer, Set<TreePath>>();
        int maxSegmentCount = -1;
        for (TreePath path : expandedElementPaths) {
            int segmentCount = path.getSegmentCount();

            Set<TreePath> pathSet = pathMaps.get(segmentCount);
            if (pathSet == null) {
                pathSet = new HashSet<TreePath>();
                pathMaps.put(segmentCount, pathSet);
            }

            pathSet.add(path);

            //
            if (maxSegmentCount < segmentCount) {
                maxSegmentCount = segmentCount;
            }
        }

        Set<TreePath> roots = pathMaps.get(1);
        if (roots == null || roots.size() == 0) {
            return new TreePath[0];
        }

        // record TreePath by tree level
        List<TreePath> paths = new ArrayList<TreePath>();
        paths.addAll(roots);

        for (int i = 2; i < maxSegmentCount + 1; i++) {
            Set<TreePath> set = pathMaps.get(i);
            if (set == null || set.size() == 0) {
                break;
            }

            Set<TreePath> parents = pathMaps.get(i - 1);
            Iterator<TreePath> iterator = set.iterator();
            while (iterator.hasNext()) {
                TreePath path = iterator.next();
                if (parents.contains(path.getParentPath())) {
                    paths.add(path);
                } else {
                    iterator.remove();
                }
            }
        }

        return paths.toArray(new TreePath[0]);
    }

    // ///
    private Object[] getExpandedEntityElements(DataModelMainPage mainPage) {
        TreeViewer elementsViewer = mainPage.getElementsViewer();
        SchemaTreeContentProvider contentProvider = (SchemaTreeContentProvider) elementsViewer.getContentProvider();
        Object[] xsdDeclarations = getChildren(contentProvider.getXsdSchema(), contentProvider);


        List<Object> result = new ArrayList<Object>();//
        Deque<ExpandInfoNode> nodeStack = new LinkedList<ExpandInfoNode>();//
        Deque<Object> elementStack = new LinkedList<Object>();//

        // record entities
        Map<ExpandInfoNode, XSDElementDeclaration> expandedRoots = new HashMap<ExpandInfoNode, XSDElementDeclaration>();
        for (Object obj : xsdDeclarations) {
            if (obj instanceof XSDElementDeclaration) {
                XSDElementDeclaration decl = (XSDElementDeclaration) obj;
                String name = decl.getName();
                for (ExpandInfoNode node : expandedElements) {
                    if (name.equals(node.name)) {
                        expandedRoots.put(node, decl);

                        result.add(decl);
                        nodeStack.add(node);// /
                        elementStack.add(decl);// /
                        break;
                    }
                }
            }
        }

        while (!nodeStack.isEmpty() && !elementStack.isEmpty()) {
            ExpandInfoNode node = nodeStack.pollFirst();
            Object element = elementStack.pollFirst();

            List<ExpandInfoNode> nodes = node.childs;
            Object[] elementChildren = getChildren(element, contentProvider);

            if (nodes != null && nodes.size() > 0 && elementChildren != null) {
                Map<ExpandInfoNode, Object> nodeElementPairs = getNodeElementPairs(elementChildren, nodes);
                for (ExpandInfoNode node2 : nodeElementPairs.keySet()) {
                    nodeStack.add(node2);
                    elementStack.add(nodeElementPairs.get(node2));
                    result.add(nodeElementPairs.get(node2));
                }
            }
        }

        return result.toArray();
    }

    private Object[] getExpandedTypeElements(DataModelMainPage mainPage) {
        TreeViewer typesViewer = mainPage.getTypesViewer();
        SchemaTreeContentProvider contentProvider = (SchemaTreeContentProvider) typesViewer.getContentProvider();
        Object[] xsdDeclarations = getChildren(contentProvider.getXsdSchema(), contentProvider);

        List<Object> result = new ArrayList<Object>();//
        Deque<ExpandInfoNode> nodeStack = new LinkedList<ExpandInfoNode>();//
        Deque<Object> elementStack = new LinkedList<Object>();//

        // record entities
        Map<ExpandInfoNode, XSDTypeDefinition> expandedRoots = new HashMap<ExpandInfoNode, XSDTypeDefinition>();
        for (Object obj : xsdDeclarations) {
            XSDTypeDefinition type = (XSDTypeDefinition) obj;
            String name = type.getName();
            for (ExpandInfoNode node : expandedTypes) {
                if (name.equals(node.name)) {
                    expandedRoots.put(node, type);

                    result.add(type);
                    nodeStack.add(node);// /
                    elementStack.add(type);// /
                    break;
                }
            }
        }

        while (!nodeStack.isEmpty() && !elementStack.isEmpty()) {
            ExpandInfoNode node = nodeStack.pollFirst();
            Object element = elementStack.pollFirst();

            List<ExpandInfoNode> nodes = node.childs;
            Object[] elementChildren = getChildren(element, contentProvider);

            if (nodes != null && nodes.size() > 0 && elementChildren != null) {
                Map<ExpandInfoNode, Object> nodeElementPairs = getNodeElementPairs(elementChildren, nodes);
                for (ExpandInfoNode node2 : nodeElementPairs.keySet()) {
                    nodeStack.add(node2);
                    elementStack.add(nodeElementPairs.get(node2));
                    result.add(nodeElementPairs.get(node2));
                }
            }
        }

        return result.toArray();
    }

    private Object[] getChildren(Object parent, SchemaTreeContentProvider contentProvider) {
        Object[] children = contentProvider.getChildren(parent);
        return children;
    }

    private Map<ExpandInfoNode, Object> getNodeElementPairs(Object[] elementChildrens, List<ExpandInfoNode> nodes) {
        Map<ExpandInfoNode, Object> pairs = new HashMap<ExpandInfoNode, Object>();

        for (Object child : elementChildrens) {
            for (ExpandInfoNode node : nodes) {
                if (isSameXSDElement(child, node)) {
                    pairs.put(node, child);
                }
            }
        }

        return pairs;
    }

    private boolean isSameXSDElement(Object objA, ExpandInfoNode objB) {
        if (objA != null && objB != null) {
            objA = ExpandInfoNode.create(getName(objA), objA.getClass().getName());
            return objB.equals(objA);
        }

        return false;
    }

    private String getName(Object objA) {
        if (objA instanceof XSDElementDeclaration) {
            XSDElementDeclaration decl = (XSDElementDeclaration) objA;
            return decl.getName();
        }

        if (objA instanceof XSDModelGroup) {
            XSDModelGroup goup = (XSDModelGroup) objA;
            XSDParticle particle = (XSDParticle) goup.getContainer();
            XSDComplexTypeDefinition complexTypeDefinition = (XSDComplexTypeDefinition) particle.getContainer();
            String name = complexTypeDefinition.getName();

            return name;
        }

        if (objA instanceof XSDModelGroupDefinition) {
            XSDModelGroupDefinition goupDef = (XSDModelGroupDefinition) objA;
            return goupDef.getName();
        }

        if (objA instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) objA;
            if (particle.getTerm() instanceof XSDElementDeclaration) {
                XSDElementDeclaration decl = (XSDElementDeclaration) particle.getTerm();
                return decl.getName();
            }
        }

        if (objA instanceof XSDAnnotation) {
            return null;
        }

        if (objA instanceof XSDIdentityConstraintDefinition) {
            XSDIdentityConstraintDefinition constraint = (XSDIdentityConstraintDefinition) objA;
            return constraint.getName();
        }

        if (objA instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition simpleDefine = (XSDSimpleTypeDefinition) objA;
            return simpleDefine.getName();
        }

        if (objA instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexDefine = (XSDComplexTypeDefinition) objA;
            return complexDefine.getName();
        }

        return null;
    }

    public void cleanCache() {
        expandedElements.clear();
        expandedTypes.clear();
    }

    // /////////////////

    static class ExpandInfoNode {

        public String name;

        public String type;

        public List<ExpandInfoNode> childs;

        public static ExpandInfoNode create(String name, String type) {
            return new ExpandInfoNode(name, type);
        }

        public ExpandInfoNode(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public void addChild(ExpandInfoNode obj) {
            if (childs == null) {
                childs = new ArrayList<ExpandInfoNode>();
            }

            childs.add(obj);
        }

        public boolean hasChildren() {
            if (childs == null || childs.size() == 0) {
                return false;
            }

            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (obj instanceof ExpandInfoNode) {
                ExpandInfoNode node = (ExpandInfoNode) obj;
                if (name != null) {
                    return name.equals(node.name) && type.equals(node.type);
                } else {
                    return type.equals(node.type);
                }
            }

            return false;
        }
    }
}

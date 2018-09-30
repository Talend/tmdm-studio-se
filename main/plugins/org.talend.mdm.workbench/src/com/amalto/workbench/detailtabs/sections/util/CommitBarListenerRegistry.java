// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.detailtabs.sections.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amalto.workbench.detailtabs.sections.CommitSection;
import com.amalto.workbench.detailtabs.sections.composites.CommitBarComposite.CommitBarListener;

public class CommitBarListenerRegistry {

    private static CommitBarListenerRegistry INSTANCE;

    private Map<String, List<CommitBarListener>> tabId2Listeners = new HashMap<String, List<CommitBarListener>>();
    
    private List<CommitSection> commitSecs=new ArrayList<CommitSection>();

    private Map<String, CommitSection> tabId2CommitSection=new HashMap<String, CommitSection>();
    
    private CommitBarListenerRegistry() {
    }

    public static CommitBarListenerRegistry getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new CommitBarListenerRegistry();
        }

        return INSTANCE;
    }

    public void registListener(String tabID, CommitBarListener listener) {
        if (tabID != null && listener != null) {
            List<CommitBarListener> listeners = tabId2Listeners.get(tabID);
            if (listeners == null) {
                listeners = new ArrayList<CommitBarListener>();
                tabId2Listeners.put(tabID, listeners);
            }

            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void registCommitSection(String tabID, CommitSection commitSec){
        if (tabID != null) {
            tabId2CommitSection.put(tabID, commitSec);
        }
    }
    public void unregistCommitSection(String tabID){
        if (tabID != null) {
            tabId2CommitSection.remove(tabID);
        }
    }
    public CommitSection getRegistCommitSection(String tabID){
        if (tabID != null) {
            return tabId2CommitSection.get(tabID);
        }
        return null;
    }
    public CommitBarListener[] getRegistedListeners(String tabID) {

        if (tabID == null || !tabId2Listeners.containsKey(tabID)) {
            return new CommitBarListener[0];
        }

        return tabId2Listeners.get(tabID).toArray(new CommitBarListener[0]);
    }

    public void removeRegistedListeners(String tabID) {
        if (tabID != null) {
            tabId2Listeners.remove(tabID);
        }
    }

    public CommitBarListener[] moveOutRegistedListeners(String tabID) {
        if (tabID != null) {
            CommitBarListener[] movedListeners = getRegistedListeners(tabID);
            removeRegistedListeners(tabID);
            return movedListeners;
        }
        return new CommitBarListener[0];
    }

    public void unregistListener(CommitBarListener listener) {

        for (List<CommitBarListener> listenersOfTab : tabId2Listeners.values()) {
            listenersOfTab.remove(listener);
        }

        Set<String> needRemovedTabIds = new HashSet<String>();
        for (Entry<String, List<CommitBarListener>> eachTabID2Listeners : tabId2Listeners.entrySet()) {
            if (eachTabID2Listeners.getValue().size() == 0) {
                needRemovedTabIds.add(eachTabID2Listeners.getKey());
            }
        }

        for (String eachNeedRemovedTabId : needRemovedTabIds) {
            tabId2Listeners.remove(eachNeedRemovedTabId);
        }
    }
    
    public void registCommitSection(CommitSection sec){
    	commitSecs.add(sec);
    }
    public void unregistCommitSection(CommitSection sec){
    	commitSecs.remove(sec);
    }

	public List<CommitSection> getCommitSecs() {
		return commitSecs;
	}
    
	public List<CommitBarListener> getAllRegistedListeners(){
		 List<CommitBarListener> listeners = new LinkedList<CommitBarListener>();
		  for (List<CommitBarListener> tabListeners : tabId2Listeners.values()) {
		   listeners.addAll(tabListeners);
		  }
		  return listeners;
	}
    
}

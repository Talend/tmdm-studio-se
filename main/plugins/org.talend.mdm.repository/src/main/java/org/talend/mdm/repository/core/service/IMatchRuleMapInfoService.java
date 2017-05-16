// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.talend.core.IService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo;
import org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo;
import org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer;

/**
 * created by HHB on 2013-8-21 Detailled comment
 * 
 */
public interface IMatchRuleMapInfoService extends IService {

    String T_SWOOSH_ALGORITHM = "T_SwooshAlgorithm"; //$NON-NLS-1$

    void saveMatchRuleMapInfo(Item item);

    MatchRuleMapInfo loadMatchRuleMapInfo(Item item);

    List<String> getAllMatchRuleNames();

    List<MatchRuleDefinition> getAllMatchRules();

    EntityMapInfo findEntityMap(MatchRuleMapInfo mapInfo, String entityName);

    EditingDomain getNewEditingDomain();

    MatchRuleDefinition getMatchRuleDef(String ruleDefName);

    IRepositoryViewObject getMatchRuleDefViewObject(String ruleDefName);

    MatchRuleMapInfoContainer generateMatchRuleMapInfoContainer(Item item);

    String convertMatchRuleMapInfoContainerToXML(MatchRuleMapInfoContainer container);

    IRepositoryViewObject generateWSMatchRuleObject(Item item);

    List<IRepositoryViewObject> findReferencedDataModels(String matchRuleName);

    void renameEntityMapinfo(Item item, String oldName, String newName);

    void renameElementMapinfo(Item item, Set<String> paths, String newName);

    void deleteEntityMapinfo(Item item, String entityName);
}

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
package org.talend.mdm.repository.ui.wizards.process;

/**
 * Subclass need to implements the interface to generate job template
 */
public interface IMDMJobTemplate {

	/**
	 *
	 * generate job template
	 * @param processType @see NewProcessWizard.BEFORE_TYPE,BEFORE_SAVING...
	 * @param processName
	 * @param infoType
	 * @param processMessage
	 */
	public void generateJobTemplate(int processType, String processName,String infoType, String processMessage);


	/**
	 *
	 * create job or not
	 * @return
	 */
	public boolean createJob();
}

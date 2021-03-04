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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo;

public class LanguageInfo {

	private String language = "";//$NON-NLS-1$

	private String languageISOCode = "";//$NON-NLS-1$

	private String label = "";//$NON-NLS-1$

	public LanguageInfo(String language, String languageISOCode, String label) {
		this.label = label;
		this.languageISOCode = languageISOCode;
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLanguageISOCode() {
		return languageISOCode;
	}

	public void setLanguageISOCode(String languageISOCode) {
		this.languageISOCode = languageISOCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LanguageInfo))
			return false;
		LanguageInfo other = (LanguageInfo) obj;
		if (label.equals(other.getLabel())
				&& language.equals(other.getLanguage())
				&& languageISOCode.equals(other.getLanguageISOCode())) {
			return true;
		}
		return false;
	}

}

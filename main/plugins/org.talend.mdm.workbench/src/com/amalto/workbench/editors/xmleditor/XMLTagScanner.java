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
package com.amalto.workbench.editors.xmleditor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class XMLTagScanner extends RuleBasedScanner {

    public XMLTagScanner(ColorManager manager) {
        IToken string = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.STRING)));

        IRule[] rules = new IRule[3];

        // Add rule for double quotes
        rules[0] = new SingleLineRule("\"", "\"", string, '\\');//$NON-NLS-1$//$NON-NLS-2$
        // Add a rule for single quotes
        rules[1] = new SingleLineRule("'", "'", string, '\\');//$NON-NLS-1$//$NON-NLS-2$
        // Add generic whitespace rule.
        rules[2] = new WhitespaceRule(new XMLWhitespaceDetector());

        setRules(rules);
    }
}

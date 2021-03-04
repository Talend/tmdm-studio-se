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

public class XMLScanner extends RuleBasedScanner {

    public XMLScanner(ColorManager manager) {
        IToken procInstr = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.PROC_INSTR)));

        IRule[] rules = new IRule[2];
        // Add rule for processing instructions
        rules[0] = new SingleLineRule("<?", "?>", procInstr);//$NON-NLS-1$//$NON-NLS-2$
        // Add generic whitespace rule.
        rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());

        setRules(rules);
    }
}

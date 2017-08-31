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
package org.talend.mdm.repository.core.service.wsimpl.servicedoc;

import org.talend.mdm.repository.core.service.ws.AbstractGetDocument;
import org.talend.mdm.repository.i18n.Messages;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class CallJobGetDocument extends AbstractGetDocument {

    /**
     * DOC hbhong CallJobGetDocument constructor comment.
     * 
     * @param twoLettersLanguageCode
     */
    public CallJobGetDocument(String twoLettersLanguageCode) {
        super(twoLettersLanguageCode);
    }

    @Override
    public String getJNDIName() {
        return "callJob"; //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        return "The service call job"; //$NON-NLS-1$
    }

    @Override
    public String getDocument() {
        return "CallJob Service\n" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "Parameters\n" //$NON-NLS-1$
                + "   url [mandatory]: the webservice port URL to the TIS Server" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "       or the local talend job URL: ltj://<jobName>/<jobVersion>/[jobMainClass]" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   contextParam   : the contextParam of the tis job" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "       name: the name of the context param" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "       value: the value of context param, the value will be viewed as a priple" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "              variable if the value is embraced with a brace, its content will be like: " //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "              <exchange><report>{update report here}</report><item>{item pointed to by Update/Key}</item></exchange>\n" //$NON-NLS-1$
                + "   username [optional]: the username to use for the call" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   password [optional]: the password to  use for the call" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   contentType [optional]: the contentType of the returned data. Defaults to 'text/xml'" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   conceptMapping [optional]: Directly map the result of a TIS call to a MDM entity" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "       concept: the name of the concept" + "\n" + "       fields: mapping rule with json format" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "Example1" + "\n" + "   <configuration>" + "\n" + "       <url>http://server:port/TISService/TISPort</url>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       <contextParam>" + "\n" + "           <name>firstname</name>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           <value>jack</value>" + "\n" + "       </contextParam>" + "\n" + "       <contextParam>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "           <name>lastname</name>" + "\n" + "           <value>jones</value>" + "\n" + "       </contextParam>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       <contextParam>" + "\n" + "           <name>xmlInput</name>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           <value>{}</value>" + "\n" + "       </contextParam>" + "\n" + "       <username>john</username>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       <password>doe</password>" + "\n" + "       <conceptMapping>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           <concept>User</concept>" + "\n" + "           <fields>" + "\n" + "             {" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "             p1:firstname," + "\n" + "             p2:lastname" + "\n" + "             }" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "           </fields>" + "\n" + "       </conceptMapping>" + "\n" + "   </configuration>" + "\n" + "Example2" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                + "\n" + "   <configuration>" + "\n" + "       <url>ltj://tiscall_multi_return/0.1</url>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "       <contextParam>" + "\n" + "           <name>nb_line</name>" + "\n" + "           <value>5</value>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       </contextParam>" + "\n" + "   </configuration>" + "\n" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }

}

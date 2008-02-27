/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFileUploadField.java,v $
 * Date   : $Date: 2008/02/19 11:55:26 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Module Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Module Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Module Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.formgenerator;

import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a file upload field.<p>
 * 
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.3 $
 * 
 * @since 7.0.4 
 */
public class CmsFileUploadField extends A_CmsField {

    /** HTML field type: file. */
    private static final String TYPE = "file";

    /** The size of the uploaded file. */
    private int m_fileSize;

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, org.opencms.i18n.CmsMessages, String, boolean)
     */
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer();
        String fieldLabel = getLabel();
        String errorMessage = "";
        String mandatory = "";

        if (CmsStringUtil.isNotEmpty(errorKey)) {

            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }

            errorMessage = messages.key("form.html.error.start") + errorMessage + messages.key("form.html.error.end");
            fieldLabel = messages.key("form.html.label.error.start")
                + fieldLabel
                + messages.key("form.html.label.error.end");
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(fieldLabel).append(mandatory).append(
            messages.key("form.html.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.field.start")).append("<input type=\"file\" name=\"").append(getName()).append(
            "\" value=\"").append(CmsStringUtil.escapeHtml(getValue())).append("\"").append(
            formHandler.getFormConfiguration().getFormFieldAttributes()).append("/>").append(errorMessage).append(
            messages.key("form.html.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

        return buf.toString();
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * Sets the size of the uploaded file.<p>
     * 
     * @param fileSize the file size
     */
    public void setFileSize(int fileSize) {

        m_fileSize = fileSize;
    }

    /**
     * Validates the input value of this field.<p>
     * 
     * @return {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    protected String validateValue() {

        // validate non-empty values with given regular expression
        if (CmsStringUtil.isNotEmpty(getValue()) && CmsStringUtil.isNotEmpty(getValidationExpression())) {
            Map substitutions = new HashMap();
            substitutions.put("<", "");
            substitutions.put("kb", "");

            int maxSize = Integer.parseInt(CmsStringUtil.substitute(getValidationExpression(), substitutions)) * 1024;
            try {
                if (m_fileSize > maxSize) {
                    return CmsFormHandler.ERROR_VALIDATION;
                }
            } catch (Exception e) {
                // syntax error in regular expression, log to opencms.log
                CmsLog.getLog(CmsFileUploadField.class).error(
                    Messages.get().getBundle().key(Messages.LOG_ERR_PATTERN_SYNTAX_0),
                    e);
            }
        }
        return "";
    }
}
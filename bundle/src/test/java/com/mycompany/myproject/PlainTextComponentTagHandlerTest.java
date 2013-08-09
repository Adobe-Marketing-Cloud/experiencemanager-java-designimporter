/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2012 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package com.mycompany.myproject;

import com.day.cq.dam.indd.PageComponent;
import com.day.cq.wcm.designimporter.parser.HTMLContentType;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlainTextComponentTagHandlerTest extends TagHandlerBaseTest {

    @Test
    public void testComponentCreation() {
        parseHtml(getClass().getClassLoader().getResourceAsStream("textcomponent.html"));
        assertNotNull(getCanvasChildComponents());
        assertTrue(getCanvasChildComponents().size() > 0);
        assertNull(designImportException);
    }

    @Test
    public void testComponentProperties() {
        parseHtml(getClass().getClassLoader().getResourceAsStream("textcomponent.html"));
        PageComponent textComponent = getCanvasChildComponents().get(0);
        assertTrue(textComponent.getChildComponents().size() == 0);
        assertEquals(textComponent.getNameHint(), "text");
        assertEquals(textComponent.getResourceType(), "foundation/components/text");
        String text = (String) textComponent.getProperties().get("text");
        assertTrue(text.contains("This content becomes plain text. All formatting is discarded."));
        assertTrue(text.contains("In other words, html tags are simply stripped off"));
        assertNull(designImportException);
    }

    @Test
    public void testDivGetHtmlContent() {
        parseHtml(getClass().getClassLoader().getResourceAsStream("textcomponent.html"));
        assertNotNull(bodyHtmlContent);
        assertEquals(((String) bodyHtmlContent.get(HTMLContentType.MARKUP)).trim(),
                "<div data-cq-component=\"plaintext\"><sling:include path=\"text\"/></div>".trim());
        assertNull(designImportException);
    }

}

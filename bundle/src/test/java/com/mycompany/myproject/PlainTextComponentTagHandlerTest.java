/*************************************************************************
 * Copyright 2013 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

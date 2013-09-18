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
package com.mycompany.myproject.example;

import com.day.cq.dam.indd.PageComponent;
import com.day.cq.wcm.designimporter.DesignImportException;
import com.day.cq.wcm.designimporter.parser.HTMLContentType;
import com.day.cq.wcm.designimporter.parser.taghandlers.AbstractTagHandler;
import com.day.cq.wcm.designimporter.util.TagUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;

/**
 * This example tag handler translates an html section into the cq foundation text component.
 * This is quite similar to the OOTB {@link com.day.cq.wcm.designimporter.parser.taghandlers.TextComponentTagHandler}
 * with the difference that this tag handler discards all the html tags and stores only plain text.
 * 
 * <p>
 *     For the html section below:
 *     <pre>
 *     &lt;div data-cq-component="plaintext"&gt;
 *         This &lt;b&gt;content&lt;/b&gt; becomes plain text. &lt;i&gt;All&lt;/i&gt; formatting is discarded.
 *         &lt;p&gt;In other words, &lt;span&gt;html tags&lt;/span&gt; are simply stripped off&lt;/p&gt;
 *     &lt;/div&gt;
 *     </pre>
 *     The tag handler should yield the following:
 *     <ol>
 *         <li>
 *             Markup
 *             <pre>
 * &lt;div data-cq-component="plaintext"&gt;
 * &lt;sling:include path="text"/&gt;
 * &lt;/div&gt;
 *             </pre>
 *         </li>
 *         <li>The CQ text component with the text property set as:<br></br> This content becomes plain text. All formatting is discarded.
 *         In other words, html tags are simply stripped off</li>
 *     </ol>
 * </p>
 */
public class PlainTextComponentTagHandler extends AbstractTagHandler {

    private String resourceType;

    private String suffix;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws DesignImportException {
        // By overriding the super.startElement() and super.endElement methods, we've made sure that the html tags shall be ignored.
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws DesignImportException {
        // By overriding the super.startElement() and super.endElement methods, we've made sure that the html tags shall be ignored.
    }

    @Override
    public void endHandling(String uri, String localName, String qName) throws DesignImportException {
        super.endHandling(uri, localName, qName);

        Map<String, Object> base = new HashMap<String, Object>();
        base.put("text", htmlBuffer.toString());
        ValueMap properties = new ValueMapDecorator(base);

        // Obtain a unique suffix from the suffix generator utility
        suffix = designImporterContext.componentSuffixGenerator.getSuffix("text");

        // Use the PageComponent API to create the in-memory cq component.
        PageComponent textComponent = pageBuilder.createComponent(resourceType, properties, getNameHint());

        // Add the created component to the component buffer. The components are all collated, while keeping their hierarchy intact,
        // and are finally handed over to the Design Importer framework which persists them to the jcr repository.
        getPageComponents().add(textComponent);
    }

    @Override
    public boolean supportsContent(HTMLContentType htmlContentType) {
        // This tag handler only supports the html content of type markup.
        // Note: We do not explicitly need to tell that this tag handler supplies components.
        // The super class AbstractTagHandler automatically takes care of that.
        if (htmlContentType == HTMLContentType.MARKUP)
            return true;
        else
            return false;
    }

    @Override
    public Object getContent(HTMLContentType htmlContentType) {
        // This callback method is called by the design importer framework to obtain the html content
        // from this tag handler. Below, we construct the markup which resembles:
        // <div data-cq-component="plaintext"><sling:include path="text"/></div>
        if (htmlContentType == HTMLContentType.MARKUP) {
            String cqIncludeJspTag = "<sling:include path=" + "\"" + getNameHint() + "\"" + "/>";
            return componentDivStartTag + cqIncludeJspTag + TagUtils.getMatchingEndTag(componentDivStartTag);
        }
        return null;
    }

    /**
     * This method lets the tag handler factory to inject the component resource type.
     * The factory in turn exposes the resource type as a configurable property
     *
     * @param resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private String getNameHint() {
        return "text" + suffix;
    }

}

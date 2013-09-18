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

import com.day.cq.wcm.designimporter.api.ImporterConstants;
import com.day.cq.wcm.designimporter.api.TagHandler;
import com.day.cq.wcm.designimporter.api.TagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.TextComponentTagHandler;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

/**
 * The TagHandlerFactory that rolls out {@link PlainTextComponentTagHandler} instances
 */
@Service
@Component(metatype = true)
@Properties({
        /* Though not applicable here, you could use the service ranking to override default tag handlers with yours*/
        @Property(name = Constants.SERVICE_RANKING, intValue = 5000, propertyPrivate = false),
        /* The tag pattern declares the kind of html tag this taghandler shall handle */
        @Property(name = TagHandlerFactory.PN_TAGPATTERN, value = PlainTextComponentTagHandlerFactory.TAG_PATTERN),
        /* The below property lets user modify the resoureType of the text component we're going to generate. This lets users plugin their own (extended) text components */
        @Property(name = "component.resourceType", value = PlainTextComponentTagHandlerFactory.RESOURCE_TYPE_TEXT)
})
public class PlainTextComponentTagHandlerFactory implements TagHandlerFactory{

    static protected final String RESOURCE_TYPE_TEXT = "foundation/components/text";

    static public final String TAG_PATTERN = "(?i)<div\\s+.*data-cq-component=\"plaintext\".*?>";

    private String resourceType;


    @Override
    public TagHandler create() {
        PlainTextComponentTagHandler textComponentTagHandler = new PlainTextComponentTagHandler();
        // Set the resourceType into the created TagHandler
        textComponentTagHandler.setResourceType(resourceType);
        return textComponentTagHandler;
    }

    /**
     * The activator method.
     * @param context
     */
    @Activate
    protected void activate(ComponentContext context) {
        resourceType = OsgiUtil.toString(context.getProperties().get("component.resourceType"), RESOURCE_TYPE_TEXT);
    }
}

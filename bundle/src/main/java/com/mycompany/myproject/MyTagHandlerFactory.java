package com.mycompany.myproject;

import com.day.cq.wcm.designimporter.api.TagHandler;
import com.day.cq.wcm.designimporter.api.TagHandlerFactory;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

@Service
@Component(metatype = true)
@Properties({
        /*
        You could use the service ranking to override default tag handlers with yours.
        This is particularly useful when you are handling tags that are already being handled
        by one of the existing Design Importer Tag Handlers.
        */
        @Property(name = Constants.SERVICE_RANKING, intValue = 5000, propertyPrivate = false),
        /*
        The tag pattern declares the kind of html tag this taghandler shall handle.
        The pattern is a regular expression that matches the html tag in picture.
        As an example, the regular expression .* matches all the html tags.
        */
        @Property(name = TagHandlerFactory.PN_TAGPATTERN, value = MyTagHandlerFactory.TAG_PATTERN)
})
public class MyTagHandlerFactory implements TagHandlerFactory {

    /**
     * The below pattern matches divs with data-cq-component attribute. If you'd like to use this pattern, make
     * sure to replace the string PUT_YOUR_VALUE_HERE with the one of your choice.
     */
    public static final String TAG_PATTERN = "(?i)<div\\s+.*data-cq-component=\"PUT_YOUR_VALUE_HERE\".*?>";

    @Override
    public TagHandler create() {
        return new MyTagHandler();
    }
}

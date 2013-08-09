package com.mycompany.myproject;

import com.day.cq.wcm.designimporter.DesignImportException;
import com.day.cq.wcm.designimporter.parser.taghandlers.AbstractTagHandler;
import org.xml.sax.Attributes;

public class MyTagHandler extends AbstractTagHandler {

    @Override
    // The initialization method, called once by the design importer framework with the tag name and attributes
    // corresponding to the tag the tag handle was registered for.
    // Use this method for any initialization activities but make sure to include the super call
    public void beginHandling(String uri, String localName, String qName, Attributes atts) throws DesignImportException {
        super.beginHandling(uri, localName, qName, atts);
    }

    @Override
    // This method is called every time an html element is encountered within the html tag this tag handler is handling.
    // For any nested tags, you need to bring to life, the relevant tag handlers but the logic can be easily inherited
    // from AbstractTagHandler. Make sure to include the super call.
    //
    // This method can be used to check if the inner html content is as per expectations. If you receive an unexpected tag,
    // raise the UnsupportedTagContentException
    public void startElement(String uri, String localName, String qName, Attributes atts) throws DesignImportException {
        super.startElement(uri, localName, qName, atts);
    }
    @Override
    // Called for all text within start and end tags. Use this method to fill content buffers of your tag handler
    // The super call ensures that the htmlBuffer is auto filled
    public void characters(char[] ch, int start, int length) throws DesignImportException {
        super.characters(ch, start, length);
    }

    @Override
    // This method is called every time the html element ends.
    public void endElement(String uri, String localName, String qName) throws DesignImportException {
        super.endElement(uri, localName, qName);
    }

    @Override
    // The finalization method, called by the design importer just before the tag handler is discarded for garbage collection
    // Use this method to collect and aggregate content from any child tag handlers.
    //
    // This method is specifically used to instantiate a cq component and add it to the list of pageComponents.
    public void endHandling(String uri, String localName, String qName) throws DesignImportException {
        super.endHandling(uri, localName, qName);
    }
}

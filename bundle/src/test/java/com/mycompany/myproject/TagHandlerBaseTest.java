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

import com.day.cq.dam.indd.PageBuilder;
import com.day.cq.dam.indd.PageComponent;
import com.day.cq.dam.indd.impl.pagebuilder.PageBuilderImpl;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.designimporter.DesignImportException;
import com.day.cq.wcm.designimporter.DesignImporterContext;
import com.day.cq.wcm.designimporter.api.TagHandlerFactory;
import com.day.cq.wcm.designimporter.impl.TagHandlerProviderImpl;
import com.day.cq.wcm.designimporter.parser.DesignImporterHTMLParser;
import com.day.cq.wcm.designimporter.parser.HTMLContent;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.CanvasComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.DefaultComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.DefaultTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.HeadTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.IFrameTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.ImageComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.ImgTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.InlineScriptTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.LinkTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.MetaTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.NonScriptTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.ParsysComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.ScriptTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.StyleTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.TextComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.TitleComponentTagHandlerFactory;
import com.day.cq.wcm.designimporter.parser.taghandlers.factory.TitleTagHandlerFactory;
import com.mycompany.myproject.example.PlainTextComponentTagHandlerFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import javax.jcr.Node;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class TagHandlerBaseTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    private Map<ServiceReference, TagHandlerFactory> tagHandlerFactoryMap;

    private Mockery context;

    protected List<PageComponent> components;

    protected DesignImporterHTMLParser designImporterHTMLParser;

    protected HTMLContent headHtmlContent;

    protected HTMLContent bodyHtmlContent;

    protected DesignImportException designImportException;

    protected static String pageDesignPath = "/etc/designs/testcanvaspage";

    @After
    public void tearDown() throws Exception {
        context = null;
        designImporterHTMLParser = null;
        tagHandlerFactoryMap = null;
    }


    @Before
    public void setUp() throws Exception {
        context = new Mockery();
        designImporterHTMLParser = new DesignImporterHTMLParser();
        tagHandlerFactoryMap = new HashMap<ServiceReference, TagHandlerFactory>();

        setupServiceReferenceFactories();

        final Node designNode = context.mock(Node.class);
        context.checking(new Expectations() {
            {
                allowing(designNode).addNode(with(any(String.class)));
                will(returnValue(null));
                allowing(designNode).hasNode(with(any(String.class)));
                will(returnValue(true));
                allowing(designNode).getPath();
                will(returnValue(pageDesignPath));
            }
        });

        DesignImporterContext designImporterContext = new DesignImporterContext(null, designNode);
        designImporterHTMLParser.setDesignImporterContext(designImporterContext);
        designImporterHTMLParser.setCanvasResourceType("geometrixx/components/canvas1234");
        TagHandlerProviderImpl tagHandlerProvider = new TagHandlerProviderImpl();
        designImporterHTMLParser.setTagHandlerProvider(tagHandlerProvider);

        final ComponentContext componentContext = context.mock(ComponentContext.class);
        final BundleContext bundleContext = context.mock(BundleContext.class);

        context.checking(new Expectations() {
            {
                allowing(componentContext).getBundleContext();
                will(returnValue(bundleContext));
            }
        });

        context.checking(new Expectations() {
            {
                allowing(bundleContext).getServiceReferences(with(TagHandlerFactory.class.getName()), with(any(String.class)));
                will(returnValue(tagHandlerFactoryMap.keySet().toArray(new ServiceReference[0])));
            }
        });

        context.checking(new Expectations() {
            {
                for(Map.Entry<ServiceReference, TagHandlerFactory> entry : tagHandlerFactoryMap.entrySet()) {
                    ServiceReference serviceReference = entry.getKey();
                    TagHandlerFactory tagHandlerFactory = entry.getValue();

                    allowing(bundleContext).getService(with(serviceReference));
                    will(returnValue(tagHandlerFactory));
                }
            }
        });
        tagHandlerProvider.activate(componentContext);
    }

    protected void parseHtml(InputStream htmlStream) {

        PageBuilder pageBuilder = new PageBuilderImpl(null, null); // We'll just be using createComponent() api

        try {
            designImporterHTMLParser.parse(null, htmlStream, pageBuilder);
            components = designImporterHTMLParser.getComponents();
            headHtmlContent = designImporterHTMLParser.getHeadHtmlContent();
            bodyHtmlContent = designImporterHTMLParser.getBodyHtmlContent();
        } catch (DesignImportException e) {
            designImportException = e;
        }
    }

    protected List<PageComponent> getCanvasChildComponents() {
        if (components != null && components.size() > 0) {
            return components.get(0).getChildComponents();
        }
        return null;
    }

    private void setupServiceReferenceFactories(){
        registerTagHandlerFactory(new TitleTagHandlerFactory(), 5000, TitleTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new TitleComponentTagHandlerFactory(), 5000, TitleComponentTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new TextComponentTagHandlerFactory(), 5000, TextComponentTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new StyleTagHandlerFactory(), 5000, StyleTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new ScriptTagHandlerFactory(), 5000, ScriptTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new ParsysComponentTagHandlerFactory(), 5000, ParsysComponentTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new NonScriptTagHandlerFactory(), 5001, NonScriptTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new MetaTagHandlerFactory(), 5000, MetaTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new LinkTagHandlerFactory(), 5000, LinkTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new InlineScriptTagHandlerFactory(), 5000, InlineScriptTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new ImgTagHandlerFactory(), 5000, ImgTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new ImageComponentTagHandlerFactory(), 5000, ImageComponentTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new IFrameTagHandlerFactory(), 5000, IFrameTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new HeadTagHandlerFactory(), 5000, HeadTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new DefaultTagHandlerFactory(), 4000, DefaultTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new DefaultComponentTagHandlerFactory(), 4500, DefaultComponentTagHandlerFactory.TAG_PATTERN);
        registerTagHandlerFactory(new CanvasComponentTagHandlerFactory(), 5000, CanvasComponentTagHandlerFactory.TAG_PATTERN);

        // Register your factory here
        registerTagHandlerFactory(new PlainTextComponentTagHandlerFactory(), 5000, PlainTextComponentTagHandlerFactory.TAG_PATTERN);
    }

    protected void registerTagHandlerFactory(TagHandlerFactory tagHandlerFactory, final int ranking, final String tagPattern){
        final ServiceReference serviceReference = context.mock(ServiceReference.class, tagHandlerFactory.getClass().getName());
        context.checking(new Expectations() {
            {
                allowing(serviceReference).getProperty(with(Constants.SERVICE_RANKING));
                will(returnValue(ranking));
                allowing(serviceReference).getProperty(with(TagHandlerFactory.PN_TAGPATTERN));
                will(returnValue(tagPattern));
            }
        });
        try {
            final ComponentContext componentContext = context.mock(ComponentContext.class, "ComponentContext-"+tagHandlerFactory.getClass().getName());
            context.checking(new Expectations(){
                {
                    allowing(componentContext).getProperties();
                    will(returnValue(new Hashtable<String, String>()));
                }
            });
            for(Method m : tagHandlerFactory.getClass().getDeclaredMethods()) {
                 if("activate".equals(m.getName())) {
                     m.setAccessible(true);
                     m.invoke(tagHandlerFactory, componentContext);
                 }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        tagHandlerFactoryMap.put(serviceReference, tagHandlerFactory);
    }

}

# Tag Handlers

## Introduction

A primary solution provided by the Design Importer is that of transforming the input HTML into a top-level generated canvas component and a set of CQ components contained therein. 

TagHandler is responsible for handling an HTML element, and all the HTML elements nested therein. The TagHandler receives SAX events corresponding to the HTML tags as and when they are encountered while parsing the HTML document. 

![image](wiki/images/taghandlerintro.png?raw=true)

TagHandlers are POJOs instantiated everytime a tag is handled. Each TagHandler has an associated TagHandlerFactory which is responsible for rolling out instances.

TagHandlerFactories reside as OSGi services and are responsible for:

- Configuration of TagHandler
- Rolling out the corresponding TagHandler instance
- Injecting other OSGi services as required by the TagHandler

## Lifecycle

The TagHandler interface callback methods are called in a particular sequence by the Design Importer framework.

## Resolution

How does the Design Importer framework decide which TagHandler should go about handling which html tag? 

## Content Aggregation

Each TagHandler is responsible for controlling the lifecycle of its nested TagHandlers. Once a TagHandler starts handling an html element, it must also handle all the nested html elements. The nested elements could well map to other TagHandlers. It's the responsibility of the TagHandler to instantiate, destroy and control the nested TagHandlers. The Design Importer framework doesn't interfere here.

This may sound intimidating but this recurring logic is encapsulated by the AbstractTagHandler. The easiest way to reuse this functionality is thus, by extending from the AbstractTagHandler.

It's important to understand the type of content the TagHandlers emit. The below table descibes various content types.
<table>

<tr>
<th>Content Type</th>
<th>Description</th>
</tr>

<tr>
<th>HtmlContentType.META</th>
<th>Meta content typically defined within the HTML meta tags</th>
</tr>

<tr>
<th>HtmlContentType.MARKUP</th>
<th>The HTML markup. This is what majority of the TagHandlers emit</th>
</tr>

<tr>
<th>HtmlContentType.SCRIPT_INCLUDE</th>
<th>External javascript included via the HTML script tag</th>
</tr>

<tr>
<th>HtmlContentType.SCRIPT_INLINE</th>
<th>The javascript defined inline, within the HTML script tag</th>
</tr>

<tr>
<th>HtmlContentType.STYLESHEET_INCLUDE</th>
<th>External css included via the HTML link tag</th>
</tr>

<tr>
<th>HtmlContentType.STYLES_INLINE</th>
<th>inline styles within the HTML style tag</th>
</tr>

</table>

** Note: ** Your TagHandler must implement the HTMLContentProvider interface if it emits any html content. Typically, most TagHandlers emit some content.

ComponentTagHandlers, in addition to the html content, also emit in-core cq components that are later persisted to the jcr repository. The TagHandlers that implement the PageComponentProvider are automatically called back for the components they've generated at the end of their handling.

## Writing your own TagHandler

Writing TagHandler should be fairly easy once you understand the architecture as described above. Enlisted below are the cookbook steps you'll need to follow, in order to write your own TagHandlers:

- Define your TagHandler implementation class.
- Define your TagHandler factory
- Create and deploy bundle

Caveats:
- In case of conflicts, servce.ranking comes into picture
- Ensure that the regex is as intended

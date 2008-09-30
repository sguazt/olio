<html xmlns="http://www.w3.org/1999/xhtml">
<HEAD>
<TITLE>Insert table button</TITLE>
<link href="style.css" rel="stylesheet" type="text/css">
</HEAD>

<BODY>

<table width="100%" border="0" cellpadding="1" cellspacing="3" class="pageheader">
  <tr> 
    <td><span class="title">Create accessible content</span></td>
    <td align="right"><a href="index.htm"><acronym title="Table of contents">TOC</acronym></a></td>
  </tr>
</table>
<hr noshade>
<p>TinyMCE can create HTML content that will be accessible to all users, including those with disabilities using assistive technologies, as well as those using text-based browsers, or those browsing the Web with images turned off. </p>

<p><strong>Things you can do to make your content accessible:</strong></p>
<ol>
<li><strong>Include an Image Description:</strong> Blind users, or others who are unable to view images, will rely on the Image Description (or Alt text) to take the place of the image. If an image contains no meaning, such as a decoration or a spacer image, leave the Image Description empty. TinyMCE will then insert an empty Alt text attribute that will force assistive technologies to ignore the image. <br /><br /></li>

<li> <strong>Add Scope to data table header cells:</strong> In the table cell editor dialog window, choose a Scope when creating Header cells so the column or row label in that cell becomes explicitely associated with its data cells. Table cell headers will then be announced with each data cell, making it easier for blind users using a screen reader to understand what the content of each cell represents. <br /><br /></li>

<li><strong> Structure content with properly nested headings:</strong> In the format selection menu choose Heading 1 to Heading 6 to represent headings in your content,  rather than using other font formating options. Blind users using a screen reader can then extract the headings from the page to generate a summary of the content it contains, and use those headings to navigate quickly to subsections within the page.<br /><br /></li>

<li><strong> Include alternate content:</strong> Create an alternate page for non-HTML content such as Flash, Java applets, or  embedded movies. This might be a static image, with a description of the image, and a description of the content that would have appeared in its place. An alternate HTML page could also be created, and a link to it included next to the non-HTML object. This will ensure that the content will be accessible to users of assistive technologies that can not view or play the content, and ensure the content will be available to those who do not have the appropriate plugin or helper application installed.<br /><br /></li>

<li><strong> Check accessbility: </strong> When the AChecker plugin is installed with TinyMCE, click on the Check Accessibility button to generate a report of potential accessibility problems.<br /><br /></li>

</ol>

<p>See the <a href="http://checker.atrc.utoronto.ca" target="_new">AChecker Web Site</a> for further details about creating content that will be accessible to all users.<br />
</p>

<hr noshade>
<table width="100%" border="0" cellpadding="1" cellspacing="3" class="pagefooter">
  <tr> 
    <td>Go to: <a href="index.htm">Table of contents</a></td>
    <td align="right"><a href="#">Top</a></td>
  </tr>
</table>

<br>
</BODY>
</HTML>

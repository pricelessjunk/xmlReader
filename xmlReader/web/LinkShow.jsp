<%-- 
    Document   : LinkShow
    Created on : Aug 25, 2014, 8:17:03 PM
    Author     : kaustuv
--%>

<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="java.io.File"%>
<%@ page import="javax.xml.parsers.DocumentBuilder"%>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.w3c.dom.NodeList"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="org.w3c.dom.Attr"%>
<%@ page import="org.w3c.dom.NamedNodeMap"%>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Link Details</title>
        <script type="text/javascript">
            var counter = 1;
            var limit = 25;
            function addInput(divName) {
                if (counter == limit) {
                    alert("You have reached the limit of adding " + counter + " inputs");
                }
                else {
                    var newdiv = document.createElement('div');
                    newdiv.innerHTML = "Links: <input type='text' name='myInputs" + counter + "'>";
                    document.getElementById(divName).appendChild(newdiv);
                    document.addForm.size.value = counter + 1;
                    counter++;
                }
            }
        </script>
        <%! String filePath = "C:\\Users\\user\\Documents\\MovieDetails.xml";%>
    </head>
    <body bgcolor="grey">

        <center><h1><u><font color="white">Movie Details</font></u></h1></center>


        <table border="1" align="center" width="50%" bgcolor="white" cellspacing="0" cellpadding="0">
            <tr>
                <td colspan="2" align="center">
                    <form name="addForm" action="XmlServlet" method="post" id="formId">
                        <input type="hidden" name="size" value="1"></input>
                        <input type="hidden" name="filePath" value="<%=filePath%>"></input>
                        <input type="hidden" name="actionType" value="add"></input>

                        Movie Name: <input type="text" name="movieName" value=""></input><br/><br/>
                        <div id="dynamicInput">
                            Links: <input type="text" name="myInputs0" /><br/>
                        </div>
                        <br />
                        <input type="submit" name="submit" value="Add to xml"></input>&nbsp;&nbsp;
                        <input type="button" name="addRow" value="Add Row" onclick="addInput('dynamicInput')"></input>
                    </form>
                </td>
            </tr>
        </table>

        <br /><br />
        <table align="center" border="1" width="90%">
            <%
                File file = new File(filePath);

                if (!file.exists()) {
                    return;
                }

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();

                try {
                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                    NodeList nameList = doc.getElementsByTagName("name");
                    System.out.println("nameList size: " + nameList.getLength());

                    if (nameList != null && nameList.getLength() > 0) {
                        for (int i = 0; i < nameList.getLength(); i++) {
                            String movieName = null;
                            Element ele = (Element) nameList.item(i);
            %>
            <tr>
                <%
                    if (ele instanceof Element && ele.hasAttributes()) {
                        NamedNodeMap attrs = ele.getAttributes();

                        for (int k = 0; k < attrs.getLength(); k++) {
                            Attr attribute = (Attr) attrs.item(k);
                            System.out.println("Printing attribute name...");
                            movieName = attribute.getValue();
                %>
                <td width="30%" bgcolor="#FFFFFF"><b><%= movieName%></b></td>
                        <%
                                }
                            }

                            NodeList linkList = ele.getChildNodes();
                            if (linkList != null && linkList.getLength() > 0) {
                        %><td width="60%" align="left" bgcolor="#FFFFFF"><%
                            for (int k = 0; k < linkList.getLength(); k++) {
                                Node linkNode = linkList.item(k);
                                if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element linkEle = (Element) linkNode;

                                    NodeList linkValues = linkEle.getChildNodes();
                                    if (linkValues != null && linkValues.getLength() > 0) {

                                        for (int l = 0; l < linkValues.getLength(); l++) {
                                            //System.out.println("Printing links...");
                                            Node nodeValueNode = (Node) linkValues.item(l);
                    %><a href="<%= nodeValueNode.getNodeValue()%>"><%= nodeValueNode.getNodeValue()%></a><br /><%
                                }

                            }
                        }
                    }
                    %>
                </td>
                <td width="10%" align="left" bgcolor="#FFFFFF">
                    <form name="form<%=i%>" method="post" action="XmlServlet">
                        <input type="hidden" name="filePath" value="<%=filePath%>"></input>
                        <input type="hidden" name="movieName" value="<%=movieName%>"></input>
                        <input type="hidden" name="actionType" value="remove"></input>
                        <input type="submit" name="removeRow" value="Remove"></input>
                    </form>
                </td>
                <%
                    }
                %>
            </tr>
            <%
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            %>


        </table>
    </body>
</html>
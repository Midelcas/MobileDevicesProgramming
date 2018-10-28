package com.example.midel.stepper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.android.gms.maps.model.LatLng;


public class XMLManagerWalk {

    public void write_XML(String path, String filename, ArrayList<SimpleWalk> simpleWalkList){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            //add elements to Document
            Element rootElement = doc.createElement("document");
            //append root element to document
            doc.appendChild(rootElement);

            //append first child element to root element
            for(int i = 0; i< simpleWalkList.size(); i++) {
                rootElement.appendChild(getSimpleWalk(doc,simpleWalkList.get(i)));
            }
            saveXMLToFile(doc, path, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node getSimpleWalk(Document doc, SimpleWalk simpleWalk) {
        Element simpleWalkNode = doc.createElement("SimpleWalk");

        //set id attribute
        simpleWalkNode.setAttribute("name", simpleWalk.getName());

        //create name element
        simpleWalkNode.appendChild(getRoute(doc, simpleWalk.getRouteList()));

        return simpleWalkNode;
    }


    //utility method to create text node
    private Node getRoute(Document doc, ArrayList<SlotWalk> slotWalkList) {
        Element routeNode = doc.createElement("Route");
        for(int i=0; i< slotWalkList.size(); i++){
            Element slotWalkNode = doc.createElement("SlotWalk");
            Element altitude = doc.createElement("altitude");
            altitude.appendChild(doc.createTextNode(slotWalkList.get(i).getAltitude()+""));
            slotWalkNode.appendChild(altitude);

            Element distance = doc.createElement("distance");
            distance.appendChild(doc.createTextNode(slotWalkList.get(i).getDistance()+""));
            slotWalkNode.appendChild(distance);

            Element location = doc.createElement("location");
            location.appendChild(doc.createTextNode(slotWalkList.get(i).getLocation().longitude+","+slotWalkList.get(i).getLocation().latitude));
            slotWalkNode.appendChild(location);

            Element steps = doc.createElement("steps");
            steps.appendChild(doc.createTextNode(slotWalkList.get(i).getSteps()+""));
            slotWalkNode.appendChild(steps);

            Element time = doc.createElement("time");
            time.appendChild(doc.createTextNode(slotWalkList.get(i).getTime()+""));
            slotWalkNode.appendChild(time);

            routeNode.appendChild(slotWalkNode);
        }
        return routeNode;
    }

    void saveXMLToFile (Document doc, String path, String fileName) throws Exception{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //for pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        StreamResult console = new StreamResult(System.out);
        StreamResult file = new StreamResult(new File(path+"/"+fileName));

        transformer.transform(source, console);
        transformer.transform(source, file);
    }
    //////////////////////////////////////////////

    private void parse_XML(InputStream is, SimpleWalk sActivity) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setExpandEntityReferences(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);

        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } catch (IOException e) {
        }

        NodeList folderList = document.getElementsByTagName("Folder");

        for (int i = 0; i < folderList.getLength(); i++) {
            Element folder = (Element) folderList.item(i);

            NodeList placemarkList = folder.getElementsByTagName("Placemark");
            Element placemark =  (Element) placemarkList.item(0);


            NodeList LineStringList = placemark.getElementsByTagName("LineString");
            Element LineString = (Element) LineStringList.item(0);

            NodeList coordinatesList = LineString.getElementsByTagName("coordinates");
            Element coordinates = (Element) coordinatesList.item(0);
            String route = coordinates.getTextContent();
            String[] points = route.split("\n");

            for(int j =0; j < points.length; j++) {
                routeList.add(new LatLng(Double.parseDouble(points[j].split(",")[1]), Double.parseDouble(points[j].split(",")[0])));
            }


        }
        sActivity.setRouteList(routeList);
    }

    public void parse_XML_route(InputStream is, SimpleWalk sActivity) throws Exception{
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setExpandEntityReferences(false);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(is);
        NodeList activityList = document.getElementsByTagName("activity");
        Element activity = (Element) activityList.item(0);

        NodeList coordinatesList = activity.getElementsByTagName("coordinate");
        Element coordinate =  (Element) coordinatesList.item(0);
        routeList.clear();;
        for(int i=0; i< coordinatesList.getLength(); i++){
            String[] coord = coordinatesList.item(i).getTextContent().split("\n")[1].split(",");
            routeList.add(new LatLng(Double.parseDouble(coord[0]),Double.parseDouble(coord[1])));
        }

        sActivity.setRouteList(routeList);
    }
}

package com.example.midel.stepper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.android.gms.maps.model.LatLng;


public class XMLManager {

    public static class XMLWalk{
        public static Document write_XML(ArrayList<SimpleWalk> simpleWalkList){
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            Document doc = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.newDocument();
                //add elements to Document
                Element rootElement = doc.createElement("document");
                //append root element to document
                doc.appendChild(rootElement);

                //append first child element to root element
                for(int i = 0; i< simpleWalkList.size(); i++) {
                    rootElement.appendChild(getSimpleWalk(doc,simpleWalkList.get(i)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                return doc;
            }
        }

        private static Node getSimpleWalk(Document doc, SimpleWalk simpleWalk) {
            Element simpleWalkNode = doc.createElement("SimpleWalk");

            //set id attribute
            simpleWalkNode.setAttribute("name", simpleWalk.getName());

            Element dateNode = doc.createElement("Date");
            dateNode.appendChild(doc.createTextNode(simpleWalk.getDate().toString()));
            //create name element
            simpleWalkNode.appendChild(dateNode);
            simpleWalkNode.appendChild(getRoute(doc, simpleWalk.getRouteList()));

            return simpleWalkNode;
        }


        //utility method to create text node
        private static Node getRoute(Document doc, ArrayList<SlotWalk> slotWalkList) {
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
                location.appendChild(doc.createTextNode(slotWalkList.get(i).getLongitude()+","+slotWalkList.get(i).getLatitude()));
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

        public static void saveXMLToFile (Document doc, File path, String fileName) throws Exception{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(path,fileName));

            transformer.transform(source, console);
            transformer.transform(source, file);
        }
        //////////////////////////////////////////////

        public static FileInputStream read_File(File path, String filename) throws IOException{
            File f = new File(path, filename);
            FileInputStream is = null;
            if (f.exists()) { // Delete the file if it exists
                is = new FileInputStream(f);
            }
            return is;
        }

        public static void parse_XML(InputStream is, ArrayList<SimpleWalk> simpleWalkList) throws ParseException {
            Document document = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                factory.setExpandEntityReferences(false);
                factory.setIgnoringComments(true);
                factory.setIgnoringElementContentWhitespace(true);

                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(is);



                NodeList documentList = document.getElementsByTagName("document");
                Element documentNode = (Element) documentList.item(0);

                NodeList simpleWalkNodeList = documentNode.getElementsByTagName("SimpleWalk");
                for (int i = 0; i < simpleWalkNodeList.getLength(); i++) {
                    Element simpleWalkNode = (Element) simpleWalkNodeList.item(i);
                    NodeList dateNodeList =  simpleWalkNode.getElementsByTagName("Date");
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
                    Element dateNode = (Element) dateNodeList.item(0);
                    Date date = formatter.parse(dateNode.getTextContent());

                    SimpleWalk simpleWalk = new SimpleWalk(simpleWalkNode.getAttribute("name"),date);

                    NodeList routeList = simpleWalkNode.getElementsByTagName("Route");
                    Element routeNode = (Element) routeList.item(0);

                    NodeList slotWalkList = routeNode.getElementsByTagName("SlotWalk");

                    for (int j = 0; j < slotWalkList.getLength(); j++) {
                        Element slotNode = (Element) slotWalkList.item(j);
                        NodeList altitudeList = slotNode.getElementsByTagName("altitude");
                        Element altitudeNode = (Element) altitudeList.item(0);
                        double altitude = Double.parseDouble(altitudeNode.getTextContent());
                        NodeList distanceList = slotNode.getElementsByTagName("distance");
                        Element distanceNode = (Element) distanceList.item(0);
                        float distance = Float.parseFloat(distanceNode.getTextContent());
                        NodeList locationList = slotNode.getElementsByTagName("location");
                        Element locationNode = (Element)locationList.item(0);
                        String[] locationString = locationNode.getTextContent().split("\n")[0].split(",");
                        LatLng location = new LatLng(Double.parseDouble(locationString[0]), Double.parseDouble(locationString[1]));

                        NodeList stepsList =  slotNode.getElementsByTagName("steps");
                        Element stepsNode = (Element) stepsList.item(0);
                        long steps = Long.parseLong(stepsNode.getTextContent());
                        NodeList timeList = slotNode.getElementsByTagName("time");
                        Element timeNode = (Element)timeList.item(0);
                        float time = Float.parseFloat(timeNode.getTextContent());

                        SlotWalk slotWalk = new SlotWalk(altitude, distance, location.longitude, location.latitude, steps, time);
                        if (j == 0) {
                            simpleWalk.startWalk(slotWalk);
                        } else if(j== (slotWalkList.getLength()-1)){
                            simpleWalk.endWalk(slotWalk);
                        }else{
                            simpleWalk.addSlot(slotWalk);
                        }

                    }
                    simpleWalkList.add(simpleWalk);
                }
                is.close();
            } catch (ParserConfigurationException e) {
            } catch (SAXException e) {
            } catch (IOException e) {
            }
        }
    }

}

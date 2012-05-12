package com.omilen.agendaculturalbsas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.omilen.agendaculturalbsas.model.Event;

public class RSSHandler extends DefaultHandler {

	private List<Event> results;
	private Event currentEvent;
	private SAXParserFactory spf = SAXParserFactory.newInstance();

	public RSSHandler(List<Event> results) {
		super();
		this.results = results;
	}

	// Used to define what elements we are currently in
	private boolean inItem = false;
	private boolean inTitle = false;
	private boolean inLink = false;
	private boolean inContent = false;

	public void startElement(String uri, String name, String qName, Attributes atts) {
		if (name.trim().equals("title")) {
			inTitle = true;
		} else if (name.trim().equals("item")) {
			inItem = true;
		} else if (name.trim().equals("link")) {
			inLink = true;
		} else if (name.trim().equals("content")) {
			inContent = true;
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		if (name.trim().equals("title")) {
			inTitle = false;
		} else if (name.trim().equals("item")) {
			inItem = false;
		} else if (name.trim().equals("link")) {
			inLink = false;
		} else if (name.trim().equals("content")) {
			inContent = false;
			results.add(currentEvent);
			currentEvent = new Event();
		}

	}

	public void characters(char ch[], int start, int length) {

		String chars = (new String(ch).substring(start, start + length));

		if (inItem) {
			if (inTitle) {
				currentEvent = new Event();
				currentEvent.setTitle(chars);
			}
			if (inLink) {
				currentEvent.setLink(chars);
			}
			if (inContent) {
				int pos01 = chars.indexOf("<");
				if (pos01 == -1 && ("".equals(currentEvent.getContent()) || currentEvent.getContent() == null)) {
					currentEvent.setContent(chars);
				}

				String img = "";
				int pos02 = chars.indexOf("src");
				if (pos02 != -1) {
					img = chars.substring(pos02 + 5, chars.length() - 3);
					currentEvent.setImg(img);
				}
			}
		}

		// else {
		// if (inLink)
		// currentArticle.url = new URL(chars);
		// if (inTitle)
		// currentArticle.title = chars;
		// }

	}

	public void createFeed(String rss) {
		try {
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			InputSource inputSource = new InputSource(new ByteArrayInputStream(rss.getBytes("ISO-8859-1")));
			inputSource.setEncoding("UTF-8");
			// inputSource.setEncoding("ISO-8859-1");

			xr.parse(inputSource);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

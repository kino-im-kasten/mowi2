package kik.config;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;


/**
 * Configuration Object, holding all configuration variables
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Service
public class Configuration {

	public class ProtoJob {

		private final String name;
		private final String description;

		ProtoJob(String name, String description){
			this.name = name;
			this.description = description;
		}
		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
	}

	private String regexPassword;
	private Double defaultNormalPrice;
	private Double defaultReducedPrice;
	private LocalTime defaultStartTime;
	private List<ProtoJob> defaultJobs;

	/**
	 * default constructor, with defaule values, in case the config-file is not readable
	 */
	public Configuration(){
		// Default if there is a file error

		this.regexPassword = ".*[0-9].*";
		this.defaultNormalPrice = 2.50;
		this.defaultReducedPrice = 1.00;
		this.defaultStartTime = LocalTime.of(20,30);
		this.defaultJobs = new LinkedList<>();

		this.loadFromFile();
	}

	/**
	 * method that loads the data of the config-file into the {@link Configuration} object
	 *
	 * @return success value for loading the data
	 */
	public boolean loadFromFile(){
		try {
			File fXmlFile = new File("configuration.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(fXmlFile);
			document.normalize();

			this.regexPassword = document.getDocumentElement()
				.getElementsByTagName("passwordregex")
				.item(0).getTextContent();

			this.defaultNormalPrice = Double.parseDouble(document.getDocumentElement()
				.getElementsByTagName("defaultnormalprice")
				.item(0).getTextContent());
			this.defaultReducedPrice = Double.parseDouble(document.getDocumentElement()
					.getElementsByTagName("defaultreducedprice")
					.item(0).getTextContent());
			
			this.defaultStartTime = LocalTime.parse(document.getDocumentElement()
				.getElementsByTagName("defaultstarttime")
				.item(0).getTextContent());

			List<ProtoJob> readJobList = new LinkedList<>();
			NodeList jobList = document.getElementsByTagName("defaultjob");

			for (int i = 0; i < jobList.getLength(); i++){
				Node jobNode = jobList.item(i);

				if (jobNode.getNodeType() == Node.ELEMENT_NODE){
					Element jobElement = (Element) jobNode;
					String jobName = jobElement.getElementsByTagName("name").item(0).getTextContent();
					String jobDescription = jobElement.getElementsByTagName("description").item(0).getTextContent();

					readJobList.add(new ProtoJob(jobName, jobDescription));
				}
			}


			this.defaultJobs = readJobList;

		}catch (Exception e){
			System.out.println("bad");
			return false;
		}
		return true;
	}

	//getter for all values
	public String getRegexPassword() {
		return this.regexPassword;
	}
	
	public Double getDefaultNormalPrice() {
		return this.defaultNormalPrice;
	}
	
	public Double getDefaultReducedPrice() {
		return this.defaultReducedPrice;
	}
	
	public LocalTime getDefaultStartTime() {
		return this.defaultStartTime;
	}

	public List<ProtoJob> getDefaultJobs() {
		return defaultJobs;
	}

	//setter for all values
	public void setRegexPassword(String regexPassword) {
		this.regexPassword = regexPassword;
	}

	public void setDefaultNormalPrice(Double defaultNormalPrice) {
		this.defaultNormalPrice = defaultNormalPrice;
	}

	public void setDefaultReducedPrice(Double defaultReducedPrice) {
		this.defaultReducedPrice = defaultReducedPrice;
	}

	public void setDefaultStartTime(LocalTime defaultStartTime) {
		this.defaultStartTime = defaultStartTime;
	}

	public void setDefaultJobs(List<ProtoJob> defaultJobs) {
		this.defaultJobs = defaultJobs;
	}
}
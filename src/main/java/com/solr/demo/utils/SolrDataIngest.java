package com.solr.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class SolrDataIngest {

    public static final Logger LOG = Logger.getLogger(SolrDataIngest.class.getName());

    // Method to ingest JSON data from resources folder into Solr collection
    public void ingestJsonData(String solrUrl, String collection) {

        try (SolrClient solrClient = new HttpSolrClient.Builder(solrUrl + "/" + collection).build()) {
            ObjectMapper mapper = new ObjectMapper();
            // Load the JSON file from the classpath
            InputStream jsonFile = getClass().getClassLoader().getResourceAsStream("data.json");

            // Read the JSON file into a JsonNode
            JsonNode rootNode = mapper.readTree(jsonFile);

            // Check if the JSON is an array
            if (rootNode.isArray()) {
                List<SolrInputDocument> documents = new ArrayList<>();

                // Iterate over each product in the JSON array
                for (JsonNode productNode : rootNode) {
                    SolrInputDocument document = new SolrInputDocument();

                    // Map the fields from JSON to SolrInputDocument
                    document.addField("id", UUID.randomUUID().toString());
                    document.addField("sku", productNode.get("sku").asText());
                    document.addField("brand", productNode.get("brand").asText());
                    document.addField("price", productNode.get("price").asDouble());
                    document.addField("category1", productNode.get("category1").asText());
                    document.addField("category2", productNode.get("category2").asText());
                    document.addField("category3", productNode.get("category3").asText());
                    document.addField("category4", productNode.get("category4").asText());
                    document.addField("category5", productNode.get("category5").asText());
                    document.addField("discontinued", productNode.get("discontinued").asBoolean());
                    document.addField("description", productNode.get("description").asText());
                    document.addField("model_number", productNode.get("model_number").asText());
                    document.addField("unit_of_measurement", productNode.get("unit_of_measurement").asText());
                    document.addField("weight", productNode.get("weight").asText());
                    document.addField("dimensions", productNode.get("dimensions").asText());
                    document.addField("color", productNode.get("color").asText());
                    document.addField("material", productNode.get("material").asText());
                    document.addField("availability", productNode.get("availability").asText());
                    document.addField("min_order_quantity", productNode.get("min_order_quantity").asInt());
                    document.addField("max_order_quantity", productNode.get("max_order_quantity").asInt());
                    document.addField("warranty", productNode.get("warranty").asText());
                    document.addField("country_of_origin", productNode.get("country_of_origin").asText());

                    // Handle arrays like "attributes" and "tags"
                    document.addField("attributes", productNode.get("attributes").toString());
                    document.addField("tags", productNode.get("tags").toString());
                    document.addField("crossReferencePartNumber", productNode.get("crossReferencePartNumber").toString());
                    documents.add(document);
                }

                // Send all documents in a single batch to Solr
                UpdateResponse response = solrClient.add(documents);
                solrClient.commit();
                LOG.info("Data ingested successfully, status code: " + response.getStatus());
            } else {
                throw new Exception("Invalid JSON format. Expected an array.");
            }
        } catch (Exception e) {
            LOG.info("Error during Solr data ingestion: " + e.getMessage());
        }
    }
}
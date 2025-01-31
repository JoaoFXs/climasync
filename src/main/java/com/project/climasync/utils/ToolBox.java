package com.project.climasync.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.camel.Exchange;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.SAXException;

public class ToolBox {
	
	@Value("${file.path}")
	String filePath;
	

	public void convertJsontoXML(Exchange ex) {
		String body = ex.getIn().getBody(String.class);
		
		JSONObject json = new JSONObject(body);
		String xml = XML.toString(json);
		
		ex.getOut().setBody(xml);
	}
	
	public void saveText(Exchange ex) throws IOException {
		String body = ex.getIn().getBody(String.class);
		 File file = new File(filePath);

	        try {
	            // Cria os diretórios necessários, se não existirem
	            if (!file.getParentFile().exists()) {
	                boolean dirsCreated = file.getParentFile().mkdirs();
	                if (!dirsCreated) {
	                    System.err.println("Erro ao criar diretórios no caminho: " + file.getParent());
	                    return;
	                }
	            }

	            // Cria ou sobrescreve o arquivo
	            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	                writer.write(body);
	                System.out.println("Arquivo XML salvo com sucesso em: " + filePath);
	            }
	        } catch (IOException e) {
	            System.err.println("Erro ao salvar o arquivo XML: " + e.getMessage());
	        }
        
		
	}
	

}

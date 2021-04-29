package com.everis.microgenerator.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ToString
@NoArgsConstructor
@Data
public class Template {	
	private Map<String, String> properties = new HashMap<>();
}

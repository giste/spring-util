package org.giste.spring.util.validation;

public enum Relationship {
	Less("less"),
	LessOrEqual("lessOrEqual"), 
	Equal("equal"), 
	Greater("greater"), 
	GreaterOrEqual("greaterOrEqual");
	
	private String text;
	
	Relationship(String text){
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

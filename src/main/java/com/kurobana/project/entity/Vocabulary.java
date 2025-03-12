package com.kurobana.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;

@Entity
public class Vocabulary {
	
	@TableGenerator(name="idGenerator", allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="idGenerator")
	private Long id;
	
	@Column(unique=true)
	private String kanji;
	private String kana;
	private String definition;
	
	protected Vocabulary() {}
	
	public Vocabulary(String kanji, String kana, String definition) {
		this.kanji = kanji;
		this.kana = kana;
		this.definition = definition;
	}
	
	@Override
	public String toString() {
		return String.format(
			"Vocabulary[id=%d, kanji='%s', kana='%s', definition='%s']",	
			id, kanji, kana, definition);
	}
	
	public Long getId() {
		return id;
	}

	public String getKanji() {
		return kanji;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public String getKana() {
		return kana;
	}

	public void setKana(String kana) {
		this.kana = kana;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	
	
	
}

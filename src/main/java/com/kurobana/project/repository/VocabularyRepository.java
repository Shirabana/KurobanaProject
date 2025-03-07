package com.kurobana.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kurobana.project.entity.Vocabulary;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
	
	Vocabulary findById(long id);
	
	List<Vocabulary> findByKanji(String kanji);
	
	List<Vocabulary> findByKana(String kana);
	
	List<Vocabulary> findByDefinition(String definition);
	
	List<Vocabulary> findByKanjiContaining(String kanji);
	
	List<Vocabulary> findByKanaContaining(String kana);
	
	List<Vocabulary> findByDefinitionContaining(String definition);
	
}

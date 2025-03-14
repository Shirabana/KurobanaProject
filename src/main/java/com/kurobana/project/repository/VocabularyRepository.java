package com.kurobana.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kurobana.project.entity.Vocabulary;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
	
	Vocabulary findById(long id);
	
	List<Vocabulary> findByKanji(String kanji);
	
	List<Vocabulary> findByKana(String kana);
	
	List<Vocabulary> findByDefinition(String definition);
	
	List<Vocabulary> findByKanjiContaining(String kanji);
	
	List<Vocabulary> findByKanaContaining(String kana);
	
	List<Vocabulary> findByDefinitionContaining(String definition);
	
	Page<Vocabulary> findAll(Pageable pageable);
	
	void deleteById(long Id);
	
}

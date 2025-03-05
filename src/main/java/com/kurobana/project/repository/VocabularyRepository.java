package com.kurobana.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kurobana.project.entity.Vocabulary;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
	
	List<Vocabulary> findByKanji(String kanji);
	
	List<Vocabulary> findByKana(String kana);
	
	Vocabulary findById(long id);
	
}

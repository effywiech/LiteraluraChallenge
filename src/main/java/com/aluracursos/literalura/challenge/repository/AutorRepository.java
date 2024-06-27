package com.aluracursos.literalura.challenge.repository;

import com.aluracursos.literalura.challenge.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
}

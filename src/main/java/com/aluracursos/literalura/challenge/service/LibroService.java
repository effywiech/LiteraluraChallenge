package com.aluracursos.literalura.challenge.service;

import com.aluracursos.literalura.challenge.model.Autor;
import com.aluracursos.literalura.challenge.model.DatosLibros;
import com.aluracursos.literalura.challenge.model.Libro;
import com.aluracursos.literalura.challenge.repository.AutorRepository;
import com.aluracursos.literalura.challenge.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public void saveLibro(DatosLibros datosLibros) {
        Autor autor = new Autor();
        autor.setNombre(datosLibros.autor().get(0).nombre());
        autor.setFechaDeNacimiento(datosLibros.autor().get(0).fechaDeNacimiento());
        autor.setFechaDeMuerte(datosLibros.autor().get(0).fechaDeMuerte());

        autor = autorRepository.save(autor);

        Libro libro = new Libro();
        libro.setTitulo(datosLibros.titulo());
        libro.setAutor(autor);
        libro.setIdiomas(String.join(",", datosLibros.idiomas()));
        libro.setNumeroDeDescargas(datosLibros.numeroDeDescargas());

        libroRepository.save(libro);
    }
}

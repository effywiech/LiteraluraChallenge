package com.aluracursos.literalura.challenge.principal;

import com.aluracursos.literalura.challenge.model.Datos;
import com.aluracursos.literalura.challenge.model.DatosLibros;
import com.aluracursos.literalura.challenge.model.DatosAutor;
import com.aluracursos.literalura.challenge.service.LibroService;
import com.aluracursos.literalura.challenge.service.ConsumoAPI;
import com.aluracursos.literalura.challenge.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {
    private int numeroOpcion = 0;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final LibroService libroService;
    private final Scanner teclado = new Scanner(System.in);
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public Principal(ConsumoAPI consumoAPI, ConvierteDatos convierteDatos, LibroService libroService) {
        this.consumoAPI = consumoAPI;
        this.convierteDatos = convierteDatos;
        this.libroService = libroService;
    }

    private final String menu = """
            *************************************************
            BIENVENIDO A LITERALURA
            
            Elija la opción que desea usar
            
            1) Buscar libro por título
            2) Listar libros registrados
            3) Listar autores registrados
            4) Listar autores vivos en un determinado periodo
            5) Listar libros por idioma
            6) Salir
            *************************************************
            """;

    public String getMenu() {
        return menu;
    }

    public void run() {
        while (true) {
            System.out.println(getMenu());
            numeroOpcion = scanner.nextInt();
            scanner.nextLine();

            switch (numeroOpcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnPeriodo();
                case 5 -> listarLibrosPorIdioma();
                case 6 -> {
                    System.out.println("Salir");
                    return;
                }
                default -> System.out.println("Opción no válida, intente de nuevo.");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Buscar libro por título");
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String tituloLibro = teclado.nextLine();

        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        Datos datosBusqueda = convierteDatos.obtenerDatos(json, Datos.class);

        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("Libro Encontrado:");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    private void listarLibrosRegistrados() {
        try {
            String json = consumoAPI.obtenerDatos(URL_BASE);
            Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

            datos.resultados().forEach(libro -> {
                System.out.println("Título: " + libro.titulo());
                System.out.println("Autor: " + libro.autor());
                System.out.println("Número de Descargas: " + libro.numeroDeDescargas());
                System.out.println("Idiomas: " + libro.idiomas());
                System.out.println("-------------------");
            });
        } catch (Exception e) {
            System.out.println("Error al listar los libros registrados: " + e.getMessage());
        }
    }

    private void listarAutoresRegistrados() {
        try {
            String json = consumoAPI.obtenerDatos(URL_BASE);
            Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

            Set<String> autores = datos.resultados().stream()
                    .flatMap(libro -> libro.autor().stream())
                    .map(DatosAutor::nombre)
                    .collect(Collectors.toSet());

            autores.forEach(autor -> {
                System.out.println("Autor: " + autor);
                System.out.println("-------------------");
            });
        } catch (Exception e) {
            System.out.println("Error al listar los autores registrados: " + e.getMessage());
        }
    }

    private void listarAutoresVivosEnPeriodo() {
        try {
            System.out.println("Ingrese el año de inicio del periodo:");
            int anoInicio = scanner.nextInt();
            System.out.println("Ingrese el año de fin del periodo:");
            int anoFin = scanner.nextInt();
            scanner.nextLine();

            String json = consumoAPI.obtenerDatos(URL_BASE);
            Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

            Set<String> autoresVivos = datos.resultados().stream()
                    .flatMap(libro -> libro.autor().stream())
                    .filter(autor -> {
                        int nacimiento = autor.fechaDeNacimiento();
                        int muerte = autor.fechaDeMuerte();
                        return (muerte == 0 || nacimiento <= muerte) && (muerte >= anoInicio && nacimiento <= anoFin);
                    })
                    .map(DatosAutor::nombre)
                    .collect(Collectors.toSet());

            autoresVivos.forEach(autor -> {
                System.out.println("Autor: " + autor);
                System.out.println("-------------------");
            });
        } catch (Exception e) {
            System.out.println("Error al listar los autores vivos en el periodo: " + e.getMessage());
        }
    }

    private void listarLibrosPorIdioma() {
        try {
            System.out.println("Seleccione el lenguaje que desea:");
            System.out.println("""
                    1) EN - Inglés
                    2) ES - Español
                    3) FR - Francés
                    4) PT - Portugués
                    
                    0) Volver al menú principal
                    """);

            String opcion = teclado.nextLine().trim();
            if (opcion.equals("0")) return;

            String languageCode;
            switch (opcion) {
                case "1" -> languageCode = "en";
                case "2" -> languageCode = "es";
                case "3" -> languageCode = "fr";
                case "4" -> languageCode = "pt";
                default -> {
                    System.out.println("Opción no válida, intente de nuevo.");
                    return;
                }
            }

            String endpoint = URL_BASE + "?languages=" + languageCode;
            System.out.println("Consultando libros en " + languageCode.toUpperCase() + "...");
            String json = consumoAPI.obtenerDatos(endpoint);
            Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

            datos.resultados().forEach(libro -> {
                System.out.println("Título: " + libro.titulo());
                System.out.println("Autor: " + libro.autor());
                System.out.println("Número de Descargas: " + libro.numeroDeDescargas());
                System.out.println("Idiomas: " + libro.idiomas());
                System.out.println("-------------------");
            });
        } catch (Exception e) {
            System.out.println("Error al listar los libros por idioma: " + e.getMessage());
        }
    }
}

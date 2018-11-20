package project.controller.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class SparqlServices {

  /** DBpedia SPARQL query endpoint */
  private static final String DBPEDIA_URL = "http://dbpedia.org/sparql";

  /** Prefix declarations */
  private static final String QNAMES =
      "PREFIX dbp: <http://dbpedia.org/property/>\n"
    + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
    + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
    + "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n"
    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
  
  /**
   * Adds prefix declarations to a query String and creates a QueryExecution.
   * 
   * @param queryString the query
   * @return a QueryExecution for this query
   */
  private static QueryExecution createPrefixedQuery (String queryString) {
    Query query = QueryFactory.create(QNAMES + queryString);
    return QueryExecutionFactory.sparqlService(DBPEDIA_URL, query);
  }

  // ----------------------------------------------------------- Services for Servlet Initialization
  
  public static Map<String, String> getAllFilmNamesAndUris() {
    Map<String, String> films = new HashMap<String, String>();
    for (int i = 10; i <= 30; i++) {
      String condition = "strlen(str(?name)) = " + i;
      if (i == 10) {
        condition = "strlen(str(?name)) <= 10";
      } else if (i == 30) {
        condition = "strlen(str(?name)) >= 30";
      }
      
      QueryExecution queryFilms = createPrefixedQuery(
          "SELECT DISTINCT ?f ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:director ?d ;\n"
        + "     rdfs:label ?name .\n"
        + "     FILTER (lang(?name) = 'en').\n"
        + "     FILTER (" + condition + "). \n"
        + "}"
      );
        
      try {
        ResultSet result = queryFilms.execSelect();
        while (result.hasNext()) {
          QuerySolution elem = result.next();
          films.put(elem.getResource("?f").getURI().toString(), elem.getLiteral("?name").getString());
        }
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        queryFilms.close();
      }
    }
    return films;
  }

  public static Map<String, String> getAllCompanyNamesAndUris() {
    Map<String, String> companies = new HashMap<String, String>();
    
    QueryExecution queryCompanies = createPrefixedQuery("SELECT ?uri ?name WHERE {\n"
            + "  ?uri rdf:type dbo:Company ;\n"
            + "     rdf:type ?o ;\n"
            + "     rdfs:label ?name .\n"
            + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\").\n"
            + "  FILTER (lang(?name)='en')\n"
            + "}");
    try {
      ResultSet results = queryCompanies.execSelect();
      while (results.hasNext()) {
        QuerySolution elem = results.nextSolution();
        companies.put(elem.getResource("uri").getURI().toString(), elem.getLiteral("name").getString());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      queryCompanies.close();
    }
    return companies;
  }

  public static Map<String, String> getAllPersonNamesAndUris() {
    Map<String, String> persons = new HashMap<String, String>();
    for (int i = 0; i < 26; i++) {
      String condition = "strstarts(str(?name), '" + (char)('A' + i) + "')";
      
      // Actors
      QueryExecution queryActors = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:starring ?a .\n"
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      // Directors
      QueryExecution queryDirectors = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:director ?a .\n"
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      // Music composers
      QueryExecution queryMusicComposers = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:musicComposer ?a .\n"
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      try {
        ResultSet actors = queryActors.execSelect();
        while (actors.hasNext()) {
          QuerySolution elem = actors.next();
          persons.put(elem.getResource("?a").getURI().toString(), elem.getLiteral("?name").getString());
        }
        
        ResultSet directors = queryDirectors.execSelect();
        while (directors.hasNext()) {
          QuerySolution elem = directors.next();
          persons.put(elem.getResource("?a").getURI().toString(), elem.getLiteral("?name").getString());
        }
        
        ResultSet musicComposers = queryMusicComposers.execSelect();
        while (musicComposers.hasNext()) {
          QuerySolution elem = musicComposers.next();
          persons.put(elem.getResource("?a").getURI().toString(), elem.getLiteral("?name").getString());
        }
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        queryActors.close();
        queryDirectors.close();
        queryMusicComposers.close();
      }
    }
    return persons;
  }

  // ----------------------------------------------------- Services to get Resource Type Information
  
  public static boolean isCompany(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?o WHERE {\n"
            + "<" + uri + ">" + " rdf:type dbo:Company ;\n"
            + "     rdf:type ?o .\n"
            + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\")\n"
            + "}");

    ResultSet results = qexec.execSelect();
    boolean company = results.hasNext();
    qexec.close();
    return company;
  }
  
  public static boolean isFilm(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?r WHERE {\n" +
        "  <"+uri+"> rdf:type dbo:Film ;\n" +
        "  dbo:runtime ?r  .\n" +
        "}");
    
    ResultSet results = qexec.execSelect();
    boolean film = results.hasNext();
    qexec.close();
    return film;
  }
  
  public static boolean isActor(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
              + "  ?f rdf:type dbo:Film ;\n"
              + "     dbo:starring <" + uri + "> \n"
              + "}");
    ResultSet results = qexec.execSelect();
    boolean actor = results.hasNext();
    qexec.close();
    return actor;
  }

  public static boolean isFilmDirector(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:director <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();
    boolean director = results.hasNext();
    qexec.close();
    return director;
  }

  public static boolean isFilmProducer(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:producer <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();
    boolean producer = results.hasNext();
    qexec.close();
    return producer;
  }

  public static boolean isFilmMusicComposer(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:musicComposer <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();
    boolean musicComposer = results.hasNext();
    qexec.close();
    return musicComposer;
  }
  
  // ----------------------------------------------------------- Services for all types of Resources
  
  public static String getResourceName(String uri){
    String name = null;
    String query = "SELECT ?name WHERE { <" + uri + "> rdfs:label ?name. FILTER(lang(?name) = \"en\") } ORDER BY ?name LIMIT 1";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000"); // DBpedia timeout
      ResultSet rs = qexec.execSelect();
      if (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        name = qs.getLiteral("name").getString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return name;
  }
  
  // -------------------------------------------------------------- Services to get Film Information
  
  public static String getFilmBudget(String uri) {
    String budget = "Unknown";
    try (QueryExecution qexec = createPrefixedQuery(" SELECT ?b WHERE{ <" + uri +  "> dbo:budget ?b . }")) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      if (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        budget = qs.get("b").toString();
        int ocurrence = budget.indexOf("^");
        budget = budget.substring(0, ocurrence);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return budget;
  }
  
  public static String getFilmRunningTime(String uri) {
    String runtime = "Unknown";
    try (QueryExecution qexec = createPrefixedQuery(" SELECT ?r WHERE{ <" + uri +  "> dbo:runtime ?r . }")) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      if (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        runtime = qs.get("r").toString();
        int ocurrence = runtime.indexOf("^");
        runtime = runtime.substring(0, ocurrence);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return runtime;
  }

  public static String getFilmBoxOffice(String uri) {
    String boxOffice = "Unknown";
    try (QueryExecution qexec = createPrefixedQuery(" SELECT ?g WHERE{ <" + uri +  "> dbo:gross ?g . }")) {
       ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
       ResultSet rs = qexec.execSelect();
        if (rs.hasNext()) {
          QuerySolution qs = rs.nextSolution();
          boxOffice = qs.get("g").toString();
          int ocurrence = boxOffice.indexOf("^");
          boxOffice = boxOffice.substring(0,ocurrence);
       } 
    } catch(Exception e){
       e.printStackTrace();
    }
    return boxOffice;
  }
  
  public static LinkedHashMap<String, String> getFilmActors(String uri) {
    LinkedHashMap<String, String> starring = new LinkedHashMap<>();
    String query = "SELECT ?actor ?name WHERE {\n" +
                    "<" + uri + "> rdf:type dbo:Film ;\n" +
                    "dbo:starring ?actor .\n" +
                    "?actor foaf:name ?name .\n" +
                    "FILTER (lang(?name) = 'en').\n" +
                    "}";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String actorUri = qs.getResource("actor").getURI();          
          String actorName = qs.getLiteral("name").getString();
          if (actorName != null && !actorName.isEmpty()) {
            starring.put(actorUri, actorName);
          }
        } catch(Exception e){
          System.out.println("Not a resource");
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return starring;
  }
  
  public static LinkedHashMap<String, String> getFilmDirector(String uri) {
    LinkedHashMap<String, String> directed = new LinkedHashMap<>();
    String query = "SELECT ?director ?name WHERE {\n" +
                    "<" + uri + "> rdf:type dbo:Film ;\n" +
                    "dbo:director ?director .\n" +
                    "?director foaf:name ?name .\n" +
                    "FILTER (lang(?name) = 'en').\n" +
                    "}";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String directorUri = qs.getResource("director").getURI();          
          String directorName = qs.getLiteral("name").getString();
          if (directorName != null && !directorName.isEmpty()) {
            directed.put(directorUri, directorName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return directed;
  }
  
  public static LinkedHashMap<String, String> getFilmMusicComposer(String uri) {
    LinkedHashMap<String, String> composed = new LinkedHashMap<>();
    String query = "SELECT ?musicComposer ?name WHERE {\n" +
                    "<" + uri + "> rdf:type dbo:Film ;\n" +
                    "dbo:musicComposer ?musicComposer .\n" +
                    "?musicComposer foaf:name ?name .\n" +
                    "FILTER (lang(?name) = 'en').\n" +
                    "}";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String musicComposerUri = qs.getResource("musicComposer").getURI();          
          String musicComposerName = qs.getLiteral("name").getString();
          if (musicComposerName != null && !musicComposerName.isEmpty()) {
            composed.put(musicComposerUri, musicComposerName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return composed;
  }
  
  public static LinkedHashMap<String, String> getFilmStudio(String uri) {
    LinkedHashMap<String, String> composed = new LinkedHashMap<>();
    String query = "SELECT ?studio ?name WHERE {\n" +
                    "<" + uri + "> rdf:type dbo:Film ;\n" +
                    "              dbo:studio ?studio .\n" +
                    "              ?studio rdfs:label ?name .\n" +
                    "FILTER (lang(?name) = 'en').\n" +
                    "}";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String studioUri = qs.getResource("studio").getURI();          
          String studioName = qs.getLiteral("name").getString();
          if (studioName != null && !studioName.isEmpty()) {
            composed.put(studioUri, studioName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return composed;
  }
  
  public static LinkedHashMap<String, String> getFilmDistributors(String uri) {
    LinkedHashMap<String, String> composed = new LinkedHashMap<>();
    String query = "SELECT ?distributor ?name WHERE {\n" +
                    "<" + uri + "> rdf:type dbo:Film ;\n" +
                    "              dbo:distributor ?distributor .\n" +
                    "              ?distributor rdfs:label ?name .\n" +
                    "FILTER (lang(?name) = 'en').\n" +
                    "}";
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String distributorUri = qs.getResource("distributor").getURI();          
          String distributoroName = qs.getLiteral("name").getString();
          if (distributoroName != null && !distributoroName.isEmpty()) {
            composed.put(distributorUri, distributoroName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return composed;
  }

  // ----------------------------------------------------------- Services to get Company Information
  
  public static String getCompanyNumberOfEmployees(String uri) {
    String employees = "Unknown";
    String query = "SELECT ?n WHERE {\n" +
                   "  <" + uri +"> rdf:type dbo:Company ;\n" +
                   "     dbo:numberOfEmployees ?n.\n" +
                   "}";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        employees = qs.get("n").toString();
        int ocurrence = employees.indexOf("^");
        employees = employees.substring(0,ocurrence);
      }  
    } catch(Exception e){
      e.printStackTrace();
    }
    return employees;
  }
  
  public static LinkedHashMap<String, String> getFilmsProduced(String uri) {
    LinkedHashMap<String, String> films = new LinkedHashMap<>();
    String query = "SELECT ?film ?name WHERE {\n" +
                   "  ?film rdf:type dbo:Film ;\n" +
                   "        dbp:studio <" + uri + "> ;\n" +
                   "        rdfs:label ?name .\n" +
                   "  FILTER (lang(?name) = 'en').\n" +
                   "  OPTIONAL {?film dbo:gross ?gross}\n" +
                   "} ORDER BY DESC(xsd:integer(?gross))";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String filmUri = qs.getResource("film").getURI();          
          String filmName = qs.getLiteral("name").getString();
          if (filmName != null && !filmName.isEmpty()) {
            films.put(filmUri, filmName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return films;
  }
  
  public static LinkedHashMap<String, String> getFilmsDistributed(String uri) {
    LinkedHashMap<String, String> films = new LinkedHashMap<>();
    String query = "SELECT ?film ?name WHERE {\n" +
                   "  ?film rdf:type dbo:Film ;\n" +
                   "        dbo:distributor <" + uri + "> ;\n" +
                   "        rdfs:label ?name .\n" +
                   "  FILTER (lang(?name) = 'en').\n" +
                   "  OPTIONAL {?film dbo:gross ?gross}\n" +
                   "} ORDER BY DESC(xsd:integer(?gross))";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String filmUri = qs.getResource("film").getURI();          
          String filmName = qs.getLiteral("name").getString();
          if (filmName != null && !filmName.isEmpty()) {
            films.put(filmUri, filmName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return films;
  }

  // ------------------------------------------------------------- Services to get Actor Information
  
  public static LinkedHashMap<String, String> getFilmsActorStarredIn(String uri) {
    LinkedHashMap<String, String> films = new LinkedHashMap<>();
    String query = "SELECT ?film ?name WHERE {\n" +
                   "?film rdf:type dbo:Film ;\n" +
                   "      dbo:starring <"+ uri +"> ;\n" +
                   "      rdfs:label ?name .\n" +
                   "  FILTER (lang(?name) = 'en').\n" +
                   "  OPTIONAL {?film dbo:gross ?gross}\n" +
                   "} ORDER BY DESC(xsd:integer(?gross))";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        String filmUri = qs.getResource("film").getURI();
        String filmName = qs.getLiteral("name").getString();
        if (filmName != null && !filmName.isEmpty()) {
          films.put(filmUri, filmName);
        }
      }  
    } catch (Exception e) {
      e.printStackTrace();
    }
    return films;
  }
    
  public static LinkedHashMap<String, String> getActorsActorStarredWith(String uri) {
    LinkedHashMap<String, String> actors = new LinkedHashMap<>();
    String query = "SELECT ?a ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:starring <" + uri + "> ;\n" +
                   "   dbo:starring ?a .\n" +
                   "?a foaf:name ?name .\n" +
                   "FILTER(lang(?name) = 'en').\n" +
                   "FILTER(?a != <" + uri + ">).\n" +
                   "}\n" +
                   "GROUP BY ?a ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String actorUri = qs.getResource("a").getURI();
          String actorName = qs.getLiteral("name").getString();
          if (actorName != null && !actorName.isEmpty()) {
            actors.put(actorUri, actorName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return actors;
  }
  
  public static LinkedHashMap<String, String> getStudiosActorWorkedFor(String uri) {
    LinkedHashMap<String, String> studios = new LinkedHashMap<>();
    String query = "SELECT ?s ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:starring <" + uri + "> ;\n" +
                   "   dbo:studio ?s .\n" +
                   "?s foaf:name ?name .\n" +
                   "FILTER (lang(?name) = 'en').\n" +
                   "}\n" +
                   "GROUP BY ?s ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String studioUri = qs.getResource("s").getURI();
          String studioName = qs.getLiteral("name").getString();
          if (studioName != null && !studioName.isEmpty()) {
            studios.put(studioUri, studioName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return studios;
  }
  
  // ---------------------------------------------------------- Services to get Director Information
  
  public static LinkedHashMap<String, String> getFilmsDirectorDirected(String uri) {
    LinkedHashMap<String, String> films = new LinkedHashMap<>();
    String query = "SELECT ?film ?name WHERE {\n" +
                   "?film rdf:type dbo:Film ;\n" +
                   "      dbo:director <"+ uri +"> ;\n" +
                   "      rdfs:label ?name .\n" +
                   "  FILTER (lang(?name) = 'en').\n" +
                   "  OPTIONAL {?film dbo:gross ?gross}\n" +
                   "} ORDER BY DESC(xsd:integer(?gross))";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        String filmUri = qs.getResource("film").getURI();
        String filmName = qs.getLiteral("name").getString();
        if (filmName != null && !filmName.isEmpty()) {
          films.put(filmUri, filmName);
        }
      }  
    } catch (Exception e) {
      e.printStackTrace();
    }
    return films;
  }
  
  public static LinkedHashMap<String, String> getActorsDirectorWorkedWith(String uri) {
    LinkedHashMap<String, String> actors = new LinkedHashMap<>();
    String query = "SELECT ?a ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:director <" + uri + "> ;\n" +
                   "   dbo:starring ?a .\n" +
                   "?a foaf:name ?name .\n" +
                   "FILTER(lang(?name) = 'en').\n" +
                   "FILTER(?a != <" + uri + ">).\n" +
                   "}\n" +
                   "GROUP BY ?a ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String actorUri = qs.getResource("a").getURI();
          String actorName = qs.getLiteral("name").getString();
          if (actorName != null && !actorName.isEmpty()) {
            actors.put(actorUri, actorName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return actors;
  }

  public static LinkedHashMap<String, String> getMusicComposersDirectorWorkedWith(String uri) {
    LinkedHashMap<String, String> actors = new LinkedHashMap<>();
    String query = "SELECT ?m ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:director <" + uri + "> ;\n" +
                   "   dbo:musicComposer ?m .\n" +
                   "?m foaf:name ?name .\n" +
                   "FILTER(lang(?name) = 'en').\n" +
                   "FILTER(?m != <" + uri + ">).\n" +
                   "}\n" +
                   "GROUP BY ?m ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String musicComposerUri = qs.getResource("m").getURI();
          String musicComposerName = qs.getLiteral("name").getString();
          if (musicComposerName != null && !musicComposerName.isEmpty()) {
            actors.put(musicComposerUri, musicComposerName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return actors;
  }
  
  public static LinkedHashMap<String, String> getStudiosDirectorWorkedFor(String uri) {
    LinkedHashMap<String, String> studios = new LinkedHashMap<>();
    String query = "SELECT ?s ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:director <" + uri + "> ;\n" +
                   "   dbo:studio ?s .\n" +
                   "?s foaf:name ?name .\n" +
                   "FILTER (lang(?name) = 'en').\n" +
                   "}\n" +
                   "GROUP BY ?s ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String studioUri = qs.getResource("s").getURI();
          String studioName = qs.getLiteral("name").getString();
          if (studioName != null && !studioName.isEmpty()) {
            studios.put(studioUri, studioName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return studios;
  }
  
  // ---------------------------------------------------- Services to get Music Composer Information
  
  public static LinkedHashMap<String, String> getFilmsMusicComposerComposed(String uri) {
    LinkedHashMap<String, String> films = new LinkedHashMap<>();
    String query = "SELECT ?film ?name WHERE {\n" +
                   "?film rdf:type dbo:Film ;\n" +
                   "      dbo:musicComposer <"+ uri +"> ;\n" +
                   "      rdfs:label ?name .\n" +
                   "  FILTER (lang(?name) = 'en').\n" +
                   "  OPTIONAL {?film dbo:gross ?gross}\n" +
                   "} ORDER BY DESC(xsd:integer(?gross))";
    
    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        String filmUri = qs.getResource("film").getURI();
        String filmName = qs.getLiteral("name").getString();
        if (filmName != null && !filmName.isEmpty()) {
          films.put(filmUri, filmName);
        }
      }  
    } catch (Exception e) {
      e.printStackTrace();
    }
    return films;
  }

  public static LinkedHashMap<String, String> getDirectorsMusicComposerWorkedWith(String uri) {
    LinkedHashMap<String, String> actors = new LinkedHashMap<>();
    String query = "SELECT ?d ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:musicComposer <" + uri + "> ;\n" +
                   "   dbo:director ?d .\n" +
                   "?d foaf:name ?name .\n" +
                   "FILTER(lang(?name) = 'en').\n" +
                   "FILTER(?d != <" + uri + ">).\n" +
                   "}\n" +
                   "GROUP BY ?d ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String directorUri = qs.getResource("d").getURI();
          String directorName = qs.getLiteral("name").getString();
          if (directorName != null && !directorName.isEmpty()) {
            actors.put(directorUri, directorName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return actors;
  }
  
  public static LinkedHashMap<String, String> getStudiosMusicComposerWorkedFor(String uri) {
    LinkedHashMap<String, String> studios = new LinkedHashMap<>();
    String query = "SELECT ?s ?name (COUNT(?f) AS ?n) WHERE {\n" +
                   "?f rdf:type dbo:Film ;\n" +
                   "   dbo:musicComposer <" + uri + "> ;\n" +
                   "   dbo:studio ?s .\n" +
                   "?s foaf:name ?name .\n" +
                   "FILTER (lang(?name) = 'en').\n" +
                   "}\n" +
                   "GROUP BY ?s ?name\n" +
                   "ORDER BY DESC(?n)\n" +
                   "LIMIT 4";

    try (QueryExecution qexec = createPrefixedQuery(query)) {
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
      ResultSet rs = qexec.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.nextSolution();
        try {
          String studioUri = qs.getResource("s").getURI();
          String studioName = qs.getLiteral("name").getString();
          if (studioName != null && !studioName.isEmpty()) {
            studios.put(studioUri, studioName);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      } 
    } catch(Exception e){
      e.printStackTrace();
    }
    return studios;
  }
  
}
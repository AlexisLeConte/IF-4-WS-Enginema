package project.controller.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project.controller.services.ResourceServices;
import project.controller.services.SparqlServices;
import project.utils.StringUtils;

public class QueryByUriAction implements Action {
  
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  private final static String FILM_COLOR = "#4444ff";
  private final static String COMPANY_COLOR = "#229922";
  private final static String PERSON_COLOR = "#ff4444";

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response) {
    String uri = request.getParameter("query");
    
    String category = ResourceServices.getCategory(uri);
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try {
      JsonObject container = null;
      if("film".equals(category)) {
        container = createFilmResponse(uri);
      } else if ("company".equals(category)) {
        container = createCompanyResponse(uri);
      } else if ("person".equals(category)) {
        container = createPersonResponse(uri);
      } else {
        container = new JsonObject();
        container.addProperty("responseType", "error");
      }
      response.getWriter().println(gson.toJson(container));
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  // --------------------------------------------------------------------------------- Film Response
  
  private JsonObject createFilmResponse(String uri){
    JsonObject container = new JsonObject();
    JsonObject responseContent = new JsonObject();
    JsonObject resourceGraph = new JsonObject();
    
    // Contains resource information: {info: {value: "", uri: ""}}
    JsonObject infos = new JsonObject();
    // Contains arbor.js graph nodes: {name: "", uri: "", color: ""}
    JsonObject nodes = new JsonObject();
    // Contains arbor.js graph edges: {start: {end: {type: ""}}}
    JsonObject edges = new JsonObject();
    // Contains arbor.js graph edges fom origin: {'origin': {end: {type: ""}}}
    JsonObject originEdges = new JsonObject();
    
    container.addProperty("responseType", "resourceInfoGraph");
    
    // Type
    addInformation("Type", new String[] {"film"}, infos);
    
    // Title
    String title = SparqlServices.getResourceName(uri);
    addOrigin(FILM_COLOR, title, uri, infos, nodes);
    
    // Budget
    String budget = SparqlServices.getFilmBudget(uri);
    addInformation("Budget", budget, infos);

    // Box Office
    String boxOffice = SparqlServices.getFilmBoxOffice(uri);
    addInformation("Box Office", boxOffice, infos);
    
    // Running Time
    String runtime = SparqlServices.getFilmRunningTime(uri);
    addInformation("Run Time", runtime, infos);
    
    // Actors
    LinkedHashMap<String, String> actors = SparqlServices.getFilmActors(uri);
    addRelationship(PERSON_COLOR, "Starring", actors, infos, nodes, edges, originEdges);
    
    // Director
    LinkedHashMap<String, String> director = SparqlServices.getFilmDirector(uri);
    addRelationship(PERSON_COLOR, "Directed By", director, infos, nodes, edges, originEdges);
    
    // Music Composer
    LinkedHashMap<String, String> musicComposer = SparqlServices.getFilmMusicComposer(uri);
    addRelationship(PERSON_COLOR, "Music By", musicComposer, infos, nodes, edges, originEdges);
    
    // Producer
    // TODO
    
    if (originEdges.size() > 0) {
      edges.add("origin", originEdges);
    }
    resourceGraph.add("nodes", nodes);
    resourceGraph.add("edges", edges);
    responseContent.add("resourceInfo", infos);
    responseContent.add("resourceGraph", resourceGraph);
    container.add("responseContent", responseContent);
    return container;
  }
  
  // ------------------------------------------------------------------------------ Company Response
  
  private JsonObject createCompanyResponse(String uri){
    JsonObject container = new JsonObject();
    JsonObject responseContent = new JsonObject();
    JsonObject resourceGraph = new JsonObject();
    
    // Array of resource information: {type: "", value: "", uri: ""}
    JsonObject infos = new JsonObject();
    // Contains arbor.js graph nodes: {name: "", uri: "", color: ""}
    JsonObject nodes = new JsonObject();
    // Contains arbor.js graph edges: {start: {end: {type: ""}}}
    JsonObject edges = new JsonObject();
    // Contains arbor.js graph edges fom origin: {'origin': {end: {type: ""}}}
    JsonObject originEdges = new JsonObject();
    
    container.addProperty("responseType", "resourceInfoGraph");
    
    // Type
    addInformation("Type", new String[] {"company"}, infos);
    
    // Name
    String name = SparqlServices.getResourceName(uri);
    addOrigin(COMPANY_COLOR, name, uri, infos, nodes);
    
    // Number of employees
    String employees = SparqlServices.getCompanyNumberOfEmployees(uri);
    addInformation("Number Of Employees", employees, infos);
    
    // Films produced
    LinkedHashMap<String, String> filmsProduced = SparqlServices.getFilmsProduced(uri);
    addInformation("Films Produced", filmsProduced, infos);
    
    // Films distributed
    LinkedHashMap<String, String> filmsDistributed = SparqlServices.getFilmsDistributed(uri);
    addInformation("Films Distributed", filmsDistributed, infos);
    
    // Major films distributed or produced
    LinkedHashMap<String, String> majorFilms = new LinkedHashMap<>();
    int numberOfFilms = 0;
    for (Map.Entry<String, String> entry : filmsProduced.entrySet()) {
      if(numberOfFilms++ >= 5) break;
      majorFilms.put(entry.getKey(), entry.getValue());
    }
    numberOfFilms = 0;
    for (Map.Entry<String, String> entry : filmsDistributed.entrySet()) {
      if(numberOfFilms++ >= 5) break;
      majorFilms.put(entry.getKey(), entry.getValue());
    }
    addRelationship(FILM_COLOR, "Highest grossing films", majorFilms, infos, nodes, edges, originEdges);
    
    // Companies Frequently Worked With
    // TODO
    
    if (originEdges.size() > 0) {
      edges.add("origin", originEdges);
    }
    resourceGraph.add("nodes", nodes);
    resourceGraph.add("edges", edges);
    responseContent.add("resourceInfo", infos);
    responseContent.add("resourceGraph", resourceGraph);
    container.add("responseContent", responseContent);
    return container;
  }
  
  // ------------------------------------------------------------------------------- Person Response
  
  private JsonObject createPersonResponse(String uri){
    JsonObject container = new JsonObject();
    JsonObject responseContent = new JsonObject();
    JsonObject resourceGraph = new JsonObject();
    
    // Array of resource information: {type: "", value: "", uri: ""}
    JsonObject infos = new JsonObject();
    // Contains arbor.js graph nodes: {name: "", uri: "", color: ""}
    JsonObject nodes = new JsonObject();
    // Contains arbor.js graph edges: {start: {end: {type: ""}}}
    JsonObject edges = new JsonObject();
    // Contains arbor.js graph edges fom origin: {'origin': {end: {type: ""}}}
    JsonObject originEdges = new JsonObject();
    
    container.addProperty("responseType", "resourceInfoGraph");

    // Name
    String name = SparqlServices.getResourceName(uri);
    addOrigin(PERSON_COLOR, name, uri, infos, nodes);
    
    boolean isActor = SparqlServices.isActor(uri);
    boolean isDirector = SparqlServices.isFilmDirector(uri);
    boolean isMusicComposer = SparqlServices.isFilmMusicComposer(uri);
    String types = "";
    if (isActor) {
      types += " actor";
      
      // Films he starred in
      LinkedHashMap<String, String> films = SparqlServices.getFilmsActorStarredIn(uri);
      addInformation("All Films Starred In", films, infos);
      
      // Actors he worked with
      LinkedHashMap<String, String> actors = SparqlServices.getActorsActorStarredWith(uri);
      addRelationship(PERSON_COLOR, "Starred With", actors, infos, nodes, edges, originEdges);
      
      // Studios he worked for
      LinkedHashMap<String, String> studios = SparqlServices.getStudiosActorWorkedFor(uri);
      addRelationship(COMPANY_COLOR, "Worked For", studios, infos, nodes, edges, originEdges);
      
      // Major films he played in
      LinkedHashMap<String, String> majorFilms = new LinkedHashMap<>();
      int numberOfFilms = 0;
      for (Map.Entry<String, String> entry : films.entrySet()) {
        if(numberOfFilms++ >= 5) break;
        majorFilms.put(entry.getKey(), entry.getValue());
      }
      addRelationship(FILM_COLOR, "Starred In", majorFilms, infos, nodes, edges, originEdges);
    }
    if (isDirector) {
      types += " director";
      
      // Films he directed
      LinkedHashMap<String, String> films = SparqlServices.getFilmsDirectorDirected(uri);
      addInformation("All Films Directed", films, infos);
      
      // Actors he worked with
      LinkedHashMap<String, String> actors = SparqlServices.getActorsDirectorWorkedWith(uri);
      addRelationship(PERSON_COLOR, "Known Actors", actors, infos, nodes, edges, originEdges);
      
      // Film music composers he worked with
      LinkedHashMap<String, String> musicComposers = SparqlServices.getMusicComposersDirectorWorkedWith(uri);
      addRelationship(PERSON_COLOR, "Known Composers", musicComposers, infos, nodes, edges, originEdges);
      
      // Studios he worked for
      LinkedHashMap<String, String> studios = SparqlServices.getStudiosDirectorWorkedFor(uri);
      addRelationship(PERSON_COLOR, "Worked For", studios, infos, nodes, edges, originEdges);
      
      // Major films he directed
      LinkedHashMap<String, String> majorFilms = new LinkedHashMap<>();
      int numberOfFilms = 0;
      for (Map.Entry<String, String> entry : films.entrySet()) {
        if(numberOfFilms++ >= 5) break;
        majorFilms.put(entry.getKey(), entry.getValue());
      }
      addRelationship(FILM_COLOR, "Directed", majorFilms, infos, nodes, edges, originEdges);
    }
    if (isMusicComposer) {
      types += " musicComposer";
      
      // Films he composed music for
      LinkedHashMap<String, String> films = SparqlServices.getFilmsMusicComposerComposed(uri);
      addInformation("All Films Composed Music", films, infos);
      
      // Film music composers he worked with
      LinkedHashMap<String, String> directors = SparqlServices.getDirectorsMusicComposerWorkedWith(uri);
      addRelationship(PERSON_COLOR, "Known Directors", directors, infos, nodes, edges, originEdges);
      
      // Studios he worked for
      LinkedHashMap<String, String> studios = SparqlServices.getStudiosMusicComposerWorkedFor(uri);
      addRelationship(PERSON_COLOR, "Worked For", studios, infos, nodes, edges, originEdges);
      
      // Major films he composed music for
      LinkedHashMap<String, String> majorFilms = new LinkedHashMap<>();
      int numberOfFilms = 0;
      for (Map.Entry<String, String> entry : films.entrySet()) {
        if(numberOfFilms++ >= 5) break;
        majorFilms.put(entry.getKey(), entry.getValue());
      }
      addRelationship(FILM_COLOR, "Composed For", majorFilms, infos, nodes, edges, originEdges);
    }
    
    // Types
    String[] typesArray = types.trim().split(" ");
    addInformation("Type", typesArray, infos);
    
    if (originEdges.size() > 0) {
      edges.add("origin", originEdges);
    }
    resourceGraph.add("nodes", nodes);
    resourceGraph.add("edges", edges);
    responseContent.add("resourceInfo", infos);
    responseContent.add("resourceGraph", resourceGraph);
    container.add("responseContent", responseContent);
    return container;
  }
  
  // ------------------------------------------------------------------------------ Response Factory
  
  private void addOrigin(String color, String name, String uri, JsonObject infos, JsonObject nodes) {
    // Add info
    JsonObject nameInfo = new JsonObject();
    nameInfo.addProperty("value", name);
    infos.add("name", nameInfo);
    
    // Add origin node
    JsonObject resourceNode = new JsonObject();
    resourceNode.addProperty("name", name);
    resourceNode.addProperty("uri", uri);
    resourceNode.addProperty("color", color);
    nodes.add("origin", resourceNode);
  }
  
  private void addInformation(String type, String value, JsonObject infos) {
    JsonObject info = new JsonObject();
    info.addProperty("value", value);
    infos.add(StringUtils.getAlphanumericIdentifier(type), info);
  }
  
  private void addInformation(String type, String[] values, JsonObject infos) {
    JsonArray valueArray = new JsonArray();
    for (String v : values) {
      valueArray.add(v);
    }
    infos.add(StringUtils.getAlphanumericIdentifier(type), valueArray);
  }
  
  private void addInformation(String type, LinkedHashMap<String, String> values, JsonObject infos) {
    if (values.isEmpty()) return;
    
    JsonArray valueArray = new JsonArray();
    for (Map.Entry<String, String> entry : values.entrySet()) {
      JsonObject value = new JsonObject();
      value.addProperty("value", entry.getValue());
      value.addProperty("uri", entry.getKey());
      valueArray.add(value);
    }
    infos.add(StringUtils.getAlphanumericIdentifier(type), valueArray);
  }
  
  private void addRelationship(String targetColor, String type, LinkedHashMap<String, String> values, JsonObject infos, JsonObject nodes, JsonObject edges, JsonObject originEdges) {
    if (values.isEmpty()) return;
    
    // Add Info
    JsonArray valueArray = new JsonArray();
    for (Map.Entry<String, String> entry : values.entrySet()) {
      JsonObject value = new JsonObject();
      value.addProperty("value", entry.getValue());
      value.addProperty("uri", entry.getKey());
      valueArray.add(value);
    }
    infos.add(StringUtils.getAlphanumericIdentifier(type), valueArray);
    
    // Add Graph Nodes
    for (Map.Entry<String, String> entry : values.entrySet()) {
      JsonObject entryNode = new JsonObject();
      entryNode.addProperty("name", entry.getValue());
      entryNode.addProperty("uri", entry.getKey());
      entryNode.addProperty("color", targetColor);
      nodes.add(StringUtils.getAlphanumericIdentifier(entry.getKey()), entryNode);
    }
    
    // Add Relationship Anonym Node
    JsonObject relationshipNode = new JsonObject();
    relationshipNode.addProperty("name", " ");
    relationshipNode.addProperty("color", "#000000");
    nodes.add(StringUtils.getAlphanumericIdentifier(type), relationshipNode);
    
    // Add Graph Edges
    JsonObject relationshipEdges = new JsonObject();
    for (Map.Entry<String, String> entry : values.entrySet()) {
      JsonObject entryEdgeProperty = new JsonObject();
      entryEdgeProperty.addProperty("type", "");
      relationshipEdges.add(StringUtils.getAlphanumericIdentifier(entry.getKey()), entryEdgeProperty);
    }
    edges.add(StringUtils.getAlphanumericIdentifier(type), relationshipEdges);
    JsonObject relationshipEdgeProperty = new JsonObject();
    relationshipEdgeProperty.addProperty("type", type);
    originEdges.add(StringUtils.getAlphanumericIdentifier(type), relationshipEdgeProperty);
  }
  
}

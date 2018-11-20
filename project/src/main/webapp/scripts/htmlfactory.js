function appendErrorMessage(component, message) {
  component.append("<div class='row-sm-2 side-title'>" + message + "</div>");
}

function appendTitle(component, message) {
  component.append("<div class='row-sm-2 side-title'>" + message + "</div>");
}

function appendQuerySuggestion(component, suggestion) {
  var suggestionClass;
  if (suggestion.resourceType === "company") {
    suggestionClass = "company-suggestion";
  }
  if (suggestion.resourceType === "film") {
    suggestionClass = "film-suggestion";
  }
  if (suggestion.resourceType === "person") {
    suggestionClass = "person-suggestion";
  }
  var onClickAction = "queryByUri('" + suggestion.resourceUri + "')";
  component.append("<div class='row-sm-2 search-suggestion " + suggestionClass + "' onclick=\"" + onClickAction + "\">" + suggestion.resourceName + "</div>");
}

function appendResourceInformation(component, information) {
  appendFilmInformation(component, information);
  appendFilmStarring(component, information);
}

function appendFilmInformation(component, information) {
  component.append("<div class='card'><div class='card-body'><div class='row-sm-2 search-suggestion film-suggestion text-center'>" + information[0].value + "</div></div>"
                  + "<ul class='list-group list-group-flush'>"
                  + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>" + information[1].type + "</span>" + information[1].value + " USD </li>"
                  + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>" + information[2].type + "</span>" + information[2].value + " USD </li>"
                  + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>" + information[3].type + "</span>" + information[3].value + " s </li>"
                  + "</ul></div>");
}

function appendFilmStarring(component, information) {
    let starringCard = "<div class='card my-2'><div class='card-body'><h5 class='card-title'>" + "Starring" + "</h5></div>";
    starringCard += "<ul class='list-group list-group-flush'>"
    
    information[4].value.map((actor) => {
      starringCard += "<li class='list-group-item no-border'><div class='row-sm-2 search-suggestion person-suggestion text-center'>" + actor.value + "</div></li>";
      return actor;
    });
    
    starringCard += "</ul></div>";
    component.append(starringCard);
}

function appendInformation(component, title, content) {
  component.append("<div class='row-sm-2'>" + title + " : " + content + "</div>");
}

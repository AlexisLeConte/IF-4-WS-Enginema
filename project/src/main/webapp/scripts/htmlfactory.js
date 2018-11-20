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
  component.append("<div class='row-sm-2 search-suggestion text-center " + suggestionClass + "' onclick=\"" + onClickAction + "\">" + suggestion.resourceName + "</div>");
}

function appendInformationCard(component, cardTitle, values, valuesStyle) {
  let card = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + cardTitle + "</h5></div>";
  card += "<ul class='list-group list-group-flush mb-2'>";

  values.map((item) => {
    card += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion " + valuesStyle + " text-center' onClick=\"queryByUri('" + item.uri + "')\">" + item.value + "</div></li>";
    return item;
  });

  card += "</ul></div>";
  component.append(card);
}

function appendResourceInformation(component, information) {
  if (information.Type[0] === "film") {
    appendFilmInformation(component, information);
  } else if (information.Type[0] === "company") {
    appendCompanyInformation(component, information);
  } else {
    appendPersonInformation(component, information);
  }
}

function appendFilmInformation(component, filmInfo) {
  // Info
  component.append("<div class='card'><div class='card-body'><div class='row-sm-2 search-suggestion film-suggestion text-center'>" + filmInfo.name.value + "</div></div>"
          + "<ul class='list-group list-group-flush'>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Budget</span>" + filmInfo.Budget.value + "</li>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Box Office</span>" + filmInfo.BoxOffice.value + "</li>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Run Time</span>" + filmInfo.RunTime.value + "</li>"
          + "</ul></div>");
  
  // Relationships
  if (filmInfo.Starring && filmInfo.Starring.length > 0) {
    appendInformationCard(component, "Starring", filmInfo.Starring, "person-suggestion");
  }
  if (filmInfo.DirectedBy && filmInfo.DirectedBy.length > 0) {
    appendInformationCard(component, "Director(s)", filmInfo.DirectedBy, "person-suggestion");
  }
  if (filmInfo.MusicBy && filmInfo.MusicBy.length > 0) {
    appendInformationCard(component, "Composer(s)", filmInfo.MusicBy, "person-suggestion");
  }
  if (filmInfo.Distributors && filmInfo.Distributors.length > 0) {
    appendInformationCard(component, "Distributor(s)", filmInfo.Distributors, "company-suggestion");
  }
  if (filmInfo.Studio && filmInfo.Studio.length > 0) {
    appendInformationCard(component, "Studio(s)", filmInfo.Studio, "company-suggestion");
  }
}

function appendCompanyInformation(component, companyInfo) {
  // Info
  component.append("<div class='card'><div class='card-body'><div class='row-sm-2 search-suggestion company-suggestion text-center'>" + companyInfo.name.value + "</div></div>"
          + "<ul class='list-group list-group-flush'>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Type</span>Company</li>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Number of Employees</span>" + companyInfo.NumberOfEmployees.value + "</li>"
          + "</ul></div>");
  
  // Relationships
  if (companyInfo.FilmsProduced && companyInfo.FilmsProduced.length > 0) {
    appendInformationCard(component, "Film(s) Produced (" + companyInfo.FilmsProduced.length + ")", companyInfo.FilmsProduced, "film-suggestion");
  }
  if (companyInfo.FilmsDistributed && companyInfo.FilmsDistributed.length > 0) {
    appendInformationCard(component, "Film(s) Distributed (" + companyInfo.FilmsDistributed.length + ")", companyInfo.FilmsDistributed, "film-suggestion");
  }
  if (companyInfo.Highestgrossingfilms && companyInfo.Highestgrossingfilms.length > 0) {
    appendInformationCard(component, "Highest Grossing Film(s)", companyInfo.Highestgrossingfilms, "film-suggestion");
  }
}

function appendPersonInformation(component, personInfo) {
  // Info
  var jobs = [];
  for (var i=0; i<personInfo.Type.length; i++) {
    if (personInfo.Type[i] === "actor") {
      jobs.push("Actor");
    }
    if (personInfo.Type[i] === "director") {
      jobs.push("Director");
    }
    if (personInfo.Type[i] === "musicComposer") {
      jobs.push("Music Composer");
    }
  }
  component.append("<div class='card'><div class='card-body'><div class='row-sm-2 search-suggestion person-suggestion text-center'>" + personInfo.name.value + "</div></div>"
          + "<ul class='list-group list-group-flush'>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Type</span>Person</li>"
          + "<li class='list-group-item no-border resource-info-item'><span style='font-weight:bold'>Job</span>" + jobs.join(", ") + "</li>"
          + "</ul></div>");
  
  // Relationships
  if (personInfo.AllFilmsStarredIn && personInfo.AllFilmsStarredIn.length > 0) {
    appendInformationCard(component, "Starred in " + personInfo.AllFilmsStarredIn.length + " Film(s)", personInfo.AllFilmsStarredIn, "film-suggestion");
  }
  if (personInfo.StarredWith && personInfo.StarredWith.length > 0) {
    appendInformationCard(component, "Often starred with", personInfo.StarredWith, "person-suggestion");
  }
  if (personInfo.WorkedForAsActor && personInfo.WorkedForAsActor.length > 0) {
    appendInformationCard(component, "Often worked for (as an actor)", personInfo.WorkedForAsActor, "company-suggestion");
  }
  
  if (personInfo.AllFilmsDirected && personInfo.AllFilmsDirected.length > 0) {
    appendInformationCard(component, "Directed " + personInfo.AllFilmsDirected.length + " Film(s)", personInfo.AllFilmsDirected, "film-suggestion");
  }
  if (personInfo.KnownActors && personInfo.KnownActors.length > 0) {
    appendInformationCard(component, "Often worked with " + personInfo.KnownActors.length + " actor(s)", personInfo.KnownActors, "person-suggestion");
  }
  if (personInfo.KnownComposers && personInfo.KnownComposers.length > 0) {
    appendInformationCard(component, "Often worked with " + personInfo.KnownComposers.length + " music composer(s)", personInfo.KnownComposers, "person-suggestion");
  }
  if (personInfo.WorkedForAsDirector && personInfo.WorkedForAsDirector.length > 0) {
    appendInformationCard(component, "Often worked for (as a director)", personInfo.WorkedForAsDirector, "company-suggestion");
  }
  
  if (personInfo.AllFilmsComposedMusic && personInfo.AllFilmsComposedMusic.length > 0) {
    appendInformationCard(component, "Composed music for " + personInfo.AllFilmsComposedMusic.length + " Film(s)", personInfo.AllFilmsComposedMusic, "film-suggestion");
  }
  if (personInfo.KnownDirectors && personInfo.KnownDirectors.length > 0) {
    appendInformationCard(component, "Often worked with " + personInfo.KnownDirectors.length + " director(s)", personInfo.KnownDirectors, "person-suggestion");
  }
  if (personInfo.WorkedForAsDirector && personInfo.WorkedForAsDirector.length > 0) {
    appendInformationCard(component, "Often worked for (as a music composer)", personInfo.WorkedForAsDirector, "company-suggestion");
  }
}

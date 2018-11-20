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

    if (information.Type[0] === "film") {
        appendFilmInformation(component, information);
    } else if (information.Type[0] === "company") {
        appendCompanyInformation(component, information);
    } else {
        appendPersonInformation(component, information);
    }
}

function appendFilmInformation(component, filmInfo) {
    //informations
    component.append("<div class='card'><div class='card-body'><div class='row-sm-2 search-suggestion film-suggestion text-center'>" + filmInfo.name.value + "</div></div>"
            + "<ul class='list-group list-group-flush'>"
            + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>Budget</span>" + filmInfo.Budget.value + "</li>"
            + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>Box Office</span>" + filmInfo.BoxOffice.value + "</li>"
            + "<li class='list-group-item no-border film-info-item'><span style='font-weight:bold'>Run Time</span>" + filmInfo.RunTime.value + "</li>"
            + "</ul></div>");

    appendFilmEntityCard(component, "Starring", filmInfo.Starring, "person-suggestion");
    appendFilmEntityCard(component, "Director", filmInfo.DirectedBy, "person-suggestion");
    appendFilmEntityCard(component, "Composer", filmInfo.MusicBy, "person-suggestion");
    appendFilmEntityCard(component, "Distributors", filmInfo.Distributors, "company-suggestion");


//    //Starring
//    let starringCard = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + "Starring" + "</h5></div>";
//    starringCard += "<ul class='list-group list-group-flush mb-2'>"
//
//    filmInfo.Starring.map((actor) => {
//        starringCard += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion person-suggestion text-center'>" + actor.value + "</div></li>";
//        return actor;
//    });

//    starringCard += "</ul></div>";
//    component.append(starringCard);
//
//    //Director
//    let directorCard = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + "Director" + "</h5></div>";
//    directorCard += "<ul class='list-group list-group-flush mb-2'>"
//
//    filmInfo.DirectedBy.map((director) => {
//        directorCard += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion person-suggestion text-center'>" + director.value + "</div></li>";
//        return director;
//    });
//
//    directorCard += "</ul></div>";
//    component.append(directorCard);
//
//    //Composer
//    let composerCard = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + "Composer" + "</h5></div>";
//    composerCard += "<ul class='list-group list-group-flush mb-2'>"
//
//    filmInfo.MusicBy.map((composer) => {
//        composerCard += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion person-suggestion text-center'>" + composer.value + "</div></li>";
//        return composer;
//    });
//
//    composerCard += "</ul></div>";
//    component.append(composerCard);
//
//    //Distributors
//    let distributorsCard = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + "Distributors" + "</h5></div>";
//    distributorsCard += "<ul class='list-group list-group-flush mb-2'>"
//
//    filmInfo.Distributors.map((distributor) => {
//        distributorsCard += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion company-suggestion text-center'>" + distributor.value + "</div></li>";
//        return distributor;
//    });
//
//    distributorsCard += "</ul></div>";
//    component.append(distributorsCard);
}

function appendFilmEntityCard(component, cardTitle, values, valuesStyle){
    
    let card = "<div class='card my-2'><div class='card-header'><h5 class='card-title mb-0 text-center'>" + cardTitle + "</h5></div>";
    card += "<ul class='list-group list-group-flush mb-2'>"

    values.map((item) => {
        card += "<li class='list-group-item no-border py-0'><div class='row-sm-2 search-suggestion " + valuesStyle + " text-center' onClick=\"queryByUri('" + item.uri + "')\">" + item.value + "</div></li>";
        return item;
    });

    card += "</ul></div>";
    component.append(card);
}

function appendCompanyInformation(component, companyInfo) {

}

function appendPersonInformation(component, personInfo) {

}

function appendInformation(component, title, content) {
  component.append("<div class='row-sm-2'>" + title + " : " + content + "</div>");
}

$(document).ready(function () {
// Initialization
    initGraph("#viewport");
    loadSuggestions();
    // Bind events to actions
    $("#search-button").on("click", function () {
        var query = $("#search-bar").val();
        if (query) {
            queryByName(query);
        } else {
            loadSuggestions();
        }
    });
    $("#search-bar").on("keyup", function (e) {
        e.preventDefault();
        if (e.keyCode === 13) {
            var query = $("#search-bar").val();
            if (query) {
                queryByName(query);
            } else {
                loadSuggestions();
            }
        }
    });
});
function loadSuggestions() {
    clearGraph();
    $.ajax({
        url: "./ActionServlet",
        method: "GET",
        data: {
            action: "loadSuggestions"
        },
        dataType: "json"
    }).done(function (data) {
        $("#query-results").html("");
        if (data.responseType === "suggestions") {
            appendTitle($("#query-results"), "Search suggestions");
            for (var i = 0; i < data.responseContent.length; i++) {
                appendQuerySuggestion($("#query-results"), data.responseContent[i]);
            }
        } else {
            appendErrorMessage($("#query-results"), "Service unavailable");
        }
    });
}

function queryByName(name) {
    $.ajax({
        url: "./ActionServlet",
        method: "GET",
        data: {
            action: "queryByName",
            query: name
        },
        dataType: "json"
    }).done(function (data) {
        $("#query-results").html("");
        clearGraph();
        if (data.responseType === "queryResults") {
            appendTitle($("#query-results"), "Search results");
            var numberOfFilms = 0;
            var numberOfCompanies = 0;
            var numberOfPersons = 0;
            var numberOfResults = data.responseContent.length;
            for (var i = 0; i < data.responseContent.length; i++) {
                var result = data.responseContent[i];
                appendQuerySuggestion($("#query-results"), result);
                if (result.resourceType === "film") {
                    numberOfFilms++;
                }
                if (result.resourceType === "company") {
                    numberOfCompanies++;
                }
                if (result.resourceType === "person") {
                    numberOfPersons++;
                }
            }
            var resultAnalysis = {
                nodes: {
                    nresults: {name: "Results (" + numberOfResults + ")", color: "#171a1d", uri: "", radius: 0},
                    nfilms: {name: "Films (" + numberOfFilms + ")", color: "#004085", uri: "", radius: 0},
                    ncompanies: {name: "Companies (" + numberOfCompanies + ")", color: "#155724", uri: "", radius: 0},
                    npersons: {name: "Persons (" + numberOfPersons + ")", color: "#822224", uri: "", radius: 0}
                },
                edges: {
                    nresults: {
                        nfilms: {type: ""},
                        ncompanies: {type: ""},
                        npersons: {type: ""}
                    }
                }
            };
            updateGraph(resultAnalysis);
        } else if (data.responseType === "noResult") {
            appendErrorMessage($("#query-results"), "Sorry, no results found :(");
        } else {
            appendErrorMessage($("#query-results"), "Service unavailable");
        }
    });
}

function queryByUri(uri) {

    let test = {
        "resourceInfo": [
            {
                "type": "name",
                "value": "Harry Potter (film series)"
            },
            {
                "type": "Budget",
                "value": "1.155E9"
            },
            {
                "type": "Box Office",
                "value": "7.723E9"
            },
            {
                "type": "Run Time",
                "value": "70740.0"
            },
            {
                "name": "Starring",
                "value": [
                    {
                        "value": "Daniel Radcliffe",
                        "uri": "http://dbpedia.org/resource/Daniel_Radcliffe"
                    },
                    {
                        "value": "Emma Watson",
                        "uri": "http://dbpedia.org/resource/Emma_Watson"
                    },
                    {
                        "value": "Rupert Grint",
                        "uri": "http://dbpedia.org/resource/Rupert_Grint"
                    }
                ]
            },
            {
                "name": "Directed By",
                "value": [
                    {
                        "value": "David Yates",
                        "uri": "http://dbpedia.org/resource/David_Yates"
                    },
                    {
                        "value": "Alfonso Cuarón",
                        "uri": "http://dbpedia.org/resource/Alfonso_Cuarón"
                    },
                    {
                        "value": "Mike Newell",
                        "uri": "http://dbpedia.org/resource/Mike_Newell_(director)"
                    },
                    {
                        "value": "Chris Columbus",
                        "uri": "http://dbpedia.org/resource/Chris_Columbus_(filmmaker)"
                    }
                ]
            },
            {
                "name": "Music By",
                "value": [
                    {
                        "value": "Alexandre Desplat",
                        "uri": "http://dbpedia.org/resource/Alexandre_Desplat"
                    },
                    {
                        "value": "Patrick Doyle",
                        "uri": "http://dbpedia.org/resource/Patrick_Doyle"
                    },
                    {
                        "value": "Nicholas Hooper",
                        "uri": "http://dbpedia.org/resource/Nicholas_Hooper"
                    },
                    {
                        "value": "John Williams",
                        "uri": "http://dbpedia.org/resource/John_Williams"
                    }
                ]
            }
        ]};

    $("#query-results").html("");
    appendResourceInformation($("#query-results"), test.resourceInfo);


//alert(JSON.stringify(film));

//  alert("URI request : " + uri);

//  $.ajax({
//    url: "./ActionServlet",
//    method: "GET",
//    data: {
//      action: "queryByUri",
//      query: uri
//    },
//    dataType: "json"
//  }).done(function (data) {
//      
//    $("#query-results").html("");
//    clearGraph();
//    if (data.responseType === "resourceInfoGraph") {
//      updateGraph(data.responseContent.resourceGraph);
//      appendResourceInformation($("#query-results"), data.responseContent.resourceInfo);
//    } else {
//      appendErrorMessage($("#query-results"), "Service unavailable :(");
//    }
//  });
}

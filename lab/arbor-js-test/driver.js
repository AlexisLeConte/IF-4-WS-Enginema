var sys;

$(document).ready(function() {
  sys = arbor.ParticleSystem(200, 800, 0.5)
  sys.parameters({gravity:true})
  sys.renderer = renderer("#viewport")
  
  var postLoadData = {
    "nodes": {
      "origin": {
        "name": "Northern Light Productions",
        "uri": "http://dbpedia.org/resource/Northern_Light_Productions",
        "color": "#155724"
      },
      "httpdbpediaorgresourceThe_Singing_Revolution": {
        "name": "The Singing Revolution",
        "uri": "http://dbpedia.org/resource/The_Singing_Revolution",
        "color": "#004085"
      },
      "Highestgrossingfilms": {
        "name": " ",
        "color": "#000000"
      }
    },
    "edges": {
      "Highestgrossingfilms": {
        "httpdbpediaorgresourceThe_Singing_Revolution": {
          "type": ""
        }
      },
      "origin": {
        "Highestgrossingfilms": {
          "type": "Highest grossing films"
        }
      }
    }
  };
  sys.merge(postLoadData);
})

function wrap(context, text, lineMaxLength) {
  var words = text.split(" ");
  var lines = [];
  var line = "";
  words.forEach(word => {
    var lineLength = context.measureText(line + " " + word).width;
    if (lineLength > lineMaxLength) {
      lines.push(line);
      line = word;
    } else {
      line = line + " " + word;
    }
  });
  lines.push(line);
  return lines;
}

function renderer(canvasId) {
  var canvas = $(canvasId).get(0)
  var context = canvas.getContext("2d");
  var particleSystem
  
  var renderer = {
    init:function(system) {
      particleSystem = system
      particleSystem.screenSize(canvas.width, canvas.height) 
      particleSystem.screenPadding(80)
      
      $(canvas).on('mousedown', function(e) {
        var pos = $(canvas).offset();
        mousePoint = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
        nearest = particleSystem.nearest(mousePoint);
        if(nearest.node.data.uri && nearest.distance < nearest.node.data.radius) {
          queryResourceByUri(nearest.node.data.uri);
        }
      })
    },
    
    redraw:function() {
      context.fillStyle = 'white'
      context.fillRect(0, 0, canvas.width, canvas.height)
      
      // edge: {source:Node, target:Node, length:#, data:{type:""}}
      // pt1:  {x:#, y:#}  source position in screen coords
      // pt2:  {x:#, y:#}  target position in screen coords
      particleSystem.eachEdge(function(edge, pt1, pt2) {
        var text = edge.data.type
        context.font = 'bold 14pt Calibri';
        context.textAlign = 'center';

        var rad1
        var rad2
        var centerX
        particleSystem.eachNode(function(node, position){
          if(position.x==pt1.x && position.y==pt1.y){
            rad1 = node.data.radius
          }
          else if(position.x==pt2.x && position.y==pt2.y){
            rad2 = node.data.radius
          }
        })
        
        // drawing
        context.strokeStyle = '#888888'
        context.lineWidth = 2
        context.beginPath()
        context.moveTo(pt1.x, pt1.y)
        context.lineTo(pt2.x, pt2.y)
        context.stroke()
        context.fillStyle = '#000000';
        var rotation = Math.acos((Math.abs(pt1.y-pt2.y))/(Math.sqrt(Math.pow((pt1.y-pt2.y),2)+Math.pow((pt1.x-pt2.x),2))))
        context.save();
        context.translate((pt1.x + pt2.x) / 2,(pt1.y + pt2.y) / 2);
        if(pt2.x>=pt1.x && pt2.y>=pt1.y){          // Bottom-right
          context.rotate(-rotation+Math.PI*0.5);
          centerX = (rad1-rad2)/2;
        }
        else if(pt2.x<=pt1.x && pt2.y>=pt1.y){     // Bottom-left
          context.rotate(rotation-Math.PI*0.5);
          centerX = (rad2-rad1)/2;
        }
        else if(pt2.x<=pt1.x && pt2.y<=pt1.y){     // Top-left
          context.rotate(-rotation+Math.PI*0.5);
          centerX = (rad2-rad1)/2;
        }
        else if(pt2.x>=pt1.x && pt2.y<=pt1.y){     // Top-right
          context.rotate(rotation-Math.PI*0.5);
          centerX = (rad1-rad2)/2;
        }
        
        context.fillText(text, centerX, -8);
        context.restore();
      })

      // node: {mass:#, p:{x,y}, name:"", data:{name:"", uri:"", radius:#, color:#}}
      // pt:   {x:#, y:#}  node position in screen coords
      particleSystem.eachNode(function(node, pt) {
        // text wrapping
        context.font = '14pt Calibri';
        context.textAlign = 'center';
        var lines = wrap(context, node.data.name, 100);
        var radius = 0;
        lines.forEach(line => {
          if (context.measureText(line).width >= radius) {
            radius = context.measureText(line).width;
          }
        });
        radius = (3 * radius) / 4;
        if (radius > 100) {
          radius = 100;
        }
        
        // set node radius
        node.data.radius = radius;
        
        // drawing
        context.beginPath();
        context.arc(pt.x, pt.y, radius, 0, 2 * Math.PI, false);
        context.fillStyle = node.data.color;
        context.fill();
        context.lineWidth = 4;
        context.strokeStyle = "#000000";
        context.stroke();
        context.fillStyle = "#000000";
        for (var i = 0; i < lines.length; i++) {
          context.fillText(lines[i], pt.x, pt.y + 16 * (i - lines.length / 2) + 8)
        }
      })		
    },
  }
  return renderer
}

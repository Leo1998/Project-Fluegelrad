<?php
	//Spam protection, IP ban, Initalize PDO
	$hostRequired=true;
	require('../scripts/sitePrepare.php');
	
	$statement = $pdo->prepare("SELECT * FROM `locations`");
	$statement->execute();
	
	$locations = array();
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$locations[] = $row;
	}
	
	
	$statement = $pdo->prepare("SELECT * FROM `sponsors`");
	$statement->execute();
	
	$sMax = 0;
	$sponsors = array();
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$sponsors[] = $row;
		if($row['id'] > $sMax){
			$sMax = $row['id'];
		}
	}
	
	$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	
	echo "
		<script type=\"text/javascript\">
			const knownLocs = ".json_encode($locations,JSON_PRETTY_PRINT).";
			const sponsors = ".json_encode($sponsors,JSON_PRETTY_PRINT).";
			const maxSponsorId = ".$sMax.";
			$hostStuff;
		</script>
	";
?>
<!doctype html>
<html>
<head>
	<title>Event erstellen</title>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<meta charset="utf-8">
	<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
	<meta name="Description" content="Ersellen sie ihr Event!" /> 
    <link href="css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
           die für die Bildschirmausgabe zuständig ist. Je nachdem in welchem Verzeichnis sich diese Datei befindet muss der Pfad angepasst werden. -->
	<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.3/cookieconsent.min.css" /> <!-- Cookie Message -->
	<script src="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.3/cookieconsent.min.js"></script>
	<script>
		window.addEventListener("load", function(){
		window.cookieconsent.initialise({
			"palette": {
				"popup": {
					"background": "#ffffff",
					"text": "#000000"
				},
				"button": {
					"background": "#dddddd",
					"text": "#000000"
				}
			},
			"theme": "edgeless",
			"position": "bottom-right"
		})});
		
		var imageCount = 0;
		var sponsorCount = 0;
		
        var map,vectorLayer,selectMarkerControl,selectedFeature;
        var lat = 51.514;
        var lon = 7.463;
        var zoom = 15;
        var curpos = new Array();
        var position;
		
		var newLocMarker;
		var state = 0;
		
		var vectorLayer

        var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
        var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection

        var cntrposition = new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
		
        function init() {
            map = new OpenLayers.Map("Map",{
				controls: 
					[
					new OpenLayers.Control.Navigation(),
					new OpenLayers.Control.PanZoomBar(),                        
					new OpenLayers.Control.LayerSwitcher({}),
					new OpenLayers.Control.ScaleLine(),
                    new OpenLayers.Control.OverviewMap(),
                    ]
                });
            var mapnik      = new OpenLayers.Layer.OSM("MAP"); 
			vectorLayer = new OpenLayers.Layer.Vector("Locations");
			
            map.addLayers([mapnik,vectorLayer]);
            map.addLayer(mapnik);
            map.setCenter(cntrposition, zoom);
			
			var markerClick = {
				selector: new OpenLayers.Control.SelectFeature(vectorLayer, { onSelect: createPopup, onUnselect: destroyPopup })
			};
			
			map.addControl(markerClick['selector']);
			markerClick['selector'].activate();
			
            var click = new OpenLayers.Control.Click();
            map.addControl(click);

            click.activate();
			
			var locationSelect = document.getElementById('location');
			
			for (var i = 0; i < knownLocs.length; i++){
				var address = knownLocs[i]["address"];
				var lonLat = new OpenLayers.LonLat(knownLocs[i]["longitude"],knownLocs[i]["latitude"]);
				var id = knownLocs[i]["id"];
				
				placeMarker(lonLat,address,id);
				
				var opt = document.createElement('option');
				opt.value = id;
				opt.innerHTML = address;
				locationSelect.appendChild(opt);
			}
			
			
			var sponsorsSelect = document.getElementById('sponsors');
			
			for (var i = 0; i < sponsors.length; i++){
				var id = sponsors[i]["id"];
				var name = sponsors[i]["name"];
				var imagePath = sponsors[i]["imagePath"];
				
				var li = document.createElement('li');
				li.class = "sponsorNode";
				
				var checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.name = "sponsor"+id;
				checkbox.value = id;
				checkbox.id = "sponsor"+id;
				checkbox.class = "sponsorCheckbox";
				
				var img=document.createElement('img');
				img.src= "../"+imagePath;
				img.alt= "Bild nicht verfügbar";
				img.title= "Vorschau";
				img.style.height="50px";
				img.htmlFor = "sponsor"+id;
				img.class = "sponsorImage";
				
				var label = document.createElement('label');
				label.htmlFor = "sponsor "+id;
				label.appendChild(document.createTextNode(name));
				label.class = "sponsorName";
				
				
				li.appendChild(checkbox);
				li.appendChild(img);
				li.appendChild(document.createElement('br'));
				li.appendChild(label);
				
				sponsorsSelect.appendChild(li);
				sponsorsSelect.appendChild(document.createElement('br'));
			}
			
			
			var navLogout = document.createElement("a");
			navLogout.href = "logout.php";
			navLogout.innerHTML = "Abmelden";
			
			document.getElementById("loginfield").appendChild(navLogout);
			
			var div = document.createElement('div');
			
			div.appendChild(document.createTextNode("Angemeldet als:"));
			div.appendChild(document.createElement("br"));
			div.appendChild(document.createTextNode(hostStuff["name"]));
			div.appendChild(document.createElement("br"));
			
			var image = document.createElement('img');
			image.src= "../"+hostStuff["image"];
			image.alt= "Bild nicht verfügbar";
			image.title= "Vorschau";
			image.style.width="100px";
			image.style.height="100px";
			div.appendChild(image);
			
			div.appendChild(document.createElement("br"));
			
			var logout = document.createElement("a");
			logout.href = "logout.php";
			logout.innerHTML = "Abmelden";
			div.appendChild(logout);
			
			document.getElementById("hostInfos").appendChild(div);
			
			document.getElementById("hostInfos").className = "show";

			
			document.getElementById('maxSponsorId').value = maxSponsorId;
		}

		OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {               
			defaultHandlerOptions: {
				'single': true,
				'double': false,
				'pixelTolerance': 0,
				'stopSingle': false,
				'stopDouble': false
				},

			initialize: function(options) {
				this.handlerOptions = OpenLayers.Util.extend(
					{}, this.defaultHandlerOptions
					);
				OpenLayers.Control.prototype.initialize.apply(
					this, arguments
					);
				this.handler = new OpenLayers.Handler.Click(
					this, {
						'click': this.trigger
					}, this.handlerOptions
					);
			},

			trigger: function(e) {
				var checkbox = document.getElementById("setMarker");
				if(checkbox.checked){
					checkbox.checked = false;
					var lonlat = map.getLonLatFromPixel(e.xy);
					lonlat1= new OpenLayers.LonLat(lonlat.lon,lonlat.lat).transform(toProjection,fromProjection);
					document.getElementById("latitude").value = lonlat1.lat;
					document.getElementById("longitude").value = lonlat1.lon;
					if(!newLocMarker){
						newLocMarker = placeMarker(lonlat1,'I am Error',0);
					}else{
						newLocMarker.move(lonlat);
					}
				}
			}
		});
		
		function applyLonLat(){
			var lon = document.getElementById("longitude").value;
			var lat = document.getElementById("latitude").value;
			map.setCenter((new OpenLayers.LonLat(lon, lat)).transform( fromProjection, toProjection), zoom);
		}
		
		function placeMarker(lonlat,popuptext,v){
			var feature = new OpenLayers.Feature.Vector(
				new OpenLayers.Geometry.Point( lonlat.lon,  lonlat.lat ).transform(fromProjection, toProjection),
				{description:popuptext , value:v} ,
				{externalGraphic: '../img/newerMarker.png', graphicHeight: 25, graphicWidth: 21, graphicXOffset:-12, graphicYOffset:-25, fillOpacity:1  }
				);    
			vectorLayer.addFeatures(feature);
			return feature;
		}
		
		function setNewMarker(){
			var lon = document.getElementById("longitude").value;
			var lat = document.getElementById("latitude").value;
			var lonlat = new OpenLayers.LonLat(lon, lat);
			var lonlat1 = new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
			if(!newLocMarker){
				newLocMarker = placeMarker(lonlat,'I am Error',0);
			}else{
				newLocMarker.move(lonlat1);
			}
			map.setCenter(lonlat1, zoom);
		}
		
		function createPopup(feature) {
			var value = feature.attributes.value;
			feature.popup = new OpenLayers.Popup.FramedCloud("pop",
				feature.geometry.getBounds().getCenterLonLat(),
				null,
				'<div class="markerContent">'+(value == 0 ? document.getElementById('newAddress').value : feature.attributes.description)+'</div>',
				null,
				true,
				function() { controls['selector'].unselectAll(); }
			);
			//feature.popup.closeOnMove = true;
			map.addPopup(feature.popup);
			if(value == 0){
				document.getElementById('newLoc').checked = true;
			}else{
				document.getElementById('oldLoc').checked = true;
				document.getElementById('location').value = value;
			}
		}

		function destroyPopup(feature) {
			feature.popup.destroy();
			feature.popup = null;
		}
		
		function previewImage(input,id) {
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function (e) {
					var preview = document.getElementById("previewImage"+id);
					preview.src = e.target.result;
				}
				reader.readAsDataURL(input.files[0]);
			}
		}
		
		function previewSponsor(input,id) {
			if(input.files && input.files[0]){
				var reader = new FileReader();
				reader.onload = function (e) {
					var preview = document.getElementById("previewSponsor"+id);
					preview.src = e.target.result;
				}
				reader.readAsDataURL(input.files[0]);
			}
		}
		
		function createImage(){
			imageCount++;
			var li = document.createElement('li');
			li.id = "imageNode"+imageCount;
			li.class = "imageNode";
			
			var span1 = document.createElement('span');
			span1.innerHTML = '<input type="file" id="imageImage'+imageCount+'" name="imageImage'+imageCount+'" onchange="previewImage(this,'+imageCount+');"/>';
			span1.class = "imageInput";
			
			var iPreview = document.createElement('img');
			iPreview.id = "previewImage"+imageCount;
			iPreview.name = "previewImage"+imageCount;
			iPreview.src = "#";
			iPreview.alt = "Bild";
			iPreview.title = "Preview";
			iPreview.style.height = "100px";
			iPreview.class = "imagePreview";
			
			var iDesc = document.createElement('input');
			iDesc.type = "text";
			iDesc.id = "descriptionImage"+imageCount;
			iDesc.name = "descriptionImage"+imageCount;
			iDesc.maxLength = "50";
			iDesc.placeholder = "Bildbeschreibung";
			iDesc.class = "imageDesc";
			
			var span2 = document.createElement('span');
			span2.innerHTML = '<button type="button" onclick="removeImage('+imageCount+');" >Bild entfernen</button>';
			span2.class = "imageRemove";
			
			li.appendChild(span2);
			li.appendChild(span1);
			li.appendChild(iPreview);
			li.appendChild(document.createElement('br'));
			li.appendChild(iDesc);
			
			document.getElementById('images').appendChild(li);
			document.getElementById('imageCount').value = imageCount;
		}
		
		function createSponsor(){
			sponsorCount++;
			var li = document.createElement('li');
			li.id = "newSponsor"+sponsorCount;
			li.class = "newSponsorNode";
			
			var label = document.createTextNode("Neuer Sponsor");
			label.class = "newSponsorLabel"
			
			var sName = document.createElement('input');
			sName.type = "text";
			sName.id = "nameSponsor"+sponsorCount;
			sName.name = "nameSponsor"+sponsorCount;
			sName.maxLength = "30";
			sName.placeholder = "Name";
			sName.class = "newSponsorName";
			
			var sPhone = document.createElement('input');
			sPhone.type = "text";
			sPhone.id = "phoneSponsor"+sponsorCount;
			sPhone.name = "phoneSponsor"+sponsorCount;
			sPhone.maxLength = "30";
			sPhone.placeholder = "Telefon (optional)";
			sPhone.class = "newSponsorPhone";
			
			var sMail = document.createElement('input');
			sMail.type = "text";
			sMail.id = "mailSponsor"+sponsorCount;
			sMail.name = "mailSponsor"+sponsorCount;
			sMail.maxLength = "30";
			sMail.placeholder = "E-Mail (optional)";
			sMail.class = "newSponsorMail";
			
			var sWeb = document.createElement('input');
			sWeb.type = "text";
			sWeb.id = "webSponsor"+sponsorCount;
			sWeb.name = "webSponsor"+sponsorCount;
			sWeb.maxLength = "30";
			sWeb.placeholder = "Website (optional)";
			sWeb.class = "newSponsorWeb";
			
			var sDesc = document.createElement('textarea');
			sDesc.id = "descriptionSponsor"+sponsorCount;
			sDesc.name = "descriptionSponsor"+sponsorCount;
			sDesc.placeholder = "Beschreibung";
			sDesc.class = "newSponsorDesc";
			
			var span1 = document.createElement('span');
			span1.innerHTML = '<input type="file" id="imageSponsor'+sponsorCount+'" name="imageSponsor'+sponsorCount+'" onchange="previewSponsor(this,'+sponsorCount+');"/>';
			span1.class = "newSponsorImgInput";
			
			var sPreview = document.createElement('img');
			sPreview.id = "previewSponsor"+sponsorCount;
			sPreview.name = "previewSponsor"+sponsorCount;
			sPreview.src = "#";
			sPreview.alt = "Bild";
			sPreview.title = "Preview";
			sPreview.style.height = "100px";
			sPreview.class = "newSponsorImgPreview";
			
			var span2 = document.createElement('span');
			span2.innerHTML = '<button type="button" onclick="removeSponsor('+sponsorCount+');" >Sponsor entfernen</button>';
			span2.class = "newSponsorRemove";
			
			li.appendChild(label);
			li.appendChild(sName);
			li.appendChild(sPhone);
			li.appendChild(sMail);
			li.appendChild(sWeb);
			li.appendChild(sDesc);
			li.appendChild(span1);
			li.appendChild(sPreview);
			li.appendChild(span2);
			
			document.getElementById('newSponsors').appendChild(li);
			document.getElementById('sponsorCount').value = sponsorCount;
		}
		
		function removeImage(id){
			li = document.getElementById("imageNode"+id);
			li.parentNode.removeChild(li);
		}
		
		function removeSponsor(id) {
			li = document.getElementById("newSponsor"+id);
			li.parentNode.removeChild(li);
		}
	</script>
</head>
<body onload='init();'>
  <header>
   <a id="logo" href="./"><span>Do</span>-Aktiv</a> 
  </header>
  
  <nav>
  	<ul>
		<li><a href="../index.php">Home</a></li>
   		<li class="active">Event erstellen</li>
   		<li><a href="eventList.php">Eventliste</a></li>
		<li id= "loginfield"></li>
  	</ul>
  </nav>

  <main role="main">


  <section>
   	
		<div id= "formDiv">
			<form action="uploadEvent.php" enctype="multipart/form-data" id="event" method="post">
				<ul style="list-style-type: none;">
					<li>
						<h2 for="name">Eventname:</h2>
						<input type="text" name="eventName" id="eventName" maxLength="30" placeholder = "Eventname">
					</li>
					<br>
					<li>
						<h2 for="price">Preis:</h2>
						<input type="number" name="price" id="price" min="0" max="128" step="0.01" value = 0>
						<label> &#x20ac;</label>
					</li>
					<br>
					<li>
						<h2 for="participants">Maximale Teilnehmerzahl:</h2>
						<input type="number" name="participants" id="participants" min="1" placeholder = "Keine">
						<input type="checkbox" name="countParticipants" id="countParticipants" checked>
						<label for="countParticipants">Teilnehmer z&#xe4;hlen</label>
					</li>
					<br>
					<li>
						<h2>Datum und Zeit:</h2>
						<label>von</label>
						<input type="datetime-local" name="dateStart" id="dateStart" max="9999-12-31T23:59:59">
						<label>bis</label>
						<input type="datetime-local" name="dateEnd" id="dateEnd" max="9999-12-31T23:59:59">
					</li>
					<br>
					<li>
						<h2>Beschreibung:</h2>
						<textarea name="description" id="description" placeholder = "Beschreibung"></textarea>
					</li>
					<br>
					<li>
						<h2>Alter:</h2>
						<label>von</label>
						<input type="number" name="ageMin" id="ageMin" min="0" max="99" value = 0>
						<label>bis</label>
						<input type="number" name="ageMax" id="ageMax" min="0" max="99" value = 99>
					</li>
					<br>
					<li>
						<h2>Position:</h2>
						<input type = "radio" name = "knowLoc" id = "oldLoc" value = "1" checked></input>
						<label for="oldLoc">Bekannte Location</label>
						<br>
						<select name = "location" id = "location">
						</select>
						<br>
						<input type = "radio" name = "knowLoc" id = "newLoc" value = "2"></input>
						<label for="newLoc">Neue Location</label>
						<br>
						<input type = "text" name = "newAddress" id = "newAddress" max = 30 placeholder = "Addresse"></input>
						<input type = "checkbox" name = "setMarker" id = "setMarker"></input>
						<label for="setMarker">Marker platzieren</label>
						<br>
						<br>
						<input type="number" name="latitude" id="latitude" min="-180" max = "180" step="any" value = 51.514>
						<input type="number" name="longitude" id="longitude" min="-180" max = "180" step="any" value = 7.463>
						<br>
						<br>
						<button type="button" onClick="applyLonLat();">Zu Position springen</button>
						<button type="button" onClick="setNewMarker();">Marker platzieren</button>
						<br>
						<br>
						<div id="Map" ></div>
						<div id="mapHeader">
							&#xa9;<a href="http://www.openstreetmap.org">OpenStreetMap</a>
							und <a href="http://www.openstreetmap.org/copyright">Mitwirkende</a>,
							<a href="http://creativecommons.org/licenses/by-sa/2.0/deed.de">CC-BY-SA</a>
						</div>
					</li>
					<li>
						<h2>Sponsoren:</h2>
						<input type = "number" id = "maxSponsorId" name = "maxSponsorId" style = "position : absolute ; display : none ;" value = "0">
						<ul name = "sponsors" id = "sponsors"  style="list-style-type: none;" multiple>
						</ul>
						<input type = "number" id = "sponsorCount" name = "sponsorCount" style = "position : absolute ; display : none ;" value = "0">
						<button type="button" onClick="createSponsor();">Neuer Sponsor</button>
						<ul name = "newSponsors" id = "newSponsors"  style="list-style-type: none;">
						</ul>
					</li>
					<br>
					<li>
						<h2>Bilder:</h2>
						<input type = "number" id = "imageCount" name = "imageCount" style = "position : absolute ; display : none ;" value = "0">
						<br>
						<button type="button" onClick="createImage();">Neues Bild</button>
						<br>
						<label>Das erste Bild wird als Vorschau-Bild des Events angezeigt</label>
						<ul name = "images" id="images"  style="list-style-type: none;">
						</ul>
					</li>
					<br>
					<li>
						<input type="submit" value="Event erstellen" class="btnSubmit" />
					</li>
				</ul>
			</form>
		</div>
	
    </section>    
   
  	<aside> 
    	<h3>Tipps und Anmerkungen beim Erstellen</h3>
    	<p>
        	Alle Felder, die mit einem "*" markiert sind, müssen ausgefüllt werden.<br>
			<br>
			Wenn keine maximale Teilnehmerzahl angegeben wurde, besitzt das Event keine Teilnehmerbeschränkung.<br>
			Wenn die Teilnehmer gezählt werden sollen, können sich Nutzer der App für das Event anmelden.<br>
			<br>
			Bei der Angabe des Ortes muss entweder ein bekannter Ort ausgewählt oder ein neuer erstellt werden.<br>
			Ein bekannter Ort kann entweder aus der Liste ausgesucht oder auf der Karte angeklickt werden, um ihn auszuwählen.<br>
			Wenn ein neuer Ort erstellt werden soll, müssen alle Felder gefüllt sein.<br>
			Wenn "Marker platzieren" gedrückt wurde, kann man auf einen Punkt auf der Karte klicken um die Koordinaten dieses Punktes als Koordinaten der neuen Ortes zu übernehmen.<br>
        	<br>
			Nur Bilder mit dem Dateiformat .png .jpg oder .gif sind erlaubt.<br>
			Bilder dürfen maximal 500kb groß sein.<br>
			Bitte achten sie darauf, dass die Bilder fehlerfrei sind, weil sie sonst nicht hochgeladen werden.<br>
			Sollte ein Bild Fehler enthalten oder keine Beschreibung haben, wird dieses nicht hochgeladen.<br>
			<br>
			Wenn notwendige Felder unausgefüllt waren, wird das Event nicht hochgeladen.<br>
			Wenn notwendige Felder bei Bildern oder bei neuen Sponsoren unausgefüllt waren, werden diese nicht hochgeladen. Das Event kann trotzdem hochgeladen werden.<br>
         </p>
	</aside>
    </main>
  	
    <footer>
		 
	</footer>
	
	<div id="hostInfos"></div>
</body>
</html>

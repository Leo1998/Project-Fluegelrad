<?php
	//Spam protection, IP ban, Initalize PDO
	$type=0;
	require('spamProtector.php');
	
	$locationsGet = $pdo->prepare("SELECT * FROM `locations`");
	$locationsGet->execute();
	
	$lIdArr = array();
	$adArr = array();
	$loArr = array();
	$laArr = array();
	
	while($row = $locationsGet->fetch()) {
		$lIdArr[] = $row['id'];
		$adArr[] = $row['address'];
		$loArr[] = $row['longitude'];
		$laArr[] = $row['latitude'];
	}
	
	$addressIds = implode("\",\"",$lIdArr);
	$addresses = implode("\",\"",$adArr);
	$longitudes = implode("\",\"",$loArr);
	$latitudes = implode("\",\"",$laArr);
	
	$addressIds = "const addressIds = Array(\"".$addressIds."\");";
	$addresses = "const addresses = Array(\"".$addresses."\");";
	$longitudes = "const longitudes = Array(\"".$longitudes."\");";
	$latitudes = "const latitudes = Array(\"".$latitudes."\");";
	
	$sponsorsGet = $pdo->prepare("SELECT * FROM `sponsors`");
	$sponsorsGet->execute();
	
	$sIdArr = array();
	$imArr = array();
	$naArr = array();
	
	while($row = $sponsorsGet->fetch()) {
		$sIdArr[] = $row['id'];
		$imArr[] = $row['imagePath'];
		$naArr[] = $row['name'];
	}
	
	$sponsorIds = implode("\",\"",$sIdArr);
	$sponsorImgs = implode("\",\"",$imArr);
	$sponsors = implode("\",\"",$naArr);
	
	$sponsorIds = "const sponsorIds = Array(\"".$sponsorIds."\");";
	$sponsorImgs = "const sponsorImgs = Array(\"".$sponsorImgs."\");";
	$sponsors = "const sponsors = Array(\"".$sponsors."\");";
	
	echo "
		<script type=\"text/javascript\">
			$addressIds
			$addresses
			$longitudes
			$latitudes
			$sponsorIds
			$sponsorImgs
			$sponsors
		</script>
	";
?>

<html>

<head>
    <title>Event erstellen</title>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
    <script>
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
			
			for(var i = 0 ; i < longitudes.length  && i < latitudes.length && i < addresses.length ; i++){
				placeMarker((new OpenLayers.LonLat(longitudes[i],latitudes[i])),addresses[i],addressIds[i]);
				var opt = document.createElement('option');
				opt.value = addressIds[i];
				opt.innerHTML = addresses[i];
				locationSelect.appendChild(opt);
			}
			
			var sponsorsSelect = document.getElementById('sponsors');
			
			for(var i = 0 ; i < sponsors.length ; i++){
				var checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.name = "sponsor "+sponsorIds[i];
				checkbox.value = sponsorIds[i];
				checkbox.id = "sponsor "+sponsorIds[i];
				
				var img=document.createElement('img');
				img.src= sponsorImgs[i];
				img.alt= "Bild nicht verfügbar";
				img.title= "Vorschau";
				img.style.height="50px";
				img.htmlFor = "sponsor"+sponsorIds[i];
				
				var label = document.createElement('label')
				label.htmlFor = "sponsor "+sponsorIds[i];
				label.appendChild(document.createTextNode(sponsors[i]));
				
				sponsorsSelect.appendChild(checkbox);
				sponsorsSelect.appendChild(img);
				sponsorsSelect.appendChild(label);
			}
		};

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
						newLocMarker = placeMarker(lonlat1,'Test',0);
					}else{
						newLocMarker.move(lonlat);
					}
				}
			}
			/**
			
			
			
			*/
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
				{externalGraphic: 'img/newerMarker.png', graphicHeight: 25, graphicWidth: 21, graphicXOffset:-12, graphicYOffset:-25, fillOpacity:1  }
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
				newLocMarker = placeMarker(lonlat,'Test',0);
			}else{
				newLocMarker.move(lonlat1);
			}
			map.setCenter(lonlat1, zoom);
		}
		
		function createPopup(feature) {
			feature.popup = new OpenLayers.Popup.FramedCloud("pop",
				feature.geometry.getBounds().getCenterLonLat(),
				null,
				'<div class="markerContent">'+feature.attributes.description+'</div>',
				null,
				true,
				function() { controls['selector'].unselectAll(); }
			);
			feature.popup.closeOnMove = true;
			map.addPopup(feature.popup);
			var value = feature.attributes.value;
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
		
		function previewImage(input) {
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function (e) {
					imageCount++;
					var li = document.createElement('li');
					li.id = "previewNode"+imageCount;
					
					var img=document.createElement('img');
					img.src= e.target.result;
					img.alt= "Vorschau nicht verfügbar";
					img.title= "Vorschau";
					img.style.height="100px";
					
					var text = document.createElement('input');
					text.type = "text";
					text.id = "imageDescription"+imageCount;
					text.name = "imageDescription"+imageCount;
					text.maxLength = "50";
					text.placeholder = "Beschreibung";
					
					var srcText = document.createElement('input');
					srcText.type = "text";
					srcText.id = "imageSrc"+imageCount;
					srcText.name = "imageSrc"+imageCount;
					srcText.value = e.target.result;
					srcText.style.position = "absolute";
					srcText.style.display = "none";
					
					var span = document.createElement('span');
					span.innerHTML = '<button type="button" onclick="removeImage('+imageCount+');" >Bild entfernen</button>';
					
					li.appendChild(img);
					li.appendChild(text);
					li.appendChild(srcText);
					li.appendChild(span);
					
					document.getElementById('previews').appendChild(li);
					document.getElementById('imageCount').value = imageCount;
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
		
		function removeImage(id){
			li = document.getElementById("previewNode"+id);
			li.parentNode.removeChild(li);
		}
		
		function createSponsor(){
			sponsorCount++;
			var li = document.createElement('li');
			li.id = "newSponsor"+sponsorCount;
			
			var label = document.createTextNode("Neuer Sponsor");
			
			var sName = document.createElement('input');
			sName.type = "text";
			sName.id = "nameSponsor"+sponsorCount;
			sName.name = "nameSponsor"+sponsorCount;
			sName.maxLength = "30";
			sName.placeholder = "Name";
			
			var sPhone = document.createElement('input');
			sPhone.type = "text";
			sPhone.id = "phoneSponsor"+sponsorCount;
			sPhone.name = "phoneSponsor"+sponsorCount;
			sPhone.maxLength = "30";
			sPhone.placeholder = "Telefon (optional)";
			
			var sMail = document.createElement('input');
			sMail.type = "text";
			sMail.id = "mailSponsor"+sponsorCount;
			sMail.name = "mailSponsor"+sponsorCount;
			sMail.maxLength = "30";
			sMail.placeholder = "E-Mail (optional)";
			
			var sWeb = document.createElement('input');
			sWeb.type = "text";
			sWeb.id = "webSponsor"+sponsorCount;
			sWeb.name = "webSponsor"+sponsorCount;
			sWeb.maxLength = "30";
			sWeb.placeholder = "Website (optional)";
			
			var sDesc = document.createElement('textarea');
			sDesc.id = "descriptionSponsor"+sponsorCount;
			sDesc.name = "descriptionSponsor"+sponsorCount;
			sDesc.placeholder = "Beschreibung";
			
			var sImage = document.createElement('input');
			sImage.type = "file";
			sImage.id = "imageSponsor"+sponsorCount;
			sImage.name = "imageSponsor"+sponsorCount;
			sImage.onchange = function () {
				previewSponsor(this,sponsorCount);
			};
			
			var sPreview = document.createElement('img');
			sPreview.id = "previewSponsor"+sponsorCount;
			sPreview.name = "previewSponsor"+sponsorCount;
			sPreview.src = "#";
			sPreview.alt = "Bild";
			sPreview.title = "Preview";
			sPreview.style.height = "100px";
			
			var span = document.createElement('span');
			span.innerHTML = '<button type="button" onclick="removeSponsor('+sponsorCount+');" >Sponsor entfernen</button>';
			
			li.appendChild(label);
			li.appendChild(sName);
			li.appendChild(sPhone);
			li.appendChild(sMail);
			li.appendChild(sWeb);
			li.appendChild(sDesc);
			li.appendChild(sImage);
			li.appendChild(sPreview);
			li.appendChild(span);
			
			document.getElementById('newSponsors').appendChild(li);
			document.getElementById('sponsorCount').value = sponsorCount;
		}
		
		function removeSponsor(id) {
			li = document.getElementById("newSponsor"+id);
			li.parentNode.removeChild(li);
		}
	</script>
	</head>

	<body onload='init();'>
		<div id= "formDiv">
			<form action="uploadEvent.php" id="event" method="post">
				<uol>
					<li>
						<input type="text" name="eventName" id="eventName" maxLength="30" placeholder = "Eventname">
					</li>
					<br>
					<li>
						<label for="price">Preis</label>
						<input type="number" name="price" id="price" min="0" max="128" step="0.01" value = 0>
						<label> &#x20ac;</label>
					</li>
					<br>
					<li>
						<label for="participants">Maximale Teilnehmerzahl</label>
						<input type="number" name="participants" id="participants" min="1" placeholder = "Keine">
						<input type="checkbox" name="countParticipants" name="countParticipants" checked>
						<label for="countParticipants">Teilnehmer z&#xe4;hlen</label>
					</li>
					<br>
					<li>
						<label>Datum und Zeit von</label>
						<input type="datetime-local" name="dateStart" id="dateStart" max="9999-12-31T23:59:59">
						<label>bis</label>
						<input type="datetime-local" name="dateEnd" id="dateEnd" max="9999-12-31T23:59:59">
					</li>
					<br>
					<li>
						<textarea name="description" id="description" placeholder = "Beschreibung"></textarea>
					</li>
					<br>
					<li>
						<label>Alter von</label>
						<input type="number" name="ageMin" id="ageMin" min="0" max="99" value = 0>
						<label>bis</label>
						<input type="number" name="ageMax" id="ageMax" min="0" max="99" value = 99>
					</li>
					<br>
					<li>
						<label>Position</label>
						<br>
						<input type = "radio" name = "knowLoc" id = "oldLoc" checked>Bekannte Location</input>
						<br>
						<select name = "location" id = "location">
						</select>
						<br>
						<input type = "radio" name = "knowLoc" id = "newLoc">Neue Location</input>
						<br>
						<input type = "text" name = "newAddress" id = "newAddress" max = 30 placeholder = "Addresse"></input>
						<input type = "checkbox" name = "setMarker" id = "setMarker">Marker platzieren</input>
						<br>
						<input type="number" name="latitude" id="latitude" min="-180" max = "180" step="any" value = 51.514>
						<input type="number" name="longitude" id="longitude" min="-180" max = "180" step="any" value = 7.463>
						<button type="button" onClick="applyLonLat();">Zu Position springen</button>
						<button type="button" onClick="setNewMarker();">Marker platzieren</button>
						<div id="Map" style="height:350px ; width:500px"></div>
						<div id="mapHeader">
							&#xa9;<a href="http://www.openstreetmap.org">OpenStreetMap</a>
							und <a href="http://www.openstreetmap.org/copyright">Mitwirkende</a>,
							<a href="http://creativecommons.org/licenses/by-sa/2.0/deed.de">CC-BY-SA</a>
						</div>
					</li>
					<br>
					<li>
						<label>Sponsoren</label>
						<div name = "sponsors" id = "sponsors" multiple>
						</div>
						<input type = "number" id = "sponsorCount" name = "sponsorCount" style = "position : absolute ; display : none ;" value = "0">
						<button type="button" onClick="createSponsor();">Neuer Sponsor</button>
						<oul name = "newSponsors" id = "newSponsors">
						</oul>
					</li>
					<br>
					<li>
						<label> Bilder </label>
						<input type = "number" id = "imageCount" name = "imageCount" style = "position : absolute ; display : none ;" value = "0">
						<br>
						<input onchange="previewImage(this);" type="file" />
						<oul id="previews"></oul>
					</li>
					<br>
					<li>
						<input type="submit" value="Event erstellen" class="btnSubmit" />
					</li>
				</uol>
			</form>
		</div>
	</body>
</html>
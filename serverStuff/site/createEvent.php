<?php
	session_start();
	
	if(!isset($_SESSION['hostId'])){
		exit('Error: Must be logged in to create an Event');
	}
	
	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	$statement = $pdo->prepare("SELECT * FROM `locations`");
	$statement->execute();
	
	$lIdArr = array();
	$adArr = array();
	$loArr = array();
	$laArr = array();
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
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
	
	$statement = $pdo->prepare("SELECT * FROM `sponsors`");
	$statement->execute();
	
	$sIdArr = array();
	$imArr = array();
	$naArr = array();
	$sMax = 0;
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$sIdArr[] = $row['id'];
		$imArr[] = $row['imagePath'];
		$naArr[] = $row['name'];
		if($row['id'] > $sMax){
			$sMax = $row['id'];
		}
	}
	
	$sponsorIds = implode("\",\"",$sIdArr);
	$sponsorImgs = implode("\",\"",$imArr);
	$sponsors = implode("\",\"",$naArr);
	
	$sponsorIds = "const sponsorIds = Array(\"".$sponsorIds."\");";
	$sponsorImgs = "const sponsorImgs = Array(\"".$sponsorImgs."\");";
	$sponsors = "const sponsors = Array(\"".$sponsors."\");";
	$maxSponsorId = "const maxSponsorId = ".$sMax.";";
	
	$hostStuff = "const hostName = \"".$_SESSION['name']."\"; const hostImage = \"".$_SESSION['image']."\";";
	
	echo "
		<script type=\"text/javascript\">
			$addressIds
			$addresses
			$longitudes
			$latitudes
			$sponsorIds
			$sponsorImgs
			$sponsors
			$maxSponsorId
			$hostStuff
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
				var li = document.createElement('li');
				li.class = "sponsorNode";
				
				var checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.name = "sponsor "+sponsorIds[i];
				checkbox.value = sponsorIds[i];
				checkbox.id = "sponsor "+sponsorIds[i];
				checkbox.class = "sponsorCheckbox";
				
				var img=document.createElement('img');
				img.src= sponsorImgs[i];
				img.alt= "Bild nicht verfügbar";
				img.title= "Vorschau";
				img.style.height="50px";
				img.htmlFor = "sponsor"+sponsorIds[i];
				img.class = "sponsorImage";
				
				var label = document.createElement('label');
				label.htmlFor = "sponsor "+sponsorIds[i];
				label.appendChild(document.createTextNode(sponsors[i]));
				label.class = "sponsorName";
				
				li.appendChild(checkbox);
				li.appendChild(img);
				li.appendChild(label);
				
				sponsorsSelect.appendChild(li);
			}
			
			var nameLabel = document.createTextNode(hostName);
			var image = document.createElement('img');
			image.src= hostImage;
			image.alt= "Bild nicht verfügbar";
			image.title= "Vorschau";
			image.style.height="50px";
			
			div = document.getElementById('hostStuff');
			div.appendChild(nameLabel);
			div.appendChild(image);
			
			document.getElementById('maxSponsorId').value = maxSponsorId;
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
						newLocMarker = placeMarker(lonlat1,'I am Error',0);
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
			li.class = "imageNode"
			
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
			iDesc.placeholder = "Beschreibung";
			iDesc.class = "imageDesc";
			
			var span2 = document.createElement('span');
			span2.innerHTML = '<button type="button" onclick="removeImage('+imageCount+');" >Bild entfernen</button>';
			span2.class = "imageRemove";
			
			li.appendChild(span1);
			li.appendChild(iPreview);
			li.appendChild(iDesc);
			li.appendChild(span2);
			
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
		<div id= "header">
				<form action="logout.php">
					<div id = "hostStuff"></div>
					<input type = "submit" value = "Abmelden">
				</form>
		</div>
		<div id= "formDiv">
			<form action="uploadEvent.php" enctype="multipart/form-data" id="event" method="post">
				<uol style="list-style-type: none;">
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
						<input type="checkbox" name="countParticipants" id="countParticipants" checked>
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
						<input type="number" name="latitude" id="latitude" min="-180" max = "180" step="any" value = 51.514>
						<input type="number" name="longitude" id="longitude" min="-180" max = "180" step="any" value = 7.463>
						<button type="button" onClick="applyLonLat();">Zu Position springen</button>
						<button type="button" onClick="setNewMarker();">Marker platzieren</button>
						<div id="Map" style="height:500px ; width:1000px"></div>
						<div id="mapHeader">
							&#xa9;<a href="http://www.openstreetmap.org">OpenStreetMap</a>
							und <a href="http://www.openstreetmap.org/copyright">Mitwirkende</a>,
							<a href="http://creativecommons.org/licenses/by-sa/2.0/deed.de">CC-BY-SA</a>
						</div>
					</li>
					<br>
					<li>
						<label>Sponsoren</label>
						<input type = "number" id = "maxSponsorId" name = "maxSponsorId" style = "position : absolute ; display : none ;" value = "0">
						<oul name = "sponsors" id = "sponsors" multiple>
						</oul>
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
						<button type="button" onClick="createImage();">Neues Bild</button>
						<br>
						<label>Das erste Bild wird als Vorschau-Bild des Events angezeigt</label>
						<oul name = "images" id="images">
						</oul>
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
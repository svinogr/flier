var geocoder;
var marker;
var map;
var inputAddress;
var lat;
var lng;
var autocomplete;

function initMap() {
    inputAddress = document.getElementById("address");
    geocoder = new google.maps.Geocoder();
    autocomplete = new google.maps.places.Autocomplete(inputAddress);

    map = new google.maps.Map(document.getElementById("map"), {
        zoom: 16,
        center: {lat: -33, lng: 151},
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
            mapTypeIds: ["roadmap", "terrain"],
        },
    })

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude,
                };
                map.setCenter(pos);
            },
            () => {

            }
        );
    } else {
        alert("erro")
    }

    google.maps.event.addListener(map, 'click', function (event) {

        if (marker != null) {
            marker.setMap(null);
        }

        geocoder.geocode({
            'latLng': event.latLng
        }, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                if (results[0]) {
                    map.setCenter(results[0].geometry.location);
                    marker = new google.maps.Marker({
                        position: results[0].geometry.location,
                        map: map
                    });
                    inputAddress.value = results[0].formatted_address;
                    lng = results[0].geometry.location.lng();
                    lat = results[0].geometry.location.lat();
                }
            }
        });
    });
}

function setAddress() {
    let address = document.getElementById("address");
    let latInput = document.getElementById("lat");
    let lngInput = document.getElementById("lng");

    address.value = inputAddress.value;
    latInput.value = lat;
    lngInput.value = lng;
}

function myautocomplete() {
    alert("autocomplete");
    var searchAddress = document.getElementById("searchAddress");
    var autocomplete = new google.map.places.Autocomplete(searchAddress);

}
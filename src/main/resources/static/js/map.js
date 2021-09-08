function addressAutocomplete() {
    var searchAddress = document.getElementById("address");
    var autocomplete = new google.maps.places.Autocomplete(searchAddress, {

        fields: ["address_components", "geometry"],
        types: ["address"],
    });
    autocomplete.addListener("place_changed", function () {
        const place = autocomplete.getPlace();
        var lat = place.geometry.location.lat();
        var lng = place.geometry.location.lng();

        let latInput = document.getElementById("lat");
        let lngInput = document.getElementById("lng");

        latInput.value = lat
        lngInput.value = lng;
    });
}

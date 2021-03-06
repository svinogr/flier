function search(id) {
    var value = $("#searchValue").prop("value");
    var type;
    var go = false;
    var url = document.URL;

    if ($('#searchId').is(':checked')) {
        type = "searchId";
        go = true;
    }
    if ($('#searchTitle').is(':checked')) {
        type = "searchTitle";
        go = true;
    }

    if ($('#searchAddress').is(':checked')) {
        type = "searchAddress";
        go = true;
    }

    if (go) {
        if (url.includes("?")) {
            url = getUrl(url, type, value);
            window.location.href = url;
        } else {
            window.location.href = url + "/searchstocks?type=" + type + "&value=" + value;
        }
    }

    function replaceQueryParam(param, newval, search) {
        var regex = new RegExp("([?;&])" + param + "[^&;]*[;&]?");
        var query = search.replace(regex, "$1").replace(/&$/, '');

        return (query.length > 2 ? query + "&" : "?") + (newval ? param + "=" + newval : '');
    }

    function getUrl(url, type, value) {
        url = replaceQueryParam("type", type, url);
        return url = replaceQueryParam("value", value, url);
    }
}
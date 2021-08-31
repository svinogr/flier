function pageClick(id) {
        var url = document.URL;
        var newUrl;

        if (url.includes("?")) {
            if (url.includes("page")) {
                newUrl = replaceQueryParam("page", id, url)
            } else {
                newUrl = url + "&page=" + id;
            }
        } else {
            newUrl = url + "?page=" + id;
        }
        window.location.href = newUrl;
    }

    function replaceQueryParam(param, newval, search) {
        var regex = new RegExp("([?;&])" + param + "[^&;]*[;&]?");
        var query = search.replace(regex, "$1").replace(/&$/, '');

        return (query.length > 2 ? query + "&" : "?") + (newval ? param + "=" + newval : '');
    }

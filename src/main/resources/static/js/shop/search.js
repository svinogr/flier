function search(id) {
         var value = $("#searchValue").prop("value");
      var type;
      var go = false;

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
          window.location.href = "/account/accountpage/"+ id+ "/searchshops?type=" + type + "&value=" + value;
      }
}
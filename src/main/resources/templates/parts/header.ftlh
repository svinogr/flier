<#macro page>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>WebInterface</title>
    <link rel="stylesheet" href="webjars/bootstrap/5.0.1/css/bootstrap.min.css" type="text/css">

    <script src="webjars/jquery/3.6.0/jquery.min.js" type="text/javascript"></script>
    <script src="webjars/popper.js/2.9.2/umd/popper.min.js" type="text/javascript"></script>
    <script src="webjars/bootstrap/5.0.1/js/bootstrap.min.js"  type="text/javascript" ></script>
  <#--  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"
            integrity="sha384-SR1sx49pcuLnqZUnnPwx6FCym0wLsk5JZuNx2bPPENzswTNFaQU1RDvt3wT4gWFG"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.min.js"
            integrity="sha384-j0CNLUeiqtyaRmlzUHCPZ+Gy5fQu0dQ6eZ/xAww941Ai1SxSY+0EQqNXNE6DZiVc"
            crossorigin="anonymous"></script>-->
</head>
<body>
<div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">WebInterface</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <a class="btn btn-outline-success" href="/" role="button">Пользователи</a>
                <a class="btn btn-outline-success" href="/" role="button">Серверы</a>
                <a class="btn btn-outline-success" href="/" role="button">Акаунт</a>
                <a class="btn btn-outline-success" href="#" role="button">Что нить еще</a>
            </div>
            <div>
                <button class="btn btn-outline-success" id="logout">Выйти</button>
            </div>
        </div>
    </nav>
</div>
    <#nested>

</html>
</#macro>
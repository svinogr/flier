<#macro login url>
    <div class="container mt-5">
        <div class="row justify-content-center ">
            <form class="center" action="${url}" method="post">
                <div class="form-group ">
                    <label>Логин</label>
                    <input type="text" class="form-control" name="username"/>
                </div>

                <div class="form-group">
                    <label>Пароль</label>
                    <input class="form-control " type="password" name="password"/>
                </div>
                <input type="hidden"   name="_csrf" value="${_csrf.token}">
                <input class="btn btn-outline-primary form-control mt-4" type="submit" value="Войти"/>
            </form>
        </div>
    </div>

    <#--
    <div class="container">
        <div class="row justify-content-md-center mt-4 mb-3">
            <div class="col-6 ">
                <form class="" id="login_form" action="${url}" method="post">
                    <input class="form-control" type="text" placeholder="email" name="email">
                    <input class="form-control mt-3" type="text" placeholder="password" name="password">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                </form>
            </div>
        </div>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <button class="btn btn-outline-success login form-control" id="login">Войти</button>
            </div>
            <div class="col-3">
                <a class="btn btn-outline-success form-control" href="newuserpage" role="button">Новый пользователь</a>
            </div>

        </div>
    </div>-->
</#macro>




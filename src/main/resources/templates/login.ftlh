<#import "parts/header.ftlh" as h>

<@h.page>
    <div class="container">

        <div class="row justify-content-md-center mt-4 mb-3">
            <div class="col-6 ">
                <form class="" id="login_form" action="" method="post">
                    <input class="form-control" type="text" placeholder="email" name="email">
                    <input class="form-control mt-3" type="text" placeholder="password" name="password">

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
    </div>

</@h.page>





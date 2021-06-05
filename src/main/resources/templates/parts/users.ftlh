
<#macro page>
    <div class="container mt-4">
        <div class="row">
            <div class="col-9">
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Пользователь</th>
                        <th scope="col">Email</th>
                    </tr>
                    </thead>
                    <tbody>
                  ${user.content}
                   <#-- <#list user.content as user >

                    </#list>
                   --><#-- <#list all as user>-->
                    <tr  class="clickable-row">
                        <th scope="row"><#--${user.getId}--></th>
                        <td></td>
                        <td></td>

                    </tr>
<#--
                    </#list>
-->
                    </tbody>
                </table>

            </div>
            <div class="col-3">
                <a class="btn btn-outline-success form-control" href="newuserpage" role="button">Добавить
                    пользователя</a>
            </div>
        </div>
    </div>

</#macro>
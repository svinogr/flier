function preview() {
    var input = event.target;

    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#img').attr('src', e.target.result);
        }
        var typeImg = $("#imgTypeAction");
        typeImg.attr('value', 1);
        reader.readAsDataURL(input.files[0]);

        $("#deleteImg").removeAttr('hidden')
    }
}

function deleteImgPreview(srcImage) {
    $("#img").attr('src', src = srcImage);
    var typeImg = $("#imgTypeAction");
    typeImg.attr('value', -1);
    $("#deleteImg").attr('hidden', 'hidden')
}
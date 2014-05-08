'use strict';

var index = {};
(function (target, $, window) {

    var template = window.template;

    target.getAlbum = function (albumId) {
        var albumJsonStr = MainJS.getAlbum(albumId);
        var albumJson = JSON.parse(albumJsonStr);
        $('#albumId')[0].value = albumJson;
        var viewDom = template.render('template_album_detail', albumJson);
        $(".topcoat-list__container").html(viewDom);
    };

    $(document).on('click', function (e) {
        var eTarget = e.target;
        if (eTarget.classList.contains('J_searchAlbum')) {
            target.getAlbum($('#albumId')[0].value);
            return false;
        }
    });
})(index, Zepto, window);


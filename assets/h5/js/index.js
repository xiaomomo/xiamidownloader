'use strict';

var index = {};
(function (target, $, window) {

    var template = window.template;
    target.getAlbum = function (albumId) {
        var albumJsonStr = MainJS.getAlbum(albumId);
        var albumJson = JSON.parse(albumJsonStr);
//        var albumJson = {"albumList": ["特别的爱给特别的你 ", "Love Warrior ", "流浪记 ", "寂寞让我如此美丽 ", "最初的信仰 ", "Someone Like You ", "梨花又开放 ", "馋 ", "我 ", "我就是这样的 "]};
        var viewDom = template.render('template_album_detail', albumJson);
        $(".topcoat-list__container").html(viewDom);
        $(".download_button")[0].style.display = 'inline';
    };
    target.downloadSong = function () {
        //获取下载的歌曲名称，调用java
        var songListStr = "";
        $('.J_song_checkbox').forEach(function (checkbox) {
            if (checkbox.checked && checkbox.value != "on") {
                songListStr += "," + checkbox.value;
            }
        });
        MainJS.downloadSong(songListStr);
    }

    $(document).on('click', function (e) {
        var eTarget = e.target;
        if (eTarget.classList.contains('J_searchAlbum')) {
            target.getAlbum($('#albumId')[0].value);
            return false;
        }
        if (eTarget.classList.contains('J_checkbox_li')) {
            eTarget.children[0].children[0].checked = true;
            return false;
        }
        if (eTarget.classList.contains('J_checkbox_li_all')) {
            $('.J_song_checkbox').forEach(function (checkbox) {
                checkbox.checked = true;
            });
            return false;
        }
        if (eTarget.classList.contains('J_downloadSong')) {
            target.downloadSong();
            return false;
        }
    });

})(index, Zepto, window);


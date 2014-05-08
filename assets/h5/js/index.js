'use strict';

var index = {};
(function (target, $, window) {

    var template = window.template;
    target.getAlbum = function (albumId) {
        var albumJsonStr = MainJS.getAlbum(albumId);
        var albumJson = JSON.parse(albumJsonStr);
//        var albumJson = {"albumList": ["�ر�İ����ر���� ", "Love Warrior ", "���˼� ", "��į����������� ", "��������� ", "Someone Like You ", "�滨�ֿ��� ", "�� ", "�� ", "�Ҿ��������� "]};
        var viewDom = template.render('template_album_detail', albumJson);
        $(".topcoat-list__container").html(viewDom);
        $(".download_button")[0].style.display = 'inline';
    };
    target.downloadSong = function () {
        //��ȡ���صĸ������ƣ�����java
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


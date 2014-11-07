(function ($) {

    var _onchange = function (event) {
        var $ref = $("#" + event.data.ref);
        var $sRef = event.data.sRef;
        if ($ref.size() == 0) return false;
        if ($sRef.val() == "") return false;
        $.ajax({
            type: 'POST', dataType: "json", url: event.data.refUrl, cache: false,
            data: { id: $sRef.val()},
            success: function (json) {
                if (!json) return;
                var html = '';

                $.each(json, function (i) {
                    html += '<option value="' + json[i].code + '">' + json[i].name + '</option>';
                });
                $ref.html(html);
                $ref.trigger("change").combox();
            },
            error: function () {
                alert("error");
            }
        });
    };

    var _loadSelectData = function (target, url) {

        $.ajax({
            type: 'POST', dataType: "json", url: url, cache: false,
            data: {},
            success: function (json) {
                if (!json) return;
                var html = '';

                $.each(json, function (i) {
                    html += '<option value="' + json[i].code + '">' + json[i].name + '</option>';
                });

                target.html(html)
            },
            error: function () {
                alert("error");
            }
        });
    }

    $.extend($.fn, {
        combox: function () {
            return this.each(function (i) {
                var $this = $(this).removeClass("combox");
                var url = $this.attr("ajaxUrl");
                var ref = $this.attr("ref");
                var refUrl = $this.attr("refUrl") || "";

                if (url) {
                    _loadSelectData($this, url);
                }

                if (ref && refUrl) {
                    $this.unbind("change", _onchange).bind("change", {ref: ref, refUrl: refUrl, sRef: $this}, _onchange);
                }
            });
        }
    });
})(jQuery);

/*!
 * Project Retail
 * Author wu.h1 (RTX 3832)
 * Email wu.han@wonhigh.cn
 * Date 2015/06/03
 * Description EasyUI for retail v2.0.0 (http://www.wonhigh.cn/)
 */
var __CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
};

var bootPATH = __CreateJSPath("boot.js");
var modulesDomain = bootPATH.replace('/s.','/retail.');

document.write('<link href="' + bootPATH + 'assets/css/ui.min.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'assets/css/base.min.css" rel="stylesheet" type="text/css" />');
document.write('<script src="' + bootPATH + 'assets/js/libs/jquery.min.js" type="text/javascript"></script>');
document.write('<script src="' + bootPATH + 'assets/js/libs/jquery.extend.min.js" type="text/javascript"></script>');
document.write('<script src="' + bootPATH + 'assets/js/ui.min.js" type="text/javascript"></script>');
document.write('<script src="' + bootPATH + 'assets/js/base.min.js" type="text/javascript"></script>');

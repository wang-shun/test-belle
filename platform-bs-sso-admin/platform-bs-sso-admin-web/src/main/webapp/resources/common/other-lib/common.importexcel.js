(function($) {
	var defaults = {
		submitUrl : '',
		downloadUrl : '',
		params : {},
		success : function() {
		}
	};

	var dialogObj;
	var importExcel = {
		open : function(o) {
			var hrefV = "";
			if (o.params.meetorderFlag) {
				hrefV = BasePath + "/iframe?meetorderFlag="
						+ o.params.meetorderFlag;
			} else {
				hrefV = BasePath + "/iframe";
			}
			var $uploadForm = null;
			ygDialog({
				id : 'exportIframe',
				isFrame : true,
				title : 'Excel导入',
				modal : true,
				showMask : false,
				width : '400',
				height : '160',
				href : hrefV,
				buttons : [ {
					text : '上传',
					iconCls : 'icon-upload',
					handler : function(self) {
						dialogObj = self;
						var doc = $("#exportIframe iframe")[0]; // .upload_();
						var win = doc.contentWindow || doc.document.window;
						var func = win.upload;
						func();
					}
				}, {
					text : '下载模板',
					iconCls : 'icon-download',
					handler : function() {
						window.location.href = o.downloadUrl;
					}
				} ],
				onLoad : function(win, dialog) {
					$uploadForm = dialog.$('#fileForm');
					$uploadForm.attr('action', o.submitUrl);
				}
			});
		},
		importSuccess : function(o) {
			o.success.call();
		}
	};

	$.importExcel = function(options) {
		$.fn.importExcel.open(options);
	};
	var tmpOptions = {};
	$.importExcel.open = function(options) {
		var opts = $.extend({}, defaults, options);
		tmpOptions = opts;
		importExcel.open(opts);
	};
	$.importExcel.importSuccess = function() {
		importExcel.importSuccess(tmpOptions);
	};
	$.importExcel.close = function() {
		dialogObj.close();
		/*if (null != msg && "" != msg) {
		  alert(msg, 1);
		}*/
	};

})(jQuery);
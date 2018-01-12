(function($) {
	var defaults = {
		submitUrl : '',
		params : {},
		success : function() {
		}
	};

	var dialogObj;
	var uploadFujian = {
		open : function(o) {
			var $uploadForm = null;
			ygDialog({
				isFrame : true,
				title : '附件上传',
				modal : true,
				showMask : false,
				width : '400',
				height : '150',
				href : BasePath + "/fujian_iframe",
				buttons : [ {
					text : '上传',
					iconCls : 'icon-upload',
					handler : function(self) {
						$("iframe")[0].contentWindow.upload_();
						dialogObj = self;
					}
				} ],
				onLoad : function(win, dialog) {
					$uploadForm = dialog.$('#uploadFormFujian');
					$uploadForm.attr('action', o.submitUrl);
				}
			});
		},
		importSuccess : function(o) {
			o.success.call();
		}
	};

	$.uploadFujian = function(options) {
		$.fn.uploadFujian.open(options);
	};
	var tmpOptions = {};
	$.uploadFujian.open = function(options) {
		var opts = $.extend({}, defaults, options);
		tmpOptions = opts;
		uploadFujian.open(opts);
	};
	$.uploadFujian.importSuccess = function(src, fileName) {
		showFujian(src, fileName);// 展示附件
		uploadFujian.importSuccess(tmpOptions);
	};
	$.uploadFujian.close = function(msg) {
		dialogObj.close();
		if (null != msg && "" != msg) {
			alert(msg, 1);
		}
	};

})(jQuery);
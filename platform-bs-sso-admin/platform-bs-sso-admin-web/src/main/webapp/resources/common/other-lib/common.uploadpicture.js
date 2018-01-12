(function($) {
	var defaults = {
		submitUrl : '',
		params : {},
		success : function() {
		}
	};

	var dialogObj;
	var uploadPicture = {
		open : function(o) {
			var $uploadForm = null;
			ygDialog({
				isFrame : true,
				title : '图片上传',
				modal : true,
				showMask : false,
				width : '400',
				height : '150',
				href : BasePath + "/picture_iframe",
				buttons : [ {
					text : '上传',
					iconCls : 'icon-upload',
					handler : function(self) {
						$("iframe")[0].contentWindow.upload_();
						dialogObj = self;
					}
				} ],
				onLoad : function(win, dialog) {
					$uploadForm = dialog.$('#uploadForm');
					$uploadForm.attr('action', o.submitUrl);
				}
			});
		},
		importSuccess : function(o) {
			o.success.call();
		}
	};

	$.uploadPicture = function(options) {
		$.fn.uploadPicture.open(options);
	};
	var tmpOptions = {};
	$.uploadPicture.open = function(options) {
		var opts = $.extend({}, defaults, options);
		tmpOptions = opts;
		uploadPicture.open(opts);
	};
	$.uploadPicture.importSuccess = function(src) {
		showImage(src);// 展示图片
		uploadPicture.importSuccess(tmpOptions);
	};
	$.uploadPicture.close = function(msg) {
		dialogObj.close();
		if (null != msg && "" != msg) {
			alert(msg, 1);
		}
	};

})(jQuery);
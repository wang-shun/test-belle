(function($) {
	$.fn.importExcel = function(options) {
		importExcel.init();
		var opts = $.extend({}, $.fn.importExcel.defaults, options);
		$(this).live('click', function() {
			importExcel.open(opts);
		});
	};

	$.fn.importExcel.defaults = {
		submitUrl : '',
		templateName : '',
		params : {},
		success : function(file, data, response) {
		}
	};

	var importExcel = {
		init : function(options) {
			if ($('#importExcel_div').length < 1) {
				$(document.body)
						.after(
								'<div id="importExcel_div" class="easyui-window" title="Excel导入" style="overflow:hidden;">'
										+ '<input type="file" name="uploadify" id="uploadify" />'
										+ '<a href="javascript:;" id="importexcel_upload">上传</a>| '
										+ '<a href="javascript:;" id="importexcel_cancel">取消上传</a>|'
										+ '<a href="javascript:;" id="importexcel_download">模板下载</a>'
										+ '</div>');
				var $this = importExcel;
				$('#importexcel_upload').live('click', function() {
					$this.upload();
				});
				$('#importexcel_download').live('click', function() {
					$this.download(options);
				});
				$('#importexcel_cancel').live('click', function() {
					$this.cancel();
				});
			}
		},
		upload : function() {
			$('#uploadify').uploadify("upload");
		},
		cancel : function() {
			$('#uploadify').uploadify("cancel");
		},
		open : function(o) {
			$('#importExcel_div')
					.window(
							{
								title : 'Excel导入',
								width : 300,
								height : 200,
								closed : false,
								cache : false,
								collapsible : false,
								minimizable : false,
								maximizable : false,
								resizable : false,
								shadow : false,
								modal : true,
								onOpen : function() {
									$("#uploadify")
											.uploadify(
													{
														// 指定swf文件
														'swf' : bootPATH
																+ 'resources/assets/js/libs/uploadify/uploadify.swf',
														// 后台处理的页面
														'uploader' : o.submitUrl,
														// 按钮显示的文字
														'buttonText' : '请选择Excel',
														// 显示的高度和宽度，默认 height
														// 30；width 120
														'height' : 15,
														'width' : 80,
														// 上传文件的类型 默认为所有文件 'All
														// Files' ; '*.*'
														// 在浏览窗口底部的文件类型下拉菜单中显示的文本
														'fileDesc' : 'Excel Files',
														'uploadLimit' : 1,
														// 允许上传的文件后缀
														'fileTypeExts' : '*.xls; *.xlsx',
														// 发送给后台的其他参数通过formData指定
														'formData' : o.params,
														// 上传文件页面中，你想要用来作为文件队列的元素的id,
														// 默认为false 自动生成, 不带#
														// 'queueID':
														// 'fileQueue',
														// 选择文件后自动上传
														'auto' : false,
														// 设置为true将允许多文件上传
														'multi' : false,
														// 检测FLASH失败调用
														'onFallback' : function() {
															alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
														},
														// 上传到服务器，服务器返回相应信息到data里
														'onUploadSuccess' : function(
																file, data,
																response) {
															$.importExcel.close();
															o.success.call(
																	file, data,
																	response);
														}
													});
								}
							});

		},
		close : function() {
			$('#importExcel_div').window('close');
		},
		download : function(o) {
			window.location.href=BasePath + '/download?fileName='+ o.templateName;
		}
	};

	$.importExcel = function(options) {
		$.fn.importExcel.open(options);
	};

	$.importExcel.open = function(options) {
		$.fn.importExcel.open(options);
	};

	$.importExcel.close = function() {
		importExcel.close();
	};

	$.fn.importExcel.open = function(options) {
		importExcel.init(options);
		var opts = $.extend({}, $.fn.importExcel.defaults, options);
		importExcel.open(opts);
	};

	$.fn.importExcel.close = function() {
		importExcel.close();
	};
})(jQuery);
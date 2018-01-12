var commonValidRule = {};

/**
 * 验证是否是中文
 */
commonValidRule.isChineseChar = function(str) {
	var reg = /^[\u4e00-\u9fa5]+$/gi;
	if (reg.test(str)) {
		return true;
	}
	return false;
};

/**
 * 不能包含中文验证
 */
commonValidRule.vnChinese = {
	vnChinese : {
		validator : function(value, param) {
			for ( var i = 0; i < value.length; i++) {
				if (commonValidRule.isChineseChar(value[i])) {
					return false;
				}
			}
			return true;
		},
		message : '{0}'
	}
};

/**
 * 长度验证
 */
commonValidRule.vLength = {
	vLength : {
		validator : function(value, param) {
			var chineseCharLength = param[3] || 1;
			var tempLength = 0;
			for ( var i = 0; i < value.length; i++) {
				if (commonValidRule.isChineseChar(value[i])) {
					tempLength += chineseCharLength;
				} else {
					tempLength += 1;
				}
			}
			if (tempLength < param[0] || tempLength > param[1]) {
				return false;
			}
			return true;
		},
		message : '{2}'
	}
};

$(document).ready(function() {
	$.extend($.fn.validatebox.defaults.rules, commonValidRule.vnChinese);
	$.extend($.fn.validatebox.defaults.rules, commonValidRule.vLength);
});

parseParam = function(param) {
	var paramStr = "";
	{
		$.each(param, function(i) {
			paramStr += '&' + i + '=' + param[i];
		});
	}
	return paramStr.substr(1);
};

/**
 * 基础资料的导出
 * 
 * @param dataGridId
 *            导出数据的表格ID
 * @param exportUrl
 *            导出数据的URL 基础资料一般都是 /模块名/do_export.htm *如机构:/store/do_export.htm
 * @param excelTitle
 *            excel文件名
 */
function exportExcelBaseInfo(dataGridId, exportUrl, excelTitle) {
	var $dg = $("#" + dataGridId + "");
	// 获取pageNo,pageSize
	var ops = $dg.datagrid('getPager').data("pagination").options;
	var pageNo = ops.pageNumber;
	var pageSize = ops.pageSize;
	var params = $dg.datagrid('options').queryParams;
	params.rows = pageSize;
	params.page = pageNo;
	var columns = $dg.datagrid('options').columns[0];

	var exportColumns = $.map(columns, function(c) {
		if (!c.checkbox && !c.hidden) {
			return c;
		}
	});

	var exportColumns = JSON.stringify(exportColumns);

	// var queryParam=parseParam(params);

	var url = BasePath + exportUrl;

	// if(exportUrl.indexOf('?')>0){
	// url=BasePath+exportUrl+'&'+queryParam;
	// }else{
	// url=BasePath+exportUrl+'?'+queryParam;
	// }

	var dataRow = $dg.datagrid('getRows');

	$("#exportExcelForm").remove();

	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	;

	var fromObj = $('#exportExcelForm');
	if (dataRow.length > 0) {
		fromObj.form('submit', {
			url : url,
			onSubmit : function(param) {
				param.exportColumns = exportColumns;
				param.fileName = excelTitle;

				if (params != null && params != {}) {
					$.each(params, function(i) {
						param[i] = params[i];
					});
				}

			},
			success : function(data) {
				if(data){
					showWarn(data);
				}
			}
		});
	} else {
		alert('查询记录为空，不能导出!', 1);
	}

}
commonValidRule.preExportColumn = [ {
	title : "序号",
	field : "index",
	width : 150
}, {
	title : "货号",
	field : "tempItemNo",
	width : 150
}, {
	title : "色-尺",
	field : "colorNo",
	width : 150
}, {
	title : "原厂号",
	field : "styleNo",
	width : 150
}, {
	title : "名称",
	field : "itemLname",
	width : 200
}, {
	title : "厂商",
	field : "supplierName",
	width : 150
}, {
	title : "楦型",
	field : "physique",
	width : 100
}, {
	title : "大类",
	field : "cateNo",
	width : 150
}, {
	title : "性别",
	field : "gender",
	width : 200
}, {
	title : "年份",
	field : "years",
	width : 150
}, {
	title : "风格",
	field : "style",
	width : 100
}, {
	title : "跟型",
	field : "heeltype",
	width : 150
}, {
	title : "底型",
	field : "bottomtype",
	width : 200
}, {
	title : "选",
	field : "check",
	width : 200
}, {
	title : "小计",
	field : "sum",
	width : 200
} ];
/**
 * 多仓订货导出
 * 
 * @param exportUrl
 *            导出数据的URL 基础资料一般都是 /模块名/do_export.htm *如机构:/store/do_export.htm
 * @param excelTitle
 *            excel文件名
 */
function exportExcelBillMore(billNo, meetorderNo, itemListNo, exportUrl,
		excelTitle) {
	var exportColumns = commonValidRule.preExportColumn.concat();
	// 动态获取仓库表头
	exportColumns = getStoreColumn(billNo, meetorderNo, itemListNo,
			exportColumns);
	var lastColumn = {
		field : "Nb7",
		title : "Nb7",
		width : 60,
		boxWidth : 60
	};
	exportColumns.push(lastColumn);
	exportColumns = JSON.stringify(exportColumns);
	var url = BasePath + exportUrl;
	$("#exportExcelForm").remove();
	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	var fromObj = $('#exportExcelForm');
	fromObj.form('submit', {
		url : url,
		onSubmit : function(param) {
			param.exportColumns = exportColumns;
			param.fileName = excelTitle;
		},
		// 前后台之间传输的是字符串形式，需要用eval函数转化
		success : function(data) {
			data = eval("(" + data + ")");
			showWarn(data.msg);
		}
	});
};
function getStoreColumn(billNo, meetorderNo, itemListNo, exportColumns) {
	var url = BasePath + '/bill_order_morestore/getStoreName.json?billNo='
			+ billNo + '&meetorderNo=' + meetorderNo + '&itemListNo='
			+ itemListNo;
	var storeStr = "";
	$.ajax({
		type : 'POST',
		url : url,
		data : null,
		async : false,
		cache : true,
		success : function(data) {
			storeStr = data.returnStoreInfo;
		}
	});
	var storesAry = storeStr.split(",");
	for ( var i = 0; i < storesAry.length; i++) {
		var tempObject = {
			field : "",
			title : "",
			width : 100,
			hidden : true,
			boxWidth : 102
		};
		tempObject.field = storesAry[i];
		tempObject.title = storesAry[i];
		exportColumns.push(tempObject);
	}
	return exportColumns;
};
/**
 * http方法静态变量
 */
var HttpMethod = {
	LIST_JSON : "list.json",// 获取列表
	GET_COUNT : "get_count.json",// 获取数量
	GET : "get",// 获取
	GET2 : "get2",// 获取拓展信息
	GET_BIZ : "get_biz",// list
	POST : "post",// 增加
	POST2 : "post2",// 增加2
	PUT : "put",// 更新
	DELETE : "delete",// 删
	DELETEALL : "deleteAll",// 删除单据、单据明细
	SAVE : "save",// 增删改
	DO_EXPORT : "do_export.htm",// 导出excel
	VERIFY : "verify",
	VERIFY_ORDER : "addBillOrder",
	VERIFY_BILL : "verifyBill",
	PRODUCT_JSON : "query_product.json",
	DEPOT_JSON : "query_depot.json",
	TOTAL_JSON : "query_total.json",
	IMPORT : "import",
	SUBMIT : "submit",
	ANTISUBMIT : "antiSubmit"
};

// 重新加载数据到datagrid
function loadDataToGrid(dataGridId, url, queryParams, callback) {
	var $dg = $('#' + dataGridId);
	$dg.datagrid('options').queryParams = queryParams;
	$dg.datagrid('options').url = url;
	if (callback) {
		$dg.datagrid({
			onLoadSuccess : callback
		});
	}
	$dg.datagrid('load');
};

/**
 * 订单功能的导出
 * 
 * @param dataGridId
 *            表格ID
 * @param sysNo
 *            品牌库的ID
 * @param preColNames
 *            前面显示业务列 公用查询动态生成的参数
 * @param endColNames
 *            后面显示的业务列
 * @param sizeTypeFiledName
 * @param excelTitle
 *            excel文件名
 */
function exportExcelBill(dataGridId, sysNo, preColNames, endColNames,
		sizeTypeFiledName, excelTitle) {

	var url = BasePath + '/initCache/do_export.htm';

	var $dg = $("#" + dataGridId + "");

	$("#exportExcelForm").remove();

	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	;

	var fromObj = $('#exportExcelForm');

	var dataRow = $dg.datagrid('getRows');

	if (dataRow.length > 0) {
		fromObj.form('submit', {
			url : url,
			onSubmit : function(param) {

				param.sysNo = sysNo;
				param.preColNames = JSON.stringify(preColNames);
				param.endColNames = JSON.stringify(endColNames);
				param.sizeTypeFiledName = sizeTypeFiledName;
				param.fileName = excelTitle;
				param.dataRow = JSON.stringify(dataRow)
			},
			success : function() {

			}
		});
	} else {
		alert('数据为空，不能导出!', 1);
	}

}
/**
 * 下单下单公用方法
 * 
 * @param dataGridId
 * @param rowIndex
 * @param type
 *            1--上单 2--下单
 * @param callBack
 *            回调函数名
 */
function preBill(dataGridId, rowIndex, type, callBack) {
	var $dg = $("#" + dataGridId + "");
	var curRowIndex = rowIndex;

	var options = $dg.datagrid('getPager').data("pagination").options;
	var currPage = options.pageNumber;
	var total = options.total;
	var max = Math.ceil(total / options.pageSize);
	var lastIndex = Math.ceil(total % options.pageSize);
	var pageSize = options.pageSize;
	var rowData = [];
	if (type == 1) {
		if (curRowIndex != 0) {
			curRowIndex = curRowIndex - 1;
			$dg.datagrid('unselectAll');
			$dg.datagrid('selectRow', curRowIndex);
			var rows = $dg.datagrid('getRows');
			if (rows) {
				rowData = rows[curRowIndex];
			}

			callBack(curRowIndex, rowData);
		} else { // 跳转到上一页的最后一行
			if (currPage <= 1) {
				$dg.datagrid('unselectAll');
				$dg.datagrid('selectRow', curRowIndex);
				callBack(curRowIndex, null);
			} else {
				$dg.datagrid('getPager').pagination({
					pageSize : options.pageSize,
					pageNumber : (currPage - 1)
				});
				$dg.datagrid('getPager').pagination('select', currPage - 1);

				curRowIndex = pageSize - 1;
				$dg.datagrid({
					onLoadSuccess : function(data) {
						if (type == 1) {
							$dg.datagrid('unselectAll');
							$dg.datagrid('selectRow', curRowIndex);
							var rows = $dg.datagrid('getRows');
							if (rows) {
								rowData = rows[curRowIndex];
							}
							callBack(curRowIndex, rowData);
						}

					}
				});

			}
		}
	} else if (type == 2) {

		if (curRowIndex != (pageSize - 1)) {
			if (currPage == max && lastIndex != 0
					&& curRowIndex == (lastIndex - 1)) {
				$dg.datagrid('unselectAll');
				$dg.datagrid('selectRow', curRowIndex);
				callBack(curRowIndex, null);
			} else {
				curRowIndex = curRowIndex + 1;
				$dg.datagrid('unselectAll');
				$dg.datagrid('selectRow', curRowIndex);
				var rows = $dg.datagrid('getRows');
				if (rows) {
					rowData = rows[curRowIndex];
				}

				callBack(curRowIndex, rowData);
			}

		} else {

			if (currPage >= max) {
				$dg.datagrid('unselectAll');
				$dg.datagrid('selectRow', curRowIndex);
				callBack(curRowIndex, null);
			} else {
				$dg.datagrid('getPager').pagination({
					pageSize : options.pageSize,
					pageNumber : (currPage + 1)
				});
				$dg.datagrid('getPager').pagination('select', currPage + 1);

				curRowIndex = 0;
				$dg.datagrid({
					onLoadSuccess : function(data) {
						if (type == 2) {

							$dg.datagrid('unselectAll');
							$dg.datagrid('selectRow', curRowIndex);
							var rows = $dg.datagrid('getRows');
							if (rows) {
								rowData = rows[curRowIndex];
							}
							callBack(curRowIndex, rowData);
						}

					}
				});
			}
		}

	}

}

// 清空表格中的数据
function deleteAllGridCommon(datagridId) {
	$('#' + datagridId).datagrid('loadData', {
		total : 0,
		rows : []
	});
}

$
		.extend(
				$.fn.datagrid.defaults.editors,
				{
					textsearch : {
						init : function(container, options) {
							var div = $('<div class="ipt-search-box"></div>');
							var input = $(
									'<input type="text" class="datagrid-editable-input ipt" style="background:#fff" required="true"/>')
									.appendTo(div);
							var i = $('<i>').appendTo(div);
							div.css({
								width : input.width() + 4,
								margin : 'auto'
							});
							div.appendTo(container);

							if (options.readOnly) {
								input.addClass('readonly').attr('readonly',
										true);
							}

							if (options.validatebox) {
								input.validatebox(options.validatebox.options);
							}

							i.bind('click', function() {
								if (typeof options.clickFn == "function") {
									options.clickFn();
								} else if (typeof options.clickFn == "string") {
									eval(options.clickFn + "()");
								}
							});
							input.bind('blur', function(e) {
								if (typeof options.blurFn == "function") {
									options.blurFn();
								} else if (typeof options.blurFn == "string") {
									eval(options.blurFn + "(e)");
								}
							});
							input.bind('focus', function(e) {
								if (typeof options.focusFn == "function") {
									options.focusFn();
								} else if (typeof options.focusFn == "string") {
									eval(options.focusFn + "(e)");
								}
							});

							return input;
						},
						getValue : function(target) {
							return $(target).val();
						},
						setValue : function(target, value) {
							$(target).val(value);
						},
						resize : function(target, width) {
							$(target)._outerWidth(width)._outerHeight(22);
							$(target).parent()._outerWidth(width);
						}
					}
				});
/**
 * validatebox绑定获取焦点事件 add by wll 20140515
 */
$
		.extend(
				$.fn.datagrid.defaults.editors,
				{
					validatebox : {
						init : function(container, options) {
							var div = $('<div class="easyui-validatebox"></div>');
							var input = $(
									'<input type="text" class="datagrid-editable-input ipt" style="background:#fff" required="true"/>')
									.appendTo(div);
							div.css({
								width : input.width() + 4,
								margin : 'auto'
							});
							div.appendTo(container);

							if (null != options) {
								input.bind('blur', function() {
									if (typeof options.blurFn == "function") {
										options.blurFn();
									}
								});
								input.bind('focus', function() {
									if (typeof options.focusFn == "function") {
										options.focusFn();
									}
								});
							}
							return input;
						},
						getValue : function(target) {
							return $(target).val();
						},
						setValue : function(target, value) {
							$(target).val(value);
						},
						resize : function(target, width) {
							$(target)._outerWidth(width)._outerHeight(22);
							$(target).parent()._outerWidth(width);
						}
					}
				});

/**
 * 打印 1:简单表头打印 获取datagrid的columns表头属性，通过queryDataUrl查询待打印数据，合并到一起，直接传给lodop打印
 * 2:动态表头打印
 * 通过queryHeaderUrl获取后台返回的待打印表头，然后把值放到弹出框中隐藏的datagrid中（获取datagrid原生结构，这样自己就不用合并表头）
 * 通过queryDataUrl查询待打印数据，合并表头数据，传给lodop打印
 */
(function($) {
	printDialog = function(opts) {
		// 合并options
		opts = opts || {};
		var winOpts = $.extend({}, {
			intOrient : 0,// 0：打印机缺省设置,1：纵向，2：横向
			params : {},// 其他需要打印的数据
			barcode : '',
			type : 0,// 0：简单打印，1：动态打印
			needParams : 0,// 0需要查询参数，1不需要带查询参数
			title : '标题', // 打印标题
			dataGridId : '', // 获取要打印的datagridId
			viewName : 'simple_print', // 打印布局界面
			queryHeaderUrl : '',// 动态打印动态获取表头
			queryDataUrl : '', // 查询被打印数据的url
		}, opts);

		// 初始化参数
		var previewUrl = BasePath + '/print/preview?viewName='
				+ winOpts.viewName;
		var $dg = $('#' + winOpts.dataGridId);
		var params = null;
		if (winOpts.needParams == 0) {
			params = $dg.datagrid('options').queryParams;
			params.rows = 100;
		}
		var columns = null;
		// 简单表头获取datagrid数据，复杂表头获取远程数据
		if (winOpts.type == 0) {
			columns = $dg.datagrid('options').columns;
		} else {
			ajaxRequestAsync(BasePath + winOpts.queryHeaderUrl, params,
					function(returnData) {
						columns = returnData.columns;
					});
		}
		var columnsLength = columns.length;
		// 过滤表头不需要打印的column 静态表头配置了printable=false都将不打印
		var grepColumns = $.grep(columns[columnsLength - 1], function(o, i) {
			if ($(o).attr('printable') == false) {
				return true;
			}
			return false;
		}, true);
		// 打开待打印数据页面
		ygDialog({
			isFrame : true,
			cache : false,
			title : winOpts.title,
			modal : true,
			showMask : false,
			fit : true,
			href : previewUrl,
			buttons : [ {
				text : '打印',
				iconCls : 'icon-print',
				handler : 'print_page'
			} ],
			onLoad : function(win, dialog) {
				var tableHeader = dialog.$('#tableHeader');
				var tableBody = dialog.$('#tableBody');
				if (columnsLength > 1) {
					dialog.$("#hiddenDiv").remove();
					dialog
							.$(
									"<div id='hiddenDiv' style='display:none'><table id='exportExcelDG'></table><div>")
							.appendTo("body");
					dialog.$("#exportExcelDG").datagrid({
						columns : columns
					});
					var tbody = dialog.$("#exportExcelDG").prev(0).find(
							'.datagrid-htable').find('tbody');
					tableHeader.append(tbody.html());
				} else {
					$(grepColumns).each(
							function(i, node) {
								var title = $(node).attr('title');
								tableHeader
										.append('<td><div align="center"><b>'
												+ title + '</b></div></td>');
							});
				}
				ajaxRequestAsync(BasePath + winOpts.queryDataUrl, params,
						function(result) {
							var rows;
							if (typeof (result) == 'string') {
								rows = eval('(' + result + ')').rows;
							} else {
								rows = result.rows;
							}
							dialog.params = result.printVo || {};
							dialog.params.title = winOpts.title;
							dialog.params.intOrient = winOpts.intOrient;
							if ('' != winOpts.barcode) {
								dialog.params.barcode = winOpts.barcode;
							}
							// 利用easyui渲染方便，但数据量大时，速度慢 ，自己拼接速度会快些
							// dialog.$("#exportExcelDG").datagrid({data:rows});
							// var bobyTr =
							// dialog.$("#exportExcelDG").prev(0).find('.datagrid-btable').find('tbody');
							// tableBody.append(bobyTr.html());
							for ( var i = 0; i < rows.length; i++) {
								var row = rows[i];
								// 拼接表体信息
								var bodyTrNode = '<tr>';
								var bodyTdNode = '';
								$(grepColumns).each(
										function(i, node) {
											var field = $(node).attr('field');
											var value = row[field + ''];
											if (typeof (value) == 'undefined'
													|| null == value) {
												value = '';
											}
											bodyTdNode += '<td>' + value
													+ '</td>';
										});
								bodyTrNode += bodyTdNode + '</tr>';
								// 填充表体
								tableBody.append(bodyTrNode);
							}
						});
			}
		});
	};
})(jQuery);

$(function($) {

	$.each($.fn.datagrid.defaults.editors, function(type, editor) {
		(function(editor) {
			var initFun = editor.init;
			editor.init = function(container, options) {
				var target = initFun(container, options);

				var editor = target;
				if (type == 'combobox') {
					editor = target.combobox('textbox')
				}
				$(editor).bind('keydown', function(event) {
					handleKeydown($(target), event);
				});
				return target;
			}
		})(editor);
	});

	function handleKeydown(target, event) {
		var key = event.keyCode;
		if (key == 37 || key == 39) {
			event.preventDefault();
			event.stopPropagation();
		}
		if (event.shiftKey)
			key = "shift+" + key;
		var fun = keyHandle[key];
		if (typeof fun == "function") {
			fun.apply(this, target);
		}
	}
	;

	function moveLeftDirect(target) {
		moveHorizontal(target, -1, true);
	}
	function moveRightDirect(target) {
		moveHorizontal(target, 1, true);
	}

	function moveLeft(target) {
		moveHorizontal(target, -1);
	}

	function moveUp(target) {
		moveVertical(target, true);
	}

	function moveDown(target) {
		moveVertical(target, false);
	}

	function moveRight(target) {
		moveHorizontal(target, 1);
	}

	function moveVertical(target, isUp) {
		var cell = getCellInfo(target);
		var index = cell.index;
		moveToOtherRow(cell, isUp, cell.index);
	}

	function moveHorizontal(target, step, direct) {
		var cell = getCellInfo(target);
		var grid = cell.grid;
		if (!grid.datagrid('validateRow'))
			return;
		// if (!direct) {
		// var selectionStart = $(target).selectionStart();
		// if (selectionStart != undefined && selectionStart != 0 && step < 0)
		// return;
		// if (step > 0) {
		// var txt = $(target).val();
		// if (txt && txt.length > selectionStart)
		// return;
		// }
		// }

		var fields = cell.fields;// grid.datagrid('getColumnFields',
									// true).concat($('#dg').datagrid('getColumnFields'));
		var index = cell.index;

		if (index < 0)
			index = step < 0 ? 0 : fields.length - 1;
		else
			index = index + step;

		if (index >= 0 && index < fields.length) {
			focusCell(target, cell, index, step < 0);
		} else if (index < 0) {
			moveToOtherRow(cell, true);
		} else if (index >= fields.length) {
			moveToOtherRow(cell, false);
		}
	}

	function focusCell(target, cell, cellNewIndex, isLeft) {
		/*
		 * rowedit不是原生方法，editindex如果通过api方式扩展，可以根据api来读取。 否则使用如下方式代替，效率会有所降低。
		 * var row = grid.datagrid('getSelected'); var rowIndex =
		 * grid.datagrid('getRowIndex', row);
		 */
		var row = cell.grid.datagrid('getSelected');
		var rowIndex = cell.grid.datagrid('getRowIndex', row);
		var ed = getCellEditor(rowIndex, cellNewIndex, cell, isLeft);// grid.datagrid('getEditor',
																		// {
																		// index:
																		// rowIndex,
																		// field:
																		// field
																		// });
		if (ed != null && ed != "") {
			var target = $(ed.target);
			if (ed.type == "combobox") {
				target = target.combobox('textbox');
			}
			target.focus();
			target.select();
			return true;
		}
		return false;
	}

	function getCellEditor(rowIndex, cellNewIndex, cell, isLeft) {
		var step = isLeft ? -1 : 1;
		var cellIndex = cellNewIndex;
		var fields = cell.fields;
		var grid = cell.grid;
		var ed = grid.datagrid('getEditor', {
			index : rowIndex,
			field : fields[cellIndex]
		});
		if (ed && ed.type != "readonlytext")
			return ed;
		// 如果下一个没有编辑器或者编辑器为只读,则跳到下一个编辑器
		while (true) {
			cellIndex += step;
			if (cellIndex < 0 || cellIndex >= fields.length) {
				moveToOtherRow(cell, isLeft);
				return;
			} else {
				ed = grid.datagrid('getEditor', {
					index : rowIndex,
					field : fields[cellIndex]
				});
				if (ed && ed.type != "readonlytext")
					return ed;
			}
		}
	}

	function moveToOtherRow(cell, isUp, cellIndex) {
		var grid = cell.grid;

		var row = grid.datagrid('getSelected');
		var rowIndex = grid.datagrid('getRowIndex', row);
		var rows = grid.datagrid('getRows');
		var index = rowIndex + (isUp ? -1 : 1);
		if (index < 0 || index >= rows.length)
			return;
		var fields = cell.fields;
		if (endEditing(grid[0].id)) {
			grid.datagrid('selectRow', index).datagrid('beginEdit', index);
			window.editIndex = index;
			if (cellIndex == undefined)
				cellIndex = isUp ? fields.length - 1 : 0;
			var field = fields[cellIndex];
			focusCell(index, cell, cellIndex, isUp);
		}
	}

	function getCellInfo(target, fields) {
		var cell = $(target).parent().parent().parent().parent().parent()
				.parent();
		var grid = findGrid($(cell));
		var data = $.data($(cell)[0], "datagrid.editor");
		var fields = grid.datagrid('getColumnFields', true).concat(
				grid.datagrid('getColumnFields'));
		var field = data.field;
		var index = -1;
		$.each(fields, function(i, name) {
			if (name == data.field)
				index = i;
		});

		return {
			cell : cell,
			grid : grid,
			index : index,
			field : field,
			fields : fields
		};
	}
	function findGrid(cell) {
		var parent = $(cell).parent();
		var i = 0;
		while (parent != window.document && i < 20) {
			var grid = "";
			if (parent.hasClass("datagrid-view")) {
				grid = parent.children('table');
			}
			if (grid.length != 0)
				return grid;
			parent = parent.parent();
			i++;
		}
	}

	function getGrid(cell) {
		var gridId = $.data(cell[0], 'gridId');
		if (!gridId) {
			var grid = findGrid(container);
			gridId = grid.attr("id");
			$.data(cell[0], 'gridId', gridId);
			return grid;
		}
		return $("#" + gridId);
	}

	var keyHandle = {
		37 : moveLeft,
		38 : moveUp,
		40 : moveDown,
		39 : moveRight,
		13 : moveRightDirect,
		9 : moveRightDirect,
		"shift+9" : moveLeftDirect
	};
	function endEditing(gridId) {
		if (window.editIndex == undefined) {
			return true;
		}
		if ($('#' + gridId).datagrid('validateRow', window.editIndex)) {
			$('#' + gridId).datagrid('endEdit', editIndex);
			editIndex = undefined;
			return true;
		} else {
			return false;
		}
	}
});
// 清除所有表单的验证提示信息
function clearAllValidateHintMsg() {
	$('.validatebox-tip').remove();
}
function dgSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var _name = opts.name;
	var iframe = opts.isFrame;
	//定义参数，用于清空界面时,某些数据的不可清除 by jian.dq
	var disClearId = opts.disClearId || null;
	//定义form 表单初始化input 值
	var initInputMap = opts.initInputMap || null;
	//禁用某些input输入框 ,array
	var disEeditorId = opts.disEeditorId || null;
	//弹出界面时，是否查询(false 表示不查询)
	var promptlyQuery = opts.promptlyQuery;
	if (typeof iframe == "undefined") {
		iframe = true;
	}
	if (typeof promptlyQuery == "undefined") {
		promptlyQuery = false;
	}
	top.dgSelectorOpts = opts;

	ygDialog({
		title : _title,
		href : _url,
		width : _w,
		height : _h,
		isFrame : iframe,
		modal : true,
		showMask : true,
		onLoad : function(win, content) {
			var tb = content.tbgrid;
			var _this = $(this);
			if (tb == null) {
				if(_name != undefined && _name != ""){
					tb = opts.tbGrid || content.$('#'+_name+'dialog_SearchDataGrid');
				}
				else{
					tb = opts.tbGrid || content.$('#dialog_SearchDataGrid');
				}
				
			}
			//初始化表单数据 by jian.dq
			if(null != initInputMap && typeof initInputMap == 'object'){
				for(var key in initInputMap){
					if(initInputMap.hasOwnProperty(key)){
						var ipobj = $('#'+key);
						var c = ipobj.attr('class');
						var val = initInputMap[key];
						if(c.indexOf('combobox') != -1){
							if(val != undefined && typeof val == "string"){
								ipobj.combobox('setValue',initInputMap[key]);
							}else if(val != undefined && typeof val == "object"){
								var d = val.attr('class');
								if(d.indexOf('combobox') != -1){
									ipobj.combobox('setValue',val.combobox('getValue'));
								}else{
									ipobj.combobox('setValue',val.val());
								}
							}
						}else{
							if(val != undefined && typeof val == "string"){
								ipobj.val(initInputMap[key]);
							}else if(val != undefined && typeof val == "object"){
								var d = val.attr('class');
								if(d.indexOf('combobox') != -1){
									ipobj.val(val.combobox('getValue'));
								}else{
									ipobj.val(val.val());
								}
							}
						}
					}
				}
			}
			//给inut表单加锁
			function  lockInput(){
				if(null != disEeditorId && disEeditorId instanceof Array){
					$.each(disEeditorId,function(index,item){
						var ipobj =$('#'+disEeditorId[index]);
						var c = ipobj.attr('class');
						if(undefined == c || null == c){
							ipobj.attr('readOnly',true);
							return;
						}else if(c.indexOf('combobox') != -1){
							ipobj.combobox('disable');
							return;
						}else if(c.indexOf('ipt') != -1 && c.indexOf('combobox') == -1){
							try{
								ipobj.iptSearch('disable');
							}catch(e){
							}
							ipobj.attr('readOnly',true);
							return;
						}else{
							ipobj.attr('readOnly',true);
						}
					});
				}
			}
			lockInput();
			//解锁
			function unlockInput(){
				if(null != disEeditorId && disEeditorId instanceof Array){
					$.each(disEeditorId,function(index,item){
						var ipobj =$('#'+disEeditorId[index]);
						var c = ipobj.attr('class');
						if(undefined == c || null == c){
							ipobj.removeAttr('readOnly');
							return;
						}else if(c.indexOf('combobox') != -1){
							ipobj.combobox('enable');
							return;
						}else if(c.indexOf('ipt') != -1 && c.indexOf('combobox') == -1){
							try{
								ipobj.iptSearch('enable');
							}catch(e){
							}
							ipobj.removeAttr('readOnly');
							return;
						}else{
							ipobj.removeAttr('readOnly');
						}
					});
				}
			}
			function searchDatagrid(){
				unlockInput();
				var targetForm = "";
				if(_name != undefined && _name != ""){
				    targetForm = $('#'+_name+'dialog_SarchForm');
				}
				else{
					 targetForm = $('#dialog_SarchForm');
				}
				tb.datagrid('options').queryParams = targetForm
						.form('getData');
				tb.datagrid('options').url = opts.queryUrl;
				tb.datagrid('load');
				lockInput();
			}
			if(promptlyQuery){
				searchDatagrid();
			}
			if (opts.queryUrl != null) {
				var searchBtn = $('#dgSelectorSearchBtn');
				var clearBtn = $('#dgSelectorClearBtn');
				var confirmBtn = $('#dgSelectorConfirmBtn');
				
				searchBtn.click(searchDatagrid);
				
				clearBtn.click(function() {
					var mapInput = new Object();
					if(null != disClearId && disClearId instanceof Array){
						$.each(disClearId,function(index,item){
							var ipobj = $('#'+disClearId[index]);
							var c = ipobj.attr('class');
							if(c){
								if(c.indexOf('combobox') != -1){
									mapInput[disClearId[index]] = ipobj.combobox('getValue');
								}else{
									mapInput[disClearId[index]] = ipobj.val();
								}
							}else{
								mapInput[disClearId[index]] = ipobj.val();
							}
							
						});
					}
					if(_name != undefined && _name != ""){
						$('#'+_name+'dialog_SarchForm').form('clear');
					}
					else{
						$('#dialog_SarchForm').form('clear');
					}
				
					if(null != disClearId && disClearId instanceof Array){
						for(var key in mapInput){
							var ipobj = $('#'+key);
							var c = ipobj.attr('class');
							if(c){
								if(c.indexOf('combobox') != -1){
									 ipobj.combobox('setValue',mapInput[key]);
								}else{
									ipobj.val(mapInput[key]);
								}
							}else{
								ipobj.val(mapInput[key]);
							}
						}
					}
				});

				if (confirmBtn) {
					confirmBtn.click(function() {
//						var rowsData = tb.datagrid('getSelections');
//						if (rowsData.length <= 0) {
//							rowsData = tb.datagrid('getChecked');
//						}
						
						rowsData = tb.datagrid('getChecked');
						
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						var returnFlag = false;
						if (typeof opts.fn == "function") {
							returnFlag = opts.fn(rowsData);
						}
						if(!returnFlag){
							win.close();
						}
						
					});
				}

			}
			if (typeof opts.remenberFn != "function") {
				$("#check-remenber").remove();
				$("#lable-remenber").remove();
			}
			if(opts.disableDblClick){
				return;
			}
			tb.datagrid({
						onDblClickRow : function(rowIndex, rowData) {
							var returnFlag = false;
							if (typeof opts.fn == "function") {
								returnFlag = opts.fn(rowData, rowIndex);
							}
							var remenberBtn = document
									.getElementById("check-remenber");
							if (null != remenberBtn && remenberBtn.checked) {
								var rowsData = tb.datagrid('getSelections');
								opts.remenberFn(rowsData);
							}
							if(!returnFlag){
								win.close();
							}
						},
						onLoadSuccess : function() {
							$('input[name=optsel]', _this.contents())
									.on(
											'click',
											function() {
												var _idx = $(
														'input[name=optsel]',
														_this.contents())
														.index(this);
												var row = tb
														.datagrid('getRows')[_idx];
												if (typeof opts.fn == "function") {
													opts.fn(row);
													if ($(top.iptSearchInputObj)[0]
															&& $(
																	top.iptSearchInputObj)
																	.hasClass(
																			'easyui-validatebox')) {
														$(top.iptSearchInputObj)
																.validatebox(
																		'validate');
													}
												}
												win.close();
											});
						}

					});
		}
	});
	return false;
}
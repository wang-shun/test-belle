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
 * @param billNos 勾选的单据号            
 */
function exportExcelBaseInfo(dataGridId, exportUrl, excelTitle,billNoIn,searchType) {
	var $dg = $("#" + dataGridId + "");
	var params = $dg.datagrid('options').queryParams;
	var grepColumns = $dg.datagrid('options').columns;
	var grepColumnsTemp=$.extend(true,{},grepColumns);//jquery 克隆对象，true表示深度克隆
	var columns = $.grep(grepColumnsTemp[0], function(o, i) {
		if ($(o).attr("notexport") == true) {
			return true;
		}
		return false;
	}, true);
	
	/**
	*非尺码横排的editor,如果有combobox,
	*后台导出时转换数据会失败，要把editor里面的data删除掉
	*/
	$.each(columns,function(i,item){
		if(item.editor&&item.editor.options&&item.editor.options.data){
			delete item.editor.options.data;
		}
	});
	
	var exportColumns = JSON.stringify(columns);
	var url = getBasePath(exportUrl);
	var dataRow = $dg.datagrid('getRows');
	$("#exportExcelForm").remove();
	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	var fromObj = $('#exportExcelForm');
	if (dataRow.length > 0) {
		fromObj.form('submit', {
			url : url,
			onSubmit : function(param) {
				param.exportColumns = exportColumns;
				param.fileName = excelTitle;
				param.rows=10000;//设置导出数量最大值
				if(searchType){
					param.searchType=searchType;//查询类型 用于传到后台控制器自定义查询
				}
				if(isNotBlank(billNoIn)){
					if(billNoIn.indexOf(",")<0){
						billNoIn=billNoIn.substring(1,billNoIn.length-1);
						param.billNo=billNoIn;
					}else{
						param.billNoIn=billNoIn;
					}
				}
				if (params != null && params != {}) {
					$.each(params, function(i) {
						param[i] = params[i];
					});
				}
			},
			success : function() {
				showSuc("导出成功！");
			}
		});
	} else {
		showWarn('查询记录为空，不能导出!', 1);
	}
}

/**
 * 表头含有多行，一个单元格占有多列；导出的数据来源于多个表，重写do_export方法或者在控制器中自定义一个方法
 * @param dataGridId
 * @param exportUrl
 * @param excelTitle
 * @param billNoIn
 * @param searchType
 */
function exportExcelWithFrozenInfo(dataGridId, exportUrl, excelTitle,billNoIn,searchType) {
	var $dg = $("#" + dataGridId + "");
	var params = $dg.datagrid('options').queryParams;
	var grepColumns = $dg.datagrid('options').columns;
	var grepColumnsTemp=$.extend(true,{},grepColumns);//jquery 克隆对象，true表示深度克隆
	
	//有frozenColumn
	var frozenColumns = $dg.datagrid('options').frozenColumns;
	var frozenColumnsTemp=$.extend(true,{},frozenColumns);//jquery 克隆对象，true表示深度克隆
	
	var columns = new Array();
	for(var i = 0;i<grepColumns.length;i++){//2
		var rowEleOfColumns = grepColumns[i];
		for(var j = 0;j<rowEleOfColumns.length;j++){
			var rowEle = rowEleOfColumns[j];
			var colspanV = rowEle['colspan'];
			if((colspanV!=null&&colspanV!="0")||rowEleOfColumns[j]['notexport']==true){
			}else{
				var obj = {'field':rowEle['field'],'title':rowEle['title']};
				columns.push(obj);
			}
		}
	}
	
	var frozenColumns = $.grep(frozenColumnsTemp[0], function(o, i) {
		if ($(o).attr("notexport") == true) {
			return true;
		}
		return false;
	}, true);

	/**
	*非尺码横排的editor,如果有combobox,
	*后台导出时转换数据会失败，要把editor里面的data删除掉
	*/
	$.each(columns,function(i,item){
		if(item.editor&&item.editor.options&&item.editor.options.data){
			delete item.editor.options.data;
		}
	});
	
	$.each(frozenColumns,function(i,item){
		if(item.editor&&item.editor.options&&item.editor.options.data){
			delete item.editor.options.data;
		}
	});
	
	var exportColumns = JSON.stringify(columns);
	var expostFrozenColumns = JSON.stringify(frozenColumns);
	
	var url = getBasePath(exportUrl);
	var dataRow = $dg.datagrid('getRows');
	$("#exportExcelForm").remove();
	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	var fromObj = $('#exportExcelForm');
	if (dataRow.length > 0) {
		fromObj.form('submit', {
			url : url,
			onSubmit : function(param) {
				param.exportColumns = exportColumns;
				param.frozenColumns = expostFrozenColumns;
				param.fileName = excelTitle;
				param.rows=10000;//设置导出数量最大值
				if(searchType){
					param.searchType=searchType;//查询类型 用于传到后台控制器自定义查询
				}
				if(isNotBlank(billNoIn)){
					if(billNoIn.indexOf(",")<0){
						billNoIn=billNoIn.substring(1,billNoIn.length-1);
						param.billNo=billNoIn;
					}else{
						param.billNoIn=billNoIn;
					}
				}
				if (params != null && params != {}) {
					$.each(params, function(i) {
						param[i] = params[i];
					});
				}
			},
			success : function() {
				showSuc("导出成功！");
			}
		});
	} else {
		showWarn('查询记录为空，不能导出!', 1);
	}
}


/**
 * 尺码横排的导出
 * @param dataGridId导出数据的表格ID
 * @param billNo单据编号
 * @param exportUrl导出数据的URL 基础资料一般都是 /模块名/do_export.htm *如机构:/store/do_export.htm
 * @param excelTitleexcel文件名
 */
function exportExcel(dataGridId, billNo, preColNames, endColNames, exportUrl, excelTitle) {
	$dg = $("#" + dataGridId);
	var page = 1;//导出默认导出全部 从第一页开始
	var rows = 10000;//设置导出数量最大值
	//**前置列
	var preColNamesTemp = $.grep(preColNames, function(o, i) {
		if ($(o).attr("notexport") == true) {
			return true;
		}
		return false;
	}, true);
	//去掉editor
	var preColNamesExport=[];
	for ( var i = 0; i < preColNamesTemp.length; i++) {
		var pre={};
		pre.title=preColNamesTemp[i].title;
		pre.width=preColNamesTemp[i].width;
		pre.field=preColNamesTemp[i].field;
		preColNamesExport.push(pre);
	}
	
	//**后置列
	var endColNamesTemp = $.grep(endColNames, function(o, i) {
		if ($(o).attr("notexport") == true) {
			return true;
		}
		return false;
	}, true);
	//去掉editor
	var endColNamesExport=[];
	for ( var i = 0; i < endColNamesTemp.length; i++) {
		var end={};
		end.title=endColNamesTemp[i].title;
		end.width=endColNamesTemp[i].width;
		end.field=endColNamesTemp[i].field;
		endColNamesExport.push(end);
	}
	
	var url = getBasePath(exportUrl);
	$("#exportExcelForm").remove();
	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	var fromObj = $('#exportExcelForm');
	fromObj.form('submit', {
		url : url,
		onSubmit : function(param) {
			param.preColNames = JSON.stringify(preColNamesExport);
			param.endColNames = JSON.stringify(endColNamesExport);
			param.fileName = excelTitle;
			param.billNo = billNo;
			param.page = page;
			param.rows = rows;
		},
		// 前后台之间传输的是字符串形式，需要用eval函数转化
		success : function(data) {
			showSuc("导出成功！");
		}
	});
};

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
			if (currPage == max && lastIndex != 0 && curRowIndex == (lastIndex - 1)) {
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
	SAVE : "save",// 增删改
	DO_EXPORT : "do_export.htm",// 导出excel
	IMPORT : "import",//导入
	CANCEL : "cancel", // 作废
	OVER : "over", // 完结
	VERIFY : "verify",//审核
	CONFIRM : "confirm" ,// 确认
	MUTI_TABLE_EXPORT:"mutiTableExport",
	SUBMIT : "submit"
};

// 重新加载数据到datagrid
function loadDataToGrid(dataGridId, url, queryParams, callback) {
	var $dg = $('#' + dataGridId);
	$dg.datagrid('options').queryParams = queryParams;
	$dg.datagrid('options').url = url;
	if (callback) {
		$dg.datagrid('options').onLoadSuccess = callback;
	}
	$dg.datagrid('load');
};

// 清空表格中的数据
function deleteAllGridCommon(datagridId) {
	$('#' + datagridId).datagrid('loadData', {
		total : 0,
		rows : []
	});
} 

// update by daixiaowei 加入可以传入参数
function dgSelector(opts) {
	var _isAutoLoad = opts.isAutoLoad || false;//是否弹窗的时候就提交查询
	var _url = opts.href || '';
	var _params=opts.params;//传入参数列表
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
	}
	top.dgSelectorOpts = opts;

	ygDialog({
		title : _title,
		href : _url,
		width : _w,
		height : 'auto',//_h 修改成自动 以自动适应父页面高度
		isFrame : iframe,
		modal : true,
		showMask : true,
		onLoad : function(win, content) {
			var tb = content.tbgrid;
			var _this = $(this);
			if (tb == null) {
				tb = opts.tbGrid || $($('table[id="dialog_SearchDataGrid"]')[$('table[id="dialog_SearchDataGrid"]').length-1]);
			}
			//动态控制弹窗面板中网格展示列
			if(opts.gridColumnsSplice){
				var gridColumns = tb.datagrid('options').columns[0];
				var columnsSplit = opts.gridColumnsSplice.split(",");
				gridColumns.splice(columnsSplit[0], columnsSplit[1]);// 删除前面两个元素 排除店(仓)信息
			}
			var targetForm = $($('form[id="dialog_SarchForm"]')[$('form[id="dialog_SarchForm"]').length-1]);
			targetForm.form('load', _params);
			// 商品查询面板需要回车弹窗 传入参数 这里特殊处理
			if(_params && _params.itemCode){
				$("#codeCondition").val(_params.itemCode);
			}
			if (opts.queryUrl != null) {
				var searchBtn = $($('a[id="dgSelectorSearchBtn"]')[$('a[id="dgSelectorSearchBtn"]').length-1]);
				var clearBtn = $($('a[id="dgSelectorClearBtn"]')[$('a[id="dgSelectorClearBtn"]').length-1]);
				var confirmBtn = $($('a[id="dgSelectorConfirmBtn"]')[$('a[id="dgSelectorConfirmBtn"]').length-1]);
				var recoveryBtn = $($('a[id="dgSelectorRecoveryBtn"]')[$('a[id="dgSelectorRecoveryBtn"]').length-1]);
				searchBtn.click(function() {
					tb.datagrid('options').queryParams = targetForm.form('getData');
					$.extend(tb.datagrid('options').queryParams,_params);
					tb.datagrid('options').url = opts.queryUrl;
					tb.datagrid('load');
					if(_params && _params.itemCode){
						delete _params.itemCode;
					}
				});
				//是否弹窗的时候就提交查询
				if (_isAutoLoad) {
					searchBtn.click();
				}
				clearBtn.click(function() {
					targetForm.form('clear');
				});
				if (typeof top.dgSelectorOpts.recoveryFn == "function") {
					recoveryBtn.click(function() {
						var reqParam = {};
						ajaxRequest(opts.recoveryUrl, reqParam, function(ret) {
							if (ret.rows.length < 1) {
								showInfo("没找到通用条件的值");
								return;
							}
							top.dgSelectorOpts.recoveryFn(ret.rows);
						});
						win.close();
					});
				} else {
					recoveryBtn.remove();
				}
				if (confirmBtn) {
					confirmBtn.click(function() {
						var rowsData = tb.datagrid('getSelections');
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData);
						}
						win.close();
					});
				}
			}
			if (typeof top.dgSelectorOpts.remenberFn != "function") {
				$("#check-remenber").remove();
				$("#lable-remenber").remove();
			}
			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					if (typeof top.dgSelectorOpts.fn == "function") {
						top.dgSelectorOpts.fn(rowData, rowIndex);
						if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
							try{$(top.iptSearchInputObj).validatebox('validate');}catch(e){}
						}
					}
					var remenberBtn = document.getElementById("check-remenber");
					if (null != remenberBtn && remenberBtn.checked) {
						var rowsData = tb.datagrid('getSelections');
						top.dgSelectorOpts.remenberFn(rowsData);
					}
					win.close();
				},
				onLoadSuccess : function() {
					$('input[name=optsel]', _this.contents()).on('click', function() {
						var _idx = $('input[name=optsel]', _this.contents()).index(this);
						var row = tb.datagrid('getRows')[_idx];
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(row);
							if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
								$(top.iptSearchInputObj).validatebox('validate');
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

/**
 * 模块选择对话框
 * 
 * @param opts
 * @returns {Boolean}
 */
function moduleSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
			var confirmBtn = $('#dgSelectorConfirmBtn');
			if (confirmBtn) {
				confirmBtn.click(function() {
					var data = $('#moduleTree').tree('getChecked');
					if (data.length <= 0) {
						showWarn('请选择后再操作！');
						return false;
					}
					var queryMxURL = BasePath + '/condition_module_custom/save';
					var condition = $.map(data, function(o) {
						if (o.children == null) {
							return '{"moduleId":"' + o.id + '","conditionType":"' + opts.conditionType + '","moduleName":"' + o.text + '"}';
						}
					});
					var reqParam = {
						"inserted" : "[" + condition.toString() + "]"
					};
					ajaxRequest(queryMxURL, reqParam, function(result) {
						if (result.success) {
							showSuc('保存成功!');
							if (typeof top.dgSelectorOpts.fn == "function") {
								top.dgSelectorOpts.fn();
							}
						} else {
							showError('保存失败,请联系管理员!');
						}
					});

					win.close();
				});
			}

		}
	});
	return false;
}
var moduleId = getParamFromUrl("moduleId");
/*
 * 选择供应商
 */
function supplierSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
				tb = opts.tbGrid || $('#dialog_SearchDataGrid');
			}

			if (opts.queryUrl != null) {
				var searchBtn = $('#dgSelectorSearchBtn');
				var clearBtn = $('#dgSelectorClearBtn');
				var confirmBtn = $('#dgSelectorConfirmBtn');
				var recoveryBtn = $('#dgSelectorRecoveryBtn');
				searchBtn.click(function() {
					var targetForm = $('#dialog_SarchForm');
					tb.datagrid('options').queryParams = targetForm.form('getData');
					tb.datagrid('options').url = opts.queryUrl;
					tb.datagrid('load');
				});

				clearBtn.click(function() {
					$('#dialog_SarchForm').form('clear');
				});
				recoveryBtn.click(function() {
					var reqParam = {};
					var recoveryUrl = BasePath + '/condition_supplier/list.json';
					ajaxRequest(recoveryUrl, reqParam, function(ret) {
						var rowsData = ret.rows;
						if (rowsData.length < 1) {
							showInfo("没找到通用条件的值");
							return;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData[0]);
						}
					});
					win.close();
				});

				if (confirmBtn) {
					confirmBtn.click(function() {
						var rowsData = tb.datagrid('getSelections');
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData);
						}
						win.close();
					});
				}

			}

			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					if (typeof top.dgSelectorOpts.fn == "function") {
						top.dgSelectorOpts.fn(rowData, rowIndex);
						if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
							$(top.iptSearchInputObj).validatebox('validate');
						}
					}
					var remenberBtn = document.getElementById("check-remenber");
					if (null != remenberBtn && remenberBtn.checked) {
						var condition = '{"supplierNo":"' + rowData.supplierNo + '","supplierName":"' + rowData.supplierName + '"}';
						var queryMxURL = BasePath + '/condition_supplier/remenber?moduleId=' + moduleId;
						var reqParam = {
							"inserted" : "[" + condition + "]"
						};
						ajaxRequest(queryMxURL, reqParam, function(ret) {
						});
					}
					win.close();
				},
				onLoadSuccess : function() {
					$('input[name=optsel]', _this.contents()).on('click', function() {
						var _idx = $('input[name=optsel]', _this.contents()).index(this);
						var row = tb.datagrid('getRows')[_idx];
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(row);
							if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
								$(top.iptSearchInputObj).validatebox('validate');
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

/*
 * 选择机构
 */
function storeSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
				tb = opts.tbGrid || $('#dialog_SearchDataGrid');
			}

			if (opts.queryUrl != null) {
				var searchBtn = $('#dgSelectorSearchBtn');
				var clearBtn = $('#dgSelectorClearBtn');
				var confirmBtn = $('#dgSelectorConfirmBtn');
				var recoveryBtn = $('#dgSelectorRecoveryBtn');
				searchBtn.click(function() {
					var targetForm = $('#dialog_SarchForm');
					tb.datagrid('options').queryParams = targetForm.form('getData');
					tb.datagrid('options').url = opts.queryUrl;
					tb.datagrid('load');
				});

				clearBtn.click(function() {
					$('#dialog_SarchForm').form('clear');
				});
				recoveryBtn.click(function() {
					var reqParam = {};
					var recoveryUrl = BasePath + '/condition_store/list.json';
					ajaxRequest(recoveryUrl, reqParam, function(ret) {
						var rowsData = ret.rows;
						if (rowsData.length < 1) {
							showInfo("没找到通用条件的值");
							return;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData[0]);
						}
					});
					win.close();
				});

				if (confirmBtn) {
					confirmBtn.click(function() {
						var rowsData = tb.datagrid('getSelections');
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData);
						}
						win.close();
					});
				}

			}

			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					if (typeof top.dgSelectorOpts.fn == "function") {
						top.dgSelectorOpts.fn(rowData, rowIndex);
						if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
							$(top.iptSearchInputObj).validatebox('validate');
						}
					}
					var remenberBtn = document.getElementById("check-remenber");
					if (null != remenberBtn && remenberBtn.checked) {
						var condition = '{"storeNo":"' + rowData.storeNo + '","storeName":"' + rowData.storeName + '"}';
						var queryMxURL = BasePath + '/condition_store/remenber?moduleId=' + moduleId;
						var reqParam = {
							"inserted" : "[" + condition + "]"
						};
						ajaxRequest(queryMxURL, reqParam, function(ret) {
						});
					}
					win.close();
				},
				onLoadSuccess : function() {
					$('input[name=optsel]', _this.contents()).on('click', function() {
						var _idx = $('input[name=optsel]', _this.contents()).index(this);
						var row = tb.datagrid('getRows')[_idx];
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(row);
							if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
								$(top.iptSearchInputObj).validatebox('validate');
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

/**
 * 通用条件查询类型定义
 */
var ConditionType = {
	Supplier : 1,
	Store : 2,
	Item : 3,
	Brand : 4,
	Zone : 5
};

/*
 * 选择品牌
 */
function brandSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
				tb = opts.tbGrid || $('#dialog_SearchDataGrid');
			}

			if (opts.queryUrl != null) {
				var searchBtn = $('#dgSelectorSearchBtn');
				var clearBtn = $('#dgSelectorClearBtn');
				var confirmBtn = $('#dgSelectorConfirmBtn');
				var recoveryBtn = $('#dgSelectorRecoveryBtn');
				searchBtn.click(function() {
					var targetForm = $('#dialog_SarchForm');
					tb.datagrid('options').queryParams = targetForm.form('getData');
					tb.datagrid('options').url = opts.queryUrl;
					tb.datagrid('load');
				});

				clearBtn.click(function() {
					$('#dialog_SarchForm').form('clear');
				});
				recoveryBtn.click(function() {
					var reqParam = {};
					var recoveryUrl = BasePath + '/condition_brand/list.json';
					ajaxRequest(recoveryUrl, reqParam, function(ret) {
						var rowsData = ret.rows;
						if (rowsData.length < 1) {
							showInfo("没找到通用条件的值");
							return;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData[0]);
						}
					});
					win.close();
				});

				if (confirmBtn) {
					confirmBtn.click(function() {
						var rowsData = tb.datagrid('getSelections');
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData);
						}
						win.close();
					});
				}

			}

			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					if (typeof top.dgSelectorOpts.fn == "function") {
						top.dgSelectorOpts.fn(rowData, rowIndex);
						if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
							$(top.iptSearchInputObj).validatebox('validate');
						}
					}
					var remenberBtn = document.getElementById("check-remenber");
					if (null != remenberBtn && remenberBtn.checked) {
						var condition = '{"brandNo":"' + rowData.brandNo + '","brandName":"' + rowData.brandName + '"}';
						var queryMxURL = BasePath + '/condition_brand/remenber?moduleId=' + moduleId;
						var reqParam = {
							"inserted" : "[" + condition + "]"
						};
						ajaxRequest(queryMxURL, reqParam, function(ret) {
						});
					}
					win.close();
				},
				onLoadSuccess : function() {
					$('input[name=optsel]', _this.contents()).on('click', function() {
						var _idx = $('input[name=optsel]', _this.contents()).index(this);
						var row = tb.datagrid('getRows')[_idx];
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(row);
							if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
								$(top.iptSearchInputObj).validatebox('validate');
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
/*
 * 选择地区
 */
function zoneSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
				tb = opts.tbGrid || $('#dialog_SearchDataGrid');
			}

			if (opts.queryUrl != null) {
				var searchBtn = $('#dgSelectorSearchBtn');
				var clearBtn = $('#dgSelectorClearBtn');
				var confirmBtn = $('#dgSelectorConfirmBtn');
				var recoveryBtn = $('#dgSelectorRecoveryBtn');
				searchBtn.click(function() {
					var targetForm = $('#dialog_SarchForm');
					tb.datagrid('options').queryParams = targetForm.form('getData');
					tb.datagrid('options').url = opts.queryUrl;
					tb.datagrid('load');
				});

				clearBtn.click(function() {
					$('#dialog_SarchForm').form('clear');
				});
				recoveryBtn.click(function() {
					var reqParam = {};
					var recoveryUrl = BasePath + '/condition_zone/list.json';
					ajaxRequest(recoveryUrl, reqParam, function(ret) {
						var rowsData = ret.rows;
						if (rowsData.length < 1) {
							showInfo("没找到通用条件的值");
							return;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData[0]);
						}
					});
					win.close();
				});

				if (confirmBtn) {
					confirmBtn.click(function() {
						var rowsData = tb.datagrid('getSelections');
						if (rowsData.length <= 0) {
							showWarn('请选择后再操作！');
							return false;
						}
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(rowsData);
						}
						win.close();
					});
				}

			}

			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					if (typeof top.dgSelectorOpts.fn == "function") {
						top.dgSelectorOpts.fn(rowData, rowIndex);
						if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
							$(top.iptSearchInputObj).validatebox('validate');
						}
					}
					var remenberBtn = document.getElementById("check-remenber");
					if (null != remenberBtn && remenberBtn.checked) {
						var condition = '{"zoneNo":"' + rowData.zoneNo + '","zoneName":"' + rowData.brandName + '"}';
						var queryMxURL = BasePath + '/condition_zone/remenber?moduleId=' + moduleId;
						var reqParam = {
							"inserted" : "[" + condition + "]"
						};
						ajaxRequest(queryMxURL, reqParam, function(ret) {
						});
					}
					win.close();
				},
				onLoadSuccess : function() {
					$('input[name=optsel]', _this.contents()).on('click', function() {
						var _idx = $('input[name=optsel]', _this.contents()).index(this);
						var row = tb.datagrid('getRows')[_idx];
						if (typeof top.dgSelectorOpts.fn == "function") {
							top.dgSelectorOpts.fn(row);
							if ($(top.iptSearchInputObj)[0] && $(top.iptSearchInputObj).hasClass('easyui-validatebox')) {
								$(top.iptSearchInputObj).validatebox('validate');
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
/*--获取网页传递的参数--*/
function getParamFromUrl(paras) {
	var url = location.href;
	var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
	var paraObj = {};
	for (i = 0; j = paraString[i]; i++) {
		paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
	}
	var returnValue = paraObj[paras.toLowerCase()];
	if (typeof (returnValue) == "undefined") {
		return "";
	} else {
		return returnValue;
	}
}
/**
 * function main_recovery(){ recoverySupplier(); recoveryStore(); }
 */
/**
 * 恢复供应商
 * 
 * @param moduleId
 *            模块ID
 */
function recoverySupplier() {
	url = BasePath + '/condition_supplier/list.json';
	var param = {
		moduleId : moduleId
	};
	ajaxRequest(url, param, function(ret) {
		var rows = ret.rows;
		if (rows != undefined) {
			if (rows.length > 1) {
				showInfo("通用条件的值不唯一，请重新选择");
				return;
			}
			var data = rows[0];
			$('#supplierNoBox').val(data.supplierNo);
			$('#supplierNoBoxStr').val(data.supplierNo + '→' + data.supplierName);
		}
	});
}
/**
 * 恢复机构
 * 
 * @param moduleId
 */
function recoveryStore() {
	var url = BasePath + '/condition_store/list.json';
	var param = {
		moduleId : moduleId
	};
	ajaxRequest(url, param, function(ret) {
		var rows = ret.rows;
		if (rows != undefined) {
			if (rows.length > 1) {
				showInfo("通用条件的值不唯一，请重新选择");
				return;
			}
			var data = rows[0];
			$('#storeNoBox').val(data.storeNo);
			$('#storeNoBoxStr').val(data.storeNo + '→' + data.storeName);
		}
	});
}
function recoveryBrand() {
	var url = BasePath + '/condition_brand/list.json';
	var param = {
		moduleId : moduleId
	};
	ajaxRequest(url, param, function(ret) {
		var rows = ret.rows;
		if (rows != undefined) {
			if (rows.length > 1) {
				showInfo("通用条件的值不唯一，请重新选择");
				return;
			}
			var data = rows[0];
			$('#brandName').val(data.brandNo + '→' + data.brandName);
			$('#brandNo').val(data.brandNo);
		}
	});
}
function recoveryZone() {
	var url = BasePath + '/condition_zone/list.json';
	var param = {
		moduleId : moduleId
	};
	ajaxRequest(url, param, function(ret) {
		var rows = ret.rows;
		if (rows != undefined) {
			if (rows.length > 1) {
				showInfo("通用条件的值不唯一，请重新选择");
				return;
			}
			var data = rows[0];
			$('#zoneName').val(data.zoneNo + '→' + data.zoneName);
			$('#zoneNo').val(data.zoneNo);
		}
	});
}

// 将Json对象换成map对象
function dataToMap(oriData, key, value) {
	var str = "";
	for ( var i = 0, length = oriData.length; i < length; i++) {
		str = str + "\"" + oriData[i][key] + "\":\"" + oriData[i][value] + "\"";
		if (i < length - 1) {
			str = str + ",";
		}
	}
	return eval("({" + str + "})");
};

/**
 * 品牌通用查询
 * 
 * @param fieldName
 *            品牌名称
 * @param fieldNo
 *            品牌编码
 */
function brandQuery(fieldName, fieldNo, fieldNameStr) {
	$('#' + fieldName).iptSearch({
		clickFn : function() {
			dgSelector({
				title : '选择品牌',
				width : 800,
				height : 500,
				isFrame : false,
				href : BasePath + '/search_dialog/brand',
				queryUrl : BasePath + '/brand/queryBrand',
				fn : function(data) {
					$('#' + fieldNo).val(data.brandNo);
					$('#' + fieldNameStr).val(data.brandName);
					$('#' + fieldName).val(data.brandNo + '→' + data.brandName);
				}
			});
		}
	});
};
// 重载ajaxRequest 加入同步异步参数
function ajaxRequestWithAsync(url, reqParam, async, callback) {
	$.ajax({
		async : async,
		type : 'POST',
		url : url,
		data : reqParam,
		cache : async,
		success : callback
	});
};

/**
 * 订单功能的导出 导出尺码横排（老版js 后面可以删除）
 * @param dataGridId
 *            表格ID
 * @param sysNo
 *            品牌库的ID
 * @param preColNames
 *            前面显示业务列 公用查询动态生成的参数
 * @param endColNames
 *            后面显示的业务列
 * @param sizeTypeFiledName
 *            sizeNo
 * @param excelTitle
 *            excel文件名
 */
function exportExcelBill(dataGridId, sysNo, preColNames, endColNames, sizeTypeFiledName, excelTitle) {
	var url = BasePath + '/initCache/do_export.htm';
	var $dg = $("#" + dataGridId + "");
	var tempTotal = -1;
	var paginationObj = $dg.datagrid('getPager').data("pagination");
	if (paginationObj) {
		tempTotal = paginationObj.options.total;
		if (tempTotal > 10000) {
			alert('导出文件数据超出限制最大数量10000!', 1);
			return;
		}
	}
	$("#exportExcelForm").remove();
	$("<form id='exportExcelForm'  method='post'></form>").appendTo("body");
	var fromObj = $('#exportExcelForm');
	var dataRow = $dg.datagrid('getRows');
	if (dataRow.length > 0) {
		if (tempTotal > dataRow.length) {
			var dataGridUrl = $dg.datagrid('options').url;
			var dataGridQueryParams = $dg.datagrid('options').queryParams;
			dataGridQueryParams.page = 1;
			dataGridQueryParams.rows = tempTotal;
			$.ajax({
				type : 'POST',
				url : dataGridUrl,
				data : dataGridQueryParams,
				cache : true,
				async : false, // 一定要
				success : function(resultData) {
					dataRow = resultData.rows;
				}
			});
		}
		fromObj.form('submit', {
			url : url,
			onSubmit : function(param) {
				param.sysNo = sysNo;
				param.preColNames = JSON.stringify(preColNames);
				param.endColNames = JSON.stringify(endColNames);
				param.sizeTypeFiledName = sizeTypeFiledName;
				param.fileName = excelTitle;
				param.dataRow = JSON.stringify(dataRow);
			},
			success : function() {
			}
		});
	} else {
		alert('数据为空，不能导出!', 1);
	}
};

var DateUtils = {
	/**
	 * 时间对象的格式化
	 * 
	 * date: 日期，必须为Date类型 format：格式化字符串，支持y年，M月，d日，H时，m分，s秒，S毫秒，q季度
	 */
	formatDate : function(date, format) {
		var result = format;
		var options = {
			"y+" : date.getFullYear(),
			"M+" : date.getMonth() + 1, // month
			"d+" : date.getDate(), // day
			"H+" : date.getHours(), // hour
			"m+" : date.getMinutes(), // minute
			"s+" : date.getSeconds(), // second
			"q+" : Math.floor((date.getMonth() + 3) / 3), // quarter
			"S+" : date.getMilliseconds()
		// millisecond
		};

		for ( var k in options) {
			if (new RegExp("(" + k + ")").test(result)) {
				var $1 = RegExp.$1;
				var value = options[k];

				result = result.replace($1, $1.length == 1 ? value : ("00" + value).substr(("" + value).length + 2 - $1.length));
			}
		}

		return result;
	},

	/**
	 * 字符串解析为日期类型，字符串的格式必须是：yyyy-MM-dd HH:mm:ss
	 */
	parseDate : function(dateStr) {
		return new Date(Date.parse(dateStr.replace(/-/g, "/")));
	},

	// 获取当前日期2014-5-14
	getCurrentDate : function() {
		var today = new Date();
		var day = today.getDate();
		var month = today.getMonth() + 1;
		var year = today.getFullYear();
		month = month < 10 ? "0" + month : month;
		day = day < 10 ? "0" + day : day;
		var date = year + "-" + month + "-" + day;
		return date;
	},

	// 获取当前日期2014-5-14 00:00:00
	getCurrentDateTime : function() {
		var today = new Date();
		var day = today.getDate();
		var month = today.getMonth() + 1;
		var year = today.getFullYear();
		month = month < 10 ? "0" + month : month;
		day = day < 10 ? "0" + day : day;
		var date = year + "-" + month + "-" + day + " 00:00:00";
		return date;
	},

	// 获取当前日期 20140514
	getCurrentDateStr : function() {
		var today = new Date();
		var day = today.getDate();
		var month = today.getMonth() + 1;
		var year = today.getFullYear();
		month = month < 10 ? "0" + month : month;
		day = day < 10 ? "0" + day : day;
		var date = year + month + day;
		return date;
	},

	// 获取当前日期 20140514
	getCurrentDateStrWithAMandPM : function() {
		var today = new Date();
		var day = today.getDate();
		var month = today.getMonth() + 1;
		var year = today.getFullYear();
		month = month < 10 ? "0" + month : month;
		day = day < 10 ? "0" + day : day;
		var AMandPM = "PM";
		if (today.getHours() < 12) {
			AMandPM = "AM";
		}
		var date = year + month + day + AMandPM;
		return date;
	},
	// 日期格式化 ftl datagrid 可以使用yyyy-MM-dd HH:mm:ss
	dateFormatter : function(value, rowData, rowIndex) {
		return DateUtils.formatDate(new Date(value), "yyyy-MM-dd HH:mm:ss");
	},
	// 日期格式化 ftl datagrid 可以使用 yyyy-MM-dd
	dateFormatterWithDate : function(value, rowData, rowIndex) {
		return DateUtils.formatDate(new Date(value), "yyyy-MM-dd");
	},
	
	//获取指定日期yyyy-MM-dd HH:mm:ss
	getDateTimeByParamDate : function(value) {
        if (value == null || value == '') {
            return '';
        }
        var dt;
	    if (value instanceof Date) {
	        dt = value;
	    } else {
	        dt = new Date(value);
	        if (isNaN(dt)) {
	            value = value.replace(/\/Date\((-?\d+)\)\//, '$1'); 
	            dt = new Date();
	            dt.setTime(value);
	        }
	    }
       return dt.format("yyyy-MM-dd hh:mm:ss");  
   },
   
   // 获取N天前的日期2015-1-12
	getDatesAgoDate : function(value) {
		var today = new Date();
		var day = today.getDate() - value;
		var month = today.getMonth() + 1;
		var year = today.getFullYear();
		month = month < 10 ? "0" + month : month;
		day = day < 10 ? "0" + day : day;
		var date = year + "-" + month + "-" + day;
		return date;
	}
	
};

function tansforArrayToMap(oriData) {
	var str = "";
	for ( var i = 0, length = oriData.length; i < length; i++) {
		str = str + "\"" + oriData[i].label + "\":\"" + oriData[i].text + "\"";
		if (i < length - 1) {
			str = str + ",";
		}
	}
	return eval("({" + str + "})");
};

function debug(name) {
	console.info(name);
}

/**
 * 树形结构tree+dataGrid选择器 
 * @param opts 
 * @returns {Boolean}
 */
function djSelector(opts) {
	var _url = opts.href || '';
	var _title = opts.title;
	var _w = opts.width || null;
	var _h = opts.height || null;
	var iframe = opts.isFrame;
	if (typeof iframe == "undefined") {
		iframe = true;
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
			//全局变量 tbgrid
			var tb = content.tbgrid;
			//全局变量 treeGrid
			var treeGrid = content.treeGrid;
			var resultObj = {};
			tb.datagrid({
				onDblClickRow : function(rowIndex, rowData) {
					//获取树形结构选中的节点
					var treeNode = treeGrid.tree("getSelected");
					if (typeof top.dgSelectorOpts.fn == "function") {
						resultObj.storeNo = rowData.storeNo;
						resultObj.storeName = rowData.shortName;
						resultObj.storeCode = rowData.storeCode;
						if(treeNode&&treeNode.no){
							resultObj.orderUnitNo = treeNode.no;
							resultObj.orderUnitName = treeNode.text;
							resultObj.orderUnitCode = treeNode.code;
							resultObj.companyNo = treeNode.companyNo;
						}else{
							resultObj.orderUnitNo = rowData.orderUnitNo;
							resultObj.orderUnitName = rowData.orderUnitName;
							resultObj.orderUnitCode = rowData.orderUnitCode;
							resultObj.companyNo = rowData.companyNo;
						}
						resultObj.shopNo = rowData.shopNo;
						resultObj.shopCode = rowData.code;
						resultObj.shopName = rowData.shortName;
						top.dgSelectorOpts.fn(resultObj);
					}
					win.close();
				}
			});
		}
	});
	return false;
};

/****************自定义工具函数*****************************/
//检查对象是否为空
function isNotBlank(obj) {
	if (!obj || typeof obj == 'undefined' || obj == '') {
		if('0' == obj){
			return true;
		}
		return false;
	}
	return true;
}

function isNotUndefined(obj) {
	if (typeof obj == 'undefined') {
		return false;
	}
	return true;
}

//计算函数
function accMul(arg1, arg2) {
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	} catch (e) {
	}
	try {
		m += s2.split(".")[1].length;
	} catch (e) {
	}
	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}


/********************add by dai.xw start 请勿修改*************************/
//设置表单控件的defaultValue值
function changeFormDefaultValue(form) {
	if(!form){
		form=document.forms["mainDataForm"];//这里默认是单据明细的表单 暂时写死
	}
	if(form){
		for (var i = 0; i < form.elements.length; i++) {
	        var element = form.elements[i];
	        var type = element.type;
	        if (type == "checkbox" || type == "radio") {
	        	element.defaultChecked=element.checked;
	        }else if (type == "hidden" || type == "password" || type == "text" || type == "textarea") {
	        	element.defaultValue=element.value;
	        }else if (type == "select-one" || type == "select-multiple") {
	            for (var j = 0; j < element.options.length; j++) {
	            	element.options[j].defaultSelected=element.options[j].selected;
	            }
	        }
	    }
	}
}

//比较表单控件的值 判读是否被修改
function formIsDirty(form) {
	if(!form){
		form=document.forms["mainDataForm"];//这里默认是单据明细的表单 暂时写死
	}
	if(form){
		for (var i = 0; i < form.elements.length; i++) {
	        var element = form.elements[i];
	        var type = element.type;
	        if (type == "checkbox" || type == "radio") {
	            if (element.checked != element.defaultChecked) {
	                return true;
	            }
	        }else if (type == "hidden" || type == "password" || type == "text" || type == "textarea") {
	            if (element.value != element.defaultValue) {
	                return true;
	            }
	        }else if (type == "select-one" || type == "select-multiple") {
	            for (var j = 0; j < element.options.length; j++) {
	                if (element.options[j].selected != element.options[j].defaultSelected) {
	                    return true;
	                }
	            }
	        }
	    }
	    return false;
	}
}

////发生在文档全部下载完毕的时候
//window.onload=function(){
//	changeFormDefaultValue(document.forms["mainDataForm"]);
//};

//window.onbeforeunload = function (e) {
//    e = e || window.event;
//    if (formIsDirty(document.forms["mainDataForm"])) {//这里默认是单据明细的表单 暂时写死
//        if (e) { // IE 和 Firefox
//            e.returnValue = "对不起，页面数据已做修改，尚未保存，确定要刷新或离开本页面？";
//        }
//        return "对不起，页面数据已做修改，尚未保存，确定要刷新或离开本页面？";// Safari浏览器
//    }
//};

////发生在窗口失去焦点的时候
//window.onblur=function(){
//	formDirty();
//};

//发生在窗口得到焦点的时候
//window.onfocus=function(){alert('得到焦点');};

//发生在错误发生的时候。它的事件处理程序通常就叫做“错误处理程序”(Error Handler)，用来处理错误
//window.onerror=function(e){
//	alert('js程序有异常，请清空缓存再刷新页面！');
//	return true;//忽略js异常
//};

/********************add by dai.xw end 请勿修改*************************/

//获取绝对路径
function getBasePath(url){
	if(url.indexOf("../")==0){
		return url;
	}
	return BasePath+url;
}




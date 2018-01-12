var searchPage = {};

// 订货会搜索框
/**
 * inputId:搜索框的Id searchFn:点击查询在列表中双击进行的操作函数 blurFn:鼠标离开事件函数
 */
searchPage.meetOrderDefine = function(inputId, searchFn, blurFn) {
	// 订货会查询
	$('#' + inputId)
			.iptSearch(
					{   width:104,
						readonly : false,
						clickFn : function() {
							dgSelector({
								title : '选择订货会',
								width : 700,
								height : 450,
								isFrame : false,
								href : BasePath
										+ '/base_attribute/listBillMeetOrderDefine',
								queryUrl : BasePath
										+ '/base_info/query_billmeetOrder_item.json?status=100&sort=bill_no&order=desc',
								fn : searchFn
							});
						}
					});
	$('#' + inputId).blur(function() {
		blurFn();
	});
};
//订货会商品清单查询
//重新定义清单弹出框，按照德群的供应商弹出框方式来定义 add by wll 20150212
searchPage.billMeetroder = function(opts) {
	 opts = opts || {};
  var initOpts = $.extend({},
  {
		   inputId : 'itemListNo',//弹出框id
		   reqParam : '',// 请求数据
		   readonly:false,// 是否仅读
		   disBlur:true, //是否启用离开事件
		   disIpt:false,//是否启启用弹出模式
		   initID:{} ,//需要填充的inputID
		   extendFn: function(data){},//拓展方法
        initInputMap:{} //初始化form表单中的数据
  },opts);
  var reqUrl =  BasePath
		+ '/base_info/list_dto.json?status=100&sort=BILL_NO&order=desc';
  if(initOpts.reqParam instanceof String){
 	 reqUrl += '&'+initOpts.reqParam;
  }
  if(!initOpts.disIpt){
 	 $('#' + initOpts.inputId)
			.iptSearch(
					{
						readonly : initOpts.readonly,
						clickFn : function() {
							dgSelector({
								title : '订货会商品清单查询',
								width : 700,
								height : 450,
								isFrame : false,
								initInputMap:initOpts.initInputMap,
								promptlyQuery:true,
								href : BasePath + '/base_info/billMeetorder',
								queryUrl : reqUrl,
								fn : function(data){
									var name = $('#'+initOpts.inputId).attr('name');
									$('#'+initOpts.inputId).val(data[name]);
									 for(var key in initOpts.initID){  
									    	  $('#'+initOpts.initID[key]).val(data[key]);
									  } 
									 var fctn = initOpts.extendFn;
									 fctn(data);
								}
							});
						}
					});
  }
	if(initOpts.disBlur){
		return;
	}
	$('#' + initOpts.inputId).blur(function() {
		var reqVal = $('#' + initOpts.inputId).val();
		if (null == reqVal || '' == reqVal.replace(/\s+/g,"")) {
			return;
		}
		var reqParams = null;
		var name = $('#'+initOpts.inputId).attr('name');
		ajaxRequest(reqUrl+'&'+name+'='+reqVal
				, reqParams, function(data) {
			if (data.total == 0 || data.rows.length != 1) {
				showWarn('请输入正确的清单编号');
				$('#' + initOpts.inputId).val('');
				return;
			}
			  $('#'+initOpts.inputId).val(data.rows[0][name]);
			 for(var key in initOpts.initID){  
			    	 $('#'+initOpts.initID[key]).val(data.rows[0][key]);
			     
			  }  
		});
	});

};

// 初始化临时商品编码查询文本框
searchPage.temporaryNo = function(inputId, searchFn, blurFn) {
	$('#' + inputId).iptSearch({
		readonly:false,
		clickFn : function() {
			dgSelector({
				title : '选择订货会货号',
				width : 600,
				height : 450,
				isFrame : false,
				href : BasePath + '/sample_info/listTemporary',
				queryUrl : BasePath + '/sample_info/list_sample.json?itemNoStatus=1',
				fn : searchFn
			});
		}

	});
};
// 初始化品牌
searchPage.brandNo = function(inputId, searchFn, blurFn) {
	$('#' + inputId).iptSearch(
			{
				readonly : false,
				clickFn : function() {
					dgSelector({
						title : '选择品牌',
						width : 600,
						height : 450,
						isFrame : false,
						readonly:false,
						href : BasePath + '/base_attribute/listBrand',
						queryUrl : BasePath
								+ '/brand/listByAuthorityUser.json?status=1',
						fn : searchFn
					});
				}
			});
};


// 初始化城市仓库(单选)
searchPage.Store = function(inputId, searchFn, blurFn) {
	$('#' + inputId).iptSearch({
		width:104,
		readonly : true,
		clickFn : function() {
			dgSelector({
				title : '选择城市仓',
				width : 600,
				height : 450,
				isFrame : false,
				href : BasePath + '/search_dialog/store',
				queryUrl : BasePath + '/store/list_store.json?sort=s.store_no',
				fn : searchFn
			});
		}
	});
};
// 初始化城市仓库(多选)
searchPage.StoreCheck = function(inputId, searchFn, blurFn) {
	$('#' + inputId).iptSearch({
		width : 104,
		readonly : true,
		clickFn : function() {
			dgSelector({
				title : '选择城市仓',
				width : 600,
				height : 450,
				isFrame : false,
				href : BasePath + '/search_dialog/storecheck',
				enableSaveButton : true,
				onSave : function(win) {
					if(searchFn()){
						win.close();
					}
				}
			});
		}
	});
};
// 初始供应商
searchPage.supplier = function(inputId, searchFn, blurFn) {
	$('#' + inputId)
			.iptSearch(
					{
						readonly : false,
						clickFn : function() {
							dgSelector({
								title : '选择供应商',
								width : 600,
								height : 450,
								isFrame : false,
								href : BasePath + '/search_dialog/supplier',
								queryUrl : BasePath
										+ '/supplier/listByAuthorityUser.json?status=1',
								fn : searchFn
							});
						}
					});
	$('#' + inputId).blur(function() {
		blurFn;
	});
};
//初始供应商 by jian.dq
/**inputId 初始化弹出框和编辑事件ID
 * reParam 查询参数 如supplierNo, fullName（1.是根据编号去查,2.根据名称查）
 * blurInitIds 初始化数据ID，OBJECT 
 */
searchPage.supplier1 = function(opts) {
	 opts = opts || {};
     var initOpts = $.extend({},
     {
		   inputId : 'supplierNo',//弹出框id
		   reqParam : '',// 请求数据
		   readonly:false,// 是否仅读
		   disBlur:true, //是否启用离开事件
		   disIpt:false,//是否启启用弹出模式
		   initID:{} ,//需要填充的inputID
           initInputMap:{} //初始化form表单中的数据
     },opts);
     var reqUrl =  BasePath
		+ '/supplier/list.json';
     if(initOpts.reqParam instanceof String){
    	 reqUrl += '&'+initOpts.reqParam;
     }
     if(!initOpts.disIpt){
    	 $('#' + initOpts.inputId)
			.iptSearch(
					{
						readonly : initOpts.readonly,
						clickFn : function() {
							dgSelector({
								title : '选择供应商',
								width : 700,
								height : 450,
								isFrame : false,
								initInputMap:initOpts.initInputMap,
								promptlyQuery:true,
								href : BasePath + '/search_dialog/supplier',
								queryUrl : reqUrl,
								fn : function(data){
									var name = $('#'+initOpts.inputId).attr('name');
									$('#'+initOpts.inputId).val(data[name]);
									 for(var key in initOpts.initID){  
									    	  $('#'+initOpts.initID[key]).val(data[key]);
									  }  	
								}
							});
						}
					});
     }
	if(initOpts.disBlur){
		return;
	}
	$('#' + initOpts.inputId).blur(function() {
		var reqVal = $('#' + initOpts.inputId).val();
		if (null == reqVal || '' == reqVal.replace(/\s+/g,"")) {
			return;
		}
		var reqParams = null;
		var name = $('#'+initOpts.inputId).attr('name');
		ajaxRequest(reqUrl+'&'+name+'='+reqVal
				, reqParams, function(data) {
			if (data.total == 0 || data.rows.length != 1) {
				showWarn('请输入正确的供应商编号');
				$('#' + initOpts.inputId).val('');
				return;
			}
			  $('#'+initOpts.inputId).val(data.rows[0][name]);
			 for(var key in initOpts.initID){  
			    	 $('#'+initOpts.initID[key]).val(data.rows[0][key]);
			     
			  }  
		});
	});
};
searchPage.categoryOldFrame = function(inputId, searchFn, blurFn,options) {
	var o = {
			readonly : false,
			clickFn : function() {
				dgSelector({
					title : '选择类别',
					width : 600,
					height : 450,
					isFrame : false,
					href : BasePath + '/base_attribute/categoryftl',
					queryUrl : BasePath + '/base_info/list_category.json?status=1&type=1',
					fn : searchFn
				});
			}
		};
	if(!!options && options.width){	
		o.width = options.width;
	}
	$('#' + inputId).iptSearch(o);
	$('#' + inputId).blur(function() {
		blurFn;
	});
};
searchPage.categorySampleFrame = function(inputId, searchFn, blurFn,options,initPam) {
	var url = BasePath + '/base_attribute/categoryftl';
	var queryUrl = BasePath + '/base_info/list_category.json?status=1';
	if(initPam){
		queryUrl += initPam;
	}
	var o = {
			readonly : false,
			clickFn : function() {
				dgSelector({
					title : '选择类别',
					width : 600,
					height : 450,
					isFrame : false,
					disEeditorId:options.disEeditorId,
					disClearId:options.disClearId,
					initInputMap:options.initInputMap,
					promptlyQuery:true,
					href : url,
					queryUrl : queryUrl,
					fn : searchFn
				});
			}
		};
	if(!!options && options.width){	
		o.width = options.width;
	}
	$('#' + inputId).iptSearch(o);
	$('#' + inputId).blur(function() {
		blurFn;
	});
};
searchPage.cateogrySeletor = function(searchFn, parentCategoryNo) {
	if (parentCategoryNo == undefined) {
		parentCategoryNo = '';
	}
	dgSelector({
		title : '选择类别',
		width : 600,
		height : 450,
		isFrame : false,
		href : BasePath + '/base_attribute/categoryftl',
		queryUrl : BasePath + '/base_info/list_category.json?status=1&type=1',
		fn : searchFn
	});
};
/**
 * 选择区域
 */
searchPage.zoneInfo = function(inputId, searchFn, blurFn) {
	$('#' + inputId).iptSearch({
		readonly : true,
		clickFn : function() {
			dgSelector({
				title : '选择区域',
				width : 600,
				height : 450,
				isFrame : false,
				href : BasePath + '/search_dialog/zone',
				queryUrl : BasePath + '/zone_info/list.json?status=1',
				fn : searchFn
			});
		}
	});
	$('#' + inputId).blur(function() {
		blurFn;
	});
};

searchPage.ajaxRequest = function(url, reqParam, isSync, callback) {
	$.ajax({
		type : 'POST',
		url : url,
		data : reqParam,
		async : isSync,
		cache : false,
		success : callback
	});
};

//初始化品牌
searchPage.brandForCheck = function(checkFn) {
	ygDialog({
		title : '选择品牌',
		href :  BasePath + '/search_dialog/brandcheck',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
//初始化供应商
searchPage.supplierForCheck = function(checkFn) {
	ygDialog({
		title : '选择供应商',
		href :  BasePath + '/search_dialog/suppliercheck',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
//初始化仓库(多选)
searchPage.storeForCheck = function(checkFn,param) {
	ygDialog({
		title : '选择城市仓',
		href :  BasePath + '/search_dialog/storecheck',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};


//初始化品牌
searchPage.brandForCheckAdmin = function(checkFn) {
	ygDialog({
		title : '选择品牌',
		href :  BasePath + '/search_dialog/brandcheckAdmin',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
//初始化供应商
searchPage.supplierForCheckAdmin = function(checkFn) {
	ygDialog({
		title : '选择供应商',
		href :  BasePath + '/search_dialog/suppliercheckAdmin',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
//初始化仓库(多选)
searchPage.storeForCheckAdmin = function(checkFn,param) {
	ygDialog({
		title : '选择城市仓',
		href :  BasePath + '/search_dialog/storecheckAdmin',
		width : 600,
		height : 450,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
//查询供应商样品信息,多选
searchPage.listSmapleInfoForCheck = function(checkFn,param) {
	ygDialog({
		title : '订货会样品信息',
		href :  BasePath + '/search_dialog/itemList?meetorderNo='+param,
		width : 760,
		height : 550,
		buttons : [ {
			text : '确定',
			iconCls : 'icon-save',
			handler : function(dialog) {
				checkFn();
				dialog.close();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function(dialog) {
				dialog.close();
			}
		} ]
	});
};
/**
 * 类别查询combo_grid_iptSearch组合查询 add by jian.dq
 * @param inputId
 * @param func
 * @param initParam初始化参数
 */
function categoryComboGridIpt(inputId,func,initParam){
	 opts = initParam || {};
	   var initOpts = $.extend({},
	   {
			   inputId : 'categoryNo',//弹出框id
			   reqParam : '',// 请求数据
			   readonly:false,// 是否仅读
			   disBlur:true, //是否启用离开事件
			   disIpt:false,//是否启启用弹出模式
			   initID:{} ,//需要填充的inputID
			   disEeditorId:[],//禁用按钮
			   disClearId:[],//清理数据时，保留编辑框中数据
			   initInputMap:{} //初始化form表单中的数据
	   },opts);
	   var reqUrl =  BasePath
			+ '/base_info/list_category.json?status=1';
	   if(initOpts.reqParam instanceof String){
	  	 reqUrl += '&'+initOpts.reqParam;
	   }else if(initOpts.reqParam != undefined && typeof initOpts.reqParam == "string"){
		   reqUrl += '&'+initOpts.reqParam;
	   }
	   
	$('#'+inputId).combogrid({    
	    panelWidth:300,    
	    loadMsg:'正在加载....',
	    delay: 500,    
	    mode: 'remote',  
	    idField:'categoryNo',
	    textField:'name',
	    url:reqUrl,    
	    rownumbers:'false',
	    columns:[[    
	        {field:'categoryNo',title:'类别编码',width:70},    
	        {field:'name',title:'类别名称',width:70},    
	        {field:'levelid',title:'等级',width:40},  
	        {field:'opcode',title:'特征码',width:50}   
	    ]],
	    onBeforeLoad:function(param){
	    	if(null != initParam.initInputMap && typeof initParam.initInputMap == 'object'){
	    		for(var key in initParam.initInputMap){
					if(initParam.initInputMap.hasOwnProperty(key)){
						var val = initParam.initInputMap[key];
						if(val != undefined && typeof val == "string"){
								return true;
						}else if(val != undefined && typeof val == "object"){
							var d = val.attr('class');
							var valueStr = "";
							var nameStr ="";
							if(d.indexOf('combobox') != -1){
								valueStr = val.combobox('getValue');
								nameStr = val.attr('comboname');
							}else{
								valueStr = val.val();
								nameStr = val.attr('name');
							}
							if(null == valueStr || valueStr == ''){
							}else{
								param[nameStr] = valueStr;
							}
							
						}
						
					}
				}
	    	}
	    	
	    },
	    onClickRow:function(rowIndex,rowData){
	    	func(rowData);
	    }
	}); 
	
	   
	  	 $('#' + inputId)
				.iptSearch(
						{
							readonly : initOpts.readonly,
							clickFn : function() {
//								if(initParam.showmsg){
//									var str1 = initParam.showmsg.combobox('getValue');
//									if(!str1){
//										showWarn('品牌不能为空!');
//										return;
//									}
//									
//								}
								dgSelector({
									title : '选择类别',
									width : 700,
									height : 450,
									isFrame : false,
									initInputMap:initOpts.initInputMap,
									disEeditorId:initOpts.disEeditorId,
									disClearId:initOpts.disClearId,
									promptlyQuery:true,
									href : BasePath + '/search_dialog/Category',
									queryUrl : reqUrl,
									fn : function(data){
										//func(rowIndex,rowData);
										if (typeof func == "function") {
											func(data);
											return;
										}
										return;
										var name = $('#'+initOpts.inputId).attr('name');
										$('#'+initOpts.inputId).val(data[name]);
										 for(var key in initOpts.initID){  
										    	  $('#'+initOpts.initID[key]).val(data[key]);
										  }  	
									} 
								});
							}
						});
	   
}
/**
 * combo_datagrid_iptSearch组合查询 by he.sz
 * 供应商
 */
function supplierNoComboGridIpt(inputId,func,initParam){
	if(initParam == undefined || initParam == ''){
		initParam = {};
	}
	 var reqUrl =  BasePath
		+ '/supplier/list.json';
	if(typeof initParam.reqParam == 'string'){
		 reqUrl += '&'+initOpts.reqParam;
	}
	var isInterrupt = false || initParam.isInterrupt;
	$('#'+inputId).combogrid({    
	    panelWidth:450,    
	    loadMsg:'正在加载....',
	    delay: 500,    
	    mode: 'remote',  
	    idField:'supplierNo',
	    textField:'supplierNo',
	    url:reqUrl,    
	    columns:[[    
	    	{field : 'supplierNo',title : '供应商编号',width : 120,align:'left',hidden:true},
	        {field:'code',title:'供应商编码',width:100},    
	        {field:'opcode',title:'供应商特征码',width:100},    
	        {field:'shortName',title:'供应商名称',width:220},    
	        {field:'fullName',title:'供应商全称',width:100,hidden:true}    
	    ]],
	    onBeforeLoad:function(param){
	    	if(null != initParam.initInputMap && typeof initParam.initInputMap == 'object'){
	    		for(var key in initParam.initInputMap){
					if(initParam.initInputMap.hasOwnProperty(key)){
						var val = initParam.initInputMap[key];
						if(val != undefined && typeof val == "string"){
								return true;
						}else if(val != undefined && typeof val == "object"){
							var d = val.attr('class');
							var valueStr = "";
							var nameStr ="";
							if(d == undefined || d == null || d == ''){
								valueStr = val.val();
								nameStr = val.attr('name');
							} else if(d.indexOf('combobox') != -1){
								valueStr = val.combobox('getValue');
								nameStr = val.attr('comboname');
							}else{
								valueStr = val.val();
								nameStr = val.attr('name');
							}
							if(null == valueStr || valueStr == ''){
								return false;
							}else{
								param[nameStr] = valueStr;
							}
						}
						
					}
				}
	    	}else{
	    		if(isInterrupt){
	    			return false;
	    		}
	    	}
	    	
	    },
	    onClickRow:function(rowIndex,rowData){
	    	//func(rowIndex,rowData);
	    	if (typeof func == "function") {
				func(rowIndex,rowData);
				return;
			}
	    }
	}); 
	
	$('#' + inputId).iptSearch({
		readonly : true,
		clickFn : function() {
			if(isInterrupt){
				if(null != initParam.initInputMap && typeof initParam.initInputMap == 'object'){
		    		for(var key in initParam.initInputMap){
						if(initParam.initInputMap.hasOwnProperty(key)){
							var val = initParam.initInputMap[key];
							if(val != undefined && typeof val == "object"){
								var d = val.attr('class');
								var valueStr = "";
								if(d.indexOf('combobox') != -1){
									valueStr = val.combobox('getValue');
								}else{
									valueStr = val.val();
								}
								if(null == valueStr || valueStr == ''){
									return;
								}
							}
							
						}
					}
		    	}
    		}
			dgSelector({
				title : '选择供应商',
				width : 700,
				height : 450,
				isFrame : false,
				initInputMap:initParam.initInputMap,
				promptlyQuery:true,
				href : BasePath + '/search_dialog/supplier',
				queryUrl : reqUrl,
				fn : function(rowData,rowIndex){
					$('#'+inputId).combogrid('setValue',rowData.supplierNo);
					if (typeof func == "function") {
						func(rowIndex,rowData);
						return;
					}
				}
			});
		}
	});
}
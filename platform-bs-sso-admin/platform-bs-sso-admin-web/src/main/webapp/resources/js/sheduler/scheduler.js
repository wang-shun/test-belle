 var url_common;
var con_data = [ {
	label : '秒',
	value : 'second'
}, {
	label : '分',
	value : 'minute'
} ];
var group_data = [ {
	triggerGroupName : 'DEFAULT'
} ];

function add(group) {
	$.extend($.fn.validatebox.defaults.rules, {
		isExist : {
			validator : function(value, param) {
				var name = $(param[0]).val();
				var group = $(param[1]).nextAll().find("input[name='group']")
						.val();
				if (name == "" || group == "") {
					// 参数不对,直接返回，不提示
					return true;
				}
				return isExist(group, name);
			},
			message : '该触发器已存在'
		}
	});
	$("#save").unbind("click");
	$("#save").bind("click", function() {
		add_save(group);
	});
	// 编辑时禁用
	$('#fm2').form('clear');
	$('#fm3').form('clear');
	$('#name1').attr("readonly", false);
	$('#group1').combobox("select", group);
	$('#group1').combobox("readonly", true);
	$('#group1').combobox("enable");
	$('#isCanParallel').combobox("enable");
	$('#name2').attr("readonly", false);
	$('#group2').combobox("select", group);
	$('#group2').combobox("readonly", true);
	$('#group2').combobox("enable");
	$('#tt').tabs({
		onSelect : function(title) {
			if (title == "表达式") {
				$('#fm2').form('enableValidation');
				$('#fm3').form('disableValidation');
			} else {
				$('#fm2').form('disableValidation');
				$('#fm3').form('enableValidation');
			}
		}
	});
	$('#dlg').dialog('open').dialog('setTitle', '添加调度');
	$('#tt').tabs('enableTab', 0);
	$('#tt').tabs('enableTab', 1);
}

function del(name, group) {
	var data = {};
	data.name = name;
	data.group = group;
	data._method = "DELETE";

	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
		if (r) {
			$.ajax({
				url : url_common,
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
						$("#parentJobIDs").combotree('reload');
					} else {
						$.messager.alert('警告', '删除失败');
					}
				}
			});
		}
	});
}

function isExist(group, name) {
	var url_isExist = BasePath + '/scheduler/is_exist/' + group + "/" + name
			+ "?date=" + new Date();
	var flag=false;
	$.ajax({
		url : url_isExist,
		type : 'get',
		async : false,
		success : function(d) {
			if (d.isExist == 'false') {
				// 不存在就不提示
				flag = true;
			} else {
				flag = false;
			}
		}
	});
	return flag;
}

function pause(name, group) {
	var data = {};
	data.name = name;
	data.group = group;
	data._method = "put";

	$.messager.confirm('确认', '您确认想要暂停触发器吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/pause',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
					} else {
						$.messager.alert('警告', '暂停失败');
					}
				}
			});
		}
	});
}

function resume(name, group) {
	var data = {};
	data.name = name;
	data.group = group;
	data._method = "put";

	$.messager.confirm('确认', '您确认想要恢复触发器吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/resume',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
					} else {
						$.messager.alert('警告', '恢复失败');
					}
				}
			});
		}
	});
}

function add_save(group) {
	var tabs_name = $('#tt').tabs('getSelected').attr('name');

	var flag = false;
	var data;
	var url;
	if (tabs_name == 'expression') {
		flag = $("#fm2").form('validate');
		data = $('#fm2').serialize();
		url = BasePath + '/scheduler/cron';
	} else if (tabs_name == 'timing') {
		flag = $("#fm3").form('validate');
		data = $('#fm3').serialize();
		url = BasePath + '/scheduler/simple';
	} else {
		return;
	}
	save_data(group, flag, url, data);
}

function edit(name, group) {
	// 编辑不验证
	$.extend($.fn.validatebox.defaults.rules, {
		isExist : {
			validator : function(value, param) {
				return true;
			},
			message : ''
		}
	});

	$("#save").unbind("click");
	$("#save").bind("click", function() {
		edit_save(group);
	});

	$.ajax({
		url : url_common + "/" + group + "/" + name + "?date=" + new Date(),
		type : 'get',
		success : function(d) {
			if (d.success == "true") {
				$('#dlg').dialog('open').dialog('setTitle', '修改调度');
				// 选中选项卡
				// 填充数据
				if (d.data.triggerType == "cron") {
					$('#tt').tabs('enableTab', 0);
					$('#tt').tabs('disableTab', 1);
					$("#tt").tabs('select', '表达式');
					$('#fm2').form('enableValidation');
					$('#fm3').form('disableValidation');
					$("#fm2").form('clear');
					$("#fm2").form('load', d.data);
					if (!!d.data.triggerName) {
						$("#name1").val(d.data.triggerName);
					}
					if (!!d.data.triggerGroup) {
						$("#group1").combobox('select', d.data.triggerGroup);
					} else {
						// $('#group1').parent().find("[class='combo-text
						// validatebox-text']").val("");
						$('#group1').combobox('select', d.data.group);
					}
					$("#cron_url").attr('value', d.data.dataInfos.url);
					$("#cron_mbeanurl")
							.attr('value', d.data.dataInfos.mbeanurl);
					$("#cron_remark").attr('value', 
							d.data.dataInfos.remark);
					$("#cron_timeout").numberbox('setValue',
							d.data.dataInfos.timeout);
					$("#cron_timeout").numberbox('validate');
					$("#jobNumber").attr('value', d.data.dataInfos.jobNumber);
					$("#checkInTimeOut").numberbox('setValue',
							d.data.dataInfos.checkInTimeOut);
					$("#tryTime").numberbox('setValue',
							d.data.dataInfos.retryTime);
					$("#ID").attr('value', d.data.dataInfos.ID);
					if (d.data.dataInfos.isCanParallel == 0) {
						$('#isCanParallel').combobox('select', "不可并行");
						$('#isCanParallel').combobox('setValue', 0);
					} else {
						$('#isCanParallel').combobox('select', "可并行");
					    $('#isCanParallel').combobox('setValue', 1);
					}
					$("#range").attr('value', d.data.dataInfos.range);
					$("#fm2").form('validate');
					// 编辑时禁用
					$('#name1').attr("readonly", true);
					$('#group1').combobox("readonly", true);
					$('#group1').combobox("disable");
					$('#isCanParallel').combobox("disable");
				} else {
					$('#tt').tabs('disableTab', 0);
					$('#tt').tabs('enableTab', 1);
					$("#tt").tabs('select', '定时运行');
					$('#fm2').form('disableValidation');
					$('#fm3').form('enableValidation');

					$("#fm3").form('clear');
					$("#fm3").form('load', d.data);
					if (!!d.data.triggerName) {
						$("#name2").val(d.data.triggerName);
					}
					if (!!d.data.triggerGroup) {
						$("#group2").combobox('select', d.data.triggerGroup);
					} else {
						// $('#group2').parent().find("[class='combo-text
						// validatebox-text']").val("");
						$('#group2').combobox('select', d.data.group);
					}
					$("#timing_url").attr('value', d.data.dataInfos.url);
					$("#timing_mbeanurl").attr('value',
							d.data.dataInfos.mbeanurl);
					$("#timing_remark").attr('value',
							d.data.dataInfos.remark);
					$("#timing_timeout").numberbox('setValue',
							d.data.dataInfos.timeout);
					$("#timing_timeout").numberbox('validate');
					$("#fm3").form('validate');
					// 编辑时禁用
					$('#name2').attr("readonly", true);
					$('#group2').combobox("readonly", true);
					$('#group2').combobox("disable");
				}
				//处理依赖jobs
				if (d.parentJobs != undefined && d.parentJobs != null){
					//循环list
					var isExists = false;
					var comboData = combotreeData;
					if (comboData != undefined && comboData != null) {
						for(var i=0; i<comboData.length; i++){
							if (comboData[i].children != null)
							{
								for(var j=0;j<comboData[i].children.length;j++){
									if (comboData[i].children[j].id == d.parentJobs[0]){
										isExists = true;
										break;
									}
								}
							}
							if (isExists) {
								break;
							}
						}
					}
					if (isExists) {
						$("#parentJobIDs").combotree("setValues",d.parentJobs);
					}
				}
			} else {
				$.messager.alert('警告', '查询失败:' + d.message);
			}
		}
	});
}

function edit_save(group) {
	// 更改url
	var tabs_name = $('#tt').tabs('getSelected').attr('name');
	var flag = false;
	var data;
	var url;
	if (tabs_name == 'expression') {
		$('#group1').combobox("enable");
		$('#isCanParallel').combobox("enable");
		flag = $("#fm2").form('validate');
		data = $('#fm2').serialize();
		data += "&_method=put";
		url = BasePath + '/scheduler/cron';
	} else if (tabs_name == 'timing') {
		$('#group2').combobox("enable");
		flag = $("#fm3").form('validate');
		data = $('#fm3').serialize();
		data += "&_method=put";
		url = BasePath + '/scheduler/simple';
	} else {
		return;
	}
	save_data(group, flag, url, data);
}

function save_data(group, flag, url, data) {	
	if (flag) {
		$.ajax({
			url : url,
			type : 'post',
			data : data,
			success : function(d) {
				if (d.success == "true") {
					$('#dlg').dialog('close');
					try {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
						//重新加载tree
						$("#parentJobIDs").combotree('reload');
					} catch (e) {
					}
				} else {
					$.messager.alert('警告', '保存失败:' + d.message);
				}
			},
			statusCode : {
				500 : function() {
					$.messager.alert('警告', '保存错误，编号500,请稍后重试');
				}
			}
		});
	}
}

function remotePause() {
	var row = $("#scheduler").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择暂停的远程任务');
		return;
	}
	var data = {};
	data.name = row.triggerName;
	data.group = row.triggerGroup;

	$.messager.confirm('确认', '您确认想要暂停远程任务吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/pauseJob',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#scheduler').datagrid('options').method = "post";
						$('#scheduler').datagrid('reload');
					} else {
						$.messager.alert('警告', '暂停失败');
					}
				}
			});
		}
	});
}

function remoteResume() {
	var row = $("#scheduler").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择恢复的远程任务');
		return;
	}
	var data = {};
	data.name = row.triggerName;
	data.group = row.triggerGroup;

	$.messager.confirm('确认', '您确认想要恢复远程任务吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/resumeJob',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#scheduler').datagrid('options').method = "post";
						$('#scheduler').datagrid('reload');
					} else {
						$.messager.alert('警告', '恢复失败');
					}
				}
			});
		}
	});
}

function remoteStop() {
	var row = $("#scheduler").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择停止的远程任务');
		return;
	}
	var data = {};
	data.name = row.triggerName;
	data.group = row.triggerGroup;

	$.messager.confirm('确认', '您确认想要停止远程任务吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/stopJob',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#scheduler').datagrid('options').method = "post";
						$('#scheduler').datagrid('reload');
					} else {
						$.messager.alert('警告', '停止失败');
					}
				}
			});
		}
	});
}

var url;
function findLogs(name, group) {
	url = BasePath + '/scheduler/findlogs?triggerName=' + name
			+ '&triggerGroup=' + group;
	$('#logsWin')
			.window(
					{
						width : 721,
						height : 315,
						title : "远程任务日志",
						modal : true,
						minimizable : false,
						content : '<iframe src="'
								+ url
								+ '" frameborder="0" border="0" marginwidth="0" marginheight="0" scrolling="auto" style="width:100%" height="100%"/>'
					});
	// $('#logsWin').window('refresh', url);
}

function resemeAll(group) {
	$.messager.confirm('确认', '您确认想要恢复整组所有的调度吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/reseme_all_trigger',
				type : 'post',
				data : {
					"group" : group
				},
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
					} else {
						$.messager.alert('警告', '恢复失败');
					}
				}
			});
		}
	});
}

function pauseAll(group) {
	$.messager.confirm('确认', '您确认想要停止整组所有的调度吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/pause_all_trigger',
				type : 'post',
				data : {
					"group" : group
				},
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
					} else {
						$.messager.alert('警告', '停止失败');
					}
				}
			});
		}
	});
}

function invokeState(name,group){
	$.ajax({
		url : BasePath + '/scheduler/invoke_state.json',
		type : 'post',
		data : {
			"group" : group,
			"name":name
		},
		success : function(d) {
			if(d.success=="false"){
				$.messager.alert('警告','服务状态调用失败,失败原因:'+d.message);
			}else if(d.success=="true"){
				$.messager.alert('提醒','服务状态调用成功,当前状态:'+triggerBizStatus[d.state]);
			}else{
				$.messager.alert('警告','调用状态失败,请检查服务!!');
			}
		},
		statusCode: {500: function() {
			$.messager.alert('警告','服务状态调用失败');
		}}
	});
}

function invokeService(name,group){
	$('#handSchedulerDialog').dialog('open');
	$('#executeTriggerName').val(name);
	$('#executeGroupName').val(group);
	var currentDate = new Date();
	currentDate = getSmpFormatDate(currentDate,false)
	var dates = currentDate.split('-');
	var today = new Date().setFullYear(+dates[0], +dates[1]-1, +dates[2]);//第二个参数减1因为月份是0~11
	var yesterday = new Date(today - 24 * 60 * 60 * 1000);
	var startTime = getSmpFormatDate(yesterday,false) + " 00:00:00";
	var endTime = currentDate + " 00:00:00";
	$('#executeStartTime').val(startTime);
	$('#executeEndTime').val(endTime);
}

function submitInvokeService() {
	var name = $('#executeTriggerName').val();
	var group = $('#executeGroupName').val();
	var startTime = $('#executeStartTime').val();
	var endTime = $('#executeEndTime').val();
	var concurrentNum = $('#concurrentNum').val();
	if (startTime == '' || endTime == '') {
		$.messager.alert('警告','开始时间或结束时间不能为空！');
		return false;
	}
	var start=new Date(startTime.replace("-", "/").replace("-", "/"));
	var end=new Date(endTime.replace("-", "/").replace("-", "/"));
	if(end<start){
		$.messager.alert('警告','结束时间不能小于开始时间！');
		return false;
	}
	$.ajax({
		url : BasePath + '/scheduler/invoke_service.json',
		type : 'post',
		data : {
			"group" : group,
			"name":name,
			"startTime":startTime,
			"endTime":endTime
		},
		success : function(d) {
			if(d.success=="false"){
				$.messager.alert('警告','服务调用失败,失败原因:'+d.message);
			}else if(d.success=="true"){
				$.messager.alert('提醒','服务调用成功,人工的触发Execute');
			}else{
				$.messager.alert('警告','调用失败,请检查服务!!');
			}
		},
		statusCode: {500: function() {
			$.messager.alert('警告','服务调用失败');
		}}
	});
	$('#handSchedulerDialog').dialog('close');
	showSuc(name + "任务正在执行中......");
}

function submitOneKeyInvokeService() {
	var name = $('#executeTriggerName').val();
	var group = $('#executeGroupName').val();
	var startTime = $('#executeStartTime').val();
	var endTime = $('#executeEndTime').val();
	var concurrentNum = $('#concurrentNum').val();
	if (startTime == '' || endTime == '') {
		$.messager.alert('警告','开始时间或结束时间不能为空！');
		return false;
	}
	var start=new Date(startTime.replace("-", "/").replace("-", "/"));
	var end=new Date(endTime.replace("-", "/").replace("-", "/"));
	if(end<start){
		$.messager.alert('警告','结束时间不能小于开始时间！');
		return false;
	}
	$.ajax({
		url : BasePath + '/scheduler/invoke_onekey_service.json',
		type : 'post',
		data : {
			"group" : group,
			"name":name,
			"startTime":startTime,
			"endTime":endTime
		},
		success : function(d) {
			if(d.success=="false"){
				$.messager.alert('警告','服务调用失败,失败原因:'+d.message);
			}else if(d.success=="true"){
				$.messager.alert('提醒','服务调用成功,人工的触发Execute');
			}else{
				$.messager.alert('警告','调用失败,请检查服务!!');
			}
		},
		statusCode: {500: function() {
			$.messager.alert('警告','服务调用失败');
		}}
	});
	$('#handSchedulerDialog').dialog('close');
	showSuc(name + "任务正在执行中......");
}
function invokeLogs(name,group){
	$.ajax({
		url : BasePath + '/scheduler/invoke_logs.json',
		type : 'post',
		data : {
			"group" : group,
			"name":name
		},
		success : function(d) {
			if(d.success=="false"){
				$.messager.alert('警告','服务调用失败,失败原因:'+d.message);
			}else if(d.success=="true"){
				var m="";
				var logs=eval(d.logs);
				if (logs != null) {
					m+="<span style='color:red'>共拉回"+logs.length+"条</span><br/><hr/>";
					$.each(logs,function(i,l){
						m+="名称:"+l.triggerName+"<br/>分组:"+l.groupName+"<br/>类型:"+triggerBizStatus[l.type]+"<br/>创建时间:"+getFormatDateByLong(l.gmtDate, 'yyyy-MM-dd hh:mm:ss')+"<br/>备注:"+l.remark+"<br/><hr/>";
					});
			    } else {
			    	m+="<span style='color:red'>共拉回"+ "0" +"条</span><br/><hr/>";
			    }
				var d=$('<div/>').appendTo("body");
				$(d).dialog({width:400,height:200,title:"服务日志",content:m,modal:true,maximizable:true});
			}else{
				$.messager.alert('警告','调用失败,请检查服务!!');
			}
		},
		statusCode: {500: function() {
			$.messager.alert('警告','服务调用失败');
		}}
	});
}

function delAll(group) {
	$.messager.confirm('确认', '您确认想要删除整组所有的调度吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/all',
				type : 'post',
				data : {
					"group" : group,
					"_method" : "delete"
				},
				success : function(d) {
					if (d.success == "true") {
						$('#ddv-' + group).datagrid('options').method = "post";
						$('#ddv-' + group).datagrid('reload');
					} else {
						$.messager.alert('警告', '停止失败');
					}
				}
			});
		}
	});
}

var intervalId;
$(function($) {
	$.ajax({
		url : BasePath + '/scheduler_trigger_group/list.json',
		type : 'get',
		async : false,
		success : function(d) {
			group_data = d.rows;
		}
	});
	url_common = BasePath + '/scheduler';
	$("#remotePause").click(remotePause);
	$("#remoteResume").click(remoteResume);
	$("#remoteStop").click(remoteStop);
	$("#logsCheck").click(findLogs);

	// 绑定分组
	$('#group1').combobox({
		onSelect : function(param) {
			$('#name1').focus();
		}
	});
	$('#group2').combobox({
		onSelect : function(param) {
			$('#name2').focus();
		}
	});
	$('#group1').combobox({
		data : group_data
	});
	$('#group2').combobox({
		data : group_data
	});
});

$(window).resize(function(){ 
	 $('#scheduler').datagrid('resize', {
        width:document.body.clientWidth,
        height:document.body.clientHeight
    });
});

// 导出
function exportScheduler(group){
	exportExcelBaseInfo('ddv-'+group,'/scheduler/do_export','调度列表','0','');
};

//导入Excel 
function importScheduler() {
	var templateName = '';
	templateName = 'CronTrigger导入.xlsx';
	$.importExcel.open({
		submitUrl: BasePath + "/scheduler/do_import",
		templateName:templateName,
		success:function(data, status){
			//price_quotation_control.doSearch();
			showSuc("数据导入成功！");
		},
		error:function(e){
			showWarn(e);
		}
	});
};

//导入依赖Excel 
function importRely() {
	var templateName = '';
	templateName = '依赖导入.xlsx';
	$.importExcel.open({
		title:"依赖导入",
		downloadText :'下载依赖模板',
		submitUrl: BasePath + "/scheduler/do_import_rely",
		templateName:templateName,
		success:function(data, status){
			//price_quotation_control.doSearch();
			showSuc("数据导入成功！");
		},
		error:function(e){
			showWarn(e);
		}
	});
};

var combotreeData = null;
$(function(){
    $("#parentJobIDs").combotree({
           panelWidth:300,  
           panelHeight:180, 
           url:BasePath+'/scheduler/jobtree.json',//获取树的信息
           multiple:true,//设置是否多选
           onLoadSuccess: function (row, data) {
			   combotreeData = data;
              $("#parentJobIDs").combotree('tree').tree("collapseAll");//expandAll展开，collapseAll收缩
           },
           onlyLeafCheck : true //只有叶子节点可选属性 
    });
    
    //combotree可编辑，自定义模糊查询  
    $.fn.combotree.defaults.editable = true;  
    $.extend($.fn.combotree.defaults.keyHandler,{  
        up:function(){  
            console.log('up');  
        },  
        down:function(){  
            console.log('down');  
        },  
        enter:function(){  
            console.log('enter');  
        },  
        query:function(q){  
            if($.trim(q) == ''){
                $("#parentJobIDs").combotree('tree').tree("collapseAll");//expandAll展开，collapseAll收缩
            }else{
                $("#parentJobIDs").combotree('tree').tree("expandAll");//expandAll展开，collapseAll收缩
            }
            var param = q.split(',');
            q = param[param.length - 1];
            
            var t = $(this).combotree('tree');  
            var nodes = t.tree('getChildren');  
            for(var i=0; i<nodes.length; i++){  
                var node = nodes[i];  
                if (node.text.indexOf(q) >= 0){  
                    //获取父节点
                    var parent = t.tree('getParent',node.target);
                    if(parent != null){
                        $(parent.target).show();
                    }else{
                        //完美匹配父节点时,展示所有子节点
                        //if(node.text == q){
                        //}
                    }
                    $(node.target).show();  
                } else {  
                    $(node.target).hide();  
                }  
            }  
            var opts = $(this).combotree('options');  
            if (!opts.hasSetEvents){  
                opts.hasSetEvents = true;  
                var onShowPanel = opts.onShowPanel;  
                opts.onShowPanel = function(){  
                    var nodes = t.tree('getChildren');  
                    for(var i=0; i<nodes.length; i++){  
                        $(nodes[i].target).show();  
                    }  
                    onShowPanel.call(this);  
                };  
                $(this).combo('options').onShowPanel = opts.onShowPanel;  
            }  
        }  
    });  
});



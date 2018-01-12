var biz_config = {};

biz_config.add = function() {
	$('#fm').form("clear");
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", biz_config.add_save);
	$("#id").val('');
	$('#bizCode').removeAttr('disabled');
	$('#dlg').dialog({
		title : '添加业务配置',
		modal:true,
	}).dialog('open');
	//在窗口一打开的时候就校验，显示出必填项
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
};

biz_config.add_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var data = $("#fm").serialize();
	var url = BasePath + '/biz_config/add';
	biz_config.save_data(url, data);
};

biz_config.edit = function() {
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", biz_config.edit_save);
	$('#bizCode').attr('disabled','disabled');
	
	var row = $("#biz_config_list").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择修改的行');
		return;
	}

	$("#id").val(row.id);
	$("#bizCode").val(row.bizCode);
	$("#bizName").val(row.bizName);
	$("#email").val(row.email);
	$("#verifyUserPwdUrl").val(row.verifyUserPwdUrl);
	$("#delUserUrl").val(row.delUserUrl);
	$("#loginUrl").val(row.loginUrl);
	$("#syncUserInfoUrl").val(row.syncUserInfoUrl);
	$("#description").val(row.description);
	$("#bizSecret").val(row.bizSecret);

	$('#dlg').dialog({
		title : '编辑业务配置',
		modal:true,
		onClose : function() {
			$('#fm').form('disableValidation');
		}
	}).dialog('open');
	var flag = $("#fm").form('validate');
};

biz_config.edit_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var data = $('#fm').serialize();
	var url = BasePath + '/biz_config/update';
	biz_config.save_data(url, data);
};

// delete one or many
biz_config.del = function() {
	var rows = $("#biz_config_list").datagrid('getSelections');
	if (rows == null || rows.length == 0) {
		$.messager.alert('警告', '请选择要删除的行');
		return;
	}
	$.messager.confirm('确认对话框', '即将删除选中的记录，是否继续？', function(r) {
		if (r) {
			// 将多行的id取出合并为一个字符串，中间用逗号","隔开
			var ids = '';
			for (var i = 0; i < rows.length; i++) {
				if (i == 0) {
					ids = rows[i].id;
				} else {
					ids += ',' + rows[i].id;
				}
			}
			var data = {
				ids : ids
			};
			var url = BasePath + '/biz_config/del';
			biz_config.save_data(url, data);
		}
	});
};

biz_config.save_data = function(url, data) {
	$.ajax({
		url : url,
		type : 'post',
		data : data,
		success : function(d) {
			if (d.status != 0) {
				$.messager.alert('警告', '保存错误，' + d.value);
				return;
			}
			$('#dlg').dialog('close');
			$('#biz_config_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '保存错误，编号500,请稍后重试');
			}
		}
	});
};

biz_config.iniBtnSearch = function() {
};

biz_config.btnSearch = function() {
	var myQueryParams = {};
	var userId = $.trim($("#userId").val());
	myQueryParams.userId = userId;

	var queryCondition = $.trim($("#queryCondition").val());
	if (queryCondition != '') {
		myQueryParams.queryCondition = queryCondition;
	}

	$("#biz_config_list").datagrid({
		queryParams : myQueryParams
	}).datagrid('reload');
};

biz_config.bindMethod = function(){
	$("#queryCondition").bind("keyup",function(event){
		if(event.keyCode ==13){
			biz_config.btnSearch();
		}
	});
};

$(document).ready(function() {
	biz_config.iniBtnSearch();
	biz_config.bindMethod();
});

$(window).load(function() {
	$("#grid_title").css({
		"color" : "red",
		"font-size" : "14px"
	});
});

$(window).resize(function() {
	$('#biz_config_list').datagrid('resize', {
		width : function() {
			return document.body.clientWidth;
		},
	});
});

biz_config.checkMail = function(mail) {
	var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (filter.test(mail))
		return true;
	else
		return false;
}
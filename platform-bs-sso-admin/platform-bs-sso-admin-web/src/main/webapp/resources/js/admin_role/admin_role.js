var admin_role = {};

admin_role.add = function() {
	$('#fm').form("clear");
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", admin_role.add_save);
	$("#id").val('');
	$('#roleName').removeAttr("disabled");
	//加载组织树
	initLeftTree(); 
	$('#dlg').dialog({
		title : '添加角色',
		modal:true
	}).dialog('open');
	var flag = $("#fm").form('validate');
};

admin_role.add_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var nodes = $('#org-unit-tree').tree('getChecked');	
	var orgIds = "";
	var orgcodes = "";
	if(nodes.length){
		$(nodes).each(function (index, obj) {
			var attr = $.parseJSON(obj.attributes);
			orgcodes += attr.unitCode +",";
			orgIds += attr.unitCode + ":" + obj.id + ",";
    	});
	}else{
		$.messager.alert('警告', '请选择一个机构');
		return;
	}
	var data = $("#fm").serialize();
	data = data+'&orgcodes='+orgcodes+'&orgIds='+orgIds;
	var url = BasePath + '/admin_role/add';
	admin_role.save_data(url, data);
};

admin_role.edit = function() {
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", admin_role.edit_save);

	var row = $("#admin_role_list").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择修改的行');
		return;
	}
	var rows = $("#admin_role_list").datagrid('getSelections');
	if(rows.length>1){
		$.messager.alert('警告', '只能选择一个角色进行修改');
		return;
	}
	$("#id").val(row.id);
	$("#roleName").val(row.roleName);
	$("#description").val(row.description);
	$('#roleName').attr("disabled","disabled");

	initRoleTree(row.id);
	$('#dlg').dialog({
		title : '编辑角色',
		modal:true,
		onClose : function() {
			$('#fm').form('disableValidation');
		}
	}).dialog('open');
};

admin_role.edit_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var nodes = $('#org-unit-tree').tree('getChecked');	
	var orgIds = "";
	var orgcodes = "";
	if(nodes.length){
		$(nodes).each(function (index, obj) {
			var attr = $.parseJSON(obj.attributes);
			orgcodes += attr.unitCode +",";
			orgIds += attr.unitCode + ":" + obj.id + ",";
    	});
	}else{
		$.messager.alert('警告', '请选择一个机构');
		return;
	}
	var data = $("#fm").serialize();
	data = data+'&orgcodes='+orgcodes+'&orgIds='+orgIds;
	var url = BasePath + '/admin_role/update';
	admin_role.save_data(url, data);
};

// delete one or many
admin_role.del = function() {
	var rows = $("#admin_role_list").datagrid('getSelections');
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
			var url = BasePath + '/admin_role/del';
			admin_role.save_data(url, data);
		}
	});
};

admin_role.save_data = function(url, data) {
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
			$('#admin_role_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '保存错误，编号500,请稍后重试');
			}
		}
	});
};

admin_role.iniBtnSearch = function() {
};

admin_role.btnSearch = function() {
	var myQueryParams = {};
	var roleName = $.trim($("#roleNameP").val());
	myQueryParams.roleName = roleName;
	
	var descriptionP = $.trim($("#descriptionP").val());
	myQueryParams.descriptionP = descriptionP;

	var queryCondition = $.trim($("#queryCondition").val());
	if (queryCondition != '') {
		myQueryParams.queryCondition = queryCondition;
	}

	$("#admin_role_list").datagrid({
		queryParams : myQueryParams
	}).datagrid('reload');
};

admin_role.bindMethod = function(){
	$("#roleNameP,#descriptionP").bind("keyup",function(event){
		if(event.keyCode ==13){
			admin_role.btnSearch();
		}
	});
};

$(document).ready(function() {
	admin_role.iniBtnSearch();
	admin_role.bindMethod();
});

$(window).load(function() {
	$("#grid_title").css({
		"color" : "red",
		"font-size" : "14px"
	});
});

$(window).resize(function() {
	$('#admin_role_list').datagrid('resize', {
		width : function() {
			return document.body.clientWidth;
		},
	});
});

admin_role.checkMail = function(mail) {
	var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (filter.test(mail))
		return true;
	else
		return false;
}
var sso_admin = {};

$.extend($.fn.validatebox.defaults.rules, {
    regexValidate: {
		validator: function(value){
			var reg = /(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,12}$/;
			return reg.test(value); 
		},
		message: '需要6-12位，数字字母字符至少2种！'
    }
});

sso_admin.add = function() {
	$('#fm').form("clear");
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", sso_admin.add_save);
	$("#id").val('');
	$('#loginName').removeAttr("disabled");
	$('#state').combobox('select', '1');
	
	//初始化角色列表
	$('#roleId').combobox({
	    url: BasePath + '/admin_role/all.json',
	    valueField:'id',
	    textField:'roleName'
	});
	
	$('#dlg').dialog({
		title : '添加管理员',
		modal:true
	}).dialog('open');
	var flag = $("#fm").form('validate');
	$("adminType").combobox("select", "0");  
	$("state").combobox("select", "1");  
};

sso_admin.add_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var data = $("#fm").serialize();
	var url = BasePath + '/sso_admin/add';
	sso_admin.save_data(url, data);
};

sso_admin.edit = function() {
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", sso_admin.edit_save);
	$('#loginName').attr("disabled","disabled");
	var row = $("#sso_admin_list").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择修改的行');
		return;
	}
	var rows = $("#sso_admin_list").datagrid('getSelections');
	if(rows.length>1){
		$.messager.alert('警告', '只能选择一个管理员进行修改');
		return;
	}
	$("#id").val(row.id);
	$("#loginName").val(row.loginName);
	$("#sureName").val(row.sureName);
	$("#adminType").val(row.adminType);
	$("#password").val(row.password);
	$("#password").validatebox('disableValidation');
	$("#password").change(function(){
			$("#password").validatebox('enableValidation'); 
			$("#password").validatebox('validate'); 
		});
//以下代码能保证只要密码不变就不会提示，但有控件bug，需要触发两次才生效
//    $("#password").validatebox('disableValidation').focus(function () {
//    	if($("#password").val()!=password){
//    		$(this).validatebox('enableValidation'); 
//            $(this).validatebox('validate');
//    	}}).blur(function () {
//    		if($("#password").val()!=password){
//    			$(this).validatebox('enableValidation'); 
//    			$(this).validatebox('validate');
//    		}});
	$("#roleId").val(row.roleId);
	$('#state').combobox('select', row.state);
	$("#phone").val(row.phone);
	$("#email").val(row.email);
	$("#description").val(row.description);
	
	$('#adminType').combobox('setValue',row.adminType);
	$('#roleId').combobox('setValue',row.roleId);
	
	//初始化角色列表
	$('#roleId').combobox({
	    url: BasePath + '/admin_role/all.json?adminId='+row.id,
	    valueField:'id',
	    textField:'roleName'
	});

	$('#dlg').dialog({
		title : '编辑管理员',
		modal:true,
		onClose : function() {
			$('#fm').form('disableValidation');
		}
	}).dialog('open');
	var flag = $("#fm").form('validate');
};

sso_admin.edit_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var data = $('#fm').serialize();

	var url = BasePath + '/sso_admin/update';

	sso_admin.save_data(url, data);
};

// delete one or many
sso_admin.del = function() {
	var rows = $("#sso_admin_list").datagrid('getSelections');
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
			var url = BasePath + '/sso_admin/del';
			sso_admin.save_data(url, data);
		}
	});
};

sso_admin.save_data = function(url, data) {
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
			$('#sso_admin_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '保存错误，编号500,请稍后重试');
			}
		}
	});
};

sso_admin.iniBtnSearch = function() {
};

sso_admin.btnSearch = function() {
	var myQueryParams = {};
	var userId = $.trim($("#userId").val());
	myQueryParams.userId = userId;
	var roleId = $('#roleIdP').combobox('getValue');
	myQueryParams.roleId = roleId;

	var adminName = $.trim($("#adminName").val());
	if (adminName != '') {
		myQueryParams.adminName = adminName;
	}

	$("#sso_admin_list").datagrid({
		queryParams : myQueryParams
	}).datagrid('reload');
};

sso_admin.initSsoAdminList = function(url) {
	$('#roleId').combogrid({
		idField : 'id',
		textField : 'roleName',
		panelWidth : 260,
		panelHeight : 220,
		checkOnSelect : true,
		selectOnCheck : true,
		multiple: true,
		mode : 'remote',
		url : '../adminRole/listAll',
		pagination : false,
	    required: true,
		columns: [[{field:'ck',checkbox:false},
		           {field:'id',title:'id',width:50,sortable:true},
		           {field:'roleName',title:'名称',width:100,sortable:true},
		           {field:'roleCode',title:'编码',width:90,sortable:true}]],
	});
};

sso_admin.bindMethod = function(){
	$("#adminName").bind("keyup",function(event){
		if(event.keyCode ==13){
			sso_admin.btnSearch();
		}
	});
};

$(document).ready(function() {
	sso_admin.iniBtnSearch();
	//sso_admin.initSsoAdminList();
	sso_admin.bindMethod();
});

$(window).load(function() {
	$("#grid_title").css({
		"color" : "red",
		"font-size" : "14px"
	});
});

$(window).resize(function() {
	$('#sso_admin_list').datagrid('resize', {
		width : function() {
			return document.body.clientWidth;
		},
	});
});


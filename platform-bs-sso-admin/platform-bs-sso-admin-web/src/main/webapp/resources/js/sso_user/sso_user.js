var sso_user = {};

sso_user.add = function() {
	$('#fm').form("clear");
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", sso_user.add_save);
	$("#id").val('');
	$('#loginName').removeAttr('disabled');
	$('#password').validatebox({ 
		required:true 
	});
	$('#sex').combobox('select', '1');
	$('#state').combobox('select', '1');
	//$('#passwordArea').show();
	initLeftTree();
	$('#dlg').dialog({
		title : '添加一账通账号',
		modal:true
	}).dialog('open');
	//在窗口一打开的时候就校验，显示出必填项
	var flag = $("#fm").form('validate');
};

sso_user.add_save = function() {
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	var nodes = $('#org-unit-tree').tree('getChecked');	
	var orgId = "";
	var orgcode = "";
	var orgName = "";
	if(nodes.length){
		if(nodes.length>1){
			$.messager.alert('警告', '只能选择一个机构');
			return;
		}
		$(nodes).each(function (index, obj) {
			orgId = obj.id;
			orgcode = obj.unitCode;
			orgName = obj.text;
    	});
	}else{
		$.messager.alert('警告', '请选择一个机构');
		return;
	}
	var data = $("#fm").serialize();
	data = data+'&organizationCode='+orgcode+'&organizationalUnitName='+orgName+'&organizationId='+orgId;
	
	var url = BasePath + '/sso_user/add';
	//sso_user.save_data(url, data);
	$.ajax({
		url : url,
		type : 'post',
		data : data,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候....',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(d) {
			if (d.isError == true) {
				$.messager.alert('警告', '保存失败，' + d.msg);
				return;
			}else {
				$.messager.alert('提示', d.msg);
				$("#fm").find("[name=loginName]")[0].value="";
				$("#fm").find("[name=sureName]")[0].value="";
				$("#fm").find("[name=password]")[0].value="";
				$("#fm").find("[name=mobile]")[0].value="";
				$("#fm").find("[name=email]")[0].value='';
				$("#fm").find("[name=employeeNumber]")[0].value='';
				$('#sex').combobox('select', '1');
				$('#state').combobox('select', '1');
				$('#org-unit-tree').tree('reload');
				$('#sso_user_list').datagrid('reload');
			}
			return;
			$('#dlg').dialog('close');
			$('#sso_user_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '保存失败，编号500,请稍后重试');
			}
		}
	});
};

sso_user.edit = function() {
	$('#fm').form('enableValidation');
	$("#save").unbind("click");
	$("#save").bind("click", sso_user.edit_save);
	$('#loginName').attr('disabled','disabled');
	$('#password').validatebox({ 
		required:false 
	});
	//$('#passwordArea').hide();
	var row = $("#sso_user_list").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择修改的行');
		return;
	}
	
	var rows = $("#sso_user_list").datagrid('getSelections');
	if(rows.length>1){
		$.messager.alert('警告', '只能选择一个用户进行修改');
		return;
	}
	
	$("#fm").find("[name=loginName]")[0].value=row.loginName;
	$("#fm").find("[name=sureName]")[0].value=row.sureName;
	$("#fm").find("[name=idCard]")[0].value=row.idCard;
	$("#fm").find("[name=positionName]")[0].value=row.positionName;
	$("#fm").find("[name=id]")[0].value=row.id;
	if(row.mobile=='0'){
		$("#fm").find("[name=mobile]")[0].value='';
	}else{
		$("#fm").find("[name=mobile]")[0].value=row.mobile;
	}
	if(row.email=='NULL'){
		$("#fm").find("[name=email]")[0].value='';
	}else{
		$("#fm").find("[name=email]")[0].value=row.email;
	}
	if(row.employeeNumber=='NULL'){
		$("#fm").find("[name=employeeNumber]")[0].value='';
	}else{
		$("#fm").find("[name=employeeNumber]")[0].value=row.employeeNumber;
	}
	$('#sex').combobox('select', row.sex);
	$('#state').combobox('select', row.state);
	$("#fm").find("[name=password]")[0].value="";
	
	var organizationCode = row.organizationCode;
	initSsoUserTree(organizationCode);

	$('#dlg').dialog({
		title : '编辑sso用户',
		modal:true,
		onClose : function() {
			$('#fm').form('disableValidation');
		}
	}).dialog('open');
};

sso_user.edit_save = function() {
	
	$('#loginName').attr("disabled", false);
	var data = $('#fm').serialize();
	/*var row = $("#org-unit-tree").tree('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择一个机构');
		return;
	}*/
	var nodes = $('#org-unit-tree').tree('getChecked');	
	var orgId = "";
	var orgcode = "";
	var orgName = "";
	if(nodes.length){
		if(nodes.length>1){
			$.messager.alert('警告', '只能选择一个机构');
			return;
		}
		$(nodes).each(function (index, obj) {
			orgId = obj.id;
			orgcode = obj.unitCode;
			orgName = obj.text;
    	});
	}else{
		$.messager.alert('警告', '请选择一个机构');
		return;
	}
	var flag = $("#fm").form('validate');
	if (flag == false) {
		return;
	}
	data = data+'&organizationCode='+orgcode+'&organizationalUnitName='+orgName+'&organizationId='+orgId;
	var url = BasePath + '/sso_user/update';

	sso_user.save_data(url, data);
};

// delete one or many
sso_user.del = function() {
	var rows = $("#sso_user_list").datagrid('getSelections');
	if (rows == null || rows.length == 0) {
		$.messager.alert('警告', '请选择要删除的行');
		return;
	}
	$.messager.confirm('确认对话框', '即将删除选中的记录，是否继续？', function(r) {
		if (r) {
			// 将多行的id取出合并为一个字符串，中间用逗号","隔开
			var ids = '';
			for (var i = 0; i < rows.length; i++) {
				ids += rows[i].id+',';
			}
			ids=ids.substring(0,ids.lastIndexOf(','));
			var data = {
				ids : ids
			};
			var url = BasePath + '/sso_user/del';
			sso_user.del_data(url, data);
		}
	});
};

sso_user.del_data = function(url, data) {
	$.ajax({
		url : url,
		type : 'post',
		data : data,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候....',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(d) {
			if (d.isError == true) {
				$.messager.alert('警告', '删除错误，' + d.value);
				return;
			}else {
				$.messager.alert('提示', d.value);
			}
			$('#dlg').dialog('close');
			$('#sso_user_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '删除错误，编号500,请稍后重试');
			}
		}
	});
};

sso_user.save_data = function(url, data) {
	$.ajax({
		url : url,
		type : 'post',
		data : data,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候……',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(d) {
			if (d.isError == true) {
				$.messager.alert('警告', '保存失败，' + d.msg);
				return;
			}else {
				$.messager.alert('提示', d.msg);
			}
			$('#dlg').dialog('close');
			$('#sso_user_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '保存失败，编号500,请稍后重试');
			}
		}
	});
};

sso_user.iniBtnSearch = function() {
	$('#searchForm').form('clear');
};

sso_user.btnSearch = function() {

	var params = eval("("+sso_user.convertArray($('#searchForm').serializeArray())+ ")");

	var userId = $.trim($("#userId").val());
	params.userId = userId;
	var bindStateP = $('#bindStateP').combobox('getValue');
	params.bindStateP = bindStateP;
	var stateP = $('#stateP').combobox('getValue');
	params.stateP = stateP;
	var bizCodeP = $('#bizCodeP').combobox('getValue');
	params.bizCodeP = bizCodeP;
	var idCardP =  $.trim($("#idCardP").val());
	params.idCardP = idCardP;
	var startTime =  $.trim($("#startTime").val());
	params.startTime = startTime;
	var endTime =  $.trim($("#endTime").val());
	params.endTime = endTime;
	if(startTime != "" && endTime !="" && startTime > endTime){
		$.messager.alert("提示","结束日期小于开始日期!");
		return;
	}
		
	//取unit_code传到后台
	var node = $('#org_cond').combotree('tree').tree('getSelected');	
	if(node!=null){
		var orgCond = node.unitCode;
		params.orgCond = orgCond;
	}
	
	$("#sso_user_list").datagrid({
		queryParams : params
	}).datagrid('reload');
};

sso_user.exportExcel = function() {

	$('#btn_exportList').attr('disabled',true);
	
	var params = eval("("+sso_user.convertArray($('#searchForm').serializeArray())+ ")");
	var userId = $.trim($("#userId").val());
	params.userId = userId;
	var bindStateP = $('#bindStateP').combobox('getValue');
	params.bindStateP = bindStateP;
	var stateP = $('#stateP').combobox('getValue');
	params.stateP = stateP;
	var bizCodeP = $('#bizCodeP').combobox('getValue');
	params.bizCodeP = bizCodeP;
	var idCardP =  $.trim($("#idCardP").val());
	params.idCardP = idCardP;
	var startTime =  $.trim($("#startTime").val());
	params.startTime = startTime;
	var endTime =  $.trim($("#endTime").val());
	params.endTime = endTime;
	if(startTime > endTime){
		$.messager.alert("提示","结束日期小于开始日期!");
		return;
	}
		
	//取unit_code传到后台
	var node = $('#org_cond').combotree('tree').tree('getSelected');	
	if(node!=null){
		var orgCond = node.unitCode;
		params.orgCond = orgCond;
	}
	
	$.ajax({
        url: BasePath + '/sso_user/exportSsoUserlist.xmls',
        contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
        type:'POST',
        data : params,
        beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候……',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    	$('#btn_exportList').attr('disabled',false);
	    },
        success : function (result){
        	var total = result.total;
        	if(total==0){
        		$.messager.alert('提示', "没有要导出的数据");
        	}
        	if(total==-1){
        		$.messager.alert('警告', "导出错误");
        	}
        	if(total>0){
        		/*var str='';
        		for(var key in params){
        			if(!params[key]==''){
        				//判断为空就不传
        				str= str+"&"+key+"="+params[key];
        			}
        		}*/
        		//window.location.href=BasePath + '/sso_user/exportSsoUserlist.xmls';
        		//window.location.href=BasePath + '/sso_user/exportSsoUserlist.xmls?'+str;
        		var excelpath = result.excelPath;
        		window.open(BasePath + excelpath);
        	}
        },
        error: function (XMLHttpRequest, textStatus, errorThrown)
        {
        	$.messager.alert('警告', '访问网络失败！' + errorThrown);
            return;
        }
    });
};

sso_user.convertArray = function(o) {
	var v = {};
	for ( var i in o) {
		if (typeof (v[o[i].name]) == 'undefined')
			v[o[i].name] = $.trim(o[i].value);
		else
			v[o[i].name] += "," + $.trim(o[i].value);
	}
	return JSON.stringify(v);
};

sso_user.initSsoAdminList = function(url) {
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

sso_user.bindMethod = function(){
	$("#loginNameP,#sureNameP,#employeeNumberP,#emailP,#mobileP,#bizCodeN").bind("keyup",function(event){
		if(event.keyCode ==13){
			sso_user.btnSearch();
		  }
	});
}

$(document).ready(function() {
	sso_user.iniBtnSearch();
	//sso_user.initSsoAdminList();
	sso_user.bindMethod();
});

$(window).load(function() {
	$("#grid_title").css({
		"color" : "red",
		"font-size" : "14px"
	});
});

$(window).resize(function() {
	$('#sso_user_list').datagrid('resize', {
		width : function() {
			return document.body.clientWidth;
		},
	});
});

//重置密码
sso_user.resetPassword = function() {
	var rows = $("#sso_user_list").datagrid('getSelections');
	if(rows.length==0){
		$.messager.alert('警告', '请选择要重置密码的账号');
		return;
	}
	var loginNameStr = "";
	$(rows).each(function (index, obj) {
		loginNameStr += obj.loginName;
		loginNameStr += ",";
	});
	$.messager.confirm('确认对话框', '即将重置选中账号的密码，是否继续？', function(r) {
		if (r) {
			$.ajax({
		        url: BasePath + '/sso_user/resetPwd?loginNameStr='+loginNameStr,
		        dataType : 'json',
		        success : function (result){
		            if(result.code==1){
		            	$.messager.alert('提示', "重置成功");
				    }else{
				    	$.messager.alert('警告', result.msg);
				    	return;
				    }
		        },
		        error: function (XMLHttpRequest, textStatus, errorThrown)
		        {
		        	$.messager.alert('警告', '访问网络失败！' + errorThrown);
		            return;
		        }
		    });
		}
	});
	
};


//生成随机密码
sso_user.getPassword = function() {
    $.ajax({
        url: BasePath + '/sso_user/getPassword',
        dataType : 'json',
        success : function (result){
            if(result.code==1){
		    	$('#password').val(result.pwd);
		    }else{
		    	alert(result.msg);
		    	return;
		    }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown)
        {
        	$.messager.alert('警告', '访问网络失败！' + errorThrown);
            return;
        }
    });
};


sso_user.bind = function(){
	//绑定的时候只能选择一个用户
	var rows = $("#sso_user_list").datagrid('getSelections');
	if(rows.length>1){
		$.messager.alert('警告', '只能选择一个用户进行绑定');
		return;
	}
	//初始化业务系统列表
	$('#bizId').combobox({
	    url: BasePath + '/biz_config/all.json',
	    valueField:'id',
	    textField:'bizName'
	});
	$('#bizLoginName').val('');
	$('#bizPwd').val('');
	var row = $('#sso_user_list').datagrid('getSelected');
	if (row){
		 $("#datagrid").datagrid({  
		 	title: "已绑定账号",
            url: BasePath + '/sso_user/bindBizUserList?id=' + row.id,  
			destroyUrl: BasePath + '/sso_user/unBindBizUser?id='+row.id+'bizCode='+row.bizCode,
			idField: "bizCode", 
            iconCls: "icon-add",
            fitColumns: false,  
            loadMsg: "数据加载中......",  
            rownumbers: true,  
            nowrap: false,  
            showFooter: true,  
            singleSelect: false,  
            toolbar: '#binddiv',
            onDestroy: function(index,row){
				$(this).datagrid('reload');
			},
			onSave: function(index,row){
				$.messager.alert('警告', row.msg);
			},
		    destroyMsg:{
		    	norecord:{	// when no record is selected
		    		title:'Warning',
		    		msg:'没有选中任何记录.'
		    	},
		    	confirm:{	// when select a row
		    		title:'Confirm',
		    		msg:'确定解绑?'
		    	}
		    },
             columns: [[  
             			{field:'selectFlag',title:'选中', width:20,checkbox:true, align:'center'
						},
		                {  
		                    field: 'loginName', title: '一账通账户', width: 150, align: 'center',  
		                },  
		                {  
		                    field: 'sureName', title: '真实姓名', width: 150, align: 'center',  
		                },
		                {  
		                    field: 'bizName', title: '业务系统名称', width: 180, align: 'center',  
		                },  
		                {  
		                    field: 'bizLoginName', title: '业务系统账户', width: 150, align: 'center',  
		                }
		                ]]  
        });
		 
		 $('#dlgAccount').dialog({
				title : '绑定业务系统账号',
				modal:true,
			}).dialog('open');
		 
	}else{
		$.messager.alert('警告', '请选择一个用户');
	}
};


//绑定业务系统
sso_user.bindUser = function(){
	var row = $('#sso_user_list').datagrid('getSelected');
	var param = {};
	param['id']=row.id;
	
	var bizId = $('#bizId').combobox('getValue');
	if(bizId==''){
		$.messager.alert('警告', '请选择业务系统');
		return;
	}
	param['bizId']=parseInt(bizId);
	var bizLoginName = $.trim($('#bizLoginName').val());
	if(bizLoginName==''){
		$.messager.alert('警告', '请输入业务系统账户');
		return;
	}
	param['bizLoginName']=bizLoginName;
	var bizPwd = $.trim($('#bizPwd').val());
	if(bizPwd==''){
		$.messager.alert('警告', '请输入业务系统密码');
		return;
	}
	param['bizPwd']=bizPwd;
	
    $.ajax({
		url : BasePath + '/sso_user/bindBizUser',
		type : 'post',
		data : param,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候……',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(result) {
			$.messager.alert('提示', result.msg); 
		    if(result.code==1){
		    	$('#datagrid').datagrid('reload');
		    }
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '操作错误，编号500,请稍后重试');
			}
		}
	});
};


sso_user.unbindUser = function(){
	var rows = $('#datagrid').datagrid('getSelections');
	var bizCodes = '';
	$(rows).each(function(index, obj){
	   bizCodes += obj.bizCode+',';
	});
	if(bizCodes==''){
		$.messager.alert('警告', '请选择要解绑的业务系统账户');
		return;
	}
	var row = $('#sso_user_list').datagrid('getSelected');
	var param = {};
	param['id']=row.id;
	param['bizCodes']=bizCodes;
    $.ajax({
		url : BasePath + '/sso_user/unBindBizUser',
		type : 'post',
		data : param,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候……',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(result) {
			$.messager.alert('提示', result.msg); 
		    if(result.code==1){
		    	$('#datagrid').datagrid('reload');
		    }
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '操作错误，编号500,请稍后重试');
			}
		}
	});
};

//lock one or many
sso_user.lock = function() {
	var rows = $("#sso_user_list").datagrid('getSelections');
	if (rows == null || rows.length == 0) {
		$.messager.alert('警告', '请选择要锁定的行');
		return;
	}
	//检查状态
	for (var i = 0; i < rows.length; i++) {
		var state = rows[i].state;
		if(state==2){
			$.messager.alert('警告', '已锁定状态无法操作，请重新选择');
			return;
		}
	}
	$.messager.confirm('确认对话框', '即将锁定选中的记录，是否继续？', function(r) {
		if (r) {
			// 将多行的id取出合并为一个字符串，中间用逗号","隔开
			var ids = '';
			for (var i = 0; i < rows.length; i++) {
				ids += rows[i].id+',';
			}
			ids=ids.substring(0,ids.lastIndexOf(','));
			var data = {
				ids : ids,
				flag:'lock'
			};
			var url = BasePath + '/sso_user/lockOrUnlock';
			sso_user.lock_unlock_data(url, data);
		}
	});
};

//unlock one or many
sso_user.unlock = function() {
	var rows = $("#sso_user_list").datagrid('getSelections');
	if (rows == null || rows.length == 0) {
		$.messager.alert('警告', '请选择要锁定的行');
		return;
	}
	//检查状态
	for (var i = 0; i < rows.length; i++) {
		var state = rows[i].state;
		if(state!=2){
			$.messager.alert('警告', '未锁定状态无法操作，请重新选择');
			return;
		}
	}
	$.messager.confirm('确认对话框', '即将锁定选中的记录，是否继续？', function(r) {
		if (r) {
			// 将多行的id取出合并为一个字符串，中间用逗号","隔开
			var ids = '';
			for (var i = 0; i < rows.length; i++) {
				ids += rows[i].id+',';
			}
			ids=ids.substring(0,ids.lastIndexOf(','));
			var data = {
				ids : ids,
				flag:'unlock'
			};
			var url = BasePath + '/sso_user/lockOrUnlock';
			sso_user.lock_unlock_data(url, data);
		}
	});
};

sso_user.lock_unlock_data = function(url, data) {
	$.ajax({
		url : url,
		type : 'post',
		data : data,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候……',
				text: ''
					});
			},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(d) {
			if (d.isError == true) {
				$.messager.alert('警告', '错误，' + d.value);
				return;
			}else {
				$.messager.alert('提示', d.value);
			}
			$('#dlg').dialog('close');
			$('#sso_user_list').datagrid('reload').datagrid('unselectAll');
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '操作错误，编号500,请稍后重试');
			}
		}
	});
};


//同步单条记录
sso_user.syncSingle = function(){
	var param = {};
	var employeeNumber = $('#employeeNumberP').val();
	if(bizId==''){
		$.messager.alert('警告', '请输入工号');
		return;
	}
	param['employeeNumber']=employeeNumber;
	$.ajax({
		url : BasePath + '/sso_user/syncHrUser',
		type : 'post',
		data : param,
		beforeSend: function () {
			$.messager.progress({
				title: '提示',
				msg: '发送请求中，请稍候....',
				text: ''
			});
		},
	    complete: function () {
	    	$.messager.progress('close');
	    },
		success : function(result) {
			if(result.code==1){
		    	$.messager.alert('提示', '同步成功');
		    	sso_user.btnSearch();
		    	//$('#datagrid').datagrid('reload');
		    }else{
		    	$.messager.alert('警告', result.msg);
		    }
		},
		statusCode : {
			500 : function() {
				$.messager.alert('警告', '操作错误，编号500,请稍后重试');
			}
		}
	});
};

//同步全部记录
sso_user.syncAll = function(){
	$.messager.confirm('确认对话框', '即将同步HR系统的所有记录，是否继续？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/sso_user/syncAllHrUser',
				type : 'post',
				beforeSend: function () {
					$.messager.progress({
						title: '提示',
						msg: '发送请求中，请稍候……',
						text: ''
							});
					},
			    complete: function () {
			    	$.messager.progress('close');
			    },
				success : function(result) {
					if(result.code==1){
						$.messager.alert('提示', '已启动同步请求，请稍后查询');
				    	$('#datagrid').datagrid('reload');
				    }else{
				    	$.messager.alert('警告', result.msg);
				    }
				},
				statusCode : {
					500 : function() {
						$.messager.alert('警告', '操作错误，编号500,请稍后重试');
					}
				}
			});
		}
	});
};

//导入事件，显示导入弹出窗口
sso_user.importClick = function ()
{
    $('#import-excel-template').window('open')
    document.getElementById("importFileForm").style.display = "block";
};

//关闭导入弹出窗口
sso_user.closeImportClick = function () {
    document.getElementById('fileImport').value = null;
    document.getElementById('fileName').innerHTML = "";
    document.getElementById('uploadInfo').innerHTML = "";
    $('#import-excel-template').window('close')
};

//导入文件 批量重置密码
sso_user.batchResetPwd = function ()
{
    //获取上传文件控件内容
    var file = document.getElementById('fileImport').files[0];
    //判断控件中是否存在文件内容，如果不存在，弹出提示信息，阻止进一步操作
    if (file == null) { alert('错误，请选择文件'); return; }
    //获取文件名称
    var fileName = file.name;
    //获取文件类型名称
    var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
    //这里限定上传文件文件类型必须为.xlsx，如果文件类型不符，提示错误信息
    if (file_typename == '.xlsx' || file_typename == '.xls')
    {
        //计算文件大小
        var fileSize = 0;
        //如果文件大小大于1024字节X1024字节，则显示文件大小单位为MB，否则为KB
        if (file.size > 1024 * 1024) {
        	fileSize = Math.round(file.size * 100 / (1024 * 1024)) / 100;
        	if (fileSize > 10) {
        		alert('错误，文件超过10MB，禁止上传！'); return;
            }
        	fileSize = fileSize.toString() + 'MB';
        }else {
            fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
        }
        //将文件名和文件大小显示在前端label文本中
        document.getElementById('fileName').innerHTML = "<span style='color:Blue'>文件名: " + file.name + ',大小: ' + fileSize + "</span>";
        //获取form数据
        var formData = new FormData($("#importFileForm")[0]);
        //调用apicontroller后台action方法，将form数据传递给后台处理。contentType必须设置为false,否则chrome和firefox不兼容
        $.ajax({
            url: BasePath+"/sso_user/resetPwd",
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            beforeSend: function () {
				$.messager.progress({
					title: '提示',
					msg: '发送请求中，请稍候……',
					text: ''
						});
				},
		    complete: function () {
		    	$.messager.progress('close');
		    },
            success: function (returnInfo) {
                //上传成功后将控件内容清空，并显示上传成功信息
                document.getElementById('fileImport').value = null;
                document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>" + returnInfo.msg + "</span>";
            },
            error: function (xhr,status,returnInfo) {
            	alert(xhr+'-'+status+'-'+returnInfo);
                //上传失败时显示上传失败信息
                document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>" + returnInfo.msg + "</span>";
            }
        });
    }
    else {
        alert("文件类型错误");
        //将错误信息显示在前端label文本中
        document.getElementById('fileName').innerHTML = "<span style='color:Red'>错误提示:上传文件应该是.xlsx后缀而不应该是" + file_typename + ",请重新选择文件</span>"
    }
}



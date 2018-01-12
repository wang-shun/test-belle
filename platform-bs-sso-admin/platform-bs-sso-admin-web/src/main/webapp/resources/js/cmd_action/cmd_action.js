var cmd_action = {};

//查询
cmd_action.search = function() {
	$('#list').datagrid('load',{
		cmd: $('#s_cmd').val(),
		action: $('#s_action').val(),
		description: $('#s_description').val()
	});
};

//删除
cmd_action.del = function() {
	var row = $("#list").datagrid('getSelections');
	if (row == null || row[0] == null) return;
	
	$.messager.confirm('确定','删除操作不可恢复，是否继续？',function(r){
	    if (r){
	    	$.ajax({
	    		url: BasePath + '/cmd/action/del',
	    		type: 'post',
	    		data: {
	    			id: row[0].id
	    		},
	    		success: function(message) {
	    			//$.messager.alert('结果',message);
	    			$("#list").datagrid('reload');
	    		}
	    	});
	    }
	});
}

//添加对话框
cmd_action.addDialog = function() {
	$("#form").form('reset');
	$("input[name=cmd]").removeAttr("readonly");
	
	$("#dialog").dialog({
		title: '添加',
		iconCls: 'icon-add',
		width: 480,
		height: 256,
		buttons: [{
			text: '保存',
			handler: function(){
				if ($("#form").form('validate')) {
					var data = $("#form").serialize();
					cmd_action.add(data);
				}
			}
		},{
			text: '取消',
			handler: function(){
				$("#dialog").dialog('close');
			}
		}]
	});
}

//添加
cmd_action.add = function(data) {
	$.ajax({
		url: BasePath + '/cmd/action/add',
		type: 'post',
		data: data,
		success: function(message) {
			if (message == 'success') {
				$("#list").datagrid('reload');
				$("#dialog").dialog('close');
			} else {
				$.messager.alert('失败',message);
			}
		}
	});
};

//修改对话框
cmd_action.editDialog = function() {
	var row = $("#list").datagrid('getSelections');
	if (row == null || row[0] == null) return;
	
	$("#form").form('reset');
	$("input[name=cmd]").attr("readonly","readonly");
	
	$("#dialog").dialog({
		title: '修改',
		iconCls: 'icon-edit',
		width: 480,
		height: 256,
		buttons: [{
			text: '确定',
			handler: function(){
				if ($("#form").form('validate')) {
					var data = $("#form").serialize();
					cmd_action.edit(data);
				}
			}
		},{
			text: '取消',
			handler: function(){
				$("#dialog").dialog('close');
			}
		}]
	});
	
	$("#form").form('load',row[0]);
}

//修改
cmd_action.edit = function(data) {
	$.ajax({
		url: BasePath + '/cmd/action/edit',
		type: 'post',
		data: data,
		success: function(message) {
			if (message == 'success') {
				$("#list").datagrid('reload');
				$("#dialog").dialog('close');
			} else {
				$.messager.alert('失败',message);
			}
		}
	});
};

//初始化列表
cmd_action.initList = function() {
	$('#list').datagrid({
	    url: BasePath + '/cmd/action/list',
	    method: 'get',
	    pagination: true,
	    fitColumns: true,
	    nowrap: false,
	    fit: true,
	    rownumbers: true,
	    selectOnCheck: true,
	    checkOnSelect: true,
	    toolbar: '#toolbar',
	    columns:[[
	        {field:'ck',checkbox:true},
	        {field:'id',title:'ID'},
	        {field:'cmd',title:'指令',width:60},
	        {field:'action',title:'行为',width:300},
	        {field:'description',title:'描述信息',width:240},
	        {field:'createTime',title:'创建时间',width:120,
	        	formatter: function(value,row,index){
	        		if (value == null || value == '') return null;
	        		return getFormatDateByLong(value, 'yyyy-MM-dd hh:mm:ss');
			    }},
	        {field:'updateTime',title:'更新时间',width:120,
			    formatter: function(value,row,index){
			    	if (value == null || value == '') return null;
			    	return getFormatDateByLong(value, 'yyyy-MM-dd hh:mm:ss');
			    }}
	    ]]
	});
};

$(document).ready(function(){
	cmd_action.initList();
});
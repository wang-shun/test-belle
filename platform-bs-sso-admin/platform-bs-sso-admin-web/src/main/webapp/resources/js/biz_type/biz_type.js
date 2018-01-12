var biz_type = {};

//查询
biz_type.search = function() {
	$('#list').datagrid('load',{
		bizNo: $('#s_biz_no').val(),
		bizName: $('#s_biz_name').val(),
		msgReceiver: $('#s_msg_receiver').val()
	});
};

//删除
biz_type.del = function() {
	var row = $("#list").datagrid('getSelections');
	if (row == null || row[0] == null) return;
	
	$.messager.confirm('确定','删除操作不可恢复，是否继续？',function(r){
	    if (r){
	    	$.ajax({
	    		url: BasePath+'/biz/type/del',
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
biz_type.addDialog = function() {
	$("#form").form('reset');
	$("input[name=bizNo]").removeAttr("readonly");
	
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
					biz_type.add(data);
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
biz_type.add = function(data) {
	$.ajax({
		url: BasePath + '/biz/type/add',
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
biz_type.editDialog = function() {
	var row = $("#list").datagrid('getSelections');
	if (row == null || row[0] == null) return;
	
	$("#form").form('reset');
	$("input[name=bizNo]").attr("readonly","readonly");
	
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
					biz_type.edit(data);
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
biz_type.edit = function(data) {
	$.ajax({
		url: BasePath + '/biz/type/edit',
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
biz_type.initList = function() {
	$('#list').datagrid({
	    url: BasePath + '/biz/type/list',
	    method: 'get',
	    pagination: true,
	    fitColumns: false,
	    nowrap: true,
	    fit: true,
	    rownumbers: true,
	    selectOnCheck: true,
//	    checkOnSelect: true,
	    toolbar: '#toolbar',
	    columns:[[
	        {field:'ck',checkbox:true},
	        {field:'id',title:'ID'},
	        {field:'bizNo',title:'业务编号',width:60},
	        {field:'bizName',title:'业务名称',width:120},
	        {field:'userName',title:'用户名',width:80},
	        {field:'msgReceiver',title:'短信状态报告接收人',width:240},
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
	biz_type.initList();
});
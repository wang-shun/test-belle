var sms_delivered = {};

//查询
sms_delivered.search = function() {
	$('#list').datagrid('load',{
		phone: $('#s_phone').val(),
		content: $('#s_content').val(),
		sendTime: $('#s_send_time').val(),
		status: $('#s_status').combobox('getValue')
	});
};

//初始化列表
sms_delivered.initList = function() {
	$('#list').datagrid({
	    url: BasePath + '/sms/delivered/',
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
	        {field:'phone',title:'手机号码',width:120},
	        {field:'channelNumber',title:'通道号',width:120},
	        {field:'extSubNumber',title:'扩展子号',width:60},
	        {field:'content',title:'发送内容',width:180},
	        {field:'status',title:'状态',width:60,
	        	formatter: function(value,row,index){
	        		if (value == 0 || value == '0') {
	        			return '未处理';
	        		} else {
	        			return '已处理';
	        		}
			    }},
	        {field:'sendTime',title:'发送时间',width:120,
	        	formatter: function(value,row,index){
	        		if (value == null || value == '') return null;
	        		return getFormatDateByLong(value, 'yyyy-MM-dd hh:mm:ss');
			    }}
	    ]]
	});
};

$(document).ready(function(){
	sms_delivered.initList();
});
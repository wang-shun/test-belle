var sms_balance = {};

//查询指定账户剩余短信数量
sms_balance.query = function() {
	if (!$("#form").form('validate')) {
		return;
	}
	$.ajax({
		url : BasePath + '/sms/balance/data',
		data : $("#form").serialize(),
		success : function(result) {
			if (result < 0) {
				$.messager.alert("信息", "查询失败,账户名称或密码错误！");
			} else {
				$("#form input[name='balance']").val(result);
			}
		}
	});
};

$(document).ready(function(){
	$.ajax({
		url : BasePath + '/sms/balance/data',
		success : function(result) {
			$("#form input[name='balance']").val(result);
		}
	});
});
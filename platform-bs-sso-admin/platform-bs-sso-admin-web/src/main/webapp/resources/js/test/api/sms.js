var test_send = {};

$(document).ready(function(){
	test_send.initBizTypeList('#form_sms input[name="bizCode"]');
	test_send.initBizTypeList('#form_email input[name="bizCode"]');
});

//初始化业务类型下拉框
test_send.initBizTypeList = function(selector) {
	$(selector).combobox({
	    url:BasePath + '/biz/type/list?rows=100&filter00=1',
	    method: 'get',
	    loadFilter: function(data){
	    	return data.rows;
	    },
	    formatter: function(row){
			var opts = $(this).combobox('options');
			return row.bizNo + '-' + row[opts.textField];
		},
	    valueField:'bizNo',
	    textField:'bizName'
	}).combobox('setValue','01');
};

//发送测试短信
test_send.sendSms = function() {
	if (!test_send.checkSms()) {
		return;
	}
		if ($("#form_sms").form('validate')) {
			var data = $("#form_sms").serialize();
			$.ajax({
				type : 'POST',
				url : BasePath + '/test/api/v1/sms',
				data : data,
				dataType:"json",
				success : function(result) {
					if(!result){
						$.messager.alert("信息", "系统异常");
					}else{
						console.log(result);
						if (result.code >= 200 && result.code < 300){
							$.messager.alert("信息",result.message);
						}else{
							$.messager.alert("信息", result.message);
						}
					}
				}
			});
		}
};

//校验短信输入信息
test_send.checkSms = function() {
	var to = $("#form_sms input[name='receivePhones']").val();
	var content = $("#form_sms input[name='content'").val();
	//是否为空
	if (to == '' || content == '') {
		$.messager.alert("信息", "请输入收信人和短信内容");
		return false;
	}
	//收信人格式
	var reg = /^1[34578]\d{9}$/;
	if (to.indexOf(",") == -1) {
		if (!reg.test(to)) {
			$.messager.alert("信息", "收信人格式错误");
			return false;
		}
	} else {
		var phones = to.split(",");
		for (var i = 0; i < phones.length; i ++) {
			if (phones[i] != null && phones[i] != '') {
				if (!reg.test(phones[i])) {
					$.messager.alert("信息", "收信人格式错误");
					return false;
				}
			}
		}
	}
	
	return true;
};

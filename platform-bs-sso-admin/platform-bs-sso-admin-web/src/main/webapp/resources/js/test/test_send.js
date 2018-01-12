var test_send = {};

$(document).ready(function(){
	test_send.initBizTypeList('#form_sms input[name="bizNo"]');
	test_send.initBizTypeList('#form_email input[name="bizNo"]');
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
	$.ajax({
		type : 'POST',
		url : BasePath + '/test/send/sms',
		data : $("#form_sms").serialize(),
		success : function(result) {
			$.messager.alert("信息", result.message);
		}
	});
};

//调用后台接口发送邮件
test_send.sendEmail = function() {
	if (!test_send.checkEmail()) {
		return;
	}
	$.ajax({
		type : 'POST',
		url : BasePath + '/test/send/email',
		data : $("#form_email").serialize(),
		success : function(result) {
			if (result.message) {
				$.messager.alert("信息", result.message);
			} else {
				$.messager.alert("信息", "邮件发送成功");
			}
		}
	});
};

//校验短信输入信息
test_send.checkSms = function() {
	var to = $("#form_sms input[name='to']").val();
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

//校验邮件输入信息
test_send.checkEmail = function() {
	var to = $("#form_email input[name='to']").val();
	var cc = $("#form_email input[name='cc']").val();
	var subject = $("#form_email input[name='subject'").val();
	var content = $("#form_email input[name='content'").val();
	//是否为空
	if (to == '' || content == '') {
		$.messager.alert("信息", "请输入收件人、主题和邮件内容");
		return false;
	}
	//收件人格式
	var reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
	if (to.indexOf(",") == -1) {
		if (!reg.test(to)) {
			$.messager.alert("信息", "收件人格式错误");
			return false;
		}
	} else {
		var emails = to.split(",");
		for (var i = 0; i < emails.length; i ++) {
			if (emails[i] != null && emails[i] != '') {
				if (!reg.test(emails[i])) {
					$.messager.alert("信息", "收件人格式错误");
					return false;
				}
			}
		}
	}
	
	//抄送人格式
	if (cc != null && cc != '') {
		if (cc.indexOf(",") == -1) {
			if (!reg.test(cc)) {
				$.messager.alert("信息", "抄送人格式错误");
				return false;
			}
		} else {
			var emails = cc.split(",");
			for (var i = 0; i < emails.length; i ++) {
				if (emails[i] != null && emails[i] != '') {
					if (!reg.test(emails[i])) {
						$.messager.alert("信息", "抄送人格式错误");
						return false;
					}
				}
			}
		}
	}
	return true;
};
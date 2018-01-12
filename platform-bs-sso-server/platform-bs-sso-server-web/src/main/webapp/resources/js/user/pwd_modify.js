$(document).keyup(function(event) {
	if (event.keyCode == 13) {
		changePwd();
	}
});

function communicationMethod() {

	var userName = $("#restUserName").val();
	var selectValue = $("#restCheckMethod").val();

	if (userName == "") {
		alert("用户名不能为空，请输入！");
		return;
	}

	var data = {};
	data.userName = userName;
	data.verificationType = selectValue;

	$.ajax({
		type : "GET",
		url : basePath + "/ignore/checkmethod_rest?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(result) {
			if (result.code == 1) {
				if (selectValue == "phone") {
					$("#restSelectValue").val(result.phoneNumber);
				}
				if (selectValue == "email") {
					$("#restSelectValue").val(result.emailAddress);
				}
			} else {
				$("#restSelectValue").val(result.msg);
				alert(result.msg);
			}
		},
		error : function(result) {
			alert("查询通讯方式失败！");
		}
	})
};

function changeCommuMethod() {
	var userName = $("#restUserName").val();
	var selectValue = $("#restCheckMethod").val();

	if (userName == "") {
		alert("请输入用户名！");
		return;
	}

	var data = {};
	data.userName = userName;
	data.verificationType = selectValue;

	$.ajax({
		type : "GET",
		url : basePath + "/ignore/checkmethod_rest?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(result) {
			if (result.code == 1) {
				if (selectValue == "phone") {
					$("#restSelectValue").val(result.phoneNumber);
				}

				if (selectValue == "email") {
					$("#restSelectValue").val(result.emailAddress);
				}

			} else {
				$("#restSelectValue").val(result.msg);
				alert(result.msg);
			}
		},
		error : function(result) {
			alert(result.msg);
		}
	})
};

function sendVerificationCode() {
	var userName = $("#restUserName").val();
	var selectValue = $("#restCheckMethod").val();
	var communicationMethod = $("#restSelectValue").val();

	if (userName == "") {
		alert("请输入用户名！")
	}

	if (selectValue == "") {
		alert("请选择获取验证码的方式！")
	}

	var data = {};
	data.userName = userName;
	data.verificationType = selectValue;
	data.communicationMethod = communicationMethod;

	$.ajax({
		type : "GET",
		url : basePath + "/ignore/verifcode_rest?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(result) {
			if (result.code == 1) {
				getCodeClick();
				alert(result.msg);
			} else {
				alert(result.msg);
			}
		},
		error : function(result) {
			alert("发送验证码失败！");
		}

	})
};

var validCode = true;
function getCodeClick() {
	var time = 60;
	var code = $("#sendVerfiCode");
	if (validCode) {
		validCode = false;
		var t = setInterval(function() {
			time--;
			code.val("重新发送(" + time + "秒)");
			code.attr("disabled", "true");
			if (time == 0) {
				clearInterval(t);
				code.val("重新获取");
				validCode = true;
				code.removeAttr("disabled");
			}
		}, 1000);
	}
};

function restPwd() {

	var userName = $("#restUserName").val();
	var selectValue = $("#restCheckMethod").val();
	var verfiCode = $('#restCheckCode').val();
	var newPassword = $('#restNewPwd').val();
	var newPasswordAgain = $('#restNewPwdAgain').val();

	if (userName == "") {
		alert('请检查，用户名不能为空！');
		return false;
	}

	if (verfiCode == "") {
		alert('请检查，验证码不能为空！');
		return false;
	}

	if (newPassword == "" || newPasswordAgain == "") {
		alert('请检查，密码不能为空！');
		return false;
	}

	var regex = /(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,16}$/;

	if (!regex.test(newPassword)) {
		alert('新密码格式不对，需要6-16位，数字字母字符至少2种！');
		return;
	}

	if (newPassword != newPasswordAgain) {
		alert('新密码两次输入不一致！');
		return false;
	}

	var data = {};
	data.userName = userName;
	data.verificationType = selectValue;
	data.newPassword = newPassword;
	data.verfiCode = verfiCode;

	$.ajax({
		type : "POST",
		url : basePath + "/ignore/pwd_rest?time=" + new Date().getTime(),
		data : data,
		success : function(d) {
			if (d.code == 1) {
				showLogoutConfirm();
			} else {
				alert(d.msg);
			}
		},
		error : function(d) {
			alert(d.msg);
		}
	});
};
function closeModifyDlg() {
	window.location.href = basePath + "/ignore/logout?time=" + new Date().getTime();
};

window.onblur = function() {
	document.getElementsByTagName('input')[0].blur();
}

//重置密码，提示跳转到登录页
function showLogoutConfirm() {
	PostbirdAlertBox.confirm({
		'title' : '提示',
		'content' : '重置密码成功，重新登录',
		'okBtn' : '确定',
		'cancelBtn' : '取消',
		'onConfirm' : function() {
			window.location.href = basePath + "/ignore/logout?time=" + new Date().getTime();
		}
	});
}